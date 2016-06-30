/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iceui.controls.color;

import java.awt.Color;
import java.util.List;

import org.icelib.Icelib;
import org.iceui.IceUI;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.form.Form;
import icetone.controls.lists.Dial;
import icetone.controls.lists.IntegerRangeSliderModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FillLayout;
import icetone.core.layout.GridLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;

/**
 *
 * @author t0neg0d
 */
public abstract class XColorWheelTab extends Element implements XColorSelector.ColorTabPanel {

	private Dial<Integer> primarySelector;
	private Element colorSwatch;
	private Slider<Integer> redSlider, greenSlider, blueSlider, hueSlider, brightnessSlider, saturationSlider, alphaSlider;
	private final IntegerRangeSliderModel redModel;
	private final IntegerRangeSliderModel greenModel;
	private final IntegerRangeSliderModel blueModel;
	private final IntegerRangeSliderModel hueModel;
	private final IntegerRangeSliderModel satModel;
	private final IntegerRangeSliderModel lightModel;
	private Label lA;
	private IntegerRangeSliderModel alphaModel;
	private final boolean includeAlpha;
	private final Form colourForm;
	private Spinner<Integer> redSpinner;
	private Spinner<Integer> greenSpinner;
	private Spinner<Integer> blueSpinner;
	private Spinner<Integer> alphaSpinner;

	/**
	 * Creates a new instance of the XColorWheel control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public XColorWheelTab(ElementManager screen, boolean includeAlpha) {
		super(screen);
		this.includeAlpha = includeAlpha;
		setLayoutManager(new MigLayout(screen, "wrap 2", "[][grow]", "[]"));

		Vector2f wheelSize = screen.getStyle("ColorWheel").getVector2f("colorWheelSize");

		// Container element for swatch / dial
		String UID = getUID();
		primarySelector = new Dial<Integer>(screen, UID + ":primarySelector", Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO,
				screen.getStyle("ColorWheel").getString("colorWheelImg")) {
			@Override
			public void onChange(int selectedIndex, Integer value) {
				setHFromWheel();
				HSLToRGB();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};

		primarySelector.setButtonHoverInfo(null, null);
		primarySelector.setButtonPressedInfo(null, null);

		primarySelector.setDialImageIndicator(screen.getStyle("ColorWheel").getString("colorWheelSelectorImg"));

		colorSwatch = new Element(screen, UID + ":colorSwatch", Vector2f.ZERO, wheelSize.divide(4f), Vector4f.ZERO, null);
		colorSwatch.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		colorSwatch.setIgnoreGlobalAlpha(true);

		//
		Container swatchContainer = new Container(screen, new FillLayout());
		swatchContainer.addChild(primarySelector);
		swatchContainer.addChild(colorSwatch);

		//
		// Sliders
		//

		Element slidersContainer = new Container(screen);
		slidersContainer.setLayoutManager(new MigLayout(screen, "hidemode 2, wrap 3", "[grow][][]", "[]"));

		redSlider = new Slider<Integer>(screen, UID + ":sR", Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Integer value) {
				RGBToHSL();
				updateWheel();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};
		redSlider.setInterval(25);
		redSlider.setLockToStep(true);
		redSlider.setSliderModel(redModel = new IntegerRangeSliderModel(0, 255, 0, 2));
		redSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		slidersContainer.addChild(redSlider);

		Label lR = new Label(screen, UID + ":lR");
		lR.setTextVAlign(BitmapFont.VAlign.Center);
		lR.setText("R");
		slidersContainer.addChild(lR);

		redSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				redSlider.setSelectedValueWithCallback(value);
			}
		};
		redSpinner.setInterval(25);
		redSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		redSpinner.setType(TextField.Type.NUMERIC);
		redSpinner.setMaxLength(5);
		slidersContainer.addChild(redSpinner);

		greenSlider = new Slider<Integer>(screen, UID + ":sG", Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Integer value) {
				RGBToHSL();
				updateWheel();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};
		greenSlider.setInterval(25);
		greenSlider.setSliderModel(greenModel = new IntegerRangeSliderModel(0, 255, 0, 2));
		greenSlider.setLockToStep(true);
		greenSlider.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
		slidersContainer.addChild(greenSlider);

		Label lG = new Label(screen, UID + ":lG");
		lG.setTextVAlign(BitmapFont.VAlign.Center);
		lG.setText("G");
		slidersContainer.addChild(lG);

		greenSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				greenSlider.setSelectedValueWithCallback(value);
			}
		};
		greenSpinner.setInterval(25);
		greenSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		greenSpinner.setType(TextField.Type.NUMERIC);
		greenSpinner.setMaxLength(5);
		slidersContainer.addChild(greenSpinner);

		blueSlider = new Slider<Integer>(screen, UID + ":sB", Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Integer value) {
				RGBToHSL();
				updateWheel();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};
		blueSlider.setInterval(25);
		blueSlider.setLockToStep(true);
		blueSlider.setSliderModel(blueModel = new IntegerRangeSliderModel(0, 255, 0, 2));

		blueSlider.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
		slidersContainer.addChild(blueSlider);

		Label lB = new Label(screen, UID + ":lB");
		lB.setTextVAlign(BitmapFont.VAlign.Center);
		lB.setText("B");
		slidersContainer.addChild(lB);

		blueSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				blueSlider.setSelectedValueWithCallback(value);
			}
		};
		blueSpinner.setInterval(25);
		blueSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		blueSpinner.setType(TextField.Type.NUMERIC);
		blueSpinner.setMaxLength(5);
		slidersContainer.addChild(blueSpinner);

		hueSlider = new Slider<Integer>(screen, UID + ":sH", Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Integer value) {
				HSLToRGB();
				updateWheel();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};
		hueSlider.setLockToStep(true);
		hueSlider.setSliderModel(hueModel = new IntegerRangeSliderModel(0, 100, 0));

		Element hueC = new Element(screen);
		hueC.setAsContainerOnly();
		hueC.setLayoutManager(new FillLayout());
		hueC.addChild(getHueSliderBG());
		hueC.addChild(hueSlider);
		slidersContainer.addChild(hueC, "growx");

		Label lH = new Label(screen, UID + ":lH");
		lH.setTextVAlign(BitmapFont.VAlign.Center);
		lH.setText("H");
		slidersContainer.addChild(lH, "span 2");

		saturationSlider = new Slider<Integer>(screen, UID + ":sS", screen.getStyle("ColorWheel").getString("colorSImg"),
				Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Integer value) {
				HSLToRGB();
				updateWheel();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};
		saturationSlider.setLockToStep(true);
		saturationSlider.setSliderModel(satModel = new IntegerRangeSliderModel(0, 100, 0));
		saturationSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		saturationSlider.getElementMaterial().setBoolean("VertexColor", true);
		saturationSlider.getModel().setGradientFillHorizontal(ColorRGBA.Gray, new ColorRGBA(getRed(), getGreen(), getBlue(), 1.0f));
		slidersContainer.addChild(saturationSlider, "growx");

		Label lS = new Label(screen, UID + ":lS");
		lS.setTextVAlign(BitmapFont.VAlign.Center);
		lS.setText("S");
		slidersContainer.addChild(lS, "span 2");

		brightnessSlider = new Slider<Integer>(screen, UID + ":sL", screen.getStyle("ColorWheel").getString("colorLImg"),
				Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Integer value) {
				HSLToRGB();
				updateWheel();
				displayFactoredColor();
				XColorWheelTab.this.onChange(getColor());
			}
		};
		brightnessSlider.setLockToStep(true);
		brightnessSlider.setSliderModel(lightModel = new IntegerRangeSliderModel(0, 100, 0));

		brightnessSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		brightnessSlider.getElementMaterial().setBoolean("VertexColor", true);
		brightnessSlider.getModel().setGradientFillHorizontal(ColorRGBA.Black, getColorFullAlpha());
		slidersContainer.addChild(brightnessSlider, "growx");

		Label lL = new Label(screen, UID + ":lL");
		lL.setTextVAlign(BitmapFont.VAlign.Center);
		lL.setText("L");
		slidersContainer.addChild(lL, "span 2");

		if (includeAlpha) {
			alphaSlider = new Slider<Integer>(screen, UID + ":sA", screen.getStyle("ColorWheel").getString("colorLImg"),
					Orientation.HORIZONTAL, true) {
				@Override
				public void onChange(Integer value) {
					displayFactoredColor();
					XColorWheelTab.this.onChange(getColor());
				}
			};
			alphaSlider.setInterval(25);
			alphaSlider.setLockToStep(true);
			alphaSlider.setSliderModel(alphaModel = new IntegerRangeSliderModel(0, 100, 0));
			alphaSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			alphaSlider.getElementMaterial().setBoolean("VertexColor", true);
			alphaSlider.getModel().setGradientFillHorizontal(getColorNoAlpha(), getColorFullAlpha());
			slidersContainer.addChild(alphaSlider, "growx");

			lA = new Label(screen, UID + ":lA");
			lA.setTextVAlign(BitmapFont.VAlign.Center);
			lA.setText("A");
			slidersContainer.addChild(lA);

			alphaSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false) {
				@Override
				public void onChange(Integer value) {
					alphaSlider.setSelectedValueWithCallback(value);
				}
			};
			alphaSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
			alphaSpinner.setType(TextField.Type.NUMERIC);
			alphaSpinner.setMaxLength(5);
			alphaSpinner.setInterval(25);
			slidersContainer.addChild(alphaSpinner);

		}

		redSlider.setSelectedValue(255);
		hueSlider.setSelectedValue(100);
		saturationSlider.setSelectedValue(100);
		brightnessSlider.setSelectedValue(100);
		if (includeAlpha) {
			alphaSlider.setSelectedValue(100);
		}

		// Build containers
		addChild(swatchContainer);
		addChild(slidersContainer);

		// Form
		colourForm = new Form(screen);
		colourForm.addFormElement(primarySelector);
		colourForm.addFormElement(redSlider);
		colourForm.addFormElement(greenSlider);
		colourForm.addFormElement(blueSlider);
		colourForm.addFormElement(hueSlider);
		colourForm.addFormElement(saturationSlider);
		colourForm.addFormElement(brightnessSlider);
		colourForm.addFormElement(redSpinner);
		colourForm.addFormElement(greenSpinner);
		colourForm.addFormElement(blueSpinner);
		if (alphaSpinner != null) {
			colourForm.addFormElement(alphaSpinner);
		}

	}

	public void setPalette(List<ColorRGBA> palette) {
	}

	private void updateWheel() {
		int hIndex = hueModel.getValue() + 51;
		if (hIndex > 100) {
			hIndex -= 101;
		}
		primarySelector.setSelectedIndex(hIndex);
	}

	private Element getHueSliderBG() {
		Element spectrum = new Container(screen, new GridLayout(6, 1));

		Element bg1 = new Element(screen, getUID() + ":HSBG1");
		bg1.getModel().setGradientFillHorizontal(new ColorRGBA(1, 0, 0, 1), new ColorRGBA(1, 0, 1, 1));
		bg1.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg1.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addChild(bg1);

		Element bg2 = new Element(screen, getUID() + ":HSBG2");
		bg2.getModel().setGradientFillHorizontal(new ColorRGBA(1, 0, 1, 1), new ColorRGBA(0, 0, 1, 1));
		bg2.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg2.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addChild(bg2);

		Element bg3 = new Element(screen, getUID() + ":HSBG3");
		bg3.getModel().setGradientFillHorizontal(new ColorRGBA(0, 0, 1, 1), new ColorRGBA(0, 1, 1, 1));
		bg3.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg3.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addChild(bg3);

		Element bg4 = new Element(screen, getUID() + ":HSBG4");
		bg4.getModel().setGradientFillHorizontal(new ColorRGBA(0, 1, 1, 1), new ColorRGBA(0, 1, 0, 1));
		bg4.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg4.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addChild(bg4);

		Element bg5 = new Element(screen, getUID() + ":HSBG5");
		bg5.getModel().setGradientFillHorizontal(new ColorRGBA(0, 1, 0, 1), new ColorRGBA(1, 1, 0, 1));
		bg5.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg5.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addChild(bg5);

		Element bg6 = new Element(screen, getUID() + ":HSBG6");
		bg6.getModel().setGradientFillHorizontal(new ColorRGBA(1, 1, 0, 1), new ColorRGBA(1, 0, 0, 1));
		bg6.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg6.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addChild(bg6);

		return spectrum;
	}

	private void setHFromWheel() {
		int hIndex = primarySelector.getSelectedIndex();
		hIndex -= 51;
		if (hIndex < 0) {
			hIndex += 101;
		}
		hueSlider.setSelectedValue(hIndex);
	}

	private void RGBToHSL() {
		float[] hsv = new float[3];
		hsv = Color.RGBtoHSB(redModel.getValue(), greenModel.getValue(), blueModel.getValue(), hsv);
		hueSlider.setSelectedValue((int) (hsv[0] * 100));
		saturationSlider.setSelectedValue((int) (hsv[1] * 100));
		brightnessSlider.setSelectedValue((int) (hsv[2] * 100));
	}

	private void HSLToRGB() {
		int rgb = Color.HSBtoRGB(100 - getHue(), getSaturation(), getLight());
		redSlider.setSelectedValue((rgb >> 16) & 0xff);
		greenSlider.setSelectedValue((rgb >> 8) & 0xff);
		blueSlider.setSelectedValue(rgb & 0xff);
	}

	private float average() {
		float sum = getRed() + getGreen() + getBlue();
		return sum / 3;
	}

	private void displayFactoredColor() {
		redSpinner.setSelectedValue((int) (getRed() * 255f));
		greenSpinner.setSelectedValue((int) (getGreen() * 255f));
		blueSpinner.setSelectedValue((int) (getBlue() * 255f));
		if (includeAlpha) {
			alphaSpinner.setSelectedValue((int) (getAlpha() * 100f));
		}
		colorSwatch.getElementMaterial().setColor("Color", getColor());
		float av = average();
		saturationSlider.getModel().setGradientFillHorizontal(new ColorRGBA(av, av, av, 1.0f), getColorFullAlpha());
		brightnessSlider.getModel().setGradientFillHorizontal(ColorRGBA.Black, getColorFullAlpha());
		// colorToWhite.getModel().setGradientFillHorizontal(new ColorRGBA(red,
		// green, blue, 1.0f), ColorRGBA.White);
		if (includeAlpha) {
			alphaSlider.getModel().setGradientFillHorizontal(getColorNoAlpha(), getColorFullAlpha());
		}
	}

	public void setColor(ColorRGBA color) {
		System.out.println("setColor(" + color + ") = " + Icelib.toHexString(IceUI.fromRGBA(color)));
		redSlider.setSelectedValue(color == null ? 0 : Math.max(0, Math.min(255, (int) (color.r * 255))));
		greenSlider.setSelectedValue(color == null ? 0 : Math.max(0, Math.min(255, (int) (color.g * 255))));
		blueSlider.setSelectedValue(color == null ? 0 : Math.max(0, Math.min(255, (int) (color.b * 255))));
		if (includeAlpha) {
			alphaSlider.setSelectedValue(color == null ? 0 : Math.max(0, Math.min(100, (int) (color.a * 100))));
		}
		RGBToHSL();
		updateWheel();
		displayFactoredColor();
	}

	public void setColor(float red, float green, float blue) {
		System.out.println("setColor(" + red + "," + green + "," + blue + ")");
		redSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (red * 255))));
		greenSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (green * 255))));
		blueSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (blue * 255))));
		if (includeAlpha) {
			alphaSlider.setSelectedValue(100);
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setColor(float red, float green, float blue, float alpha) {
		System.out.println("setColor(" + red + "," + green + "," + blue + "," + alpha + ")");
		redSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (red * 255))));
		greenSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (green * 255))));
		blueSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (blue * 255))));
		if (includeAlpha) {
			alphaSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (alpha * 100))));
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setColor(int red, int green, int blue) {
		System.out.println("setColor(" + red + "," + green + "," + blue + ")");
		redSlider.setSelectedValue(Math.max(0, Math.min(255, red)));
		greenSlider.setSelectedValue(Math.max(0, Math.min(255, green)));
		blueSlider.setSelectedValue(Math.max(0, Math.min(255, blue)));
		if (includeAlpha) {
			alphaSlider.setSelectedValue(100);
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setRed(int red) {
		redSlider.setSelectedValue(Math.max(0, Math.min(255, red)));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setRed(float red) {
		redSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (red * 255))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setGreen(int green) {
		greenSlider.setSelectedValue(Math.max(0, Math.min(255, green)));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setGreen(float green) {
		greenSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (green * 255))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setBlue(int blue) {
		blueSlider.setSelectedValue(Math.max(0, Math.min(255, blue)));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setBlue(float blue) {
		blueSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (blue * 255))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setAlpha(float alpha) {
		if (alphaSlider == null) {
			throw new IllegalStateException("No alpha");
		}
		alphaSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (alpha * 100))));
		displayFactoredColor();
	}

	public void setHue(float hue) {
		hueSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (hue * 100))));
		HSLToRGB();
		displayFactoredColor();
	}

	public void setSaturation(float saturation) {
		saturationSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (saturation * 100))));
		HSLToRGB();
		displayFactoredColor();
	}

	public void setLight(float light) {
		brightnessSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (light * 100))));
		HSLToRGB();
		displayFactoredColor();
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
		return (float) redModel.getValue() / 255;
	}

	public final float getGreen() {
		return (float) greenModel.getValue() / 255f;
	}

	public final float getBlue() {
		return (float) blueModel.getValue() / 255f;
	}

	public final float getAlpha() {
		return (float) alphaModel.getValue() / 100f;
	}

	public float getHue() {
		return ((float) hueModel.getValue()) / 100f;
	}

	public float getSaturation() {
		return (float) satModel.getValue() / 100f;
	}

	public float getLight() {
		return (float) lightModel.getValue() / 100f;
	}

	public abstract void onChange(ColorRGBA color);
}
