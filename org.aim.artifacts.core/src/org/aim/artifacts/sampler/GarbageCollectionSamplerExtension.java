package org.aim.artifacts.sampler;

import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;

public class GarbageCollectionSamplerExtension extends AbstractSamplerExtension {

	@Override
	public String getName() {
		return GarbageCollectionSampler.class.getName();
	}

	@Override
	public AbstractSampler createExtensionArtifact() {
		return new GarbageCollectionSampler(this);
	}

}
