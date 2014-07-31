package org.aim.mainagent.probes.builder;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class DummyProbe2Extension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new DummyProbe2(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {

		return DummyProbe2.class;
	}

}
