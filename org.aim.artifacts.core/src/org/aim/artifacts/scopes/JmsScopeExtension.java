package org.aim.artifacts.scopes;

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;

public class JmsScopeExtension extends AbstractInstApiScopeExtension {

	@Override
	public String getName() {
		return JmsScope.class.getName();
	}

	@Override
	public AbstractInstAPIScope createExtensionArtifact() {
		return new JmsScope(this);
	}

}
