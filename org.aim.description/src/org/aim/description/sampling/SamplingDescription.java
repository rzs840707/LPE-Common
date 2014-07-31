package org.aim.description.sampling;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents a sampling description.
 * 
 * @author Henning Schulz
 * 
 */
public class SamplingDescription {

	private final long delay;

	private final String resourceName;

	/**
	 * Constructor.
	 * 
	 * @param resourceName
	 *            resource to be sampled
	 * @param delay
	 *            sampling delay
	 */
	@JsonCreator
	public SamplingDescription(@JsonProperty("resourceName") String resourceName, @JsonProperty("delay") long delay) {
		this.resourceName = resourceName;
		this.delay = delay;
	}

	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public String toString() {
		return resourceName + " +" + delay;
	}

}
