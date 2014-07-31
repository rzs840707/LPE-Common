package org.aim.api.instrumentation;

import java.util.Collections;
import java.util.Set;

import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.extension.IExtension;

public abstract class AbstractCustomScopeExtension implements IExtension<AbstractCustomScope> {

	@SuppressWarnings("unchecked")
	@Override
	public Set<ConfigParameterDescription> getConfigParameters() {
		return Collections.EMPTY_SET;
	}

}
