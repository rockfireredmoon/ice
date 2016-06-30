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
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public abstract class Vector4fControl extends Element {

	public enum Type {
		VECTOR, RGBA
	}

	private final Vector4f val;
	private boolean adjusting;
	private final Spinner<Float> x;
	private final Spinner<Float> y;
	private final Spinner<Float> z;
	private final Spinner<Float> w;
	private Spinner<Float> a;
	private Label wLabel;
	private Label zLabel;
	private Label yLabel;
	private Label xLabel;

	public Vector4fControl(float min, float max, float inc, Vector4f initial, boolean all) {
		this(Screen.get(), min, max, inc, initial, false, all);
	}

	public Vector4fControl(ElementManager screen, float min, float max, float inc, Vector4f initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public Vector4fControl(ElementManager screen, float min, float max, float inc, Vector4f initial, boolean cycle, boolean all) {
		this(screen, UIDUtil.getUID(), min, max, inc, initial, cycle, all);
	}

	public Vector4fControl(ElementManager screen, String UID, float min, float max, float inc, Vector4f initial, boolean all) {
		this(screen, UID, min, max, inc, initial, false, all);
	}

	public Vector4fControl(ElementManager screen, String UID, float min, float max, float inc, Vector4f initial, boolean cycle,
			boolean all) {
		super(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		val = initial.clone();
		setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0", "[fill, grow][]"));
		adjusting = true;

		// all
		if (all) {
			a = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
				@Override
				public void onChange(Float newValue) {
					if (!adjusting) {
						val.x = newValue;
						x.setSelectedValue(newValue);
						val.y = newValue;
						y.setSelectedValue(newValue);
						val.z = newValue;
						z.setSelectedValue(newValue);
						val.w = newValue;
						w.setSelectedValue(newValue);
						onChangeVector(val);
					}
				}
			};
			a.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, (val.x + val.y + val.z + val.w) / 4f));
			addChild(a);
			createLabel(screen, "*");
		}

		// x
		x = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
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
		xLabel = createLabel(screen, "X");

		// y
		y = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
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
		yLabel = createLabel(screen, "Y");

		// z
		z = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
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
		zLabel = createLabel(screen, "Z");

		// w
		w = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle) {
			@Override
			public void onChange(Float newValue) {
				if (!adjusting) {
					val.w = newValue;
					setA();
					onChangeVector(val);
				}
			}
		};
		w.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.w));
		addChild(w);
		wLabel = createLabel(screen, "W");

		//
		adjusting = false;
		setInterval(5f);
	}

	public void setValue(Vector4f newValue) {
		this.val.set(newValue);
		this.x.setSelectedValue(this.val.x);
		this.y.setSelectedValue(this.val.y);
		this.z.setSelectedValue(this.val.z);
		this.w.setSelectedValue(this.val.w);
		setA();
	}

	public void setValueWithCallback(Vector4f newValue) {
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

	public void setType(Type type) {
		switch (type) {
		case RGBA:
			xLabel.setText("R");
			yLabel.setText("G");
			zLabel.setText("B");
			wLabel.setText("A");
			break;
		case VECTOR:
			xLabel.setText("X");
			yLabel.setText("Y");
			zLabel.setText("Z");
			wLabel.setText("W");
			break;
		}
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

	protected abstract void onChangeVector(Vector4f newValue);

	public Vector4f getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			float avg = (val.x + val.y + val.z + val.w) / 4;
			a.setSelectedValue(avg);
		}
	}

	private Label createLabel(ElementManager screen, String text) {
		Label label = new Label(screen, getUID() + ":" + text + ":Label", new Vector2f(12, 12));
		label.setText(text);
		// ElementStyle.small(screen, label);
		addChild(label, "wrap");
		return label;
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
