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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class offers a generic view towards available extensions. It implements
 * {@link Iterable}.
 * 
 * @param <E>
 *            type of the extension
 * 
 * @author Roozbeh Farahbod
 * 
 */
public class Extensions<E extends IExtension> implements Iterable<E> {

	private final IExtensionRegistry registry;

	private final List<E> extensions;

	/**
	 * Creates an extensions view that filters extensions such that it reports
	 * only those that implement the given class (or interface).
	 * 
	 * @filter a class instance
	 */
	@SuppressWarnings("unchecked")
	protected Extensions(final Class<E> filter) {
		registry = ExtensionRegistry.getSingleton();

		// load the relevant extensions
		extensions = new ArrayList<E>();
		for (final IExtension ext : registry.getExtensions()) {
			if (filter.isAssignableFrom(ext.getClass())) {
				extensions.add((E) ext);
			}
		}
	}

	/**
	 * Returns a list of extensions that provide the desired extension
	 * interface.
	 * 
	 * @return a list of extensions that provide the desired extension
	 *         interface.
	 */
	public List<E> getList() {
		return Collections.unmodifiableList(extensions);
	}

	/**
	 * @return returns an iterator over extensions
	 */
	@Override
	public Iterator<E> iterator() {
		return extensions.iterator();
	}
}
