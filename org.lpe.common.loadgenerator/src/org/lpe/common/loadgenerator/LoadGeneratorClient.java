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
import java.io.OutputStream;

import javax.ws.rs.core.MediaType;

import org.lpe.common.loadgenerator.config.LGMeasurementConfig;
import org.lpe.common.loadgenerator.config.LGWorkloadConfig;
import org.lpe.common.loadgenerator.data.LGMeasurementData;
import org.lpe.common.util.LpeStreamUtils;
import org.lpe.common.util.web.LpeHTTPUtils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for communication with the load generator service.
 * 
 * @author Alexander Wert
 * 
 */
public class LoadGeneratorClient {

	private static final String REST = "loadgenerator";
	private static final String TEST_CONNECTION = "testConnection";
	private static final String GET_DATA = "getData";
	private static final String CURRENT_TIME = "currentTime";
	private static final String START = "startLoad";
	private static final String IS_FINISHED = "isLoadFinished";
	private static final String GET_REPORT = "getReport";

	private static final long DEFAULT_POLLING_DELAY = 1000; // [ms]

	private final String url;
	private final String host;
	private final String port;
	private final WebResource webResource;

	/**
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public LoadGeneratorClient(final String host, final String port) {
		this.host = host;
		this.port = port;
		url = "http://" + host + ":" + port;
		webResource = LpeHTTPUtils.getWebClient().resource(url);
	}

	/**
	 * @param lrmConfig
	 *            load generator measurement configuration, describing the place
	 *            where to search for measurement data
	 * @return collected measurement data
	 * 
	 */
	public LGMeasurementData getMeasurementData(final LGMeasurementConfig lrmConfig) {

		return webResource.path(REST).path(GET_DATA).type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(LGMeasurementData.class, lrmConfig);

	}

	/**
	 * 
	 * @return current local time of the load generator service machine
	 */
	public long getCurrentTime() {
		return webResource.path(REST).path(CURRENT_TIME).accept(MediaType.APPLICATION_JSON).get(long.class);
	}

	/**
	 * starts the workload generation.
	 * 
	 * @param lrConfig
	 *            configuration describing the workload characteristics
	 */
	public void startLoad(final LGWorkloadConfig lrConfig) {

		webResource.path(REST).path(START).type(MediaType.APPLICATION_JSON).post(lrConfig);
	}

	/**
	 * waits until load has finished.
	 */
	public void waitForFinishedLoad() {
		boolean isFinished = false;

		while (!isFinished) {
			isFinished = webResource.path(REST).path(IS_FINISHED).accept(MediaType.APPLICATION_JSON).get(boolean.class);
			try {
				Thread.sleep(DEFAULT_POLLING_DELAY);
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 
	 * @return true if connecting to service possible
	 */
	public boolean testConnection() {
		return testConnection(host, port);
	}

	/**
	 * Test connection to the load generator satellite.
	 * 
	 * @param host
	 *            host name of the satellite
	 * @param port
	 *            port of the satellite
	 * @return true if connection could be established
	 */
	public static boolean testConnection(final String host, final String port) {
		final String path = REST + "/" + TEST_CONNECTION;
		return LpeHTTPUtils.testConnection(host, port, path);
	}

	/**
	 * 
	 * @param oStream
	 *            stream where to pipe to
	 * @param lrmConfig
	 *            configuration
	 * @throws MeasurementException
	 *             thrown if streaming fails
	 */
	public void pipeReportToOutputStream(final OutputStream oStream, final LGMeasurementConfig lrmConfig) throws IOException {
		final ClientResponse response = webResource.path(REST).path(GET_REPORT).type(MediaType.APPLICATION_JSON)
				.accept("application/zip").post(ClientResponse.class, lrmConfig);

		LpeStreamUtils.pipe(response.getEntityInputStream(), oStream);

	}
}
