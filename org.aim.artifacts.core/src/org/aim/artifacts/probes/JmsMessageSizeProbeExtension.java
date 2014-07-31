package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class JmsMessageSizeProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new JmsMessageSizeProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {
		return JmsMessageSizeProbe.class;
	}

}
