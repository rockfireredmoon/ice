package org.iceui.effects;

import org.iceui.UIConstants;
import org.iceui.controls.FancyWindow;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.effects.Effect;

public class EffectHelper {

    public Effect reveal(Element element, Effect.EffectType type, Effect.EffectDirection direction) {
        return reveal(element, type, direction, UIConstants.UI_EFFECT_TIME);
    }

    public Effect reveal(Element element, Effect.EffectType type) {
        return reveal(element, type, null);
    }

    public Effect reveal(Element element, Effect.EffectType type, Effect.EffectDirection direction, float effectTime) {
        Effect reveal = new Effect(type, Effect.EffectEvent.Show, effectTime);
        reveal.setEffectDirection(direction);
        element.addEffect(reveal);
        if (direction != null) {
            reveal.setEffectDirection(direction);
        }
        element.hide();
        if (element instanceof FancyWindow) {
            ((FancyWindow) element).showWithEffect();
        } else if (element instanceof Panel) {
            ((Panel) element).showWithEffect();
        } else {
            element.showWithEffect();
        }
        return reveal;
    }

    public Effect destroy(Element element, Effect.EffectType type, Effect.EffectDirection direction) {
        return destroy(element, type, direction, UIConstants.UI_EFFECT_TIME);
    }

    public Effect destroy(Element element, Effect.EffectType type) {
        return destroy(element, type, null);
    }

    public Effect destroy(Element element, Effect.EffectType type, Effect.EffectDirection direction, float effectTime) {
        Effect destroy = new Effect(type, Effect.EffectEvent.Hide, effectTime);
        destroy.setEffectDirection(direction);
        element.addEffect(destroy);
        destroy.setDestroyOnHide(true);
        if (direction != null) {
            destroy.setEffectDirection(direction);
        }
        if (element instanceof FancyWindow) {
            ((FancyWindow) element).hideWithEffect();
        } else if (element instanceof Panel) {
            ((Panel) element).hideWithEffect();
        } else {
            element.hideWithEffect();
        }
        return destroy;
    }

    public Effect hide(Element element, Effect.EffectType type, Effect.EffectDirection direction) {
        return hide(element, type, direction, UIConstants.UI_EFFECT_TIME);
    }

    public Effect hide(Element element, Effect.EffectType type) {
        return hide(element, type, null);
    }

    public Effect hide(Element element, Effect.EffectType type, Effect.EffectDirection direction, float effectTime) {
        Effect hide = new Effect(type, Effect.EffectEvent.Hide, effectTime);
        hide.setEffectDirection(direction);
        element.addEffect(hide);
        if (direction != null) {
            hide.setEffectDirection(direction);
        }
        if (element instanceof FancyWindow) {
            ((FancyWindow) element).hideWithEffect();
        } else if (element instanceof Panel) {
            ((Panel) element).hideWithEffect();
        } else {
            element.hideWithEffect();
        }
        return hide;
    }

    public Effect imageSwap(Element element, Texture texture, float time) {
        Effect effect = new Effect(Effect.EffectType.ImageSwap,
                Effect.EffectEvent.Press,
                time);
        effect.setBlendImage(texture);
        effect.setElement(element);
        element.getScreen().getEffectManager().applyEffect(effect);
        return effect;
    }

    public Effect colorSwap(Element element, Effect.EffectEvent event, ColorRGBA color, float time) {
        Effect effect = new Effect(Effect.EffectType.ColorSwap,
                event,
                time);
        effect.setColor(color);
        effect.setElement(element);
        element.getScreen().getEffectManager().applyEffect(effect);
        return effect;
    }

    public Effect effect(Element element, Effect.EffectType type, Effect.EffectDirection direction, Effect.EffectEvent event, float time) {
        Effect effect = new Effect(type,
                event,
                time);
        effect.setEffectDirection(direction);
        effect.setElement(element);
        element.getScreen().getEffectManager().applyEffect(effect);
        return effect;
    }

    public Effect slideTo(Element element, Vector2f destination, Effect.EffectEvent event, float time) {
        Effect effect = new Effect(Effect.EffectType.SlideTo,
                event,
                time);
        effect.setEffectDestination(destination);
        effect.setElement(element);
        element.getScreen().getEffectManager().applyEffect(effect);
        return effect;
    }

    public Effect effect(Element element, Effect.EffectType effectType, Effect.EffectEvent event, float time) {
        Effect effect = new Effect(effectType,
                event,
                time);
        effect.setElement(element);
        element.getScreen().getEffectManager().applyEffect(effect);
        return effect;
    }
}
