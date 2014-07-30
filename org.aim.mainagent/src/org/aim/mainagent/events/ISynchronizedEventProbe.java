package org.aim.mainagent.events;

public interface ISynchronizedEventProbe extends IEventProbe {
	
	public void setThread(Thread thread);
	
	public void setMonitor(Object monitor);
	
	public void setWaitStartTime(long timestamp);
	
	public void setEnteredTime(long timestamp);
	
}
