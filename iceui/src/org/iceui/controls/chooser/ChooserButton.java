package org.iceui.controls.chooser;

import org.icelib.Icelib;
import org.iceui.controls.UIButton;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.texture.Image;

import icetone.core.ElementManager;
import icetone.listeners.MouseWheelListener;

public class ChooserButton extends UIButton implements MouseWheelListener {

    private String path;
    private final ChooserPanel chooser;

    public ChooserButton(String path, String imagePath, ElementManager screen, ChooserPanel chooser, float previewSize) {
        super(screen);
        this.path = path;
        this.chooser = chooser;
        setPreferredDimensions(new Vector2f(previewSize, previewSize));
        setToolTipText(Icelib.getFilename(path));
        Image img = screen.getApplication().getAssetManager().loadTexture(imagePath).getImage();
        float maxw = previewSize - borders.y - borders.z;
        float w = img.getWidth();
        float h = img.getHeight();
        if (w > maxw) {
            float f = maxw / img.getWidth();
            w = maxw;
            h *= f;
        }
        if (h > maxw) {
            float f = maxw / img.getHeight();
            h = maxw;
            w *= f;
        }
        setButtonIcon(w, h, imagePath);
    }

    @Override
    public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        chooser.choose(path);
    }

    public void onMouseWheelPressed(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    public void onMouseWheelReleased(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    @Override
    public void onMouseWheelUp(MouseMotionEvent evt) {
//            if (vScrollBar != null) {
//                vScrollBar.scrollByYInc(-vScrollBar.getTrackInc());
//            }
        evt.setConsumed();
    }

    @Override
    public void onMouseWheelDown(MouseMotionEvent evt) {
//            if (vScrollBar != null) {
//                vScrollBar.scrollByYInc(vScrollBar.getTrackInc());
//            }
        evt.setConsumed();
    }
}