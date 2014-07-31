package org.aim.artifacts.sampler;

import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;

public class MemoryUsageSamplerExtension extends AbstractSamplerExtension {

	@Override
	public String getName() {
		return MemoryUsageSampler.class.getName();
	}

	@Override
	public AbstractSampler createExtensionArtifact() {
		return new MemoryUsageSampler(this);
	}

}
