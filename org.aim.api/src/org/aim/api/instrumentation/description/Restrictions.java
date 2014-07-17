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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.lpe.common.util.LpeStringUtils;

/**
 * Restricts the instrumentation scope.
 * 
 * @author Alexander Wert
 * 
 */
public class Restrictions {
	public static final String EXCLUDE_JAVA = "java.*";
	public static final String EXCLUDE_JAVAX = "javax.*";
	public static final String EXCLUDE_JAVASSIST = "javassist.*";
	public static final String EXCLUDE_LPE_COMMON = "org.lpe.common.*";

	private Set<String> inclusions;
	private Set<String> exclusions;

	private int modifier = 0;

	/**
	 * Constructor.
	 */
	public Restrictions() {
		getExclusions().add(EXCLUDE_JAVA);
		getExclusions().add(EXCLUDE_JAVAX);
		getExclusions().add(EXCLUDE_JAVASSIST);
		getExclusions().add(EXCLUDE_LPE_COMMON);
	}

	/**
	 * @return the inclusions
	 */
	public Set<String> getInclusions() {
		if (inclusions == null) {
			inclusions = new HashSet<>();
		}
		return inclusions;
	}

	/**
	 * @param inclusions
	 *            the inclusions to set
	 */
	public void setInclusions(Set<String> inclusions) {
		this.inclusions = inclusions;
	}

	/**
	 * @return the exclusions
	 */
	public Set<String> getExclusions() {
		if (exclusions == null) {
			exclusions = new HashSet<>();
		}
		return exclusions;
	}

	/**
	 * @param exclusions
	 *            the exclusions to set
	 */
	public void setExclusions(Set<String> exclusions) {
		this.exclusions = exclusions;
	}

	/**
	 * @return the modifier
	 */
	public int getModifier() {
		return modifier;
	}

	/**
	 * @param modifier
	 *            the modifier to set
	 */
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	/**
	 * Adds a modifier to the scope.
	 * 
	 * @param modifier
	 *            the modifier to set
	 */
	@JsonIgnore
	public void addModifier(int modifier) {

		this.modifier = this.modifier | modifier;
	}

	/**
	 * Checks whether given entity is excluded from instrumentation.
	 * 
	 * @param entityName
	 *            full qualified name of the entity (class, package, interface,
	 *            etc. ) to check
	 * @return true, if entity shell be excluded from instrumentation
	 */
	@JsonIgnore
	public boolean isExcluded(String entityName) {

		if (getInclusions().isEmpty()) {
			for (String excl : getExclusions()) {
				if(LpeStringUtils.patternMatches(entityName, excl)){
					return true;
				}
			}
			return false;
		} else {
			boolean found = false;
			for (String incl : getInclusions()) {
				if(LpeStringUtils.patternMatches(entityName, incl)){
					found = true;
					break;
				}
			}

			if (!found) {
				return true;
			}

			for (String excl : getExclusions()) {
				if(LpeStringUtils.patternMatches(entityName, excl)){
					return true;
				}
			}
			return false;

		}
	}

	/**
	 * Checks whether the passed modifier is excluded from instrumentation.
	 * 
	 * @param modifier
	 *            modifier to check
	 * @return true, if the passed modifier is excluded
	 */
	@JsonIgnore
	public boolean isModifiersExcluded(int modifier) {
		return (this.modifier & modifier) != this.modifier;
	}

	/**
	 * Indicates whether any modifier restrictions are available.
	 * 
	 * @return true, if any modifier restrictions have been specified
	 */
	@JsonIgnore
	public boolean hasModifierRestrictions() {
		return modifier != 0;
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
		result = prime * result + ((exclusions == null) ? 0 : exclusions.hashCode());
		result = prime * result + ((inclusions == null) ? 0 : inclusions.hashCode());
		result = prime * result + modifier;
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
		Restrictions other = (Restrictions) obj;
		if (exclusions == null) {
			if (other.exclusions != null) {
				return false;
			}
		} else if (!exclusions.equals(other.exclusions)) {
			return false;
		}
		if (inclusions == null) {
			if (other.inclusions != null) {
				return false;
			}
		} else if (!inclusions.equals(other.inclusions)) {
			return false;
		}
		if (modifier != other.modifier) {
			return false;
		}
		return true;
	}

}
