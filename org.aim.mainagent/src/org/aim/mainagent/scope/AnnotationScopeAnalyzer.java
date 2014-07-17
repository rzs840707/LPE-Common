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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aim.api.instrumentation.AbstractInstAnnotationScope;
import org.aim.api.instrumentation.AbstractScopeAnalyzer;
import org.aim.api.instrumentation.description.Restrictions;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.aim.mainagent.utils.Utils;
import org.lpe.common.util.LpeStringUtils;

public class AnnotationScopeAnalyzer extends AbstractScopeAnalyzer {

	private Restrictions restrictions;
	private AbstractInstAnnotationScope annotationScope;

	public AnnotationScopeAnalyzer(AbstractInstAnnotationScope annotationScope) {
		this.annotationScope = annotationScope;
	}

	@Override
	public void visitClass(Class<?> clazz, Set<FlatScopeEntity> scopeEntities) {
		if (clazz == null || !Utils.isNormalClass(clazz)) {
			return;
		}
		if (restrictions.hasModifierRestrictions()
				&& !Modifier.isPublic(restrictions.getModifier())) {
			return;
		}
		if (restrictions.isExcluded(clazz.getName())) {
			return;
		}
		if (scopeEntities == null) {
			scopeEntities = new HashSet<>();
		}

		for (Annotation classAnnotation : clazz.getAnnotations()) {
			for (String classAnnotationName : annotationScope
					.getAnnotationsMatch().keySet()) {
				if (!LpeStringUtils.patternMatches(classAnnotation
						.annotationType().getName(), classAnnotationName)) {
					continue;
				}

				List<Method> methods = new ArrayList<>();
				for (Method method : clazz.getMethods()) {
					methods.add(method);
				}

				for (Method method : clazz.getDeclaredMethods()) {
					methods.add(method);
				}

				for (Method method : methods) {
					for (Annotation methodAnnotation : method.getAnnotations()) {

						for (String methodAnnotationName : annotationScope
								.getAnnotationsMatch().get(classAnnotationName)) {
							if (!LpeStringUtils.patternMatches(methodAnnotation
									.annotationType().getName(),
									methodAnnotationName)) {
								continue;
							}

							scopeEntities.add(new FlatScopeEntity(clazz, Utils
									.getMethodSignature(method, true)));
						}

					}
				}

			}

		}

	}

	@Override
	public void setRestrictions(Restrictions restrictions) {
		this.restrictions = restrictions;

	}

}
