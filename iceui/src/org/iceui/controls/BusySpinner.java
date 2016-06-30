package org.iceui.controls;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.ui.Picture;

import icetone.core.Element;
import icetone.core.ElementManager;

public class BusySpinner extends Element {

	private float speed;
	private final Spatial p;
	private AbstractControl spin;

	public BusySpinner(ElementManager screen) {
		this(screen, screen.getStyle("Common").getVector2f("busyIconSize"));
	}

	public float getSpeed() {
		return speed;
	}

	public BusySpinner setSpeed(final float speed) {
		if (this.speed == 0 && speed != 0) {
			p.addControl(spin = new AbstractControl() {
				private float rot;

				@Override
				protected void controlUpdate(float tpf) {
					rot -= tpf * speed;
					if (rot < 0) {
						rot = FastMath.TWO_PI - tpf;
					}
					spatial.rotate(0, 0, tpf * speed);
					spatial.setLocalTranslation(0, 0, 0);
					spatial.rotate(0, 0, tpf * speed);
					spatial.center();
					spatial.move(getDimensions().x / 2f, getDimensions().y / 2f, 0);
				}

				@Override
				protected void controlRender(RenderManager rm, ViewPort vp) {
				}

				@Override
				public Control cloneForSpatial(Spatial paramSpatial) {
					return null;
				}
			});
			this.speed = speed;
		} else if (this.speed != 0 && speed == 0) {
			p.removeControl(spin);
			this.speed = speed;
		}
		return this;
	}

	public BusySpinner(ElementManager screen, Vector2f size) {
		super(screen, size);
		Picture p = new Picture("busy");
		p.setImage(app.getAssetManager(), screen.getStyle("Common").getString("busyIconImg"), false);
		Material mat = p.getMaterial().clone();
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		p.setMaterial(mat);
		p.setWidth(size.x);
		p.setHeight(size.y);
		this.p = p;
		attachChild(p);
	}
}
