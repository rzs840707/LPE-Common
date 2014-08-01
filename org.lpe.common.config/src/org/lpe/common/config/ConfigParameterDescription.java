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

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.lpe.common.util.LpeSupportedTypes;

/**
 * An instance of that class describes a configuration parameter. The
 * description contains a name, configuration parameter type and possible values
 * for the parameter.
 * 
 * @author Alexander Wert
 * 
 */
// @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include =
// JsonTypeInfo.As.PROPERTY, property = "class")
public class ConfigParameterDescription {
	public static final String LIST_VALUE_SEPARATOR = ",";
	public static final String EXT_DESCRIPTION_KEY = "extension.description";
	private String name;
	private String description;
	private String defaultValue;
	private LpeSupportedTypes type;
	private boolean aset;
	private Set<String> options;
	private String[] fileExtensions;
	private String fileDefaultName;
	private String lowerBoundary;
	private String upperBoundary;
	private boolean directory;
	private boolean file;
	private boolean mandatory = false;
	private boolean editable = true;

	/**
	 * Default constructor.
	 */
	public ConfigParameterDescription() {

	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            configuration parameter name
	 * @param type
	 *            type of the parameter
	 */
	public ConfigParameterDescription(String name, LpeSupportedTypes type) {
		this.name = name;
		this.type = type;
		setIsASet(false);
	}

	/**
	 * Convenience method to create a non-editable extension description with
	 * the given text.
	 * 
	 * @param descriptionText
	 *            text describing the extension itself
	 * @return the created extension description
	 */
	public static ConfigParameterDescription createExtensionDescription(String descriptionText) {
		ConfigParameterDescription extensionDescription = new ConfigParameterDescription(EXT_DESCRIPTION_KEY,
				LpeSupportedTypes.String);
		extensionDescription.setDefaultValue(descriptionText);
		extensionDescription.setEditable(false);
		return extensionDescription;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public LpeSupportedTypes getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(LpeSupportedTypes type) {
		this.type = type;
	}

	/**
	 * Returns possible options for that configuration parameter. If the
	 * returned set is empty all possible values (within the ranges of the
	 * corresponding parameter type) are valid
	 * 
	 * @return the options
	 */
	public Set<String> getOptions() {
		if (options == null) {
			options = new HashSet<String>();
		}
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(Set<String> options) {
		if (rangeAvailable()) {
			throw new IllegalStateException("Cannot define options if a value range have been defined!");
		}
		this.options = options;
	}

	/**
	 * Returns the set file extensions. <code>null</code> possible.
	 * 
	 * @return the file extensions, <code>null</code> possible
	 */
	public String[] getFileExtensions() {
		return fileExtensions;
	}

	/**
	 * Sets the file extensions. Follow the rules of
	 * org.eclipse.swt.widgets.FileDialog.setFilterExtensions(...).
	 * 
	 * @param fileExtensions
	 *            the allowed file extensions
	 */
	public void setFileExtensions(String[] fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConfigParameterDescription other = (ConfigParameterDescription) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns the default file name.
	 * 
	 * @return the default file name
	 */
	public String getDefaultFileName() {
		return fileDefaultName;
	}

	/**
	 * The default file name value is only relevant for the dialog popping up to
	 * select a specific file. It is not the default value for the whole
	 * parameter!
	 * 
	 * @param fileDefaultName
	 *            the default value for the file
	 */
	public void setDefaultFileName(String fileDefaultName) {
		this.fileDefaultName = fileDefaultName;
	}

	/**
	 * @return true, if value range has been specified
	 */
	@JsonIgnore
	public boolean rangeAvailable() {
		return lowerBoundary != null && upperBoundary != null;
	}

	/**
	 * 
	 * @return true if value options have been specified
	 */
	@JsonIgnore
	public boolean optionsAvailable() {
		return getOptions() != null && !getOptions().isEmpty();
	}

	/**
	 * 
	 * @return range lower boundary
	 */
	public String getLowerBoundary() {
		return lowerBoundary;
	}

	/**
	 * Sets the range. The range can only be set if options have not been set,
	 * yet.
	 * 
	 * @param lowerBoundary
	 *            lower range boundary
	 * @param upperBoundary
	 *            upper range boundary
	 */
	@JsonIgnore
	public void setRange(String lowerBoundary, String upperBoundary) {
		if (optionsAvailable()) {
			throw new IllegalStateException("Cannot define a range if value options have been defined!");
		}
		this.lowerBoundary = lowerBoundary;
		this.upperBoundary = upperBoundary;

	}

	/**
	 * 
	 * @return range upper boundary
	 */
	public String getUpperBoundary() {
		return upperBoundary;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory
	 *            the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable
	 *            the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	@JsonIgnore
	public String toString() {
		return "ConfigParameterDescription [name=" + name + ", type=" + type + "]";
	}

	/**
	 * @return the isDirectory
	 */
	public boolean isDirectory() {
		if (type != null && !type.equals(LpeSupportedTypes.String)) {
			return false;
		}
		return directory;
	}

	/**
	 * @param isDirectory
	 *            the isDirectory to set
	 */
	public void setDirectory(boolean isDirectory) {
		if (type != null && !type.equals(LpeSupportedTypes.String) && isDirectory) {
			throw new IllegalStateException("A non String parameter cannot be a directory!");
		}
		this.directory = isDirectory;
	}

	/**
	 * 
	 * @return true, if the parameter is a file parameter
	 */
	public boolean isAFile() {
		return file;
	}

	/**
	 * Indicates whether the parameter should be a file parameter.
	 * 
	 * @param file
	 *            boolean to set
	 */
	public void setIsAFile(boolean file) {
		this.file = file;
	}

	/**
	 * 
	 * @return true, if the parameter is a set parameter
	 */
	public boolean isASet() {
		return aset;
	}

	/**
	 * Indicates whether the parameter should be a set parameter.
	 * 
	 * @param aset
	 *            boolean to set
	 */
	public void setIsASet(boolean aset) {
		this.aset = aset;
	}

}
