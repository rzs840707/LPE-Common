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
package org.aim.mainagent.scope;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.aim.api.instrumentation.AbstractScopeAnalyzer;
import org.aim.api.instrumentation.description.Restrictions;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.aim.mainagent.utils.Utils;
import org.lpe.common.util.LpeStringUtils;

/**
 * Analyzes a methods scope.
 * 
 * @author Alexander Wert
 * 
 */
public class MethodScopeAnalyzer extends AbstractScopeAnalyzer {
	private Restrictions restrictions;
	private final Set<String> methodPatterns;

	/**
	 * Constructor.
	 * 
	 * @param methodNames
	 *            full qualified signatures of the methods to instrument
	 */
	public MethodScopeAnalyzer(Set<String> methodNames) {
		this.methodPatterns = methodNames;
	}

	@Override
	public void visitClass(Class<?> clazz, Set<FlatScopeEntity> scopeEntities) {
		String className = clazz.getName();
		if (restrictions.isExcluded(className)) {
			return;
		}

		for (String patternToInstrument : methodPatterns) {
			if (!LpeStringUtils.patternPrefixMatches(className, patternToInstrument)) {
				continue;
			}

			for (Method m : clazz.getMethods()) {
				checkMethodMatching(scopeEntities, patternToInstrument, m);
			}

			for (Method m : clazz.getDeclaredMethods()) {
				checkMethodMatching(scopeEntities, patternToInstrument, m);
			}

		}

	}

	private void checkMethodMatching(Set<FlatScopeEntity> scopeEntities, String patternToMatch, Method m) {
		if (Modifier.isAbstract(m.getModifiers()) || Modifier.isNative(m.getModifiers())) {
			return;
		}
		if (LpeStringUtils.patternMatches(Utils.getMethodSignature(m, true), patternToMatch)) {
			if (!restrictions.isModifiersExcluded(m.getModifiers())
					&& !restrictions.isExcluded(m.getDeclaringClass().getName())) {
				scopeEntities.add(new FlatScopeEntity(m.getDeclaringClass(), Utils.getMethodSignature(m, true)));
			}
		}
	}

	@Override
	public void setRestrictions(Restrictions restrictions) {
		this.restrictions = restrictions;

	}
}
