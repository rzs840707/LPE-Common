package org.aim.description.builder;

import org.aim.description.restrictions.Restriction;

/**
 * Common interface of builders building restrictable elements.
 * 
 * @author Henning Schulz
 * 
 */
public abstract class AbstractRestrictableBuilder {

	/**
	 * Restricts this object by the given {@link Restriction}.
	 * 
	 * @param restriction
	 *            restriction to be applied
	 */
	protected abstract void setRestriction(Restriction restriction);

}
