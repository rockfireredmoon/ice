package org.iceui.controls.color;

import java.util.List;

import org.iceui.controls.Swatch;
import org.iceui.controls.color.XColorSelector.ColorTab;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.form.Form;
import icetone.core.ElementManager;

public abstract class ColorButton extends ButtonAdapter {

	private XColorSelector chooser;
	private ColorRGBA value;
	private boolean includeAlpha;
	private String chooserText = "Choose Color";
	private List<ColorRGBA> palette;
	private ColorTab[] tabs = new ColorTab[] { ColorTab.WHEEL };
	private Swatch swatch;
	private boolean showChooserHex;

	public ColorButton(ElementManager screen, ColorRGBA initial) {
		this(screen, initial, false);
	}

	public ColorButton(ElementManager screen, ColorRGBA initial, boolean includeAlpha) {
		this(screen, initial, includeAlpha, false);
	}

	public ColorButton(ElementManager screen, ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		super(screen, Vector2f.ZERO, screen.getStyle("ColorButton").getVector2f("defaultSize"),
				screen.getStyle("ColorButton").getVector4f("resizeBorders"),
				screen.getStyle("ColorButton").getString("defaultImg"));
		init(initial, includeAlpha, showChooserHex);
	}

	public ColorButton(ElementManager screen, String UID, ColorRGBA initial) {
		this(screen, UID, initial, false);
	}

	public ColorButton(ElementManager screen, String UID, ColorRGBA initial, boolean includeAlpha) {
		this(screen, UID, initial, includeAlpha, false);
	}

	public ColorButton(ElementManager screen, String UID, ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		super(screen, UID, Vector2f.ZERO, screen.getStyle("ColorButton").getVector2f("defaultSize"),
				screen.getStyle("ColorButton").getVector4f("resizeBorders"),
				screen.getStyle("ColorButton").getString("defaultImg"));
		init(initial, includeAlpha, showChooserHex);
	}

	public ColorTab[] getTabs() {
		return tabs;
	}

	public void setTabs(ColorTab... tabs) {
		this.tabs = tabs;
	}

	@Override
	public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
		showChooser(evt.getX(), evt.getY());
	}

	@Override
	protected void onAfterLayout() {
		Vector2f swatchSize = getDimensions().clone();
		swatchSize.x -= borders.y + borders.z;
		swatchSize.y -= borders.x + borders.w;
		swatch.setDimensions(swatchSize);
		swatch.setPosition(new Vector2f(borders.y, ((getHeight() - swatch.getHeight()) / 2)));
	}

	private void init(ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		setStyles("ColorButton");
		value = initial;
		this.includeAlpha = includeAlpha;
		this.showChooserHex = showChooserHex;
		swatch = new Swatch(screen);
		swatch.setDocking(null);
		swatch.setIgnoreMouse(true);

		addChild(swatch);

		// Set controls to initial value
		updateControls();
	}

	public void addToForm(Form form) {
		form.addFormElement(this);
	}

	public void setValue(ColorRGBA value) {
		this.value = value;
		updateControls();
	}

	public ColorRGBA getValue() {
		return value;
	}

	public String getChooserText() {
		return chooserText;
	}

	public void setChooserText(String chooserText) {
		this.chooserText = chooserText;
	}

	protected abstract void onChangeColor(ColorRGBA newColor);

	protected void onBeforeShowChooser() {
	}

	public void hideChooser() {
		if (chooser != null) {
			chooser.hideWithEffect();
			screen.removeElement(chooser);
			chooser = null;
		}
	}

	private void showChooser(float x, float y) {
		onBeforeShowChooser();
		chooser = new XColorSelector(screen, this.getPosition(), includeAlpha, showChooserHex, tabs) {
			@Override
			public void onChange(ColorRGBA crgba) {
				value = crgba;
				updateControls();
			}

			@Override
			public void onComplete(ColorRGBA crgba) {
				value = crgba;
				updateControls();
				onChangeColor(value);
			}
		};
		if (chooserText != null) {
			chooser.setWindowTitle(chooserText);
		}
		if (palette != null) {
			chooser.setPalette(palette);
		}
		chooser.setDestroyOnHide(true);
		chooser.setColor(value);
		chooser.sizeToContent();
		chooser.setIsResizable(false);
		float cx = x + 20;
		if (cx + chooser.getWidth() > screen.getWidth()) {
			cx = screen.getWidth() - chooser.getWidth();
		}
		chooser.setPosition(cx, screen.getHeight() - y - (chooser.getHeight() / 2));
		screen.addElement(chooser, null, true);
		chooser.showAsModal(true);
	}

	private void updateControls() {
		swatch.setColor(value);
	}

	public void setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (chooser != null) {
			chooser.setPalette(palette);
		}
	}
}
