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
import java.util.HashMap;
import java.util.Map;

import org.lpe.common.util.web.LpeHTTPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

/**
 * Main class of the System Monitoring Utility. Starts a grizzly server and
 * initializes the Jersey application.
 * 
 * @author Henning Muszynski
 * 
 */
public final class ServerLauncher {
	private static final int DEFAULT_PORT = 8080;
	private static final String PORT_KEY = "port=";
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLauncher.class);

	private static ServerLauncher instance;

	protected static synchronized ServerLauncher getInstance() {
		if (instance == null) {
			instance = new ServerLauncher();
		}
		return instance;
	}



	private String uri;
	private static Integer port = DEFAULT_PORT;
	private boolean shutdown = false;

	/**
	 * Private constructor due to singleton class.
	 */
	private ServerLauncher() {

	}

	private void start() {
		final String url = getLocalAddress();
		uri = url;
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", "org.lpe.common.remotecontrol;"
				+ "org.lpe.common.remotecontrol.service;" + "org.codehaus.jackson.jaxrs");
		initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

		SelectorThread server = null;

		try {

			server = GrizzlyWebContainerFactory.create(url, initParams);
			LOGGER.info("Started server: {}.", url);
			waitForShutdown();

		} catch (final IllegalArgumentException iae) {
			LOGGER.warn("Illegal Argument Exception happend in main method of ServerLauncher: {}", iae.getMessage());
		} catch (final IOException ioe) {
			LOGGER.warn("IO Exception happend in main method of ServerLauncher: {}", ioe.getMessage());
		} catch (final InterruptedException e) {
			LOGGER.warn("Interrupt received. Stopping server.");
		} finally {
			if (server != null) {
				server.stopEndpoint();
			}
		}
	}

	/**
	 * Opens up a server on the localhost IP address and the port 8090 of the
	 * underlying system.
	 * 
	 * @param args
	 *            not used.
	 */
	public static void main(final String[] args) {
		if (args == null || args.length < 1) {
			LOGGER.error("Remote Control Service Launcher requires exactly two arguments:");
			LOGGER.error("1st argument: start / shutdown");
			System.exit(0);
		}

		parseArgs(args);

		if (args[0].equalsIgnoreCase("start")) {
			getInstance().start();
		} else if (args[0].equalsIgnoreCase("shutdown")) {
			final ServerLauncher launcher = getInstance();
			final WebResource server = LpeHTTPUtils.getWebClient().resource(launcher.getLocalAddress());
			server.path("ShutdownService").path("shutdown").post();
		} else {
			LOGGER.error("Invalid value for 1st argument! Valid values are: start / shutdown");
		}

	}

	/**
	 * Parses the agent arguments.
	 * 
	 * @param agentArgs
	 *            arguments as string
	 */
	private static void parseArgs(final String [] agentArgs) {
		if (agentArgs == null) {
			return;
		}
		for (final String arg : agentArgs) {
			if (arg.startsWith(PORT_KEY)) {
				port = Integer.parseInt(arg.substring(PORT_KEY.length()));
			} 
		}
	}
	
	/**
	 * 
	 * @return returns the URI of the running Monitoring application.
	 */
	public String getUri() {
		return uri;
	}

	private String getLocalAddress() {
		return "http://0.0.0.0:" + port + "/";
	}

	private synchronized void waitForShutdown() throws InterruptedException {
		while (!shutdown) {
			this.wait();
		}
	}

	protected synchronized void shutDown() {
		shutdown = true;
		this.notify();
	}
}
