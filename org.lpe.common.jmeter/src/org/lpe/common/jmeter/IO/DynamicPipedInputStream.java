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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Works just like the standard JavaPipedInputStream, except that this implementation uses a dynamic buffer
 * in order to avoid Exception when the fixed size buffer of the standard implementation gets full. 
 * 
 * The read-methods block if no data is available.
 * 
 * TODO: Why is this class needed??? Can't you just use the Java
 * BufferedReader???
 * 
 * @author Jonas Kunz
 * 
 */
public class DynamicPipedInputStream extends InputStream {

	private static final int _0X_FF = 0xFF;
	private LinkedBlockingQueue<byte[]> buffer; // the storage
	byte[] current; // current head element
	private int currentOffset; // read-offset int the current element

	/**
	 * Constructor.
	 */
	public DynamicPipedInputStream() {
		buffer = new LinkedBlockingQueue<byte[]>();
		current = null;
		currentOffset = 0;
	}

	/**
	 * adds data to the tail of the stream.
	 * 
	 * @param data
	 *            the data top append - the data is NOT copied here, if it is
	 *            planned to be manipulated a copy should be passed here
	 */
	public void appendToBuffer(byte[] data) {
		buffer.add(data);
	}

	private void checkReadCurrent() {
		if (current != null && current.length == currentOffset) {
			current = null;
		}

		if (current == null && !buffer.isEmpty()) {
			try {
				current = buffer.take();
				currentOffset = 0;
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		}
	}

	private void waitReadCurrent() throws InterruptedException {
		if (current != null && current.length == currentOffset) {
			current = null;
		}
		if (current == null) {
			current = buffer.take();
			currentOffset = 0;
		}
	}

	@Override
	public synchronized int available() {
		checkReadCurrent();
		if (current == null) {
			return 0;
		}
		return current.length - currentOffset;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if (len == 0) {
			return 0;
		}
		try {
			waitReadCurrent();
		} catch (InterruptedException e) {
			throw new IOException("Waiting for inputdata was interrupted");
		}
		len = Math.min(len, current.length - currentOffset);
		System.arraycopy(current, currentOffset, b, off, len);
		currentOffset += len;
		return len;
	}

	@Override
	public synchronized int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public synchronized int read() throws IOException {
		try {
			waitReadCurrent();
		} catch (InterruptedException e) {
			throw new IOException("Waiting for inputdata was interrupted");
		}
		int data = current[currentOffset] & _0X_FF;
		currentOffset++;
		return data;
	}

	@Override
	public synchronized long skip(long n) {
		long bytesToSkip = n;
		int availableCount = 0;
		availableCount = available();
		while (availableCount > 0 && bytesToSkip > 0) {
			int skipCount = (int) Math.min((long) availableCount, bytesToSkip);
			currentOffset += skipCount;
			bytesToSkip -= skipCount;
			availableCount = available();
		}
		return n - bytesToSkip;
	}
}
