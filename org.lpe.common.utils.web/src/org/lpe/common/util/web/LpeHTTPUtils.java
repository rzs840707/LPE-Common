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
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * Utilities for HTTP operations.
 * 
 * @author Alexander Wert
 * 
 */
public final class LpeHTTPUtils {
	private static final String HTTP_STR = "http://";
	private static final int THREE_SECONDS = 3000;

	/**
	 * private constructor due to utility class.
	 */
	private LpeHTTPUtils() {
	}

	/**
	 * Creates a {@link WebResource} for the passed service url.
	 * 
	 * @param service
	 *            url of the service
	 * @return a {@link WebResource} instanceO
	 */
	public static WebResource getWebResource(final String service) {
		return getWebResource(service, 0);
	}

	/**
	 * Creates a {@link WebResource} for the passed service url. The timeout
	 * specifies how long the request is waiting before an exception is thrown.
	 * 
	 * @param service
	 *            url of the service
	 * @param timeout
	 *            time before an exception is thrown
	 * @return a {@link WebResource} instanceO
	 */
	public static WebResource getWebResource(final String service, final int timeout) {
		final ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);
		cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
		return Client.create(cc).resource(service);
	}

	/**
	 * Performs a GET operation with the specified result type to the given URL.
	 * 
	 * @param url
	 *            URL of the resource to get
	 * @param resultClass
	 *            type of the result
	 * @param <T>
	 *            result type of the GET operation
	 * @return the GET result
	 */
	public static <T> T get(final String url, final Class<T> resultClass) {
		final WebResource service = getWebResource(getService(url));
		final String path = getPath(url);
		final T result = service.path(path).accept(MediaType.APPLICATION_JSON)
				.get(resultClass);
		return result;
	}

	/**
	 * Performs a GET operation with the specified result type to the given URL.
	 * This operation inserts the passed inputParameters into the URL.
	 * 
	 * @param url
	 *            URL of the resource to get
	 * @param resultClass
	 *            type of the result
	 * @param inputParameters
	 *            query parameters
	 * @param <T>
	 *            result type of the GET operation
	 * @return the GET result
	 */
	public static <T> T get(final String url,
			final MultivaluedMap<String, String> inputParameters, final Class<T> resultClass) {
		final WebResource service = getWebResource(getService(url));
		final String path = getPath(url);
		final T result = service.path(path).queryParams(inputParameters)
				.accept(MediaType.APPLICATION_JSON).get(resultClass);
		return result;
	}

	/**
	 * Performs a POST operation to the given url.
	 * 
	 * @param url
	 *            target URL
	 * @param resultClass
	 *            class of result type
	 * @param <T>
	 *            result type
	 * @return result of the post operation of type T
	 */
	public static <T> T post(final String url, final Class<T> resultClass) {
		final WebResource service = getWebResource(getService(url));
		final String path = getPath(url);
		final T result = service.path(path).accept(MediaType.APPLICATION_JSON)
				.post(resultClass);
		return result;
	}

	/**
	 * Performs a POST operation to the given url, using input as input
	 * parameter.
	 * 
	 * @param url
	 *            target URL
	 * @param resultClass
	 *            class of result type
	 * @param input
	 *            input object for post
	 * @param <T>
	 *            result type
	 * @return result of the post operation of type T
	 */
	public static <T> T post(final String url, final Object input, final Class<T> resultClass) {
		final WebResource service = getWebResource(getService(url));
		final String path = getPath(url);
		final T result = service.path(path).type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(resultClass, input);
		return result;
	}

	/**
	 * Performs a POST operation to the given url.
	 * 
	 * @param url
	 *            target URL
	 */
	public static void post(final String url) {
		final WebResource service = getWebResource(getService(url));
		final String path = getPath(url);
		service.path(path).post();
	}

	/**
	 * Performs a POST operation to the given url.
	 * 
	 * @param url
	 *            target URL
	 * @param input
	 *            input input object for post
	 */
	public static void post(final String url, final Object input) {
		final WebResource service = getWebResource(getService(url));
		final String path = getPath(url);
		service.path(path).type(MediaType.APPLICATION_JSON).post(input);
	}

	/**
	 * @param url
	 *            URL to connect to
	 * @return a HTTPConnection to the given URL.
	 * @throws IOException
	 *             If connection could not be established
	 */
	public static HttpURLConnection get(final String url) throws IOException {
		final URL connURL = new URL(url);
		final HttpURLConnection conn = (HttpURLConnection) connURL.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/plain");

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		return conn;
	}

	private static String getService(final String url) {
		String subURL = null;
		if (url.startsWith(HTTP_STR)) {
			subURL = url.substring(HTTP_STR.length());

		} else {
			subURL = url;
		}
		final int slashIndex = subURL.indexOf('/');
		if (slashIndex >= 0) {
			return HTTP_STR + subURL.substring(0, slashIndex);
		} else {
			return HTTP_STR + subURL;
		}
	}

	private static String getPath(final String url) {
		String subURL = null;
		if (url.startsWith(HTTP_STR)) {
			subURL = url.substring(HTTP_STR.length());

		} else {
			subURL = url;
		}
		final int slashIndex = subURL.indexOf('/');
		if (slashIndex >= 0) {
			return subURL.substring(slashIndex + 1);
		} else {
			return null;
		}
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
	public static boolean testConnection(final String host, final String port, final String path) {
		final String baseUrl = HTTP_STR + host + ":" + port;
		final Client client = getWebClient();
		client.setConnectTimeout(THREE_SECONDS);
		boolean result = false;
		try {
			result = client.resource(baseUrl).path(path).accept(MediaType.APPLICATION_JSON).get(Boolean.class);
		} catch (final Exception e) {
			// connection failed
			result = false;
		} finally {
				client.destroy();
		}

		return result;
	}

	/**
	 * Creates a {@link Client}.
	 * 
	 * @return a {@link Client} instanceO
	 */
	public static Client getWebClient() {
		final ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		return Client.create(cc);
	}

}
