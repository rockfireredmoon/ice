package org.iceui.controls.chooser;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.iceui.HPosition;
import org.iceui.VPosition;
import org.iceui.controls.FancyPersistentWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.SaveType;
import org.iceui.controls.chooser.ChooserPanel.ChooserView;

import com.jme3.math.Vector2f;

import icetone.core.ElementManager;

/**
 * The {@link AbstractChooserDialog} provides a window for selecting one or more
 * entries from a heirarchical list of strings. The heirarchy is determined by
 * the '/' in the paths supplied.
 * <p>
 * The window is split into two parts, the left hand side lists the available
 * 'folders', i.e. all of the parents derived from the list of provided resource
 * path strings. The right hand side is used to list the resources in the
 * folder. The presentation for this is provided by an implementor of
 * {@link ChooserView}.
 */
public abstract class AbstractChooserDialog extends FancyPersistentWindow {

	protected final ChooserView view;
	protected final ChooserPanel panel;
	protected boolean chosen;
	protected Collection<String> resources;

	public AbstractChooserDialog(final ElementManager screen, String title, Collection<String> resources, Preferences pref,
			ChooserView view) {
		super(screen, title, screen.getStyle("Common").getInt("defaultWindowOffset"), VPosition.MIDDLE, HPosition.RIGHT,
				new Vector2f(550, 480), FancyWindow.Size.SMALL, true, SaveType.SIZE, pref);
		this.view = view;
		this.resources = resources;
		panel = createPanel();
		setWindowTitle(title);
		setDestroyOnHide(true);
		setFolder(null);
		setIsResizable(true);
	}

	public void setResources(Collection<String> resources) {
		this.resources = resources;
		panel.setResources(resources);
	}

	protected abstract ChooserPanel createPanel();

	public ChooserView getView() {
		return panel.getView();
	}

	public String getSelected() {
		return panel.getSelected();
	}

	@Override
	protected final void onCloseWindow() {
		super.onCloseWindow();
		if (!chosen) {
			onChosen(null);
		} else {
			chosen = false;
		}
		onCloseChooser();
	}

	protected void onCloseChooser() {

	}

	public void setSelectedFile(String file, boolean callback) {
		panel.setSelectedFile(file, callback);
	}

	public final void setFolder(String folder) {
		panel.setFolder(folder);
	}

	public void choose(String path) {
		panel.choose(path);
	}

	protected abstract boolean onChosen(String path);
}
