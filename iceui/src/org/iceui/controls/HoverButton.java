package org.iceui.controls;

import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.ElementManager;
import icetone.effects.Effect;
import icetone.effects.Effect.EffectEvent;
import icetone.effects.Effect.EffectType;
import icetone.style.Style;

public class HoverButton extends ButtonAdapter {

	private Style style;
	private Texture hoverImage;
	private Texture blankImg;

	public HoverButton(ElementManager screen, String styleName) {
		super(screen);
		init(styleName);
	}

	public HoverButton(ElementManager screen, Vector2f dimensions, String defaultImg, String styleName) {
		super(screen, Vector2f.ZERO, dimensions, Vector4f.ZERO, defaultImg);
		init(styleName);
	}

	public HoverButton(ElementManager screen, Vector2f dimensions, String styleName) {
		super(screen, dimensions);
		init(styleName);
	}

	public HoverButton(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, String styleName) {
		super(screen, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
		init(styleName);
	}

	public HoverButton(ElementManager screen, String UID, String styleName) {
		super(screen, UID);
		init(styleName);
	}

	public HoverButton(ElementManager screen, String UID, Vector2f dimensions, String styleName) {
		super(screen, UID, dimensions);
		init(styleName);
	}

	public HoverButton(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			String styleName) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
		init(styleName);
	}

	private void init(String styleName) {
		setStyles(styleName);
		style = screen.getStyle(styleName);
		hoverImage = screen.createNewTexture(style.getString("hoverImg"));
		blankImg = screen.createNewTexture(screen.getStyle("Common").getString("blankImg"));
		final Vector2f defSize = style.getVector2f("defaultSize");
		setButtonHoverInfo(null, null);
		setButtonIcon(defSize.x, defSize.y, screen.getStyle("Common").getString("blankImg"));
	}

	@Override
	public void onButtonFocus(MouseMotionEvent evt) {
		super.onButtonFocus(evt);
		Effect effect = new Effect(EffectType.Pulse, EffectEvent.Hover, 0.25f);
		effect.setBlendImage(hoverImage);
		effect.setElement(this.getButtonIcon());
		if (style.getBoolean("useHoverSound")) {
			effect.setAudioVolume(style.getFloat("hoverSoundVolume"));
			effect.setAudioFile(style.getString("hoverSound"));
		}
		screen.getEffectManager().applyEffect(effect);
	}

	@Override
	public void onButtonLostFocus(MouseMotionEvent evt) {
		super.onButtonLostFocus(evt);
		Effect effect = new Effect(EffectType.ImageSwap, EffectEvent.LoseFocus, 0.25f);
		effect.setBlendImage(blankImg);
		effect.setElement(this.getButtonIcon());
		screen.getEffectManager().applyEffect(effect);
	}
}
