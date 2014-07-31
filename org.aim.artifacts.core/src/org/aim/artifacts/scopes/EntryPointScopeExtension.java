package org.aim.artifacts.scopes;

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;

public class EntryPointScopeExtension extends AbstractInstApiScopeExtension {

	@Override
	public String getName() {
		return EntryPointScope.class.getName();
	}

	@Override
	public AbstractInstAPIScope createExtensionArtifact() {
		return new EntryPointScope(this);
	}

}
