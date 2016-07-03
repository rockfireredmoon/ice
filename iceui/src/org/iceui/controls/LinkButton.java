package org.iceui.controls;

import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;

public class LinkButton extends ButtonAdapter {

	public LinkButton(String text, ElementManager screen) {
		this(screen);
		setText(text);
	}

	public LinkButton(ElementManager screen) {
		super(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("LinkButton").getVector4f("resizeBorders"),
				screen.getStyle("LinkButton").getString("defaultImg"));
		setStyles("LinkButton");
	}
}
