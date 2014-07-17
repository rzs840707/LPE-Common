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
package org.lpe.common.util.dummy;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author C5170547
 * 
 */
@Path("dummy")
public class DummyService {
	public static AtomicInteger queue = new AtomicInteger(0);
	public static AtomicInteger maxQueue = new AtomicInteger(0);

	/**
	 * test.
	 * 
	 * @return string
	 */
	@GET
	@Path("service")
	@Produces(MediaType.APPLICATION_JSON)
	public String service() {
		queue.incrementAndGet();

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized (this) {
			if (queue.get() > maxQueue.get()) {
				maxQueue.set(queue.get());
			}
		}
		queue.decrementAndGet();

		return "hallo";
	}
}
