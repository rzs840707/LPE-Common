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
package org.lpe.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for stream operations.
 * 
 * @author Alexander Wert, Roozbeh Farahbod
 * 
 */
public final class LpeStreamUtils {

	private static final int DEFAULT_BUFFER_SIZE = 1024;

	/**
	 * private constructor due to utility class.
	 */
	private LpeStreamUtils() {

	}

	/**
	 * Reads the lines of the given URL.
	 * 
	 * @param url
	 *            where to read lines from
	 * @return a list of lines read
	 * @throws IOException
	 *             thrown if URL not reachable
	 */
	public static List<String> readLines(URL url) throws IOException {
		List<String> result = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		String line = "";

		do {
			line = reader.readLine();
			if (line != null) {
				result.add(line);
			}
		} while (line != null);

		reader.close();

		return result;
	}

	/**
	 * Reads the content of the given stream as a string.
	 * 
	 * @param is
	 *            stream from which to read
	 * @return the string read from stream
	 * @throws IOException
	 *             thrwon if stream cannot be opened
	 */
	public static String readFromInputStream(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuffer content = new StringBuffer();
		String line;

		while ((line = reader.readLine()) != null) {
			content.append(line + " ");
		}

		return content.toString();
	}

	/**
	 * Pipes input stream to output stream .
	 * 
	 * @param is
	 *            input stream
	 * @param os
	 *            output stream
	 * @param bufferSize
	 *            buffer size
	 * @throws IOException
	 *             io exception
	 */
	public static void pipe(InputStream is, OutputStream os, int bufferSize) throws IOException {
		if (os == null) {
			throw new RuntimeException("Cannot pipe to an outputstream which is null!");
		}
		int n;
		byte[] buffer = new byte[bufferSize];
		while ((n = is.read(buffer)) > -1) {
			os.write(buffer, 0, n);
			os.flush();
		}
		os.close();
	}

	/**
	 * Pipes input stream to output stream .
	 * 
	 * @param is
	 *            input stream
	 * @param os
	 *            output stream
	 * @throws IOException
	 *             io exception
	 */
	public static void pipe(InputStream is, OutputStream os) throws IOException {
		pipe(is, os, DEFAULT_BUFFER_SIZE);
	}
}
