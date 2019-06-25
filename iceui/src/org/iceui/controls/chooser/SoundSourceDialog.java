package org.iceui.controls.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.iceui.controls.SoundFieldControl;
import org.iceui.controls.SoundFieldControl.Type;

import com.jme3.input.KeyInput;

import icetone.controls.buttons.ButtonGroup;
import icetone.controls.buttons.PushButton;
import icetone.controls.buttons.RadioButton;
import icetone.controls.lists.ComboBox;
import icetone.controls.menuing.MenuItem;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.AbstractChooserDialog;
import icetone.extras.chooser.ChooserModel;
import icetone.extras.chooser.ChooserPanel;
import icetone.extras.chooser.ChooserPanel.ChooserView;

/**
 */
public abstract class SoundSourceDialog extends AbstractChooserDialog<String> {

	private final ButtonGroup<RadioButton<Source>> bg;
	private final RadioButton<Source> useResource;
	private RadioButton<Source> useDownloadURL;
	private RadioButton<Source> useStreamURL;
	private final boolean allowPreview;
	private final RadioButton<Source> noSound;

	public enum Source {

		NONE, STREAM_URL, DOWNLOAD_URL, RESOURCE
	}

	private URLField streamURL;
	private URLField downloadURL;
	private Source source = Source.NONE;
	private PushButton stop;
	private String prefKey;
	private org.iceui.controls.SoundFieldControl.Type type;
	private List<Source> sources = new ArrayList<Source>();

	abstract class URLField extends StyledContainer {

		private static final String STREAM_URLS = "StreamUrls";
		// private final LTextField textField;
		private final ComboBox<String> textField;
		private PushButton playStreamURL;

		public URLField(BaseScreen screen) {
			super(screen);
			setLayoutManager(new BorderLayout());
			textField = new ComboBox<String>(screen);
			String statusListString = pref.get(prefKey + STREAM_URLS, "");
			String[] statusList = statusListString.split("\n");
			for (String s : statusList) {
				if (s.startsWith("http:") || s.startsWith("https:")) {
					textField.addComboItem(s, s);
				}
			}

			// textField = new LTextField(screen);
			addElement(textField, Border.CENTER);
			if (allowPreview) {
				playStreamURL = new PushButton(screen) {
					{
						setStyleClass("play-button");
					}
				};
				addElement(playStreamURL, Border.EAST);
			}

			// Events
			textField.onKeyboardPressed(evt -> {
				if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
					if (getText().length() > 0) {
						setURL(getText());
					}
				}
			});
			textField.onChange(evt -> setURL(evt.getNewValue()));
			playStreamURL.onMouseReleased(evt -> onURLPlay(textField.getText()));
		}

		String getURL() {
			return textField.getText();
		}

		void setURL(String url) {
			if (!url.startsWith("http:") && !url.startsWith("https:")) {
				return;
			}
			if (url.equals(textField.getSelectedValue()))
				return;
			StringBuilder sb = new StringBuilder();
			for (MenuItem<String> i : textField.getListItems()) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(i.getValue().toString());
				if (i.getValue().equals(url)) {
					// Already in list
					textField.runAdjusting(() -> textField.setSelectedByValue(i.getValue()));
					return;
				}
			}
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(url);
			textField.addComboItem(url, url);
			pref.put(prefKey + STREAM_URLS, sb.toString());
			textField.runAdjusting(() -> textField.setSelectedByValue(sb.toString()));
			textField.getTextField().setCaretPositionToStart();
			textField.getTextField().selectTextRangeAll();

		}

		abstract void onURLPlay(String url);

	}

	public SoundSourceDialog(final BaseScreen screen, String styleId, String title,
			ChooserModel<String> resources, Preferences pref, ChooserView<String> view, boolean allowPreview,
			String prefKey, SoundFieldControl.Type type) {
		super(screen, styleId, title, resources, pref, view);

		this.type = type;
		this.prefKey = prefKey;
		this.allowPreview = allowPreview;

		if (type == org.iceui.controls.SoundFieldControl.Type.RESOURCE)
			content.setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[shrink 0][]",
					"[shrink 0][shrink 0][fill, grow][shrink 0]"));
		else
			content.setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[shrink 0][]",
					"[shrink 0][shrink 0][shrink 0][shrink 0][shrink 0][shrink 0][fill, grow][shrink 0]"));

		bg = new ButtonGroup<RadioButton<Source>>();

		// No sound
		noSound = new RadioButton<Source>(screen).setValue(Source.NONE);
		noSound.setText("No audio");
		bg.addButton(noSound);
		content.addElement(noSound, "span 2, growx");
		sources.add(Source.NONE);

		if (type == org.iceui.controls.SoundFieldControl.Type.ALL) {
			// Stream URL
			useStreamURL = new RadioButton<Source>(screen).setValue(Source.STREAM_URL);
			useStreamURL.setText("Stream from URL");
			bg.addButton(useStreamURL);
			content.addElement(useStreamURL, "span 2, growx");
			content.addElement(new Label(screen, "URL:"), "shrink 0");
			content.addElement(streamURL = new URLField(screen) {
				@Override
				void onURLPlay(String url) {
					SoundSourceDialog.this.onURLPlay(Source.STREAM_URL, getPlayURL());
				}
			}, "growx, shrink 200");
			sources.add(Source.STREAM_URL);

			// Download URL
			useDownloadURL = new RadioButton<Source>(screen).setValue(Source.DOWNLOAD_URL);
			useDownloadURL.setText("Download from URL");
			bg.addButton(useDownloadURL);
			content.addElement(useDownloadURL, "span 2, growx");
			content.addElement(new Label(screen, "URL:"), "shrink 0");
			content.addElement(downloadURL = new URLField(screen) {
				@Override
				void onURLPlay(String url) {
					SoundSourceDialog.this.onURLPlay(Source.DOWNLOAD_URL, getPlayURL());
				}
			}, "growx, shrink 200");
			sources.add(Source.DOWNLOAD_URL);
		}

		// Local resource
		useResource = new RadioButton<Source>(screen).setValue(Source.DOWNLOAD_URL);
		useResource.setText("Use sound resource");
		bg.addButton(useResource);
		content.addElement(useResource, "span 2, growx");
		content.addElement(panel, "span 2, growx, growy");
		sources.add(Source.RESOURCE);

		// Close Window
		PushButton close = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		close.onMouseReleased(evt -> {
			if (stop.isEnabled()) {
				onStop();
			}
			Source selectedSource = getSource();
			switch (selectedSource) {
			case NONE:
				choose(null, getSelected(), false);
				break;
			case DOWNLOAD_URL:
				choose(downloadURL.getText(), getSelected(), false);
				break;
			case RESOURCE:
				choose(panel.getSelected(), getSelected(), false);
				break;
			case STREAM_URL:
				String url = getPlayURL();
				choose(url, getSelected(), false);
				break;
			}
		});
		close.setText("Select");

		if (allowPreview) {
			StyledContainer south = new StyledContainer(screen);
			south.setLayoutManager(new MigLayout(screen, "ins 0, wrap 2, fill", "[]push[]", "[]"));
			stop = new PushButton(screen) {
				{
					setStyleClass("fancy");
				}
			};
			stop.onMouseReleased(evt -> onStop());
			stop.setEnabled(isAnyAudioPlaying());
			stop.setText("Stop");
			south.addElement(stop);
			south.addElement(close);
			content.addElement(south, "span 2, growx");
		} else {
			content.addElement(close, "span 2, al right");
		}

		//
		setWindowTitle(title);
		setDestroyOnHide(true);
		setFolder(null);
		setSource(Source.RESOURCE);

		onElementEvent(evt -> {
			if (stop.isEnabled()) {
				onStop();
			}
		}, icetone.core.event.ElementEvent.Type.HIDDEN);

		bg.onChange(evt -> {
			source = evt.getSource().getSelected().getValue();
			setAvailable();
		});
	}

	protected boolean isAnyAudioPlaying() {
		return false;
	}

	@Override
	protected ChooserPanel<String> createPanel() {
		return new ChooserPanel<String>(screen, resources, pref, view);
		// {
		// @Override
		// protected void onItemChosen(String path) {
		// if (onChosen(Source.RESOURCE, path)) {
		// hide();
		// }
		// }
		// };
	}

	public Source getSource() {
		if (type == Type.ALL && useDownloadURL.getState()) {
			return Source.DOWNLOAD_URL;
		} else if (type == Type.ALL && useStreamURL.getState()) {
			return Source.STREAM_URL;
		} else if (noSound.getState()) {
			return Source.NONE;
		}
		return Source.RESOURCE;
	}

	public final void setSource(Source source) {
		bg.setSelected(sources.indexOf(source));
	}

	public void setSelectedFile(String file) {
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
			super.setSelectedFile(file);
		}
	}

	public void choose(String path) {
		panel.choose(path);
	}

	public void setStopAvailable(boolean stopAvailable) {
		stop.setEnabled(stopAvailable);
	}

	protected void onURLPlay(Source source, String path) {
	}

	protected void onStop() {
	}

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
			streamURL.setEnabled(bg.getSelected().equals(useStreamURL));
			downloadURL.setEnabled(bg.getSelected().equals(useDownloadURL));
		}
		panel.setEnabled(bg.getSelected().equals(useResource));
	}
}
