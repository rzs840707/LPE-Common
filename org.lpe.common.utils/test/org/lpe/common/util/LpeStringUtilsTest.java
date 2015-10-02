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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.lpe.common.util.LpeStringUtils.clearMethodName;
import static org.lpe.common.util.LpeStringUtils.concatFileName;
import static org.lpe.common.util.LpeStringUtils.convertMethodSignatureToJVMMethodDescriptor;
import static org.lpe.common.util.LpeStringUtils.correctFileSeparator;
import static org.lpe.common.util.LpeStringUtils.extendStr;
import static org.lpe.common.util.LpeStringUtils.extractClassName;
import static org.lpe.common.util.LpeStringUtils.getDetailedTimeStamp;
import static org.lpe.common.util.LpeStringUtils.getDistance;
import static org.lpe.common.util.LpeStringUtils.getDistanceCaseInsensitive;
import static org.lpe.common.util.LpeStringUtils.getPropertyOrFail;
import static org.lpe.common.util.LpeStringUtils.getSimpleMethodName;
import static org.lpe.common.util.LpeStringUtils.getTimeStamp;
import static org.lpe.common.util.LpeStringUtils.getUniqueTimeStamp;
import static org.lpe.common.util.LpeStringUtils.isClassName;
import static org.lpe.common.util.LpeStringUtils.isEmptyOrNullString;
import static org.lpe.common.util.LpeStringUtils.nextWordAfter;
import static org.lpe.common.util.LpeStringUtils.packageNameToFilePath;
import static org.lpe.common.util.LpeStringUtils.strEqualCaseInsensitive;
import static org.lpe.common.util.LpeStringUtils.strEqualName;
import static org.lpe.common.util.LpeStringUtils.tokenize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link LpeStringUtils}.
 * 
 * @author Henning Schulz
 * 
 */
public class LpeStringUtilsTest {

	private static final double DELTA = 0.01;
	private static final double PREFIX_SCALE = 0.1;

	/**
	 * Tests the {@link LpeStringUtils#correctFileSeparator(String)
	 * correctFileSeparator(String)}.
	 */
	@Test
	public void testFileSeparators() {
		final String fs = System.getProperty("file.separator");
		assertEquals("C:" + fs + "tmp" + fs + "abc", correctFileSeparator("C:/tmp/abc"));
		assertEquals("C:" + fs + "tmp" + fs + "abc", correctFileSeparator("C:\\tmp\\abc"));
	}

	/**
	 * Tests the {@link LpeStringUtils#nextWordAfter(String, String)
	 * nextWordAfter(String, String)}.
	 */
	@Test
	public void findNextWords() {
		assertEquals("world", nextWordAfter("Hello world", "Hello"));
		assertEquals("world", nextWordAfter("Hello world", "Hel"));
		assertEquals("", nextWordAfter("Hello world", "world"));
		assertEquals("", nextWordAfter("", "Hello"));
	}

	/**
	 * Tests the {@link LpeStringUtils#strEqualCaseInsensitive(String, String)
	 * strEqualCaseInsensitive(String, String)},
	 * {@link LpeStringUtils#strEqualName(String, String) strEqualName(String,
	 * String)} and {@link LpeStringUtils#isClassName(String, Class)
	 * isClassName(String, Class)}.
	 */
	@Test
	public void testEquivalencies() {
		assertTrue(strEqualCaseInsensitive("ABCDEFG", "abcdefg"));
		assertTrue(strEqualCaseInsensitive("AbCdefG", "aBcDeFg"));
		assertFalse(strEqualCaseInsensitive("ABDCEFG", "abcdefg"));

		assertTrue(strEqualName("Hans", " HANS "));
		assertFalse(strEqualName("Hans", " HANNES "));

		assertTrue(isClassName("String", "".getClass()));
		assertTrue(isClassName("string", "".getClass()));
		assertTrue(isClassName(" STRING ", "".getClass()));
	}

	/**
	 * Tests the {@link LpeStringUtils#extendStr(String, char, int)
	 * extendStr(String, char, int)}.
	 */
	@Test
	public void testExtension() {
		assertEquals("aaaaaaabcd", extendStr("bcd", 'a', 10));
		assertEquals("_____", extendStr("", '_', 5));
		assertEquals("abc", extendStr("abc", 'a', 0));
	}

	/**
	 * Tests the {@link LpeStringUtils#concatFileName(String, String)
	 * concatFileName(String, String)}.
	 */
	@Test
	public void testFileConcatenations() {
		final String fs = System.getProperty("file.separator");
		assertEquals("C:" + fs + "tmp", concatFileName("C:" + fs, "tmp"));
		assertEquals("C:" + fs + "tmp", concatFileName("C:", "tmp"));
	}

	/**
	 * Tests the {@link LpeStringUtils#isEmptyOrNullString(String)
	 * isEmptyOrNullString(String)}.
	 */
	@Test
	public void testEmptyness() {
		assertTrue(isEmptyOrNullString(""));
		assertTrue(isEmptyOrNullString(null));
		assertFalse(isEmptyOrNullString(" "));
	}

	/**
	 * Tests the {@link LpeStringUtils#tokenize(String, String) tokenize(String,
	 * String)}.
	 */
	@Test
	public void testTokenize() {
		final String[] tokens = tokenize("Hello world", " ");
		assertTrue(tokens.length == 2);
		assertEquals("Hello", tokens[0]);
		assertEquals("world", tokens[1]);
	}

	/**
	 * Tests the {@link LpeStringUtils#getTimeStamp(Date) getTimeStamp(Date)},
	 * {@link LpeStringUtils#getTimeDetailedStamp(Date)
	 * getDetailedTimeStamp(Date)}, {@link LpeStringUtils#getTimeStamp()
	 * getTimeStamp()} and {@link LpeStringUtils#getUniqueTimeStamp()
	 * getUniqueTimeStamp()}.
	 */
	@Test
	public void foolAroundWithTimeStamps() {
		assertEquals("13.11.14 - 13:25", getTimeStamp(new Date(2013, 10, 14, 13, 25, 43)));
		assertEquals("01.01.01 - 00:00", getTimeStamp(new Date(1, 0, 1, 0, 0, 0)));
		assertEquals("13.11.14 - 13:25:43:000", getDetailedTimeStamp(new Date(2013, 10, 14, 13, 25, 43)));
		assertEquals((new SimpleDateFormat("yy.MM.dd - HH:mm")).format(new Date()), getTimeStamp());
		assertTrue(getUniqueTimeStamp().matches("\\d{2}[.]\\d{2}[.]\\d{2} [-] \\d{2}[:]\\d{2}[-]\\d*"));
	}

	/**
	 * Tests the {@link LpeStringUtils#clearMethodName(String)
	 * clearMethodName(String)},
	 * {@link LpeStringUtils#getSimpleMethodName(String)
	 * getSimpleMethodName(String)},
	 * {@link LpeStringUtils#convertMethodSignatureToJVMMethodDescriptor(String)
	 * convertMethodSignatureToJVMMethodDescriptor(String)},
	 * {@link LpeStringUtils#extractClassName(String) extractClassName(String)}
	 * and {@link LpeStringUtils#packageNameToFilePath(String)
	 * packageNameToFilePath(String)}.
	 */
	@Test
	public void testCovertedMethodNames() {
		assertEquals("java.lang.String.trim()",
				clearMethodName("public synchronized void java.lang.String.trim() throws Exception"));
		assertEquals("java.lang.String.equals(java.lang.Object)",
				clearMethodName("public boolean java.lang.String.equals(java.lang.Object)"));

		assertEquals("trim", getSimpleMethodName("public synchronized void java.lang.String.trim() throws Exception"));
		assertEquals("equals", getSimpleMethodName("public boolean java.lang.String.equals(java.lang.Object)"));

		assertEquals("(I)Ljava/lang/String;",
				convertMethodSignatureToJVMMethodDescriptor("java.lang.String java.lang.String.trim(int)"));

		assertEquals("java.lang.String", extractClassName("java.lang.String java.lang.String.trim(int)"));
		assertEquals("java.lang.String", extractClassName("void java.lang.String.trim(int)"));

		assertEquals("java/lang/String", packageNameToFilePath("java.lang.String"));
	}

	/**
	 * Tests the
	 * {@link LpeStringUtils#getPropertyOrFail(Properties, String, String)
	 * getPropertyOrFail(Properties, String, String)}.
	 */
	@Test
	public void testGetPropertyOrFail() {
		final Properties props = new Properties();
		props.setProperty("one", "prop1");

		assertEquals("prop1", getPropertyOrFail(props, "one", null));
		assertEquals("prop2", getPropertyOrFail(props, "two", "prop2"));

		boolean catched = false;

		try {
			getPropertyOrFail(props, "two", null);
		} catch (final IllegalArgumentException e) {
			catched = true;
		}

		assertTrue("Getting a not existing property was not prevented.", catched);
	}

	/**
	 * Tests the {@link LpeStringUtils#getDistance(String, String)
	 * getTimeStamp(...)} and
	 * {@link LpeStringUtils#getDistanceCaseIntensitive(String, String)
	 * getDistanceCaseIntensitive(...)}.
	 */
	@Test
	public void testStringDistances() {
		final String[] source = new String[] { "ABCD", "MARTHA", "Martha", "MacMahons", "DIXON" };
		final String[] target = new String[] { "abcd", "MARHTA", "MARHTA", "McDonalds", "DICKSONX" };
		final double[] dist = new double[] { 0.0, 0.96, 0.5, 0.7, 0.81 };
		final double[] ciDist = new double[] { 1.0, 0.96, 0.96, 0.7, 0.81 };

		assertEquals(source.length, target.length);
		assertEquals(source.length, dist.length);
		assertEquals(source.length, ciDist.length);

		for (int i = 0; i < source.length; i++) {
			assertEquals(dist[i], getDistance(source[i], target[i]), DELTA);
			// getDistance should be symmetrical
			assertEquals(dist[i], getDistance(target[i], source[i]), DELTA);
			assertEquals(ciDist[i], getDistanceCaseInsensitive(source[i], target[i]), DELTA);
			// getDistanceCaseIntensitive should be symmetrical
			assertEquals(ciDist[i], getDistanceCaseInsensitive(target[i], source[i]), DELTA);
		}
	}

	/**
	 * Tests the {@link LpeStringUtils#getDistance(String, String)
	 * getDistance(...)} and
	 * {@link LpeStringUtils#getDistanceCaseIntensitive(String, String)
	 * getDistanceCaseIntensitive(...)} using generated Strings.
	 */
	@Test
	public void testStringDistancesGenerically() {
		for (int n = 10; n < 50; n++) {
			final Random rand = new Random();
			final int length1 = n;
			final int length2 = n + (rand.nextInt(n / 2));
			final double m = rand.nextInt(n);
			int l; // common prefix
			if (m == 0) {
				l = 0;
			} else {
				l = rand.nextInt(Math.min((int) m, 4));
			}

			double t;
			if (m - l < 2) {
				t = 0;
			} else {
				t = rand.nextInt(((int) m - l) / 2);
			}

			// System.out.println("[GEN]: length1 = " + length1 + ", length2 = "
			// + length2 + ", m = " + m + ", t = " + t + ", l = " + l);

			double dw;
			if (m == 0) {
				dw = 0;
			} else {
				final double dj = ((m / length1) + (m / length2) + ((m - t) / m)) / 3;
				dw = dj + ((l) * PREFIX_SCALE * (1.0 - dj));
			}

			// generate random string
			final Random charRand = new Random();
			final char[] chars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
					'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4',
					'5', '6', '7', '8', '9', '0' };
			final char[] randChars = new char[length1];
			for (int i = 0; i < length1; i++) {
				int j = charRand.nextInt(chars.length);
				while (chars[j] == '$') {
					j = charRand.nextInt(chars.length);
				}

				randChars[i] = chars[j];
				chars[j] = '$';
			}

			final String s1 = new String(randChars);

			// generate string with distance dw to s1

			// common prefix
			final char[] randChars2 = new char[length2];
			for (int i = 0; i < l; i++) {
				randChars2[i] = randChars[i];
			}

			// end of common prefix
			randChars2[l] = '$';

			// rest of matching characters
			for (int i = l + 1; i <= m; i++) {
				randChars2[i] = randChars[i - 1];
			}

			// transitions
			for (int i = l + 1; i < l + 2 * t; i = i + 2) {
				final char tmp = randChars2[i];
				randChars2[i] = randChars2[i + 1];
				randChars2[i + 1] = tmp;
			}

			// fill
			for (int i = (int) m + 1; i < length2; i++) {
				randChars2[i] = '$';
			}

			final String s2 = new String(randChars2);

			// System.out.println("[GEN]: s1 = <" + s1 + ">, s2 = <" + s2 +
			// ">");

			assertEquals(dw, getDistance(s1, s2), DELTA);
		}
	}

	@Test
	public void testWildcardsMatching() {
		final String subject = "my.test.string";
		final String patternA = "my.*.string";
		final String patternB = "*my.test.string*";
		final String patternC = "my.test.string*";
		final String patternD = "*my.test.string";
		final String patternE = "my.test.string";
		final String patternE_2 = "**test**string";
		final String patternE_3 = "**test**";
		final String patternA_2 = "my.**.string";
		final String patternB_2 = "**my.test.string*";
		final String patternC_2 = "my.test.string**";
		final String patternD_2 = "**my.test.string";
		final String patternE_4 = "my.test.string";
		final String patternE_5 = "**test**string";
		final String patternE_6 = "**test**";
		final String patternE_7 = "*";

		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternA));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternB));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternC));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternD));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE_2));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE_3));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternA_2));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternB_2));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternC_2));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternD_2));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE_4));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE_5));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE_6));
		Assert.assertTrue(LpeStringUtils.patternMatches(subject, patternE_7));

		final String patternF = "my.test.string.something";
		final String patternG = "my.test.string*something";
		final String patternH = "my.*x*.string";
		final String patternI = "test*string";
		final String patternJ = "my*test";
		final String patternK = "my.*x*.str";
		final String patternL = "";
		final String patternM = "string";

		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternF));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternG));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternH));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternI));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternJ));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternK));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternL));
		Assert.assertFalse(LpeStringUtils.patternMatches(subject, patternM));

	}
	
	@Test
	public void testWildcardsPrefixMatching() {
		final String subject = "my.test";
		final String patternA = "my.*.string";
		final String patternB = "*my.test.string*";
		final String patternC = "my.test.string*";
		final String patternD = "*my.test.string";
		final String patternE = "my.test.string";
		final String patternE_2 = "**test**string";
		final String patternE_3 = "**test**";
		final String patternA_2 = "my.**.string";
		final String patternB_2 = "**my.test.string*";
		final String patternC_2 = "my.test.string**";
		final String patternD_2 = "**my.test.string";
		final String patternE_4 = "my.test.string";
		final String patternE_5 = "**test**string";
		final String patternE_6 = "**test**";
		final String patternE_7 = "*";

		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternA));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternB));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternC));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternD));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE_2));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE_3));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternA_2));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternB_2));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternC_2));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternD_2));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE_4));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE_5));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE_6));
		Assert.assertTrue(LpeStringUtils.patternPrefixMatches(subject, patternE_7));

		final String patternF = "test.string";
		final String patternH = "my..*x*.string";
		final String patternI = "test*string";
		final String patternJ = "my";
		final String patternL = "";
		final String patternM = "string";

		Assert.assertFalse(LpeStringUtils.patternPrefixMatches(subject, patternF));
		Assert.assertFalse(LpeStringUtils.patternPrefixMatches(subject, patternH));
		Assert.assertFalse(LpeStringUtils.patternPrefixMatches(subject, patternI));
		Assert.assertFalse(LpeStringUtils.patternPrefixMatches(subject, patternJ));
		Assert.assertFalse(LpeStringUtils.patternPrefixMatches(subject, patternL));
		Assert.assertFalse(LpeStringUtils.patternPrefixMatches(subject, patternM));

	}
}
