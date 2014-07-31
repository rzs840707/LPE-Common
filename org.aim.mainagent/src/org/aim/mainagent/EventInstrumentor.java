package org.aim.mainagent;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.description.InstrumentationDescription;
import org.aim.mainagent.events.EventProbeRegistry;
import org.aim.mainagent.events.SynchonizedBlocksWaitingTimeProbe;
import org.aim.mainagent.events.SynchronizedEventListener;

public class EventInstrumentor implements IInstrumentor {

	private static EventInstrumentor instance;

	@Override
	public void instrument(InstrumentationDescription descr) throws InstrumentationException {
		CEventAgentAdapter.setSynchronizedListener(SynchronizedEventListener.getInstance());
		//TODO: Change to select Scope and EventProbe from descr
		EventProbeRegistry.getInstance().addProbe(SynchronizedEventListener.class, SynchonizedBlocksWaitingTimeProbe.class);
		CEventAgentAdapter.enableMonitorEvents();
	}

	@Override
	public void undoInstrumentation() throws InstrumentationException {
		CEventAgentAdapter.disableMonitorEvents();
	}

	public static EventInstrumentor getInstance() {
		if (instance == null) {
			instance = new EventInstrumentor();
		}

		return instance;
	}

}
