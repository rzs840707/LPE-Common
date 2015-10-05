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
package org.lpe.common.extension;

import java.util.Set;

import org.lpe.common.config.ConfigParameterDescription;

/**
 * This is root interface of all LPE extensions.
 * 
 * @author Roozbeh Farahbod, Steffen Becker
 * 
 */
public interface IExtension {

	/**
	 * Returns the name of the extension which is expected to be unique in the
	 * framework. 
	 * 
	 * The name is expected to be specific to the extension that is provided,
	 * for example 'MARS' or 'GP'.
	 * 
	 * Commonly this returns the FQClassName of the Extension Artifact created 
	 * by this extension.
	 * 
	 * @return the name of this extension
	 */
	String getName();
	
	/**
	 * Get a short label for this extension which can be used for example in UIs.
	 * @return The display label
	 */
	String getDisplayLabel();
	
	/** Get a user friendly description of this extension
	 * @return A description of this extension.
	 */
	String getDescription();

	/**
	 * Creates a new artifact for this extension.
	 * @param constructorArgs Optional arguments to be passed to the constructor of the extension artifact
	 * 
	 * @return a new artifact for this extension.
	 */
	<EA extends IExtensionArtifact> EA createExtensionArtifact(Object ... constructorArgs);

	/**
	 * Returns a set of configuration parameter descriptions.
	 * 
	 * @return a set of configuration parameter descriptions
	 */
	Set<ConfigParameterDescription> getConfigParameters();
	
	/**
	 * @return The class of the extension artifact this extension can generate
	 */
	Class<? extends IExtensionArtifact> getExtensionArtifactClass();
}
