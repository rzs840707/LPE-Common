package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope refers to all points in execution, where an object is allocated.
 * These points can be restricted to a set of classes.
 * 
 * @author Henning Schulz
 * 
 */
public class AllocationScope implements Scope {

	private final String[] targetClasses;
	
	private final long id;

	@Override
	public long getId() {
		return id;
	}

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes to be considered
	 */
	@JsonCreator
	public AllocationScope(@JsonProperty("targetClasses") String[] targetClasses, long id) {
		this.id = id;
		this.targetClasses = targetClasses;
	}

	@JsonCreator
	public AllocationScope(@JsonProperty("targetClasses") String[] targetClasses) {
		this(targetClasses, System.nanoTime());
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

		builder.append("Allocation Scope [");

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
