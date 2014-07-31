package org.aim.mainagent.probes.builder;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class InvalidDummyProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new InvalidDummyProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {

		return InvalidDummyProbe.class;
	}

}
