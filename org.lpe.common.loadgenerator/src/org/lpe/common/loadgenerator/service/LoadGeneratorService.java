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
package org.lpe.common.loadgenerator.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.lpe.common.aim.api.measurement.MeasurementData;
import org.lpe.common.loadgenerator.LoadGeneratorMeasurementController;
import org.lpe.common.loadgenerator.LoadGeneratorWorkloadController;
import org.lpe.common.loadgenerator.config.LGMeasurementConfig;
import org.lpe.common.loadgenerator.config.LGWorkloadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

/**
 * The server of load generator offering RESTful web services to the
 * client.
 * 
 * @author Le-Huan Stefan Tran
 */
@Path("loadgenerator")
@Singleton
public class LoadGeneratorService {
	private static Logger LOGGER = LoggerFactory.getLogger(LoadGeneratorService.class);

	/**
	 * Starts a load generator experiment.
	 * 
	 * @param lrConfig
	 *            experiment configuration
	 * @throws IOException
	 *             thrown if experiment fails
	 */
	@POST
	@Path("startLoad")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startLoad(LGWorkloadConfig lrConfig) throws IOException {
		lrConfig.correctPathSeparators();
		LoadGeneratorWorkloadController.getInstance().startExperiment(lrConfig);
	}

	/**
	 * 
	 * @return true if experiment has been finished
	 */
	@GET
	@Path("isLoadFinished")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isLoadFinished() {
		return LoadGeneratorWorkloadController.getInstance().isFinished();
	}

	/**
	 * 
	 * @return true if experiment has been finished
	 */
	@GET
	@Path("currentTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long getCurrentTime() {
		LOGGER.info("CURRENT TIME CALLED!");
		return System.currentTimeMillis();
	}

	/**
	 * 
	 * @return true if connection established
	 */
	@GET
	@Path("testConnection")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean testConnection() {
		return true;
	}

	/**
	 * 
	 * @param lrmConfig
	 *            data source configuration
	 * @return data collected by load generator
	 * @throws IOException
	 *             if data cannot be retrieved
	 */
	@POST
	@Path("getData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MeasurementData getData(LGMeasurementConfig lrmConfig) throws IOException {
		lrmConfig.correctPathSeparators();
		return LoadGeneratorMeasurementController.getInstance().getMeasurementData(lrmConfig);
	}

	/**
	 * 
	 * @param lrmConfig
	 *            data source configuration
	 * @return data collected by load generator
	 */
	@POST
	@Path("getReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public StreamingOutput getReport(LGMeasurementConfig lrmConfig) {
		lrmConfig.correctPathSeparators();
		final LGMeasurementConfig finalConfig = lrmConfig;
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) {
				try {
					LoadGeneratorMeasurementController.getInstance().pipeReportToOutputStream(finalConfig, os);
				} catch (Exception e) {
					throw new RuntimeException();
				}
			}
		};
		return stream;

	}
}
