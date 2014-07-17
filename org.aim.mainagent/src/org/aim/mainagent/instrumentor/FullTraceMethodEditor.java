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
package org.aim.mainagent.instrumentor;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.description.Restrictions;
import org.lpe.common.util.LpeStringUtils;

/**
 * MEthod visitor for instrumentation, used for full trace instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public class FullTraceMethodEditor extends ExprEditor {
	private String incrementalSnippet;

	/**
	 * Constructor.
	 * 
	 * @param incrementalSnippet
	 *            instrumentation statement snippet to inject.
	 */
	public FullTraceMethodEditor(String incrementalSnippet) {
		this.incrementalSnippet = incrementalSnippet;
	}

	@Override
	public void edit(MethodCall m) throws CannotCompileException {

		try {
			if (LpeStringUtils.patternMatches(m.getClassName(), Restrictions.EXCLUDE_JAVA)
					|| LpeStringUtils.patternMatches(m.getClassName(), Restrictions.EXCLUDE_LPE_COMMON)) {
				// prevent cyclic / recursive self instrumentation and
				// instrumentation of java native classes
				return;
			}
			String methodName = "." + m.getMethodName()
					+ m.getMethod().getLongName().substring(m.getMethod().getLongName().indexOf("("));

			String tempSnippet = incrementalSnippet.replace(AbstractEnclosingProbe.METHOD_SIGNATURE_PLACE_HOLDER, "\""
					+ methodName + "\"");

			m.replace("{" + tempSnippet + " $_ = $proceed($$);}");
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}

	}

}
