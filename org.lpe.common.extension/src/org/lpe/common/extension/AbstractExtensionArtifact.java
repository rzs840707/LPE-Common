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

/**
 * The abstract class for SoPeCo extension artifacts.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public abstract class AbstractExtensionArtifact implements IExtensionArtifact {

	/**
	 * Extension provider.
	 */
	private final IExtension provider;

	/**
	 * Creates a new extension artifact with the given extension provider.
	 * 
	 * @param provider
	 *            the provider of this artifact
	 */
	public AbstractExtensionArtifact(final IExtension provider) {
		this.provider = provider;
	}

	/**
	 * @return returns the provider of this extension.
	 */
	@Override
	public IExtension getProvider() {
		return this.provider;
	}

}
