package org.aim.api.measurement.sampling;

import java.util.Collections;
import java.util.Set;

import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.extension.IExtension;

public abstract class AbstractSamplerExtension implements IExtension<AbstractSampler> {

	@SuppressWarnings("unchecked")
	@Override
	public Set<ConfigParameterDescription> getConfigParameters() {
		return Collections.EMPTY_SET;
	}

}
