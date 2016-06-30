package org.iceui.controls.chooser;

import org.icelib.Icelib;

import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class SoundButton extends Element {

	private final ChooserButton selectButton;
	private final ButtonAdapter playButton;
	private final String path;

	public SoundButton(final ChooserPanel chooser, float previewSize, final String path, ElementManager screen) {
		super(screen);

		this.path = path;
		Vector2f arrowSize = screen.getStyle("Common").getVector2f("arrowSize");

		//
		Label xLabel = new Label(Icelib.getBaseFilename(path), screen);
		xLabel.setTextWrap(LineWrapMode.Character);
		selectButton = new ChooserButton(path, screen.getStyle("Common").getString("audioIcon"), screen, chooser, previewSize);
		playButton = new ButtonAdapter(screen, arrowSize.add(new Vector2f(3, 3))) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onPlay();
			}
		};
		playButton.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowRight"));

		//
		Container b = new Container(screen);
		b.setLayoutManager(new BorderLayout());
		b.addChild(xLabel, BorderLayout.Border.CENTER);
		b.addChild(playButton, BorderLayout.Border.EAST);

		//
		setLayoutManager(new MigLayout(screen, "wrap 1", "[]", "[][]"));
		addChild(b);
		addChild(selectButton, "ax 50%");
	}

	public String getPath() {
		return path;
	}

	public ButtonAdapter getPlayButton() {
		return playButton;
	}

	protected void onPlay() {
	}
}
