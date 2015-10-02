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
package org.lpe.common.utils.sql;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.ConstantNode;
import com.foundationdb.sql.parser.QueryTreeNode;
import com.foundationdb.sql.parser.ResultColumn;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.parser.TableName;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;

/**
 * Utility class to decide if two given SQL statements are equal.
 * 
 * @author Henning Schulz
 * 
 */
public final class SQLSimilarity {
	
	private SQLSimilarity() {
	}

	/**
	 * Returns if the given statements are equal.
	 * 
	 * @param sql1
	 *            first SQL statement
	 * @param sql2
	 *            second SQL statement
	 * @return if {@code sql1} and {@code sql2} are equal
	 */
	protected static boolean areEqual(String sql1, String sql2) {
		SQLParser parser = new SQLParser();
		StatementNode stmt1 = null;
		StatementNode stmt2 = null;
		try {
			stmt1 = parser.parseStatement(sql1);
			stmt2 = parser.parseStatement(sql2);

			SQLCompareVisitor vis1 = new SQLCompareVisitor();
			SQLCompareVisitor vis2 = new SQLCompareVisitor();
			stmt1.accept(vis1);
			stmt2.accept(vis2);
			return vis1.getHash() == vis2.getHash();
		} catch (StandardException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static class SQLCompareVisitor implements Visitor {

		private int depth = -1;
		private long hash = 0;
		private static final int PRIME = 31;
		private static final int PRIME_POWER = 31 * 31;

		@Override
		public Visitable visit(Visitable node) throws StandardException {
			if (node instanceof ConstantNode) {
				depth--;
				return node;
			}

			QueryTreeNode qtNode = (QueryTreeNode) node;
			int type = qtNode.getNodeType();
			long thisHash = depth * PRIME_POWER + type * PRIME + getAdditionalHash(qtNode);
			hash = hash * PRIME + thisHash;

			depth--;
			return node;
		}

		private long getAdditionalHash(Visitable node) {
			if (node instanceof ResultColumn) {
				ResultColumn col = ((ResultColumn) node);
				
				if (col.getName() != null) {
					return col.getName().hashCode();
				}
			} else if (node instanceof TableName) {
				TableName name = ((TableName) node);
				
				if (name.getFullTableName() != null) {
					return name.getFullTableName().hashCode();
				}
			}

			return 0;
		}

		public long getHash() {
			return hash;
		}

		@Override
		public boolean visitChildrenFirst(Visitable node) {
			depth++;
			return true;
		}

		@Override
		public boolean stopTraversal() {
			return false;
		}

		@Override
		public boolean skipChildren(Visitable node) throws StandardException {
			return false;
		}

	}

}
