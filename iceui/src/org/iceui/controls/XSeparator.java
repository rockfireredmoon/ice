package org.iceui.controls;

import org.icelib.Icelib;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

public class XSeparator extends Element {

	public enum Style {

		FANCY, SIMPLE
	}

	public XSeparator(ElementManager screen, Orientation orientation) {
		this(screen, Style.FANCY, orientation);
	}

	public XSeparator(ElementManager screen, Style style, Element.Orientation orientation) {
		this(screen, orientation.equals(Orientation.HORIZONTAL) ? "Separator" + Icelib.toEnglish(style) + "Horizontal"
				: "Separator" + Icelib.toEnglish(style) + "Vertical");
	}

	private XSeparator(ElementManager screen, String styleName) {
		super(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, screen.getStyle(styleName).getVector4f("resizeBorders"),
				screen.getStyle(styleName).getString("defaultImg"));
	}
}
