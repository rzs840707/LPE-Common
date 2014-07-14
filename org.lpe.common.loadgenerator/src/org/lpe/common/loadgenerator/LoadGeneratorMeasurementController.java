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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.apache.commons.io.IOUtils;
import org.lpe.common.loadgenerator.config.LGMeasurementConfig;
import org.lpe.common.util.LpeFileUtils;
import org.lpe.common.util.LpeStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * The controller to retrieve the load generator results.
 * 
 * @author Le-Huan Stefan Tran
 */
public final class LoadGeneratorMeasurementController {
	private static final double KILO = 1000.0;
	private static final String LR_RESULT_FILE_EXTENSION = ".lrr";
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadGeneratorMeasurementController.class);

	private static final String ACCESS_DB_FILE_EXTENSION = ".mdb";

	private static final String ZIP_FILE_EXTENSION = ".zip";

	private static final String TABLE_EVENT_MAP = "Event_map";
	private static final String CELL_TRANSACTION = "Transaction";
	private static final String COLUMN_EVENT_TYPE = "Event Type";
	private static final String COLUMN_EVENT_ID = "Event ID";
	private static final String COLUMN_EVENT_NAME = "Event Name";

	private static final String TABLE_EVENT_METER = "Event_meter";
	private static final String COLUMN_VALUE = "Value";
	private static final String COLUMN_END_TIME = "End Time";

	private static final String DIR_REPORT = "report";

	private static LoadGeneratorMeasurementController instance;

	private boolean analysisFinished = false;

	/**
	 * 
	 * @return singleton instance
	 */
	public static LoadGeneratorMeasurementController getInstance() {
		if (instance == null) {
			instance = new LoadGeneratorMeasurementController();
		}
		return instance;
	}

	private LoadGeneratorMeasurementController() {
	}

	/**
	 * 
	 * @param lrmConfig
	 *            configuration of data source where to get the measurement data
	 *            from
	 * @return measurement data collected by load generator
	 * @throws IOException
	 *             if retrieving data fails
	 */
	public MeasurementData getMeasurementData(LGMeasurementConfig lrmConfig) throws IOException {
		LOGGER.debug("Fetching measurement data from load generator Measurement...");

		if (!isAnalysisFinished()) {
			runLoadGeneratorAnalysis(lrmConfig);
		}

		String databasePath = lrmConfig.getResultDir() + System.getProperty("file.separator")
				+ lrmConfig.getSessionName() + System.getProperty("file.separator") + lrmConfig.getSessionName()
				+ ACCESS_DB_FILE_EXTENSION;

		List<AbstractRecord> responseTimes = new LinkedList<AbstractRecord>();

		// Open database connection
		Database db = Database.open(new File(databasePath));

		HashMap<String, String> transactionNames = getTransactionIdsAndNames(db);

		getResponseTimes(responseTimes, db, transactionNames);

		// Close database connection
		db.close();

		LOGGER.debug("Measurement data from load generator Measurement fetched!");

		MeasurementData measurementData = new MeasurementData();
		measurementData.setRecords(responseTimes);
		return measurementData;
	}

	private void runLoadGeneratorAnalysis(LGMeasurementConfig lrmConfig) {

		LOGGER.debug("Executing load generator analysis...");
		String resultDir = lrmConfig.getResultDir();
		String resultFileName = lrmConfig.getResultDir().substring(
				lrmConfig.getResultDir().lastIndexOf(System.getProperty("file.separator")) + 1)
				+ LR_RESULT_FILE_EXTENSION;
		String command1 = "\"" + lrmConfig.getAnalysisPath() + "\"" + " -RESULTPATH " + "\"" + resultDir
				+ System.getProperty("file.separator") + resultFileName + "\"" + " -TEMPLATENAME " + "\""
				+ lrmConfig.getAnalysisTemplate() + "\"";

		try {
			Process pr = Runtime.getRuntime().exec(command1);
			IOUtils.copy(pr.getInputStream(), System.out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.debug("load generator analysis finished!");
		setAnalysisFinished(true);
	}

	private void getResponseTimes(List<AbstractRecord> responseTimes, Database db,
			HashMap<String, String> transactionNames) throws IOException {
		// Get response times and timestamp of transactions in ms
		Table eventMeterTable = db.getTable(TABLE_EVENT_METER);
		Set<String> transactionIDs = transactionNames.keySet();

		for (Map<String, Object> row : eventMeterTable) {
			String transactionID = row.get(COLUMN_EVENT_ID).toString();

			if (transactionIDs.contains(transactionID)) {
				String operation = transactionNames.get(transactionID);

				// endTime in ms is relative to the scenarioStartTime
				// endTime is used as the timestamp
				long endTime = (long) (new Double(row.get(COLUMN_END_TIME).toString()) * KILO);
				long responseTime = (long) (new Double(row.get(COLUMN_VALUE).toString()) * KILO);

				// Add record
				responseTimes.add(new ResponseTimeRecord(endTime, operation, responseTime));
			}
		}
	}

	private HashMap<String, String> getTransactionIdsAndNames(Database db) throws IOException {
		// Get ID and name of transactions
		Table eventMapTable = db.getTable(TABLE_EVENT_MAP);
		HashMap<String, String> transactionNames = new HashMap<String, String>();

		for (Map<String, Object> row : eventMapTable) {
			if (CELL_TRANSACTION.equals(row.get(COLUMN_EVENT_TYPE))) {
				String transactionID = row.get(COLUMN_EVENT_ID).toString();
				String transactionName = row.get(COLUMN_EVENT_NAME).toString();
				transactionNames.put(transactionID, transactionName);
			}
		}
		return transactionNames;
	}

	private FileInputStream getMeasurementReport(LGMeasurementConfig lrmConfig) throws IOException {
		LOGGER.debug("Packing result data from load generator Measurement...");

		if (!isAnalysisFinished()) {
			runLoadGeneratorAnalysis(lrmConfig);
		}

		String resultDir = lrmConfig.getResultDir() + System.getProperty("file.separator") + DIR_REPORT;

		String zipFile = lrmConfig.getResultDir() + System.getProperty("file.separator") + DIR_REPORT
				+ ZIP_FILE_EXTENSION;

		LpeFileUtils.zip(resultDir, zipFile);

		LOGGER.debug("Result data from load generator Measurement packed!");

		FileInputStream fis = new FileInputStream(zipFile);

		return fis;
	}

	/**
	 * 
	 * @param lrmConfig
	 *            configuration of data source where to get the measurement data
	 *            from
	 * @param oStream
	 *            stream where to pipe to
	 * @throws MeasurementException
	 *             thrown if streaming fails
	 * @throws IOException
	 */
	public void pipeReportToOutputStream(LGMeasurementConfig lrmConfig, OutputStream oStream)
			throws MeasurementException, IOException {
		FileInputStream fileInputStream = getMeasurementReport(lrmConfig);
		LpeStreamUtils.pipe(fileInputStream, oStream);
		fileInputStream.close();
	}

	/**
	 * @return the analysisFinished
	 */
	protected boolean isAnalysisFinished() {
		return analysisFinished;
	}

	/**
	 * @param analysisFinished
	 *            the analysisFinished to set
	 */
	protected void setAnalysisFinished(boolean analysisFinished) {
		this.analysisFinished = analysisFinished;
	}
}
