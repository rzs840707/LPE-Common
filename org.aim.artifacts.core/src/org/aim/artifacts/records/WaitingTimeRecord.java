package org.aim.artifacts.records;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.RecordValue;

/**
 * 
 * @author Henning Schulz
 *
 */
public class WaitingTimeRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8172433493460433334L;
	
	public static final String PAR_WAITING_TIME = "waitingTime";
	
	public static final String PAR_MONITOR_CLASS = "monitorClass";
	
	public static final String PAR_MONITOR_HASH_CODE = "monitorHashCode";
	
	public WaitingTimeRecord() {
		super();
	}
	
	public WaitingTimeRecord(long timestamp, String monitorClass, int monitorHashCode, long waitingTime) {
		super(timestamp);
		this.monitorClass = monitorClass;
		this.monitorHashCode = monitorHashCode;
		this.waitingTime = waitingTime;
	}
	
	/**
	 * @return the monitorClass
	 */
	public String getMonitorClass() {
		return monitorClass;
	}

	/**
	 * @param monitorClass the monitorClass to set
	 */
	public void setMonitorClass(String monitorClass) {
		this.monitorClass = monitorClass;
	}

	/**
	 * @return the monitorHashCode
	 */
	public int getMonitorHashCode() {
		return monitorHashCode;
	}

	/**
	 * @param monitorHashCode the monitorHashCode to set
	 */
	public void setMonitorHashCode(int monitorHashCode) {
		this.monitorHashCode = monitorHashCode;
	}

	/**
	 * @return the waitingTime
	 */
	public long getWaitingTime() {
		return waitingTime;
	}

	/**
	 * @param waitingTime the waitingTime to set
	 */
	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}

	@RecordValue(name = PAR_MONITOR_CLASS)
	String monitorClass;
	
	@RecordValue(name = PAR_MONITOR_HASH_CODE)
	int monitorHashCode;
	
	@RecordValue(name = PAR_WAITING_TIME)
	long waitingTime;

}
