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
package org.lpe.common.remotecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.ws.rs.core.MediaType;

import org.lpe.common.remotecontrol.data.FileContainer;
import org.lpe.common.util.web.LpeWebUtils;

import com.sun.jersey.api.client.WebResource;

/**
 * Client for communication with the remote control service.
 * 
 * @author Christoph Heger
 * 
 */
public class RemoteControlClient {

	private static final String REST = "remotecontrol";
	private static final String TEST_CONNECTION = "testConnection";
	private static final String CURRENT_TIME = "currentTime";
	private static final String EXECUTE_SHELL_SCRIPT = "execute";
	private static final String GET_CONFIG_FILE = "getConfigFile";
	private static final String WRITE_FILE = "writeFile";
	private static final String STREAM_FILE = "streamFile";
	private static final String SET_FILE_TO_STREAM = "setFileToStream";

	private String url;
	private WebResource service;

	/**
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public RemoteControlClient(String host, String port) {
		url = "http://" + host + ":" + port;
		service = LpeWebUtils.getWebClient().resource(url);
	}

	/**
	 * 
	 * @return current local time of the remote control service machine
	 */
	public long getCurrentTime() {
		return service.path(REST).path(CURRENT_TIME).accept(MediaType.APPLICATION_JSON).get(long.class);
	}

	/**
	 * Executes a given shell script on the remote controlled machine.
	 * 
	 * @param fileName
	 *            full qualified file name of shell script to execute
	 */
	public void executeShellScript(String fileName) {

		service.path(REST).path(EXECUTE_SHELL_SCRIPT).type(MediaType.APPLICATION_JSON).post(fileName);
	}

	/**
	 * Writes the given content the to given file specified in the provided
	 * {@link FileContainer} on the remote system.
	 * 
	 * @param fileContainer
	 *            {@link FileContainer} containing full qualified file name and
	 *            content to write
	 */
	public void writeFile(FileContainer fileContainer) {

		service.path(REST).path(WRITE_FILE).type(MediaType.APPLICATION_JSON).post(fileContainer);
	}

	/**
	 * Reads the content of the specified file on the remote system.
	 * 
	 * @param fileName
	 *            full qualified path of file to return
	 * @return {@link FileContainer} with content of file
	 */
	public FileContainer readFile(String fileName) {

		return service.path(REST).path(GET_CONFIG_FILE).type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(FileContainer.class, fileName);
	}
	
	/**
	 * Returns input stream of a returned file.
	 * @param filename file to retrieve
	 * @return inputstream
	 */
	public InputStream readFileStreamed(String filename) {
		service.path(REST).path(SET_FILE_TO_STREAM).type(MediaType.APPLICATION_JSON).post(filename);		
		try {
			HttpURLConnection connection = LpeWebUtils.get(url + "/" + REST + "/" + STREAM_FILE);
			return connection.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}

	/**
	 * 
	 * @return true if connecting to service possible
	 */
	public boolean testConnection() {
		return service.path(REST).path(TEST_CONNECTION).accept(MediaType.APPLICATION_JSON).get(Boolean.class);
	}
}
