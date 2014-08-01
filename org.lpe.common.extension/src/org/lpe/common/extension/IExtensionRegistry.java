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

import java.util.Collection;

/**
 * The interface of the extension registry.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public interface IExtensionRegistry {

	/**
	 * Returns a collection of all registered extensions.
	 * 
	 * @return Collection of supported extensions
	 */
	Collection<? extends IExtension<?>> getExtensions();

	/**
	 * Returns an extensions object that filters in only those extensions that
	 * support the extension type.
	 * 
	 * @param c
	 *            the extension class
	 * @param <E>
	 *            type of the extension to be retrieved
	 * @see Extensions
	 * 
	 * @return an extensions object that filters in only those extensions that
	 *         support the extension type.
	 */
	<E extends IExtension<?>> Extensions<E> getExtensions(Class<E> c);

	/**
	 * Assuming that the extension names are unique for any category of PPD
	 * extensions, this method returns an extension artifact from the extension
	 * identified by the extension type.
	 * 
	 * @param c
	 *            the extension class
	 * @param name
	 *            name of the extension
	 * @param <EA>
	 *            type of the extension artefact to be retrieved
	 * @return an extension artifact produced by the extension
	 */
	<EA extends IExtensionArtifact> EA getExtensionArtifact(Class<? extends IExtension<EA>> c, String name);

	/**
	 * Adds a new extension to the registry.
	 * 
	 * It is assumed that extensions have unique names.
	 * 
	 * @param ext
	 *            an instantiated extension
	 */
	void addExtension(IExtension<?> ext);

	/**
	 * Removes the extension with the given name from the registry.
	 * 
	 * @param name
	 *            extension name
	 */
	void removeExtension(String name);

	/**
	 * 
	 * @param name
	 *            name of the extension
	 * @return extension for the given name
	 */
	IExtension<?> getExtension(String name);

}
