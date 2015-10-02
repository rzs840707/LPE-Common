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
package org.lpe.common.util.web;

import java.util.Collection;

import org.glassfish.grizzly.http.server.HttpServer;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.WebResource;

/**
 * Wraps a Web server.
 * 
 * @author C5170547
 * 
 */
public final class WebServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
	private static final String SHUTDOWN_PACKAGE = "org.lpe.common.util.web";
	private static final int SHUTDOWN_DELAY = 3000;
	private static final String GENERIC_IP = "0.0.0.0";
	private static WebServer instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static synchronized WebServer getInstance() {
		if (instance == null) {
			instance = new WebServer();
		}
		return instance;
	}

	private HttpServer server;
	private final Object shutdownMonitor = new Object();
	private boolean shutdown = false;

	/**
	 * private constructor.
	 */
	private WebServer() {

	}

	/**
	 * Starts a new WebServer!
	 * 
	 * @param port
	 *            port to bind to
	 * @param basePath
	 *            base URL path after 'http://HOST:PORT/'
	 * @param servicePackages
	 *            packages to look for Jersey services
	 */
	public void start(final int port, final String basePath, final Collection<String> servicePackages) {
		start(GENERIC_IP, port, basePath, servicePackages);

	}

	/**
	 * Starts a new WebServer!
	 * 
	 * @param port
	 *            port to bind to
	 * @param basePath
	 *            base URL path after 'http://HOST:PORT/'
	 * @param servicePackages
	 *            packages to look for Jersey services
	 * @param minNumWorker
	 *            initial thread pool size
	 * @param maxNumWorker
	 *            maximal thread pool size
	 */
	public void start(final int port, final String basePath, final Collection<String> servicePackages, final Integer minNumWorker,
			final Integer maxNumWorker) {
		start(GENERIC_IP, port, basePath, servicePackages, minNumWorker, maxNumWorker);

	}

	/**
	 * Starts a new WebServer!
	 * 
	 * @param host
	 *            host to bind to
	 * @param port
	 *            port to bind to
	 * @param basePath
	 *            base URL path after 'http://HOST:PORT/'
	 * @param servicePackages
	 *            packages to look for Jersey services
	 */
	public void start(final String host, final int port, final String basePath, final Collection<String> servicePackages) {
		start(GENERIC_IP, port, basePath, servicePackages, null, null);
	}

	/**
	 * Starts a new WebServer!
	 * 
	 * @param host
	 *            host to bind to
	 * @param port
	 *            port to bind to
	 * @param basePath
	 *            base URL path after 'http://HOST:PORT/'
	 * @param servicePackages
	 *            packages to look for Jersey services
	 * @param minNumWorker
	 *            initial thread pool size
	 * @param maxNumWorker
	 *            maximal thread pool size
	 */
	public void start(final String host, final int port, final String basePath, final Collection<String> servicePackages, final Integer minNumWorker,
			final Integer maxNumWorker) {
		if (server != null) {
			shutdown();
		}
		shutdown = false;
		servicePackages.add(SHUTDOWN_PACKAGE);
		server = LpeWebUtils.startHttpServer(host, port, basePath, servicePackages.toArray(new String[0]),
				minNumWorker, maxNumWorker);
		LOGGER.info("Web-Server started on port {}", port);
		// wait for shutdown thread
		LpeSystemUtils.submitTask(new Runnable() {

			@Override
			public void run() {
				synchronized (shutdownMonitor) {
					while (!shutdown) {
						try {
							shutdownMonitor.wait();
						} catch (final InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				}
				try {
					Thread.sleep(SHUTDOWN_DELAY);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
				server.shutdown();
				LOGGER.info("Web-Server terminated!");
				System.exit(0);
			}
		});
	}

	/**
	 * Shuts down current server.
	 */
	public void shutdown() {
		synchronized (shutdownMonitor) {
			shutdown = true;
			shutdownMonitor.notifyAll();
		}

	}

	/**
	 * Triggers shutdown of the Web server which is bound to given port.
	 * 
	 * @param port
	 *            port of the server
	 * @param basePath
	 *            basePath of the server
	 */
	public static void triggerServerShutdown(final int port, final String basePath) {
		triggerServerShutdown("localhost", port, basePath);
	}

	/**
	 * Triggers shutdown of the Web server which is bound to given host and
	 * port.
	 * 
	 * @param host
	 *            host of the server
	 * @param port
	 *            port of the server
	 * @param basePath
	 *            basePath of the server
	 */
	public static void triggerServerShutdown(final String host, final int port, final String basePath) {
		final WebResource server = LpeHTTPUtils.getWebClient().resource(
				"http://" + host + ":" + String.valueOf(port) + "/" + basePath);
		server.path("ShutdownService").path("shutdown").post();
	}
}
