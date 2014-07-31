package org.aim.mainagent;

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;

public class TestAPIScopeExtension extends AbstractInstApiScopeExtension {

	@Override
	public String getName() {
		return TestAPIScope.class.getName();
	}

	@Override
	public AbstractInstAPIScope createExtensionArtifact() {
		// TODO Auto-generated method stub
		return new TestAPIScope(this);
	}

}
