/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class DOSWriter extends PrintWriter {

	private boolean autoFlush;

	public DOSWriter(Writer out) {
		super(out);
	}

	public DOSWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public DOSWriter(OutputStream out) {
		super(out);
	}

	public DOSWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		this.autoFlush = autoFlush;
	}

	public DOSWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public DOSWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public DOSWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public DOSWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	@Override
	public void println() {
		try {
			synchronized (lock) {
				ensureOpen();
				out.write("\r\n");
				if (autoFlush) {
					out.flush();
				}
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			setError();
		}

	}

	@Override
	public void print(String text) {
		try {
			synchronized (lock) {
				ensureOpen();
				text = text.replace("\r\n", (char) 1 + "__" + (char) 1);
				text = text.replace("\n", "\r\n");
				text = text.replace((char) 1 + "__" + (char) 1, "\r\n");
				out.write(text.toCharArray());
				if (autoFlush) {
					out.flush();
				}
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			setError();
		}

	}

	@Override
	public void println(String text) {
		try {
			synchronized (lock) {
				ensureOpen();
				text = text.replace("\r\n", (char) 1 + "__" + (char) 1);
				text = text.replace("\n", "\r\n");
				text = text.replace((char) 1 + "__" + (char) 1, "\r\n");
				out.write(text.toCharArray());
				out.write("\r\n");
				if (autoFlush) {
					out.flush();
				}
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			setError();
		}

	}

	private void ensureOpen() throws IOException {
		if (out == null) {
			throw new IOException("Stream closed");
		}
	}
}
