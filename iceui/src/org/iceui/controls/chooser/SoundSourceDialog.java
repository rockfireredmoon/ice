package org.iceui.controls.chooser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

import org.iceui.controls.FancyButton;
import org.iceui.controls.SoundFieldControl;
import org.iceui.controls.SoundFieldControl.Type;
import org.iceui.controls.chooser.ChooserPanel.ChooserView;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.RadioButton;
import icetone.controls.buttons.RadioButtonGroup;
import icetone.controls.lists.ComboBox;
import icetone.controls.menuing.MenuItem;
import icetone.controls.text.Label;
import icetone.core.Container;
import icetone.core.ElementManager;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

/**
 */
public abstract class SoundSourceDialog extends AbstractChooserDialog {

	private final RadioButtonGroup bg;
	private final RadioButton useResource;
	private RadioButton useDownloadURL;
	private RadioButton useStreamURL;
	private final boolean allowPreview;
	private final RadioButton noSound;

	public enum Source {

		NONE, STREAM_URL, DOWNLOAD_URL, RESOURCE
	}

	private URLField streamURL;
	private URLField downloadURL;
	private Source source = Source.NONE;
	private FancyButton stop;
	private String prefKey;
	private org.iceui.controls.SoundFieldControl.Type type;
	private List<Source> sources = new ArrayList<Source>();

	abstract class URLField extends Container {

		private static final String STREAM_URLS = "StreamUrls";
		// private final LTextField textField;
		private final ComboBox<String> textField;
		private ButtonAdapter playStreamURL;

		public URLField(ElementManager screen) {
			super(screen);
			setLayoutManager(new BorderLayout());
			textField = new ComboBox<String>(screen) {
				@Override
				public void onChange(int selectedIndex, String value) {
					setURL(value);
				}

				@Override
				public void controlKeyPressHook(KeyInputEvent evt, String text) {
					if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
						if (getText().length() > 0) {
							setURL(getText());
						}
					}
				}
			};
			String statusListString = pref.get(prefKey + STREAM_URLS, "");
			String[] statusList = statusListString.split("\n");
			for (String s : statusList) {
				if (s.startsWith("http:") || s.startsWith("https:")) {
					textField.addListItem(s, s);
				}
			}

			// textField = new LTextField(screen);
			addChild(textField, BorderLayout.Border.CENTER);
			if (allowPreview) {
				Vector2f arrowSize = screen.getStyle("Common").getVector2f("arrowSize");
				playStreamURL = new ButtonAdapter(screen, arrowSize.add(new Vector2f(3, 3))) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						onURLPlay(textField.getText());
					}
				};
				playStreamURL.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowRight"));
				addChild(playStreamURL, BorderLayout.Border.EAST);
			}
		}

		String getURL() {
			return textField.getText();
		}

		void setURL(String url) {
			if (!url.startsWith("http:") && !url.startsWith("https:")) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (MenuItem<String> i : textField.getListItems()) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(i.getValue().toString());
				if (i.getValue().equals(url)) {
					// Already in list
					textField.setSelectedByValue(i.getValue(), false);
					return;
				}
			}
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(url);
			textField.addListItem(url, url);
			pref.put(prefKey + STREAM_URLS, sb.toString());
			textField.setSelectedByValue(sb.toString(), false);
			textField.setCaretPositionToStart();
			textField.selectTextRangeAll();

		}

		abstract void onURLPlay(String url);

	}

	public SoundSourceDialog(final ElementManager screen, String title, Collection<String> resources, Preferences pref,
			ChooserView view, boolean allowPreview, String prefKey, SoundFieldControl.Type type) {
		super(screen, title, resources, pref, view);

		this.type = type;
		this.prefKey = prefKey;
		this.allowPreview = allowPreview;

		if (type == org.iceui.controls.SoundFieldControl.Type.RESOURCE)
			content.setLayoutManager(
					new MigLayout(screen, "wrap 2, fill", "[shrink 0][]", "[shrink 0][shrink 0][fill, grow][shrink 0]"));
		else
			content.setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[shrink 0][]",
					"[shrink 0][shrink 0][shrink 0][shrink 0][shrink 0][shrink 0][fill, grow][shrink 0]"));

		bg = new RadioButtonGroup(screen) {
			@Override
			public void onSelect(int index, Button value) {
				source = Source.values()[index];
				setAvailable();
			}
		};

		// No sound
		noSound = new RadioButton(screen);
		noSound.setLabelText("No audio");
		bg.addButton(noSound);
		content.addChild(noSound, "span 2, growx");
		sources.add(Source.NONE);

		if (type == org.iceui.controls.SoundFieldControl.Type.ALL) {
			// Stream URL
			useStreamURL = new RadioButton(screen);
			useStreamURL.setLabelText("Stream from URL");
			bg.addButton(useStreamURL);
			content.addChild(useStreamURL, "span 2, growx");
			content.addChild(new Label("URL:", screen), "shrink 0");
			content.addChild(streamURL = new URLField(screen) {
				@Override
				void onURLPlay(String url) {
					SoundSourceDialog.this.onURLPlay(Source.STREAM_URL, getPlayURL());
				}
			}, "growx, shrink 200");
			sources.add(Source.STREAM_URL);

			// Download URL
			useDownloadURL = new RadioButton(screen);
			useDownloadURL.setLabelText("Download from URL");
			bg.addButton(useDownloadURL);
			content.addChild(useDownloadURL, "span 2, growx");
			content.addChild(new Label("URL:", screen), "shrink 0");
			content.addChild(downloadURL = new URLField(screen) {
				@Override
				void onURLPlay(String url) {
					SoundSourceDialog.this.onURLPlay(Source.DOWNLOAD_URL, getPlayURL());
				}
			}, "growx, shrink 200");
			sources.add(Source.DOWNLOAD_URL);
		}

		// Local resource
		useResource = new RadioButton(screen);
		useResource.setLabelText("Use sound resource");
		bg.addButton(useResource);
		content.addChild(useResource, "span 2, growx");
		content.addChild(panel, "span 2, growx, growy");
		sources.add(Source.RESOURCE);

		// Close Window
		FancyButton close = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (stop.getIsEnabled()) {
					onStop();
				}
				Source selectedSource = getSource();
				switch (selectedSource) {
				case NONE:
					if (onChosen(selectedSource, null)) {
						hideWindow();
					}
					break;
				case DOWNLOAD_URL:
					if (onChosen(selectedSource, downloadURL.getText())) {
						hideWindow();
					}
					break;
				case RESOURCE:
					if (onChosen(selectedSource, panel.getSelected())) {
						hideWindow();
					}
					break;
				case STREAM_URL:
					String url = getPlayURL();
					if (onChosen(selectedSource, url)) {
						hideWindow();
					}
					break;
				}
			}
		};
		close.setText("Select");

		if (allowPreview) {
			Container south = new Container(screen);
			south.setLayoutManager(new MigLayout(screen, "ins 0, wrap 2, fill", "[]push[]", "[]"));
			stop = new FancyButton(screen) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					onStop();
				}
			};
			stop.setIsEnabled(isAnyAudioPlaying());
			stop.setText("Stop");
			south.addChild(stop);
			south.addChild(close);
			content.addChild(south, "span 2, growx");
		} else {
			content.addChild(close, "span 2, al right");
		}

		//
		setWindowTitle(title);
		setDestroyOnHide(true);
		setFolder(null);
		setSource(Source.RESOURCE);
	}

	protected boolean isAnyAudioPlaying() {
		return false;
	}

	@Override
	protected ChooserPanel createPanel() {
		return new ChooserPanel(screen, resources, pref, view) {
			@Override
			protected void onItemChosen(String path) {
				if (onChosen(Source.RESOURCE, path)) {
					hideWindow();
				}
			}
		};
	}

	public Source getSource() {
		if (type == Type.ALL && useDownloadURL.getIsChecked()) {
			return Source.DOWNLOAD_URL;
		} else if (type == Type.ALL && useStreamURL.getIsChecked()) {
			return Source.STREAM_URL;
		} else if (noSound.getIsChecked()) {
			return Source.NONE;
		}
		return Source.RESOURCE;
	}

	public final void setSource(Source source) {
		bg.setSelected(sources.indexOf(source));
	}

	@Override
	protected final void onCloseChooser() {
		if (stop.getIsEnabled()) {
			onStop();
		}
	}

	public void setSelectedFile(String file, boolean callback) {
		if (file == null || file.equals("")) {
			setSource(Source.NONE);
		} else if (file.startsWith("http://") || file.startsWith("https://")) {
			Source newSource = Source.DOWNLOAD_URL;
			int idx = file.indexOf("?stream");
			if (idx == -1) {
				idx = file.indexOf("&stream");
			}
			if (idx != -1) {
				file = file.substring(0, idx) + file.substring(idx + 7);
				newSource = Source.STREAM_URL;
				streamURL.setURL(file);
			} else {
				downloadURL.setURL(file);
			}
			setSource(newSource);

		} else {
			setSource(Source.RESOURCE);
			super.setSelectedFile(file, callback);
		}
	}

	public void choose(String path) {
		panel.choose(path);
	}

	public void setStopAvailable(boolean stopAvailable) {
		stop.setIsEnabled(stopAvailable);
	}

	protected void onURLPlay(Source source, String path) {
	}

	protected void onStop() {
	}

	@Override
	protected final boolean onChosen(String path) {
		return onChosen(source, path);
	}

	protected abstract boolean onChosen(Source source, String path);

	private String getPlayURL() {
		if (source.equals(Source.DOWNLOAD_URL)) {
			return downloadURL.getURL();
		} else {
			String url = streamURL.getURL();
			if (url.indexOf('?') == -1) {
				url += "?stream";
			} else {
				url += "&stream";
			}
			return url;
		}
	}

	private void setAvailable() {
		if (type == org.iceui.controls.SoundFieldControl.Type.ALL) {
			streamURL.setIsEnabled(bg.getSelected().equals(useStreamURL));
			downloadURL.setIsEnabled(bg.getSelected().equals(useDownloadURL));
		}
		panel.setIsEnabled(bg.getSelected().equals(useResource));
	}
}
