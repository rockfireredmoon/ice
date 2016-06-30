package org.iceui.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.ElementManager;

public class CancelButton extends FancyButton {

    public CancelButton(ElementManager screen) {
        super(screen);
        init();
    }

    public CancelButton(ElementManager screen, Vector2f dimensions, String defaultImg) {
        super(screen, dimensions, defaultImg);
        init();
    }

    public CancelButton(ElementManager screen, Vector2f dimensions) {
        super(screen, dimensions);
        init();
    }

    public CancelButton(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, dimensions, resizeBorders, defaultImg);
        init();
    }

    public CancelButton(ElementManager screen, String UID) {
        super(screen, UID);
        init();
    }

    public CancelButton(ElementManager screen, String UID, Vector2f dimensions) {
        super(screen, UID, dimensions);
        init();
    }

    public CancelButton(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, UID, dimensions, resizeBorders, defaultImg);
        init();
    }


    private void init() {
        setStyles("CancelButton");
    }
    
}
