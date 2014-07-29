package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.WaitingTimeRecord;

public class WaitingTimeProbe extends AbstractEnclosingProbe {
	@ProbeVariable
	public long _WaitingTimeRecord_endTime;
	@ProbeVariable
	public WaitingTimeRecord _WaitingTimeRecord_record;
	@ProbeVariable
	public int _WaitingTimeRecord_monitorHash;
	@ProbeVariable
	public String _WaitingTimeRecord_monitorClass;
	
	@ProbeAfterPart
	public void afterPart() {
		_WaitingTimeRecord_endTime = System.currentTimeMillis();
		_WaitingTimeRecord_record = new WaitingTimeRecord();
		_WaitingTimeRecord_record.setCallId(_GenericProbe_callId);
		_WaitingTimeRecord_record.setMonitorClass(_WaitingTimeRecord_monitorClass);
		_WaitingTimeRecord_record.setMonitorHashCode(_WaitingTimeRecord_monitorHash);
		_WaitingTimeRecord_record.setWaitingTime(_WaitingTimeRecord_endTime - _GenericProbe_startTime);
		_WaitingTimeRecord_record.setTimeStamp(_GenericProbe_startTime);
		_GenericProbe_collector.newRecord(_WaitingTimeRecord_record);
	}
}
