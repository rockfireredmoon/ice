package org.iceui.controls;

import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;

public class BigButton extends ButtonAdapter {

    public BigButton(String text, ElementManager screen) {
    	this(screen);
    	setText(text);
    }

    public BigButton(ElementManager screen) {
        super(screen, 
        		Vector2f.ZERO,
                LUtil.LAYOUT_SIZE,
                screen.getStyle("BigButton").getVector4f("resizeBorders"),
                screen.getStyle("BigButton").getString("defaultImg"));
        setStyles("BigButton");

//        this.setFont(screen.getStyle("Font").getString(screen.getStyle("BigButton").getString("fontName")));
//        this.setFontSize(screen.getStyle("BigButton").getFloat("fontSize"));
//        this.setFontColor(screen.getStyle("BigButton").getColorRGBA("fontColor"));
//        this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("BigButton").getString("textVAlign")));
//        this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("BigButton").getString("textAlign")));
//        this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("BigButton").getString("textWrap")));
//
//
//        if (screen.getStyle("BigButton").getString("hoverImg") != null) {
//            setButtonHoverInfo(
//                    screen.getStyle("BigButton").getString("hoverImg"),
//                    screen.getStyle("BigButton").getColorRGBA("hoverColor"));
//        }
//        if (screen.getStyle("BigButton").getString("pressedImg") != null) {
//            setButtonPressedInfo(
//                    screen.getStyle("BigButton").getString("pressedImg"),
//                    screen.getStyle("BigButton").getColorRGBA("pressedColor"));
//        }
//        originalFontColor = fontColor.clone();
//
//        hoverSound = screen.getStyle("BigButton").getString("hoverSound");
//        useHoverSound = screen.getStyle("BigButton").getBoolean("useHoverSound");
//        hoverSoundVolume = screen.getStyle("BigButton").getFloat("hoverSoundVolume");
//        pressedSound = screen.getStyle("BigButton").getString("pressedSound");
//        usePressedSound = screen.getStyle("BigButton").getBoolean("usePressedSound");
//        pressedSoundVolume = screen.getStyle("BigButton").getFloat("pressedSoundVolume");

        // TODO cant undoo the effects :\
//        populateEffects("BigButton");
//        if (Screen.isAndroid()) {
//            removeEffect(Effect.EffectEvent.Hover);
//            removeEffect(Effect.EffectEvent.TabFocus);
//            removeEffect(Effect.EffectEvent.LoseTabFocus);
//        }
    }
}
