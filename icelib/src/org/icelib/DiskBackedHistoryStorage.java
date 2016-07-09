/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class DiskBackedHistoryStorage extends UndoManager.DefaultListStorage {

	private static final Logger LOG = Logger.getLogger(DiskBackedHistoryStorage.class.getName());
	private int pageSize;
	private int undoBlock;
	private final File undoDir;
	private final File redoDir;

	public DiskBackedHistoryStorage() throws IOException {
		this(250);
	}

	public DiskBackedHistoryStorage(int pageSize) throws IOException {
		this.pageSize = pageSize;

		final File storageDir = new File(new File(System.getProperty("java.io.tmpdir")),
				"umgr" + System.currentTimeMillis() + hashCode());
		if (!storageDir.mkdirs()) {
			throw new IOException(String.format("Could not create undo manager storage directory %s.", storageDir));
		}
		undoDir = new File(storageDir, "undo");
		if (!undoDir.mkdir()) {
			throw new IOException(String.format("Could not create undo manager storage directory %s.", undoDir));
		}
		redoDir = new File(storageDir, "redo");
		if (!redoDir.mkdir()) {
			throw new IOException(String.format("Could not create undo manager storage directory %s.", undoDir));
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					FileUtils.deleteDirectory(storageDir);
				} catch (IOException ex) {
				}
			}
		});
	}

	@Override
	public void pushUndo(UndoManager.UndoableCommand command) {
		super.pushUndo(command);

		// If the undo list is at the maximum allowed in memory, store the
		// current contents
		// to disk
		if (undoSize() == pageSize) {
			//
			LOG.info(String.format("Writing block of %s", pageSize));
			try {
				FileOutputStream out = new FileOutputStream(new File(undoDir, (undoBlock++) + ".txt"));
				ObjectOutputStream oos = new ObjectOutputStream(out);
				try {
					while (!history.isEmpty()) {
						oos.writeObject(history.pop());
					}
					oos.flush();
				} finally {
					oos.close();
				}
			} catch (IOException ioe) {
				throw new RuntimeException("Failed to store undo operation to disk.", ioe);
			}
		}
	}
}
