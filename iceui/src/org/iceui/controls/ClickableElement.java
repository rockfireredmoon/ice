package org.iceui.controls;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.listeners.MouseButtonListener;

public abstract class ClickableElement extends Element implements MouseButtonListener {

    public ClickableElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
        super(screen, UID, position, dimensions, resizeBorders, texturePath);
    }

    public void onMouseLeftPressed(MouseButtonEvent evt) {
    }

    public void onMouseRightPressed(MouseButtonEvent evt) {
    }

    public void onMouseRightReleased(MouseButtonEvent evt) {
    }
    
}
