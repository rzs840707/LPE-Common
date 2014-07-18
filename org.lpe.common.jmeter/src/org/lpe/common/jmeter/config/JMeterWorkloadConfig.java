/**
 * Copyright 2014 SAP AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.lpe.common.jmeter.config;

import java.util.Properties;

/**
 * This configuration comprises all the JMeter configuration possiblites.
 * 
 * @author Peter Merkert
 */
public class JMeterWorkloadConfig {

	/**
	 * Flag if a log file should be created.
	 */
	private boolean createLog;

	/**
	 * The experiment duration in seconds.
	 */
	private int experimentDuration; // [sec]

	/**
	 * The ramp up interval in seconds.
	 */
	private double rampUpInterval; // [sec]

	/**
	 * The number of users per ramp up interval.
	 */
	private double rampUpNumUsersPerInterval;

	/**
	 * The cool down interval in seconds.
	 */
	private double coolDownInterval; // [sec]

	/**
	 * The number of users per cool down interval.
	 */
	private double coolDownNumUsersPerInterval;

	/**
	 * The maximum number of users during the experiment.
	 */
	private int numUsers;

	/**
	 * The minimum think time in ms.
	 */
	private int thinkTimeMinimum; // [ms]

	/**
	 * The maximum think time in ms.
	 */
	private int thinkTimeMaximum; // [ms]

	/**
	 * The path to the JMeter root folder. The bin folder must be a sub folder in this directory.
	 */
	private String pathToJMeterRootFolder;

	/**
	 * The path to the .jmx load script.
	 */
	private String pathToScript; // [*.jmx]

	/**
	 * The default output file will be used to store the output of JMeter. It is required to read the JMeter output.
	 * Otherwise JMeter waits to infinity till the output is read by anyone.<br />
	 * This parameter is added to the JMeter bin directory!
	 */
	private String defaultOutputFile; // [*.out]

	/**
	 * The path to the JMeter result file. The result file contains the sampling values JMeter collects during the load
	 * script run.
	 */
	private String pathToSamplingFile; // [*.csv]

	/**
	 * The prefix each log file has.
	 */
	private String logFilePrefix;

	/**
	 * Custom additional properties can be passed with this {@link Properties}.
	 */
	Properties additionalProps;

	/**
	 * Initializes all variables with default values.
	 */
	public JMeterWorkloadConfig() {
		createLog = false;
		experimentDuration = 1;
		rampUpInterval = 1.0;
		rampUpNumUsersPerInterval = 1;
		coolDownInterval = 1;
		coolDownNumUsersPerInterval = 1;
		numUsers = 1;
		thinkTimeMinimum = 1000;
		thinkTimeMaximum = 1000;
		pathToJMeterRootFolder = "";
		pathToScript = "";
		defaultOutputFile = "jmeter_spotter.out";
		logFilePrefix = "JMETWRAPPERLOG_";
		pathToSamplingFile = "";
		additionalProps = new Properties();
	}

	/**
	 * Checks if the JMeter-log should be generated and parsed.
	 * 
	 * @return the flag
	 */
	public boolean getCreateLogFlag() {
		return createLog;
	}

	/**
	 * Sets the flag which determines if the JMeter-log should be generated and parsed.
	 * 
	 * @param createLog true if log should pe created/parsed
	 */
	public void setCreateLogFlag(boolean createLog) {
		this.createLog = createLog;
	}

	/**
	 * Gets the upper limit for ThinkTimes in the script (actual think time is a random number between minimum and
	 * maximum).
	 * 
	 * @return the maximum in MS
	 */
	public int getThinkTimeMaximum() {
		return thinkTimeMaximum;
	}

	/**
	 * Sets the upper limit for ThinkTimes in the script (actual think time is a random number between minimum and
	 * maximum).
	 * 
	 * @param thinkTimeMaximum the maximum think time in MS
	 */
	public void setThinkTimeMaximum(int thinkTimeMaximum) {
		this.thinkTimeMaximum = thinkTimeMaximum;
	}

	/**
	 * Gets the lower limit for ThinkTimes in the script (actual think time is a random number between minimum and
	 * maximum).
	 * 
	 * @return the minimum in MS
	 */
	public int getThinkTimeMinimum() {
		return thinkTimeMinimum;
	}

	/**
	 * Sets the lower limit for ThinkTimes in the script (actual think time is a random number between minimum and
	 * maximum).
	 * 
	 * @param thinkTimeMinimum the minimum think time in MS
	 */
	public void setThinkTimeMinimum(int thinkTimeMinimum) {
		this.thinkTimeMinimum = thinkTimeMinimum;
	}

	/**
	 * Get the test duration in seconds.
	 * 
	 * @return duration in seconds
	 */
	public int getExperimentDuration() {
		return experimentDuration;
	}

	/**
	 * Set the test duration in seconds.
	 * 
	 * @param durationSeconds duration in seconds
	 */
	public void setExperimentDuration(int durationSeconds) {
		this.experimentDuration = durationSeconds;
	}

	/**
	 * Gets the number of users to simulate.
	 * 
	 * @return usercount
	 */
	public int getNumUsers() {
		return numUsers;
	}

	/**
	 * Gets the number of users to simulate.
	 * 
	 * @param numUsers the number of users
	 */
	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	/**
	 * @return the rampUpInterval
	 */
	public double getRampUpInterval() {
		return rampUpInterval;
	}

	/**
	 * @param rampUpInterval the rampUpInterval to set [seconds]
	 */
	public void setRampUpInterval(double rampUpInterval) {
		this.rampUpInterval = rampUpInterval;
	}

	/**
	 * @return the rampUpNumUsersPerInterval
	 */
	public double getRampUpNumUsersPerInterval() {
		return rampUpNumUsersPerInterval;
	}

	/**
	 * @param rampUpNumUsersPerInterval the rampUpNumUsersPerInterval to set
	 */
	public void setRampUpNumUsersPerInterval(double rampUpNumUsersPerInterval) {
		this.rampUpNumUsersPerInterval = rampUpNumUsersPerInterval;
	}

	/**
	 * @return the coolDownInterval [seconds]
	 */
	public double getCoolDownInterval() {
		return coolDownInterval;
	}

	/**
	 * @param coolDownInterval the coolDownInterval to set [seconds]
	 */
	public void setCoolDownInterval(double coolDownInterval) {
		this.coolDownInterval = coolDownInterval;
	}

	/**
	 * @return the coolDownNumUsersPerInterval
	 */
	public double getCoolDownNumUsersPerInterval() {
		return coolDownNumUsersPerInterval;
	}

	/**
	 * @param coolDownNumUsersPerInterval the coolDownNumUsersPerInterval to set
	 */
	public void setCoolDownNumUsersPerInterval(double coolDownNumUsersPerInterval) {
		this.coolDownNumUsersPerInterval = coolDownNumUsersPerInterval;
	}

	/**
	 * Gets the file path of the JMeter root folder.
	 * 
	 * @return path to jMeter folder
	 */
	public String getPathToJMeterRootFolder() {
		return pathToJMeterRootFolder;
	}

	/**
	 * Sets the file path of the JMeter root folder (for example "C:\...\apache-jmeter-2.9").
	 * 
	 * @param pathToJMeterRootFolder the path
	 */
	public void setPathToJMeterBinFolder(String pathToJMeterRootFolder) {
		this.pathToJMeterRootFolder = pathToJMeterRootFolder;
	}

	/**
	 * Gets the Path of the JMeter load script
	 * 
	 * @return the path to the load script
	 */
	public String getPathToScript() {
		return pathToScript;
	}

	/**
	 * Sets the Path of the JMeter load script.
	 * 
	 * @param pathToScript the path to the script
	 */
	public void setPathToScript(String pathToScript) {
		this.pathToScript = pathToScript;
	}

	/**
	 * Return a properties object in which additional properties can be set, which also wil lbe passed to jmeter.
	 * 
	 * @return the properties object containing the additional properties.
	 */
	public Properties getAdditionalProperties() {
		return additionalProps;
	}

	/**
	 * @return the pathToDefaultOutputFile
	 */
	public String getDefaultOutputFile() {
		return defaultOutputFile;
	}

	/**
	 * @return the pathToSamplingFile
	 */
	public String getPathToSamplingFile() {
		return pathToSamplingFile;
	}

	/**
	 * @param pathToSamplingFile the pathToSamplingFile to set
	 */
	public void setPathToSamplingFile(String pathToSamplingFile) {
		this.pathToSamplingFile = pathToSamplingFile;
	}

	/**
	 * @return the logFilePrefix
	 */
	public String getLogFilePrefix() {
		return logFilePrefix;
	}

	/**
	 * @param logFilePrefix the logFilePrefix to set
	 */
	public void setLogFilePrefix(String logFilePrefix) {
		this.logFilePrefix = logFilePrefix;
	}

}
