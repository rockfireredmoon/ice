package org.iceui.controls;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;

import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.layout.mig.MigLayout;

public abstract class QuaternionControl extends Element {

	public static final Vector2f SPINNER_SIZE = new Vector2f(90, 20);
	private final Quaternion val;
	private boolean adjusting;
	private Spinner<Float> x;
	private Spinner<Float> y;
	private Spinner<Float> z;
	private Spinner<Float> w;
	private Spinner<Float> a;

	public QuaternionControl(BaseScreen screen, float min, float max, float inc, Quaternion initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public QuaternionControl(BaseScreen screen, float min, float max, float inc, Quaternion initial, boolean cycle,
			boolean all) {
		this(screen, null, min, max, inc, initial, cycle, all);
	}

	public QuaternionControl(BaseScreen screen, String UID, float min, float max, float inc, Quaternion initial,
			boolean all) {
		this(screen, UID, min, max, inc, initial, false, all);
	}

	public QuaternionControl(BaseScreen screen, String UID, float min, float max, float inc, Quaternion initial,
			boolean cycle, boolean all) {
		super(screen, UID);
		val = initial.clone();
		setLayoutManager(new MigLayout(screen, "fill, gap 0, ins 0", "[fill, grow][]"));
		adjusting = true;

		// all
		if (all) {
			a = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
			a.onChange(evt -> {
				if (!adjusting) {
					x.setSelectedValue(evt.getNewValue());
					y.setSelectedValue(evt.getNewValue());
					z.setSelectedValue(evt.getNewValue());
					w.setSelectedValue(evt.getNewValue());
					val.set(evt.getNewValue(), evt.getNewValue(), evt.getNewValue(), evt.getNewValue());
					onChangeVector(val);
				}
			});
			a.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc,
					(val.getX() + val.getY() + val.getZ() + val.getW()) / 4f));
			addElement(a);
			createLabel(screen, "*");
		}

		// x
		x = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		x.onChange(evt -> {
			if (!adjusting) {
				val.set(evt.getNewValue(), val.getY(), val.getZ(), val.getW());
				setA();
				onChangeVector(val);
			}
		});
		x.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getX()));
		addElement(x);
		createLabel(screen, "X");

		// y
		y = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		y.onChange(evt -> {
			if (!adjusting) {
				val.set(val.getX(), evt.getNewValue(), val.getZ(), val.getW());
				setA();
				onChangeVector(val);
			}
		});
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getY()));
		addElement(y);
		createLabel(screen, "Y");

		// z
		z = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		z.onChange(evt -> {
			if (!adjusting) {
				val.set(val.getX(), val.getY(), evt.getNewValue(), val.getW());
				setA();
				onChangeVector(val);
			}
		});
		z.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getZ()));
		addElement(z);
		createLabel(screen, "Z");

		// w
		w = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		w.onChange(evt -> {
			if (!adjusting) {
				val.set(val.getX(), val.getY(), val.getZ(), evt.getNewValue());
				setA();
				onChangeVector(val);
			}
		});
		w.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.getW()));
		addElement(w);
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

	private void createLabel(BaseScreen screen, String text) {
		Label label = new Label(screen, text);
		label.setText(text);
		addElement(label, "wrap");
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
