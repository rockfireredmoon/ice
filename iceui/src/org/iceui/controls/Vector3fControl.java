package org.iceui.controls;

import java.math.BigDecimal;
import java.util.Objects;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
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

public abstract class Vector3fControl extends Element {

	private final Vector3f val;
	private boolean adjusting;
	private final Spinner<Float> x;
	private final Spinner<Float> y;
	private final Spinner<Float> z;
	private Spinner<Float> a;
	private int precision;

	public Vector3fControl(ElementManager screen, float min, float max, float inc, Vector3f initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public Vector3fControl(ElementManager screen, float min, float max, float inc, Vector3f initial, boolean cycle, boolean all) {
		this(screen, UIDUtil.getUID(), min, max, inc, initial, cycle, all);
	}

	public Vector3fControl(ElementManager screen, String UID, float min, float max, float inc, Vector3f initial, boolean all) {
		this(screen, UID, min, max, inc, initial, false, all);
	}

	public Vector3fControl(ElementManager screen, String UID, float min, float max, float inc, Vector3f initial, boolean cycle,
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
						val.z = newValue;
						z.setSelectedValue(newValue);
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
					val.x = newValue;
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
					val.y = (Float) newValue;
					setA();
					onChangeVector(val);
				}
			}
		};
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.y));
		addChild(y);
		createLabel(screen, "Y");

		// z
		z = new Spinner<Float>(screen, getUID() + ":Z", Vector2f.ZERO, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.z = newValue;
					setA();
					onChangeVector(val);
				}
			}
		};
		z.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.z));
		addChild(z);
		createLabel(screen, "Z");

		//
		adjusting = false;
		setInterval(5f);
		setA();
	}

	public void setValue(Vector3f newValue) {
		if (!Objects.equals(this.val, newValue)) {
			this.val.set(newValue);
			this.x.setSelectedValue(this.val.x);
			this.y.setSelectedValue(this.val.y);
			this.z.setSelectedValue(this.val.z);
			setA();
		}
	}

	public void setValueWithCallback(Vector3f newValue) {
		if (!Objects.equals(this.val, newValue)) {
			setValue(newValue);
			onChangeVector(newValue);
		}
	}

	public void addToForm(Form form) {
		if (a != null) {
			form.addFormElement(a);
		}
		form.addFormElement(x);
		form.addFormElement(y);
		form.addFormElement(z);
	}

	@Override
	public void setFont(String fontPath) {
		super.setFont(fontPath);
		if (a != null) {
			a.setFont(fontPath);
		}
		x.setFont(fontPath);
		y.setFont(fontPath);
		z.setFont(fontPath);
	}

	@Override
	public void setFontSize(float fontSize) {
		super.setFontSize(fontSize);
		if (a != null) {
			a.setFontSize(fontSize);
		}
		x.setFontSize(fontSize);
		y.setFontSize(fontSize);
		z.setFontSize(fontSize);
	}

	@Override
	public void setFontColor(ColorRGBA fontColor) {
		super.setFontColor(fontColor);
		if (a != null) {
			a.setFontColor(fontColor);
		}
		x.setFontColor(fontColor);
		y.setFontColor(fontColor);
		z.setFontColor(fontColor);
	}

	public void setInterval(float callsPerSeconds) {
		if (a != null) {
			a.setInterval(callsPerSeconds);
		}
		x.setInterval(callsPerSeconds);
		y.setInterval(callsPerSeconds);
		z.setInterval(callsPerSeconds);
	}

	protected abstract void onChangeVector(Vector3f newValue);

	public Vector3f getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			BigDecimal avg = new BigDecimal(val.x);
			avg = avg.add(new BigDecimal(val.y));
			avg = avg.add(new BigDecimal(val.z));
			avg = avg.divide(new BigDecimal(3f), BigDecimal.ROUND_HALF_UP);
			if (precision != -1)
				avg = avg.setScale(precision, BigDecimal.ROUND_HALF_UP);
			a.setSelectedValue(avg.floatValue());
		}
	}

	private void createLabel(ElementManager screen, String text) {
		Label label = new Label(screen, getUID() + ":" + text + ":Label");
		label.setText(text);
		ElementStyle.small(screen, label);
		addChild(label, "wrap");
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		((FloatRangeSpinnerModel) x.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) y.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) z.getSpinnerModel()).setPrecision(precision);
		if (a != null)
			((FloatRangeSpinnerModel) a.getSpinnerModel()).setPrecision(precision);
	}

	public void snapToIncr(boolean withCallback) {
		Float cx = (Float) x.getSpinnerModel().getCurrentValue();
		Float cy = (Float) y.getSpinnerModel().getCurrentValue();
		Float cz = (Float) z.getSpinnerModel().getCurrentValue();
		((FloatRangeSpinnerModel) x.getSpinnerModel()).snapToIncr();
		((FloatRangeSpinnerModel) y.getSpinnerModel()).snapToIncr();
		((FloatRangeSpinnerModel) z.getSpinnerModel()).snapToIncr();
		if (a != null) {
			((FloatRangeSpinnerModel) a.getSpinnerModel()).snapToIncr();
		}

		Float nx = (Float) x.getSpinnerModel().getCurrentValue();
		Float ny = (Float) y.getSpinnerModel().getCurrentValue();
		Float nz = (Float) z.getSpinnerModel().getCurrentValue();

		if (withCallback) {
			if (!cx.equals(nx) || !cy.equals(ny) || !cz.equals(nz)) {
				x.setSelectedValue(nx);
				y.setSelectedValue(ny);
				z.setSelectedValue(nz);
				setA();
				onChangeVector(getValue());
			}
		}
	}
}
