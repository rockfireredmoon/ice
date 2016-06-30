package org.iceui.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 * Use as the container for tab content when borders are wanted.
 */
public class TabPanelContent extends Element {

	public TabPanelContent(ElementManager screen) {
		this(screen, LUtil.LAYOUT_SIZE);
	}

	public TabPanelContent(ElementManager screen, Vector2f dimensions) {
		this(screen, dimensions, screen.getStyle("TabContent").getVector4f("resizeBorders"),
				screen.getStyle("TabContent").getString("defaultImg"));
	}

	public TabPanelContent(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID() + ":TabPanelContent", dimensions, resizeBorders, defaultImg);
	}

	public TabPanelContent(ElementManager screen, String UID) {
		this(screen, UID, LUtil.LAYOUT_SIZE);
	}

	public TabPanelContent(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, dimensions, screen.getStyle("TabContent").getVector4f("resizeBorders"),
				screen.getStyle("TabContent").getString("defaultImg"));
	}

	public TabPanelContent(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
		getElementTexture().setWrap(Texture.WrapMode.Repeat);
		setTileImage(true);
		setTextPadding(screen.getStyle("TabContent").getVector4f("textPadding"));
		setIgnoreMouse(true);
	}
}
