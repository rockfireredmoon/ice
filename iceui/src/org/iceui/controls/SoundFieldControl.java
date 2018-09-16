package org.iceui.controls;

import java.util.prefs.Preferences;

import org.iceui.controls.chooser.SoundSourceDialog;
import org.iceui.controls.chooser.SoundView;

import icetone.controls.containers.Frame;
import icetone.core.BaseScreen;
import icetone.extras.chooser.ChooserModel;
import icetone.extras.chooser.ChooserPanel;

public abstract class SoundFieldControl extends ChooserFieldControl<String> {

	private String prefKey = "sound";
	private Type type;

	public enum Type {
		RESOURCE, ALL
	}

	public SoundFieldControl(BaseScreen screen, Type type, String initial, ChooserModel<String> imageResources,
			Preferences pref) {
		super(screen, initial, imageResources, pref);
		this.type = type;
	}

	public SoundFieldControl(BaseScreen screen, Type type, String UID, String initial, boolean includeAlpha,
			ChooserModel<String> imageResources, Preferences pref) {
		super(screen, UID, initial, true, true, imageResources, pref);
		this.type = type;
	}

	public SoundFieldControl(BaseScreen screen, Type type, String initial, boolean showHex,
			boolean showChooserButton, ChooserModel<String> imageResources, Preferences pref) {
		super(screen, initial, showHex, showChooserButton, imageResources, pref);
		this.type = type;
	}

	public SoundFieldControl(BaseScreen screen, Type type, String UID, String initial, boolean showHex,
			boolean showChooserButton, ChooserModel<String> imageResources, Preferences pref) {
		super(screen, UID, initial, showHex, showChooserButton, imageResources, pref);
		this.type = type;
	}

	public String getPrefKey() {
		return prefKey;
	}

	public void setPrefKey(String prefKey) {
		this.prefKey = prefKey;
	}

	@Override
	protected void onResourceChosen(String newResource) {
	}

	@Override
	protected Frame createChooser() {

		SoundSourceDialog chooser = new SoundSourceDialog(screen, getStyleId() + "-chooser", chooserTitle, resources,
				pref, createView(), true, prefKey, type) {

			protected boolean isAnyAudioPlaying() {
				return SoundFieldControl.this.isAudioPlaying();
			}

			@Override
			protected void onURLPlay(SoundSourceDialog.Source source, String path) {
				playURL(source, path);
			}

			@Override
			protected void onStop() {
				SoundFieldControl.this.stopAudio();
				setStopAvailable(false);
			}
		};
		chooser.onChange(evt -> {
			if (evt.getNewValue() != null) {
				setValueFromChooserPath(evt.getNewValue());
				updateControls();
				onResourceChosen(value);
			}
			if (!evt.isTemporary())
				chooser.hide();
		});
		chooser.setModal(isDialogModal());
		if (value != null) {
			chooser.setSelectedFile(getChooserPathFromValue());
		}
		return chooser;
	}

	protected boolean isAudioPlaying() {
		return false;
	}

	protected void stopAudio() {
	}

	@Override
	protected ChooserPanel.ChooserView<String> createView() {
		return new SoundView(screen) {
			@Override
			protected void onPlay(String path) {
				playURL(SoundSourceDialog.Source.RESOURCE, path);
			}

		};
	}

	protected void playURL(SoundSourceDialog.Source source, String path) {
		((SoundSourceDialog) SoundFieldControl.this.chooser).setStopAvailable(true);
	}
}
