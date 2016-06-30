package org.iceui.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

public class UIButton extends ButtonAdapter {

	public UIButton() {
		this(Screen.get());
	}

	public UIButton(ElementManager screen) {
		this(screen, LUtil.LAYOUT_SIZE, screen.getStyle("UIButton").getString("defaultImg"));
	}

	public UIButton(ElementManager screen, Vector2f dimensions, String defaultImg) {
		this(screen, dimensions, screen.getStyle("UIButton").getVector4f("resizeBorders"), defaultImg);
	}

	public UIButton(ElementManager screen, Vector2f dimensions) {
		this(screen, dimensions, screen.getStyle("UIButton").getString("defaultImg"));
	}

	public UIButton(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg);
	}

	public UIButton(ElementManager screen, String UID) {
		this(screen, UID, LUtil.LAYOUT_SIZE);
	}

	public UIButton(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, dimensions, screen.getStyle("UIButton").getVector4f("resizeBorders"),
				screen.getStyle("UIButton").getString("defaultImg"));
	}

	public UIButton(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
		setStyles("UIButton");
	}
}
