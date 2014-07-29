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
package org.aim.mainagent;

import java.util.HashMap;
import java.util.Map;

import org.aim.artifacts.probes.WaitingTimeProbe;
import org.aim.mainagent.probes.GenericProbe;

public class AIMEventListener {
	
	private final String type;
	
	public AIMEventListener(String type) {
		this.type = type;
	}
	
	private Map<Thread, WaitingTimeProbe> waitingTimeProbes = new HashMap<>();

	public void onWaitingTimeStart(Thread thread, Object monitor, long timestamp) {
		WaitingTimeProbe probe = waitingTimeProbes.get(thread);
		if (probe == null) {
			probe = new WaitingTimeProbe();
			probe._WaitingTimeRecord_monitorClass = monitor.getClass().getName();
			probe._WaitingTimeRecord_monitorHash = monitor.hashCode();
			probe._GenericProbe_startTime = timestamp;
			probe._GenericProbe_callId = GenericProbe.getNewCallID();
			probe._GenericProbe_collector = org.aim.api.measurement.collector.AbstractDataSource.getDefaultDataSource();
			waitingTimeProbes.put(thread, probe);
		}
	}

	public void onWaitingTimeEnd(Thread thread, Object monitor, long timestamp) {
		waitingTimeProbes.get(thread).afterPart();
		waitingTimeProbes.remove(thread);
	}
	
	public String getType() {
		return type;
	}
	
}
