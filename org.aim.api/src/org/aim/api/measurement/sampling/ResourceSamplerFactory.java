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
package org.aim.api.measurement.sampling;

import org.hyperic.sigar.Sigar;
import org.aim.api.measurement.collector.IDataCollector;

/**
 * Creates recorder for different sampling types.
 * 
 * @author Alexander Wert
 * 
 */
public final class ResourceSamplerFactory {
	private ResourceSamplerFactory() {
	}


	private static Sigar sigar;

	private static Sigar getSigar() {
		if (sigar == null) {
			sigar = new Sigar();
		}
		return sigar;
	}

	/**
	 * 
	 * @param sType
	 *            sampling type
	 * @param dataCollector
	 *            data colelctor to use
	 * @return sampler for the type
	 */
	public static AbstractSampler getSampler(String sType, IDataCollector dataCollector) {
		AbstractResourceSampler recorder;
		try {
			recorder = (AbstractResourceSampler) Class.forName(sType).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException("Failed loading sampler of type: " + sType, e);
		}

		recorder.setDataCollector(dataCollector);
		recorder.setSigar(getSigar());
		return recorder;

	}

}
