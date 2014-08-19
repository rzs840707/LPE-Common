package org.lpe.common.loadgenerator.data;

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
	public TimeSpan(long start, long stop) {
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
