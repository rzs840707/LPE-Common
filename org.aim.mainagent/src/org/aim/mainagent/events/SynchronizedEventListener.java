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
package org.aim.mainagent.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for synchronized events.
 * 
 * @author Alexander Wert
 * 
 */
public final class SynchronizedEventListener implements IEventListener<ISynchronizedEventProbe> {

	private static SynchronizedEventListener instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static SynchronizedEventListener getInstance() {
		if (instance == null) {
			instance = new SynchronizedEventListener();
		}

		return instance;
	}

	private final Map<ProbeKey, ISynchronizedEventProbe> probes = new HashMap<>();

	private SynchronizedEventListener() {
	}

	/**
	 * Event callback when a thread starts waiting for a monitor.
	 * 
	 * @param thread
	 *            thread which waits
	 * @param monitor
	 *            monitor which has been requested
	 * @param timestamp
	 *            timestamp
	 */
	public void onMonitorWait(Thread thread, Object monitor, long timestamp) {
		for (Class<? extends ISynchronizedEventProbe> probeClass : EventProbeRegistry.getInstance().getProbeClasses(
				getClass())) {
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

	/**
	 * Event callback when a thread gets a monitor.
	 * 
	 * @param thread
	 *            thread which waits
	 * @param monitor
	 *            monitor which has been requested
	 * @param timestamp
	 *            timestamp
	 */
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
		private static final int HASH_CONSTANT = 31;
		private Thread thread;
		private Object monitor;

		@Override
		public int hashCode() {
			return thread.hashCode() * HASH_CONSTANT + monitor.hashCode();
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

}
