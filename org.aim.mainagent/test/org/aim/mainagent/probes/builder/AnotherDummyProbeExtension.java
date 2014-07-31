package org.aim.mainagent.probes.builder;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class AnotherDummyProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new AnotherDummyProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {
		return AnotherDummyProbe.class;
	}

}
