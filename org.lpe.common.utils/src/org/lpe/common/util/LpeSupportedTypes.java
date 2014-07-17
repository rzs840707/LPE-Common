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
package org.lpe.common.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Enumerates all types supported.
 * 
 * @author Alexander Wert
 * 
 */
public enum LpeSupportedTypes {
	Byte, Short, Double, Float, Character, Integer, String, Boolean, Long;


	/**
	 * If the given class is supported, returns the supported type for that
	 * class. Otherwise, returns null.
	 * 
	 * @param c
	 *            class for which an enum value should be created
	 * @return the enum value for the given class
	 */
	public static LpeSupportedTypes get(Class<?> c) {
		return get(c.getSimpleName());
	}

	/**
	 * If the given class name is supported, returns the supported type for that
	 * class. Otherwise, returns null.
	 * 
	 * @param name
	 *            name of the enum value
	 * @return the enum value for the given string
	 */
	public static LpeSupportedTypes get(String name) {
		if (LpeStringUtils.strEqualName(name, Byte.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Byte.TYPE.getSimpleName())) {
			return Byte;
		}
		
		if (LpeStringUtils.strEqualName(name, java.lang.Double.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Double.TYPE.getSimpleName())) {
			return Double;
		}

		if (LpeStringUtils.strEqualName(name, java.lang.Integer.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Integer.TYPE.getSimpleName())) {
			return Integer;
		}

		if (LpeStringUtils.strEqualName(name, String.class.getSimpleName())) {
			return String;
		}

		if (LpeStringUtils.strEqualName(name, Boolean.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Boolean.TYPE.getSimpleName())) {
			return Boolean;
		}

		if (LpeStringUtils.strEqualName(name, Long.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Long.TYPE.getSimpleName())) {
			return Long;
		}
		
	

		if (LpeStringUtils.strEqualName(name, java.lang.Byte.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Byte.TYPE.getSimpleName())) {
			return Byte;
		}

		if (LpeStringUtils.strEqualName(name, java.lang.Short.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Short.TYPE.getSimpleName())) {
			return Short;
		}

		if (LpeStringUtils.strEqualName(name, Float.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Float.TYPE.getSimpleName())) {
			return Float;
		}

		if (LpeStringUtils.strEqualName(name, Character.class.getSimpleName())
				|| LpeStringUtils.strEqualName(name, java.lang.Character.TYPE.getSimpleName())) {
			return Character;
		}

		return null;
	}

	/**
	 * 
	 * @return Returns all values as list
	 */
	public static List<String> asList() {
		ArrayList<String> supportedTypes = new ArrayList<String>();
		for (LpeSupportedTypes t : EnumSet.allOf(LpeSupportedTypes.class)) {
			supportedTypes.add(t.toString());
		}
		return supportedTypes;
	}

	/**
	 * 
	 * @return the class of the underlying type.
	 */
	public Class<?> getTypeClass() {
		switch (this) {
		case Boolean:
			return Boolean.class;
		case Double:
			return Double.class;
		case Integer:
			return Integer.class;
		case Long:
			return Long.class;
		case String:
			return String.class;
		case Byte:
			return Byte.class;
		case Character:
			return Character.class;
		case Float:
			return Float.class;
		case Short:
			return Short.class;

		default:
			return null;

		}
	}

	/**
	 * Returns the value of the type specified by the passed LPESupportedType.
	 * 
	 * @param value
	 *            string value to parse
	 * @param type
	 *            type to parse to
	 * @return parsed value
	 */
	public static Object getValueOfType(String value, LpeSupportedTypes type) {
		switch (type) {
		case Boolean:
			return java.lang.Boolean.parseBoolean(value);
		case Double:
			return java.lang.Double.parseDouble(value);
		case Integer:
			return java.lang.Integer.parseInt(value);
		case Long:
			return java.lang.Long.parseLong(value);
		case String:
			return value;
		case Byte:
			return java.lang.Byte.parseByte(value);
		case Character:
			return value.charAt(0);
		case Float:
			return java.lang.Float.parseFloat(value);
		case Short:
			return java.lang.Short.parseShort(value);
		default:
			return null;

		}

	}

};
