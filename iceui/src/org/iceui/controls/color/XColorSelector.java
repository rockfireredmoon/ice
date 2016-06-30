package org.iceui.controls.color;

import java.util.ArrayList;
import java.util.List;

import org.icelib.Icelib;
import org.iceui.IceUI;
import org.iceui.controls.FancyButton;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.UIUtil;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.TabControl;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;

public abstract class XColorSelector extends FancyWindow {

	private List<ColorRGBA> palette;

	public enum ColorTab {

		WHEEL, PALETTE
	}

	public interface ColorTabPanel {

		public void onChange(ColorRGBA color);

		public void setColor(ColorRGBA color);

		public void setPalette(List<ColorRGBA> palette);
	}

	private TextField tfHex;
	private ButtonAdapter bFinish;
	private final boolean includeAlpha;
	private ColorRGBA color = ColorRGBA.White;
	private List<ColorTabPanel> panels = new ArrayList<ColorTabPanel>();
	private boolean allowUnset;
	private Element buttons;
	private FancyButton unset;

	/**
	 * Creates a new instance of the XColorSelector control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param includeAlpha
	 *            include alpha component
	 * @param showHex
	 *            show hex values
	 * @param tabs
	 *            the tabs to display
	 */
	public XColorSelector(ElementManager screen, Vector2f position, boolean includeAlpha, boolean showHex, ColorTab... tabs) {
		super(screen, position, LUtil.LAYOUT_SIZE, Size.SMALL, true);
		String UID = getUID();

		this.includeAlpha = includeAlpha;
		content.setLayoutManager(new MigLayout(screen, "ins 0, wrap 1, fill", "[grow]", "[grow][]"));

		// Container element for buttons
		buttons = new Element(screen);
		buttons.setLayoutManager(new FlowLayout(8, BitmapFont.Align.Center));

		// Tabs
		if (tabs.length > 1) {
			TabControl colorTabs = new TabControl(screen);
			int ti = 0;
			for (ColorTab t : tabs) {
				Element tabComponent = createTab(t);
				panels.add((ColorTabPanel) tabComponent);

				// TabPanelContent tpc = new TabPanelContent(screen,
				// LUtil.LAYOUT_SIZE);
				// tpc.addChild(tabComponent);

				colorTabs.addTab(Icelib.toEnglish(t), tabComponent);
				// colorTabs.addTabChild(ti++, tpc);
			}
			content.addChild(colorTabs);
		} else {
			Element tabComponent = createTab(tabs[0]);
			panels.add((ColorTabPanel) tabComponent);
			content.addChild(tabComponent);
		}

		// Buttons

		if (showHex) {
			Label lHex = new Label(screen, UID + ":lHex");
			lHex.setTextVAlign(BitmapFont.VAlign.Center);
			lHex.setText("HEX: #");
			buttons.addChild(lHex);

			tfHex = new TextField(screen, UID + ":tfHex") {
				@Override
				public void controlKeyPressHook(KeyInputEvent evt, String text) {
					if (!text.equals("") && text.length() == 6
							&& (Character.isDigit(evt.getKeyChar()) || ((Character.toLowerCase(evt.getKeyChar()) >= 'a')
									&& Character.toLowerCase(evt.getKeyChar()) <= 'f'))) {
						try {
							setColor(UIUtil.fromColorString(text));
						} catch (IllegalArgumentException nfe) {
						}
					}
				}
			};
			tfHex.setType(TextField.Type.ALPHANUMERIC_NOSPACE);
			tfHex.setMaxLength(6);
			buttons.addChild(tfHex);
		}

		bFinish = new FancyButton(screen, UID + ":bFiniah") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
				onComplete(getColor());
				hideWindow();
			}
		};
		bFinish.setText("Done");
		buttons.addChild(bFinish);

		// Build containers
		content.addChild(buttons, "ax 50%");

		// Container
		setIsResizable(false);
		pack(false);

	}

	public void setAllowUnset(boolean allowUnset) {
		if (allowUnset != this.allowUnset) {
			if (allowUnset) {
				buttons.addChild(unset = new FancyButton("Unset", screen) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						setColor(null);
					}

				});
			} else {
				buttons.removeChild(unset);
			}
			this.allowUnset = allowUnset;
		}
	}

	private Element createTab(ColorTab tab) {

		switch (tab) {
		case PALETTE:
			return new XColorPaletteTab(screen) {
				@Override
				public void onChange(ColorRGBA color) {
					colorChangedFromTab(this, color);
				}
			};
		case WHEEL:
			return new XColorWheelTab(screen, includeAlpha) {
				@Override
				public void onChange(ColorRGBA color) {
					colorChangedFromTab(this, color);
				}
			};
		}
		throw new IllegalArgumentException();
	}

	public List<ColorRGBA> getPalette() {
		return palette;
	}

	public void setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		for (ColorTabPanel p : panels) {
			p.setPalette(palette);
		}
		content.layoutChildren();
	}

	public void setColor(ColorRGBA color) {
		this.color = color == null ? null : color.clone();
		for (ColorTabPanel c : panels) {
			c.setColor(this.color);
		}
		updateHEX();
	}

	public final ColorRGBA getColorNoAlpha() {
		return new ColorRGBA(getRed(), getGreen(), getBlue(), 0);
	}

	public final ColorRGBA getColorFullAlpha() {
		return new ColorRGBA(getRed(), getGreen(), getBlue(), 1);
	}

	public final ColorRGBA getColor() {
		if (includeAlpha) {
			return new ColorRGBA(getRed(), getGreen(), getBlue(), getAlpha());
		} else {
			return getColorFullAlpha();
		}
	}

	public final float getRed() {
		return color.r;
	}

	public final float getGreen() {
		return color.g;
	}

	public final float getBlue() {
		return color.b;
	}

	public final float getAlpha() {
		return color.a;
	}

	public abstract void onChange(ColorRGBA color);

	public abstract void onComplete(ColorRGBA color);

	protected void updateHEX() {
		if (tfHex != null) {
			String hex = String.format("%02x%02x%02x", (int) (getRed() * 255), (int) (getGreen() * 255), (int) (getBlue() * 255));
			if (includeAlpha) {
				hex += String.format("%02x", (int) (getAlpha() * 100f));
			}
			tfHex.setText(hex);
		}
	}

	private void colorChangedFromTab(ColorTabPanel panel, ColorRGBA color) {
		this.color = color.clone();
		for (ColorTabPanel p : panels) {
			if (!p.equals(panel)) {
				p.setColor(this.color);
			}
		}
		updateHEX();
		onChange(this.color);
	}
}
