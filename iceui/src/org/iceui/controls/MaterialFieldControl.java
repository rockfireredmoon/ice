package org.iceui.controls;

import java.util.prefs.Preferences;

import org.iceui.controls.chooser.MaterialView;

import com.jme3.material.MaterialList;

import icetone.core.BaseScreen;
import icetone.extras.chooser.ChooserModel;
import icetone.extras.chooser.ChooserPanel;

public abstract class MaterialFieldControl extends ChooserFieldControl<String> {

	public MaterialFieldControl(BaseScreen screen, String initial, ChooserModel<String> resources,
			Preferences pref) {
		super(screen, initial, resources, pref);
	}

	public MaterialFieldControl(BaseScreen screen, String UID, String initial, ChooserModel<String> resources,
			Preferences pref) {
		super(screen, UID, initial, true, true, resources, pref);
	}

	public MaterialFieldControl(BaseScreen screen, String initial, boolean showChooserButton,
			ChooserModel<String> resources, Preferences pref) {
		super(screen, initial, false, showChooserButton, resources, pref);
	}

	public MaterialFieldControl(BaseScreen screen, String UID, String initial, boolean showChooserButton,
			ChooserModel<String> resources, Preferences pref) {
		super(screen, UID, initial, false, showChooserButton, resources, pref);
	}

	public abstract MaterialList getMaterialList(String path);

	@Override
	protected ChooserPanel.ChooserView<String> createView() {
		return new MaterialView(screen) {

			@Override
			protected MaterialList getMaterialList(String path) {
				return MaterialFieldControl.this.getMaterialList(path);
			}

		};
	}

}
