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

/**
 * Interface for Synchronized event probes.
 * 
 * @author Alexander Wert
 * 
 */
public interface ISynchronizedEventProbe extends IEventProbe {

	/**
	 * Sets the thread.
	 * 
	 * @param thread
	 *            thread to set
	 */
	void setThread(Thread thread);

	/**
	 * Sets the monitor.
	 * 
	 * @param monitor
	 *            monitor to set
	 */
	void setMonitor(Object monitor);

	/**
	 * Sets the wait start time.
	 * 
	 * @param timestamp
	 *            timestamp to set
	 */
	void setWaitStartTime(long timestamp);

	/**
	 * Sets the entered time.
	 * 
	 * @param timestamp
	 *            timestamp to set
	 */
	void setEnteredTime(long timestamp);

}
