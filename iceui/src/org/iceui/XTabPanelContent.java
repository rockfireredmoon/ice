package org.iceui;

import org.iceui.controls.TabPanelContent;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.LUtil;

@Deprecated
public class XTabPanelContent extends TabPanelContent {

	private Element bgElement;

	public static XTabPanelContent create(ElementManager screen, Element content) {
		XTabPanelContent c = new XTabPanelContent(screen);
		c.setLayoutManager(new BorderLayout());
		c.addChild(content, BorderLayout.Border.CENTER);
		return c;
	}

	public XTabPanelContent(ElementManager screen) {
		super(screen, LUtil.LAYOUT_SIZE);
		init();
	}

	public XTabPanelContent(ElementManager screen, Vector2f dimensions) {
		super(screen, dimensions);
		init();
	}

	public XTabPanelContent(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, dimensions, resizeBorders, defaultImg);
		init();
	}

	public XTabPanelContent(ElementManager screen, String UID) {
		super(screen, UID, LUtil.LAYOUT_SIZE);
		init();
	}

	public XTabPanelContent(ElementManager screen, String UID, Vector2f dimensions) {
		super(screen, UID, dimensions);
		init();
	}

	public XTabPanelContent(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, dimensions, resizeBorders, defaultImg);
		init();
	}

//	@Override
//	public Collection<Element> getElements() {
//		if (bgElement == null) {
//			return super.getElements();
//		}
//		final ArrayList<Element> arrayList = new ArrayList<Element>(super.getElements());
//		arrayList.remove(bgElement);
//		return arrayList;
//	}
//
//	@Override
//	public void removeAllChildren() {
//		super.removeAllChildren();
//		if (bgElement != null) {
//			// Must recreate, not just re-add or z-order will be wrong
//			init();
//			sizeBg();
//		}
//	}
//
//	@Override
//	public void controlResizeHook() {
//		super.controlResizeHook();
//		if (bgElement != null) {
//			sizeBg();
//		}
//	}
//
	private void init() {
//		String tileImage = screen.getStyle("TabContent").getString("tileImg");
//		if (tileImage != null) {
//			bgElement = new Element(screen, getUID() + ":bg", tileImage);
//			bgElement.setTileImage(true);
//			addChild(bgElement);
//			sizeBg();
//		}
//		LUtil.noScaleNoDock(this);
	}
//
//	private void sizeBg() {
//		bgElement.setDimensions(getWidth() - borders.z - borders.y, getHeight() - borders.x - borders.w);
//		bgElement.setPosition(borders.y, borders.x);
//	}
}
