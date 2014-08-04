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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.threadpool.GrizzlyExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
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
	private static final int THREE_SECONDS = 3000;
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
	 * Creates a {@link Client}.
	 * 
	 * @return a {@link Client} instanceO
	 */
	public static Client getWebClient() {
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		return Client.create(cc);
	}

	/**
	 * @param url
	 *            URL to connect to
	 * @return a HTTPConnection to the given URL.
	 * @throws IOException
	 *             If connection could not be established
	 */
	public static HttpURLConnection get(String url) throws IOException {
		URL connURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) connURL.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/plain");

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		return conn;
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
	public static HttpServer startHttpServer(String host, int port, String basePath, String[] servicePackages) {
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
	public static HttpServer startHttpServer(String host, int port, String basePath, String[] servicePackages,
			Integer minNumWorker, Integer maxNumWorker) {
		String[] packages = new String[servicePackages.length + 1];
		packages[0] = JACKSON_PACKAGE;
		for (int i = 0; i < servicePackages.length; i++) {
			packages[i + 1] = servicePackages[i];
		}

		ResourceConfig config = new PackagesResourceConfig(packages);
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
		HttpServer server = null;
		URI uri = UriBuilder.fromUri(HTTP_STR + host + "/").port(port).path(basePath).build();
		try {
			server = createHttpServer(uri, config, minNumWorker, maxNumWorker);
		} catch (IllegalArgumentException iae) {
			LOGGER.warn("Illegal Argument Exception happend in main method of ServerLauncher: {}", iae.getMessage());
			if (server != null) {
				server.shutdownNow();
			}
		} catch (IOException ioe) {
			LOGGER.warn("IO Exception happend in main method of ServerLauncher: {}", ioe.getMessage());
		}

		return server;
	}

	private static HttpServer createHttpServer(URI uri, ResourceConfig config, Integer minNumWorker,
			Integer maxNumWorker) throws IOException {
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
		GrizzlyExecutorService ges = GrizzlyExecutorService.createInstance(listener.getTransport()
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

	/**
	 * Tests connection to a service which returns a Boolean without consuming
	 * any parameters.
	 * 
	 * @param host
	 *            host of service
	 * @param port
	 *            port of service
	 * @param path
	 *            path of service
	 * @return true, if connection was successful
	 */
	public static boolean testConnection(String host, String port, String path) {
		String baseUrl = HTTP_STR + host + ":" + port;
		Client client = LpeWebUtils.getWebClient();
		client.setConnectTimeout(THREE_SECONDS);
		boolean result = false;
		try {
			result = client.resource(baseUrl).path(path).accept(MediaType.APPLICATION_JSON).get(Boolean.class);
		} catch (Exception e) {
			// connection failed
			result = false;
		} finally {
				client.destroy();
		}

		return result;
	}

}
