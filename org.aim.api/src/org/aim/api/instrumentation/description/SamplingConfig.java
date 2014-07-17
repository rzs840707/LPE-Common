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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The {@link SamplingConfig} wrappes the configuration for the sampling
 * process.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class SamplingConfig {

	private static final long DEFAULT_SAMPLING_DELAY = 1000; // ms

	private final Map<String, Long> configValues = new HashMap<String, Long>();

	/**
	 * Public default constructor.
	 */
	public SamplingConfig() {
	}

	/**
	 * Constructor.
	 * 
	 * @param samplingType
	 *            initial sampling type (class name of the corresponding
	 *            sampler), using the default sampling delay of 1 second
	 */
	public SamplingConfig(String samplingType) {
		this();

		getConfigValues().put(samplingType, DEFAULT_SAMPLING_DELAY);

	}

	/**
	 * Constructor.
	 * 
	 * @param samplingType
	 *            initial sampling type (class name of the corresponding
	 *            sampler)
	 * @param samplingDelay
	 *            delay (in milliseconds) for the sampling loop
	 */
	public SamplingConfig(String samplingType, long samplingDelay) {
		this();

		getConfigValues().put(samplingType, samplingDelay);

	}

	/**
	 * Adds another {@link SamplingConfig} content to the configuration.
	 * 
	 * @param conf
	 *            sampling configuration to be added
	 */
	@JsonIgnore
	public void add(SamplingConfig conf) {
		for (String type : conf.getValues()) {
			getConfigValues().put(type, conf.getConfigValues().get(type));
		}
	}

	/**
	 * Adds a sampler class name to the configuration. Uses the default sampling
	 * delay of 1 second.
	 * 
	 * @param samplingType
	 *            sampling type (class name of the corresponding sampler) to be
	 *            added
	 */
	@JsonIgnore
	public void add(String samplingType) {

		getConfigValues().put(samplingType, DEFAULT_SAMPLING_DELAY);

	}

	/**
	 * Adds a sampler class name to the configuration.
	 * 
	 * @param samplingType
	 *            sampling type (class name of the corresponding sampler) to be
	 *            added
	 * @param samplingDelay
	 *            delay for sampling that value
	 */
	@JsonIgnore
	public void add(String samplingType, long samplingDelay) {

		getConfigValues().put(samplingType, samplingDelay);

	}

	/**
	 * Returns true, if underlying configuration contains the passed
	 * {@link SamplingConfig} content.
	 * 
	 * @param other
	 *            configuration to check against
	 * @return returns true, if underlying configuration contains the passed
	 *         configuration.
	 */
	@JsonIgnore
	public boolean contains(SamplingConfig other) {
		return getValues().containsAll(other.getValues());
	}

	/**
	 * Returns true, if underlying configuration contains the passed sampler
	 * class name.
	 * 
	 * @param other
	 *            type (class name of the corresponding sampler) to check
	 *            against
	 * @return returns true, if underlying configuration contains the passed
	 *         sampling type.
	 */
	@JsonIgnore
	public boolean contains(String other) {
		return getValues().contains(other);
	}

	/**
	 * Getter for sampling delay.
	 * 
	 * @param samplingType
	 *            the sampling type (class name of the corresponding sampler)
	 *            for which the delay should be retrieved
	 * 
	 * @return returns the sampling delay (in milliseconds)
	 */
	@JsonIgnore
	public long getSamplingDelay(String samplingType) {

		return getConfigValues().get(samplingType);
	}

	/**
	 * Setter for sampling delay.
	 * 
	 * @param samplingType
	 *            the sampling type (class name of the corresponding sampler)
	 *            for which the delay should be set
	 * @param samplingDelay
	 *            the delay to use (in milliseconds)
	 */
	@JsonIgnore
	public void setSamplingDelay(String samplingType, long samplingDelay) {
		getConfigValues().put(samplingType, samplingDelay);
	}

	/**
	 * Getter for sampling elements.
	 * 
	 * @return set of sampling types (sampler class names)
	 */
	@JsonIgnore
	public Set<String> getValues() {
		return getConfigValues().keySet();
	}

	@Override
	public String toString() {
		String s = "SamplingConfig:\n";
		for (String type : getConfigValues().keySet()) {
			s += type + "\n";
		}
		return s;
	}

	/**
	 * @return the configValues (mapping from sampler class to delays)
	 */
	public Map<String, Long> getConfigValues() {
		return configValues;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configValues == null) ? 0 : configValues.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SamplingConfig other = (SamplingConfig) obj;
		if (configValues == null) {
			if (other.configValues != null) {
				return false;
			}
		} else if (!configValues.equals(other.configValues)) {
			return false;
		}
		return true;
	}
	
	

}
