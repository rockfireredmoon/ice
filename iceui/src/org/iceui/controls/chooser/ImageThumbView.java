package org.iceui.controls.chooser;

import icetone.controls.buttons.SelectableItem;
import icetone.core.BaseScreen;
import icetone.extras.chooser.AbstractButtonView;
import icetone.extras.chooser.ChooserDialog;

/**
 * {@link ChooserDialog.ChooserView} that lists resources as image thumbnails.
 * Appropriate for any image type supported by JME3.
 */
public class ImageThumbView extends AbstractButtonView<String> {

	public ImageThumbView(BaseScreen screen) {
		super("image-view", screen);
	}

	@Override
	protected void configureButton(SelectableItem button, String path) {
		super.configureButton(button, path);
		button.setButtonIcon(path);
	}

}
