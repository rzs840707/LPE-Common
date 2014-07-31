package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope is an extension point to define custom scopes. For that, a String
 * denoting the scope has to be specified.
 * 
 * @author Henning Schulz
 * 
 */
public class CustomScope extends MethodsEnclosingScope {

	private final String scopeName;

	/**
	 * Constructor.
	 * 
	 * @param scopeName
	 *            name of the custom scope
	 */
	@JsonCreator
	public CustomScope(@JsonProperty("scopeName") String scopeName, long id) {
		super(id);
		this.scopeName = scopeName;
	}
	
	@JsonCreator
	public CustomScope(@JsonProperty("scopeName") String scopeName) {
		this(scopeName, System.nanoTime());
	}

	/**
	 * @return the scope name
	 */
	public String getScopeName() {
		return scopeName;
	}

	@Override
	public String toString() {
		return scopeName;
	}

}
