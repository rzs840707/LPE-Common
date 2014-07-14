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
package org.aim.api.instrumentation.description;

import java.lang.reflect.Modifier;
import java.util.Map.Entry;

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.aim.api.measurement.sampling.ISampler;

/**
 * Used to build an instrumentation description.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentationDescriptionBuilder {
	protected InstrumentationDescription instDescription;

	/**
	 * Constructor.
	 */
	public InstrumentationDescriptionBuilder() {
		instDescription = new InstrumentationDescription();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptionToExtend
	 *            instrumentation description to extend with this builder
	 */
	public InstrumentationDescriptionBuilder(InstrumentationDescription descriptionToExtend) {
		instDescription = descriptionToExtend;
	}

	/**
	 * Appends the content of another instrumentation.
	 * 
	 * @param other
	 *            other instrumentation whose content to append
	 * @return this builder
	 */
	public synchronized InstrumentationDescriptionBuilder appendOtherDescription(InstrumentationDescription other) {
		for (String inc : other.getGlobalRestrictions().getInclusions()) {
			addInclusion(inc);
		}
		for (String exc : other.getGlobalRestrictions().getExclusions()) {
			addExclusion(exc);
		}
		addModifier(other.getGlobalRestrictions().getModifier());

		if (other.getSamplingDescription() != null) {
			for (Entry<String, Long> samplerEntry : other.getSamplingDescription().getConfigValues().entrySet()) {
				addSamplingInstruction(samplerEntry.getKey(), samplerEntry.getValue());
			}
		}

		for (EnclosingInstrumentationEntity eiEntity : other.getEntities(EnclosingInstrumentationEntity.class)) {
			AbstractEnclosingScope aeScope = eiEntity.getScope();
			AbstractEntityBuilder aeBuilder = appendScope(aeScope);

			for (String inc : eiEntity.getRestrictions().getInclusions()) {
				aeBuilder.addInclusion(inc);
			}
			for (String exc : eiEntity.getRestrictions().getExclusions()) {
				aeBuilder.addExclusion(exc);
			}
			aeBuilder.addModifier(eiEntity.getRestrictions().getModifier());
			for (String probe : eiEntity.getProbes()) {
				aeBuilder.addProbe(probe);
			}
			aeBuilder.entityDone();
		}

		for (PairedEventsInstrumentationEntity peiEntity : other.getEntities(PairedEventsInstrumentationEntity.class)) {
			PairedEventEntityBuilder peiBuilder = addPairedEventHook(peiEntity.getEvent());
			for (String probe : peiEntity.getProbes()) {
				peiBuilder.addProbe(probe);
			}
		}

		return this;
	}

	private AbstractEntityBuilder appendScope(AbstractEnclosingScope aeScope) {
		AbstractEntityBuilder aeBuilder = null;
		if (aeScope instanceof MethodsScope) {
			MethodsScope scope = (MethodsScope) aeScope;
			MethodsScope.EntityBuilder eBuilder = addMethodInstrumentation(scope.getId());
			for (String method : scope.getMethodNames()) {
				eBuilder.addMethod(method);
			}
			aeBuilder = eBuilder;

		} else if (aeScope instanceof ConstructorsScope) {
			ConstructorsScope scope = (ConstructorsScope) aeScope;
			ConstructorsScope.EntityBuilder eBuilder = addConstructorInstrumentation(scope.getId());
			for (String className : scope.getClassNames()) {
				eBuilder.addConstructor(className);
			}
			aeBuilder = eBuilder;
		} else if (aeScope instanceof ObjectAllocationScope) {
			ObjectAllocationScope scope = (ObjectAllocationScope) aeScope;
			ObjectAllocationScope.EntityBuilder eBuilder = addObjectAllocationInstrumentation();
			for (String className : scope.getClassNames()) {
				eBuilder.addClass(className);
			}
			aeBuilder = eBuilder;
		} else if (aeScope instanceof APIScope) {
			APIScope scope = (APIScope) aeScope;
			APIScope.EntityBuilder eBuilder = addAPIInstrumentation(scope.getApiScopeClass(), scope.getId());

			aeBuilder = eBuilder;
		} else if (aeScope instanceof FullTraceScope) {
			FullTraceScope.EntityBuilder eBuilder = appendFullTraceScope(aeScope);
			aeBuilder = eBuilder;
		} else if (aeScope instanceof CustomScope) {
			CustomScope scope = (CustomScope) aeScope;
			CustomScope.EntityBuilder eBuilder = addCustomInstrumentation(scope.getScopeClass(), scope.getId());
			aeBuilder = eBuilder;
		} else {
			throw new IllegalArgumentException("Invalid scope type!");
		}
		return aeBuilder;
	}

	private FullTraceScope.EntityBuilder appendFullTraceScope(AbstractEnclosingScope aeScope) {
		FullTraceScope scope = (FullTraceScope) aeScope;
		FullTraceScope.EntityBuilder eBuilder = addFullTraceInstrumentation();
		for (AbstractEnclosingScope absSubScope : scope.getSubScopes()) {
			if (absSubScope instanceof MethodsScope) {
				MethodsScope subScope = (MethodsScope) absSubScope;
				for (String methodName : subScope.getMethodNames()) {
					eBuilder.addRootMethod(methodName);
				}
			} else if (absSubScope instanceof ConstructorsScope) {
				ConstructorsScope subScope = (ConstructorsScope) absSubScope;
				for (String className : subScope.getClassNames()) {
					eBuilder.addRootConstructor(className);
				}
			} else if (absSubScope instanceof APIScope) {
				APIScope subScope = (APIScope) absSubScope;
				eBuilder.addRootAPI(subScope.getApiScopeClass());

			} else if (absSubScope instanceof CustomScope) {
				CustomScope subScope = (CustomScope) absSubScope;
				eBuilder.addCustomRoot(subScope.getScopeClass());
			}
		}
		return eBuilder;
	}

	/**
	 * Adds a sampling configuration. Uses the default sampling delay of 1
	 * second.
	 * 
	 * @param sampler
	 *            name of the sampling type (class of the corresponding sampler)
	 *            to be added
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addSamplingInstruction(String sampler) {
		SamplingConfig samplingConfig = instDescription.getSamplingDescription();
		if (samplingConfig == null) {
			samplingConfig = new SamplingConfig(sampler);
		} else {
			samplingConfig.add(sampler);
		}
		instDescription.setSamplingDescription(samplingConfig);
		return this;
	}

	/**
	 * Adds a sampler class name to the configuration.
	 * 
	 * @param sampler
	 *            name of the sampling type (class of the corresponding sampler)
	 *            to be added
	 * @param samplingDelay
	 *            delay for sampling that value
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addSamplingInstruction(String sampler, long samplingDelay) {
		SamplingConfig samplingConfig = instDescription.getSamplingDescription();
		if (samplingConfig == null) {
			samplingConfig = new SamplingConfig(sampler, samplingDelay);
		} else {
			samplingConfig.add(sampler, samplingDelay);
		}
		instDescription.setSamplingDescription(samplingConfig);
		return this;
	}

	/**
	 * Adds a sampling configuration. Uses the default sampling delay of 1
	 * second.
	 * 
	 * @param sampler
	 *            sampling type (class of the corresponding sampler) to be added
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addSamplingInstruction(Class<? extends ISampler> sampler) {
		return addSamplingInstruction(sampler.getName());
	}

	/**
	 * Adds a sampler class name to the configuration.
	 * 
	 * @param sampler
	 *            sampling type (class of the corresponding sampler) to be added
	 * @param samplingDelay
	 *            delay for sampling that value
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addSamplingInstruction(Class<? extends ISampler> sampler,
			long samplingDelay) {
		return addSamplingInstruction(sampler.getName(), samplingDelay);
	}

	/**
	 * Adds a package to exclusion list. <br>
	 * <br>
	 * <b>Note</b>: Inclusions are intersected with exclusions!
	 * 
	 * @param packagePrefix
	 *            packagePrefix to exclude
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addExclusion(String packagePrefix) {
		Restrictions gRestrictions = instDescription.getGlobalRestrictions();
		if (gRestrictions == null) {
			gRestrictions = new Restrictions();
		}
		gRestrictions.getExclusions().add(packagePrefix);
		return this;
	}

	/**
	 * Adds a package to the inclusion list. <br>
	 * <br>
	 * <b>Note</b>: Inclusions are intersected with exclusions!
	 * 
	 * @param packagePrefix
	 *            packagePrefix to be considered for instrumentation
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addInclusion(String packagePrefix) {
		Restrictions gRestrictions = instDescription.getGlobalRestrictions();
		if (gRestrictions == null) {
			gRestrictions = new Restrictions();
		}
		gRestrictions.getInclusions().add(packagePrefix);
		return this;
	}

	/**
	 * Adds a method modifier for consideration in instrumentation. If no
	 * modifier is specified then no restrictions are assumed.
	 * 
	 * @param modifierCode
	 *            code of the modifier. Can be retrieved from {@link Modifier}.
	 * @return the builder
	 */
	public synchronized InstrumentationDescriptionBuilder addModifier(int modifierCode) {
		if ((Modifier.methodModifiers() & modifierCode) == 0) {
			return this;
		}

		Restrictions gRestrictions = instDescription.getGlobalRestrictions();
		if (gRestrictions == null) {
			gRestrictions = new Restrictions();
		}
		gRestrictions.addModifier(modifierCode);
		return this;
	}

	/**
	 * Adds a method instrumentation entity.
	 * 
	 * 
	 * @return an entity builder
	 */
	public synchronized MethodsScope.EntityBuilder addMethodInstrumentation() {
		MethodsScope mScope = new MethodsScope();
		return mScope.new EntityBuilder(this);
	}

	/**
	 * Adds a constructor instrumentation entity.
	 * 
	 * 
	 * @return an entity builder
	 */
	public synchronized ConstructorsScope.EntityBuilder addConstructorInstrumentation() {
		ConstructorsScope cScope = new ConstructorsScope();
		return cScope.new EntityBuilder(this);
	}

	/**
	 * Adds an object allocation instrumentation entity.
	 * 
	 * @return an entity builder
	 */
	public synchronized ObjectAllocationScope.EntityBuilder addObjectAllocationInstrumentation() {
		ObjectAllocationScope oaScope = new ObjectAllocationScope();
		return oaScope.new EntityBuilder(this);
	}

	/**
	 * Adds an API instrumentation entity.
	 * 
	 * @param apiScopeClass
	 *            the class implementing the API scope
	 * @return an entity builder
	 */
	public synchronized APIScope.EntityBuilder addAPIInstrumentation(Class<? extends AbstractInstAPIScope> apiScopeClass) {
		return addAPIInstrumentation(apiScopeClass.getName());
	}

	/**
	 * Adds a custom instrumentation entity.
	 * 
	 * @param scopeClass
	 *            the class implementing the scope
	 * @return an entity builder
	 */
	public synchronized CustomScope.EntityBuilder addCustomInstrumentation(Class<? extends IScopeAnalyzer> scopeClass) {
		return addCustomInstrumentation(scopeClass.getName());
	}

	/**
	 * Adds an API instrumentation entity.
	 * 
	 * @param apiScopeClass
	 *            name of the class implementing the API scope
	 * @return an entity builder
	 */
	public synchronized APIScope.EntityBuilder addAPIInstrumentation(String apiScopeClass) {
		APIScope aScope = new APIScope(apiScopeClass);
		return aScope.new EntityBuilder(this);
	}

	/**
	 * Adds a custom instrumentation entity.
	 * 
	 * @param scopeClass
	 *            name of the class implementing the scope
	 * @return an entity builder
	 */
	public synchronized CustomScope.EntityBuilder addCustomInstrumentation(String scopeClass) {
		CustomScope eScope = new CustomScope(scopeClass);
		return eScope.new EntityBuilder(this);
	}

	/**
	 * Adds a full trace instrumentation entity.
	 * 
	 * @return an entity builder
	 */
	public synchronized FullTraceScope.EntityBuilder addFullTraceInstrumentation() {
		FullTraceScope ftScope = new FullTraceScope();
		return ftScope.new EntityBuilder(this);
	}

	/**
	 * Adds a method instrumentation entity.
	 * 
	 * @param scopeId
	 *            scope Id is only used for incremental instrumentation (full
	 *            trace instrumentation)
	 * @return an entity builder
	 */
	public synchronized MethodsScope.EntityBuilder addMethodInstrumentation(Long scopeId) {
		MethodsScope mScope = new MethodsScope();
		mScope.setId(scopeId);
		return mScope.new EntityBuilder(this);
	}

	/**
	 * Adds a constructor instrumentation entity.
	 * 
	 * @param scopeId
	 *            scope Id is only used for incremental instrumentation (full
	 *            trace instrumentation)
	 * @return an entity builder
	 */
	public synchronized ConstructorsScope.EntityBuilder addConstructorInstrumentation(Long scopeId) {
		ConstructorsScope cScope = new ConstructorsScope();
		cScope.setId(scopeId);
		return cScope.new EntityBuilder(this);
	}

	/**
	 * Adds an API instrumentation entity.
	 * 
	 * @param apiScopeClass
	 *            name of the class implementing the API scope
	 * @param scopeId
	 *            scope Id is only used for incremental instrumentation (full
	 *            trace instrumentation)
	 * @return an entity builder
	 */
	public synchronized APIScope.EntityBuilder addAPIInstrumentation(String apiScopeClass, Long scopeId) {
		APIScope aScope = new APIScope(apiScopeClass);
		aScope.setId(scopeId);
		return aScope.new EntityBuilder(this);
	}

	/**
	 * Adds a custom instrumentation entity.
	 * 
	 * @param scopeClass
	 *            name of the class implementing the scope
	 * @param scopeId
	 *            scope Id is only used for incremental instrumentation (full
	 *            trace instrumentation)
	 * @return an entity builder
	 */
	public synchronized CustomScope.EntityBuilder addCustomInstrumentation(String scopeClass, Long scopeId) {
		CustomScope eScope = new CustomScope(scopeClass);
		eScope.setId(scopeId);
		return eScope.new EntityBuilder(this);
	}

	/**
	 * Adds an API instrumentation entity.
	 * 
	 * @param apiScopeClass
	 *            the class implementing the API scope
	 * @param scopeId
	 *            scope Id is only used for incremental instrumentation (full
	 *            trace instrumentation)
	 * @return an entity builder
	 */
	public synchronized APIScope.EntityBuilder addAPIInstrumentation(
			Class<? extends AbstractInstAPIScope> apiScopeClass, Long scopeId) {
		return addAPIInstrumentation(apiScopeClass.getName(), scopeId);
	}

	/**
	 * Adds a custom instrumentation entity.
	 * 
	 * @param scopeClass
	 *            the class implementing the scope
	 * @param scopeId
	 *            scope Id is only used for incremental instrumentation (full
	 *            trace instrumentation)
	 * @return an entity builder
	 */
	public synchronized CustomScope.EntityBuilder addCustomInstrumentation(Class<? extends IScopeAnalyzer> scopeClass,
			Long scopeId) {
		return addCustomInstrumentation(scopeClass.getName(), scopeId);
	}

	/**
	 * Adds an event hook for an event pair.
	 * 
	 * @param event
	 *            event pair type to intercept
	 * @return a paired event entity builder
	 */
	public synchronized PairedEventEntityBuilder addPairedEventHook(PairedEvent event) {
		return new PairedEventEntityBuilder(this, event);
	}

	/**
	 * Creates the instrumentation description instance.
	 * 
	 * @return instrumentation description
	 */
	public synchronized InstrumentationDescription build() {
		return instDescription;
	}

}
