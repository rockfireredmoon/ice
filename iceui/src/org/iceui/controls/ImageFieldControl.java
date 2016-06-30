package org.iceui.controls;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.iceui.controls.chooser.ChooserPanel;
import org.iceui.controls.chooser.ImageThumbView;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;
import icetone.listeners.MouseButtonListener;

public abstract class ImageFieldControl extends ChooserFieldControl {

    class Swatch extends Element implements MouseButtonListener {

        Swatch(ElementManager screen, String UID) {
            super(screen, UID, Vector2f.ZERO, new Vector2f(20, 20), Vector4f.ZERO, screen.getStyle("UIButton").getString("defaultImg"));
        }

        public void onMouseLeftPressed(MouseButtonEvent evt) {
        }

        public void onMouseLeftReleased(MouseButtonEvent evt) {
            if (!showChooserButton) {
                showChooser(evt.getX(), evt.getY());
            }
        }

        public void onMouseRightPressed(MouseButtonEvent evt) {
        }

        public void onMouseRightReleased(MouseButtonEvent evt) {
        }
    }
    private Swatch colorSwatch;

    public ImageFieldControl(ElementManager screen, String initial, Collection<String> imageResources, Preferences pref) {
        super(screen, initial, imageResources, pref);
    }

    public ImageFieldControl(ElementManager screen, String initial, boolean includeAlpha, Collection<String> imageResources, Preferences pref) {
        super(screen, initial, imageResources, pref);
    }

    public ImageFieldControl(ElementManager screen, String UID, String initial, boolean includeAlpha, Collection<String> imageResources, Preferences pref) {
        super(screen, UID, initial, true, true, imageResources, pref);
    }

    public ImageFieldControl(ElementManager screen, String initial, boolean showHex, boolean showChooserButton, Collection<String> imageResources, Preferences pref) {
        super(screen, initial, showHex, showChooserButton, imageResources, pref);
    }

    public ImageFieldControl(ElementManager screen, String UID, String initial, boolean includeAlpha, boolean showHex, boolean showChooserButton, Collection<String> imageResources, Preferences pref) {
        super(screen, UID, initial, showHex, showChooserButton, imageResources, pref);
    }

    @Override
    protected void createLayout() {

        // Configure layout depending on options
        if (showName) {
            if (showChooserButton) {
                setLayoutManager(new MigLayout(screen, "fill, gap 1, ins 0", "[][grow][shrink 0]"));
            } else {
                setLayoutManager(new MigLayout(screen, "fill, gap 1, ins 0", "[][grow]"));
            }
        } else {
            if (showChooserButton) {
                setLayoutManager(new MigLayout(screen, "fill, gap 1, ins 0", "[][shrink 0]"));
            } else {
                setLayoutManager(new MigLayout(screen, "fill, gap 1, ins 0", "[]"));
            }
        }

        // Swatch
        colorSwatch = new Swatch(screen, getUID() + ":colorSwatch");
        addChild(colorSwatch, "width 22, height 22");

    }

    @Override
    protected ChooserPanel.ChooserView createView() {
        return new ImageThumbView(screen);
    }

    @Override
    protected String getIconPath() {
        return "BuildIcons/Icon-32-Build-PickImage.png";
    }

    @Override
    protected void updateControls() {
        super.updateControls();
        if (value == null) {
            colorSwatch.getElementTexture().setImage(screen.getApplication().getAssetManager().loadTexture(screen.getStyle("UIButton").getString("defaultImg")).getImage());
        } else {
            colorSwatch.getElementTexture().setImage(screen.getApplication().getAssetManager().loadTexture(getChooserPathFromValue()).getImage());
        }
    }
}
