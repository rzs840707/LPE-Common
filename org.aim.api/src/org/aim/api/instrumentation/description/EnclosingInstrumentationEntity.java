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
 * Abstract description of an instrumentation entity.
 * 
 * @author Alexander Wert
 * 
 */
public class EnclosingInstrumentationEntity extends AbstractInstrumentationEntity {

	

	private Restrictions restrictions;
	private AbstractEnclosingScope scope;

	/**
	 * Constructor.
	 */
	public EnclosingInstrumentationEntity() {
		restrictions = new Restrictions();
		
	}

	/**
	 * @return the restrictions
	 */
	public Restrictions getRestrictions() {
		if (restrictions == null) {
			restrictions = new Restrictions();
		}
		return restrictions;
	}

	/**
	 * @param restrictions
	 *            the restrictions to set
	 */
	public void setRestrictions(Restrictions restrictions) {
		this.restrictions = restrictions;
	}

	/**
	 * @return the scope
	 */
	public AbstractEnclosingScope getScope() {
		return scope;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(AbstractEnclosingScope scope) {
		this.scope = scope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((restrictions == null) ? 0 : restrictions.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EnclosingInstrumentationEntity other = (EnclosingInstrumentationEntity) obj;
		if (restrictions == null) {
			if (other.restrictions != null) {
				return false;
			}
		} else if (!restrictions.equals(other.restrictions)) {
			return false;
		}
		if (scope == null) {
			if (other.scope != null) {
				return false;
			}
		} else if (!scope.equals(other.scope)) {
			return false;
		}
		return true;
	}

}
