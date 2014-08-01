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
package org.lpe.common.loadgenerator.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.lpe.common.loadgenerator.scenario.SchedulingMode;
import org.lpe.common.loadgenerator.scenario.VUserInitializationMode;
import org.lpe.common.util.LpeStringUtils;

/**
 * Configuration of the workload service of this load generator adapter.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class LGWorkloadConfig {
	private static final int DEFAULT_EXPERIMENT_DURATION = 10;
	private int numUsers;
	private String loadGeneratorPath = "NA";
	private String scenarioPath = "NA";
	private String resultPath = "NA";

	private SchedulingMode schedulingMode = SchedulingMode.dynamicScheduling;
	private VUserInitializationMode vUserInitMode = VUserInitializationMode.beforeRunning;

	private int rampUpUsersPerInterval = 1;
	private int rampUpIntervalLength = 1;

	private int coolDownUsersPerInterval = 1;
	private int coolDownIntervalLength = 1;

	private int experimentDuration = DEFAULT_EXPERIMENT_DURATION;

	/**
	 * @return the numUsers
	 */
	public int getNumUsers() {
		return numUsers;
	}

	/**
	 * @param numUsers
	 *            the numUsers to set
	 */
	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	/**
	 * @return the loadGeneratorPath
	 */
	public String getLoadGeneratorPath() {
		return loadGeneratorPath;
	}

	/**
	 * @param loadGeneratorPath
	 *            the loadGeneratorPath to set
	 */
	public void setLoadGeneratorPath(String loadGeneratorPath) {
		loadGeneratorPath = LpeStringUtils.correctFileSeparator(loadGeneratorPath);
		this.loadGeneratorPath = loadGeneratorPath;
	}

	/**
	 * @return the scenarioPath
	 */
	public String getScenarioPath() {
		return scenarioPath;
	}

	/**
	 * @param scenarioPath
	 *            the scenarioPath to set
	 */
	public void setScenarioPath(String scenarioPath) {
		scenarioPath = LpeStringUtils.correctFileSeparator(scenarioPath);
		this.scenarioPath = scenarioPath;
	}

	/**
	 * @return the resultPath
	 */
	public String getResultPath() {
		return resultPath;
	}

	/**
	 * @param resultPath
	 *            the resultPath to set
	 */
	public void setResultPath(String resultPath) {
		resultPath = LpeStringUtils.correctFileSeparator(resultPath);
		if (resultPath.endsWith(System.getProperty("file.separator"))) {
			resultPath = resultPath.substring(0, resultPath.length() - 1);
		}
		this.resultPath = resultPath;
	}

	/**
	 * @return the experimentDuration in seconds
	 */
	public int getExperimentDuration() {
		return experimentDuration;
	}

	/**
	 * @param experimentDuration
	 *            the experimentDuration to set in seconds
	 */
	public void setExperimentDuration(int experimentDuration) {
		this.experimentDuration = experimentDuration;
	}

	/**
	 * @return the vUserInitMode
	 */
	public VUserInitializationMode getvUserInitMode() {
		return vUserInitMode;
	}

	/**
	 * @param vUserInitMode
	 *            the vUserInitMode to set
	 */
	public void setvUserInitMode(VUserInitializationMode vUserInitMode) {
		this.vUserInitMode = vUserInitMode;
	}

	/**
	 * @return the schedulingMode
	 */
	public SchedulingMode getSchedulingMode() {
		return schedulingMode;
	}

	/**
	 * @param schedulingMode
	 *            the schedulingMode to set
	 */
	public void setSchedulingMode(SchedulingMode schedulingMode) {
		this.schedulingMode = schedulingMode;
	}

	/**
	 * @return the rampUpUsersPerInterval
	 */
	public int getRampUpUsersPerInterval() {
		return rampUpUsersPerInterval;
	}

	/**
	 * @param rampUpUsersPerInterval
	 *            the rampUpUsersPerInterval to set
	 */
	public void setRampUpUsersPerInterval(int rampUpUsersPerInterval) {
		this.rampUpUsersPerInterval = rampUpUsersPerInterval;
	}

	/**
	 * @return the rampUpIntervalLength [s]
	 */
	public int getRampUpIntervalLength() {
		return rampUpIntervalLength;
	}

	/**
	 * @param rampUpIntervalLength
	 *            the rampUpIntervalLength to set [s]
	 */
	public void setRampUpIntervalLength(int rampUpIntervalLength) {
		this.rampUpIntervalLength = rampUpIntervalLength;
	}

	/**
	 * @return the coolDownUsersPerInterval
	 */
	public int getCoolDownUsersPerInterval() {
		return coolDownUsersPerInterval;
	}

	/**
	 * @param coolDownUsersPerInterval
	 *            the coolDownUsersPerInterval to set
	 */
	public void setCoolDownUsersPerInterval(int coolDownUsersPerInterval) {
		this.coolDownUsersPerInterval = coolDownUsersPerInterval;
	}

	/**
	 * @return the coolDownIntervalLength [s]
	 */
	public int getCoolDownIntervalLength() {
		return coolDownIntervalLength;
	}

	/**
	 * @param coolDownIntervalLength
	 *            the coolDownIntervalLength to set [s]
	 */
	public void setCoolDownIntervalLength(int coolDownIntervalLength) {
		this.coolDownIntervalLength = coolDownIntervalLength;
	}

	/**
	 * corrects all paths to OS specific representation.
	 */
	public void correctPathSeparators() {
		resultPath = LpeStringUtils.correctFileSeparator(resultPath);
		if (resultPath.endsWith(System.getProperty("file.separator"))) {
			resultPath = resultPath.substring(0, resultPath.length() - 1);
		}

		scenarioPath = LpeStringUtils.correctFileSeparator(scenarioPath);
		loadGeneratorPath = LpeStringUtils.correctFileSeparator(loadGeneratorPath);

	}

}
