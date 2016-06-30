package org.iceui.controls;

import com.jme3.math.Vector2f;

import icetone.controls.extras.Indicator;
import icetone.core.ElementManager;

@Deprecated
public abstract class XIndicator extends Indicator {

	public XIndicator() {
		super();
	}
	
	public XIndicator(ElementManager screen, Orientation orientation) {
		super(screen, orientation);
	}

	public XIndicator(ElementManager screen, Vector2f dimensions, Orientation orientation) {
		super(screen, dimensions, orientation);
	}

	public XIndicator(ElementManager screen, String UID, Vector2f dimensions, Orientation orientation) {
		super(screen, UID, dimensions, orientation);
	}
}
