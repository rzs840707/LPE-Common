package org.aim.description.scopes;

/**
 * This scope refers to all points in execution related to {@code synchronized}
 * events.
 * 
 * @author Henning Schulz
 * 
 */
public class SynchronizedScope implements Scope {
	private final long id;

	@Override
	public long getId() {
		return id;
	}

	public SynchronizedScope(long id) {
		this.id = id;
	}

	public SynchronizedScope() {
		this(System.nanoTime());
	}

	@Override
	public String toString() {
		return "Synchronized Scope";
	}

}
