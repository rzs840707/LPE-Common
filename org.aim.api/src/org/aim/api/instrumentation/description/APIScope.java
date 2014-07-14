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

import org.codehaus.jackson.annotate.JsonIgnoreType;

/**
 * This scope includes all classes and methods impementing the specified API.
 * 
 * @author Alexander Wert
 * 
 */
public class APIScope extends AbstractEnclosingScope {
	private String apiScopeClass;

	/**
	 * Default Constructor.
	 */
	public APIScope() {
	}

	/**
	 * Constructor.
	 * 
	 * @param apiScopeClass
	 *            API extension class defining the API
	 */
	public APIScope(String apiScopeClass) {
		super();
		this.apiScopeClass = apiScopeClass;
	}

	/**
	 * @return the apiScopeClass
	 */
	public String getApiScopeClass() {
		return apiScopeClass;
	}

	/**
	 * @param apiScopeClass
	 *            the apiScopeClass to set
	 */
	public void setApiScopeClass(String apiScopeClass) {
		this.apiScopeClass = apiScopeClass;
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
		result = prime * result + ((apiScopeClass == null) ? 0 : apiScopeClass.hashCode());
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
		APIScope other = (APIScope) obj;
		if (apiScopeClass == null) {
			if (other.apiScopeClass != null) {
				return false;
			}
		} else if (!apiScopeClass.equals(other.apiScopeClass)) {
			return false;
		}
		return true;
	}

	/**
	 * Builder for an EnclosingInstrumentationEntity with a api scope.
	 * 
	 * @author Alexander Wert
	 * 
	 */
	@JsonIgnoreType
	public class EntityBuilder extends AbstractEntityBuilder {

		/**
		 * Protected default constructor.
		 */
		protected EntityBuilder(InstrumentationDescriptionBuilder parentBuilder) {
			super(new EnclosingInstrumentationEntity(), parentBuilder);
			eiEntity.setScope(APIScope.this);
		}

	}

}
