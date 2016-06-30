package org.iceui.controls;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.windows.TabControl;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;

@Deprecated
public class XTabControl extends TabControl {

	public XTabControl(ElementManager screen) {
		super(screen);
	}

	public XTabControl(ElementManager screen, Vector2f position) {
		super(screen, position);
	}

	public XTabControl(ElementManager screen, Vector2f position, Vector2f dimensions) {
		super(screen, position, dimensions);
	}

	public XTabControl(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, position, dimensions, resizeBorders, defaultImg);
	}

	public XTabControl(ElementManager screen, String UID, Vector2f position) {
		super(screen, UID, position);
	}

	public XTabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		super(screen, UID, position, dimensions);
	}

	public XTabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, Orientation.HORIZONTAL);
	}

	public XTabControl(ElementManager screen, Orientation orientation) {
		super(screen, orientation);
	}

	public XTabControl(ElementManager screen, Vector2f position, Orientation orientation) {
		super(screen, position, orientation);
	}

	public XTabControl(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation) {
		super(screen, position, dimensions, resizeBorders, defaultImg, orientation);
	}

	public XTabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, orientation);
	}

//	@Override
//	protected TabPanel createTabPanel() {
//		TabPanel panel = new TabPanel(screen, getUID() + ":TabPanel" + tabButtonIndex, Vector2f.ZERO, LUtil.LAYOUT_SIZE);
//		return panel;
//	}
//
//	@Override
//	public int addTab(String title, ButtonAdapter tab, boolean isCustomButton) {
//		int tabIndex = super.addTab(title, tab, isCustomButton); 
//		for (Button b : tabs) {
//			b.setFont(screen.getStyle("Font").getString(screen.getStyle(getTabStyleName()).getString("font")));
//			b.setFontSize(screen.getStyle(getTabStyleName()).getFloat("fontSize"));
//		}
//		return tabIndex;
//	}

}
