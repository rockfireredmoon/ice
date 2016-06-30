package org.iceui.controls;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.iceui.HPosition;
import org.iceui.VPosition;

import com.jme3.math.Vector2f;

import icetone.core.ElementManager;
import icetone.core.layout.LUtil;

public class FancyPersistentWindow extends FancyPositionableWindow {

	private static final Logger LOG = Logger.getLogger(FancyPersistentWindow.class.getName());
	private SaveType saveType;
	private boolean loadedGeometry;
	protected Preferences pref;

	public FancyPersistentWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size,
			boolean closeable, SaveType saveType, Preferences pref) {
		super(screen, UID, position, dimensions, size, closeable);
		init(saveType, pref);
	}

	public FancyPersistentWindow(ElementManager screen, String UID, int offset, VPosition vposition, HPosition hposition,
			Vector2f dimensions, Size size, boolean closeable, SaveType saveType, Preferences pref) {
		super(screen, UID, offset, vposition, hposition, dimensions, size, closeable);
		init(saveType, pref);
	}

	private void init(SaveType saveType, Preferences pref) {
		this.saveType = saveType;
		this.pref = pref;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		if (saveType != null) {
			switch (saveType) {
			case POSITION:
				if (UIUtil.isWindowPositionSaved(pref, getUID())) {
					final Vector2f windowPosition = UIUtil.getWindowPosition(pref, screen, getUID(), getDimensions());
					LUtil.setPosition(this, windowPosition);
				}
				sizeToContent();
				setIsMovable(true);
				break;
			case POSITION_AND_SIZE:
				if (UIUtil.isWindowPositionSaved(pref, getUID())) {
					final Vector2f windowPosition = UIUtil.getWindowPosition(pref, screen, getUID(), getDimensions());
					LUtil.setPosition(this, windowPosition);
				}
				if (UIUtil.isWindowSizeSaved(pref, getUID())) {
					LUtil.setDimensions(this, UIUtil.getWindowSize(pref, screen, getUID(), getOrgDimensions()));
				} else
					sizeToContent();
				setIsMovable(true);
				setIsResizable(true);
				break;
			case SIZE:
				setIsResizable(true);
				if (UIUtil.isWindowSizeSaved(pref, getUID())) {
					LUtil.setDimensions(this, UIUtil.getWindowSize(pref, screen, getUID(), getOrgDimensions()));
				} else
					sizeToContent();
				break;
			}
			loadedGeometry = true;
		}
	}

	@Override
	protected final void onControlResizeHook() {
		super.onControlResizeHook();
		saveMetrics();
		onPersistentWindowResizeHook();
	}

	@Override
	public final void controlMoveHook() {
		super.controlMoveHook();
		saveMetrics();
		onControlMoveHook();
	}

	protected void onControlMoveHook() {
	}

	protected void onPersistentWindowResizeHook() {
	}

	private void saveMetrics() {
		if (loadedGeometry) {
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
