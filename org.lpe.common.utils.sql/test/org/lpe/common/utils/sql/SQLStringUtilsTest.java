package org.lpe.common.utils.sql;

import static org.junit.Assert.assertEquals;
import static org.lpe.common.utils.sql.SQLStringUtils.areEqualSql;

import org.junit.Test;

public class SQLStringUtilsTest {
	
	/**
	 * Tests the {@link LpeStringUtils#areEqualSql(String, String)
	 * areEqualSql(...)}.
	 */
	@Test
	public void testAreEqualSql() {
		final String[] sqlFrom = new String[] { "SELECT a FROM b WHERE a=1", "SELECT a FROM b WHERE a=1",
				"SELECT a FROM b WHERE a=1", "SELECT a FROM b WHERE a=1", "SELECT a, c FROM b WHERE a=1",
				"SELECT a FROM b WHERE a=b", "UPDATE t SET a = 'foo' WHERE id = 42",
				"UPDATE t SET a = 'foo' WHERE id = 42", "UPDATE t SET a = 'foo' WHERE id = 42",
				"INSERT INTO t (col) VALUES ('foo')", "INSERT INTO t (col) VALUES ('foo')",
				"INSERT INTO t (col) VALUES ('foo')", "INSERT INTO t (col) VALUES (SELECT a FROM b WHERE c=1)" };
		final String[] sqlTo = new String[] { "SELECT a FROM b WHERE a=1", "SELECT b FROM a WHERE c=1",
				"SELECT a FROM b WHERE a=5", "SELECT a FROM b WHERE 42=a", "SELECT b FROM a WHERE c=1",
				"SELECT a FROM b WHERE b=a", "UPDATE t SET a = 'bar' WHERE id = 0",
				"UPDATE s SET a = 'foo' WHERE id = 42", "UPDATE t SET b = 'foo' WHERE id = 42",
				"INSERT INTO t (col) VALUES ('bar')", "INSERT INTO s (col) VALUES ('foo')",
				"INSERT INTO t (c) VALUES ('foo')", "INSERT INTO t (col) VALUES (SELECT a FROM b WHERE a=42)" };
		final boolean[] results = new boolean[] { true, false, true, true, false, true, true, false, false, true, false,
				false, true };

		for (int i = 0; i < sqlFrom.length; i++) {
			assertEquals("<" + sqlFrom[i] + "> and <" + sqlTo[i] + "> should be "
					+ (results[i] ? "equal." : "not equal."), results[i], areEqualSql(sqlFrom[i], sqlTo[i]));
		}
	}
	
}
