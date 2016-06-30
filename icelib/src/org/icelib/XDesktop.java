/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * AWT {@link Desktop} doesn't seem to work that well on Linux, stuff always
 * opens for me in Google Chrome.
 */
public class XDesktop {

	private static XDesktop desktop;

	// Created the appropriate instance
	public static XDesktop getDesktop() {
		if (desktop == null) {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("linux") != -1) {
				desktop = new LinuxDesktop();
			} else {
				desktop = new XDesktop();
			}
		}
		return desktop;
	}

	// default implementation :(
	public void open(File file) throws IOException {
		Desktop.getDesktop().open(file);
	}

	public void edit(File file) throws IOException {
		Desktop.getDesktop().edit(file);
	}

	public void browse(URI uri) throws IOException {
		Desktop.getDesktop().browse(uri);
	}

	static class LinuxDesktop extends XDesktop {

		@Override
		public void edit(File file) throws IOException {
			open(file);
		}

		@Override
		public void open(File file) throws IOException {
			ProcessBuilder pb = new ProcessBuilder("xdg-open", file.getAbsolutePath());
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
			pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			Process p = pb.start();
			try {
				if (p.waitFor() != 0) {
					throw new IOException(String.format("Open of %s resulted in non-zero exit value (%d)", file.getAbsolutePath(),
							p.exitValue()));
				}
			} catch (InterruptedException ie) {
				//
			}
		}
	}
}
