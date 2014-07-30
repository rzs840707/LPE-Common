package org.aim.description.probes;

import org.aim.description.scopes.Scope;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents a measurement probe.
 * 
 * @author Henning Schulz
 * 
 * @param <S>
 *            type of scopes which can be used with this probe
 */
public class MeasurementProbe<S extends Scope> {

	private final String name;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name of this probe
	 */
	@JsonCreator
	public MeasurementProbe(@JsonProperty("name") String name) {
		this.name = name;
	}

	/**
	 * @return the probeName
	 */
	public String getName() {
		return name;
	}

}
