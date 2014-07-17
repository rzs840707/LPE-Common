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
package org.aim.mainagent.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.aim.api.instrumentation.description.APIScope;
import org.aim.api.instrumentation.description.AbstractInstrumentationEntity;
import org.aim.api.instrumentation.description.ConstructorsScope;
import org.aim.api.instrumentation.description.CustomScope;
import org.aim.api.instrumentation.description.EnclosingInstrumentationEntity;
import org.aim.api.instrumentation.description.InstrumentationDescription;
import org.aim.api.instrumentation.description.MethodsScope;
import org.aim.api.instrumentation.description.Restrictions;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;

/**
 * Analyzes tho whole scope.
 * 
 * @author Alexander Wert
 * 
 */
public class ScopeAnalysisController {

	private InstrumentationDescription instrumentationDescription;

	/**
	 * Constructor.
	 * 
	 * @param instDescription
	 *            instrumentation description
	 */
	public ScopeAnalysisController(InstrumentationDescription instDescription) {
		instrumentationDescription = instDescription;
	}

	/**
	 * Creates flat internal instrumentation description.
	 * 
	 * @param allLoadedClasses
	 *            all classes loaded in the JVM
	 * @return set of flat instrumentation entities
	 * @throws InstrumentationException
	 *             if scope cannot be resolved
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<FlatInstrumentationEntity> resolveScopes(List<Class> allLoadedClasses) throws InstrumentationException {
		removeGlobalyExcludedClasses(allLoadedClasses);

		Map<IScopeAnalyzer, Set<String>> scopeAnalyzersToProbesMap = createScopeAnalyzerToProbesMapping();
		Set<FlatInstrumentationEntity> instrumentationEntities = new HashSet<>();
		Map<String, Class<? extends AbstractEnclosingProbe>> probeClasses = new HashMap<>();
		for (Entry<IScopeAnalyzer, Set<String>> mapEntry : scopeAnalyzersToProbesMap.entrySet()) {
			IScopeAnalyzer sAnalyzer = mapEntry.getKey();
			Set<String> probes = mapEntry.getValue();
			Set<FlatScopeEntity> scopeEntities = new HashSet<>();
			for (Class<?> clazz : allLoadedClasses) {
				sAnalyzer.visitClass(clazz, scopeEntities);
			}
			for (FlatScopeEntity fse : scopeEntities) {
				for (String probe : probes) {
					Class<? extends AbstractEnclosingProbe> probeClass = null;
					if (!probeClasses.containsKey(probe)) {
						try {
							probeClass = (Class<? extends AbstractEnclosingProbe>) Class.forName(probe);
						} catch (ClassNotFoundException e) {
							throw new InstrumentationException("Failed loading Probe class " + probe, e);
						}
						probeClasses.put(probe, probeClass);
					} else {
						probeClass = probeClasses.get(probe);
					}
					FlatInstrumentationEntity fiEntity = new FlatInstrumentationEntity(fse, probeClass);
					fiEntity.setScopeId(sAnalyzer.getScopeId());

					instrumentationEntities.add(fiEntity);
				}
			}
		}

		return instrumentationEntities;

	}

	@SuppressWarnings("rawtypes")
	private void removeGlobalyExcludedClasses(List<Class> allLoadedClasses) {
		List<Class> toRemove = new ArrayList<>();

		for (Class clazz : allLoadedClasses) {
			String className = clazz.getName();
			boolean invalidClass = false;
			if (instrumentationDescription.getGlobalRestrictions().isExcluded(className)) {
				invalidClass = true;
			} else if (clazz.getClassLoader() == null) {
				invalidClass = true;
			} else if (clazz.isInterface() || clazz.isPrimitive() || clazz.isArray() || clazz.isAnnotation()
					|| clazz.isAnonymousClass() || clazz.isEnum() || clazz.isSynthetic() || clazz.isLocalClass()) {
				invalidClass = true;
			} else {
				try {
					clazz.getClassLoader().loadClass(this.getClass().getName());
				} catch (ClassNotFoundException cnfe) {
					invalidClass = true;
				}
			}

			if (invalidClass) {
				toRemove.add(clazz);
			}

		}

		allLoadedClasses.removeAll(toRemove);
	}

	private Map<IScopeAnalyzer, Set<String>> createScopeAnalyzerToProbesMapping() throws InstrumentationException {
		Map<IScopeAnalyzer, Set<String>> mapping = new HashMap<>();
		for (AbstractInstrumentationEntity abstractEntity : instrumentationDescription.getEntities()) {
			if (abstractEntity instanceof EnclosingInstrumentationEntity) {
				EnclosingInstrumentationEntity eiEntity = (EnclosingInstrumentationEntity) abstractEntity;

				IScopeAnalyzer scopeAnalyzer = null;
				if (eiEntity.getScope() instanceof MethodsScope) {
					scopeAnalyzer = new MethodScopeAnalyzer(((MethodsScope) eiEntity.getScope()).getMethodNames());
				} else if (eiEntity.getScope() instanceof ConstructorsScope) {
					scopeAnalyzer = new ConstructorScopeAnalyzer(
							((ConstructorsScope) eiEntity.getScope()).getClassNames());
				} else if (eiEntity.getScope() instanceof CustomScope) {
					String scopeClass = ((CustomScope) eiEntity.getScope()).getScopeClass();
					try {
						scopeAnalyzer = (IScopeAnalyzer) Class.forName(scopeClass).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						// TODO: show warning
						throw new InstrumentationException("Unable to instantiate scope analyzer for scopeClass "
								+ scopeClass, e);
					}
				} else if (eiEntity.getScope() instanceof APIScope) {
					String scopeClass = ((APIScope) eiEntity.getScope()).getApiScopeClass();
					try {
						AbstractInstAPIScope apiScopeInstance = (AbstractInstAPIScope) Class.forName(scopeClass)
								.newInstance();
						scopeAnalyzer = new APIScopeAnalyzer(apiScopeInstance);
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						// TODO: show warning
						throw new RuntimeException("Unable to instantiate API scope for scopeClass " + scopeClass, e);
					}
				} else {
					continue;
				}
				Restrictions combinedRestriction = new Restrictions();
				combinedRestriction.addModifier(eiEntity.getRestrictions().getModifier());
				combinedRestriction.addModifier(instrumentationDescription.getGlobalRestrictions().getModifier());
				combinedRestriction.getInclusions().addAll(eiEntity.getRestrictions().getInclusions());
				combinedRestriction.getInclusions().addAll(
						instrumentationDescription.getGlobalRestrictions().getInclusions());
				combinedRestriction.getExclusions().addAll(eiEntity.getRestrictions().getExclusions());
				combinedRestriction.getExclusions().addAll(
						instrumentationDescription.getGlobalRestrictions().getExclusions());

				scopeAnalyzer.setRestrictions(combinedRestriction);
				scopeAnalyzer.setScopeId(eiEntity.getScope().getId());
				mapping.put(scopeAnalyzer, eiEntity.getProbes());
			}

		}
		return mapping;
	}
}
