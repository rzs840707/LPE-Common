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

/**
 * This scope allows to provide custom scope implementations.
 * 
 * @author Alexander Wert
 * 
 */
public class CustomScope extends AbstractEnclosingScope {
	private String scopeClass;

	/**
	 * Default Constructor.
	 */
	public CustomScope() {
	}

	/**
	 * Constructor.
	 * 
	 * @param scopeClass
	 *            name of the class implementing a CustomScope.
	 */
	public CustomScope(String scopeClass) {
		super();
		this.scopeClass = scopeClass;
	}

	/**
	 * @return the scopeClass
	 */
	public String getScopeClass() {
		return scopeClass;
	}

	/**
	 * @param scopeClass
	 *            the scopeClass to set
	 */
	public void setScopeClass(String scopeClass) {
		this.scopeClass = scopeClass;
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
		result = prime * result + ((scopeClass == null) ? 0 : scopeClass.hashCode());
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
		CustomScope other = (CustomScope) obj;
		if (scopeClass == null) {
			if (other.scopeClass != null) {
				return false;
			}
		} else if (!scopeClass.equals(other.scopeClass)) {
			return false;
		}
		return true;
	}

	/**
	 * Builder for an EnclosingInstrumentationEntity with a full trace scope.
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
			eiEntity.setScope(CustomScope.this);
		}

	}

}
