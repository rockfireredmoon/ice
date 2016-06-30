package org.iceui.controls;

import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.ElementManager;
import icetone.core.utils.UIDUtil;

public class LayoutAwareBigButton extends ButtonAdapter {

    public LayoutAwareBigButton(ElementManager screen) {
        this(screen, UIDUtil.getUID());
    }

    public LayoutAwareBigButton(ElementManager screen, String UID) {
        this(screen, UID,
                screen.getStyle("BigButton").getVector2f("defaultSize"));
    }

    public LayoutAwareBigButton(ElementManager screen, Vector2f dimensions) {
        this(screen, UIDUtil.getUID(), dimensions);
    }

    public LayoutAwareBigButton(ElementManager screen, String UID, Vector2f dimensions) {
        super(screen, UID,
        		Vector2f.ZERO,
                dimensions,
                screen.getStyle("BigButton").getVector4f("resizeBorders"),
                screen.getStyle("BigButton").getString("defaultImg"));
        setStyles("BigButton");
    }
}
