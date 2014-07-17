/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.mainagent;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for the JVMTI Event Agent. To use this agent, the CEventAgent has to
 * be started with the target application.<br>
 * 
 * Before the agent can be used, the {@link CEventAgentAdapter#initialize()
 * initialize()} method has to be called.
 * 
 * @author Henning Schulz
 * 
 */
public class CEventAgentAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CEventAgentAdapter.class);

	private static boolean initialized = false;

	private static boolean activated = false;

	/**
	 * This method is called if a thread has to wait on a monitor and monitor
	 * events are enabled.<br>
	 * 
	 * <b>This methods implementation should avoid to provoke monitor waits as
	 * they may lead to endless recursions!</b>
	 * 
	 * @param thread
	 *            Thread which has to wait
	 * @param monitor
	 *            Monitor on which {@code thread} has to wait
	 * 
	 * @see CEventAgentAdapter#enableMonitorEvents() enableMonitorEvents()
	 * @see CEventAgentAdapter#disableMonitorEvents() disableMonitorEvents()
	 */
	// TODO: raw implementation
	public static void onMonitorWait(Thread thread, Object monitor) {
		if (monitor instanceof PrintStream) {
			printlnNonBlocking("I (TestAgent) recognized: Just PrintStream monitor!");
		} else {
			StackTraceElement[] stackTrace = thread.getStackTrace();

			printlnNonBlocking("I (TestAgent) recognized: Thread " + thread.getId()
					+ " is waiting to enter a monitor of type " + monitor.getClass().getName()
					+ stackTrace[2].getMethodName() + " in line " + stackTrace[2].getLineNumber());
		}
	}

	/**
	 * This method is called if a thread enters a synchronized block, after he
	 * had to wait on the corresponding monitor.
	 * 
	 * <b>This methods implementation should avoid to provoke monitor waits as
	 * they may lead to endless recursions!</b>
	 * 
	 * @param thread
	 *            Thread which had to wait
	 * @param monitor
	 *            Monitor on which {@code thread} had to wait
	 * 
	 * @see CEventAgentAdapter#enableMonitorEvents() enableMonitorEvents()
	 * @see CEventAgentAdapter#disableMonitorEvents() disableMonitorEvents()
	 */
	// TODO: raw implementation
	public static void onMonitorEntered(Thread thread, Object monitor) {
		if (monitor instanceof PrintStream) {
			printlnNonBlocking("I (TestAgent) recognized: Just PrintStream monitor!");
		} else {
			StackTraceElement[] stackTrace = thread.getStackTrace();

			printlnNonBlocking("I (TestAgent) recognized: Thread " + thread.getId() + " has entered a monitor of type "
					+ monitor.getClass().getName() + " in method " + stackTrace[2].getMethodName() + " in line "
					+ stackTrace[2].getLineNumber());
		}
	}

	/**
	 * Initializes the underlying JVMTI agent.
	 * 
	 * @return {@code false}, if initialization failed, because the C agent
	 *         could not be found, or {@code true} otherwise.
	 */
	public static boolean initialize() {
		if (initialized) {
			LOGGER.warn("The C agent has alredy been initialized!");
		} else {
			try {
				init();
			} catch (UnsatisfiedLinkError e) {
				LOGGER.warn("The C agent could not be found!");
				return false;
			}

			initialized = true;
		}

		return true;
	}

	private static native void init();

	/**
	 * Enables listening to monitor events.
	 * 
	 * @see CEventAgentAdapter#onMonitorWait(Thread, Object)
	 *      onMonitorWait(Thread, Object)
	 * @see CEventAgentAdapter#onMonitorEntered(Thread, Object)
	 *      onMonitorEntered(Thread, Object)
	 */
	public static void enableMonitorEvents() {
		if (!initialized) {
			throw new RuntimeException("The C agent has to be initialized before using it!");
		}

		if (activated) {
			LOGGER.warn("Synchronized listening has alredy been activated!");
		} else {
			activateMonitorEvents();
			activated = true;
		}
	}

	private static native void activateMonitorEvents();

	/**
	 * Disables listening to monitor events.
	 * 
	 * @see CEventAgentAdapter#onMonitorWait(Thread, Object)
	 *      onMonitorWait(Thread, Object)
	 * @see CEventAgentAdapter#onMonitorEntered(Thread, Object)
	 *      onMonitorEntered(Thread, Object)
	 */
	public static void disableMonitorEvents() {
		if (!initialized) {
			throw new RuntimeException("The C agent has to be initialized before using it!");
		}

		if (!activated) {
			LOGGER.warn("Synchronized listening has alredy been deactivated!");
		} else {
			deactivateMonitorEvents();
			activated = false;
		}
	}

	private static native void deactivateMonitorEvents();

	/**
	 * Prints a message without the ability of monitor waits. May be used for
	 * logging in {@link CEventAgentAdapter#onMonitorWait(Thread, Object)
	 * onMonitorWait(Thread, Object)} and
	 * {@link CEventAgentAdapter#onMonitorEntered(Thread, Object)
	 * onMonitorEntered(Thread, Object)}.
	 * 
	 * @param message
	 *            Message to be printed
	 */
	private static native void printlnNonBlocking(String message);

}
