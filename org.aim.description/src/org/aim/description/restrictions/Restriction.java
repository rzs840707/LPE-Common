package org.aim.description.restrictions;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;

/**
 * This class represents a restriction to a given scope.
 * 
 * @author Henning Schulz
 * 
 */
public class Restriction {

	private final Set<String> packageIncludes;
	private final Set<String> packageExcludes;

	private final Set<Integer> modifierIncludes;
	private final Set<Integer> modifierExcludes;

	/**
	 * Constructor. Sets all sets to empty ones.
	 */
	@JsonCreator
	public Restriction() {
		this.packageIncludes = new HashSet<>();
		this.packageExcludes = new HashSet<>();
		this.modifierExcludes = new HashSet<>();
		this.modifierIncludes = new HashSet<>();
	}

	/**
	 * Includes the given package.
	 * @param packageName package to be included
	 */
	public void addPackageInclude(String packageName) {
		packageIncludes.add(packageName);
	}

	/**
	 * @return the packageIncludes
	 */
	public Set<String> getPackageIncludes() {
		return packageIncludes;
	}

	/**
	 * Excludes the given package.
	 * @param packageName package to be excluded
	 */
	public void addPackageExclude(String packageName) {
		packageExcludes.add(packageName);
	}

	/**
	 * @return the packageExcludes
	 */
	public Set<String> getPackageExcludes() {
		return packageExcludes;
	}

	/**
	 * Includes all methods having the given modifier.
	 * @param modifier modifier of the methods to be included
	 */
	public void addModifierInclude(int modifier) {
		modifierIncludes.add(modifier);
	}

	/**
	 * @return the modifierIncludes
	 */
	public Set<Integer> getModifierIncludes() {
		return modifierIncludes;
	}

	/**
	 * Excludes all methods having the given modifier.
	 * @param modifier modifier of the methods to be excluded
	 */
	public void addModifierExclude(int modifier) {
		modifierExcludes.add(modifier);
	}

	/**
	 * @return the modifierExcludes
	 */
	public Set<Integer> getModifierExcludes() {
		return modifierExcludes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		for (String include : packageIncludes) {
			builder.append("+");
			builder.append(include);
			builder.append(", ");
			trailingComma = true;
		}

		for (int mod : modifierIncludes) {
			builder.append("+\"");
			builder.append(Modifier.toString(mod));
			builder.append("\" methods, ");
			trailingComma = true;
		}

		for (String exclude : packageExcludes) {
			builder.append("-");
			builder.append(exclude);
			builder.append(", ");
			trailingComma = true;
		}

		for (int mod : modifierExcludes) {
			builder.append("-\"");
			builder.append(Modifier.toString(mod));
			builder.append("\" methods, ");
			trailingComma = true;
		}

		if (trailingComma) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

}
