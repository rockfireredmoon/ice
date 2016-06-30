package org.icescripting;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

public interface ScriptLoader {
	
	boolean exists(String path);

	InputStream load(String path) throws FileNotFoundException;

	Set<String> locate(String pattern);
}
