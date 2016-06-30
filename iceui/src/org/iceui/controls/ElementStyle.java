package org.iceui.controls;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;

public class ElementStyle {

    public static Element styleElement(ElementManager screen, String styleName, Element element) {
        element.setFontSize(screen.getStyle(styleName).getFloat("fontSize"));
        element.setFontColor(screen.getStyle(styleName).getColorRGBA("fontColor"));
        element.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(styleName).getString("textVAlign")));
        element.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(styleName).getString("textAlign")));
        element.setTextWrap(LineWrapMode.valueOf(screen.getStyle(styleName).getString("textWrap")));
        return element;
    }

    public static Element altColor(ElementManager screen, Element label) {
        label.setFontColor(screen.getStyle("Common").getColorRGBA("altColor"));
        return label;
    }

    public static Element successColor(ElementManager screen, Element label) {
        label.setFontColor(screen.getStyle("Common").getColorRGBA("positiveColor"));
        return label;
    }

    public static Element errorColor(ElementManager screen, Element label) {
        label.setFontColor(screen.getStyle("Common").getColorRGBA("errorColor"));
        return label;
    }

    public static Element normalColor(ElementManager screen, Element label) {
        label.setFontColor(screen.getStyle("Common").getColorRGBA("fontColor"));
        return label;
    }

    public static Element tiny(ElementManager screen, Element label) {
        label.setFont(screen.getStyle("Font").getString("tinyFont"));
        label.setFontSize(screen.getStyle("Common").getFloat("tinyFontSize"));
        return label;
    }

    public static Element mediumOutline(ElementManager screen, Element label) {
        label.setFont(screen.getStyle("Font").getString("mediumOutlineFont"));
        label.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
        return label;
    }

    public static Element medium(ElementManager screen, Element label, boolean bold, boolean italic) {
        if (bold) {
            if (italic) {
                label.setFont(screen.getStyle("Font").getString("mediumStrongItalicFont"));
            } else {
                label.setFont(screen.getStyle("Font").getString("mediumStrongFont"));
            }
        } else {
            if (italic) {
                label.setFont(screen.getStyle("Font").getString("mediumItalicFont"));
            } else {
                label.setFont(screen.getStyle("Font").getString("mediumFont"));
            }
        }
        label.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
        return label;
    }
    
    public static Element medium(Element label) {
    	return medium(label.getScreen(), label);
    }

    public static Element medium(ElementManager screen, Element label) {
        medium(screen, label, false, false);
        return label;
    }

    public static Element normal(ElementManager screen, Element label, boolean bold, boolean italic) {
        return normal(screen, label, bold, italic, false);
    }

    public static Element normal(ElementManager screen, Element label, boolean bold, boolean italic, boolean outline) {
        if (outline) {
            label.setFont(screen.getStyle("Font").getString("defaultOutlineFont"));
        } else {
            if (bold) {
                if (italic) {
                    label.setFont(screen.getStyle("Font").getString("strongItalicFont"));
                } else {
                    label.setFont(screen.getStyle("Font").getString("strongFont"));
                }
            } else {
                if (italic) {
                    label.setFont(screen.getStyle("Font").getString("italicFont"));
                } else {
                    label.setFont(screen.getStyle("Font").getString("defaultFont"));
                }
            }
        }

        label.setFontSize(screen.getStyle("Common").getFloat("fontSize"));
        return label;
    }

    public static Element large(ElementManager screen, Element label) {
        label.setFont(screen.getStyle("Font").getString("largeFont"));
        label.setFontSize(screen.getStyle("Common").getFloat("largeFontSize"));
        return label;
    }

    public static Element small(ElementManager screen, Element label, boolean bold, boolean italic) {
        label.setFont(screen.getStyle("Font").getString("smallFont"));
        label.setFontSize(screen.getStyle("Common").getFloat("smallFontSize"));
        return label;
    }

    public static Element small(Element label) {
    	return small(label.getScreen(), label);
    }

    public static Element small(ElementManager screen, Element label) {
        small(screen, label, false, false);
        return label;
    }

    public static Button arrowButton(Screen screen, Button button, String direction) {
        Vector2f sz = screen.getStyle("Common").getVector2f("arrowSize");
        if(button.getButtonIcon() != null) {
            button.removeChild(button.getButtonIcon());
        }
        LUtil.setInaccessibleField(null, "icon", button, Button.class);
        button.setButtonIcon(sz.x, sz.y, screen.getStyle("Common").getString("arrow" + direction));
        return button;
    }
}
