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
 * @param <EA>
 *            Type of the extension artifact
 * @author Roozbeh Farahbod
 * 
 */
public interface IExtension<EA extends IExtensionArtifact> {

	/**
	 * Returns the name of the extension which is expected to be unique in the
	 * framework.
	 * 
	 * The name is expected to be specific to the extension that is provided,
	 * for example 'MARS' or 'GP'.
	 * 
	 * @return the name of this extension
	 */
	String getName();

	/**
	 * Creates a new artifact for this extension.
	 * 
	 * @return a new artifact for this extension.
	 */
	EA createExtensionArtifact();

	/**
	 * Returns a set of configuration parameter descriptions.
	 * 
	 * @return a set of configuration parameter descriptions
	 */
	Set<ConfigParameterDescription> getConfigParameters();
}
