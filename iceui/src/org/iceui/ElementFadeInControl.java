package org.iceui;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import icetone.core.Element;

public class ElementFadeInControl extends AbstractControl {

	@Override
	protected void controlUpdate(float tpf) {
		Element pic = (Element) getSpatial();
		float alpha = pic.getGlobalAlpha();
		if (alpha < 1) {
			alpha += tpf;
			pic.setGlobalAlpha(Math.min(1, alpha));
		}
		else
			complete();
	}

	public void complete() {
		Element pic = (Element) getSpatial();
		pic.setGlobalAlpha(1);
		pic.removeControl(this);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

}