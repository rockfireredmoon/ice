package org.iceui.controls.chooser;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.iceui.controls.chooser.ChooserPanel.ChooserView;

import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;

/**
 * The {@link ChooserDialog} provides a window for selecting one or more entries
 * from a heirarchical list of strings. The heirarchy is determined by the '/'
 * in the paths supplied.
 * <p>
 * The window is split into two parts, the left hand side lists the available
 * 'folders', i.e. all of the parents derived from the list of provided resource
 * path strings. The right hand side is used to list the resources in the
 * folder. The presentation for this is provided by an implementor of
 * {@link ChooserView}.
 */
public abstract class ChooserDialog extends AbstractChooserDialog {

	public ChooserDialog(final ElementManager screen, String title, Collection<String> resources, Preferences pref, ChooserView view) {
		super(screen, title, resources, pref, view);
		content.setLayoutManager(new MigLayout(screen, "gap 0, ins 0, wrap 1", "[fill, grow]", "[fill, grow]"));
		content.addChild(panel);
	}

	@Override
	protected ChooserPanel createPanel() {
		return new ChooserPanel(screen, resources, pref, view) {
			@Override
			protected void onItemChosen(String path) {
				if (onChosen(path)) {
					chosen = true;
					hideWindow();
				}
			}
		};
	}

}
