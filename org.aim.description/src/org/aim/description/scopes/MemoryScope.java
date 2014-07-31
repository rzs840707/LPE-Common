package org.aim.description.scopes;

/**
 * This scope refers to tasks related to memory.
 * 
 * @author Henning Schulz
 * 
 */
public class MemoryScope implements Scope {
	private final long id;

	@Override
	public long getId() {
		return id;
	}

	public MemoryScope(long id) {
		this.id = id;
	}

	public MemoryScope() {
		this(System.nanoTime());
	}

	@Override
	public String toString() {
		return "Memory Scope";
	}

}
