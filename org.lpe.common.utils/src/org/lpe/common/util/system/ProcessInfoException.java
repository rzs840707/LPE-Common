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
package org.lpe.common.util.system;

import org.slf4j.Logger;

/**
 * Represents exceptions regarding process information retrieval.
 * 
 * @author Roozbeh Farahbod
 * 
 */
public class ProcessInfoException extends SystemToolsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *            message
	 * @param logger
	 *            logger
	 */
	public ProcessInfoException(String msg, Logger logger) {
		super(msg, logger);
	}

	/**
	 * Constructor.
	 * 
	 * @param throwable
	 *            cause
	 */
	public ProcessInfoException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *            message
	 * @param throwable
	 *            cause
	 * @param logger
	 *            logger
	 */
	public ProcessInfoException(String msg, Throwable throwable, Logger logger) {
		super(msg, throwable, logger);
	}

}
