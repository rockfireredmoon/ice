package org.iceui.controls.chooser;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.icelib.Icelib;
import org.iceui.controls.BusySpinner;
import org.iceui.controls.FancyButton;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.SplitPanel;
import icetone.controls.lists.Table;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.listeners.MouseButtonListener;

/**
 * The {@link ChooserDialog} provides a window for selecting one or more entries
 * from a
 * heirarchical list of strings. The heirarchy is determined by the '/' in the
 * paths
 * supplied.
 * <p>
 * The window is split into two parts, the left hand side lists the available
 * 'folders', i.e. all of the parents derived from the list of provided resource
 * path strings. The right hand side is used to list the resources in the
 * folder. The presentation for this is provided by an implementor of
 * {@link ChooserView}.
 */
public abstract class ChooserPanel extends Container {

	private final ChooserView view;
	private final SplitPanel split;
	private final BusySpinner busy;

	public final class FolderTable extends Table implements MouseButtonListener {
		public FolderTable(ElementManager screen) {
			super(screen);
		}

		@Override
		public void onChange() {
		}

		@Override
		public void onMouseLeftReleased(MouseButtonEvent evt) {
			if (LUtil.isDoubleClick(evt)) {
				final List<TableRow> selectedListItems = getSelectedRows();
				if (!selectedListItems.isEmpty()) {
					TableRow selRow = selectedListItems.iterator().next();
					TableCell selCell = (TableCell) selRow.getElements().iterator().next();
					setFolder((String) selCell.getValue());
				}
			}
		}

		@Override
		public void onMouseLeftPressed(MouseButtonEvent evt) {
		}

		@Override
		public void onMouseRightPressed(MouseButtonEvent evt) {
		}

		@Override
		public void onMouseRightReleased(MouseButtonEvent evt) {
		}
	}

	/**
	 * Implement to provide the UI component used for the right hand side of the
	 * chooser
	 * dialog.
	 */
	public interface ChooserView {

		/**
		 * Set whether the view is enabled or not. May change during life of
		 * view
		 * 
		 * @param enabled
		 *            enabled
		 */
		void setEnabled(boolean enabled);

		/**
		 * Create the UI component for this view. It will only be called once.
		 *
		 * @param chooser
		 *            chooser
		 * @return view element
		 */
		Element createView(ChooserPanel chooser);

		/**
		 * Rebuild the displayed items. Will be called multiple times as the
		 * user
		 * navigates the folder heirarchy.
		 *
		 * @param cwd
		 *            the current working directory, i.e. the path of parent.
		 * @param filesNames
		 *            the list of file names in the current working directory
		 */
		void rebuild(String cwd, Collection<String> filesNames);

		/**
		 * Present the file as selected in this view.
		 *
		 * @param file
		 *            file
		 */
		void select(String file);
	}

	private final Table folders;
	private Collection<String> resources;
	private String cwd;
	private final Element breadCrumbs;
	private String selected;

	public ChooserPanel(final ElementManager screen, Collection<String> resources, Preferences pref, ChooserView view) {
		super(screen);

		this.view = view;

		setLayoutManager(new MigLayout(screen, "wrap 2", "[grow, fill][]", "[shrink 0]4[fill, grow, shrink 200]"));

		// Bread crumbs
		breadCrumbs = new Element(screen);
		breadCrumbs.setLayoutManager(new MigLayout(screen, "ins 0, gap 0", "[]4", "[]"));
		addChild(breadCrumbs, "growx");
		busy = new BusySpinner(screen);
		addChild(busy);

		// Folders
		folders = new FolderTable(screen);
		folders.setMinDimensions(Vector2f.ZERO);
		folders.addColumn("Folder");
		folders.setHeadersVisible(false);
		folders.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);

		// Split
		split = new SplitPanel(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, Orientation.HORIZONTAL);
		split.setLeftOrTop(folders);
		split.setRightOrBottom(view.createView(this));
		split.setDefaultDividerLocationRatio(0.25f);

		addChild(split, "span 2, growx, growy");

		//
		this.resources = resources;
		setFolder(null);
	}

	public BusySpinner getBusy() {
		return busy;
	}

	public ChooserView getView() {
		return view;
	}

	public String getSelected() {
		return selected;
	}

	public final void setSelectedFile(String file, boolean callback) {
		System.err.println("se; " + file);
		String dirname = Icelib.getDirname(file);
		if (cwd == null || !dirname.equals(cwd)) {
			// Will cause a rebuild so no need to select invidually
			selected = file;
			setFolder(dirname);
		} else {
			selected = file;
			view.select(file);
		}
		if (callback) {
			onItemChosen(selected);
		}
	}

	public final void setFolder(String folder) {
		if (folder != null && folder.endsWith("/")) {
			folder = folder.substring(0, folder.length() - 1);
		}
		this.cwd = folder;
		rebuildList();
		rebuildBreadCrumbs();
		layoutChildren();
	}

	public void choose(String path) {
		System.err.println("choose(" + path + ")");
		onItemChosen(path);
	}

	protected abstract void onItemChosen(String path);

	private void rebuildBreadCrumbs() {
		breadCrumbs.removeAllChildren();
		final FancyButton root = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				super.onButtonMouseLeftUp(evt, toggled);
				setFolder(null);
			}
		};
		root.setMinDimensions(new Vector2f(64, 16));
		root.setText("/");
		breadCrumbs.addChild(root);
		if (cwd != null) {
			String tp = null;
			for (String p : cwd.split("/")) {
				final String ftp = tp == null ? p : tp + "/" + p;
				final FancyButton dir = new FancyButton(screen) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						super.onButtonMouseLeftUp(evt, toggled);
						setFolder(ftp);
					}
				};
				dir.setText(p);
				breadCrumbs.addChild(dir);
				tp = ftp;
			}
		}
		breadCrumbs.layoutChildren();
	}

	private void rebuildList() {
		folders.removeAllRows();

		// Find all the unique folder names in the current path
		Set<String> foldersNames = new LinkedHashSet<String>();
		final Set<String> filesNames = new LinkedHashSet<String>();
		if (resources != null) {
			for (String s : resources) {
				if (cwd != null) {
					if (s.startsWith(cwd + "/")) {
						s = s.substring(cwd.length() + 1);
					} else {
						continue;
					}
				}
				int idx = s.indexOf('/');
				if (idx > -1) {
					foldersNames.add(s.substring(0, idx));
				} else {
					filesNames.add(s);
				}
			}
		}

		// Folders
		if (cwd != null) {
			int idx = cwd.lastIndexOf('/');
			String par = idx == -1 ? null : cwd.substring(0, idx);
			Table.TableRow row = new Table.TableRow(screen, folders);
			row.addCell("..", par);
			folders.addRow(row);
		}
		for (String s : foldersNames) {
			Table.TableRow row = new Table.TableRow(screen, folders);
			row.addCell(s, getPath(s));
			folders.addRow(row);
		}

		// Now the images
		boolean foundSelection = false;
		for (String s : filesNames) {
			final String path = getPath(s);
			if (path.equals(selected)) {
				foundSelection = true;
			}
		}

		new Thread("ViewRebuild") {
			@Override
			public void run() {
				view.rebuild(cwd, filesNames);
			}
		}.start();

		// Clear selection if it is not in current direction
		if (!foundSelection) {
			selected = null;
		}
	}

	private String getPath(String s) {
		final String path = cwd == null ? s : (cwd + "/" + s);
		return path;
	}

	public void setResources(Collection<String> resources) {
		this.resources = resources;
		setFolder(cwd);
	}
}
