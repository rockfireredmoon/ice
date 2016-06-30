package org.iceui.controls;

import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.framework.animation.Interpolation;
import icetone.framework.animation.RotateByAction;
import icetone.framework.core.AnimElement;

public class NewBusySpinner extends Element {

	private float speed;
	private AnimElement stars;

	public NewBusySpinner(ElementManager screen) {
		this(screen, screen.getStyle("Common").getVector2f("busyIconSize"));
	}

	public float getSpeed() {
		return speed;
	}

	public NewBusySpinner setSpeed(float speed) {
		return this;
	}

	public NewBusySpinner(ElementManager screen, Vector2f size) {
		super(screen, size);

		// Create anim element
		stars = new AnimElement(screen.getApplication().getAssetManager()) {
			@Override
			public void animElementUpdate(float tpf) {
			}
		};
		// Set texture
		stars.setTexture(screen.getApplication().getAssetManager().loadTexture(screen.getStyle("Common").getString("busyIconImg")));
		stars.addTextureRegion("busy", 0, 0, 64, 64);
		stars.addQuad("busyQuad", "busy", new Vector2f(0, 0), new Vector2f(0, 0));
		stars.initialize();

		attachChild(stars);// or apply directly to the AnimElement
		RotateByAction rot = new RotateByAction();
		rot.setAmount(360);
		rot.setDuration(1);
		rot.setInterpolation(Interpolation.exp5Out);
		stars.addAction(rot);

	}
}
