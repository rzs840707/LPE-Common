package org.aim.description;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.aim.description.restrictions.Restriction;
import org.aim.description.sampling.SamplingDescription;
import org.aim.description.scopes.Scope;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

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
	 * Returns all instrumentation entities.
	 * 
	 * @return the instrumentation entities
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public <S extends Scope> Set<InstrumentationEntity<S>> getInstrumentationEntities(Class<S> type) {
		Set<InstrumentationEntity<S>> sEntities = new HashSet<>();

		for (InstrumentationEntity<?> ie : instrumentationEntities) {
			if (type.isAssignableFrom(ie.getScope().getClass())) {
				sEntities.add((InstrumentationEntity<S>) ie);
			}
		}

		return sEntities;
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
		if (globalRestriction == null) {
			globalRestriction = new Restriction();
		}
		return globalRestriction;
	}

	/**
	 * Adds a sampling description.
	 * 
	 * @param description
	 *            sampling description to be added.
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

	/**
	 * Returns, if this instrumentation description contains an entity of the
	 * given scope type.
	 * 
	 * @param scopeClass
	 *            scope class which is to be searched for
	 * @return {@code true}, if there is an entity of the {@code scopeClass}
	 *         type, of {@code false} otherwise
	 */
	public boolean containsScopeType(Class<? extends Scope> scopeClass) {
		for (InstrumentationEntity<?> entity : instrumentationEntities) {
			if (scopeClass.isAssignableFrom(entity.getScope().getClass())) {
				return true;
			}
		}

		return false;
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

		if (getGlobalRestriction() != null) {
			builder.append("\n\tGlobal Restriction:\n\t\t");
			builder.append(getGlobalRestriction().toString().replace(", ", "\n\t\t").replace("+", "+ ")
					.replace("-", "- "));
		}

		builder.append("\n\tSampling Descriptions:");

		for (SamplingDescription sDesc : samplingDescriptions) {
			builder.append("\n\t\t* ");
			builder.append(sDesc.toString());
		}

		return builder.toString();
	}

}
