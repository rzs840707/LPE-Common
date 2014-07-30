package org.aim.mainagent.events;

import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.artifacts.records.WaitingTimeRecord;
import org.aim.mainagent.probes.GenericProbe;

public class SynchonizedBlocksWaitingTimeProbe implements ISynchronizedEventProbe {

	private Object monitor;
	private long waitStartTime;
	private long enteredTime;

	@Override
	public void beforePart() {
	}

	@Override
	public void afterPart() {
		WaitingTimeRecord record = new WaitingTimeRecord();
		record.setLocation(monitor.getClass().getName() + "@" + monitor.hashCode());
		record.setCallId(GenericProbe.getNewCallID());
		record.setTimeStamp(waitStartTime);
		record.setWaitingTime(enteredTime - waitStartTime);

		AbstractDataSource dataSource = org.aim.api.measurement.collector.AbstractDataSource.getDefaultDataSource();
		dataSource.newRecord(record);
	}

	@Override
	public void setThread(Thread thread) {
	}

	@Override
	public void setMonitor(Object monitor) {
		this.monitor = monitor;
	}

	@Override
	public void setWaitStartTime(long timestamp) {
		this.waitStartTime = timestamp;
	}

	@Override
	public void setEnteredTime(long timestamp) {
		this.enteredTime = timestamp;
	}

}
