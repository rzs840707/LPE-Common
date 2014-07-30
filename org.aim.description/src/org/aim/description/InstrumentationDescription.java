package org.aim.description;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.aim.description.restrictions.Restriction;
import org.aim.description.sampling.SamplingDescription;
import org.codehaus.jackson.annotate.JsonCreator;

/**
 * This class is a wrapper class for instrumentation descriptions.
 * 
 * 
 * @author Henning Schulz
 * 
 */
@XmlRootElement
public class InstrumentationDescription {

	private final Set<InstrumentationEntity<?>> instrumentationEntities;

	private Restriction globalRestriction;

	private final Set<SamplingDescription> samplingDescriptions;

	/**
	 * Constructor. Initializes all sets with empty sets.
	 */
	@JsonCreator
	public InstrumentationDescription() {
		this.instrumentationEntities = new HashSet<>();
		this.samplingDescriptions = new HashSet<>();
	}

	/**
	 * Adds a new instrumentation entity to the description.
	 * 
	 * @param entity
	 *            instrumentation entity to be added
	 */
	public void addInstrumentationEntity(InstrumentationEntity<?> entity) {
		instrumentationEntities.add(entity);
	}

	/**
	 * Returns all instrumentation entities.
	 * 
	 * @return the instrumentation entities
	 */
	public Set<InstrumentationEntity<?>> getInstrumentationEntities() {
		return instrumentationEntities;
	}

	/**
	 * Sets the global restriction. Returns, if the restriction is already set.
	 * The restriction is set to the given one, anyway.
	 * 
	 * @param restriction
	 *            global restriction to be set
	 * @return {@code true}, if the global restriction already has been set, or
	 *         {@code false} otherwise
	 */
	public boolean setGlobalRestriction(Restriction restriction) {
		boolean replaced = this.globalRestriction == null;
		this.globalRestriction = restriction;
		return replaced;
	}

	/**
	 * Return the global restriction.
	 * 
	 * @return the global restriction
	 */
	public Restriction getGlobalRestriction() {
		return globalRestriction;
	}

	/**
	 * Adds a sampling description.
	 * 
	 * @param description sampling description to be added.
	 */
	public void addSamplingDescription(SamplingDescription description) {
		samplingDescriptions.add(description);
	}

	/**
	 * Returns all sampling descriptions.
	 * 
	 * @return the samplingDescriptions
	 */
	public Set<SamplingDescription> getSamplingDescriptions() {
		return samplingDescriptions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Instrumentation Description:\n");
		builder.append("\tInstrumentation Entities:");

		for (InstrumentationEntity<?> entity : instrumentationEntities) {
			builder.append("\n\t\t* ");
			builder.append(entity.toString());
		}

		if (globalRestriction != null) {
			builder.append("\n\tGlobal Restriction:\n\t\t");
			builder.append(globalRestriction.toString().replace(", ", "\n\t\t").replace("+", "+ ").replace("-", "- "));
		}

		builder.append("\n\tSampling Descriptions:");

		for (SamplingDescription sDesc : samplingDescriptions) {
			builder.append("\n\t\t* ");
			builder.append(sDesc.toString());
		}

		return builder.toString();
	}

}
