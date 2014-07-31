package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope refers to all methods of the given API. The APIScope is an
 * extension point and can be extended by using custom API names.
 * 
 * @author Henning Schulz
 * 
 */
public class APIScope extends MethodsEnclosingScope {

	private final String aPIName;

	/**
	 * Constructor.
	 * @param aPIName name of the represented API
	 */
	@JsonCreator
	public APIScope(@JsonProperty("aPIName") String aPIName, long id) {
		super(id);
		this.aPIName = aPIName;
	}
	
	@JsonCreator
	public APIScope(@JsonProperty("aPIName") String aPIName) {
		this(aPIName, System.nanoTime());
	}

	/**
	 * @return the API name
	 */
	public String getAPIName() {
		return aPIName;
	}

	@Override
	public String toString() {
		return aPIName + " API Scope";
	}

}
