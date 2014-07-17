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
package org.lpe.common.jmeter.config;

import java.util.Properties;

/**
 * @author D061588
 * 
 *         configuration Element for JMeter loadrun
 */
public class JMeterWorkloadConfig {

	private static final int TEN_SECONDS = 10000;

	private boolean createLog;

	private int durationSeconds;
	private double rampUpTimeSecondsPerUser;
	private double coolDownTimeSecondsPerUser;

	private int numUsers;

	private int thinkTimeMinimumMS;
	private int thinkTimeMaximumMS;

	private String pathToJMeterBinFolder;
	private String pathToScript;

	Properties additionalProps;

	/**
	 * Constructor.
	 */
	public JMeterWorkloadConfig() {
		createLog = false;
		durationSeconds = 1;
		numUsers = 1;
		rampUpTimeSecondsPerUser = 1;

		thinkTimeMinimumMS = TEN_SECONDS;
		thinkTimeMaximumMS = TEN_SECONDS;

		additionalProps = new Properties();
	}

	/**
	 * checks if the JMeter-log should be generated and parsed.
	 * 
	 * @return the flag
	 */
	public boolean getCreateLogFlag() {
		return createLog;
	}

	/**
	 * Sets the flag which determines if the JMeter-log should be generated and
	 * parsed.
	 * 
	 * @param createLog
	 *            true if log should pe created/parsed
	 */
	public void setCreateLogFlag(boolean createLog) {
		this.createLog = createLog;
	}

	/**
	 * Gets the upper limit for ThinkTimes in the script (actual think time is a
	 * random number between minimum and maximum).
	 * 
	 * @return the maximum in MS
	 */
	public int getThinkTimeMaximumMS() {
		return thinkTimeMaximumMS;
	}

	/**
	 * Sets the upper limit for ThinkTimes in the script (actual think time is a
	 * random number between minimum and maximum).
	 * 
	 * @param thinkTimeMaximumMS
	 *            the maximum in MS
	 */
	public void setThinkTimeMaximumMS(int thinkTimeMaximumMS) {
		this.thinkTimeMaximumMS = thinkTimeMaximumMS;
	}

	/**
	 * Gets the lower limit for ThinkTimes in the script (actual think time is a
	 * random number between minimum and maximum).
	 * 
	 * @return the minimum in MS
	 */
	public int getThinkTimeMinimumMS() {
		return thinkTimeMinimumMS;
	}

	/**
	 * Sets the lower limit for ThinkTimes in the script (actual think time is a
	 * random number between minimum and maximum).
	 * 
	 * @param thinkTimeMinimumMS
	 *            the minimum in MS
	 */
	public void setThinkTimeMinimumMS(int thinkTimeMinimumMS) {
		this.thinkTimeMinimumMS = thinkTimeMinimumMS;
	}

	/**
	 * Get the test duration in seconds.
	 * 
	 * @return duration in seconds
	 */
	public int getDurationSeconds() {
		return durationSeconds;
	}

	/**
	 * Set the test duration in seconds.
	 * 
	 * @param durationSeconds
	 *            duration in seconds
	 */
	public void setDurationSeconds(int durationSeconds) {
		this.durationSeconds = durationSeconds;
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
	 * @param numUsers
	 *            the number of users
	 */
	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	/**
	 * Gets the duration to wait between user starts.
	 * 
	 * @return time in seconds per user
	 */
	public double getRampUpTimeSecondsPerUser() {
		return rampUpTimeSecondsPerUser;
	}

	/**
	 * Sets the duration to wait between user starts.
	 * 
	 * @param rampUpTimeSecondsPerUser
	 *            time in seconds per user
	 */
	public void setRampUpTimeSecondsPerUser(double rampUpTimeSecondsPerUser) {
		this.rampUpTimeSecondsPerUser = rampUpTimeSecondsPerUser;
	}

	/**
	 * Gets the duration to wait between user shutdowns.
	 * 
	 * @return time in seconds per user
	 */
	public double getCoolDownTimeSecondsPerUser() {
		return coolDownTimeSecondsPerUser;
	}

	/**
	 * Sets the duration to wait between user shutdowns.
	 * 
	 * @param coolDownTimeSecondsPerUser
	 *            time in seconds per user
	 */
	public void setCoolDownTimeSecondsPerUser(double coolDownTimeSecondsPerUser) {
		this.coolDownTimeSecondsPerUser = coolDownTimeSecondsPerUser;
	}

	/**
	 * Gets the file path of the JMeter bin folder.
	 * 
	 * @return path to jMeter folder
	 */
	public String getPathToJMeterBinFolder() {
		return pathToJMeterBinFolder;
	}

	/**
	 * Sets the file path of the JMeter bin folder (for example
	 * "C:\...\apache-jmeter-2.9\bin").
	 * 
	 * @param pathToJMeterBinFolder
	 *            the path
	 */
	public void setPathToJMeterBinFolder(String pathToJMeterBinFolder) {
		this.pathToJMeterBinFolder = pathToJMeterBinFolder;
	}

	/**
	 * Gets the Path of the JMeter-script to run.
	 * 
	 * @return the path to the file
	 */
	public String getPathToScript() {
		return pathToScript;
	}

	/**
	 * Sets the Path of the JMeter-script to run.
	 * 
	 * @param pathToScript
	 *            The path to the script
	 */
	public void setPathToScript(String pathToScript) {
		this.pathToScript = pathToScript;
	}

	/**
	 * Return a properties object in which additional properties can be set,
	 * which also wil lbe passed to jmeter.
	 * 
	 * @return the properties object containing the additional properties.
	 */
	public Properties getAdditionalProperties() {
		return additionalProps;
	}

}
