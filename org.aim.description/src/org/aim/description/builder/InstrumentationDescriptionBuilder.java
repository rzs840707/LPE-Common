package org.aim.description.builder;

import org.aim.description.InstrumentationDescription;
import org.aim.description.InstrumentationEntity;
import org.aim.description.restrictions.Restriction;
import org.aim.description.sampling.SamplingDescription;
import org.aim.description.scopes.APIScope;
import org.aim.description.scopes.AllocationScope;
import org.aim.description.scopes.ConstructorScope;
import org.aim.description.scopes.CustomScope;
import org.aim.description.scopes.MemoryScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.SynchronizedScope;
import org.aim.description.scopes.TraceScope;

/**
 * Builder for {@link InstrumentationDescription}s.
 * 
 * @author Henning Schulz
 * 
 */
public class InstrumentationDescriptionBuilder extends AbstractRestrictableBuilder {

	private InstrumentationDescription description;

	/**
	 * Constructor.
	 */
	public InstrumentationDescriptionBuilder() {
		this.description = new InstrumentationDescription();
	}

	protected void addInstrumentationEntity(InstrumentationEntity<?> entity) {
		description.addInstrumentationEntity(entity);
	}

	/**
	 * Starts definition of the global restriction.
	 * 
	 * @return a {@link RestrictionBuilder}
	 */
	public RestrictionBuilder<InstrumentationDescriptionBuilder> newGlobalRestriction() {
		return new RestrictionBuilder<InstrumentationDescriptionBuilder>(this, description.getGlobalRestriction());
	}

	@Override
	protected void setRestriction(Restriction restriction) {
		description.setGlobalRestriction(restriction);
	}

	/**
	 * Adds a new {@link SamplingDescription}.
	 * 
	 * @param resource
	 *            resource to be sampled
	 * @param delay
	 *            sampling delay
	 * @return this builder
	 */
	public InstrumentationDescriptionBuilder newSampling(String resource, int delay) {
		description.addSamplingDescription(new SamplingDescription(resource, delay));
		return this;
	}

	/**
	 * Builds and returns the instrumentation description.
	 * 
	 * @return the built instrumentation description.
	 */
	public InstrumentationDescription build() {
		return description;
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MethodScope}.
	 * 
	 * @param patterns
	 *            methods in the method scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<MethodScope> newMethodScopeEntity(String... patterns) {
		return new InstrumentationEntityBuilder<>(new MethodScope(patterns), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link TraceScope}. In a first step, the sub-scope of the trace scope has
	 * to be specified.
	 * 
	 * @return an {@link TraceEntityBuilder}
	 */
	public TraceEntityBuilder newTraceScopeEntity() {
		return new TraceEntityBuilder(this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MemoryScope}.
	 * 
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<MemoryScope> newMemoryScopeEntity() {
		return new InstrumentationEntityBuilder<>(new MemoryScope(), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link AllocationScope}.
	 * 
	 * @param classes
	 *            classes of the allocation scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<AllocationScope> newAllocationScopeEntity(String... classes) {
		return new InstrumentationEntityBuilder<>(new AllocationScope(classes), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link ConstructorScope}.
	 * 
	 * @param classes
	 *            classes of the constructor scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<ConstructorScope> newConstructorScopeEntity(String... classes) {
		return new InstrumentationEntityBuilder<>(new ConstructorScope(classes), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link SynchronizedScope}.
	 * 
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<SynchronizedScope> newSynchronizedScopeEntity() {
		return new InstrumentationEntityBuilder<>(new SynchronizedScope(), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link APIScope}.
	 * 
	 * @param apiName
	 *            name of the API
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<APIScope> newAPIScopeEntity(String apiName) {
		return new InstrumentationEntityBuilder<>(new APIScope(apiName), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link CustomScope}.
	 * 
	 * @param scopeName
	 *            name of the scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<CustomScope> newCustomScopeEntity(String scopeName) {
		return new InstrumentationEntityBuilder<>(new CustomScope(scopeName), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MethodScope}.
	 * 
	 * @param patterns
	 *            methods in the method scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<MethodScope> newMethodScopeEntityWithId(long id, String... patterns) {
		return new InstrumentationEntityBuilder<>(new MethodScope(patterns, id), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link TraceScope}. In a first step, the sub-scope of the trace scope has
	 * to be specified.
	 * 
	 * @return an {@link TraceEntityBuilder}
	 */
	public TraceEntityBuilder newTraceScopeEntityWithId(long id) {
		return new TraceEntityBuilder(this, id);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MemoryScope}.
	 * 
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<MemoryScope> newMemoryScopeEntityWithId(long id) {
		return new InstrumentationEntityBuilder<>(new MemoryScope(id), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link AllocationScope}.
	 * 
	 * @param classes
	 *            classes of the allocation scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<AllocationScope> newAllocationScopeEntityWithId(long id, String... classes) {
		return new InstrumentationEntityBuilder<>(new AllocationScope(classes, id), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link ConstructorScope}.
	 * 
	 * @param classes
	 *            classes of the constructor scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<ConstructorScope> newConstructorScopeEntityWithId(long id, String... classes) {
		return new InstrumentationEntityBuilder<>(new ConstructorScope(classes, id), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link SynchronizedScope}.
	 * 
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<SynchronizedScope> newSynchronizedScopeEntityWithId(long id) {
		return new InstrumentationEntityBuilder<>(new SynchronizedScope(id), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link APIScope}.
	 * 
	 * @param apiName
	 *            name of the API
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<APIScope> newAPIScopeEntityWithId(long id, String apiName) {
		return new InstrumentationEntityBuilder<>(new APIScope(apiName, id), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link CustomScope}.
	 * 
	 * @param scopeName
	 *            name of the scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<CustomScope> newCustomScopeEntityWithId(long id, String scopeName) {
		return new InstrumentationEntityBuilder<>(new CustomScope(scopeName, id), this);
	}

}
