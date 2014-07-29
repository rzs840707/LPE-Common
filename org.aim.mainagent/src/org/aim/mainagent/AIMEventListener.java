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
