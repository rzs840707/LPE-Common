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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lpe.common.loadgenerator.config.LGWorkloadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable for execution of the load generator process.
 * 
 * @author Alexander Wert
 * 
 */
public class ScenarioRunner implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioRunner.class);

	private LGWorkloadConfig lrConfig;
	private String newScenarioPath;

	private volatile boolean finished = true;

	/**
	 * Runs the load generator experiment and analysis.
	 */
	public void run() {
		finished = false;

		cleanResultDir(lrConfig.getResultPath());

		runLoadGeneratorExperiment(lrConfig.getResultPath());

		finished = true;
	}

	private void cleanResultDir(String resultDir) {
		try {
			File file = new File(resultDir);
			if (file.exists()) {
				FileUtils.cleanDirectory(new File(resultDir));
			}
		} catch (IOException e) {
			LOGGER.error("Cannot clean result directory: {} Cause: {}", resultDir, e.getMessage());
			throw new RuntimeException("Cannot clean result directory!");
		}
	}

	private void runLoadGeneratorExperiment(String resultDir) {
		LOGGER.debug("Executing load generator scenario" + newScenarioPath + " with " + lrConfig.getNumUsers()
				+ " users...");
		String command = "\"" + lrConfig.getLoadGeneratorPath() + "\"" + " -Run" + " -TestPath " + "\"" + newScenarioPath
				+ "\"" + " -ResultName " + "\"" + resultDir + "\"";
		try {
			Process pr = Runtime.getRuntime().exec(command);
			IOUtils.copy(pr.getInputStream(), System.out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.debug("load generator scenario finished!");
	}

	/**
	 * @return the finished
	 */
	public synchronized boolean isFinished() {
		return finished;
	}

	/**
	 * @return the lrConfig
	 */
	public LGWorkloadConfig getLrConfig() {
		return lrConfig;
	}

	/**
	 * @param lrConfig
	 *            the lrConfig to set
	 */
	public void setLrConfig(LGWorkloadConfig lrConfig) {
		this.lrConfig = lrConfig;
	}

	/**
	 * @return the newScenarioPath
	 */
	public String getNewScenarioPath() {
		return newScenarioPath;
	}

	/**
	 * @param newScenarioPath
	 *            the newScenarioPath to set
	 */
	public void setNewScenarioPath(String newScenarioPath) {
		this.newScenarioPath = newScenarioPath;
	}

}
