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

import org.lpe.common.util.LpeStringUtils;

/**
 * Configuration for the measurement service of load generator.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class LGMeasurementConfig {
	private String sessionName = "NA";
	private String resultDir = "NA";
	private String analysisPath = "NA";
	private String analysisTemplate = "NA";
	
	/**
	 * @return the sessionName
	 */
	public String getSessionName() {
		return sessionName;
	}

	/**
	 * @param sessionName
	 *            the sessionName to set
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	/**
	 * @return the resultDir
	 */
	public String getResultDir() {
		return resultDir;
	}

	/**
	 * @return the analysisPath
	 */
	public String getAnalysisPath() {
		return analysisPath;
	}

	/**
	 * @param analysisPath
	 *            the analysisPath to set
	 */
	public void setAnalysisPath(String analysisPath) {
		analysisPath = LpeStringUtils.correctFileSeparator(analysisPath);
		this.analysisPath = analysisPath;
	}

	/**
	 * @return the analysisTemplate
	 */
	public String getAnalysisTemplate() {
		return analysisTemplate;
	}

	/**
	 * @param analysisTemplate
	 *            the analysisTemplate to set
	 */
	public void setAnalysisTemplate(String analysisTemplate) {
		this.analysisTemplate = analysisTemplate;
	}
	
	/**
	 * @param resultDir
	 *            the resultDir to set
	 */
	public void setResultDir(String resultDir) {
		resultDir = LpeStringUtils.correctFileSeparator(resultDir);
		if (resultDir.endsWith(System.getProperty("file.separator"))) {
			resultDir = resultDir.substring(0, resultDir.length() - 1);
		}
		this.resultDir = resultDir;
	}
	
	/**
	 * corrects all paths to OS specific representation.
	 */
	public void correctPathSeparators() {
		resultDir = LpeStringUtils.correctFileSeparator(resultDir);
		if (resultDir.endsWith(System.getProperty("file.separator"))) {
			resultDir = resultDir.substring(0, resultDir.length() - 1);
		}
		analysisPath = LpeStringUtils.correctFileSeparator(analysisPath);

	}
}
