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

import org.aim.api.instrumentation.AbstractEnclosingProbe;

/**
 * Builder for paired event entities.
 * 
 * @author Alexander Wert
 * 
 */
public class PairedEventEntityBuilder {
	protected PairedEventsInstrumentationEntity peiEntity;
	protected InstrumentationDescriptionBuilder parentBuilder;

	/**
	 * Protected default constructor.
	 */
	protected PairedEventEntityBuilder(InstrumentationDescriptionBuilder parentBuilder, PairedEvent event) {
		this.parentBuilder = parentBuilder;
		peiEntity = new PairedEventsInstrumentationEntity(event);
	}

	/**
	 * Adds a probe to the instrumentation entity.
	 * 
	 * @param probeClass
	 *            class / type of the probe
	 * @return the entity builder
	 */
	public PairedEventEntityBuilder addProbe(Class<? extends AbstractEnclosingProbe> probeClass) {
		return addProbe(probeClass.getName());
	}

	/**
	 * Adds a probe to the instrumentation entity.
	 * 
	 * @param probeClass
	 *            name of the class / type of the probe
	 * @return the entity builder
	 */
	public PairedEventEntityBuilder addProbe(String probeClass) {
		peiEntity.getProbes().add(probeClass);
		return this;
	}

	/**
	 * Finalizes the building of the instrumentation entity.
	 * 
	 * @return the instrumentation description builder
	 */
	public synchronized InstrumentationDescriptionBuilder entityDone() {
		parentBuilder.instDescription.getEntities().add(peiEntity);
		return parentBuilder;
	}
}
