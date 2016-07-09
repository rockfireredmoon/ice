/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UndoManager {

	private static final Logger LOG = Logger.getLogger(UndoManager.class.getName());

	public static class UndoableCompoundCommand extends ArrayList<UndoableCommand> implements UndoableCommand {

		@Override
		public void undoCommand() {
			for (UndoableCommand u : this) {
				u.undoCommand();
			}
		}

		@Override
		public void doCommand() {
			for (UndoableCommand u : this) {
				u.doCommand();
			}
		}
	}

	public interface HistoryStorage {

		void clearRedo();

		void pushUndo(UndoableCommand command);

		UndoableCommand popRedo();

		void pushRedo(UndoableCommand last);

		UndoableCommand popUndo();

		boolean isRedoAvailable();

		boolean isUndoAvailable();

		boolean isEmpty();

		int undoSize();

		int redoSize();
	}

	public interface Listener {

		void undoing(UndoableCommand command);

		void undone(UndoableCommand command);

		void redoing(UndoableCommand command);

		void redone(UndoableCommand command);

		void doing(UndoableCommand command);

		void done(UndoableCommand command);
	}

	public static class ListenerAdapter implements Listener {

		@Override
		public void undoing(UndoableCommand command) {
			starting();
			change();
		}

		@Override
		public void undone(UndoableCommand command) {
			complete();
			change();
		}

		@Override
		public void redoing(UndoableCommand command) {
			starting();
			change();
		}

		@Override
		public void redone(UndoableCommand command) {
			complete();
			change();
		}

		@Override
		public void doing(UndoableCommand command) {
			starting();
			change();
		}

		@Override
		public void done(UndoableCommand command) {
			complete();
			change();
		}

		protected void change() {
		}

		protected void starting() {
		}

		protected void complete() {
		}
	}

	public interface UndoableCommand extends Serializable {

		void undoCommand();

		void doCommand();
	}

	private final HistoryStorage storage;
	private List<Listener> listeners = new ArrayList<>();

	public static class DefaultListStorage implements HistoryStorage {

		protected Stack<UndoableCommand> history = new Stack<>();
		protected Stack<UndoableCommand> redo = new Stack<>();

		@Override
		public void clearRedo() {
			redo.clear();
		}

		@Override
		public void pushUndo(UndoableCommand command) {
			history.push(command);
		}

		@Override
		public UndoableCommand popRedo() {
			return redo.pop();
		}

		@Override
		public void pushRedo(UndoableCommand last) {
			redo.push(last);
		}

		@Override
		public UndoableCommand popUndo() {
			return history.pop();
		}

		@Override
		public boolean isRedoAvailable() {
			return !redo.isEmpty();
		}

		@Override
		public boolean isUndoAvailable() {
			return !history.isEmpty();
		}

		@Override
		public boolean isEmpty() {
			return history.isEmpty() && redo.isEmpty();
		}

		@Override
		public int undoSize() {
			return history.size();
		}

		@Override
		public int redoSize() {
			return redo.size();
		}
	}

	public UndoManager() {
		this(new DefaultListStorage());
	}

	public UndoManager(HistoryStorage storage) {
		this.storage = storage;
	}

	public int redoSize() {
		return storage.redoSize();
	}

	public int undoSize() {
		return storage.undoSize();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}

	public boolean isUndoAvailable() {
		return storage.isUndoAvailable();
	}

	public boolean isRedoAvailable() {
		return storage.isRedoAvailable();
	}

	public void undo() {
		if (!isUndoAvailable()) {
			throw new IllegalStateException("Cannot undo any further, no history.");
		}
		UndoableCommand last = storage.popUndo();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Undo  - history is now %d big, redo is %d", storage.undoSize(), storage.redoSize()));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).undoing(last);
		}
		last.undoCommand();
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).undone(last);
		}
		storage.pushRedo(last);
	}

	public void redo() {
		if (!isRedoAvailable()) {
			throw new IllegalStateException("Cannot redo any further, no history.");
		}
		UndoableCommand last = storage.popRedo();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Redo  - history is now %d big, redo is %d", storage.undoSize(), storage.redoSize()));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).redoing(last);
		}
		last.doCommand();
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).redone(last);
		}
		storage.pushUndo(last);
	}

	public void storeAndExecute(UndoableCommand command) {
		storage.pushUndo(command);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Do - history is now %d big, redo is %d", storage.undoSize(), storage.redoSize()));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).doing(command);
		}
		command.doCommand();
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).done(command);
		}
		storage.clearRedo();
	}
}
