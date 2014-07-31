package org.aim.api.instrumentation;

import java.util.Collections;
import java.util.Set;

import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.extension.IExtension;

public abstract class AbstractEnclosingProbeExtension implements IExtension<AbstractEnclosingProbe> {

	@Override
	public String getName() {
		return getProbeClass().getName();
	}

	@Override
	public Set<ConfigParameterDescription> getConfigParameters() {
		return Collections.EMPTY_SET;
	}

	public abstract Class<? extends AbstractEnclosingProbe> getProbeClass();

}
