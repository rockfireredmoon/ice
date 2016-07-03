package org.iceui.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;
import icetone.style.Style;

public class FancyWindow extends Element {

	private ButtonAdapter minimizeButton;
	private ButtonAdapter maximizeButton;
	private final Element buttonTray;
	private boolean wasDestroyOnHide;
	private Vector2f beforeMax;
	private Vector2f beforeMaxPos;
	private boolean managedHint = true;

	private static List<FancyWindow> windows = new ArrayList<FancyWindow>();

	private boolean isActuallyDestroyOnHide() {
		return destroyOnHide || (state == State.MINIMIZED && wasDestroyOnHide);
	}

	public static List<FancyWindow> getWindows() {
		return Collections.unmodifiableList(windows);
	}

	public enum State {

		MAXIMIZED, MINIMIZED, NORMAL
	}

	public interface Listener {

		void destroyed(FancyWindow window);

		void closed(FancyWindow window);

		void stateChanged(FancyWindow window, State oldState, State newState);

		void windowTitleChanged(FancyWindow window, String oldTitle, String newTitle);

		void selected(FancyWindow window);

		void opened(FancyWindow window);

		// Will only be caught by global listeners
		void created(FancyWindow window);
	}

	private boolean destroyOnHide;
	private final Size size;
	private boolean minimizable;
	private boolean maximizable;
	private State state = State.NORMAL;
	private List<Listener> listeners = new ArrayList<Listener>();
	private boolean selected;

	private static List<Listener> globalListeners = new ArrayList<Listener>();

	public enum Size {

		LARGE, SMALL, MINIMAP;

		public String toStyleName() {
			switch (this) {
			case LARGE:
				return "FancyWindowLarge";
			case MINIMAP:
				return "Minimap";
			default:
				return "FancyWindow";
			}
		}
	}

	protected Element dragBar;
	protected final Element content;
	private boolean useShowSound, useHideSound;
	private String showSound, hideSound;
	private float showSoundVolume, hideSoundVolume;
	private final Element dragLeft;
	private final Vector4f dbIndents;
	protected final float defaultControlSize;
	private final Element dragRight;
	private final Vector4f contentResizeBorders;
	private ButtonAdapter closeButton;
	private Vector2f buttonsOffset;

	public FancyWindow(Size size) {
		this(Screen.get(), size, false);
	}

	public FancyWindow() {
		this(Screen.get(), Size.SMALL, false);
	}

	public FancyWindow(LayoutManager contentLayoutManager) {
		this(Screen.get(), Size.SMALL, true);
		getContentArea().setLayoutManager(contentLayoutManager);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, Vector2f position, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, screen.getStyle(size.toStyleName()).getVector2f("defaultSize"),
				screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Window
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, size, closeable);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, String UID, Vector2f position, Size size, boolean closeable) {
		this(screen, UID, position, screen.getStyle(size.toStyleName()).getVector2f("defaultSize"),
				screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UID, position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the Window control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param closeable
	 *            show the close icon
	 */
	public FancyWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Size size, boolean closeable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		this.size = size;
		windows.add(this);

		UIUtil.cleanUpWindow(this);

		setLockToParentBounds(true);

		setClipPaddingByKey(size.toStyleName(), "clipPadding");
		setTextPaddingByKey(size.toStyleName(), "textPadding");

		dbIndents = screen.getStyle(size.toStyleName() + "#Dragbar").getVector4f("indents");
		defaultControlSize = screen.getStyle(size.toStyleName() + "#Dragbar").getFloat("defaultControlSize");
		buttonsOffset = screen.getStyle(size.toStyleName() + "#Dragbar").getVector2f("buttonsOffset");
		if (buttonsOffset == null) {
			buttonsOffset = Vector2f.ZERO;
		}

		dragLeft = new Element(screen, UID + ":DragLeft", new Vector2f(0, 0), new Vector2f(dbIndents.y, defaultControlSize),
				Vector4f.ZERO, screen.getStyle(size.toStyleName() + "#Dragbar").getString("leftImg"));
		dragLeft.addClippingLayer(this);
		dragLeft.setIsMovable(false);
		dragLeft.setIsResizable(false);
		addChild(dragLeft);

		dragRight = new Element(screen, UID + ":DragRight", new Vector2f(getWidth() - dbIndents.z, dbIndents.x),
				new Vector2f(dbIndents.y, defaultControlSize), Vector4f.ZERO,
				screen.getStyle(size.toStyleName() + "#Dragbar").getString("rightImg"));
		dragRight.addClippingLayer(this);
		dragRight.setIsMovable(false);
		dragRight.setIsResizable(false);
		addChild(dragRight);

		dragBar = new Element(screen, UID + ":DragBar", Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle(size.toStyleName() + "#Dragbar").getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName() + "#Dragbar").getString("centerImg"));

		dragBar.setFontSize(screen.getStyle(size.toStyleName() + "#Dragbar").getFloat("fontSize"));
		dragBar.setFontColor(screen.getStyle(size.toStyleName() + "#Dragbar").getColorRGBA("fontColor"));
		dragBar.setFont(screen.getStyle("Font").getString(screen.getStyle(size.toStyleName() + "#Dragbar").getString("fontName")));
		dragBar.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(size.toStyleName() + "#Dragbar").getString("textAlign")));
		dragBar.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(size.toStyleName() + "#Dragbar").getString("textVAlign")));
		dragBar.setTextPosition(screen.getStyle(size.toStyleName() + "#Dragbar").getFloat("textPosX"),
				screen.getStyle(size.toStyleName() + "#Dragbar").getFloat("textPosY"));
		dragBar.setTextPadding(screen.getStyle(size.toStyleName() + "#Dragbar").getFloat("textPadding"));
		dragBar.setTextWrap(LineWrapMode.valueOf(screen.getStyle(size.toStyleName() + "#Dragbar").getString("textWrap")));
		dragBar.setIsResizable(false);
		dragBar.setIsMovable(true);
		dragBar.setResizeN(false);
		dragBar.setResizeS(true);
		dragBar.setResizeW(false);
		dragBar.setResizeE(false);
		dragBar.setEffectParent(true);
		dragBar.addClippingLayer(this);
		addChild(dragBar);

		buttonTray = new Container(screen);
		buttonTray.setLayoutManager(new FlowLayout(4, BitmapFont.Align.Right));
		buttonTray.addClippingLayer(this);
		addChild(buttonTray);

		if (closeable) {
			final Style style = screen.getStyle(size.toStyleName() + "#CloseButton");
			if (style == null) {
				throw new IllegalArgumentException("No close button style for " + size.toStyleName() + "#CloseButton");
			}
			closeButton = new ButtonAdapter(screen, style.getVector2f("defaultSize")) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					if (canClose()) {
						hideWindow();
						onCloseWindow();
					}
				}
			};
			closeButton.setStyles(size.toStyleName() + "#CloseButton");
			buttonTray.addChild(closeButton);
		}

		showSound = screen.getStyle(size.toStyleName()).getString("showSound");
		useShowSound = screen.getStyle(size.toStyleName()).getBoolean("useShowSound");
		showSoundVolume = screen.getStyle(size.toStyleName()).getFloat("showSoundVolume");
		hideSound = screen.getStyle(size.toStyleName()).getString("hideSound");
		useHideSound = screen.getStyle(size.toStyleName()).getBoolean("useHideSound");
		hideSoundVolume = screen.getStyle(size.toStyleName()).getFloat("hideSoundVolume");
		contentResizeBorders = screen.getStyle(size.toStyleName() + "#Content").getVector4f("resizeBorders");

		content = new Element(screen, getUID() + ":Content", Vector2f.ZERO, LUtil.LAYOUT_SIZE, contentResizeBorders,
				screen.getStyle(size.toStyleName() + "#Content").getString("defaultImg")) {
			@Override
			public void resize(float x, float y, Borders dir) {
				// super.resize(x, y, dir);
				FancyWindow.this.resize(x, y, dir);
			}
		};
		content.setTextPaddingByKey(size.toStyleName() + "#Content", "textPadding");
		content.setLayoutManager(new MigLayout());
		content.setIgnoreMouse(false);
		content.setIsMovable(false);
		content.setResizeN(false);
		content.setResizeS(true);
		content.setResizeE(true);
		content.setResizeW(true);
		content.setIsResizable(true);
		// content.setEffectParent(true);
		content.setMinDimensions(screen.getStyle(size.toStyleName()).getVector2f("minSize"));

		addChild(content);

		LUtil.removeEffects(this);
		populateEffects(size.toStyleName());

		setLayoutManager(new FancyWindowLayout());

		// super.setIsResizable(false);
		// super.setIsMovable(false);
		setIgnoreMouse(true);
		setEffectZOrder(true);

		setIsResizable(false);
		// setResizeS(false);
		// setResizeN(false);
		// setResizeW(false);
		// setResizeE(false);
	}

	public boolean isManagedHint() {
		return managedHint;
	}

	public void setManagedHint(boolean managedHint) {
		this.managedHint = managedHint;
	}

	/**
	 * Stubbed for future use.
	 */
	public void onInitialized() {
		super.onInitialized();
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).created(this);
		}
		setIsSelected(true);
	}

	public void setTitle(String title) {
		getDragBar().setText(title);
	}

	public String getTitle() {
		return getDragBar().getText();
	}

	@Override
	public Element getAbsoluteParent() {
		return this;
	}

	@Override
	public void movedToFrontHook() {
		super.movedToFrontHook();
		setIsSelected(true);
	}

	public static void addGlobalListener(Listener l) {
		globalListeners.add(l);
	}

	public static void removeGlobalListener(Listener l) {
		globalListeners.remove(l);
	}

	public void addListener(Listener l) {
		listeners.add(l);
	}

	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	public boolean isMinimizable() {
		return minimizable;
	}

	public void setMinimizable(boolean minimizable) {
		if (this.minimizable != minimizable) {
			this.minimizable = minimizable;
			rebuildWindowButtons();
		}
	}

	public boolean isMaximizable() {
		return maximizable;
	}

	public void setMaximizable(boolean maximizable) {
		if (this.maximizable != maximizable) {
			this.maximizable = maximizable;
			rebuildWindowButtons();
		}
	}

	public State getState() {
		return state;
	}

	@Deprecated
	public void pack(boolean reposition) {
		sizeToContent();
		// final Vector2f preferredDimensions = LUtil.getPreferredSize(content);
		// content.setDimensions(preferredDimensions.clone());
		// final Vector2f newWindowSize = layoutManager.preferredSize(this);
		//
		// // Work out the change
		// Vector2f current = getDimensions();
		// Vector2f dif = new Vector2f(current.x - newWindowSize.x, current.y -
		// newWindowSize.y);
		// if (reposition) {
		// if (dif.y > 0) {
		// setY(getY() - dif.y);
		// } else {
		// setY(getY() + dif.y);
		// }
		// }
		// setDimensions(newWindowSize);
		// controlResizeHook();
		// layoutChildren();
		// checkBounds();
	}

	@Override
	public void cleanup() {
		super.cleanup();
		if (isActuallyDestroyOnHide()) {
			windows.remove(this);
			fireDestroyed();
		}
	}

	public boolean isDestroyOnHide() {
		return destroyOnHide;
	}

	public void setDestroyOnHide(boolean destroyOnHide) {
		this.destroyOnHide = destroyOnHide;
	}

	@Override
	public void setIsMovable(boolean isMovable) {
		dragBar.setIsMovable(isMovable);
	}

	@Override
	public void setIsResizable(boolean isResizable) {
		content.setIsResizable(isResizable);
	}

	// @Override
	// public final void controlResizeHook() {
	// if (getDimensions().x < getMinDimensions().x || getDimensions().y <
	// getMinDimensions().y) {
	// setDimensions(getMinDimensions());
	// }
	// onBeforeControlResizeHook();
	// super.controlResizeHook();
	// // Redo layout when window gets resized
	// // layoutChildren();
	// onControlResizeHook();
	// }

	/**
	 * Get the content element. Add content to this.
	 *
	 * @return content element
	 */
	public Element getContentArea() {
		return content;
	}

	/**
	 * Returns a pointer to the Element used as a window dragbar
	 *
	 * @return Element
	 */
	public Element getDragBar() {
		return this.dragBar;
	}

	/**
	 * Returns the drag bar height
	 *
	 * @return float
	 */
	public float getDragBarHeight() {
		return dragBar.getHeight();
	}

	/**
	 * Sets the Window title text
	 *
	 * @param title
	 *            String
	 */
	public void setWindowTitle(String title) {
		String oldTitle = dragBar.getText();
		if (!Objects.equals(oldTitle, title)) {
			dragBar.setText(title);
			for (int i = globalListeners.size() - 1; i >= 0; i--) {
				globalListeners.get(i).windowTitleChanged(this, oldTitle, title);
			}
			for (int i = listeners.size() - 1; i >= 0; i--) {
				listeners.get(i).windowTitleChanged(this, oldTitle, title);
			}
		}
	}

	@Override
	public final void controlHideHook() {
		onControlHideHook();
	}

	/**
	 * Shows the window using the default Show Effect
	 */
	public void showWindow() {
		Effect effect = getEffect(Effect.EffectEvent.Show);
		if (effect != null) {
			setIsSelected(true);
			if (useShowSound && screen.getUseUIAudio()) {
				effect.setAudioFile(showSound);
				effect.setAudioVolume(showSoundVolume);
			}
			if (effect.getEffectType() == Effect.EffectType.FadeIn) {
				Effect clone = effect.clone();
				clone.setAudioFile(null);
				this.propagateEffect(clone, false);
			} else {
				screen.getEffectManager().applyEffect(effect);
			}
		} else {
			show();
		}
	}

	public void show() {
		super.show();
		setIsSelected(true);
	}

	/**
	 * Hides the Window using the default Hide Effect
	 */
	public void hideWindow() {
		Effect effect = getEffect(Effect.EffectEvent.Hide);
		if (effect != null) {
			effect.setDestroyOnHide(isActuallyDestroyOnHide());
			if (useHideSound && screen.getUseUIAudio()) {
				effect.setAudioFile(hideSound);
				effect.setAudioVolume(hideSoundVolume);
			}
			if (effect.getEffectType() == Effect.EffectType.FadeOut) {
				Effect clone = effect.clone();
				clone.setDestroyOnHide(isActuallyDestroyOnHide());
				clone.setAudioFile(null);
				this.propagateEffect(clone, true);
			} else {
				screen.getEffectManager().applyEffect(effect);
			}
			if (isVisibleAsModal) {
				isVisibleAsModal = false;
				screen.releaseModal(this);
			}
		} else {
			hide();
		}
	}

	private void fireClosed() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).closed(this);
		}
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).closed(this);
		}
	}

	private void fireDestroyed() {
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).destroyed(this);
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).destroyed(this);
		}
	}

	private void fireSelected() {
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).selected(this);
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).selected(this);
		}
	}

	@Override
	public void hide() {
		boolean showing = isActive();
		super.hide();
		if (showing) {
			fireClosed();
			if (isActuallyDestroyOnHide()) {
				screen.removeElement(this);
				windows.remove(this);
				fireDestroyed();
			}
		}
	}

	/**
	 * Enables/disables the Window dragbar
	 *
	 * @param isMovable
	 *            boolean
	 */
	public void setWindowIsMovable(boolean isMovable) {
		this.dragBar.setIsMovable(isMovable);
	}

	/**
	 * Returns if the Window dragbar is currently enabled/disabled
	 *
	 * @return boolean
	 */
	public boolean getWindowIsMovable() {
		return this.dragBar.getIsMovable();
	}

	protected boolean isActive() {
		return getIsVisible() || (state == State.MINIMIZED);
	}

	protected boolean canClose() {
		return true;
	}

	protected void onBeforeContentLayout() {
		// For subclasses to override. Called before content is laid out
	}

	protected void onContentLayout() {
		// For subclasses to override. Called after content is laid out
	}

	protected void onControlResizeHook() {
		// For subclasses to override. Called on window resize
	}

	protected void onBeforeControlResizeHook() {
		// For subclasses to override. Called before window resize
	}

	protected void onControlHideHook() {
		// For subclasses to override. Called on window hide
	}

	protected void onCloseWindow() {
		// For subclasses to override. Called when window is manually closed
	}

	// protected void checkBounds() {
	// Vector2f minSize = LUtil.getMinimumSize(this);
	// if (getWidth() < minSize.x) {
	// setWidth(minSize.x);
	// }
	// if (getHeight() < minSize.y) {
	// setHeight(minSize.y);
	// }
	//
	// if (getX() < 0) {
	// setX(0);
	// } else if (getX() + getWidth() > screen.getWidth()) {
	// setX(screen.getWidth() - getWidth());
	// }
	// if (getY() < 0) {
	// setY(0);
	// } else if (getY() + getHeight() > screen.getHeight()) {
	// setY(screen.getHeight() - getHeight());
	// }
	// }

	public void maximize() {
		if (state != State.NORMAL) {
			throw new IllegalArgumentException("Not normal.");
		}
		if (minimizeButton != null) {
			minimizeButton.setIsEnabled(false);
		}
		beforeMaxPos = getPosition().clone();
		beforeMax = getDimensions().clone();
		setDimensions(new Vector2f(screen.getWidth(), screen.getHeight()));
		setPosition(0, 0);
		state = State.MAXIMIZED;
		screen.dirtyLayout();
		screen.layoutChildren();
	}

	@Override
	public Vector2f getMinDimensions() {
		if (state == State.MAXIMIZED)
			return new Vector2f(screen.getWidth(), screen.getHeight());
		return super.getMinDimensions();
	}

	@Override
	public Vector2f getMaxDimensions() {
		if (state == State.MAXIMIZED)
			return new Vector2f(screen.getWidth(), screen.getHeight());
		return super.getMaxDimensions();
	}

	@Override
	public Vector2f getPreferredDimensions() {
		if (state == State.MAXIMIZED)
			return new Vector2f(screen.getWidth(), screen.getHeight());
		return super.getPreferredDimensions();
	}

	public void minimize() {
		if (state == State.MINIMIZED) {
			throw new IllegalArgumentException("Already minimized.");
		}
		wasDestroyOnHide = destroyOnHide;
		setDestroyOnHide(false);
		setIsSelected(false);
		hideWindow();
		updateState(State.MINIMIZED);
	}

	public void restore() {
		if (state == State.MINIMIZED) {
			setDestroyOnHide(wasDestroyOnHide);
			showWindow();
			updateState(State.NORMAL);
			if (minimizeButton != null) {
				minimizeButton.setIsEnabled(true);
			}
		} else if (state == State.MAXIMIZED) {
			setDimensions(beforeMax);
			setPosition(beforeMaxPos);
			state = State.NORMAL;
			screen.dirtyLayout();
			screen.layoutChildren();
			if (minimizeButton != null) {
				minimizeButton.setIsEnabled(true);
			}
		} else {
			throw new IllegalArgumentException("Already restored.");
		}
	}

	public boolean getIsSelected() {
		return selected;
	}

	public void setIsSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			if (selected) {
				fireSelected();
				dragLeft.setColorMap(screen.getStyle(size.toStyleName() + "#Dragbar").getString("leftImgSelected"));
				dragBar.setColorMap(screen.getStyle(size.toStyleName() + "#Dragbar").getString("centerImgSelected"));
				dragRight.setColorMap(screen.getStyle(size.toStyleName() + "#Dragbar").getString("rightImgSelected"));
			} else {
				dragLeft.setColorMap(screen.getStyle(size.toStyleName() + "#Dragbar").getString("leftImg"));
				dragBar.setColorMap(screen.getStyle(size.toStyleName() + "#Dragbar").getString("centerImg"));
				dragRight.setColorMap(screen.getStyle(size.toStyleName() + "#Dragbar").getString("rightImg"));
			}

		}
	}

	private void updateState(State newState) {
		State oldState = state;
		state = newState;
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).stateChanged(this, oldState, state);
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).stateChanged(this, oldState, state);
		}
	}

	private void rebuildWindowButtons() {
		buttonTray.removeAllChildren();
		if (minimizable) {
			minimizeButton = new ButtonAdapter(screen,
					screen.getStyle(size.toStyleName() + "#MinimizeButton").getVector2f("defaultSize")) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					minimize();
				}
			};
			minimizeButton.setStyles(size.toStyleName() + "#MinimizeButton");
			buttonTray.addChild(minimizeButton);
		}
		if (maximizable) {
			maximizeButton = new ButtonAdapter(screen,
					screen.getStyle(size.toStyleName() + "#MaximizeButton").getVector2f("defaultSize")) {

				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					if (state.equals(State.MAXIMIZED)) {
						restore();
					} else {
						maximize();
					}
				}
			};
			maximizeButton.setStyles(size.toStyleName() + "#MaximizeButton");
			buttonTray.addChild(maximizeButton);
		}
		if (closeButton != null) {
			buttonTray.addChild(closeButton);
		}
	}

	class FancyWindowLayout extends AbstractLayout {

		public Vector2f minimumSize(Element parent) {
			Vector2f contentMin = LUtil.getMinimumSize(content);
			contentMin.y += defaultControlSize + borders.x + borders.w;
			contentMin.x += borders.y + borders.z;
			return contentMin;
		}

		public Vector2f maximumSize(Element parent) {
			return null;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f contentPref = LUtil.getPreferredSize(content);
			contentPref.y += defaultControlSize + borders.x + borders.w;
			contentPref.x += borders.y + borders.z;
			return contentPref;
		}

		public void layout(Element childElement) {
			onBeforeContentLayout();
			LUtil.setBounds(content, 0, defaultControlSize, childElement.getWidth(), childElement.getHeight() - defaultControlSize);
			LUtil.setBounds(dragBar, dbIndents.y, 0, childElement.getWidth() - dbIndents.y - dbIndents.z, defaultControlSize);
			LUtil.setBounds(dragLeft, 0, 0, dbIndents.y, defaultControlSize);
			LUtil.setBounds(dragRight, childElement.getWidth() - dbIndents.z, 0, dbIndents.y, defaultControlSize);

			Vector2f buttonPref = LUtil.getPreferredSize(buttonTray);

			float y = Math.round(((dragBar.getHeight() - dbIndents.x - dbIndents.w) - buttonPref.y) / 2f);
			LUtil.setBounds(buttonTray, childElement.getWidth() - dbIndents.z - buttonPref.x - buttonsOffset.x, y, buttonPref.x,
					Math.min(buttonPref.y, defaultControlSize));

			onContentLayout();
		}

		public void constrain(Element child, Object constraints) {
		}

		public void remove(Element child) {
		}
	}
}
