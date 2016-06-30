package org.iceui.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.windows.TabControl;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;

/**
 * Extension of {@link TabControl} that allows icons to be used in place of
 * text for tab headings.
 */
public class IconTabControl extends XTabControl {
    
    public IconTabControl(ElementManager screen) {
        super(screen);
    }
    
    public IconTabControl(ElementManager screen, Vector2f position) {
        super(screen, position);
    }
    
    public IconTabControl(ElementManager screen, Vector2f position, Vector2f dimensions) {
        super(screen, position, dimensions);
    }
    
    public IconTabControl(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, position, dimensions, resizeBorders, defaultImg);
    }
    
    public IconTabControl(ElementManager screen, String UID, Vector2f position) {
        super(screen, UID, position);
    }
    
    public IconTabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
        super(screen, UID, position, dimensions);
    }
    
    public IconTabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, UID, position, dimensions, resizeBorders, defaultImg, Orientation.HORIZONTAL);
    }
    
    @Override
    protected String getTabStyleName() {
        return "IconTab";
    }
    
    public void addTabWithIcon(String icon) {
        addTabWithIcon(null, icon);
    }
    
    public void addTabWithIcon(String toolTipText, String icon) {
        addTabWithIcon("", toolTipText, icon);
    }
    
    public void addTabWithIcon(String title, String toolTipText, String icon) {
        ButtonAdapter tab = getTabButtonWithIcon(title, toolTipText, icon);
        addTab(title, tab, true);
    }
    
    private ButtonAdapter getTabButtonWithIcon(String title, String toolTipText, String icon) {
//        Vector2f pos = new Vector2f();
//        Vector2f dim = new Vector2f(tabWidth, tabHeight);
//        if (getOrientation() == Orientation.HORIZONTAL) {
//            pos.set(tabInc * tabButtonIndex, 0);
//        } else {
//            pos.set(0, tabInc * tabButtonIndex);
//        }
        ButtonAdapter tab = new ButtonAdapter(
                screen,
                getUID() + ":Tab" + tabButtonIndex,
                Vector2f.ZERO,
                LUtil.LAYOUT_SIZE,
                (getOrientation() == Orientation.HORIZONTAL) ? screen.getStyle(getTabStyleName()).getVector4f("resizeBorders") : screen.getStyle("Tab").getVector4f("resizeBordersV"),
                (getOrientation() == Orientation.HORIZONTAL) ? screen.getStyle(getTabStyleName()).getString("defaultImg") : screen.getStyle("Tab").getString("defaultImgV")) {
        };
        
        tab.clearAltImages();
        tab.setButtonHoverInfo(
                screen.getStyle(getTabStyleName()).getString("hoverImg"),
                screen.getStyle(getTabStyleName()).getColorRGBA("hoverColor"));
        tab.setButtonPressedInfo(
                screen.getStyle(getTabStyleName()).getString("pressedImg"),
                screen.getStyle(getTabStyleName()).getColorRGBA("pressedColor"));
        
        Vector2f iconSize = screen.getStyle(getTabStyleName()).getVector2f("iconSize");
        tab.setButtonIcon(iconSize.x, iconSize.y, icon);
        if (title != null) {
            tab.setText(title);
        }
        if (toolTipText != null) {
            tab.setToolTipText(toolTipText);
        }
        
        tab.setDocking(Docking.NW);
        tab.setScaleEW(false);
        tab.setScaleNS(false);

        return tab;
    }
    
}
