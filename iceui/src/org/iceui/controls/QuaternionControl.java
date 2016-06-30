package org.iceui.controls;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.form.Form;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public abstract class QuaternionControl extends Element {

	public static final Vector2f SPINNER_SIZE = new Vector2f(90, 20);
	private final Quaternion val;
	private boolean adjusting;
	private final Spinner<Float> x;
	private final Spinner<Float> y;
	private final Spinner<Float> z;
	private final Spinner<Float> w;
	private Spinner<Float> a;

	public QuaternionControl(ElementManager screen, float min, float max, float inc, Quaternion initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public QuaternionControl(ElementManager screen, float min, float max, float inc, Quaternion initial, boolean cycle,
			boolean all) {
		this(screen, UIDUtil.getUID(), min, max, inc, initial, cycle, all);
	}

	public QuaternionControl(ElementManager screen, String UID, float min, float max, float inc, Quaternion initial, boolean all) {
		this(screen, UID, min, max, inc, initial, false, all);
	}

	public QuaternionControl(ElementManager screen, String UID, float min, float max, float inc, Quaternion initial, boolean cycle,
			boolean all) {
		super(screen, UID, Vector2f.ZERO, new Vector2f(Short.MAX_VALUE, 86), Vector4f.ZERO, null);
		val = initial.clone();
		setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0", "[fill, grow][]"));
		adjusting = true;

		// all
		if (all) {
			a = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
				@Override
				public void onChange(Float newValue) {
					if (!adjusting) {
						x.setSelectedValue(newValue);
						y.setSelectedValue(newValue);
						z.setSelectedValue(newValue);
						w.setSelectedValue(newValue);
						val.set(newValue, newValue, newValue, newValue);
						onChangeVector(val);
					}
				}
			};
			a.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, (val.getX() + val.getY() + val.getZ() + val.getW()) / 4f));
			addChild(a);
			createLabel(screen, "*");
		}

		// x
		x = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.set((Float) newValue, val.getY(), val.getZ(), val.getW());
					setA();
					onChangeVector(val);
				}
			}
		};
		x.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getX()));
		addChild(x);
		createLabel(screen, "X");

		// y
		y = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.set(val.getX(), (Float) newValue, val.getZ(), val.getW());
					setA();
					onChangeVector(val);
				}
			}
		};
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getY()));
		addChild(y);
		createLabel(screen, "Y");

		// z
		z = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.set(val.getX(), val.getY(), (Float) newValue, val.getW());
					setA();
					onChangeVector(val);
				}
			}
		};
		z.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getZ()));
		addChild(z);
		createLabel(screen, "Z");

		// w
		w = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.set(val.getX(), val.getY(), val.getZ(), (Float) newValue);
					setA();
					onChangeVector(val);
				}
			}
		};
		w.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getW()));
		addChild(w);
		createLabel(screen, "W");

		//
		adjusting = false;
		setInterval(5f);
	}

	public void setValue(Quaternion newValue) {
		this.val.set(newValue);
		this.x.setSelectedValue(this.val.getX());
		this.y.setSelectedValue(this.val.getY());
		this.z.setSelectedValue(this.val.getZ());
		this.w.setSelectedValue(this.val.getW());
		setA();
	}

	public void setValueWithCallback(Quaternion newValue) {
		setValue(newValue);
		onChangeVector(newValue);
	}

	public void addToForm(Form form) {
		if (a != null) {
			form.addFormElement(a);
		}
		form.addFormElement(x);
		form.addFormElement(y);
		form.addFormElement(z);
		form.addFormElement(w);
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
		w.setFont(fontPath);
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
		w.setFontSize(fontSize);
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
		w.setFontColor(fontColor);
	}

	public void setInterval(float callsPerSeconds) {
		if (a != null) {
			a.setInterval(callsPerSeconds);
		}
		x.setInterval(callsPerSeconds);
		y.setInterval(callsPerSeconds);
		z.setInterval(callsPerSeconds);
		w.setInterval(callsPerSeconds);
	}

	protected abstract void onChangeVector(Quaternion newValue);

	public Quaternion getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			float avg = (val.getX() + val.getY() + val.getZ() + val.getW()) / 4;
			a.setSelectedValue(avg);
		}
	}

	private void createLabel(ElementManager screen, String text) {
		Label label = new Label(screen, getUID() + ":" + text + ":Label", new Vector2f(12, 12));
		label.setText(text);
		// ElementStyle.small(screen, label);
		addChild(label, "wrap");
	}

	public void snapToIncr(boolean withCallback) {
		Float cx = (Float) x.getSpinnerModel().getCurrentValue();
		Float cy = (Float) y.getSpinnerModel().getCurrentValue();
		Float cz = (Float) z.getSpinnerModel().getCurrentValue();
		Float cw = (Float) z.getSpinnerModel().getCurrentValue();
		((FloatRangeSpinnerModel) x.getSpinnerModel()).snapToIncr();
		((FloatRangeSpinnerModel) y.getSpinnerModel()).snapToIncr();
		((FloatRangeSpinnerModel) z.getSpinnerModel()).snapToIncr();
		((FloatRangeSpinnerModel) w.getSpinnerModel()).snapToIncr();

		Float nx = (Float) x.getSpinnerModel().getCurrentValue();
		Float ny = (Float) y.getSpinnerModel().getCurrentValue();
		Float nz = (Float) z.getSpinnerModel().getCurrentValue();
		Float nw = (Float) w.getSpinnerModel().getCurrentValue();

		if (withCallback) {
			if (!cx.equals(nx) || !cy.equals(ny) || !cz.equals(nz) || !cw.equals(nw)) {
				x.setSelectedValue(nx);
				y.setSelectedValue(ny);
				z.setSelectedValue(nz);
				w.setSelectedValue(nw);
				setA();
				onChangeVector(getValue());
			}
		}
	}
}
