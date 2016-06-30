package org.iceui;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * Extension of {@link ChaseCamera} that adds the following features :-
 * <p>
 * <ul>
 * <li>Input may be detached. This means we can flick between the Flycam and this chase
 * camera when switching to and from build mode.</li>
 * <li>"Push Zoom" allowing zooming without mouse whell. See {@link #setPushZoom(boolean)
 * }.</li>
 * <li>Height adjustment of the point the camera is looking at based on the normalized
 * distance between the camera and target. For example, as you zoom towards the player,
 * the height can be made to decrease, increasing the zoom decrease up to the maximum at
 * which point the height is at it's base value.</li>
 * </ul>
 */
public class DetachableChaseCam extends ChaseCamera {

    private boolean pushZoom = true;
    private float pushAmount;
    private float heightAdjust = 1;
    private float lookAtHeightFactor;
    private float pushZoomSpeed = 100f;

    public DetachableChaseCam(Camera cam, Spatial target) {
        super(cam, target);
    }

    public DetachableChaseCam(Camera cam) {
        super(cam);
    }

    public DetachableChaseCam(Camera cam, InputManager inputManager) {
        super(cam, inputManager);
    }

    public DetachableChaseCam(Camera cam, Spatial target, InputManager inputManager) {
        super(cam, target, inputManager);
    }

    /**
     * Get whether pushzoom is enabled.
     *
     * @return
     * @see #setPushZoom(boolean)
     */
    public boolean isPushZoom() {
        return pushZoom;
    }

    /**
     * Set how much to adjust the height by based on the distance between the camera and
     * the target. For example, if the
     * <code>y<code> value of the {@link #lookAtOffset} is 20, and camera is fully zoomed
     * out, then the camera will actually be looking at a
     * <code>y</code> of 20. If {@link #heightAdjust} is set to 0.5, then as the camera is
     * zoomed towards the target, the height will be adjusted until it reaches 10.
     *
     * @param heightAdjust height adjustment
     */
    public void setHeightAdjust(float heightAdjust) {
        this.heightAdjust = heightAdjust;
    }

    /**
     * Set whether push zoom is enabled. In effect this allows zooming without using the
     * mouse wheel, instead relying on vertical mouse motion that would otherwise do
     * nothing.
     * <p>
     * When this and {@link #veryCloseRotation} is enabled, attemping to vertically rotate
     * at the minimum rotation BEFORE the distance is close enough to allow downward
     * rotation will cause the camera to be temporarily zoomed towards the target. Once
     * the distance reaches the point at which downward rotation is allowed, zooming will
     * stop and the player will be able to look down (or up if inverted). Rotating
     * vertically in the opposite direction will then cause the player to start looking up
     * (or down) until vertical rotation reaches the point at which the temporary zoom
     * starts decreasing until it reaches it's original position.
     *
     * @param pushZoom enable pushzoom.
     */
    public void setPushZoom(boolean pushZoom) {
        this.pushZoom = pushZoom;
        if (!pushZoom) {
            pushAmount = 0;
        }
    }

    public float getPushZoomSpeed() {
        return pushZoomSpeed;
    }

    public void setPushZoomSpeed(float pushZoomSpeed) {
        this.pushZoomSpeed = pushZoomSpeed;
    }

    @Override
    public void setMaxDistance(float maxDistance) {
        super.setMaxDistance(maxDistance);
        updateHeightAdjustment();
    }

    @Override
    public void setMinDistance(float minDistance) {
        super.setMinDistance(minDistance);
        updateHeightAdjustment();
    }

    protected void followView(float rotation) {
    }


    /**
     * Detach all keyboard input.
     */
    public void detachInput() {
        inputManager.deleteMapping(ChaseCamDown);
        String[] inputs = {ChaseCamToggleRotate,
            ChaseCamDown,
            ChaseCamUp,
            ChaseCamMoveLeft,
            ChaseCamMoveRight,
            ChaseCamToggleRotate,
            ChaseCamZoomIn,
            ChaseCamZoomOut};
        for (String i : inputs) {
            if (inputManager.hasMapping(i)) {
                inputManager.deleteMapping(i);
            }
        }
        inputManager.removeListener(this);
    }

    @Override
    protected void updateCamera(float tpf) {
        if (enabled) {
            float oOffset = lookAtOffset.y;
            lookAtOffset.y = lookAtOffset.y + (heightAdjust * lookAtHeightFactor);
            super.updateCamera(tpf);
            lookAtOffset.y = oOffset;
        }
    }

    //rotate the camera around the target on the vertical plane
    @Override
    protected void vRotateCamera(float value) {
        if (!canRotate || !enabled) {
            return;
        }
        vRotating = true;
        float lastGoodRot = targetVRotation;
        targetVRotation += value * rotationSpeed;
        if (targetVRotation > maxVerticalRotation) {
            targetVRotation = lastGoodRot;
        }
        if (veryCloseRotation) {
            if ((targetVRotation < minVerticalRotation) && (targetDistance - pushAmount > (minDistance + 1.0f))) {
                targetVRotation = minVerticalRotation;
                if (pushZoom) {
                    pushAmount += value * -pushZoomSpeed * (1 + (lookAtHeightFactor * 3));
                    updateHeightAdjustment();
                }
            } else if (targetVRotation < -FastMath.DEG_TO_RAD * 90) {
                targetVRotation = lastGoodRot;
            } else {
                if (pushAmount > 0 && targetVRotation > lastGoodRot && targetVRotation > 0) {
                    pushAmount -= value * pushZoomSpeed * 5;
                    updateHeightAdjustment();
                }
            }
        } else {
            if ((targetVRotation < minVerticalRotation)) {
                targetVRotation = lastGoodRot;
            } else {
                if (pushAmount > 0) {
                    pushAmount -= 1;
                }
            }
        }
    }

    @Override
    protected void computePosition() {
        if (pushZoom) {
            float actualDistance = getActualDistance();
            float hDistance = (actualDistance) * FastMath.sin((FastMath.PI / 2) - vRotation);
            pos.set(hDistance * FastMath.cos(rotation), (actualDistance) * FastMath.sin(vRotation), hDistance * FastMath.sin(rotation));
            pos.addLocal(target.getWorldTranslation());
        } else {
            super.computePosition();
        }
    }

    @Override
    protected void zoomCamera(float value) {
        if (pushAmount > 0) {
            targetDistance -= pushAmount;
            pushAmount = 0;
        }
        super.zoomCamera(value);
        updateHeightAdjustment();
    }

    private void updateHeightAdjustment() {
        // Update the amount we should adjust the lookAt offset by based on current zoom
        lookAtHeightFactor = 1 - ((getActualDistance() - minDistance) / (maxDistance - minDistance));
    }

    private float getActualDistance() {
        return Math.max(getMinDistance(), distance - pushAmount);
    }
}
