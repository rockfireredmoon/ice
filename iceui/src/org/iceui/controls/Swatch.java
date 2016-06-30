package org.iceui.controls;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseButtonListener;

public class Swatch extends Element implements MouseButtonListener {

    public Swatch(ElementManager screen) {
        this(screen, UIDUtil.getUID());
    }

    public Swatch(ElementManager screen, Vector2f dimensions) {
        this(screen, UIDUtil.getUID(), dimensions);
    }

    public Swatch(ElementManager screen, String UID) {
        this(screen, UID, new Vector2f(20, 20));
    }

    public Swatch(ElementManager screen, String UID, Vector2f dimensions) {
        super(screen, UID, dimensions);
    }

    public void setColor(ColorRGBA color) {
        getElementMaterial().setColor("Color", color);
    }

    public void onMouseLeftPressed(MouseButtonEvent evt) {
    }

    public void onMouseRightPressed(MouseButtonEvent evt) {
    }

    public void onMouseRightReleased(MouseButtonEvent evt) {
    }

    public void onMouseLeftReleased(MouseButtonEvent evt) {
    }
}
