package org.iceui.controls.chooser;

import com.jme3.font.BitmapFont.Align;

import icetone.controls.buttons.SelectableItem;
import icetone.controls.menuing.Menu;
import icetone.core.BaseScreen;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.WrappingLayout;
import icetone.extras.chooser.AbstractButtonView;
import icetone.extras.chooser.ChooserDialog;
import icetone.extras.util.ExtrasUtil;

/**
 * {@link ChooserDialog.ChooserView} that lists resources as sound thumb nails,
 * each button having a play button to preview the sound.
 */
public class SoundView extends AbstractButtonView<String> {

	public SoundView(BaseScreen screen) {
		super("sound-view", screen);
	}

	@Override
	protected void configureButton(SelectableItem item, final String path) {
		super.configureButton(item, path);

		item.onMouseReleased(evt -> {
			Menu<String> rightClickMenu = new Menu<>(screen);
			rightClickMenu.onChanged((evt2) -> {
				SoundView.this.onPlay(path);
			});
			rightClickMenu.addMenuItem("Play");
		}, MouseUIButtonEvent.RIGHT);
		item.setText(ExtrasUtil.getFilename(path));
	}

	@Override
	protected WrappingLayout createLayout() {
		return ((WrappingLayout) super.createLayout()).setFill(true).setAlign(Align.Left);
	}

	protected void onStop() {
	}

	protected void onPlay(String path) {
	}
}
