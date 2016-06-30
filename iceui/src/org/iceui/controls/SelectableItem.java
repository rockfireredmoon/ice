package org.iceui.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

public class SelectableItem extends Element {

	private boolean selected;

	public SelectableItem() {
		this(Screen.get());
	}

	public SelectableItem(ElementManager screen) {
		this(screen, LUtil.LAYOUT_SIZE);
	}

	public SelectableItem(ElementManager screen, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), dimensions);
	}

	public SelectableItem(ElementManager screen, String UID) {
		this(screen, UID, LUtil.LAYOUT_SIZE);
	}

	public SelectableItem(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, dimensions, screen.getStyle("SelectArea#SelectableItem").getVector4f("resizeBorders"));
	}

	public SelectableItem(ElementManager screen, String UID, Vector4f resizeBorders) {
		this(screen, UID, LUtil.LAYOUT_SIZE, screen.getStyle("SelectArea#SelectableItem").getVector4f("resizeBorders"));
	}

	public SelectableItem(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, screen.getStyle("SelectArea#SelectableItem").getString("defaultImg"));
		setIgnoreMouse(true);
		setTextPadding(screen.getStyle("SelectArea#SelectableItem").getVector4f("textPadding"));
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			getElementTexture().setImage(app.getAssetManager()
					.loadTexture(screen.getStyle("SelectArea#SelectableItem").getString("selectedImg")).getImage());
		} else {
			getElementTexture().setImage(app.getAssetManager()
					.loadTexture(screen.getStyle("SelectArea#SelectableItem").getString("defaultImg")).getImage());
		}
	}

	public boolean isSelected() {
		return selected;
	}
}
