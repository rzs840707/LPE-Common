package org.aim.description;

import java.util.HashSet;
import java.util.Set;

import org.aim.description.probes.MeasurementProbe;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.Scope;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This is a wrapper class for instrumentation entities, composed of one scope
 * and several probes.
 * 
 * @author Henning Schulz
 * 
 */
public class InstrumentationEntity<S extends Scope> {

	private final S scope;

	private Set<MeasurementProbe<? super S>> probes;

	private Restriction localRestriction;

	/**
	 * Constructor. Initializes the probe set with an empty one.
	 * 
	 * @param scope
	 *            scope to be set.
	 */
	@JsonCreator
	public InstrumentationEntity(@JsonProperty("scope") S scope) {
		this.scope = scope;
		this.probes = new HashSet<>();
	}

	/**
	 * @return the scope
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * Adds a new probe.
	 * 
	 * @param probe
	 *            new probe to be added
	 */
	public void addProbe(MeasurementProbe<? super S> probe) {
		probes.add(probe);
	}

	/**
	 * @return the probes
	 */
	public Set<MeasurementProbe<? super S>> getProbes() {
		return probes;
	}

	/**
	 * Sets the local restriction. Returns, if the restriction is already set.
	 * The restriction is set to the given one, anyway.
	 * 
	 * @param restriction
	 *            local restriction to be set
	 * @return {@code true}, if the global restriction alredy has been set, or
	 *         {@code false} otherwise
	 */
	public boolean setLocalRestriction(Restriction restriction) {
		boolean replaced = this.localRestriction == null;
		this.localRestriction = restriction;
		return replaced;
	}

	/**
	 * @return the local restriction
	 */
	public Restriction getLocalRestriction() {
		return localRestriction;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append(scope.toString());

		if (localRestriction != null) {
			builder.append(" (");
			builder.append(localRestriction.toString());
			builder.append(")");
		}

		builder.append(": ");

		for (MeasurementProbe<? super S> probe : probes) {
			builder.append(probe.getName());
			builder.append(", ");
			trailingComma = true;
		}

		if (trailingComma) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

}
