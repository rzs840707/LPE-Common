package org.lpe.common.extension;

import java.util.LinkedHashSet;
import java.util.Set;

import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.util.LpeSupportedTypes;

public abstract class AbstractExtension implements IExtension {

	private final Class<? extends IExtensionArtifact> extensionArtifactClass;
	
	/**
	 * The set contains all the configuration for this extension.
	 */
	private final Set<ConfigParameterDescription> configParameters;

	protected AbstractExtension(final Class<? extends IExtensionArtifact> extensionArtifactClass) {
		super();
		this.configParameters = new LinkedHashSet<ConfigParameterDescription>();
		this.extensionArtifactClass = extensionArtifactClass;
		
		createExtensionDescriptionParameter();
	}

	private void createExtensionDescriptionParameter() {
		createExtensionDescription(ConfigParameterDescription.EXT_DESCRIPTION_KEY, getDescription());
		createExtensionDescription(ConfigParameterDescription.EXT_LABEL_KEY, getDisplayLabel());
	}

	@Override
	public final String getName() {
		return getExtensionArtifactClass().getName();
	}

	@Override
	public final Class<? extends IExtensionArtifact> getExtensionArtifactClass() {
		return this.extensionArtifactClass;
	}

	/**
	 * Adds a configuration parameter to the extension.
	 * 
	 * @param parameter
	 *            parameter to add to this extension
	 */
	protected void addConfigParameter(final ConfigParameterDescription parameter) {
		configParameters.add(parameter);
	}

	@Override
	public final Set<ConfigParameterDescription> getConfigParameters() {
		return configParameters;
	}
	
	/**
	 * Convenience method to create a non-editable extension description with
	 * the given text.
	 * 
	 * @param descriptionText
	 *            text describing the extension itself
	 * @return the created extension description
	 */
	void createExtensionDescription(final String key, final String descriptionText) {
		final ConfigParameterDescription extensionDescription = new ConfigParameterDescription(key,
				LpeSupportedTypes.String);
		extensionDescription.setDefaultValue(descriptionText);
		extensionDescription.setEditable(false);
		addConfigParameter(extensionDescription);
	}

}
