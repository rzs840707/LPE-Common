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

/**
 * This scope includes all constructors of the specified class.
 * 
 * @author Alexander Wert
 * 
 */
public class ConstructorsScope extends AbstractEnclosingScope {
	private Set<String> classNames;

	/**
	 * Default Constructor.
	 */
	public ConstructorsScope() {
	}

	/**
	 * @return the className
	 */
	public Set<String> getClassNames() {
		if (classNames == null) {
			classNames = new HashSet<>();
		}
		return classNames;
	}

	/**
	 * @param classNames
	 *            the classNames to set
	 */
	public void setClassNames(Set<String> classNames) {
		this.classNames = classNames;
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
		result = prime * result + ((classNames == null) ? 0 : classNames.hashCode());
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
		ConstructorsScope other = (ConstructorsScope) obj;
		if (classNames == null) {
			if (other.classNames != null) {
				return false;
			}
		} else if (!classNames.equals(other.classNames)) {
			return false;
		}
		return true;
	}

	/**
	 * Builder for an EnclosingInstrumentationEntity with a classes scope.
	 * 
	 * @author Alexander Wert
	 * 
	 */
	public class EntityBuilder extends AbstractEntityBuilder {

		/**
		 * Protected default constructor.
		 */
		protected EntityBuilder(InstrumentationDescriptionBuilder parentBuilder) {
			super(new EnclosingInstrumentationEntity(), parentBuilder);
			eiEntity.setScope(ConstructorsScope.this);
		}

		/**
		 * Adds a class instrumentation entity.
		 * 
		 * @param className
		 *            name of the class to instrument
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addConstructor(String className) {
			ConstructorsScope.this.getClassNames().add(className);
			return this;
		}

	}

}
