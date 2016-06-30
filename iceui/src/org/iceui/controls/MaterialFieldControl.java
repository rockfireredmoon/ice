package org.iceui.controls;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.iceui.controls.chooser.ChooserPanel;
import org.iceui.controls.chooser.MaterialView;

import com.jme3.material.MaterialList;

import icetone.core.ElementManager;

public abstract class MaterialFieldControl extends ChooserFieldControl {

	public MaterialFieldControl(ElementManager screen, String initial, Collection<String> resources, Preferences pref) {
		super(screen, initial, resources, pref);
	}

	public MaterialFieldControl(ElementManager screen, String UID, String initial, 
			Collection<String> resources, Preferences pref) {
		super(screen, UID, initial, true, true, resources, pref);
	}

	public MaterialFieldControl(ElementManager screen, String initial, boolean showChooserButton,
			Collection<String> resources, Preferences pref) {
		super(screen, initial, false, showChooserButton, resources, pref);
	}

	public MaterialFieldControl(ElementManager screen, String UID, String initial, 
			boolean showChooserButton, Collection<String> resources, Preferences pref) {
		super(screen, UID, initial, false, showChooserButton, resources, pref);
	}

	public abstract MaterialList getMaterialList(String path);

	@Override
	protected ChooserPanel.ChooserView createView() {
		return new MaterialView(screen) {

			@Override
			protected MaterialList getMaterialList(String path) {
				return MaterialFieldControl.this.getMaterialList(path);
			}
			
		};
	}

	@Override
	protected String getIconPath() {
		return "BuildIcons/Icon-32-Build-PickImage.png";
	}

}
