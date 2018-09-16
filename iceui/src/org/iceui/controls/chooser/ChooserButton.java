package org.iceui.controls.chooser;

import org.icelib.Icelib;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.extras.chooser.ChooserPanel;

public class ChooserButton extends PushButton {

	public ChooserButton(String path, BaseScreen screen, ChooserPanel<String> chooser, float previewSize) {
		super(screen);
		setPreferredDimensions(new Size(previewSize, previewSize));
		setToolTipText(Icelib.getFilename(path));
		onMouseReleased(evt -> chooser.choose(path));
	}

}