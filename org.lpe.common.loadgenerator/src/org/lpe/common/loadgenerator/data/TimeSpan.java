package org.lpe.common.loadgenerator.data;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Defines a times sapn.
 * 
 * @author Alexander Wert
 * 
 */
public class TimeSpan {
	private long start;
	private long stop;

	/**
	 * Constructor.
	 * 
	 * @param start
	 *            start timestamp in [ms]
	 * @param stop
	 *            stop timestamp in [ms]
	 */
	@JsonCreator
	public TimeSpan(@JsonProperty("start") long start, @JsonProperty("stop") long stop) {
		super();
		this.start = start;
		this.stop = stop;
	}

	/**
	 * @return the start timestamp in [ms]
	 */
	public long getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start timestamp in [ms] to set
	 */
	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * @return the stop timestamp in [ms]
	 */
	public long getStop() {
		return stop;
	}

	/**
	 * @param stop
	 *            the stop timestamp in [ms] to set
	 */
	public void setStop(long stop) {
		this.stop = stop;
	}

}
