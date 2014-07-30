package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope contains all methods of all traces rooting from the methods in the
 * given sub-scope.
 * 
 * @author Henning Schulz
 * 
 */
public class TraceScope implements MethodsEnclosingScope {

	private final MethodsEnclosingScope subScope;

	/**
	 * Constructor.
	 * 
	 * @param subScope
	 *            scope of root methods
	 */
	@JsonCreator
	public TraceScope(@JsonProperty("subScope") MethodsEnclosingScope subScope) {
		this.subScope = subScope;
	}

	@Override
	public String toString() {
		return "Trace Scope [" + subScope.toString() + "]";
	}

}
