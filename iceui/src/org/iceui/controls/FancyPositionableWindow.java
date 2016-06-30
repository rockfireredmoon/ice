package org.iceui.controls;

import java.util.prefs.Preferences;

import org.iceui.HPosition;
import org.iceui.VPosition;

import com.jme3.math.Vector2f;

import icetone.core.ElementManager;

public class FancyPositionableWindow extends FancyWindow {

    public FancyPositionableWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
        super(screen, UID, position, dimensions, size, closeable);
    }

    public FancyPositionableWindow(ElementManager screen, String UID, int offset, VPosition vposition, HPosition hposition, Vector2f dimensions, Size size, boolean closeable) {
        super(screen, UID, UIUtil.getDefaultPosition(offset, hposition, vposition, screen, dimensions),
                dimensions, size, closeable);
    }

    public static Vector2f getWindowPosition(ElementManager screen, String id, Vector2f defaultWindowSize, Preferences pref) {
        return UIUtil.getWindowPosition(pref, screen, id, defaultWindowSize, 0, HPosition.CENTER, VPosition.MIDDLE);
    }
}
