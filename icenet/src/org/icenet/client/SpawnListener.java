package org.icenet.client;

import org.icelib.Armed;
import org.icelib.Point3D;

public interface SpawnListener {
    
    void jump(Spawn spawn);

    void statsChanged(Spawn spawn);

    void equipmentChanged(Spawn spawn);

    void armedChanged(Spawn spawn, Armed armed);

    void appearanceChange(Spawn spawn);

    void serverLocationChanged(Spawn spawn, Point3D oldLocation, boolean warpTo);

    void moved(Spawn spawn, Point3D oldLocation, int oldRotation, int oldHeading, int oldSpeed);

    void destroyed(Spawn spawn);
    
    void recalcElevation(Spawn spawn);
    
}
