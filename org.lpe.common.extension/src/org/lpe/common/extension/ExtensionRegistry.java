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
/**
 * 
 */
package org.lpe.common.extension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.util.LpeFileUtils;
import org.lpe.common.util.LpeStreamUtils;
import org.lpe.common.util.LpeStringUtils;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The extension registry.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public final class ExtensionRegistry implements IExtensionRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionRegistry.class);

	private static final String DIR_SEPARATOR = ",";

	private static final String DEFAULT_PLUGINS_FOLDER_IN_CLASSPATH = "plugins";
	public static final String PLUGINS_FOLDER_PROPERTY_KEY = "org.lpe.common.extension.pluginsDirNames";
	public static final String APP_ROOT_DIR_PROPERTY_KEY = "org.lpe.common.extension.appRootDir";

	private static final String EXTENSIONS_FILE_NAME = "extensions.info";

	private static IExtensionRegistry singleton = null;

	/** Holds a mapping of extension names to extensions. */
	private Map<String, IExtension<?>> extensions = new HashMap<String, IExtension<?>>();

	@SuppressWarnings("rawtypes")
	private Map<Class, Extensions> extensionsMap = new HashMap<Class, Extensions>();

	private boolean initialized = false;
	private String tempPluginsDir;

	/**
	 * Returns a singleton instance of the extension registry.
	 * 
	 * @return Returns a singleton instance of the extension registry.
	 */
	public static synchronized IExtensionRegistry getSingleton() {
		if (singleton == null) {
			singleton = new ExtensionRegistry();
		}
		return singleton;
	}

	/**
	 * Adds an extension to the registry.
	 * 
	 * @param ext
	 *            extension to add
	 */
	public void addExtension(IExtension<?> ext) {
		extensions.put(ext.getName(), ext);
	}

	@Override
	public void removeExtension(String name) {
		extensions.remove(name);
	}

	@Override
	public Collection<? extends IExtension<?>> getExtensions() {
		return Collections.unmodifiableCollection(extensions.values());
	}

	@Override
	public IExtension<?> getExtension(String name) {
		return extensions.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IExtensionArtifact> IExtension<T> getExtension(String name, Class<T> type) {
		IExtension<?> ext = extensions.get(name);
		if (type.isAssignableFrom(ext.getClass().getTypeParameters()[0].getGenericDeclaration())) {
			return (IExtension<T>) ext;
		} else {
			return null;
		}
	}

	@Override
	public <E extends IExtension<?>> Extensions<E> getExtensions(Class<E> c) {
		@SuppressWarnings("unchecked")
		Extensions<E> exts = extensionsMap.get(c);
		if (exts == null) {
			exts = new Extensions<E>(c);
			extensionsMap.put(c, exts);
		}
		return exts;
	}

	/*
	 * Preventing public instantiation of the registry.
	 */
	private ExtensionRegistry() {
		initialize();
	}

	/**
	 * Initializes the plugin registry.
	 */
	private void initialize() {
		if (!initialized) {
			loadExtensions();
		} else {
			LOGGER.warn("Plugin registry cannot be re-initialized.");
		}

		initialized = true;
	}

	/**
	 * Loads the extensions with basic class loading.
	 * 
	 * @param registry
	 *            the Eclipse platform extension registry
	 * @param eid
	 *            extension point id
	 */

	private void loadExtensions() {
		LOGGER.info("Loading extensions...");
		String pluginsDirNames = GlobalConfiguration.getInstance().getProperty(PLUGINS_FOLDER_PROPERTY_KEY);

		Set<String> pluginsDirsSet = new HashSet<String>();
		pluginsDirsSet.add(DEFAULT_PLUGINS_FOLDER_IN_CLASSPATH);
		if (pluginsDirNames != null) {
			String[] pluginsDirs = tokenize(pluginsDirNames, DIR_SEPARATOR);

			for (String dir : pluginsDirs) {
				pluginsDirsSet.add(dir);
			}
		}

		Set<URL> extensionsInfoURLs = new HashSet<URL>();
		ClassLoader classLoader = loadPluginInformationFromSources(pluginsDirsSet, extensionsInfoURLs);

		lookForExtensionInfoFiles(classLoader, extensionsInfoURLs);

		Set<String> extensionClasses = gatherExtensionClassFiles(extensionsInfoURLs);

		loadExtensionClasses(classLoader, extensionClasses);

		if (tempPluginsDir != null && new File(tempPluginsDir).exists()) {
			try {
				LpeFileUtils.removeDir(tempPluginsDir);
			} catch (IOException e) {
				LOGGER.warn("Where not able to remove directory {}!", tempPluginsDir);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void loadExtensionClasses(ClassLoader classLoader, Set<String> extensionClasses) {
		for (String extClassName : extensionClasses) {
			if (extClassName.trim().isEmpty()) {
				continue;
			}

			Class<?> c;
			try {
				c = classLoader.loadClass(extClassName);
				Object o = c.newInstance();
				if (o instanceof IExtension) {
					IExtension ext = (IExtension) o;
					this.addExtension(ext);
					LOGGER.debug("Loading extension {}.", ext.getName());
				}
			} catch (Exception e) {
				LOGGER.warn("Could not load extension {}. Reason: ({}) {}", new Object[] { extClassName,
						e.getClass().getSimpleName(), e.getMessage() });
			}
		}
	}

	/**
	 * gather the list of all extension class files
	 */
	private Set<String> gatherExtensionClassFiles(Set<URL> extensionsInfoURLs) {
		Set<String> extensionClasses = new HashSet<String>();
		for (URL url : extensionsInfoURLs) {
			try {
				extensionClasses.addAll(LpeStreamUtils.readLines(url));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return extensionClasses;
	}

	/**
	 * Look for all 'extensions.info' files in the classpath and the default
	 * location
	 * 
	 * @param classLoader
	 *            class loader to use
	 * @param extensionsInfoURLs
	 *            set of URLs where to add the URLs of found extension.info
	 *            files
	 */
	private void lookForExtensionInfoFiles(ClassLoader classLoader, Set<URL> extensionsInfoURLs) {
		final String extensionFilePath = DEFAULT_PLUGINS_FOLDER_IN_CLASSPATH + '/' + EXTENSIONS_FILE_NAME;
		Enumeration<URL> eURLs;
		try {
			eURLs = classLoader.getResources(extensionFilePath);
			while (eURLs.hasMoreElements()) {
				extensionsInfoURLs.add(eURLs.nextElement());
			}

		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		for (URL url : extensionsInfoURLs) {
			LOGGER.debug("Found extensions info at: {}", url);
		}
		if (extensionsInfoURLs.size() == 0) {
			LOGGER.warn("Found no extensions information.");
		}
	}

	/**
	 * Gather list of JAR files in plugins folders and gathersextensions.info
	 * files in those directories.
	 */
	private ClassLoader loadPluginInformationFromSources(Set<String> pluginsDirsSet, Set<URL> extensionsInfoURLs) {
		Set<URL> jarURLs = new HashSet<URL>();

		for (String dir : pluginsDirsSet) {
			loadExtensionsInfoAndJARs(dir, extensionsInfoURLs, jarURLs);
		}

		ClassLoader classLoader = new URLClassLoader(jarURLs.toArray(new URL[] {}), this.getClass().getClassLoader());

		// unpack all extensions.info's separately and gathers them in the list

		try {
			tempPluginsDir = LpeSystemUtils.extractFilesFromClasspath("plugins", "lpePlugins", "plugins files",
					classLoader);

			String[] infoFiles = LpeFileUtils.getFileNames(tempPluginsDir, "*.info");
			for (String infoFileName : infoFiles) {
				final String fullName = LpeFileUtils.concatFileName(tempPluginsDir, infoFileName);
				URL url = new URL("file", "", fullName);
				extensionsInfoURLs.add(url);
			}

		} catch (Exception e) {
			LOGGER.error("Could not unpack plugins information in the classpath. Reason:", e);
		}
		return classLoader;
	}

	/**
	 * Loads JAR files and plugin extension information from the given relative
	 * plugins directory.
	 * 
	 * @param pluginsDirName
	 *            relative path to the plugins folder
	 * @param extensionsInfoURLs
	 *            the resulting set of URLs to extension infos
	 * @param jarURLs
	 *            the resulting aggregated JAR URLs
	 */
	private void loadExtensionsInfoAndJARs(String pluginsDirName, Set<URL> extensionsInfoURLs, Set<URL> jarURLs) {
		if (!LpeFileUtils.isAbsolutePath(pluginsDirName)) {
			String appRootDir = GlobalConfiguration.getInstance().getProperty(APP_ROOT_DIR_PROPERTY_KEY);
			appRootDir = appRootDir.replace("\\", File.separator);
			appRootDir = appRootDir.replace("/", File.separator);
			GlobalConfiguration.getInstance().putProperty(APP_ROOT_DIR_PROPERTY_KEY, appRootDir);
			pluginsDirName = LpeFileUtils.concatFileName(appRootDir, pluginsDirName);
		}

		final File pluginsDir = new File(pluginsDirName);
		final String defExtensionsFileName = pluginsDirName + File.separatorChar + EXTENSIONS_FILE_NAME;
		final File defExtensionsFile = new File(defExtensionsFileName);

		// 1. Locate JAR files from the plugins folder

		if (pluginsDir.exists()) {
			String[] names = LpeFileUtils.getFileNames(pluginsDirName, "*.jar");

			if (names.length > 0) {
				for (String str : names) {
					final String jarName = LpeFileUtils.concatFileName(pluginsDirName, str);
					try {
						File jarFile = new File(jarName);
						URL url = jarFile.toURI().toURL();
						jarURLs.add(url);
					} catch (MalformedURLException e) {
						LOGGER.warn("Ignoring JAR file {}", jarName);
					}
				}
			}
			for (URL url : jarURLs) {
				LOGGER.debug("Found extension JAR file {}", url);
			}
		} else {
			LOGGER.debug("Could not locate the plugins folder ({}), but it is OK.", pluginsDirName);
		}

		// 2. add the 'extensions.info' of the plugins directory if it exists

		if (defExtensionsFile.exists()) {
			try {
				extensionsInfoURLs.add(new URL("file", "", defExtensionsFileName));
			} catch (MalformedURLException e1) {
				LOGGER.warn("Could not read '{}'. Reason: (MalformedURLException) {}", defExtensionsFile,
						e1.getMessage());
			}
		}

	}

	@Override
	public <EA extends IExtensionArtifact> EA getExtensionArtifact(Class<? extends IExtension<EA>> c, String name) {
		Extensions<? extends IExtension<EA>> extensions = getExtensions(c);

		for (IExtension<EA> ext : extensions) {
			if (LpeStringUtils.strEqualName(ext.getName(), name)) {
				return ext.createExtensionArtifact();
			}
		}
		LOGGER.warn("Could not find extension {} for extension category {}.", name, c.getSimpleName());

		return null;
	}

	/**
	 * Performs a simple tokenization (!) on the given source string.
	 * 
	 * @param src
	 * @param separator
	 * @return
	 */
	private static String[] tokenize(String src, String separator) {
		StringTokenizer tokenizer = new StringTokenizer(src, separator);

		String[] result = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			result[i] = tokenizer.nextToken();
			i++;
		}

		return result;
	}

}
