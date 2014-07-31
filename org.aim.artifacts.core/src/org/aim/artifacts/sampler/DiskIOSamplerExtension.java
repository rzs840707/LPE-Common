package org.aim.artifacts.sampler;

import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;

public class DiskIOSamplerExtension extends AbstractSamplerExtension {

	@Override
	public String getName() {
		return DiskIOSampler.class.getName();
	}

	@Override
	public AbstractSampler createExtensionArtifact() {
		return new DiskIOSampler(this);
	}

}
