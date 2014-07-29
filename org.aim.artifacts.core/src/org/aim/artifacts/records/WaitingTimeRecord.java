/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.artifacts.records;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.RecordValue;

/**
 * Records for monitoring waiting times, like monitor waits.
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

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public WaitingTimeRecord() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param timestamp
	 *            timestamp of record
	 * @param monitorClass
	 *            classname of the monitor
	 * @param monitorHashCode
	 *            hash code of the monitor
	 * @param waitingTime
	 *            timespan the thread had to wait
	 */
	public WaitingTimeRecord(long timestamp, String monitorClass, int monitorHashCode, long waitingTime) {
		super(timestamp);
		this.monitorClass = monitorClass;
		this.monitorHashCode = monitorHashCode;
		this.waitingTime = waitingTime;
	}

	@RecordValue(name = PAR_MONITOR_CLASS)
	String monitorClass;

	@RecordValue(name = PAR_MONITOR_HASH_CODE)
	int monitorHashCode;

	@RecordValue(name = PAR_WAITING_TIME)
	long waitingTime;

	/**
	 * @return the monitorClass
	 */
	public String getMonitorClass() {
		return monitorClass;
	}

	/**
	 * @param monitorClass
	 *            the monitorClass to set
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
	 * @param monitorHashCode
	 *            the monitorHashCode to set
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
	 * @param waitingTime
	 *            the waitingTime to set
	 */
	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}

}
