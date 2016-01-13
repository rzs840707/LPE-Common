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
package org.lpe.common.jmeter;

import java.io.IOException;

import org.lpe.common.jmeter.JMeterWrapper;
import org.lpe.common.jmeter.config.JMeterWorkloadConfig;


public class WrapperTest {
	
	
	public static void main(String[] args){
		
		
		final JMeterWrapper wrap = JMeterWrapper.getInstance();
		
		System.out.println("starting...");
		JMeterWorkloadConfig conf = new JMeterWorkloadConfig();
		
		/*使用JMeter进行测试和验证，直接将JMeter的地址写在了程序里*/
		conf.setPathToJMeterBinFolder("C:\\Users\\D061588\\Desktop\\apache-jmeter-2.9\\bin");
		conf.setPathToScript("C:\\Users\\D061588\\Desktop\\apache-jmeter-2.9\\scripts\\mediastore\\mediastore_usage_profile.jmx");
		
		conf.setCreateLogFlag(true);
		conf.setNumUsers(10);
		conf.setExperimentDuration(20);
		conf.setRampUpInterval(2.0);
		conf.setRampUpNumUsersPerInterval(1.0);
		conf.setCoolDownInterval(2.0);
		conf.setCoolDownNumUsersPerInterval(1.0);
		conf.setThinkTimeMinimum(200);
		conf.setThinkTimeMaximum(2000);
		
		try {
			wrap.startLoadTest(conf);
		} catch (Exception e) {	e.printStackTrace();}
		
		System.out.println("waiting...");
		
		Thread readerThread = new Thread(new Runnable(){
			
			public void run() {
					byte[] buffer = new byte[1024];
					while(true){		
						try {				
							System.out.write(buffer,0,wrap.getLogStream().read(buffer,0,1024));
						} catch (Exception e) {
							break;
						}
					}
					//read the rest if available
					try {
						while(wrap.getLogStream().available()>0){	
							try {				
								System.out.write(buffer,0,wrap.getLogStream().read(buffer,0,1024));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}			
		});
		
		readerThread.start();
		
		try {
			wrap.waitForLoadTestFinish();
			readerThread.interrupt();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done!.");
		
		
		
	}
}
