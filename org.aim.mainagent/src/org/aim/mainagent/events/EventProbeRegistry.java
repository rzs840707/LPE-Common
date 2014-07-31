package org.aim.mainagent.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventProbeRegistry {

	private static EventProbeRegistry instance;

	private final Map<Class<? extends IEventListener<?>>, List<Class<? extends IEventProbe>>> activatedProbes = new HashMap<>();
	
	private EventProbeRegistry() {
	}

	public <P extends IEventProbe> void addProbe(Class<? extends IEventListener<? super P>> listenerClass, Class<P> probeClass) {
		List<Class<? extends IEventProbe>> probeList = activatedProbes.get(listenerClass);
		if (probeList == null) {
			probeList = new ArrayList<>();
			activatedProbes.put(listenerClass, probeList);
		}

		probeList.add(probeClass);
	}

	@SuppressWarnings("unchecked")
	public <P extends IEventProbe> List<Class<? extends P>> getProbeClasses(Class<? extends IEventListener<? super P>> listenerClass) {
		List<Class<? extends IEventProbe>> probeClassList = activatedProbes.get(listenerClass);
		if (probeClassList == null) {
			return null;
		}
		
		List<Class<? extends P>> castedProbeClassList = new ArrayList<>();
		
		for (Class<? extends IEventProbe> pc : probeClassList) {
			castedProbeClassList.add((Class<? extends P>) pc);
		}

		return (List<Class<? extends P>>) castedProbeClassList;
	}

	public static EventProbeRegistry getInstance() {
		if (instance == null) {
			instance = new EventProbeRegistry();
		}

		return instance;
	}

}
