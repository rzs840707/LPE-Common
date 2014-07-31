package org.aim.artifacts.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;

public class SQLQueryProbeExtension extends AbstractEnclosingProbeExtension {

	@Override
	public AbstractEnclosingProbe createExtensionArtifact() {
		return new SQLQueryProbe(this);
	}

	@Override
	public Class<? extends AbstractEnclosingProbe> getProbeClass() {
		return SQLQueryProbe.class;
	}

}
