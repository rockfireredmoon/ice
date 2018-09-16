package org.iceui.controls;

import java.util.prefs.Preferences;

import org.iceui.controls.chooser.ImageThumbView;

import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserModel;
import icetone.extras.chooser.ChooserPanel;

public abstract class ImageFieldControl extends ChooserFieldControl<String> {

	class Swatch extends Element {

		Swatch(BaseScreen screen) {
			super(screen);
			onMouseReleased(evt -> {
				if (!showChooserButton) {
					showChooser(evt.getX(), evt.getY());
				}
			});
		}

	}

	private Swatch colorSwatch;

	public ImageFieldControl(BaseScreen screen, String initial, ChooserModel<String> imageResources,
			Preferences pref) {
		super(screen, initial, imageResources, pref);
	}

	public ImageFieldControl(BaseScreen screen, String initial, boolean includeAlpha,
			ChooserModel<String> imageResources, Preferences pref) {
		super(screen, initial, imageResources, pref);
	}

	public ImageFieldControl(BaseScreen screen, String UID, String initial, boolean includeAlpha,
			ChooserModel<String> imageResources, Preferences pref) {
		super(screen, UID, initial, true, true, imageResources, pref);
	}

	public ImageFieldControl(BaseScreen screen, String initial, boolean showHex, boolean showChooserButton,
			ChooserModel<String> imageResources, Preferences pref) {
		super(screen, initial, showHex, showChooserButton, imageResources, pref);
	}

	public ImageFieldControl(BaseScreen screen, String UID, String initial, boolean includeAlpha,
			boolean showHex, boolean showChooserButton, ChooserModel<String> imageResources, Preferences pref) {
		super(screen, UID, initial, showHex, showChooserButton, imageResources, pref);
	}

	@Override
	protected void createLayout() {

		// Configure layout depending on options
		if (showName) {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0, wrap 3", "[][grow]1[shrink 0]"));
			} else {
				setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0, wrap 2", "[][grow]"));
			}
		} else {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0, wrap 2", "[][shrink 0]"));
			} else {
				setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0, wrap 1", "[]"));
			}
		}

		// Swatch
		colorSwatch = new Swatch(screen);
		addElement(colorSwatch);

	}

	@Override
	protected ChooserPanel.ChooserView<String> createView() {
		return new ImageThumbView(screen);
	}

	@Override
	protected void updateControls() {
		super.updateControls();
		if (value == null) {
			colorSwatch.clearUserStyles();
		} else {
			colorSwatch.setTexture(getChooserPathFromValue());
		}
	}
}
