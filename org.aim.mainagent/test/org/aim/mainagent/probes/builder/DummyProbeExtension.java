package org.aim.mainagent.probes.builder;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class DummyProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new DummyProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {

		return DummyProbe.class;
	}

}
