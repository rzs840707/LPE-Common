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

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.IScopeAnalyzer;

/**
 * This scope includes all called methods initiated from the specified
 * sub-scope.
 * 
 * @author Alexander Wert
 * 
 */
public class FullTraceScope extends AbstractEnclosingScope {
	private Set<AbstractEnclosingScope> subScopes;

	/**
	 * @return the subScope
	 */
	public Set<AbstractEnclosingScope> getSubScopes() {
		if (subScopes == null) {
			subScopes = new HashSet<>();
		}
		return subScopes;
	}

	/**
	 * @param subScope
	 *            the subScope to set
	 */
	public void setSubScopes(Set<AbstractEnclosingScope> subScope) {
		this.subScopes = subScope;
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
		result = prime * result + ((subScopes == null) ? 0 : subScopes.hashCode());
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
		FullTraceScope other = (FullTraceScope) obj;
		if (subScopes == null) {
			if (other.subScopes != null) {
				return false;
			}
		} else if (!subScopes.equals(other.subScopes)) {
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
			eiEntity.setScope(FullTraceScope.this);
		}

		

		/**
		 * Adds a method instrumentation entity.
		 * 
		 * @param methodName
		 *            name of the method to instrument
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addRootMethod(String methodName) {
			for (AbstractEnclosingScope aeScope : FullTraceScope.this.getSubScopes()) {
				if (aeScope instanceof MethodsScope) {
					((MethodsScope) aeScope).getMethodNames().add(methodName);
					return this;
				}
			}
			MethodsScope mScope = new MethodsScope();
			mScope.getMethodNames().add(methodName);
			FullTraceScope.this.getSubScopes().add(mScope);
			return this;
		}

		/**
		 * Adds a constructor instrumentation entity.
		 * 
		 * @param className
		 *            name of the class whose constructors to instrument
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addRootConstructor(String className) {

			for (AbstractEnclosingScope aeScope : FullTraceScope.this.getSubScopes()) {
				if (aeScope instanceof ConstructorsScope) {
					((ConstructorsScope) aeScope).getClassNames().add(className);
					return this;
				}
			}
			ConstructorsScope cScope = new ConstructorsScope();
			cScope.getClassNames().add(className);
			FullTraceScope.this.getSubScopes().add(cScope);
			return this;
		}

		/**
		 * Adds an API instrumentation entity.
		 * 
		 * @param apiScopeClass
		 *            name of the class implementing the API scope
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addRootAPI(String apiScopeClass) {
			((FullTraceScope) eiEntity.getScope()).getSubScopes().add(new APIScope(apiScopeClass));
			return this;
		}

		/**
		 * Adds a custom instrumentation entity.
		 * 
		 * @param scopeClass
		 *            name of the class implementing the scope
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addCustomRoot(String scopeClass) {
			((FullTraceScope) eiEntity.getScope()).getSubScopes().add(new CustomScope(scopeClass));
			return this;
		}

		/**
		 * Adds an API instrumentation entity.
		 * 
		 * @param apiScopeClass
		 *            class implementing the API scope
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addRootAPI(Class<? extends AbstractInstAPIScope> apiScopeClass) {
			return addRootAPI(apiScopeClass.getName());
		}

		/**
		 * Adds a custom instrumentation entity.
		 * 
		 * @param scopeClass
		 *            class implementing the scope
		 * @return an entity builder
		 */
		public synchronized EntityBuilder addCustomRoot(Class<? extends IScopeAnalyzer> scopeClass) {
			return addCustomRoot(scopeClass.getName());
		}

	}

}
