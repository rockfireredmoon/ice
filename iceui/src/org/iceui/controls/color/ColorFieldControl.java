package org.iceui.controls.color;

import java.util.List;

import org.iceui.controls.Swatch;
import org.iceui.controls.UIUtil;
import org.iceui.controls.color.XColorSelector.ColorTab;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.form.Form;
import icetone.controls.text.TextField;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public class ColorFieldControl extends Element {

	private XColorSelector chooser;
	private ColorRGBA value;
	private TextField textField;
	private Swatch colorSwatch;
	private boolean includeAlpha;
	private Button chooserButton;
	private String chooserText = "Choose Color";
	private List<ColorRGBA> palette;
	private ColorTab[] tabs = new ColorTab[] { ColorTab.WHEEL };
	private boolean showHexInChooser;
	private boolean allowUnset;

	public ColorFieldControl(ColorRGBA initial) {
		this(Screen.get(), initial);
	}

	public ColorFieldControl(ElementManager screen, ColorRGBA initial) {
		this(screen, initial, false);
	}

	public ColorFieldControl(ElementManager screen, ColorRGBA initial, boolean includeAlpha) {
		super(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE);
		init(initial, includeAlpha, true, true);
	}

	public ColorFieldControl(ElementManager screen, String UID, ColorRGBA initial) {
		this(screen, UID, initial, false);
	}

	public ColorFieldControl(ElementManager screen, String UID, ColorRGBA initial, boolean includeAlpha) {
		this(screen, UID, initial, includeAlpha, true, true);
	}

	public ColorFieldControl(ElementManager screen, ColorRGBA initial, boolean includeAlpha, boolean showHex,
			boolean showChooserButton) {
		super(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE);
		init(initial, includeAlpha, showHex, showChooserButton);
	}

	public ColorFieldControl(ElementManager screen, String UID, ColorRGBA initial, boolean includeAlpha, boolean showHex,
			boolean showChooserButton) {
		super(screen, UID, LUtil.LAYOUT_SIZE);
		init(initial, includeAlpha, showHex, showChooserButton);
	}

	@Override
	public void setToolTipText(String toolTip) {
		super.setToolTipText(toolTip);
		if (textField != null) {
			textField.setToolTipText(toolTip);
			if (chooserButton != null) {
				chooserButton.setToolTipText("Choose Colour");
			}
		} else if (chooserButton != null) {
			chooserButton.setToolTipText(toolTip);
		}
	}

	public boolean isAllowUnset() {
		return allowUnset;
	}

	public void setAllowUnset(boolean allowUnset) {
		this.allowUnset = allowUnset;
	}

	public boolean isShowHexInChooser() {
		return showHexInChooser;
	}

	public ColorTab[] getTabs() {
		return tabs;
	}

	public void setTabs(ColorTab... tabs) {
		this.tabs = tabs;
	}

	public void setShowHexInChooser(boolean showHexInChooser) {
		this.showHexInChooser = showHexInChooser;
	}

	protected void init(ColorRGBA initial, boolean includeAlpha, boolean showHex, final boolean showChooserButton) {
		value = initial;
		this.includeAlpha = includeAlpha;

		// Configure layout depending on options
		if (showHex) {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0]2[][shrink 0]", "[fill, grow]"));
			} else {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0][]", "[fill, grow]"));
			}
		} else {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0]2[shrink 0]", "[fill, grow]"));
			} else {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0]", "[fill, grow]"));
			}
		}

		// Swatch
		colorSwatch = new Swatch(screen, getUID() + ":colorSwatch") {
			public void onMouseLeftReleased(MouseButtonEvent evt) {
				if (!showChooserButton) {
					showChooser(evt.getX(), evt.getY());
				}
			}
		};
		colorSwatch.setColor(value == null? ColorRGBA.Black : value);
		addChild(colorSwatch, "width 22, height 22");

		// Text field
		if (showHex) {
			textField = new TextField(screen) {
				@Override
				public void controlTextFieldResetTabFocusHook() {
					try {
						value = UIUtil.fromColorString(getText(), ColorFieldControl.this.includeAlpha);
						colorSwatch.setColor(value);
						onChangeColor(value);
					} catch (IllegalArgumentException iae) {
						updateControls();
					}
				}
			};
			textField.setColorMap(screen.getStyle("ComboBox").getString("defaultImg"));
			textField.setMaxLength(includeAlpha ? 9 : 7);
			addChild(textField);
		}

		// Chooser
		if (showChooserButton) {
			chooserButton = new ButtonAdapter(screen) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					showChooser(evt.getX(), evt.getY());
				}
			};
			chooserButton.setColorMap(screen.getStyle("ComboBox").getString("buttonImg"));
			chooserButton.setButtonHoverInfo(screen.getStyle("ComboBox").getString("buttonHoverImg"), null);
			chooserButton.setButtonPressedInfo(screen.getStyle("ComboBox").getString("buttonPressedImg"), null);
			chooserButton.setToolTipText("Choose Colour");
			chooserButton.setButtonIcon(-1, -1, "BuildIcons/Icon-32-Build-PickColor.png");
			addChild(chooserButton, "wrap");
		}

		// Set controls to initial value
		updateControls();
	}

	public void addToForm(Form form) {
		if (textField != null) {
			form.addFormElement(textField);
		}
		if (chooserButton != null) {
			form.addFormElement(chooserButton);
		}
	}

	@Override
	public void setFont(String fontPath) {
		super.setFont(fontPath);
		if (textField != null) {
			textField.setFont(fontPath);
		}
	}

	@Override
	public void setFontSize(float fontSize) {
		super.setFontSize(fontSize);
		if (textField != null) {
			textField.setFontSize(fontSize);
		}
	}

	@Override
	public void setFontColor(ColorRGBA fontColor) {
		super.setFontColor(fontColor);
		if (textField != null) {
			textField.setFontColor(fontColor);
		}
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

	protected void onChangeColor(ColorRGBA newColor) {
	}

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
		
		if(chooser != null) {
			if(!chooser.getIsVisible()) 
				chooser.show();
//			chooser.bringToFront();
			return;
		}
		chooser = new XColorSelector(screen, this.getPosition(), includeAlpha, showHexInChooser, tabs) {
			@Override
			public void onChange(ColorRGBA crgba) {
				value = crgba;
				updateControls();
			}

			@Override
			public void onComplete(ColorRGBA crgba) {
				value = crgba;
				updateControls();
				screen.removeElement(this);
				onChangeColor(value);
			}

			@Override
			protected void onCloseWindow() {
				chooser = null;
			}
		};
		if (chooserText != null) {
			chooser.setWindowTitle(chooserText);
		}
		if (palette != null) {
			chooser.setPalette(palette);
		}
		chooser.setAllowUnset(allowUnset); 
		LUtil.setPosition(chooser, x, y);
		chooser.setLockToParentBounds(true);
		chooser.setDestroyOnHide(true);
		chooser.setColor(value);
		chooser.sizeToContent();
		chooser.setIsResizable(false);
		screen.addElement(chooser);
//		chooser.bringToFront();
	}

	private void updateControls() {
        if (textField != null) {
            textField.setText(value == null ? "" : UIUtil.toHexString(value, includeAlpha));
        }
        colorSwatch.setColor(value == null? ColorRGBA.Black : value);
    }

	public void setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (chooser != null) {
			chooser.setPalette(palette);
		}
	}
}
