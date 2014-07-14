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
package org.overhead;

import java.util.ArrayList;
import java.util.List;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.description.InstrumentationDescription;
import org.aim.api.instrumentation.description.InstrumentationDescriptionBuilder;
import org.aim.api.instrumentation.entities.OverheadData;
import org.aim.api.instrumentation.entities.OverheadRecord;
import org.aim.mainagent.AdaptiveInstrumentationFacade;

public class OverheadEstimator {

	@SuppressWarnings("unchecked")
	public static List<OverheadRecord> measureOverhead(String probeTypeName) throws InstrumentationException {
		Class<? extends AbstractEnclosingProbe> probeType = null;
		try {
			probeType = (Class<? extends AbstractEnclosingProbe>) Class.forName(probeTypeName);
		} catch (ClassNotFoundException e) {
			throw new InstrumentationException("Where not able to measure overhead. {}", e);
		}

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.addMethodInstrumentation().addMethod(OverheadTargetClass.class.getName() + ".called()")
				.addProbe(probeType).entityDone();

		List<OverheadRecord> records = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			records.add(runExperiment(idBuilder.build()));
		}

		return records;
	}

	public static OverheadRecord runExperiment(InstrumentationDescription instDescr) throws InstrumentationException {
		List<OverheadRecord> records = new ArrayList<>();
		AdaptiveInstrumentationFacade.getInstance().instrument(instDescr);

		OverheadTargetClass target = new OverheadTargetClass();
		for (int i = 0; i < 1010; i++) {
			OverheadRecord rec = target.caller();
			if (i > 10) {
				records.add(rec);
			}

		}

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		OverheadRecord record = new OverheadRecord();
		OverheadData data = new OverheadData();
		data.setoRecords(records);
		record.setOverallNanoTimeSpan((long) data.getMeanOverall());
		record.setBeforeNanoTimeSpan((long) data.getMeanBefore());
		record.setAfterNanoTimeSpan((long) data.getMeanAfter());
		return record;
	}

}
