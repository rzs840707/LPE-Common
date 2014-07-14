/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.mainagent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.description.APIScope;
import org.aim.api.instrumentation.description.AbstractEnclosingScope;
import org.aim.api.instrumentation.description.AbstractInstrumentationEntity;
import org.aim.api.instrumentation.description.ConstructorsScope;
import org.aim.api.instrumentation.description.CustomScope;
import org.aim.api.instrumentation.description.EnclosingInstrumentationEntity;
import org.aim.api.instrumentation.description.FullTraceScope;
import org.aim.api.instrumentation.description.InstrumentationDescription;
import org.aim.api.instrumentation.description.InstrumentationDescriptionBuilder;
import org.aim.api.instrumentation.description.MethodsScope;
import org.aim.api.instrumentation.description.Restrictions;
import org.aim.mainagent.probes.IncrementalInstrumentationProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instrumentor for traces.
 * 
 * @author Alexander Wert
 * 
 */
public final class TraceInstrumentor implements IInstrumentor {
	private static final Logger LOGGER = LoggerFactory.getLogger(TraceInstrumentor.class);
	private static TraceInstrumentor instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static TraceInstrumentor getInstance() {
		if (instance == null) {
			instance = new TraceInstrumentor();
		}
		return instance;
	}

	private final Map<Long, Set<String>> incrementalInstrumentationProbes;
	private final Map<Long, Restrictions> incrementalInstrumentationRestrictions;
	private final Set<String> instrumentationFlags;
	private volatile long idCounter = 0;

	private TraceInstrumentor() {
		incrementalInstrumentationProbes = new HashMap<>();
		incrementalInstrumentationRestrictions = new HashMap<>();
		instrumentationFlags = new HashSet<>();
	}

	/**
	 * Does an incremental step in instrumentation.
	 * 
	 * @param methodName
	 *            method to instrument
	 * @param jobID
	 *            incremental instrumentation job id identifying the
	 *            instrumentation details
	 */
	public void instrumentIncrementally(String methodName, long jobID) {
		LOGGER.info("Incrementally going to instrument method: {}", methodName);
		try {
			String keyString = methodName + "__" + jobID;
			if (!instrumentationFlags.contains(keyString)) {

				InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
				for (String inc : incrementalInstrumentationRestrictions.get(jobID).getInclusions()) {
					idBuilder.addInclusion(inc);
				}
				for (String exc : incrementalInstrumentationRestrictions.get(jobID).getExclusions()) {
					idBuilder.addExclusion(exc);
				}
				idBuilder.addModifier(incrementalInstrumentationRestrictions.get(jobID).getModifier());

				MethodsScope.EntityBuilder meBuilder = idBuilder.addMethodInstrumentation(jobID).addMethod(methodName);
				for (String probe : incrementalInstrumentationProbes.get(jobID)) {
					meBuilder.addProbe(probe);
					meBuilder.addProbe(IncrementalInstrumentationProbe.class);
				}
				meBuilder.entityDone();
				InstrumentationDescription instDescr = idBuilder.build();
				AdaptiveInstrumentationFacade.getInstance().instrument(instDescr);
				instrumentationFlags.add(keyString);
			}
		} catch (Throwable e) {
			// Catch all exceptions and errors since this code is executed
			// directly from the target application
			LOGGER.error("Error during incremental instrumentation: {}", e);

		}
	}

	@Override
	public void instrument(InstrumentationDescription descr) throws InstrumentationException {
		if (!descr.containsScope(FullTraceScope.class)) {
			return;
		}
		for (AbstractInstrumentationEntity aiEntity : descr.getEntities()) {
			if (aiEntity instanceof EnclosingInstrumentationEntity) {
				EnclosingInstrumentationEntity eiEntity = (EnclosingInstrumentationEntity) aiEntity;
				if (!(eiEntity.getScope() instanceof FullTraceScope)) {
					continue;
				}
				FullTraceScope ftScope = (FullTraceScope) eiEntity.getScope();
				long scopeId = idCounter++;
				incrementalInstrumentationProbes.put(scopeId, eiEntity.getProbes());
				Restrictions restrictions = new Restrictions();
				restrictions.getExclusions().addAll(descr.getGlobalRestrictions().getExclusions());
				restrictions.getInclusions().addAll(descr.getGlobalRestrictions().getInclusions());
				restrictions.addModifier(descr.getGlobalRestrictions().getModifier());
				incrementalInstrumentationRestrictions.put(scopeId, restrictions);
				InstrumentationDescription extendedDescr = getExtendedInstrumentationDescription(descr, ftScope,
						eiEntity, scopeId);
				AdaptiveInstrumentationFacade.getInstance().instrument(extendedDescr);
			}
		}

	}

	@Override
	public void undoInstrumentation() throws InstrumentationException {
		incrementalInstrumentationProbes.clear();
		incrementalInstrumentationRestrictions.clear();
		instrumentationFlags.clear();

	}

	private InstrumentationDescription getExtendedInstrumentationDescription(InstrumentationDescription descr,
			FullTraceScope ftScope, EnclosingInstrumentationEntity eiEntity, Long scopeId)
			throws InstrumentationException {

		Set<AbstractEnclosingScope> initialScopes = ftScope.getSubScopes();
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		for (String inc : descr.getGlobalRestrictions().getInclusions()) {
			idBuilder.addInclusion(inc);
		}
		for (String exc : descr.getGlobalRestrictions().getExclusions()) {
			idBuilder.addExclusion(exc);
		}
		idBuilder.addModifier(descr.getGlobalRestrictions().getModifier());
		for (AbstractEnclosingScope iScope : initialScopes) {
			if (iScope instanceof MethodsScope) {
				buildMethodInstEntity(eiEntity, scopeId, idBuilder, iScope);
			} else if (iScope instanceof ConstructorsScope) {
				buildConstructorInstEntity(eiEntity, scopeId, idBuilder, iScope);
			} else if (iScope instanceof APIScope) {
				buildAPIInstEntity(eiEntity, scopeId, idBuilder, iScope);
			} else if (iScope instanceof CustomScope) {
				buildCustomInstEntity(eiEntity, scopeId, idBuilder, iScope);
			} else {
				throw new InstrumentationException("Invalid sub scope type for full trace instrumentation scope: "
						+ iScope.getClass().getName());
			}
		}
		return idBuilder.build();
	}

	private void buildCustomInstEntity(EnclosingInstrumentationEntity eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, AbstractEnclosingScope iScope) {
		CustomScope cScope = (CustomScope) iScope;
		CustomScope.EntityBuilder ceBuilder = idBuilder.addCustomInstrumentation(cScope.getScopeClass(), scopeId);
		ceBuilder.addProbe(IncrementalInstrumentationProbe.class.getName());
		for (String probe : eiEntity.getProbes()) {
			ceBuilder.addProbe(probe);
		}

		for (String exclusion : eiEntity.getRestrictions().getExclusions()) {
			ceBuilder.addExclusion(exclusion);
		}
		for (String inclusion : eiEntity.getRestrictions().getInclusions()) {
			ceBuilder.addInclusion(inclusion);
		}
		ceBuilder.addModifier(eiEntity.getRestrictions().getModifier());
		ceBuilder.entityDone();
	}

	private void buildAPIInstEntity(EnclosingInstrumentationEntity eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, AbstractEnclosingScope iScope) {
		APIScope apiScope = (APIScope) iScope;
		APIScope.EntityBuilder apieBuilder = idBuilder.addAPIInstrumentation(apiScope.getApiScopeClass(), scopeId);
		apieBuilder.addProbe(IncrementalInstrumentationProbe.class.getName());
		for (String probe : eiEntity.getProbes()) {
			apieBuilder.addProbe(probe);
		}
		for (String exclusion : eiEntity.getRestrictions().getExclusions()) {
			apieBuilder.addExclusion(exclusion);
		}
		for (String inclusion : eiEntity.getRestrictions().getInclusions()) {
			apieBuilder.addInclusion(inclusion);
		}
		apieBuilder.addModifier(eiEntity.getRestrictions().getModifier());
		apieBuilder.entityDone();
	}

	private void buildConstructorInstEntity(EnclosingInstrumentationEntity eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, AbstractEnclosingScope iScope) {
		ConstructorsScope cScope = (ConstructorsScope) iScope;
		ConstructorsScope.EntityBuilder ceBuilder = idBuilder.addConstructorInstrumentation(scopeId);
		for (String className : cScope.getClassNames()) {
			ceBuilder.addConstructor(className);
			ceBuilder.addProbe(IncrementalInstrumentationProbe.class.getName());
			for (String probe : eiEntity.getProbes()) {
				ceBuilder.addProbe(probe);
			}
		}
		for (String exclusion : eiEntity.getRestrictions().getExclusions()) {
			ceBuilder.addExclusion(exclusion);
		}
		for (String inclusion : eiEntity.getRestrictions().getInclusions()) {
			ceBuilder.addInclusion(inclusion);
		}
		ceBuilder.addModifier(eiEntity.getRestrictions().getModifier());
		ceBuilder.entityDone();
	}

	private void buildMethodInstEntity(EnclosingInstrumentationEntity eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, AbstractEnclosingScope iScope) {
		MethodsScope mScope = (MethodsScope) iScope;
		MethodsScope.EntityBuilder meBuilder = idBuilder.addMethodInstrumentation(scopeId);
		for (String methodName : mScope.getMethodNames()) {
			meBuilder.addMethod(methodName);
			meBuilder.addProbe(IncrementalInstrumentationProbe.class.getName());
			for (String probe : eiEntity.getProbes()) {
				meBuilder.addProbe(probe);
			}
		}
		for (String exclusion : eiEntity.getRestrictions().getExclusions()) {
			meBuilder.addExclusion(exclusion);
		}
		for (String inclusion : eiEntity.getRestrictions().getInclusions()) {
			meBuilder.addInclusion(inclusion);
		}
		meBuilder.addModifier(eiEntity.getRestrictions().getModifier());
		meBuilder.entityDone();
	}

}
