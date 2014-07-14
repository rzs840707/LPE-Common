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

import org.aim.api.instrumentation.AbstractEnclosingProbe;

/**
 * Abstract class as basis for all specific entity builders.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractEntityBuilder {
	protected EnclosingInstrumentationEntity eiEntity;
	protected InstrumentationDescriptionBuilder parentBuilder;

	/**
	 * Constructor.
	 * 
	 * @param eiEntity
	 *            underlying instrumentation entity
	 * @param parentBuilder
	 *            parent entity builder
	 */
	public AbstractEntityBuilder(EnclosingInstrumentationEntity eiEntity,
			InstrumentationDescriptionBuilder parentBuilder) {
		this.eiEntity = eiEntity;
		this.parentBuilder = parentBuilder;
	}

	/**
	 * Adds a probe to the instrumentation entity.
	 * 
	 * @param probeClass
	 *            class / type of the probe
	 * @return the entity builder
	 */
	public AbstractEntityBuilder addProbe(Class<? extends AbstractEnclosingProbe> probeClass) {
		eiEntity.getProbes().add(probeClass.getName());
		return this;
	}

	/**
	 * Adds a probe to the instrumentation entity.
	 * 
	 * @param probeClassName
	 *            name of the class / type of the probe
	 * @return the entity builder
	 */
	public AbstractEntityBuilder addProbe(String probeClassName) {
		eiEntity.getProbes().add(probeClassName);
		return this;
	}

	/**
	 * Adds a package to exclusion list of the corresponding scope. <br>
	 * <br>
	 * <b>Note</b>: Inclusions are intersected with exclusions!
	 * 
	 * @param packagePrefix
	 *            packagePrefix to exclude
	 * @return the builder
	 */
	public synchronized AbstractEntityBuilder addExclusion(String packagePrefix) {
		Restrictions restrictions = eiEntity.getRestrictions();

		restrictions.getExclusions().add(packagePrefix);
		return this;
	}

	/**
	 * Adds a package to the inclusion list of the corresponding scope. <br>
	 * <br>
	 * <b>Note</b>: Inclusions are intersected with exclusions!
	 * 
	 * @param packagePrefix
	 *            packagePrefix to be considered for instrumentation
	 * @return the builder
	 */
	public synchronized AbstractEntityBuilder addInclusion(String packagePrefix) {
		Restrictions restrictions = eiEntity.getRestrictions();

		restrictions.getInclusions().add(packagePrefix);
		return this;
	}

	/**
	 * Adds a method modifier for consideration in instrumentation of the
	 * corresponding scope. If no modifier is specified then no restrictions are
	 * assumed.
	 * 
	 * @param modifierCode
	 *            code of the modifier. Can be retrieved from {@link Modifier}.
	 * @return the builder
	 */
	public synchronized AbstractEntityBuilder addModifier(int modifierCode) {
		if ((Modifier.methodModifiers() & modifierCode) == 0) {
			return this;
		}

		Restrictions restrictions = eiEntity.getRestrictions();

		restrictions.addModifier(modifierCode);
		return this;
	}

	/**
	 * Finalizes the building of the instrumentation entity.
	 * 
	 * @return the instrumentation description builder
	 */
	public synchronized InstrumentationDescriptionBuilder entityDone() {
		parentBuilder.instDescription.getEntities().add(eiEntity);
		return parentBuilder;
	}
}
