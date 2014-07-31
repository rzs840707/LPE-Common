package org.aim.api.instrumentation;

import org.lpe.common.extension.AbstractExtensionArtifact;
import org.lpe.common.extension.IExtension;

public abstract class AbstractCustomScope extends AbstractExtensionArtifact implements IScopeAnalyzer {

	public AbstractCustomScope(IExtension<?> provider) {
		super(provider);
	}

}
