package org.iceui.controls;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.form.Form;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public abstract class Vector2fControl extends Element {

	public static final Vector2f SPINNER_SIZE = new Vector2f(90, 20);
	private final Vector2f val;
	private boolean adjusting;
	private final Spinner<Float> x;
	private final Spinner<Float> y;
	private Spinner<Float> a;

	public Vector2fControl(ElementManager screen, float min, float max, float inc, Vector2f initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public Vector2fControl(ElementManager screen, float min, float max, float inc, Vector2f initial, boolean cycle, boolean all) {
		this(screen, UIDUtil.getUID(), min, max, inc, initial, cycle, all);
	}

	public Vector2fControl(ElementManager screen, String UID, float min, float max, float inc, Vector2f initial, boolean all) {
		this(screen, UID, min, max, inc, initial, false, all);
	}

	public Vector2fControl(ElementManager screen, String UID, float min, float max, float inc, Vector2f initial, boolean cycle,
			boolean all) {
		super(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		val = initial.clone();
		setLayoutManager(new MigLayout(screen, "ins 0", "[grow][]"));
		adjusting = true;

		// all
		if (all) {
			a = new Spinner<Float>(screen, getUID() + ":A", Vector2f.ZERO, Orientation.HORIZONTAL, cycle) {
				@Override
				public void onChange(Float newValue) {
					if (!adjusting) {
						val.x = newValue;
						x.setSelectedValue(newValue);
						val.y = newValue;
						y.setSelectedValue(newValue);
						onChangeVector(val);
					}
				}
			};
			a.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
			addChild(a);
			createLabel(screen, "*");
		}

		// x
		x = new Spinner<Float>(screen, getUID() + ":X", Vector2f.ZERO, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.x = (Float) newValue;
					setA();
					onChangeVector(val);
				}
			}
		};
		x.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
		addChild(x);
		createLabel(screen, "X");

		// y
		y = new Spinner<Float>(screen, getUID() + ":Y", Vector2f.ZERO, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.y = newValue;
					setA();
					onChangeVector(val);
				}
			}
		};
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.y));
		addChild(y);
		createLabel(screen, "Y");

		//
		adjusting = false;
		setInterval(5f);
	}

	public void addToForm(Form form) {
		if (a != null) {
			form.addFormElement(a);
		}
		form.addFormElement(x);
		form.addFormElement(y);
	}

	@Override
	public void setFont(String fontPath) {
		super.setFont(fontPath);
		if (a != null) {
			a.setFont(fontPath);
		}
		x.setFont(fontPath);
		y.setFont(fontPath);
	}

	@Override
	public void setFontSize(float fontSize) {
		super.setFontSize(fontSize);
		if (a != null) {
			a.setFontSize(fontSize);
		}
		x.setFontSize(fontSize);
		y.setFontSize(fontSize);
	}

	@Override
	public void setFontColor(ColorRGBA fontColor) {
		super.setFontColor(fontColor);
		if (a != null) {
			a.setFontColor(fontColor);
		}
		x.setFontColor(fontColor);
		y.setFontColor(fontColor);
	}

	public void setInterval(float callsPerSeconds) {
		if (a != null) {
			a.setInterval(callsPerSeconds);
		}
		x.setInterval(callsPerSeconds);
		y.setInterval(callsPerSeconds);
	}

	public void setValue(Vector2f newValue) {
		this.val.set(newValue);
		this.x.setSelectedValue(this.val.x);
		this.y.setSelectedValue(this.val.y);
		setA();
	}

	public void setValueWithCallback(Vector2f newValue) {
		setValue(newValue);
		onChangeVector(newValue);
	}

	protected abstract void onChangeVector(Vector2f newValue);

	public Vector2f getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			float avg = (val.x + val.y) / 2;
			a.setSelectedValue(avg);
		}
	}

	private void createLabel(ElementManager screen, String text) {
		Label label = new Label(screen, getUID() + ":" + text + ":Label");
		label.setText(text);
		ElementStyle.small(screen, label);
		addChild(label, "wrap");
	}

	public void snapToIncr(boolean withCallback) {
		Float cx = (Float) x.getSpinnerModel().getCurrentValue();
		Float cy = (Float) y.getSpinnerModel().getCurrentValue();
		((FloatRangeSpinnerModel) x.getSpinnerModel()).snapToIncr();
		((FloatRangeSpinnerModel) y.getSpinnerModel()).snapToIncr();

		Float nx = (Float) x.getSpinnerModel().getCurrentValue();
		Float ny = (Float) y.getSpinnerModel().getCurrentValue();

		if (withCallback) {
			if (!cx.equals(nx) || !cy.equals(ny)) {
				x.setSelectedValue(nx);
				y.setSelectedValue(ny);
				setA();
				onChangeVector(getValue());
			}
		}
	}
}
