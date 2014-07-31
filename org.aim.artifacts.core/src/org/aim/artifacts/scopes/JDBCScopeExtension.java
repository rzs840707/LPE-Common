package org.aim.artifacts.scopes;

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;

public class JDBCScopeExtension extends AbstractInstApiScopeExtension {

	@Override
	public String getName() {
		return JDBCScope.class.getName();
	}

	@Override
	public AbstractInstAPIScope createExtensionArtifact() {
		return new JDBCScope(this);
	}

}
