/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInfo {

	private static final Logger LOG = Logger.getLogger(AppInfo.class.getName());
	
	/**
	 * The calling app should set this EARLY, it is used to locate the jar that
	 * contains
	 * the MANIFEST we want to read Application-Version and Application-Name
	 */
	public static Class context;
	private final static Object LOCK = new Object();
	private static String version;
	private static String name;
	private static Boolean dev;
	
	public static void setDev(boolean dev) {
		AppInfo.dev = dev;
	}

	public static boolean isDev() {
		return dev == null ? getVersion().indexOf("-DEV-") != -1 : dev;
	}

	public static String getName() {
		synchronized (LOCK) {
			if (context == null) {
				throw new IllegalStateException("AppInfo.context must be set before the first call to getName()");
			}
			if (version == null) {
				doGetInfo();
			}
			return name;
		}
	}

	public static String getVersion() {
		synchronized (LOCK) {
			if (context == null) {
				throw new IllegalStateException("AppInfo.context must be set before the first call to getVersion()");
			}
			if (version == null) {
				doGetInfo();
			}
			return version;
		}
	}

	private static void readManifest(Manifest manifest) {
		version = manifest.getMainAttributes().getValue("Application-Version");
		name = manifest.getMainAttributes().getValue("Application-Name");
	}

	private static void doGetInfo() {
		try {
			String className = context.getSimpleName();
			String classFileName = className + ".class";
			LOG.info(String.format("Looking for application versioning in %s", classFileName));
			String pathToThisClass = context.getResource(classFileName).toString();
			LOG.info(String.format("Path to class is %s", pathToThisClass));
			int mark = pathToThisClass.indexOf("!");
			String pathToManifest = pathToThisClass.toString().substring(0, mark + 1);
			pathToManifest = pathToManifest + "/META-INF/MANIFEST.MF";
			LOG.info(String.format("Manifest should be at %s", pathToManifest));
			Manifest manifest =   new Manifest(pathToManifest.startsWith("/") ? AppInfo.class.getResourceAsStream(pathToManifest): new URL(pathToManifest).openStream());
			readManifest(manifest);
		} catch (Exception ioe) {
			LOG.log(Level.WARNING, "Could not locate manifest for application versioning.", ioe);
		}

		// Is the a MANIFEST.MF in the current directory?
		if (version == null && name == null) {
			File mf = new File(new File(System.getProperty("user.dir")), "MANIFEST.MF");
			if (mf.exists()) {
				try {
					try (FileInputStream in = new FileInputStream(mf)) {
						Manifest manifest = new Manifest(in);
						readManifest(manifest);
					}
				} catch (Exception ioe) {
				}
			}
		}

		// Look for local project properties
		if (name == null && version == null) {
			final Properties properties = loadProjectProperties();
			name = properties.getProperty("application.title");
			version = loadProjectProperties().getProperty("app.version");
		}

		// Defaults
		if (name == null) {
			name = context.getSimpleName();
		}
		if (version == null) {
			version = "999.999.999";
		}
		LOG.info(String.format("%s (%s)", name, version));
	}

	protected static Properties loadProjectProperties() {
		File f = new File("nbproject/project.properties");
		Properties p = new Properties();
		try {
			try (FileInputStream fin = new FileInputStream(f)) {
				p.load(fin);
				return p;
			}
		} catch (Exception ioe) {
			return new Properties();
		}
	}
}
