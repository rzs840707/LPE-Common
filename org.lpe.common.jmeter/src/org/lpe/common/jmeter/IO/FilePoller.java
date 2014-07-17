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
package org.lpe.common.jmeter.IO;

import java.io.File;
import java.io.FileInputStream;

/**
 * This class is used for polling log-files written by other application and
 * redirects the log into an {@link DynamicPipedInputStream}.
 * 
 * @author Jonas Kunz
 */
public class FilePoller implements Runnable {

	private DynamicPipedInputStream out;
	private File file;
	private boolean continuePolling;
	private boolean deleteFileOnExit;
	private Thread pollThread;

	public static final int POLL_FREQUENCY = 300;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            The File which should be polled
	 * @param out
	 *            The {@link DynamicPipedInputStream} to which the log-data
	 *            should be output to
	 * @param deleteFileOnExit
	 *            Flag whether the polled file should be deleted after the
	 *            polling was ended
	 */
	public FilePoller(File file, DynamicPipedInputStream out, boolean deleteFileOnExit) {
		this.out = out;
		this.file = file;
		this.deleteFileOnExit = deleteFileOnExit;
	}

	/**
	 * Starts a parallel Thread which polls the File. To end the polling use
	 * {@link endPolling}
	 */
	public void startPolling() {
		continuePolling = true;
		pollThread = new Thread(this);
		pollThread.start();
	}

	/**
	 * Stops the pollingthread and deletes the file if requested before. This
	 * method waits until the polling Thread has terminated
	 * 
	 * @throws InterruptedException
	 *             if the current thread is interrupted before the pollingthread
	 *             has ended
	 */
	public void endPolling() throws InterruptedException {
		continuePolling = false;
		pollThread.interrupt();
		pollThread.join();
	}

	/**
	 * Implementation of the polling Thread - must not be called.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			int numBytes;
			FileInputStream fin = new FileInputStream(file);
			while (continuePolling) {
				while ((numBytes = fin.available()) > 0) {
					byte[] buffer = new byte[numBytes];
					fin.read(buffer);
					out.appendToBuffer(buffer);
				}
				while (fin.available() == 0 && continuePolling) {
					try {
						Thread.sleep(POLL_FREQUENCY);
					} catch (InterruptedException e) {
						if (fin != null) {
							fin.close();
						}
						throw new RuntimeException(e);
					}
				}
			}
			while ((numBytes = fin.available()) > 0) {
				byte[] buffer = new byte[numBytes];
				fin.read(buffer);
				out.appendToBuffer(buffer);
			}
			fin.close();
			if (deleteFileOnExit) {
				file.delete();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
