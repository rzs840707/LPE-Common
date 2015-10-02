package org.lpe.common.extension;

public abstract class AbstractExtension implements IExtension {

	private final Class<? extends IExtensionArtifact> extensionArtifactClass;

	protected AbstractExtension(final Class<? extends IExtensionArtifact> extensionArtifactClass) {
		super();
		this.extensionArtifactClass = extensionArtifactClass;
	}

	@Override
	public final String getName() {
		return getExtensionArtifactClass().getName();
	}

	@Override
	public final Class<? extends IExtensionArtifact> getExtensionArtifactClass() {
		return this.extensionArtifactClass;
	}

}
