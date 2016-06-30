package org.iceui.controls;

import java.util.prefs.Preferences;

import org.icelib.AbstractConfig;
import org.iceui.HPosition;
import org.iceui.VPosition;

import com.jme3.math.Vector2f;

import icetone.controls.windows.Window;
import icetone.core.ElementManager;

public class PersistentWindow extends Window {
	private final Preferences pref;

	public PersistentWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Preferences pref) {
		super(screen, UID, UIUtil.getWindowPosition(pref, screen, UID, UIUtil.getWindowSize(pref, screen, UID, dimensions)),
				org.iceui.controls.UIUtil.getWindowSize(pref, screen, UID, dimensions));
		this.pref = pref;
	}

	public PersistentWindow(ElementManager screen, String UID, int offset, VPosition vposition, HPosition hposition,
			Vector2f dimensions, Preferences pref) {
		super(screen, UID, UIUtil.getWindowPosition(pref, screen, UID, UIUtil.getWindowSize(pref, screen, UID, dimensions), offset,
				hposition, vposition), UIUtil.getWindowSize(pref, screen, UID, dimensions));
		this.pref = pref;
	}

	protected void saveWindowPosition() {
		pref.putInt(getUID() + AbstractConfig.WINDOW_X, (int) getX());
		pref.putInt(getUID() + AbstractConfig.WINDOW_Y, (int) getY());
	}
}
