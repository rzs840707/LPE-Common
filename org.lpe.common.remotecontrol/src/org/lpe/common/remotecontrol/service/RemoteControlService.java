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
package org.lpe.common.remotecontrol.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.lpe.common.remotecontrol.data.FileContainer;
import org.lpe.common.remotecontrol.exceptions.RemoteControlException;
import org.lpe.common.util.LpeStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

/**
 * The server of the remote control service offering RESTful web services to the
 * client.
 * 
 * @author Christoph Heger
 */
@Path("remotecontrol")
@Singleton
public class RemoteControlService {

	private static final int BUFFER_SIZE = 4096;
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteControlService.class);
	private FileReader fileReader;

	/**
	 * Executes a command on the remote controlled system.
	 * 
	 * @param command
	 *            command to execute (e.g. full qualified file name of shell
	 *            script)
	 * @throws RemoteControlException
	 *             if command cannot be executed
	 */
	@POST
	@Path("execute")
	@Consumes(MediaType.APPLICATION_JSON)
	public void execute(String command) throws RemoteControlException {

		// switch (commandType) {
		// case SHELL_SCRIPT:
		executeShellScript(command);
		// break;
		// default:
		// break;
		// }
	}

	/**
	 * 
	 * @param fileName
	 *            full qualified file name of config file to read
	 * @return content of config file
	 * @throws RemoteControlException
	 *             if config file cannot be read
	 */
	@POST
	@Path("getConfigFile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FileContainer getConfigFile(String fileName) throws RemoteControlException {
		return readConfigFile(fileName);
	}

	private String fileToRead;

	/**
	 * Simply returns all gathered information from monitoring application.
	 * Returns them in a wrapper object for full JSON support.
	 * 
	 * @return all results from all recorders
	 */
	@GET
	@Path("streamFile")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response streamFile() {
		final String fileName = fileToRead;
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) {

				try {
					InputStream in = new FileInputStream(fileName);
					LpeStreamUtils.pipe(in, os, BUFFER_SIZE);
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("Failed reading config file!", e);
				}

			}
		};
		Response resp = Response.ok(stream).build();
		return resp;
	}

	/**
	 * 
	 * @param fileContainer
	 *            the {@link FileContainer} containing the file's full qualified
	 *            path and content to write
	 * @throws RemoteControlException
	 *             if config file cannot be read
	 */
	@POST
	@Path("writeFile")
	@Consumes(MediaType.APPLICATION_JSON)
	public void writeFile(FileContainer fileContainer) throws RemoteControlException {
		writeConfigFile(fileContainer);
	}

	/**
	 * 
	 * @param filename
	 *            path to the file to read
	 * @throws RemoteControlException
	 *             if config file cannot be read
	 */
	@POST
	@Path("setFileToStream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setFileToStream(String filename) throws RemoteControlException {
		this.fileToRead = filename;
	}

	/**
	 * 
	 * @return true if experiment has been finished
	 */
	@GET
	@Path("currentTime")
	@Produces(MediaType.APPLICATION_JSON)
	public long getCurrentTime() {
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

	private void executeShellScript(String fileName) throws RemoteControlException {
		checkFile(fileName);

		ProcessBuilder processBuilder = new ProcessBuilder(fileName);

		try {
			Process shellScript = processBuilder.start();
			int exitValue = shellScript.waitFor();
			if (exitValue != 0) {
				LOGGER.error("Shell script {} exited with exit value '{}'!", fileName, exitValue);
				throw new RemoteControlException("Shell script " + fileName + " exited with exit value '" + exitValue
						+ "'!");
			}
		} catch (IOException e) {
			LOGGER.error("Unable to execute shell script {}! Cause: {}", fileName, e.getMessage());
			throw new RemoteControlException("Unable to execute shell script" + fileName + "!", e);
		} catch (InterruptedException e) {
			LOGGER.error("Wait for shell script {} to finish interrupted! Cause: {}", fileName, e.getMessage());
			throw new RemoteControlException("Wait for shell script " + fileName + " to finish interrupted!", e);
		}
	}

	private void checkFile(String fileName) {
		File script = new File(fileName);

		if (!script.exists()) {
			LOGGER.error("Cannot find file {}", fileName);
			throw new IllegalArgumentException("Cannot find file " + fileName);
		}

		if (!script.isFile()) {
			LOGGER.error("{} is not a file", fileName);
			throw new IllegalArgumentException(fileName + " is not a file");
		}

		if (!script.canExecute()) {
			LOGGER.error("Cannot execute {}", fileName);
			throw new IllegalArgumentException("Canno execute " + fileName);
		}
	}

	private FileContainer readConfigFile(String fileName) throws RemoteControlException {
		FileContainer fContainer = new FileContainer();
		fContainer.setFileName(fileName);
		try {
			fileReader = new FileReader(fileName);

			StringBuffer sBuffer = new StringBuffer();
			BufferedReader bReader = new BufferedReader(fileReader);

			String line = bReader.readLine();

			while (line != null) {
				sBuffer.append(line);
				line = bReader.readLine();
				if (line != null) {
					sBuffer.append("\r\n");
				}
			}

			fContainer.setFileContent(sBuffer.toString());

			return fContainer;
		} catch (IOException e) {
			throw new RemoteControlException("Failed reading config file!", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new RemoteControlException("Error while closing file reader for file " + fileName, e);
				}
			}

		}
	}

	private void writeConfigFile(FileContainer fileContainer) throws RemoteControlException {
		String fileName = fileContainer.getFileName();
		String fileContent = fileContainer.getFileContent();

		if (!fileName.endsWith(System.getProperty("file.separator"))) {
			fileName = fileName + "/";
		}

		LOGGER.debug("Writing file content to file: {}", fileName);

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(fileName);
			fileWriter.append(fileContent);

			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new RemoteControlException("Cannot write content to file " + fileName, e);
		}
	}

}
