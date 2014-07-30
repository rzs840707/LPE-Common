package org.aim.mainagent.events;

import java.util.HashMap;
import java.util.Map;

public class SynchronizedEventListener implements IEventListener<ISynchronizedEventProbe> {
	
	private static SynchronizedEventListener instance;
	
	private final Map<ProbeKey, ISynchronizedEventProbe> probes = new HashMap<>();
	
	private SynchronizedEventListener() {
	}
	
	public void onMonitorWait(Thread thread, Object monitor, long timestamp) {
		for (Class<? extends ISynchronizedEventProbe> probeClass : EventProbeRegistry.getInstance().getProbeClasses(getClass())) {
			try {
				ISynchronizedEventProbe probe = probeClass.newInstance();
				probe.setThread(thread);
				probe.setMonitor(monitor);
				probe.setEnteredTime(timestamp);
				probes.put(newProbeKey(thread, monitor), probe);
				probe.beforePart();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onMonitorEntered(Thread thread, Object monitor, long timestamp) {
		ISynchronizedEventProbe probe = probes.get(newProbeKey(thread, monitor));
		probe.afterPart();
	}
	
	private ProbeKey newProbeKey(Thread thread, Object monitor) {
		ProbeKey key = new ProbeKey();
		key.thread = thread;
		key.monitor = monitor;
		return key;
	}
	
	private class ProbeKey {
		private Thread thread;
		private Object monitor;
		
		@Override
		public int hashCode() {
			return thread.hashCode() * 31 + monitor.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ProbeKey)) {
				return false;
			}
			
			ProbeKey other = (ProbeKey) obj;
			return this.thread == other.thread && this.monitor == other.monitor;
		}
	}

	public static SynchronizedEventListener getInstance() {
		if (instance == null) {
			instance = new SynchronizedEventListener();
		}
		
		return instance;
	}
	
}
