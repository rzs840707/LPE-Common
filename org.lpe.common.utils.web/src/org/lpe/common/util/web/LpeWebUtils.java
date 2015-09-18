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

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.threadpool.GrizzlyExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * Utilities for HTTP operations.
 * 
 * @author Alexander Wert
 * 
 */
public final class LpeWebUtils {
	private static final int TIMEOUT_SECONDS = 60;
	private static final int KILO = 1000;
	private static final int DEFAULT_PORT = 80;
	private static final String JACKSON_PACKAGE = "org.codehaus.jackson.jaxrs";
	private static final Logger LOGGER = LoggerFactory.getLogger(LpeWebUtils.class);
	private static final String HTTP_STR = "http://";

	/**
	 * private constructor due to utility class.
	 */
	private LpeWebUtils() {
	}

	/**
	 * Starts a Web server. The server is bound to the given host and port. The
	 * servicePackages are searched for Jersey services.
	 * 
	 * @param host
	 *            host to bind the server to
	 * @param port
	 *            port to bind the server to
	 * @param basePath
	 *            the base URL path after http://HOST:PORT/
	 * @param servicePackages
	 *            packages to look for Jersey services
	 * @return the HttpServer instance.
	 */
	public static HttpServer startHttpServer(final String host, final int port, final String basePath, final String[] servicePackages) {
		return startHttpServer(host, port, basePath, servicePackages, null, null);
	}

	/**
	 * Starts a Web server. The server is bound to the given host and port. The
	 * servicePackages are searched for Jersey services.
	 * 
	 * @param host
	 *            host to bind the server to
	 * @param port
	 *            port to bind the server to
	 * @param basePath
	 *            the base URL path after http://HOST:PORT/
	 * @param servicePackages
	 *            packages to look for Jersey services
	 * @param minNumWorker
	 *            initial thread pool size
	 * @param maxNumWorker
	 *            maximal thread pool size
	 * @return the HttpServer instance.
	 */
	public static HttpServer startHttpServer(final String host, final int port, final String basePath, final String[] servicePackages,
			final Integer minNumWorker, final Integer maxNumWorker) {
		final String[] packages = new String[servicePackages.length + 1];
		packages[0] = JACKSON_PACKAGE;
		for (int i = 0; i < servicePackages.length; i++) {
			packages[i + 1] = servicePackages[i];
		}

		final ResourceConfig config = new PackagesResourceConfig(packages);
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
		HttpServer server = null;
		final URI uri = UriBuilder.fromUri(HTTP_STR + host + "/").port(port).path(basePath).build();
		try {
			server = createHttpServer(uri, config, minNumWorker, maxNumWorker);
		} catch (final IllegalArgumentException iae) {
			LOGGER.warn("Illegal Argument Exception happend in main method of ServerLauncher: {}", iae.getMessage());
			if (server != null) {
				server.shutdownNow();
			}
		} catch (final IOException ioe) {
			LOGGER.warn("IO Exception happend in main method of ServerLauncher: {}", ioe.getMessage());
		}

		return server;
	}

	private static HttpServer createHttpServer(final URI uri, final ResourceConfig config, final Integer minNumWorker,
			final Integer maxNumWorker) throws IOException {
		final HttpHandler processor = ContainerFactory.createContainer(HttpHandler.class, config);

		final String scheme = uri.getScheme();
		if (!scheme.equalsIgnoreCase("http")) {
			throw new IllegalArgumentException("The URI scheme, of the URI " + uri
					+ ", must be equal (ignoring case) to 'http'");
		}

		final String host = (uri.getHost() == null) ? NetworkListener.DEFAULT_NETWORK_HOST : uri.getHost();
		final int port = (uri.getPort() == -1) ? DEFAULT_PORT : uri.getPort();

		// Create the server.
		final HttpServer server = new HttpServer();
		final NetworkListener listener = new NetworkListener("grizzly", host, port);

		// Map the path to the processor.
		final ServerConfiguration serverConfig = server.getServerConfiguration();
		serverConfig.addHttpHandler(processor, uri.getPath());

		// Start the server.

		if (minNumWorker != null && minNumWorker > 0) {
			listener.getTransport().getWorkerThreadPoolConfig().setCorePoolSize(minNumWorker);
		}

		if (maxNumWorker != null && maxNumWorker > 0) {
			listener.getTransport().getWorkerThreadPoolConfig().setMaxPoolSize(maxNumWorker);
		}
		final GrizzlyExecutorService ges = GrizzlyExecutorService.createInstance(listener.getTransport()
				.getWorkerThreadPoolConfig());
		listener.getTransport().setWorkerThreadPool(ges);
		listener.getKeepAlive().setIdleTimeoutInSeconds(TIMEOUT_SECONDS);
		listener.getTransport().setConnectionTimeout(TIMEOUT_SECONDS * KILO);
		listener.getTransport().setKeepAlive(true);
		server.addListener(listener);
		serverConfig.setJmxEnabled(true);
		server.start();

		return server;
	}

	public static void addServlet(final HttpServer server, final Service service, final String path) {
		server.getServerConfiguration().addHttpHandler(new HttpHandler() {

			@Override
			public void service(final Request req, final Response resp) throws Exception {
				service.doService(req, resp);

			}
		}, path);
	}
}
