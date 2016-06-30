package org.icenet.client;

import org.icelib.Armed;
import org.icelib.Point3D;

public class SpawnListenerAdapter implements SpawnListener {

    @Override
    public void statsChanged(Spawn spawn) {
    }

    @Override
    public void equipmentChanged(Spawn spawn) {
    }

    @Override
    public void appearanceChange(Spawn spawn) {
    }

    @Override
    public void serverLocationChanged(Spawn spawn, Point3D oldLocation, boolean warpTo) {
    }

    @Override
    public void destroyed(Spawn spawn) {
    }

    @Override
    public void recalcElevation(Spawn spawn) {
    }

    @Override
    public void jump(Spawn spawn) {
    }

    @Override
    public void armedChanged(Spawn spawn, Armed armed) {
    }

	@Override
	public void moved(Spawn spawn, Point3D oldLocation, int oldRotation, int oldHeading, int oldSpeed) {
	}
}
