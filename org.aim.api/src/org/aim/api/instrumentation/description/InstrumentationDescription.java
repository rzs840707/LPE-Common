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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Root element for an instrumenttion description.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class InstrumentationDescription {
	private Restrictions globalRestrictions;
	private SamplingConfig samplingDescription;
	private Set<AbstractInstrumentationEntity> entities;

	/**
	 * @return the globalRestrictions
	 */
	public Restrictions getGlobalRestrictions() {
		if (globalRestrictions == null) {
			globalRestrictions = new Restrictions();
		}
		return globalRestrictions;
	}

	/**
	 * @param globalRestrictions
	 *            the globalRestrictions to set
	 */
	public void setGlobalRestrictions(Restrictions globalRestrictions) {
		this.globalRestrictions = globalRestrictions;
	}

	/**
	 * @return the samplingDescription
	 */
	public SamplingConfig getSamplingDescription() {
		return samplingDescription;
	}

	/**
	 * @param samplingDescription
	 *            the samplingDescription to set
	 */
	public void setSamplingDescription(SamplingConfig samplingDescription) {
		this.samplingDescription = samplingDescription;
	}

	/**
	 * @return the entities
	 */
	public Set<AbstractInstrumentationEntity> getEntities() {
		if (entities == null) {
			entities = new HashSet<>();
		}
		return entities;
	}

	/**
	 * @param entities
	 *            the entities to set
	 */
	public void setEntities(Set<AbstractInstrumentationEntity> entities) {
		this.entities = entities;
	}

	/**
	 * 
	 * @param type
	 *            type of interest
	 * @return entities of given type
	 * @param <T>
	 *            type parameter
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public <T extends AbstractInstrumentationEntity> Set<T> getEntities(Class<T> type) {
		if (entities == null) {
			entities = new HashSet<>();
		}
		Set<T> result = new HashSet<T>();

		for (AbstractInstrumentationEntity aiEntity : entities) {
			if (aiEntity.getClass() == type) {
				result.add((T) aiEntity);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param aeScope
	 *            scope of interest
	 * @return true if description contains the given scope
	 */
	@JsonIgnore
	public boolean containsScope(Class<? extends AbstractEnclosingScope> aeScope) {
		for (EnclosingInstrumentationEntity eiEntity : getEntities(EnclosingInstrumentationEntity.class)) {
			if (eiEntity.getScope().getClass() == aeScope) {
				return true;
			}

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entities == null) ? 0 : entities.hashCode());
		result = prime * result + ((globalRestrictions == null) ? 0 : globalRestrictions.hashCode());
		result = prime * result + ((samplingDescription == null) ? 0 : samplingDescription.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		InstrumentationDescription other = (InstrumentationDescription) obj;
		if (entities == null) {
			if (other.entities != null) {
				return false;
			}
		} else if (!entities.equals(other.entities)) {
			return false;
		}
		if (globalRestrictions == null) {
			if (other.globalRestrictions != null) {
				return false;
			}
		} else if (!globalRestrictions.equals(other.globalRestrictions)) {
			return false;
		}
		if (samplingDescription == null) {
			if (other.samplingDescription != null) {
				return false;
			}
		} else if (!samplingDescription.equals(other.samplingDescription)) {
			return false;
		}
		return true;
	}

}
