package org.aim.description.builder;

import org.aim.description.InstrumentationEntity;
import org.aim.description.scopes.APIScope;
import org.aim.description.scopes.ConstructorScope;
import org.aim.description.scopes.CustomScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.TraceScope;

/**
 * Builder of an {@link InstrumentationEntity} with a {@link TraceScope}. It is
 * only responsible for setting the sub-scope and delegates all further
 * buildings to the {@link InstrumentationEntityBuilder}.
 * 
 * @author Henning Schulz
 * 
 */
public class TraceEntityBuilder {
	private long scopeId;
	private final InstrumentationDescriptionBuilder parentBuilder;

	/**
	 * Constructor.
	 * 
	 * @param parentBuilder
	 *            builder which called this constructor
	 */
	public TraceEntityBuilder(InstrumentationDescriptionBuilder parentBuilder) {
		this(parentBuilder, System.nanoTime());
	}

	public TraceEntityBuilder(InstrumentationDescriptionBuilder parentBuilder, long id) {
		this.parentBuilder = parentBuilder;
		scopeId = id;
	}

	/**
	 * Sets a method scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param patterns
	 *            method patterns of the sub-scope
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setMethodSubScope(String... patterns) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new MethodScope(patterns), scopeId), parentBuilder);
	}

	/**
	 * Sets a constructor scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param classes
	 *            classes to be considered in the sub-scope
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setConstructorSubScope(String... classes) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new ConstructorScope(classes), scopeId), parentBuilder);
	}

	/**
	 * Sets an API scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param apiName
	 *            name of the API
	 * 
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setAPISubScope(String apiName) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new APIScope(apiName), scopeId), parentBuilder);
	}

	/**
	 * Sets an custom scope as sub-scope and go to an
	 * {@link InstrumentationEntityBuilder}.
	 * 
	 * @param scopeName
	 *            name of the scope
	 * 
	 * @return an {@code InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder<TraceScope> setCustomSubScope(String scopeName) {
		return new InstrumentationEntityBuilder<>(new TraceScope(new CustomScope(scopeName), scopeId), parentBuilder);
	}

}
