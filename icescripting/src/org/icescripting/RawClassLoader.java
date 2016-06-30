package org.icescripting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class RawClassLoader extends ClassLoader {
	private ScriptLoader loader;

	public RawClassLoader(ClassLoader parent, ScriptLoader loader) {
		super(parent);
		this.loader = loader;
	}

	public Class<?> load(String path) throws IOException {
		InputStream reader = loader.load(path);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(reader, baos);
			byte[] byteArray = baos.toByteArray();
			return defineClass(FilenameUtils.getBaseName(path), byteArray, 0, byteArray.length);
		} finally {
			reader.close();
		}

	}

}