/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class Zip {

	private Zip() {
	}

	public static void compress(File sourceDir, File outputFile) throws IOException, FileNotFoundException {
		ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
		try {
			compressDir(sourceDir, sourceDir, zipFile);
		} finally {
			IOUtils.closeQuietly(zipFile);
		}
	}

	private static void compressDir(File rootDir, File sourceDir, ZipOutputStream out) throws IOException, FileNotFoundException {
		for (File file : sourceDir.listFiles()) {
			if (file.isDirectory()) {
				compressDir(rootDir, new File(sourceDir, file.getName()), out);
			} else {
				String rp = rootDir.getPath();
				ZipEntry entry = new ZipEntry(
						sourceDir.getPath().substring(rp.length()) + "/" + file.getName() + (file.isDirectory() ? "/" : ""));
				out.putNextEntry(entry);
				FileInputStream in = new FileInputStream(file);
				try {
					IOUtils.copy(in, out);
				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		}
	}
}
