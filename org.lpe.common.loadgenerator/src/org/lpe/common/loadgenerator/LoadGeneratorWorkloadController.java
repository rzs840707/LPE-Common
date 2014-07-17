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
package org.lpe.common.loadgenerator;

import java.io.IOException;

import org.lpe.common.loadgenerator.config.LGWorkloadConfig;
import org.lpe.common.loadgenerator.scenario.ScenarioModifier;
import org.lpe.common.loadgenerator.scenario.ScenarioRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The controller to drive the load generator application.
 * 
 * @author Le-Huan Stefan Tran
 */
public final class LoadGeneratorWorkloadController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadGeneratorWorkloadController.class);

	private static LoadGeneratorWorkloadController instance;
	private ScenarioModifier scenarioModifier;
	private ScenarioRunner scenarioRunner;

	/**
	 * 
	 * @return singleton instance
	 */
	public static LoadGeneratorWorkloadController getInstance() {
		if (instance == null) {
			instance = new LoadGeneratorWorkloadController();
		}
		return instance;
	}

	private LoadGeneratorWorkloadController() {
		scenarioModifier = new ScenarioModifier();
		scenarioRunner = new ScenarioRunner();
	}

	/**
	 * Starts a load generator experiment.
	 * 
	 * @param lrConfig
	 *            experiment configuration
	 * @throws IOException
	 *             if experiment fails
	 */
	public void startExperiment(final LGWorkloadConfig lrConfig) throws IOException {
		LoadGeneratorMeasurementController.getInstance().setAnalysisFinished(false);
		LOGGER.info("Modifying scenario...");
		final String newScenarioPath = scenarioModifier.modifyScenario(lrConfig);

		LOGGER.info("Modifying finished!");

		if (scenarioRunner.isFinished()) {
			scenarioRunner.setLrConfig(lrConfig);
			scenarioRunner.setNewScenarioPath(newScenarioPath);

			// Execute load generator scenario asynchronously
			Thread thread = new Thread(scenarioRunner);
			thread.start();
		} else {
			LOGGER.debug("load generator is not yet finished - wait for completion!");
		}
	}

	/**
	 * 
	 * @return true, if experiment has been finished
	 */
	public boolean isFinished() {
		return scenarioRunner.isFinished();
	}
}
