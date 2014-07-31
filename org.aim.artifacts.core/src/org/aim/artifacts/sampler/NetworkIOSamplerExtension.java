package org.aim.artifacts.sampler;

import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;

public class NetworkIOSamplerExtension extends AbstractSamplerExtension {

	@Override
	public String getName() {
		return NetworkIOSampler.class.getName();
	}

	@Override
	public AbstractSampler createExtensionArtifact() {
		return new NetworkIOSampler(this);
	}

}
