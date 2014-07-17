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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Utility class for string operations.
 * 
 * @author Alexander Wert, Roozbeh Farahbod
 * 
 */
public final class LpeStringUtils {
	/**
	 * private constructor due to utility class.
	 */
	private LpeStringUtils() {

	}

	/**
	 * Corrects the File separator of the given path to the correct System
	 * specific character! ('/' on Linux/MacOS and '\\' on Windows)
	 * 
	 * @param path
	 *            string to correct
	 * @return corrected string
	 */
	public static String correctFileSeparator(String path) {
		path = path.replace("\\", System.getProperty("file.separator"));
		path = path.replace("/", System.getProperty("file.separator"));
		return path;
	}

	/**
	 * Returns the next word (words delimeted by whitespace or comma) in the src
	 * string after the beginWith portion.
	 * 
	 * @param src
	 *            string in which to search for the word
	 * @param beginWith
	 *            place where to start search
	 * @return Returns the next word (words delimeted by whitespace or comma) in
	 *         the src string after the beginWith portion.
	 */
	public static String nextWordAfter(String src, String beginWith) {
		int index = src.indexOf(beginWith);
		if (index < 0) {
			return "";
		}

		final String rest = src.substring(index);
		StringTokenizer tk = new StringTokenizer(rest, " ,");
		if (tk.countTokens() >= 2) {
			tk.nextToken();
			return tk.nextToken();
		} else {
			return "";
		}
	}

	/**
	 * Checks the equivalency of two strings ignoring lower/upper cases.
	 * 
	 * @param a
	 *            first string
	 * @param b
	 *            second string
	 * @return <code>true</code> if the two strings are equal.
	 */
	public static boolean strEqualCaseInsensitive(String a, String b) {
		return a.toLowerCase().equals(b.toLowerCase());
	}

	/**
	 * Checks the equivalency of two names ignoring lower/upper cases. It trims
	 * the strings before calling
	 * {@link #strEqualCaseInsensitive(String, String)}.
	 * 
	 * @param a
	 *            first string
	 * @param b
	 *            second string
	 * @return <code>true</code> if the two names are equal.
	 */
	public static boolean strEqualName(String a, String b) {
		return strEqualCaseInsensitive(a.trim(), b.trim());
	}

	/**
	 * Checks if the given name is the simple name of the given class. The test
	 * is case insensitive.
	 * 
	 * @param name
	 *            string to check
	 * @param c
	 *            class to check the name against
	 * @return true, if name is the simple name of the passed class
	 */
	public static boolean isClassName(String name, Class<?> c) {
		return strEqualName(name, c.getSimpleName());
	}

	/**
	 * Adds a sequence of the given character to the beginning of the given
	 * string until it reaches the given length.
	 * 
	 * @param src
	 *            source string
	 * @param filler
	 *            filler character
	 * @param fixedLen
	 *            desired length of the resulting string
	 * @return extended string
	 */
	public static String extendStr(String src, char filler, int fixedLen) {
		String result = src;
		while (result.length() < fixedLen) {
			result = filler + result;
		}
		return result;
	}

	/**
	 * Performs a simple tokenization (!) on the given source string.
	 * 
	 * @param src
	 *            string to tokenize
	 * @param separator
	 *            separator for tokenization
	 * @return array of string tokens
	 */
	public static String[] tokenize(String src, String separator) {
		StringTokenizer tokenizer = new StringTokenizer(src, separator);

		String[] result = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			result[i] = tokenizer.nextToken();
			i++;
		}

		return result;
	}

	/**
	 * Given a base directory and a path to a file, it concatinates the two
	 * parts and takes care of missing file separators.
	 * 
	 * @param baseDir
	 *            base directory
	 * @param fileName
	 *            file name
	 * @return the absolute path to the file
	 */
	public static String concatFileName(String baseDir, String fileName) {
		// cleanup
		baseDir = baseDir.trim();
		fileName = fileName.trim();

		if (baseDir.lastIndexOf(File.separator) != baseDir.length() - 1) {
			baseDir = baseDir + File.separator;
		}

		String concat = baseDir + fileName;

		return concat;
	}

	/**
	 * Checks if the given string value is empty or null.
	 * 
	 * @param str
	 *            a string value
	 * @return <code>true</code> if the given value is either <code>null</code>
	 *         or empty; <code>false</code> otherwise.
	 */
	public static boolean isEmptyOrNullString(String str) {
		return (str == null || str.isEmpty());
	}

	/**
	 * Returns a time stamp of the format "yy.MM.dd - HH:mm" for the given date.
	 * 
	 * @param date
	 *            date to convert
	 * @return Returns a time stamp of the format "yy.MM.dd - HH:mm" for the
	 *         given date.
	 */
	public static String getTimeStamp(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd - HH:mm");
		return formatter.format(date);
	}

	/**
	 * Returns a time stamp of the format "yy.MM.dd - HH:mm" for the given date.
	 * 
	 * @param date
	 *            date to convert
	 * @return Returns a time stamp of the format "yy.MM.dd - HH:mm" for the
	 *         given date.
	 */
	public static String getDetailedTimeStamp(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd - HH:mm:ss:SSS");
		return formatter.format(date);
	}

	/**
	 * 
	 * @return returns a time stamp for the current time and date.
	 */
	public static String getTimeStamp() {
		return getTimeStamp(new Date());
	}

	/**
	 * Returns a time stamp that is unique to the given machine, using
	 * {@link System#nanoTime()}.
	 * 
	 * @return a time stamp that is unique to the given machine
	 */
	public static String getUniqueTimeStamp() {
		return getTimeStamp() + "-" + System.nanoTime();
	}

	/**
	 * Clears the string representation of a method signature.
	 * 
	 * @param methodName
	 *            method name to be cleared.
	 * @return clear method name
	 */
	public static String clearMethodName(String methodName) {
		try {
			methodName = methodName.trim();
			int firstBraceIndex = methodName.indexOf('(');
			int secondBraceIndex = methodName.indexOf(')');
			String methodFirstPart = methodName.substring(0, firstBraceIndex);

			if (methodFirstPart.indexOf(' ') >= 0) {
				methodFirstPart = methodFirstPart.substring(methodFirstPart.lastIndexOf(' ') + 1,
						methodFirstPart.length());
			}

			String params = methodName.substring(firstBraceIndex, secondBraceIndex + 1);
			String parameterStringNew = "(";
			if (params.length() > 2) {
				String[] paramArray = params.substring(1, params.length() - 1).split(" ");

				int i = 0;
				for (String par : paramArray) {
					par = par.trim();
					int whiteSpaceIndex = par.indexOf(' ');
					if (whiteSpaceIndex > 0) {
						parameterStringNew += par.substring(0, whiteSpaceIndex);
					} else {
						parameterStringNew += par;
					}
					if (i < (paramArray.length - 1)) {
						parameterStringNew += ",";
					}

				}
			}
			parameterStringNew += ")";
			return methodFirstPart + parameterStringNew;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid method name format!");
		}
	}

	/**
	 * 
	 * @param methodName
	 *            method signature
	 * @return Returns the simple name for the passed full qualified methodName
	 *         / signature
	 */
	public static String getSimpleMethodName(String methodName) {
		try {
			methodName = methodName.trim();
			int firstBraceIndex = methodName.indexOf('(');
			String methodFirstPart = null;
			if (firstBraceIndex >= 0) {
				methodFirstPart = methodName.substring(0, firstBraceIndex);
			} else {
				methodFirstPart = methodName;
			}
			int lastDotIndex = methodFirstPart.lastIndexOf('.');
			if (lastDotIndex >= 0) {
				return methodFirstPart.substring(lastDotIndex + 1);
			} else {
				if (methodFirstPart.contains(" ")) {
					return methodFirstPart.substring(methodFirstPart.lastIndexOf(' ') + 1);
				} else {
					return methodFirstPart;
				}

			}

		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid method name format!");
		}
	}

	/**
	 * Converts method signature (with a leading return type) to a JVM
	 * representation of a method descriptor. Needs a full method signature.
	 * 
	 * @param originSignature
	 *            signature of the method to convert
	 * @return formatted method descriptor
	 */
	public static String convertMethodSignatureToJVMMethodDescriptor(String originSignature) {
		try {
			originSignature = originSignature.trim();
			String[] strArray = originSignature.split(" ");
			String returnType = strArray[strArray.length - 2];
			String methodSignature = strArray[strArray.length - 1];
			int firstBraceIndex = methodSignature.indexOf('(');
			int secondBraceIndex = methodSignature.indexOf(')');
			String parameterString = methodSignature.substring(firstBraceIndex + 1, secondBraceIndex);
			String resultString = "(";
			if (parameterString.length() > 0) {
				String[] parameters = parameterString.split(",");
				for (int i = 0; i < parameters.length; i++) {
					resultString += convertTypeToNativeFormat(parameters[i].trim());
				}
			}
			resultString += ")" + convertTypeToNativeFormat(returnType.trim());
			return resultString;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid method signature format!");
		}
	}

	/**
	 * Converts the given type to the JVM Specification internal format.
	 * 
	 * @param type
	 *            type name to convert
	 * @return native representation of the given type
	 */
	public static String convertTypeToNativeFormat(String type) {
		String resultString = "";
		while (type.contains("[]")) {
			type = type.substring(0, type.lastIndexOf('['));
			resultString += "[";
		}

		if (type.trim().equals("byte")) {
			resultString += "B";
		} else if (type.equals("char")) {
			resultString += "C";
		} else if (type.equals("double")) {
			resultString += "D";
		} else if (type.equals("float")) {
			resultString += "F";
		} else if (type.equals("int")) {
			resultString += "I";
		} else if (type.equals("long")) {
			resultString += "J";
		} else if (type.equals("short")) {
			resultString += "S";
		} else if (type.equals("boolean")) {
			resultString += "Z";
		} else if (type.equals("void")) {
			resultString += "V";
		} else {
			String complexTypeString = type.replace('.', '/');
			resultString += "L" + complexTypeString + ";";
		}
		return resultString;
	}

	/**
	 * Extracts the class name from a method name.
	 * 
	 * @param methodName
	 *            full method name from which the class name should be extracted
	 * @return full class name
	 */
	public static String extractClassName(String methodName) {
		try {
			// cut argument list
			String className = methodName.substring(0, methodName.indexOf('('));
			// cut methodName
			className = className.substring(0, className.lastIndexOf('.'));
			// cut modifier
			if (className.indexOf(' ') >= 0) {
				className = className.substring(className.lastIndexOf(' ') + 1, className.length());
			}
			return className;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid method name format!");
		}
	}

	/**
	 * Converts a package name to a file path.
	 * 
	 * @param packageName
	 *            package name to be converted
	 * @return a path to a directory
	 */
	public static String packageNameToFilePath(String packageName) {
		return packageName.replace('.', '/');
	}

	/**
	 * Tries to retrieve the property with the passed key from the Properties
	 * instance. If property has not been specified, the default value is used.
	 * However, if default value is null, this method throws an
	 * {@link IllegalArgumentException}.
	 * 
	 * @param properties
	 *            Properties to search in
	 * @param key
	 *            key to search for
	 * @param defaultValue
	 *            default value
	 * @return property for the given key
	 */
	public static String getPropertyOrFail(Properties properties, String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (value != null) {
			return value;
		} else if (value == null && defaultValue != null) {
			return defaultValue;
		} else {
			throw new IllegalArgumentException("Property " + key + " has not been specified!");
		}
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
	public static double getDistance(String s1, String s2) {
		return JaroWinklerDistance.getDistance(s1, s2);
	}

	/**
	 * Calculates the Jaro-Winkler distance between the given strings ignoring
	 * lower/upper cases.
	 * 
	 * @param s1
	 *            first string
	 * @param s2
	 *            second string
	 * @return the Jaro-Winkler distance between {@code s1} and {@code s2}
	 *         ignoring lower/upper cases.
	 */
	public static double getDistanceCaseInsensitive(String s1, String s2) {
		return JaroWinklerDistance.getDistance(s1.toLowerCase(), s2.toLowerCase());
	}

	/**
	 * Returns if the given statements are equal.
	 * 
	 * @param sql1
	 *            first SQL statement
	 * @param sql2
	 *            second SQL statement
	 * @return if {@code sql1} and {@code sql2} are equal
	 */
	public static boolean areEqualSql(String sql1, String sql2) {
		return SQLSimilarity.areEqual(sql1, sql2);
	}

	/**
	 * Shortens full qualified method names. Thus,
	 * my.full.package.Class.operation() becomes m.f.p.Class.operation()
	 * 
	 * @param operation
	 *            name to shorten
	 * @return short method name
	 */
	public static String shortenOperationName(String operation) {
		String mainPart = operation.substring(0, operation.indexOf("("));
		String[] packages = mainPart.split("\\.");
		String result = "";
		for (int i = 0; i < packages.length - 2; i++) {
			result += packages[i].substring(0, 1);
			result += ".";
		}
		result += packages[packages.length - 2] + "." + packages[packages.length - 1];
		result += "(";

		String[] parameters = operation.substring(operation.indexOf("(") + 1, operation.indexOf(")")).split(",");
		boolean first = true;
		for (String p : parameters) {
			if (!first) {
				result += ",";
			}
			if (p.contains(".")) {
				result += p.substring(p.lastIndexOf(".") + 1);
			} else {
				result += p;
			}
			first = false;

		}

		result += ")";
		return result;
	}

	/**
	 * Checks whether the given subject matches the target (possibly containing
	 * * wildcards).
	 * 
	 * @param subject
	 *            string to test
	 * @param targetPattern
	 *            pattern to test against (may contain wildcards)
	 * @return true, if subject, matches target
	 */
	public static boolean patternMatches(String subject, String targetPattern) {
		if (targetPattern.contains("*")) {
			String[] wCards = targetPattern.split("\\*");

			for (int i = 0; i < wCards.length; i++) {
				if (subject == null) {
					return false;
				}
				String wc = wCards[i];
				if (wc.isEmpty()) {
					continue;
				}

				int index = subject.indexOf(wc);

				// wCard not detected in the text.
				if (index < 0 || (index > 0 && i == 0)) {
					return false;
				} else {

					// Move to the next card
					try {
						subject = subject.substring(index + wc.length());
					} catch (IndexOutOfBoundsException e) {
						subject = null;
					}
					if (subject != null && subject.isEmpty()) {
						subject = null;
					}

				}

			}
			return targetPattern.endsWith("*") ? true : subject == null;
		} else {
			return subject.equals(targetPattern);
		}

	}

	/**
	 * Checks whether the given subject matches any prefix of the target
	 * (possibly containing * wildcards).
	 * 
	 * @param subject
	 *            string to test
	 * @param targetPattern
	 *            pattern to test against (may contain wildcards)
	 * @return true, if subject matches prefix of target
	 */
	public static boolean patternPrefixMatches(String subject, String targetPattern) {
		if (targetPattern.startsWith(subject)) {
			return true;
		}

		if (targetPattern.contains("*")) {
			String prefixPattern = targetPattern.substring(0, targetPattern.indexOf("*"));
			return subject.startsWith(prefixPattern);
		}

		return false;
	}

}
