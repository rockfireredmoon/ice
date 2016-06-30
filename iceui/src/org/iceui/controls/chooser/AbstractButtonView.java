package org.iceui.controls.chooser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.iceui.UIConstants;
import org.iceui.controls.SelectableItem;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;

import icetone.controls.scrolling.ScrollBar;
import icetone.controls.scrolling.ScrollPanel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseWheelListener;

/**
 */
public abstract class AbstractButtonView implements ChooserPanel.ChooserView {

    protected float previewSize = 64;
    protected Element scrollContent;
    protected ScrollPanel scrollPanel;
    protected ScrollBar vScrollBar;
    protected final ElementManager screen;
    protected ChooserPanel chooser;
    protected String cwd;
    private Map<String, ButtonSelect> items = new HashMap<String, ButtonSelect>();

    public AbstractButtonView(ElementManager screen) {
        this.screen = screen;
    }

    public void setEnabled(boolean enabled) {
        vScrollBar.getButtonScrollDown().setIsEnabled(enabled);
        vScrollBar.getButtonScrollUp().setIsEnabled(enabled);
        vScrollBar.getScrollTrack().setIsEnabled(enabled);
        vScrollBar.getScrollThumb().setIsEnabled(enabled);

        for (Element el : scrollContent.getElements()) {
            ButtonSelect sel = (ButtonSelect) el;
            sel.setIsEnabled(enabled);
        }
    }

    public Element createView(ChooserPanel chooser) {
        this.chooser = chooser;
        scrollPanel = new ScrollPanel(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("Menu").getVector4f("resizeBorders"), screen.getStyle("Menu").getString("defaultImg"));
        scrollPanel.setMinDimensions(Vector2f.ZERO);
        scrollPanel.setPreferredDimensions(new Vector2f(400, 400));
        scrollPanel.setUseContentPaging(true);
        scrollPanel.setScrollContentLayout(new WrappingLayout(4).setEqualSizeCells(true));
        scrollPanel.setUseVerticalWrap(true);
        scrollContent = scrollPanel.getScrollableArea();
        vScrollBar = scrollPanel.getVerticalScrollBar();
        return scrollPanel;
    }

    public void rebuild(String cwd, Collection<String> filesNames) {
        this.cwd = cwd;
        items.clear();
        screen.getApplication().enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                chooser.getBusy().setSpeed(UIConstants.SPINNER_SPEED);
                scrollContent.removeAllChildren();
                return null;
            }
        });

        for (String s : filesNames) {
        	Thread.yield();
            final String path = getPath(s);
            final ButtonSelect uib = new ButtonSelect(path, screen);
            if (path.equals(chooser.getSelected())) {
                uib.setSelected(true);
            }
            items.put(path, uib);
            screen.getApplication().enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    scrollPanel.addScrollableContent(uib);
                    chooser.layoutChildren();
                    return null;
                }
            });
        }
        screen.getApplication().enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                chooser.getBusy().setSpeed(0);
                chooser.layoutChildren();
                return null;
            }
        });
    }

    public void select(String file) {
        for (Map.Entry<String, ButtonSelect> en : items.entrySet()) {
            if (en.getKey().equals(file)) {
                en.getValue().setSelected(true);
                break;
            } else {
                en.getValue().setSelected(false);
            }
        }
    }

    protected abstract Element createButton(String path);

    private String getPath(String s) {
        final String path = cwd == null ? s : (cwd + "/" + s);
        return path;
    }

    private class ButtonSelect extends SelectableItem implements MouseWheelListener {

        private final Element button;

        public ButtonSelect(String path, ElementManager screen) {
            super(screen);
            setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "push[]push", "push[]push"));
            addChild(button = createButton(path));
            setControlClippingLayer(scrollPanel);
        }

        public void setIsEnabled(boolean enabled) {
        }

        public void onMouseWheelPressed(MouseButtonEvent evt) {
            evt.setConsumed();
        }

        public void onMouseWheelReleased(MouseButtonEvent evt) {
            evt.setConsumed();
        }

        @Override
        public void onMouseWheelUp(MouseMotionEvent evt) {
            if (vScrollBar != null) {
                scrollPanel.scrollYBy(-scrollPanel.getTrackInc());
            }
            evt.setConsumed();
        }

        @Override
        public void onMouseWheelDown(MouseMotionEvent evt) {
            if (vScrollBar != null) {
                scrollPanel.scrollYBy(scrollPanel.getTrackInc());
            }
            evt.setConsumed();
        }
    }
}
