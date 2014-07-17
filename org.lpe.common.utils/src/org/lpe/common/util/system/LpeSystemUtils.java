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
package org.lpe.common.util.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.lpe.common.util.LpeFileUtils;
import org.lpe.common.util.LpeStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for generic system operations.
 * 
 * @author Alexander Wert, Roozbeh Farahbod
 * 
 */
public final class LpeSystemUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(LpeSystemUtils.class);

	private static final String NATIVE_SUBFOLDER = "native";

	private static final String JAVA_LIBRARY_PATH = "java.library.path";

	private static final String KILLSERVICE_CMD_WINDOWS = "net stop ";

	private static Logger logger = LoggerFactory.getLogger(LpeSystemUtils.class);

	private static boolean nativeLibrariesLoaded = false;

	private static String systemTempDir = null;

	private static String eol;

	/**
	 * private constructor due to utility class.
	 */
	private LpeSystemUtils() {
	}

	/**
	 * @return a system independent EOL string.
	 */
	public static String getEOL() {
		if (eol == null) {
			eol = System.getProperty("line.separator");
			if (eol == null) {
				eol = "\n";
			}
		}
		return eol;
	}

	/**
	 * 
	 * @return returns the root folder of the running application.
	 */
	public static String getRootFolder() {
		return getRootFolder(null);
	}

	/**
	 * Detects and returns the root folder of the running application using the
	 * passed class to locate the directory.
	 * 
	 * @param mainClass
	 *            class used to locate directory
	 * @return returns the root folder of the running application.
	 */
	public static String getRootFolder(Class<?> mainClass) {
		if (mainClass == null) {
			mainClass = LpeSystemUtils.class;
		}

		final String baseErrorMsg = "Cannot locate root folder.";

		final String classFile = mainClass.getName().replaceAll("\\.", "/") + ".class";
		final URL classURL = ClassLoader.getSystemResource(classFile);

		String fullPath = "";
		String sampleClassFile = "/org/sopeco/util/Tools.class";
		if (classURL == null) {
			LpeSystemUtils tempObject = new LpeSystemUtils();
			fullPath = tempObject.getClass().getResource(sampleClassFile).toString();
			// logger.warn("{} The application may be running in an OSGi container.",
			// baseErrorMsg);
			// return ".";
		} else {
			fullPath = classURL.toString();
		}

		try {
			fullPath = URLDecoder.decode(fullPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("{} UTF-8 encoding is not supported.", baseErrorMsg);
			return ".";
		}

		if (fullPath.indexOf("file:") > -1) {
			fullPath = fullPath.replaceFirst("file:", "").replaceFirst(classFile, "");
			fullPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
		}
		if (fullPath.indexOf("jar:") > -1) {
			fullPath = fullPath.replaceFirst("jar:", "").replaceFirst("!" + classFile, "");
			fullPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
		}
		if (fullPath.indexOf("bundleresource:") > -1) {
			fullPath = fullPath.substring(0, fullPath.indexOf(sampleClassFile));
		}

		// replace the java separator with the
		fullPath = fullPath.replace('/', File.separatorChar);

		// remove leading backslash
		if (fullPath.startsWith("\\")) {
			fullPath = fullPath.substring(1);
		}

		// remove the final 'bin'
		final int binIndex = fullPath.indexOf(File.separator + "bin");
		if (binIndex == fullPath.length() - "/bin".length()) {
			fullPath = fullPath.substring(0, binIndex);
		}

		LOGGER.debug("Root folder is detected at {}.", fullPath);

		return fullPath;
	}

	/**
	 * Returns the index of the given object in the given array if it exsists.
	 * It uses the {@link Object#equals(Object)} method.
	 * 
	 * @param obj
	 *            object to look for
	 * @param array
	 *            array of objects
	 * @return the index of the object or -1 if the object doesn't exist
	 */
	public static int exists(Object obj, Object[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(obj)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return the name of the operating system
	 */
	public static String getOSName() {
		return System.getProperty("os.name");
	}

	/**
	 * @return true if the OS is Mac.
	 */
	public static boolean isMacOS() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

	/**
	 * @return trus if the OS is Windows.
	 */
	public static boolean isWindowsOS() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	/**
	 * @return trus if the OS is Unix or Linux.
	 */
	public static boolean isUnix() {
		final String name = System.getProperty("os.name").toLowerCase();
		return name.contains("nix") || name.contains("nux");
	}

	/**
	 * Shutdowns the service identified with the given name.
	 * 
	 * @param serviceName
	 *            the name of the service
	 * @throws SystemToolsException
	 *             if shutdown fails.
	 */
	public static void shutdownService(String serviceName) throws SystemToolsException {
		if (LpeSystemUtils.isWindowsOS()) {
			try {
				Runtime.getRuntime().exec(KILLSERVICE_CMD_WINDOWS + serviceName);
			} catch (IOException e) {
				throw new SystemToolsException("Could not shutdown " + serviceName + ".", e, logger);
			}
		} else {
			logger.warn("Process is running on a non-Windows OS. Shutting down services is not implemented for this OS.");
			// TODO Implement shutdown for other OSs
		}
	}

	/**
	 * Unpacks all native libraries in the JAR files into a temp folder and
	 * explicitly appends them to the Java native library path.
	 * 
	 * The native library files are assumed to be under
	 * {@value SystemTools#NATIVE_SUBFOLDER} directories in the classpath. If
	 * this is done once, it will not do it again. Also, see
	 * {@link #appendLibraryPath(String)}.
	 * 
	 */
	public static void loadNativeLibraries() {
		if (nativeLibrariesLoaded) {
			logger.warn("Native libraries are already loaded. Nothing done.");
			return;
		}

		try {
			String tempLibDir = extractFilesFromClasspath(NATIVE_SUBFOLDER, "lpeLibs", "native libraries",
					LpeSystemUtils.class.getClassLoader());
			appendLibraryPath(tempLibDir);

			nativeLibrariesLoaded = true;

			logger.debug("The value of '{}' is {}.", JAVA_LIBRARY_PATH, System.getProperty(JAVA_LIBRARY_PATH));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the system temp directory.
	 * 
	 * @return the system temp directory
	 */
	public static String getSystemTempDir() {
		if (systemTempDir == null) {
			systemTempDir = FileUtils.getTempDirectoryPath();
			logger.debug("Temp direcotry is located at {}.", systemTempDir);
		}
		return systemTempDir;
	}

	/**
	 * Extracts files from a directory in the classpath to a temp directory
	 * (with a time stamp) and returns the File instance of the destination
	 * directory.
	 * 
	 * @param srcDirName
	 *            a directory name in the classpath
	 * @param destName
	 *            the name of the destination folder in the temp folder
	 * @param fileType
	 *            a string describing the file types, if a log message is needed
	 * @param classLoader
	 *            classloader to use
	 * 
	 * @return the name of the target directory
	 * 
	 * @throws IOException
	 *             ...
	 * @throws URISyntaxException
	 *             ...
	 * 
	 * @see #extractFilesFromClasspath(String, String, String, boolean)
	 */
	public static String extractFilesFromClasspath(String srcDirName, String destName, String fileType,
			ClassLoader classLoader) throws IOException, URISyntaxException {
		return extractFilesFromClasspath(srcDirName, destName, fileType, classLoader, true);
	}

	/**
	 * Extracts files from a directory in the classpath to a temp directory and
	 * returns the File instance of the destination directory.
	 * 
	 * @param srcDirName
	 *            a directory name in the classpath
	 * @param destName
	 *            the name of the destination folder in the temp folder
	 * @param fileType
	 *            a string describing the file types, if a log message is needed
	 * @param timeStamp
	 *            if <code>true</code>, it will add a time stamp to the name of
	 *            the target directory (recommended)
	 * @param classloader
	 *            classloader to use
	 * 
	 * @return the name of the target directory
	 * 
	 * @throws IOException
	 *             ...
	 * @throws URISyntaxException
	 *             ...
	 */
	public static String extractFilesFromClasspath(String srcDirName, String destName, String fileType,
			ClassLoader classloader, boolean timeStamp) throws IOException, URISyntaxException {

		if (timeStamp) {
			// remove dots and colons from timestamp as they are not allowed for
			// windows directory names
			String clearedTimeStamp = (LpeStringUtils.getTimeStamp() + "-" + Thread.currentThread().getId()).replace(
					'.', '_');

			clearedTimeStamp = clearedTimeStamp.replace(':', '_');
			clearedTimeStamp = clearedTimeStamp.replace(' ', '_');

			destName = destName + "_" + clearedTimeStamp;
		}
		final String targetDirName = LpeFileUtils.concatFileName(getSystemTempDir(), destName);

		// create a temp lib directory
		File targetDirFile = new File(targetDirName);
		if (!targetDirFile.exists()) {
			boolean ok = targetDirFile.mkdir();
			if (!ok) {
				logger.warn("Could not create directory {}", targetDirFile.getAbsolutePath());
			}
		}

		logger.debug("Copying {} to {}.", fileType, targetDirName);

		Enumeration<URL> urls = classloader.getResources(srcDirName); // getSystemResources(srcDirName);

		if (urls.hasMoreElements()) {
			logger.debug("There are some urls for resource '{}' provided by the classloader.", srcDirName);
		} else {
			logger.debug("There are no urls for resource '{}' provided by the classloader.", srcDirName);
		}

		while (urls.hasMoreElements()) {

			final URL url = urls.nextElement();

			if (fileType != null && fileType.trim().length() > 0) {
				logger.debug("Loading {} from {}...", fileType, url);
			}

			Iterator<File> libs = null;

			if (url.getProtocol().equals("bundleresource")) {
				continue;
			} else if (url.getProtocol().equals("jar")) {
				try {
					// most likely because it is within a JAR file
					final String unpackedJarDir = LpeFileUtils.concatFileName(targetDirName, "temp");
					// TODO: fix exception handling

					extractJARtoTemp(url, srcDirName, unpackedJarDir);

					final String unpackedNativeDir = LpeFileUtils.concatFileName(unpackedJarDir, srcDirName);
					final File unpackedNativeDirFile = new File(unpackedNativeDir);
					libs = FileUtils.iterateFiles(unpackedNativeDirFile, null, false);
				} catch (IllegalArgumentException iae) {
					// ignore
					LOGGER.warn("Jar not found, could not extract jar. {}", iae);

				}
			} else {
				// File nativeLibDir = new
				// File(url.toString().replaceAll("\\\\", "/"));
				File nativeLibDir = new File(url.toURI());
				libs = FileUtils.iterateFiles(nativeLibDir, null, false);
			}

			while (libs != null && libs.hasNext()) {
				final File libFile = libs.next();
				logger.debug("Copying resouce file {}...", libFile.getName());
				FileUtils.copyFileToDirectory(libFile, targetDirFile);
			}
		}

		return targetDirName;
	}

	/**
	 * Extracts a file/folder identified by the URL that resides in the
	 * classpath, into the destiation folder.
	 * 
	 * @param url
	 *            URL of the JAR file
	 * @param dirOfInterest
	 *            the name of the directory of interest
	 * @param dest
	 *            destination folder
	 * 
	 * @throws IOException
	 *             ...
	 * @throws URISyntaxException
	 *             ...
	 */
	public static void extractJARtoTemp(URL url, String dirOfInterest, String dest) throws IOException,
			URISyntaxException {
		if (!url.getProtocol().equals("jar")) {
			throw new IllegalArgumentException("Cannot locate the JAR file.");
		}

		// create a temp lib directory
		File tempJarDirFile = new File(dest);
		if (!tempJarDirFile.exists()) {
			boolean ok = tempJarDirFile.mkdir();
			if (!ok) {
				logger.warn("Could not create directory {}", tempJarDirFile.getAbsolutePath());
			}

		} else {
			FileUtils.cleanDirectory(tempJarDirFile);
		}

		String urlStr = url.getFile();
		// if (urlStr.startsWith("jar:file:") || urlStr.startsWith("jar:") ||
		// urlStr.startsWith("file:")) {
		// urlStr = urlStr.replaceFirst("jar:", "");
		// urlStr = urlStr.replaceFirst("file:", "");
		// }
		if (urlStr.contains("!")) {
			final int endIndex = urlStr.indexOf("!");
			urlStr = urlStr.substring(0, endIndex);
		}

		URI uri = new URI(urlStr);

		final File jarFile = new File(uri);

		logger.debug("Unpacking jar file {}...", jarFile.getAbsolutePath());

		java.util.jar.JarFile jar = null;
		InputStream is = null;
		OutputStream fos = null;
		try {
			jar = new JarFile(jarFile);
			java.util.Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				java.util.jar.JarEntry file = (JarEntry) entries.nextElement();

				String destFileName = dest + File.separator + file.getName();
				if (destFileName.indexOf(dirOfInterest) < 0) {
					continue;
				}

				logger.debug("unpacking {}...", file.getName());

				java.io.File f = new java.io.File(destFileName);
				if (file.isDirectory()) { // if its a directory, create it
					boolean ok = f.mkdir();
					if (!ok) {
						logger.warn("Could not create directory {}", f.getAbsolutePath());
					}
					continue;
				}
				is = new BufferedInputStream(jar.getInputStream(file));
				fos = new BufferedOutputStream(new FileOutputStream(f));
				while (is.available() > 0) {
					fos.write(is.read());
				}
			}

			logger.debug("Unpacking jar file done.");
		} catch (IOException e) {
			throw e;
		} finally {
			if (jar != null) {
				jar.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * Appends a directory to the Java library path.
	 * 
	 * @param path
	 *            the new path
	 * @throws Exception
	 *             ...
	 * @see {@link #setLibraryPath(String)}
	 */
	public static void appendLibraryPath(String path) throws Exception {
		setLibraryPath(System.getProperty(JAVA_LIBRARY_PATH) + File.pathSeparator + path);
	}

	/**
	 * Resets the Java library path to a new value.
	 * 
	 * @param path
	 *            the new path
	 * @throws Exception
	 *             ...
	 */
	public static void setLibraryPath(String path) throws Exception {
		System.setProperty(JAVA_LIBRARY_PATH, path);

		// set sys_paths to null
		final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
		sysPathsField.setAccessible(true);
		sysPathsField.set(null, null);
	}

	/**
	 * Main method. Load native libraries.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		loadNativeLibraries();
	}
}
