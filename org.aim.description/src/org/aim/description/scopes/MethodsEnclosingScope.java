package org.aim.description.scopes;

/**
 * This is a common interface for all scopes referring to sets of method
 * enclosures.
 * 
 * @author Henning Schulz
 * 
 */
public abstract class MethodsEnclosingScope implements Scope {
	private final long id;

	public MethodsEnclosingScope(long id) {
		this.id = id;
	}

	@Override
	public long getId() {
		return id;
	}
}
