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
/**
 * 
 */
package org.lpe.common.remotecontrol.service;

import org.lpe.common.remotecontrol.RemoteControlClient;
import org.lpe.common.remotecontrol.data.FileContainer;

/**
 * @author Christoph Heger
 * 
 */
public class CommandTest {

	private static RemoteControlClient rcClient;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		rcClient = new RemoteControlClient("deqkal278.qkal.sap.corp", "8090");

		readFileTest();
		writeFileTest();
	}

	private static void startupTomcatTest() {
		rcClient.executeShellScript("/home/tpcwUser/apache/bin/startup.sh");
	}

	private static void shutdownTomcatTest() {
		rcClient.executeShellScript("/home/tpcwUser/apache/bin/shutdown.sh");
	}

	private static void readFileTest() {
		final FileContainer fContainer = rcClient.readFile("/home/tpcwUser/apache/conf/context.xml");
		/*
		InputStream in = rcClient.readFileStreamed("/home/tpcwUser/mysql-monitoring-data/mysql-slow.log");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		try {
			while((line = br.readLine()) != null) {
				count ++;
				if((count % 100) == 0) {
					System.out.println(count + ": "+line);
				}
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private static void writeFileTest() {
		final FileContainer fContainer = new FileContainer();

		fContainer.setFileName("/home/tpcwUser/Test.txt");
		fContainer.setFileContent("Don't forget to smile, it's a beautiful day.");

		rcClient.writeFile(fContainer);
	}

}
