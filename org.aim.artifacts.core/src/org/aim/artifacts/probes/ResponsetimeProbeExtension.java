package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class ResponsetimeProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new ResponsetimeProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {
		return ResponsetimeProbe.class;
	}

}
