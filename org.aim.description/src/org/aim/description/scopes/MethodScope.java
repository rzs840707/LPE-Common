package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope contains a given set of methods. These methods are to be specified
 * by method patterns.
 * 
 * @author Henning Schulz
 * 
 */
public class MethodScope implements MethodsEnclosingScope {

	private final String[] methods;

	/**
	 * Constructor.
	 * 
	 * @param methods
	 *            methods which are to be instrumented
	 */
	@JsonCreator
	public MethodScope(@JsonProperty("methods") String[] methods) {
		this.methods = methods;
	}

	/**
	 * @return the methods
	 */
	public String[] getMethods() {
		return methods;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Method Scope [");

		for (String m : methods) {
			builder.append(m);
			builder.append(", ");
			trailingComma = true;
		}

		if (trailingComma) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		builder.append("]");

		return builder.toString();
	}

}
