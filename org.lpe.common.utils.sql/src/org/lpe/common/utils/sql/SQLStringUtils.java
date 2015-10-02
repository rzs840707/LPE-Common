package org.lpe.common.utils.sql;

public final class SQLStringUtils {

	/**
	 * Returns if the given statements are equal.
	 * 
	 * @param sql1
	 *            first SQL statement
	 * @param sql2
	 *            second SQL statement
	 * @return if {@code sql1} and {@code sql2} are equal
	 */
	public static boolean areEqualSql(final String sql1, final String sql2) {
		return SQLSimilarity.areEqual(sql1, sql2);
	}

	/**
	 * Returns a generalized query string. More precisely, specific constants
	 * are replaced by wildcards.
	 * 
	 * @param queryString
	 *            Query string to be generalized
	 * @return Generalization of {@code queryString}
	 */
	public static String getGeneralizedQuery(final String queryString) {
		if (queryString == null || queryString.contains("?")) {
			return queryString;
		}

		return SQLGeneralizer.getGeneralizedString(queryString);
	}

}
