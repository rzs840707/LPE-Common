package org.lpe.common.extension;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

class HTMLTestXMLArtifactDBMS extends AbstractExtensionArtifact {
	final String[] args;

	public HTMLTestXMLArtifactDBMS(final IExtension provider, final String ... args) {
		super(provider);
		this.args = args;
	}
}

public class TestExtension {
	
	@Test
	public void testReflectiveAbstractExtension() {		

		final IExtension ex = new ReflectiveAbstractExtension(HTMLTestXMLArtifactDBMS.class) {

		};
		
		assertEquals(HTMLTestXMLArtifactDBMS.class.getName(), ex.getName());
		assertEquals("HTML Test XML Artifact DBMS", ex.getDisplayLabel());
		assertEquals(HTMLTestXMLArtifactDBMS.class, ex.getExtensionArtifactClass());
		
		final String[] param = new String[]{"La","Le","Lu"};
		final HTMLTestXMLArtifactDBMS artifact = ex.createExtensionArtifact(new Object[]{param});
		
		assertTrue(artifact.getClass() == HTMLTestXMLArtifactDBMS.class);
		assertEquals(param, artifact.args);
		assertEquals(ex, artifact.getProvider());
	}
	
}
