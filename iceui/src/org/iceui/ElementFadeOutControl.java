package org.iceui;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

import icetone.core.Element;

public class ElementFadeOutControl extends AbstractControl {

	@Override
	protected void controlUpdate(float tpf) {
		Element pic = (Element) getSpatial();
		float alpha = pic.getGlobalAlpha();
		if (alpha > 0) {
			alpha -= tpf;
			pic.setGlobalAlpha(Math.max(0, alpha));
		}
		if (alpha <= 0)
			complete();
	}

	public void complete() {
		Element pic = (Element) getSpatial();
		Node par = pic.getParent();
		pic.removeFromParent();
		if (par != null)
			par.updateGeometricState();
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

}