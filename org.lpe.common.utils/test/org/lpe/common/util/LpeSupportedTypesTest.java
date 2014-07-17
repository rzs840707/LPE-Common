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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.lpe.common.util.LpeSupportedTypes.Boolean;
import static org.lpe.common.util.LpeSupportedTypes.Double;
import static org.lpe.common.util.LpeSupportedTypes.Integer;
import static org.lpe.common.util.LpeSupportedTypes.Long;
import static org.lpe.common.util.LpeSupportedTypes.String;
import static org.lpe.common.util.LpeSupportedTypes.asList;
import static org.lpe.common.util.LpeSupportedTypes.get;
import static org.lpe.common.util.LpeSupportedTypes.getValueOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests {@link LpeSupportedTypes}.
 * 
 * @author Henning Schulz
 * 
 */
public class LpeSupportedTypesTest {

	/**
	 * Tests the {@link LpeSupportedTypes#get(Class) get(Class)} and
	 * {@link LpeSupportedTypes#get(String) get(String)}.
	 */
	@Test
	public void testGet() {
		assertEquals(LpeSupportedTypes.Double, get(Double.class));
		assertEquals(LpeSupportedTypes.Integer, get(Integer.class));
		assertEquals(LpeSupportedTypes.String, get(String.class));
		assertEquals(LpeSupportedTypes.Boolean, get(Boolean.class));
		assertEquals(LpeSupportedTypes.Long, get(Long.class));

		assertEquals(LpeSupportedTypes.Double, get("Double"));
		assertEquals(LpeSupportedTypes.Integer, get("Integer"));
		assertEquals(LpeSupportedTypes.String, get("String"));
		assertEquals(LpeSupportedTypes.Boolean, get("Boolean"));
		assertEquals(LpeSupportedTypes.Long, get("Long"));
	}

	/**
	 * Tests the {@link LpeSupportedTypes#getTypeClass() getTypeClass()}.
	 */
	@Test
	public void testGetTypeClass() {
		assertEquals(java.lang.Double.class, Double.getTypeClass());
		assertEquals(java.lang.Integer.class, Integer.getTypeClass());
		assertEquals(java.lang.String.class, String.getTypeClass());
		assertEquals(java.lang.Boolean.class, Boolean.getTypeClass());
		assertEquals(java.lang.Long.class, Long.getTypeClass());
	}

	/**
	 * Tests the
	 * {@link LpeSupportedTypes#getValueOfType(String, LpeSupportedTypes)
	 * getValueOfType(String, LpeSupportedTypes)}.
	 */
	@Test
	public void testGetValueOfType() {
		assertEquals(new java.lang.Double("0.1"), getValueOfType("0.1", Double));
		assertEquals(new java.lang.Integer(42), getValueOfType("42", Integer));
		assertEquals(new java.lang.String("Hello world"), getValueOfType("Hello world", String));
		assertEquals(new java.lang.Boolean(true), getValueOfType("true", Boolean));
		assertEquals(new java.lang.Long("12345"), getValueOfType("12345", Long));
	}

	/**
	 * Tests the {@link LpeSupportedTypes#asList() asList()}.
	 */
	@Test
	public void testAsList() {
		List<String> typesList = new ArrayList<String>();
		typesList.add("Byte");
		typesList.add("Short");
		typesList.add("Double");
		typesList.add("Float");
		typesList.add("Character");
		typesList.add("Integer");
		typesList.add("String");
		typesList.add("Boolean");
		typesList.add("Long");

		assertTrue(asList().containsAll(typesList));
	}

}
