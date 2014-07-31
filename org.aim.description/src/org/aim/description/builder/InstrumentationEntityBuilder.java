package org.aim.description.builder;

import org.aim.description.InstrumentationEntity;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.Scope;

/**
 * Builder for an {@link InstrumentationEntity}.
 * 
 * @author Henning Schulz
 * 
 */
public class InstrumentationEntityBuilder<S extends Scope> extends AbstractRestrictableBuilder {

	private final InstrumentationEntity<S> entity;

	private final InstrumentationDescriptionBuilder parentBuilder;

	/**
	 * Constructor.
	 * 
	 * @param scope
	 *            scope of the instrumentation entity
	 * @param parentBuilder
	 *            builder which called this constructor
	 */
	public InstrumentationEntityBuilder(S scope, InstrumentationDescriptionBuilder parentBuilder) {
		entity = new InstrumentationEntity<S>(scope);
		this.parentBuilder = parentBuilder;
	}

	/**
	 * Adds a new probe to the entity.
	 * 
	 * @param probe
	 *            probe to be added
	 * @return this builder
	 * @see ProbePredefinitions
	 */
	public InstrumentationEntityBuilder<S> addProbe(MeasurementProbe<? super S> probe) {
		entity.addProbe(probe);
		return this;
	}

	/**
	 * Adds a new probe to the entity. We assume, that the specified probe can
	 * be used with the given scope. Thus, the type of the probe is set to the
	 * given scope. (e.g. if this scope is a {@code MethodScope}, the probe type
	 * is set to {@code MethodScope}).
	 * 
	 * @param probe
	 *            probe to be added
	 * @return this builder
	 */
	public InstrumentationEntityBuilder<S> addProbe(String probeName) {
		entity.addProbe(new MeasurementProbe<S>(probeName));
		return this;
	}

	/**
	 * Sets the local restriction.
	 * 
	 * @return this builder
	 */
	public RestrictionBuilder<InstrumentationEntityBuilder<S>> newLocalRestriction() {
		return new RestrictionBuilder<InstrumentationEntityBuilder<S>>(this, entity.getLocalRestriction());
	}

	/**
	 * Finishes entity building and returns to the parent builder.
	 * 
	 * @return the parent builder
	 */
	public InstrumentationDescriptionBuilder entityDone() {
		parentBuilder.addInstrumentationEntity(entity);
		return parentBuilder;
	}

	@Override
	protected void setRestriction(Restriction restriction) {
		entity.setLocalRestriction(restriction);
	}

}
