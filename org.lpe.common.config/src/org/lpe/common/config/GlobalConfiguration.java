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
package org.lpe.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This configuration class provides a singleton wrapper around a properties
 * object, which can be used in different projects for a global (application
 * wide) configuration.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class GlobalConfiguration {
	private static GlobalConfiguration instance;

	/**
	 * Core properties need to be loaded once and never changed during a running
	 * Spotter Service
	 */
	private static Properties coreProperties;
	private static boolean initialized = false;

	/**
	 * Creates the global singleton configuration and loads the initial
	 * properties from the passed file.
	 * 
	 * @param coreConfigFile
	 *            path to the file containing initial configurations for the
	 *            underlying service
	 * 
	 * @param projectConfigFile
	 *            path to the file containing initial configurations for the
	 *            project
	 */
	public static void initialize(String coreConfigFile,
			String projectConfigFile) {
		Properties projectProperties = getPropertiesFromFile(projectConfigFile);
		Properties coreProperties = getPropertiesFromFile(coreConfigFile);
		initialize(coreProperties, projectProperties);
	}

	/**
	 * Creates the global singleton configuration and loads the initial
	 * properties from the passed file.
	 * 
	 * @param coreConfigFile
	 *            path to the file containing initial configurations for the
	 *            underlying service
	 */
	public static void initialize(String coreConfigFile) {
		Properties coreProperties = getPropertiesFromFile(coreConfigFile);
		initialize(coreProperties);
	}

	/**
	 * Creates the global singleton configuration and loads the initial
	 * properties from the passed file.
	 * 
	 * @param coreProperties
	 *            properties containing initial configurations for the
	 *            underlying service
	 */
	public static void initialize(Properties coreProperties) {
		initialize(coreProperties, null);
	}

	/**
	 * Creates the global singleton configuration and loads the initial
	 * properties from the passed file.
	 * 
	 * @param coreProperties
	 *            properties containing initial configurations for the
	 *            underlying service
	 * 
	 * @param projectProperties
	 *            properties containing initial configurations for the project
	 */
	public static void initialize(Properties coreProperties,
			Properties projectProperties) {
		if (!initialized) {
			GlobalConfiguration.coreProperties = new Properties();
			GlobalConfiguration.coreProperties.putAll(coreProperties);
			instance = new GlobalConfiguration(coreProperties,
					projectProperties);
			initialized = true;
		}
	}

	/**
	 * Creates the global singleton configuration and loads the initial
	 * properties from the passed file.
	 * 
	 * @param projectConfigFile
	 *            path to the file containing initial configurations
	 */
	public static void reinitialize(String projectConfigFile) {
		Properties projectProperties = getPropertiesFromFile(projectConfigFile);
		reinitialize(projectProperties);
	}

	/**
	 * Creates the global singleton configuration and loads the project
	 * properties from the passed file.
	 * 
	 * @param projectProperties
	 *            properties containing project configurations
	 */
	public static void reinitialize(Properties projectProperties) {
		if (!initialized) {
			throw new IllegalStateException(
					"Global configuration has not been initialized, yet.");
		}
		instance = new GlobalConfiguration(coreProperties, projectProperties);
	}

	/**
	 * 
	 * @return Returns the singleton global configuration.
	 */
	public static GlobalConfiguration getInstance() {
		if (instance == null) {
			throw new RuntimeException(
					"Configuration has not been in itialized!");
		}
		return instance;
	}

	private static Properties getPropertiesFromFile(String configFile) {
		try {
			File propertiesFile = new File(configFile);
			if (!propertiesFile.exists()) {
				throw new IllegalStateException(
						"Specified configuration file does not exist!");
			}

			Properties properties = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(propertiesFile);
				properties.load(fis);
			} catch (IOException e) {
				throw new RuntimeException("Failed loading configuration!", e);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
			return properties;
		} catch (IOException e) {
			throw new RuntimeException("Failed loading configuration!", e);
		}

	}

	/** Holds the configured property values. */
	private Properties properties;

	private GlobalConfiguration(Properties coreProperties,
			Properties projectProperties) {
		this.properties = new Properties();
		this.properties.putAll(coreProperties);
		if (projectProperties != null) {
			this.properties.putAll(projectProperties);
		}

	}

	/**
	 * Indicates whether a property with the passed key is available or not.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @return true, if property is available, otherwise false
	 */
	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}

	/**
	 * Returns the property for the passed key.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @return Returns the property for the passed key.
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found the default value is returned.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @param defaultValue
	 *            default value to use if property cannot be found
	 * @return Returns the property for the passed key.
	 */
	public String getProperty(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found an IllegalArgumentException is thrown.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @return Returns the property for the passed key.
	 * 
	 */
	public boolean getPropertyAsBoolean(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new IllegalArgumentException("Property " + key
					+ " not found.");
		} else {
			return Boolean.parseBoolean(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found the default value is returned.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @param defaultValue
	 *            default value to use if property cannot be found
	 * @return Returns the property for the passed key.
	 */
	public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found an IllegalArgumentException is thrown.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @return Returns the property for the passed key.
	 * 
	 */
	public int getPropertyAsInteger(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new IllegalArgumentException("Property " + key
					+ " not found.");
		} else {
			return Integer.parseInt(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found the default value is returned.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @param defaultValue
	 *            default value to use if property cannot be found
	 * @return Returns the property for the passed key.
	 */
	public int getPropertyAsInteger(String key, int defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found an IllegalArgumentException is thrown.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @return Returns the property for the passed key.
	 * 
	 */
	public long getPropertyAsLong(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new IllegalArgumentException("Property " + key
					+ " not found.");
		} else {
			return Long.parseLong(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found the default value is returned.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @param defaultValue
	 *            default value to use if property cannot be found
	 * @return Returns the property for the passed key.
	 */
	public long getPropertyAsLong(String key, long defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found an IllegalArgumentException is thrown.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @return Returns the property for the passed key.
	 * 
	 */
	public double getPropertyAsDouble(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new IllegalArgumentException("Property " + key
					+ " not found.");
		} else {
			return Double.parseDouble(value);
		}
	}

	/**
	 * Returns the property for the passed key. If a property for the passed key
	 * cannot be found the default value is returned.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @param defaultValue
	 *            default value to use if property cannot be found
	 * @return Returns the property for the passed key.
	 */
	public double getPropertyAsDouble(String key, double defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Double.parseDouble(value);
		}
	}

	/**
	 * Inserts or overwrites a property for the passed key with the given value.
	 * 
	 * @param key
	 *            key of the property of interest
	 * @param value
	 *            value of the property to be set
	 */
	public void putProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Inserts or overwrites all properties.
	 * 
	 * @param properties
	 *            properties to add
	 */
	public void putAll(Properties properties) {
		this.properties.putAll(properties);
	}

	/**
	 * 
	 * @return Returns a copy of the properties.
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * clears all properties.
	 */
	public void clear() {
		properties.clear();
	}

}
