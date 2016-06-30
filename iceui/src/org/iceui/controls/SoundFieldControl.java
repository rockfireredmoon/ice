package org.iceui.controls;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.iceui.controls.chooser.ChooserPanel;
import org.iceui.controls.chooser.SoundSourceDialog;
import org.iceui.controls.chooser.SoundView;

import icetone.core.ElementManager;

public abstract class SoundFieldControl extends ChooserFieldControl {

	private String prefKey = "sound";
	private Type type;

	public enum Type {
		RESOURCE, ALL
	}

	public SoundFieldControl(ElementManager screen, Type type, String initial, Collection<String> imageResources, Preferences pref) {
		super(screen, initial, imageResources, pref);
		this.type = type;
	}

	public SoundFieldControl(ElementManager screen, Type type, String UID, String initial, boolean includeAlpha,
			Collection<String> imageResources, Preferences pref) {
		super(screen, UID, initial, true, true, imageResources, pref);
		this.type = type;
	}

	public SoundFieldControl(ElementManager screen, Type type, String initial, boolean showHex, boolean showChooserButton,
			Collection<String> imageResources, Preferences pref) {
		super(screen, initial, showHex, showChooserButton, imageResources, pref);
		this.type = type;
	}

	public SoundFieldControl(ElementManager screen, Type type, String UID, String initial, boolean showHex,
			boolean showChooserButton, Collection<String> imageResources, Preferences pref) {
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
		// TODO Auto-generated method stub

	}

	@Override
	protected FancyWindow createChooser() {

		SoundSourceDialog chooser = new SoundSourceDialog(screen, chooserTitle, resources, pref, createView(), true, prefKey, type) {

			protected boolean isAnyAudioPlaying() {
				return SoundFieldControl.this.isAudioPlaying();
			}
			
			@Override
			protected boolean onChosen(Source source, String path) {
				if (path != null) {
					setValueFromChooserPath(path);
					updateControls();
					onResourceChosen(value);
				}
				return true;
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
		if (value != null) {
			chooser.setSelectedFile(getChooserPathFromValue(), false);
		}
		return chooser;
	}
	
	protected boolean isAudioPlaying() {
		return false;
	}

	protected void stopAudio() {
	}

	@Override
	protected ChooserPanel.ChooserView createView() {
		return new SoundView(screen) {
			@Override
			protected void onPlay(String path) {
				playURL(SoundSourceDialog.Source.RESOURCE, path);
			}

		};
	}

	@Override
	protected String getIconPath() {
		return String.format("%s/audio.png", screen.getStyle("Common").getString("iconPath"));
	}

	protected void playURL(SoundSourceDialog.Source source, String path) {
		((SoundSourceDialog) SoundFieldControl.this.chooser).setStopAvailable(true);
	}
}
