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
package org.lpe.common.loadgenerator.scenario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.lpe.common.loadgenerator.config.LGWorkloadConfig;

/**
 * Modifier class to adapt load generator scenarios.
 * 
 * @author Le-Huan Stefan Tran
 */
public class ScenarioModifier {

	private static final int ZERO = 0;

	private static final int ONE = 1;

	private static final String NEWLINE = System.getProperty("line.separator");

	private static final String SCENARIO_FILE_SUFFIX = ".lrs";

	private static final String SCENARIO_TAG_OPEN = "{";
	private static final String SCENARIO_TAG_CLOSE = "}";

	private static final String SCENARIO_PRIVATE_CONFIG_TAG = "ScenarioPrivateConfig";
	private static final String SCENARIO_PATH_KEY = "Path=";
	private static final String V_USERS_KEY = "Vusers=";

	private static final String GROUP_CHIEF_TAG = "GroupChief";
	private static final String GROUP_CHIEF_SETTINGS_TAG = "ChiefSettings";
	private static final String TEST_CHIEF_TAG = "TestChief";

	private static final String SCHEDULER_CONFIG_TAG = "ScenarioSchedulerConfig";

	private static final String HOST = "localhost";

	private static final CharSequence SCHEDULING_PATTERN = "<Scheduling>";
	private static final CharSequence SCHEDULING_PATTERN_END = "</Scheduling>";

	private static final CharSequence SCHEDULING_GROUPNAME_PATTERN = "<GroupName>";
	private static final CharSequence SCHEDULING_GROUPNAME_PATTERN_END = "</GroupName>";

	private static final int INITIALIZATION_INTERVALMODE_VUSERS = 50;

	private LGWorkloadConfig lrConfig;

	private final LinkedList<String> groupNames = new LinkedList<>();

	private final HashMap<String, Integer> groupUserNums = new HashMap<>();

	/**
	 * Modifies the scenario file according to the passed workload
	 * configuration.
	 * 
	 * @param lrConfig
	 *            describes the modification on the scenario
	 * @return the path to the new scenario file
	 * @throws IOException
	 *             if scenario modification fails
	 */
	public synchronized String modifyScenario(final LGWorkloadConfig lrConfig) throws IOException {
		this.lrConfig = lrConfig;

		String newScenarioPath = null;
		if (lrConfig.getScenarioPath().endsWith(".lrs")) {
			newScenarioPath = lrConfig.getScenarioPath().replace(SCENARIO_FILE_SUFFIX, "_new" + SCENARIO_FILE_SUFFIX);
		} else {
			throw new IllegalArgumentException("Workload error. Scenario path must end with" + SCENARIO_FILE_SUFFIX
					+ " !");
		}

		// Read scenario file and save modified version in buffer
		String scriptName = null;
		final StringBuilder writeBuffer = new StringBuilder();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(lrConfig.getScenarioPath()), "UTF8"));
		String line = null;
		final LinkedList<String> currentTags = new LinkedList<String>();
		int numGroupUsers = lrConfig.getNumUsers();
		int allUsersIndex = 0;
		while ((line = reader.readLine()) != null) {
			updateTagChange(currentTags, line);

			if (line.trim().startsWith(SCENARIO_PATH_KEY) && !currentTags.isEmpty()
					&& currentTags.peek().equals(SCENARIO_PRIVATE_CONFIG_TAG)) {
				writeBuffer.append(SCENARIO_PATH_KEY);
				writeBuffer.append(newScenarioPath);
			} else if (line.trim().startsWith(V_USERS_KEY) && !currentTags.isEmpty()
					&& currentTags.peek().equals(SCENARIO_PRIVATE_CONFIG_TAG)) {
				writeBuffer.append(V_USERS_KEY);
				allUsersIndex = writeBuffer.length();
			} else if (currentTags.size() == 2 && currentTags.indexOf(TEST_CHIEF_TAG) == 1 && scriptName == null) {
				writeBuffer.append(line);
				scriptName = currentTags.peek();
			} else if (!currentTags.isEmpty() && currentTags.peek().equals(GROUP_CHIEF_TAG)) {
				writeBuffer.append(line);
				writeBuffer.append(NEWLINE);
				generateWorkloadGroups(currentTags, reader, writeBuffer);
				writeBuffer.append(SCENARIO_TAG_CLOSE);
			} else if (!currentTags.isEmpty() && currentTags.peek().equals(SCHEDULER_CONFIG_TAG)
					&& line.contains(SCHEDULING_PATTERN)) {
				writeBuffer.append(line);
				writeBuffer.append(NEWLINE);
				modifySchedule(currentTags, reader, writeBuffer, lrConfig.getNumUsers());
			} else if (!currentTags.isEmpty() && currentTags.peek().equals(SCHEDULER_CONFIG_TAG)
					&& line.contains(SCHEDULING_GROUPNAME_PATTERN)) {

				// skip old configuration
				while (line != null && !line.contains(SCHEDULING_GROUPNAME_PATTERN_END)) {
					line = reader.readLine();
				}

				writeBuffer.append(SCHEDULING_GROUPNAME_PATTERN);
				writeBuffer.append(groupNames.getFirst().toLowerCase());
				writeBuffer.append(SCHEDULING_GROUPNAME_PATTERN_END);

				numGroupUsers = groupUserNums.get(groupNames.pop());
			} else {
				writeBuffer.append(line);
			}
			writeBuffer.append(NEWLINE);

		}

		reader.close();

		// Insert number of vusers (cannot be appended when passing the
		// vusers-key, since the number may be affected when counting the
		// groups)
		writeBuffer.insert(allUsersIndex, lrConfig.getNumUsers());

		// Write buffer to file

		final BufferedWriter writer = new BufferedWriter(new FileWriter(newScenarioPath, false));
		writer.write(writeBuffer.toString());
		writer.flush();
		writer.close();

		return newScenarioPath;
	}

	private void updateTagChange(final LinkedList<String> currentTags, final String line) {
		if (line.trim().startsWith(SCENARIO_TAG_OPEN)) {
			currentTags.push(line.substring(1));

		} else if (line.trim().startsWith(SCENARIO_TAG_CLOSE)) {
			currentTags.pop();

		}

	}

	private void generateWorkloadGroups(final LinkedList<String> currentTags, final BufferedReader reader, final StringBuilder writeBuffer)
			throws IOException {

		final LinkedList<String> scriptNames = new LinkedList<>();

		// skip old workload config and find group and script names
		String line = null;
		String groupName = null;
		String scriptName = null;

		while ((line = reader.readLine()) != null) {
			updateTagChange(currentTags, line);

			if (!currentTags.contains(GROUP_CHIEF_TAG)) {
				break;
			}

			if (line.startsWith("5")) {
				scriptName = line.substring(2);
			}

			if ((groupName != null) && !currentTags.contains(groupName)) {
				groupNames.add(groupName);
				scriptNames.add(scriptName);
				groupName = null;
			}

			if ((groupName == null) && !GROUP_CHIEF_TAG.equals(currentTags.peek())) {
				groupName = currentTags.peek();
			}
		}

		// generate groups
		lrConfig.setNumUsers(Math.max(groupNames.size(), lrConfig.getNumUsers()));
		final int numGroupUsers = lrConfig.getNumUsers() / groupNames.size();
		int carryover = lrConfig.getNumUsers() % groupNames.size();
		for (final String gn : groupNames) {
			int n = numGroupUsers;

			if (carryover > 0) {
				n++;
				carryover--;
			}

			groupUserNums.put(gn, n);
			generateWorkloadGroup(currentTags, reader, writeBuffer, gn, scriptNames.pop(), n);
		}
	}

	private void generateWorkloadGroup(final LinkedList<String> currentTags, final BufferedReader reader,
			final StringBuilder writeBuffer, final String groupName, final String scriptName, final int numGroupUser) throws IOException {

		// generate group
		writeBuffer.append(NEWLINE);
		writeBuffer.append(SCENARIO_TAG_OPEN);
		writeBuffer.append(groupName);
		writeBuffer.append(NEWLINE);

		for (int user = 1; user <= numGroupUser; user++) {
			writeBuffer.append(NEWLINE);
			writeBuffer.append(SCENARIO_TAG_OPEN);
			writeBuffer.append(user);
			writeBuffer.append(NEWLINE);

			writeBuffer.append("5=");
			writeBuffer.append(scriptName);
			writeBuffer.append(NEWLINE);

			writeBuffer.append("9=");
			writeBuffer.append(HOST);
			writeBuffer.append(NEWLINE);

			writeBuffer.append("SEED_NUM=");
			writeBuffer.append(ZERO);
			writeBuffer.append(NEWLINE);

			writeBuffer.append(SCENARIO_TAG_CLOSE);
			writeBuffer.append(NEWLINE);
		}

		// generate chief settings
		writeBuffer.append(NEWLINE);
		writeBuffer.append(SCENARIO_TAG_OPEN);
		writeBuffer.append(GROUP_CHIEF_SETTINGS_TAG);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("5=");
		writeBuffer.append(scriptName);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("9=");
		writeBuffer.append(HOST);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("GroupParam=");
		writeBuffer.append(NEWLINE);

		writeBuffer.append("Enabled=1");
		writeBuffer.append(NEWLINE);

		writeBuffer.append("EmulatedLocation=");
		writeBuffer.append(NEWLINE);

		writeBuffer.append(SCENARIO_TAG_CLOSE);
		writeBuffer.append(NEWLINE);

		// close group
		writeBuffer.append(SCENARIO_TAG_CLOSE);
		writeBuffer.append(NEWLINE);
	}

	/**
	 * Modify the schedule
	 */
	private void modifySchedule(final LinkedList<String> currentTags, final BufferedReader reader, final StringBuilder writeBuffer,
			final int numUsers) throws IOException {

		writeBuffer.append(NEWLINE);
		writeBuffer.append("            <IsDefaultScheduler>true</IsDefaultScheduler>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);

		// Skip old schedule instructions
		String line = reader.readLine();
		while (line != null && !line.contains(SCHEDULING_PATTERN_END)) {
			line = reader.readLine();
		}

		insertInitializationMode(writeBuffer);

		switch (lrConfig.getSchedulingMode()) {
		case runUntilCompletion:
			writeBuffer.append("            <RunUntilComplete>");
			writeBuffer.append(NEWLINE);
			writeBuffer.append(NEWLINE);
			insertRampUp(writeBuffer, numUsers);
			writeBuffer.append("            </RunUntilComplete>");
			writeBuffer.append(NEWLINE);
			writeBuffer.append(NEWLINE);
			break;
		case dynamicScheduling:
			writeBuffer.append("            <DynamicScheduling>");
			writeBuffer.append(NEWLINE);
			writeBuffer.append(NEWLINE);
			insertRampUp(writeBuffer, numUsers);
			insertDuration(writeBuffer);
			insertRampDownAll(writeBuffer);
			writeBuffer.append("            </DynamicScheduling>");
			writeBuffer.append(NEWLINE);
			writeBuffer.append(NEWLINE);
			break;
		default:
			throw new IllegalArgumentException("Unknown scheduling type!");
		}

		// Append end pattern
		writeBuffer.append(SCHEDULING_PATTERN_END);
		writeBuffer.append(NEWLINE);
	}

	/**
	 * Insert initialization behavior of VUsers
	 */
	private void insertInitializationMode(final StringBuilder writeBuffer) throws IOException {
		switch (lrConfig.getvUserInitMode()) {
		case simultaneously:
			insertSimultaneousInitMode(writeBuffer);
			break;

		case beforeRunning:
			// Initialize each VUsers just before it runs.
			// It is the default configuration thus nothing to insert here.
			break;
		case interval:
			insertIntervalInitMode(writeBuffer);
			break;
		default:
			throw new IllegalArgumentException("Unknown v-user initialization mode!");
		}
	}

	/**
	 * Initialize given number of VUsers every given interval
	 * 
	 */
	private void insertIntervalInitMode(final StringBuilder writeBuffer) {
		writeBuffer.append("            <Initialization>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              <RampInitAll>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <PrevAction />");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <Batch>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <Count>");
		writeBuffer.append(INITIALIZATION_INTERVALMODE_VUSERS);
		writeBuffer.append("</Count>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <Interval>");
		writeBuffer.append(ONE);
		writeBuffer.append("</Interval>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </Batch>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              </RampInitAll>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              <DelayAfterInitialization>");
		writeBuffer.append(ZERO);
		writeBuffer.append("</DelayAfterInitialization>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("            </Initialization>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
	}

	/**
	 * Initialize all VUsers simultaneously
	 * 
	 */
	private void insertSimultaneousInitMode(final StringBuilder writeBuffer) {
		writeBuffer.append("            <Initialization>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              <InitAll>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <PrevAction />");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              </InitAll>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              <DelayAfterInitialization>");
		writeBuffer.append(ZERO);
		writeBuffer.append("</DelayAfterInitialization>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("            </Initialization>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
	}

	/**
	 * Insert the ramp-up behavior
	 */
	private void insertRampUp(final StringBuilder writeBuffer, final int numUsers) throws IOException {

		writeBuffer.append("              <RampUp>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <PrevAction />");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <Batch>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("                  <Count>");
		writeBuffer.append(lrConfig.getRampUpUsersPerInterval());
		writeBuffer.append("</Count>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("                  <Interval>");
		writeBuffer.append(lrConfig.getRampUpIntervalLength());
		writeBuffer.append("</Interval>");

		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </Batch>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <TotalVusersNumber>");
		writeBuffer.append(numUsers);
		writeBuffer.append("</TotalVusersNumber>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              </RampUp>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
	}

	/**
	 * Insert experiment run duration
	 */
	private void insertDuration(final StringBuilder writeBuffer) throws IOException {
		writeBuffer.append("              <Duration>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <PrevAction />");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <RunFor>");
		writeBuffer.append(lrConfig.getExperimentDuration());
		writeBuffer.append("</RunFor>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              </Duration>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
	}

	/**
	 * Insert cool down behavior
	 */
	private void insertRampDownAll(final StringBuilder writeBuffer) throws IOException {

		writeBuffer.append("              <RampDownAll>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                  <PrevAction />");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </StartCondition>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                <Batch>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("                  <Count>");
		writeBuffer.append(lrConfig.getCoolDownUsersPerInterval());
		writeBuffer.append("</Count>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);

		writeBuffer.append("                  <Interval>");
		writeBuffer.append(lrConfig.getCoolDownIntervalLength());
		writeBuffer.append("</Interval>");

		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("                </Batch>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
		writeBuffer.append("              </RampDownAll>");
		writeBuffer.append(NEWLINE);
		writeBuffer.append(NEWLINE);
	}

}
