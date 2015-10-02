package org.lpe.common.utils.sql;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.ConstantNode;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;
import com.foundationdb.sql.unparser.NodeToString;

/**
 * Utility class for generation of generalized query strings. More precisely,
 * specific constants are replaced by wildcards.
 * 
 * @author Henning Schulz
 * 
 */
public final class SQLGeneralizer {

	private SQLGeneralizer() {
	}

	/**
	 * Returns a generalized query string.
	 * 
	 * @param queryString
	 *            Query string to be generalized
	 * @return Generalization of {@code queryString}
	 */
	public static String getGeneralizedString(String queryString) {
		SQLParser parser = new SQLParser();

		try {
			StatementNode rootNode = parser.parseStatement(queryString);
			GeneralizerVisitor visitor = new GeneralizerVisitor();
			rootNode.accept(visitor);
			return new NodeToString().toString(rootNode);
		} catch (StandardException e) {
			return null;
		}
	}

	private static class GeneralizerVisitor implements Visitor {

		private static final String TOKEN_CONSTANT = "?";

		@Override
		public Visitable visit(Visitable node) throws StandardException {
			if (node instanceof ConstantNode) {
				((ConstantNode) node).setValue(TOKEN_CONSTANT);
			}

			return node;
		}

		@Override
		public boolean visitChildrenFirst(Visitable node) {
			return false;
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
