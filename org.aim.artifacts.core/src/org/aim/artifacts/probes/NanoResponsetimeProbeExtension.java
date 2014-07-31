package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class NanoResponsetimeProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new NanoResponsetimeProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {
		return NanoResponsetimeProbe.class;
	}

}
