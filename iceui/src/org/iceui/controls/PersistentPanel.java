package org.iceui.controls;

import java.util.prefs.Preferences;

import org.iceui.HPosition;
import org.iceui.VPosition;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.windows.Panel;
import icetone.core.ElementManager;

public class PersistentPanel extends Panel {

	private SaveType saveType;
	private Preferences pref;

	public PersistentPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, SaveType saveType,
			Preferences pref) {
		super(screen, UID,
				org.iceui.controls.UIUtil.getWindowPosition(pref, screen, UID, UIUtil.getWindowSize(pref, screen, UID, dimensions)),
				org.iceui.controls.UIUtil.getWindowSize(pref, screen, UID, dimensions));
		init(saveType, pref);
	}

	public PersistentPanel(ElementManager screen, String UID, int offset, VPosition vposition, HPosition hposition,
			Vector2f dimensions, SaveType saveType, Preferences pref) {
		super(screen,
				UID, org.iceui.controls.UIUtil.getWindowPosition(pref, screen, UID,
						UIUtil.getWindowSize(pref, screen, UID, dimensions), offset, hposition, vposition),
				org.iceui.controls.UIUtil.getWindowSize(pref, screen, UID, dimensions));
		init(saveType, pref);
	}

	public PersistentPanel(ElementManager screen, String UID, int offset, VPosition vposition, HPosition hposition,
			Vector2f dimensions, Vector4f resizeBorders, String defaultImg, SaveType saveType, Preferences pref) {
		super(screen, UID,
				org.iceui.controls.UIUtil.getWindowPosition(pref, screen, UID, UIUtil.getWindowSize(pref, screen, UID, dimensions),
						offset, hposition, vposition),
				org.iceui.controls.UIUtil.getWindowSize(pref, screen, UID, dimensions), resizeBorders, defaultImg);
		init(saveType, pref);
	}

	private void init(SaveType saveType, Preferences pref) {
		this.saveType = saveType;
		this.pref = pref;
		if (saveType != null) {
			switch (saveType) {
			case POSITION:
				setPosition(UIUtil.getWindowPosition(pref, screen, getUID(), getDimensions()));
				break;
			case POSITION_AND_SIZE:
				setPosition(UIUtil.getWindowPosition(pref, screen, getUID(), getDimensions()));
				setDimensions(UIUtil.getWindowSize(pref, screen, getUID(), getOrgDimensions()));
				break;
			case SIZE:
				setDimensions(UIUtil.getWindowSize(pref, screen, getUID(), getOrgDimensions()));
				break;
			}
		}
	}

	@Override
	public final void controlResizeHook() {
		saveMetrics();
		onPersistentWindowReiszeHook();
	}

	@Override
	public final void controlMoveHook() {
		super.controlMoveHook();
		saveMetrics();
		onControlMoveHook();
	}

	protected void onControlMoveHook() {
	}

	protected void onPersistentWindowReiszeHook() {
	}

	private void saveMetrics() {
		if (saveType != null) {
			switch (saveType) {
			case POSITION:
				UIUtil.saveWindowPosition(pref, this, getUID());
				break;
			case POSITION_AND_SIZE:
				UIUtil.saveWindowPositionAndSize(pref, this, getUID());
				break;
			case SIZE:
				UIUtil.saveWindowSize(pref, this, getUID());
				break;
			}
		}
	}
}
