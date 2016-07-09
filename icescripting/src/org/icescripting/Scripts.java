package org.icescripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.FilenameUtils;
import org.icelib.Icelib;
import org.icescripting.squirrel.BuiltIn;
import org.icesquirrel.jsr223.BindingsAdapter;
//import org.icesquirrel.runtime.SquirrelTable;

import org.icesquirrel.runtime.SquirrelExecutionContext;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class Scripts {
	// extends SquirrelTable {
	final static Logger LOG = Logger.getLogger(Scripts.class.getName());

	private ScriptEngineManager manager;
	private ScriptLoader loader;
	private Properties properties = new Properties();
	private RawClassLoader classLoader;
	private Map<String, CompiledScript> scriptCache = new HashMap<>();
	private List<String> extensions = new ArrayList<>();
	private static Scripts instance;
	private Set<String> loaded = new LinkedHashSet<>();
	private ThreadLocal<List<String>> currentPath = new ThreadLocal<>();

	public static Scripts get() {
		return instance;
	}

	public Scripts(ScriptLoader loader) throws Exception {
		if (instance != null) {
			throw new IllegalStateException("May only create one " + Scripts.class + " instance.");
		}
		instance = this;
		this.loader = loader;
		manager = new ScriptEngineManager();
		// Bindings bindings = new BindingsAdapter(this);
		Bindings bindings = new SimpleBindings();
		bindings.put("_ctx", this);
		manager.setBindings(bindings);

		configureManager(manager);

		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		if (parent == null) {
			parent = Scripts.class.getClassLoader();
		}
		classLoader = new RawClassLoader(parent, loader);

		for (Enumeration<URL> urlE = Scripts.class.getClassLoader().getResources("META-INF/script-to-bytecode.properties"); urlE
				.hasMoreElements();) {
			Properties t = new Properties();
			InputStream in = urlE.nextElement().openStream();
			try {
				t.load(in);
			} finally {
				in.close();
			}
			properties.putAll(t);
		}

		for (ScriptEngineFactory f : manager.getEngineFactories()) {
			LOG.info(String.format("Supports %s %s scripts (via %s %s), using extensions %s", f.getLanguageName(),
					f.getLanguageVersion(), f.getEngineName(), f.getEngineVersion(), f.getExtensions()));
			extensions.addAll(f.getExtensions());
		}
	}

	private void configureManager(ScriptEngineManager manager) {
		// Some JME3 classes that can be used in configuration scripts
		// manager.put("ColorRGBA", ColorRGBA.class);
		// manager.put("Vector3", Vector3f.class);
		// manager.put("Vector2", Vector2f.class);

		try {
			manager.put("Scripts", this);
		} catch (Exception e) {
			throw new RuntimeException("Failed to configure manager.", e);
		}
	}

	public boolean isLoaded(String path) {
		String dir = FilenameUtils.getPath(path);
		String base = FilenameUtils.getBaseName(path);
		return loaded.contains(String.format("%s%s", dir, base));
	}

	public boolean require(String path) {
		String dir = FilenameUtils.getPath(path);
		if (dir.equals("")) {
			path = String.format("%s%s", FilenameUtils.getPath(getCurrentPath()), path);
		}
		if (!isLoaded(path)) {
			eval(path);
		}
		return Boolean.TRUE;
	}

	public ScriptLoader getLoader() {
		return loader;
	}

	@SuppressWarnings("unchecked")
	public Map<Object, Object> getBoundMap(String key) {
		return (Map<Object, Object>) manager.getBindings().get(key);
	}

	public Set<String> locateScript(String scriptPath) {
		Set<String> l = new LinkedHashSet<>();
		for (String ext : extensions) {
			l.addAll(loader.locate(String.format("%s\\.%s", scriptPath, ext)));
		}
		return l;
	}

	public Bindings getBindings() {
		return manager.getBindings();
	}

	public String getCurrentPath() {
		List<String> list = currentPath.get();
		return list.isEmpty() ? null : list.get(list.size() - 1);
	}

	public Object eval(String scriptPath) {
		return eval(scriptPath, null);
	}

	public Bindings createBindings() {
		return new SimpleBindings();
	}

	public Object eval(File scriptFile) {
		return eval(scriptFile, null);
	}

	public Object eval(File scriptFile, Bindings engineBindings) {
		return eval(scriptFile, engineBindings, null);
	}

	private void removeCurrentPath() {
		currentPath.get().remove(currentPath.get().size() - 1);
	}

	private void addCurrentPath(String p) {
		if (currentPath.get() == null) {
			currentPath.set(new ArrayList<String>());
		}
		List<String> cp = currentPath.get();
		cp.add(p);
	}

	public Object eval(File scriptFile, Bindings engineBindings, Bindings userGlobalBindings) {
		try {
			addCurrentPath(scriptFile.getPath());
			ScriptEngineManager userManager = manager;
			if (userGlobalBindings != null) {
				userManager = new ScriptEngineManager();
				userManager.setBindings(userGlobalBindings);
				configureManager(userManager);
				// if(engineBindings == null) {
				// engineBindings = createBindings();
				// }
			}
			String ext = FilenameUtils.getExtension(scriptFile.getName());
			if (ext.equals("")) {
				Set<String> s = loader.locate(scriptFile + ".*(\\." + Icelib.toSeparatedList(extensions, "|\\.") + ")");
				if (s.size() > 0) {
					return eval(s.iterator().next());
				} else {
					throw new UnsupportedOperationException(String.format(
							"No file extension to determine script language from for %s", scriptFile));
				}
			}

			LOG.info(String.format("Evaluating script %s", scriptFile));

			String base = FilenameUtils.getBaseName(scriptFile.getName());
			File dir = scriptFile.getParentFile();
			String pathLessExtension = String.format("%s%s", dir.getPath(), base);
			ScriptEngine engine = userManager.getEngineByExtension(ext);

			// Do we already have a compiled script
			if (scriptCache.containsKey(scriptFile.getPath())) {
				return scriptCache.get(scriptFile.getPath()).eval();
			}

			// Check if there is a bytecode compiled version of the script
			// available
			// from the
			// loader
			if (properties.containsKey(ext)) {
				String compiled = String.format("%s/%s.%s", dir, base, "class");
				if (new File(compiled).exists()) {
					LOG.info(String.format("Loading %s from classpath", compiled));
					throw new UnsupportedOperationException();
				}
			}

			//
			if (engine instanceof Compilable) {
				Compilable ce = (Compilable) engine;
				InputStream in = new FileInputStream(scriptFile);
				try {
					LOG.info(String.format("Compiling script %s", scriptFile));
					CompiledScript cs = ce.compile(new InputStreamReader(in));
					scriptCache.put(scriptFile.getPath(), cs);
					loaded.add(pathLessExtension);
					return cs.eval();
				} finally {
					in.close();
				}
			}
			InputStream in = new FileInputStream(scriptFile);
			try {
				Object result = engineBindings == null ? engine.eval(new InputStreamReader(in)) : engine.eval(
						new InputStreamReader(in), engineBindings);
				loaded.add(pathLessExtension);

				return result;
			} finally {
				in.close();
			}
		} catch (FileNotFoundException fnfe) {
			AssetNotFoundException ex = new AssetNotFoundException(fnfe.getMessage());
			ex.initCause(fnfe);
			throw ex;
		} catch (IOException | ScriptException se) {
			throw new ScriptEvalException("Failed to evaluate script.", se);
		} finally {
			removeCurrentPath();
		}
	}

	public Object eval(String scriptPath, Bindings engineBindings) {
		try {
			addCurrentPath(scriptPath);
			String ext = FilenameUtils.getExtension(scriptPath);
			if (ext.equals("")) {
				Set<String> s = loader.locate(scriptPath + ".*(\\." + Icelib.toSeparatedList(extensions, "|\\.") + ")");
				if (s.size() > 0) {
					return eval(s.iterator().next());
				} else {
					throw new UnsupportedOperationException(String.format(
							"No file extension to determine script language from for %s", scriptPath));
				}
			}

			LOG.info(String.format("Evaluating script %s", scriptPath));

			String base = FilenameUtils.getBaseName(scriptPath);
			String dir = FilenameUtils.getFullPath(scriptPath);
			String pathLessExtension = String.format("%s%s", dir, base);
			ScriptEngine engine = manager.getEngineByExtension(ext);

			// Do we already have a compiled script
			if (scriptCache.containsKey(scriptPath)) {
				return scriptCache.get(scriptPath).eval();
			}

			// Check if there is a bytecode compiled version of the script
			// available
			// from the
			// loader
			if (properties.containsKey(ext)) {
				String compiled = String.format("%s/%s.%s", dir, base, "class");
				if (loader.exists(compiled)) {
					LOG.info(String.format("Loading %s from classpath", compiled));
					throw new UnsupportedOperationException();
				}
			}

			//
			if (engine instanceof Compilable) {
				Compilable ce = (Compilable) engine;
				InputStream in = loader.load(scriptPath);
				LOG.info(String.format("Compiling script %s", scriptPath));
				// Note, the input stream is closed by the compiler itself
				CompiledScript cs = ce.compile(new InputStreamReader(in));
				scriptCache.put(scriptPath, cs);
				loaded.add(pathLessExtension);
				return engineBindings == null ? cs.eval() : cs.eval(engineBindings);
			}
			InputStream in = loader.load(scriptPath);
			try {
				Object result = engineBindings == null ? engine.eval(new InputStreamReader(in)) : engine.eval(
						new InputStreamReader(in), engineBindings);
				loaded.add(pathLessExtension);
				return result;
			} finally {
				in.close();
			}
		} catch (FileNotFoundException fnfe) {
			AssetNotFoundException ex = new AssetNotFoundException(fnfe.getMessage());
			ex.initCause(fnfe);
			throw ex;
		} catch (IOException | ScriptException se) {
			throw new ScriptEvalException(String.format("Failed to evaluate script %s.", scriptPath) , se);
		} finally {
			removeCurrentPath();
		}
	}
}
