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


/**
 * Utility class for calculating the Jaro-Winkler distance of two strings.
 * 
 * @author Henning Schulz
 * 
 */
public final class JaroWinklerDistance {

	// should not exceed 0.25
	private static final double PREFIX_SCALE = 0.1;

	private static final int MAX_PREFIX_LENGTH = 4;

	private static final double _3 = 3.0;

	private JaroWinklerDistance() {
	}

	/**
	 * Calculates the Jaro-Winkler distance between the given strings.
	 * 
	 * @param s1
	 *            first string
	 * @param s2
	 *            second string
	 * @return the Jaro-Winkler distance between {@code s1} and {@code s2}
	 */
	protected static double getDistance(String s1, String s2) {
		double dj = jaroDistance(s1, s2);
		double l = commonPrefix(s1, s2);

		return dj + ((l * PREFIX_SCALE * (1.0 - dj)));
	}

	private static int commonPrefix(String s1, String s2) {
		for (int i = 1; i <= Math.min(s1.length(), MAX_PREFIX_LENGTH); i++) {
			if (!s2.startsWith(s1.substring(0, i))) {
				return i - 1;
			}
		}

		return MAX_PREFIX_LENGTH;
	}

	private static double jaroDistance(String s1, String s2) {
		String matches1 = matchingCharacters(s1, s2);
		String matches2 = matchingCharacters(s2, s1);

		int m = matches1.length();

		int t = transpositions(matches1, matches2);

		if (m == 0) {
			return 0.0;
		}

		double l1 = s1.length();
		double l2 = s2.length();
		double dm = m;
		double dt = t;

		return ((dm / l1) + (dm / l2) + ((dm - dt) / dm)) / _3;
	}

	private static String matchingCharacters(String s1, String s2) {
		int l1 = s1.length();
		int l2 = s2.length();
		int scope = scope(l1, l2);

		char[] c1 = s1.toCharArray();
		char[] c2 = s2.toCharArray();

		StringBuilder matches = new StringBuilder();

		for (int i = 0; i < c1.length; i++) {
			for (int j = Math.max(0, i - scope); j < Math.min(c2.length, i + scope + 1); j++) {
				if (c1[i] == c2[j]) {
					matches.append(c1[i]);
					c2[j] = 0;
					break;
				}
			}
		}

		return matches.toString();
	}

	private static int transpositions(String m1, String m2) {
		char[] c1 = m1.toCharArray();
		char[] c2 = m2.toCharArray();

		int t = 0;

		for (int i = 0; i < c1.length; i++) {
			if (c1[i] != c2[i]) {
				t++;
			}
		}

		return t / 2;
	}

	private static int scope(int i, int j) {
		return (Math.min(i, j) / 2) + 1;
	}

}
