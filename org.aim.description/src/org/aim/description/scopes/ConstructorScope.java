package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope refers to all constructor calls of the given classes.
 * 
 * @author Henning Schulz
 * 
 */
public class ConstructorScope implements MethodsEnclosingScope {

	private final String[] targetClasses;

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes which constructors are to be instrumented
	 */
	@JsonCreator
	public ConstructorScope(@JsonProperty("targetClasses") String[] targetClasses) {
		this.targetClasses = targetClasses;
	}

	/**
	 * @return the target classes
	 */
	public String[] getTargetClasses() {
		return targetClasses;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Constructor Scope [");

		for (String clazz : targetClasses) {
			builder.append(clazz);
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
