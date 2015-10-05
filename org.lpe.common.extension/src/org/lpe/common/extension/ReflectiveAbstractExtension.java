package org.lpe.common.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public abstract class ReflectiveAbstractExtension extends AbstractExtension {

	private final Constructor<?> constructor;

	protected ReflectiveAbstractExtension(final Class<? extends IExtensionArtifact> extensionArtifactClass) {
		super(extensionArtifactClass);

		Constructor<?> foundConstructor = null;
		for (final Constructor<?> con : getExtensionArtifactClass().getConstructors()) {
			if (!Modifier.isPublic(con.getModifiers())) {
				continue;
			}
			if (con.getParameterTypes()[0] == IExtension.class) {
				if (foundConstructor != null) {
					throw new UnsupportedOperationException("Found multiple matching constructors");
				}
				foundConstructor = con;
			}
		}
		if (foundConstructor == null) {
			throw new UnsupportedOperationException("Can only instanciate extension artifacts with suited constructor");
		}
		constructor = foundConstructor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <EA extends IExtensionArtifact> EA createExtensionArtifact(final Object... constructorArgs) {
		try {
			final Object[] args = new Object[constructorArgs.length+1];
			args[0] = this;
			for (int i = 1; i <= constructorArgs.length; i++) {
				args[i] = constructorArgs[i-1];
			}
			return (EA) constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("Failed to create instance of extension artifact from extension "+getName(),e);
		}
	}

	private String displayLabelCache = null;
	
	/* (non-Javadoc)
	 * @see org.lpe.common.extension.IExtension#getDisplayLabel()
	 */
	@Override
	public String getDisplayLabel() {
		if (displayLabelCache == null) {
			final StringBuilder className = new StringBuilder(getExtensionArtifactClass().getSimpleName());
			int pos = 1;
			while (pos < className.length()) {
				if (pos + 1 < className.length() && Character.isLowerCase(className.charAt(pos)) && Character.isUpperCase(className.charAt(pos+1))) {
					className.insert(pos + 1, " ");
					pos += 2;
				}
				if (pos + 2 < className.length() && Character.isUpperCase(className.charAt(pos)) && Character.isUpperCase(className.charAt(pos+1)) && Character.isLowerCase(className.charAt(pos+2))) {
					className.insert(pos + 1, " ");
					pos += 3;
				}
				pos++;
			}
			displayLabelCache = className.toString();
		} 
		return displayLabelCache;
	}

	/* (non-Javadoc)
	 * @see org.lpe.common.extension.IExtension#getDescription()
	 */
	@Override
	public String getDescription() {
		return "No description provided.";
	}
	
	

}
