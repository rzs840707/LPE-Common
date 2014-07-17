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

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Common class for instrumentation entities.
 * 
 * @author Alexander Wert
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public abstract class AbstractInstrumentationEntity {
	private Set<String> probes;

	/**
	 * @return the probes
	 */
	public Set<String> getProbes() {
		if (probes == null) {
			probes = new HashSet<>();
		}
		return probes;
	}

	/**
	 * @param probes
	 *            the probes to set
	 */
	public void setProbes(Set<String> probes) {
		this.probes = probes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((probes == null) ? 0 : probes.hashCode());
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
		AbstractInstrumentationEntity other = (AbstractInstrumentationEntity) obj;
		if (probes == null) {
			if (other.probes != null) {
				return false;
			}
		} else if (!probes.equals(other.probes)) {
			return false;
		}
		return true;
	}
	
	
}
