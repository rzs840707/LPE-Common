package org.aim.artifacts.sampler;

import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;

public class CPUSamplerExtension extends AbstractSamplerExtension {

	@Override
	public String getName() {
		return CPUSampler.class.getName();
	}

	@Override
	public AbstractSampler createExtensionArtifact() {
		return new CPUSampler(this);
	}


}
