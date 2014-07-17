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
 * This scope includes exactly one method.
 * 
 * @author Alexander Wert
 * 
 */
public class MethodsScope extends AbstractEnclosingScope {
	private Set<String> methodNames;

	/**
	 * Default Constructor.
	 */
	public MethodsScope() {
	}

	/**
	 * @return the methodName
	 */
	public Set<String> getMethodNames() {
		if (methodNames == null) {
			methodNames = new HashSet<>();
		}
		return methodNames;
	}

	/**
	 * @param methodNames
	 *            the methodName to set
	 */
	public void setMethodNames(Set<String> methodNames) {
		this.methodNames = methodNames;
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
		result = prime * result + ((methodNames == null) ? 0 : methodNames.hashCode());
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
		MethodsScope other = (MethodsScope) obj;
		if (methodNames == null) {
			if (other.methodNames != null) {
				return false;
			}
		} else if (!methodNames.equals(other.methodNames)) {
			return false;
		}
		return true;
	}

	/**
	 * Builder for an EnclosingInstrumentationEntity with a methods scope.
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
			eiEntity.setScope(MethodsScope.this);
		}

		/**
		 * Adds a method instrumentation entity.
		 * 
		 * @param methodName
		 *            name of the method to instrument
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addMethod(String methodName) {
			MethodsScope.this.getMethodNames().add(methodName);
			return this;
		}

	}
}
