package org.iceui.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

public class FancyButton extends ButtonAdapter {

    public FancyButton(String text, ElementManager screen) {
        this(screen, LUtil.LAYOUT_SIZE, screen.getStyle("FancyButton").getString("defaultImg"));
        setText(text);
    }

    public FancyButton(ElementManager screen) {
        this(screen, LUtil.LAYOUT_SIZE, screen.getStyle("FancyButton").getString("defaultImg"));
    }

    public FancyButton(ElementManager screen, Vector2f dimensions, String defaultImg) {
        this(screen, dimensions, screen.getStyle("FancyButton").getVector4f("resizeBorders"), defaultImg);
    }

    public FancyButton(ElementManager screen, Vector2f dimensions) {
        this(screen, dimensions, screen.getStyle("FancyButton").getString("defaultImg"));
    }

    public FancyButton(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg);
    }

    public FancyButton(ElementManager screen, String UID) {
        this(screen, UID, LUtil.LAYOUT_SIZE);
    }

    public FancyButton(ElementManager screen, String UID, Vector2f dimensions) {
        this(screen, UID, dimensions, screen.getStyle("FancyButton").getVector4f("resizeBorders"), screen.getStyle("FancyButton").getString("defaultImg"));
    }

    public FancyButton(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
        setStyles("FancyButton");
    }

}
