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

import com.sun.jersey.api.client.WebResource;

/**
 * Wraps a Web server.
 * 
 * @author C5170547
 * 
 */
public final class WebServer {
	private static final String SHUTDOWN_PACKAGE = "org.lpe.common.util.web";
	private static final int SHUTDOWN_DELAY = 3000;
	private static final String GENERIC_IP = "0.0.0.0";
	private static WebServer instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static WebServer getInstance() {
		if (instance == null) {
			instance = new WebServer();
		}
		return instance;
	}

	private HttpServer server;
	private final Object shutdownMonitor = new Object();
	private boolean shutdown = false;
	private final Object finishedMonitor = new Object();
	private boolean finished = false;

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
	public void start(int port, String basePath, Collection<String> servicePackages) {
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
	public void start(int port, String basePath, Collection<String> servicePackages, Integer minNumWorker,
			Integer maxNumWorker) {
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
	public void start(String host, int port, String basePath, Collection<String> servicePackages) {
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
	public void start(String host, int port, String basePath, Collection<String> servicePackages, Integer minNumWorker,
			Integer maxNumWorker) {
		if (server != null) {
			shutdown();
		}
		shutdown = false;
		finished = false;
		servicePackages.add(SHUTDOWN_PACKAGE);
		server = LpeWebUtils.startHttpServer(host, port, basePath, servicePackages.toArray(new String[0]),
				minNumWorker, maxNumWorker);
		// wait for shutdown thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (shutdownMonitor) {
					while (!shutdown) {
						try {
							shutdownMonitor.wait();
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				}
				try {
					Thread.sleep(SHUTDOWN_DELAY);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				server.shutdown();
				synchronized (finishedMonitor) {
					finished = true;
					finishedMonitor.notifyAll();
				}
			}
		}).start();

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
	public static void triggerServerShutdown(int port, String basePath) {
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
	public static void triggerServerShutdown(String host, int port, String basePath) {
		WebResource server = LpeWebUtils.getWebClient().resource(
				"http://" + host + ":" + String.valueOf(port) + "/" + basePath);
		server.path("ShutdownService").path("shutdown").post();
	}

	/**
	 * Blocks until server has been shut down.
	 */
	public void waitForShutdown() {
		synchronized (finishedMonitor) {
			while (!finished) {
				try {
					finishedMonitor.wait();
				} catch (InterruptedException e) {
					shutdown();
				}
			}
		}

	}
}
