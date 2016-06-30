package org.iceui.controls.chooser;

import icetone.core.Element;
import icetone.core.ElementManager;

/**
 * {@link ChooserDialog.ChooserView} that lists resources as sound thumb nails,
 * each
 * button having a play button to preview the sound.
 */
public class SoundView extends AbstractButtonView {

	public SoundView(ElementManager screen) {
		super(screen);
	}

	@Override
	protected Element createButton(final String path) {
		return new SoundButton(chooser, previewSize, path, screen) {
			@Override
			protected void onPlay() {
				SoundView.this.onPlay(getPath());
			}

		};
	}

	protected void onStop() {
	}

	protected void onPlay(String path) {
	}
}
