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

import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.artifacts.records.WaitingTimeRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.SynchronizedScope;
import org.aim.mainagent.probes.GenericProbe;

/**
 * Event probe for generating the waiting time of threads while they are waiting
 * to enter a {@code synchronized} block.
 * 
 * @author Henning Schulz
 * 
 */
public class SynchronizedBlocksWaitingTimeProbe implements ISynchronizedEventProbe {

	public static final MeasurementProbe<SynchronizedScope> MODEL_PROBE = new MeasurementProbe<>("SynchronizedProbe");

	private Object monitor;
	private long waitStartTime;
	private long enteredTime;

	@Override
	public void beforePart() {
	}

	@Override
	public void afterPart() {
		WaitingTimeRecord record = new WaitingTimeRecord();
		StringBuilder locationBuilder = new StringBuilder();
		locationBuilder.append(monitor.getClass().getName());
		if (monitor instanceof Class<?>) {
			locationBuilder.append("<");
			locationBuilder.append(((Class<?>) monitor).getName());
			locationBuilder.append(">");
		}
		locationBuilder.append("@");
		locationBuilder.append(monitor.hashCode());
		record.setLocation(locationBuilder.toString());
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
