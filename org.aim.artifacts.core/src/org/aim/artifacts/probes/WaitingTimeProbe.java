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
package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.WaitingTimeRecord;

/**
 * Probe for monitoring waiting times like monitor waits.
 * 
 * @author Henning Schulz
 * 
 */
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
