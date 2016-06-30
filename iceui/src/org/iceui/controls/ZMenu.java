package org.iceui.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.menuing.AutoHide;
import icetone.controls.menuing.Menu;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.controls.text.TextElement;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.FillLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.MouseFocusListener;
import icetone.listeners.MouseWheelListener;
import icetone.listeners.TabFocusListener;

/**
 * A replacement <strong>Menu</menu> that allows any components in the gutter,
 * uses the new {@link ScrollPanel} and does a few other things the exiting
 * {@link Menu} can't.
 * 
 * <h2>Example Usage</h2> <code>
 * <pre>
 * 
 * </pre>
 * </code>
 * 
 * @author rockfire
 */
public class ZMenu extends Element implements MouseWheelListener, AutoHide, TabFocusListener {

	protected ZMenu showingChildMenu;
	protected ZMenuItem childMenusItem;
	protected float menuHeight = -1f;
	protected float childMenuGap = 8f;
	protected BitmapFont.Align direction = BitmapFont.Align.Right;
	private final ScrollPanel scroller;
	private ZMenu caller;
	private final Vector2f minSize;
	private boolean destroyOnHide = true;
	private Element inner;

	public ZMenu() {
		this(Screen.get());
	}

	public ZMenu(ElementManager screen) {
		super(screen, "ZMenu" + UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("ZMenu").getVector4f("resizeBorders"), screen.getStyle("ZMenu").getString("defaultImg"));
		setLayoutManager(new FillLayout());
		
		inner = new Element(screen, screen.getStyle("ZMenu").getVector4f("menuResizeBorders"),
				screen.getStyle("ZMenu").getString("menuImg"));
		inner.setLayoutManager(new FillLayout());
		inner.setTileImageByKey("ZMenu", "tileMenuImg");
		addChild(inner);
		
		setTileImageByKey("ZMenu", "tileImg");

		scroller = new ScrollPanel(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				Vector4f.ZERO,
				null);
		scroller.setScrollSize(screen.getStyle("ZMenu").getFloat("scrollSize"));
		scroller.setHorizontalScrollBarMode(ScrollBarMode.Never);
		
		// Configure default layout
		((WrappingLayout) scroller.getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		((WrappingLayout) scroller.getScrollContentLayout()).setEqualSizeCells(true);
		((WrappingLayout) scroller.getScrollContentLayout()).setWidth(1);
		((WrappingLayout) scroller.getScrollContentLayout()).setFill(false);

		// So some mouse events bubble up (except scrolling)
		scroller.getScrollBounds().setIgnoreMouseButtons(true);
		scroller.getScrollBounds().setIgnoreMouse(true);

		minSize = screen.getStyle("ZMenu").getVector2f("minSize");
		LayoutManager scrollContentLayout = scroller.getScrollContentLayout();
		if (scrollContentLayout instanceof WrappingLayout) {
			((WrappingLayout) scrollContentLayout).setGap(screen.getStyle("ZMenu").getInt("rowGap"));
			scroller.getScrollBounds().setTextPaddingByKey("ZMenu", "margin");
		}

		inner.addChild(scroller, "growx, growy");

		setTextPaddingByKey("ZMenu", "textPadding");
		setIsGlobalModal(true);
		setIgnoreMouseWheel(false);
		setPriority(ZPriority.MENU);
		setLockToParentBounds(true);
	}

	public float getChildMenuGap() {
		return childMenuGap;
	}

	public void setChildMenuGap(float childMenuGap) {
		this.childMenuGap = childMenuGap;
	}

	public List<ZMenuItem> getMenuItems() {
		List<ZMenuItem> mi = new ArrayList<ZMenuItem>();
		for (Element e : scroller.getScrollableArea().getElements()) {
			mi.add((ZMenuItem) e);
		}
		return mi;
	}

	public float getMenuHeight() {
		return menuHeight;
	}

	public void setMenuHeight(float menuHeight) {
		this.menuHeight = menuHeight;
	}

	public ZMenuItem addMenuItem(String caption, Object value) {
		return addMenuItem(caption, null, value);
	}

	public void setDestroyOnHide(boolean destroyOnHide) {
		this.destroyOnHide = destroyOnHide;
	}

	public void removeAllMenuItems() {
		scroller.getScrollableArea().removeAllChildren();
		layoutChildren();
	}

	public ZMenuItem addMenuItem(String caption, Element itemElement, Object value) {
		final ZMenuItem zMenuItem = new ZMenuItem(screen, caption, itemElement, value);
		scroller.addScrollableContent(zMenuItem);
		return zMenuItem;
	}

	public void pack() {
		// First pass - check whether we need a 'gutters', left or right
		float leftGutterWidth = 0;
		float rightGutterWidth = 0;
		for (Element el : scroller.getScrollableArea().getElements()) {
			final ZMenuItem menuItem = (ZMenuItem) el;
			leftGutterWidth = Math.max(menuItem.getLeftGutterWidth(), leftGutterWidth);
			rightGutterWidth = Math.max(menuItem.getRightGutterWidth(), rightGutterWidth);
		}

		// Layout each item
		for (Element el : scroller.getScrollableArea().getElements()) {
			((ZMenuItem) el).pack(leftGutterWidth, rightGutterWidth);
		}
		
		sizeToContent();
	}

	@Override
	public void controlHideHook() {
		super.controlHideHook();

		// TODO
		//
		// This is hacky. We need to ensure child menus are hidden no matter
		// how this menu is closed. But, doing so here can cause Screen
		// to complain about concurrent modification. This work around
		// defers the close till the next tick.

		if (showingChildMenu != null && showingChildMenu.getIsVisible()) {
			final ZMenu fShowingChildMenu = showingChildMenu;
			app.enqueue(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (fShowingChildMenu.getUID() != null) {
						screen.removeElement(fShowingChildMenu);
						fShowingChildMenu.controlHideHook();
					}
					return null;
				}
			});
		}
		if (caller != null) {
			caller.childHidden();
			caller = null;
		}
	}

	public void showMenu(ZMenu caller, float x, float y) {

		this.caller = caller;

		LUtil.setPosition(this, x, y);

		pack();

		Vector2f pref = getDimensions();

		// Don't let preferred dimensions be smaller than the minimum
		if (minSize != null) {
			pref.x = Math.max(pref.x, minSize.x);
			pref.y = Math.max(pref.y, minSize.y);
		}

		float ny = Math.max(0, y);
		float nx = Math.max(0, x);

		// If the new of this menu would take it offscreen, restrict it, and
		// reverse
		// the direction of future menus
		if (direction == BitmapFont.Align.Right && pref.x + nx > screen.getWidth()) {
			nx = screen.getWidth() - pref.x;
			direction = BitmapFont.Align.Left;
		} else if (direction == BitmapFont.Align.Left && nx < 0) {
			nx = 0;
			direction = BitmapFont.Align.Right;
		}

		if (ny < screen.getHeight() / 2f) {
			// If this leaves the menu hanging below the edge of the screen
			// shift it up
			if (ny + pref.y > screen.getHeight()) {
				ny = 0;
			}

			// If this still leaves the menu hanging below the edge of the
			// screen
			// reduce its size
			if (ny + pref.y > screen.getHeight()) {
				pref.y = screen.getHeight();
			}
		} else {
			// Shift the menu down by its height so it 'hangs' from the
			// activation
			// point
			ny -= pref.y;

			// If this pushes the menu past the edge of the screen, shift it
			// back
			// towards to the top
			if (ny < 0) {
				ny = 0;
			}

			// If this leaves the menu hanging below the edge of the screen
			// again,
			// reduce the size
			if (ny + pref.y > screen.getHeight()) {
				pref.y = screen.getHeight() - ny;
			}
		}

		// Postion, size, layout and show
		LUtil.setDimensions(this, pref.x, pref.y);
		LUtil.setPosition(this, nx, ny);
		layoutChildren();
		
		scroller.scrollToTop();
		show();
		
		bringAllToFront();
	}

	protected void bringAllToFront() {
		bringToFront();
		if(caller != null)
			caller.bringToFront();
	}

	protected void itemSelected(ZMenu originator, ZMenuItem item) {
		// Bubble up to the caller by default if there is one
		if (caller != null) {
			caller.itemSelected(originator, item);
		}
		hideThisAndChildren();
		if (this.equals(originator)) {
			onItemSelected(item);
		}
	}

	public void close() {
		if (caller != null) {
			caller.close();
		} else {
			hideThisAndChildren();
		}
	}

	protected void onItemSelected(ZMenuItem item) {
	}

	protected boolean isDestroyOnHide() {
		if (caller != null) {
			return caller.isDestroyOnHide();
		}
		return destroyOnHide;
	}

	protected void hideThisAndChildren() {
		if (isDestroyOnHide()) {
			screen.removeElement(this);
		} else {
			hide();
		}
		for (Element e : scroller.getScrollableArea().getElements()) {
			ZMenuItem z = (ZMenuItem) e;
			if (z.itemElement != null && z.itemElement instanceof ZMenu) {
				((ZMenu) z.itemElement).hideThisAndChildren();
			}
		}
		if(caller != null) {
			caller.bringAllToFront();
		}
	}

	protected void childHidden() {
		if (childMenusItem != null) {
			childMenusItem.setSelected(false);
		}
		childMenusItem = null;
		showingChildMenu = null;
	}

	public static class ZMenuItem extends Element implements MouseFocusListener, MouseButtonListener {

		private final Object value;
		private TextElement itemText;
		private boolean selected;
		private final Element itemElement;
		private boolean selectable = true;
		private Vector2f min;

		public ZMenuItem(ElementManager screen, String caption, Element itemElement, Object value) {
			super(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, screen.getStyle("ZMenuItem#Deselected").getVector4f("resizeBorders"),
					null);

			this.itemElement = itemElement;

			setIgnoreMouseWheel(true);

			if (caption != null) {
				itemText = new TextElement(screen, getFont()) {
					@Override
					public void onUpdate(float tpf) {
					}

					@Override
					public void onEffectStart() {
					}

					@Override
					public void onEffectStop() {
					}
				};
				configureFont();
				itemText.setText(caption);
				itemText.setLineWrapMode(LineWrapMode.NoWrap);
			}
			reconfigureStyles(selected);
			this.value = value;

		}

		public Object getValue() {
			return value;
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isSelectable() {
			return selectable;
		}

		@Override
		public void onGetFocus(MouseMotionEvent evt) {
			ZMenu parentMenu = getMenu();
			if (parentMenu.showingChildMenu != null) {
				screen.removeElement(parentMenu.showingChildMenu);
				parentMenu.showingChildMenu.controlHideHook();
			}
			if (itemElement != null && itemElement instanceof ZMenu) {
				ZMenu menu = (ZMenu) itemElement;
				screen.addElement(menu);
				parentMenu.childMenusItem = this;
				parentMenu.showingChildMenu = menu;
				if (parentMenu.direction == BitmapFont.Align.Right) {
					menu.showMenu(getMenu(), getAbsoluteX() + getWidth() + getMenu().getChildMenuGap(),
							evt.getY() - ( getHeight() / 2f));
				} else {
					menu.showMenu(getMenu(), getAbsoluteX() - getWidth() - getMenu().getChildMenuGap(),
							evt.getY() - ( getHeight() / 2f));
				}
			}
			if (isSelectable()) {
				setSelected(true);
			}
		}

		public ZMenu getMenu() {
			return (ZMenu) getAbsoluteParent();
		}

		@Override
		public void onLoseFocus(MouseMotionEvent evt) {
			ZMenu parentMenu = getMenu();
			if (parentMenu.childMenusItem == null || !parentMenu.childMenusItem.equals(this)) {
				setSelected(false);
			}
		}

		public Element getItemTextElement() {
			return itemText;
		}

		public ZMenuItem setSelectable(boolean selectable) {
			this.selectable = selectable;
			return this;
		}

		public ZMenuItem setSelected(boolean selected) {
			if (selected != this.selected) {
				if (selected && !selectable) {
					throw new IllegalStateException("Item is not selectable.");
				}
				this.selected = selected;
				reconfigureStyles(selected);
			}
			return this;
		}

		protected int getLeftGutterWidth() {
			return itemElement == null || itemElement instanceof ZMenu || itemText == null ? 0
					: (int) LUtil.getPreferredSize(itemElement).x;
		}

		protected int getRightGutterWidth() {
			final Vector2f arrowSize = screen.getStyle("Common").getVector2f("arrowSize");
			return itemElement != null && itemElement instanceof ZMenu ? (arrowSize == null ? 20 : (int) arrowSize.x) : 0;
		}

		void pack(float leftGutterWidth, float rightGutterWidth) {
			removeAllChildren();
			Vector2f arrowSize = screen.getStyle("Common").getVector2f("arrowSize");
			if (arrowSize == null) {
				arrowSize = new Vector2f(16, 16);
			}
			
			if (leftGutterWidth > 0 && rightGutterWidth == 0) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0, fill",
						"[" + leftGutterWidth + "!, align 50%, grow][fill,grow]", "[align 50%]"));
				if (itemText == null) {
					addChild(itemElement, "span 2, growx");
				} else {
					if (itemElement != null) {
						addChild(itemElement);
						addChild(itemText);
					} else {
						addChild(new Container(screen));
						addChild(itemText);
					}
				}
			} else if (leftGutterWidth == 0 && rightGutterWidth > 0) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0, fill",
						"[fill,grow][" + rightGutterWidth + "!, align 50%, grow]", "[align 50%]"));

				if (itemElement != null && itemElement instanceof ZMenu) {
					if (itemText != null) {
						addChild(itemText);
					} else {
						addChild(new Container(screen));
					}
					addChild(new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, arrowSize, Vector4f.ZERO,
							screen.getStyle("Common").getString("arrowRight")));
				} else {
					if (itemText == null) {
						addChild(new Label(screen), "span 2");
					} else {
						addChild(itemText, "span 2");
					}
				}
			} else if (leftGutterWidth > 0 && rightGutterWidth > 0) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0, fill",
						"[" + leftGutterWidth + "!, align 50%, grow][fill,grow][" + rightGutterWidth + "!, align 50%, grow]",
						"[align 50%]"));
				if (itemElement != null && !(itemElement instanceof ZMenu)) {
					// Generic component
					if (itemText != null) {
						addChild(itemElement);
						addChild(itemText, "span 2, growx");
					} else {
						addChild(itemElement, "span 3, growx");
					}
				} else if (itemElement != null) {

					// Submenu
					if (itemText != null) {
						addChild(new Container(screen));
						addChild(itemText, "growx");
						addChild(new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, arrowSize, Vector4f.ZERO,
								screen.getStyle("Common").getString("arrowRight")));
					} else {
						addChild(new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, arrowSize, Vector4f.ZERO,
								screen.getStyle("Common").getString("arrowRight")), "span 3");
					}
				} else {
					// Text
					if (itemText != null) {
						addChild(new Container(screen));
						addChild(itemText, "span 2, growx");
					} else {
						addChild(new Container(screen), "span 3, growx");
					}
				}
			} else {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0, fill", "[fill, grow]", "[align 50%]"));
				if (itemText == null) {
					if (itemElement == null) {
						addChild(new Container(screen));
					} else {
						addChild(itemElement, "growx");
					}
				} else {
					addChild(itemText);
				}
			}
		}

		private void reconfigureStyles(boolean selected) {
			// Selection highlight effects
			final String style = selected ? "ZMenuItem#Selected" : "ZMenuItem#Deselected";
			ColorRGBA selColor = screen.getStyle(style).getColorRGBA("highlightColor");
			getElementMaterial().setColor("Color", selColor == null ? new ColorRGBA(1, 1, 1, 0) : selColor);
			borders.set(screen.getStyle(style).getVector4f("resizeBorders"));
			final String img = screen.getStyle(style).getString("defaultImg");
			if (img != null) {
				setColorMap(img);
			}

			// Optional minimum dimensions
			min = screen.getStyle(style).getVector2f("minSize");
			if (min != null) {
				setMinDimensions(min);
			}

			// Alignment
			if (itemText != null) {
				configureFont();
				itemText.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(style).getString("textAlign")));
				itemText.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(style).getString("textVAlign")));
				itemText.setMinDimensions(min.subtract(textPadding.y + textPadding.z, textPadding.x + textPadding.w));
			}

			// Layout
			layoutChildren();
		}

		public void onMouseLeftPressed(MouseButtonEvent evt) {
			// Pass the event on to the gutter element if there is one
			if (itemElement != null && itemText != null && itemElement instanceof MouseButtonListener) {
				((MouseButtonListener) itemElement).onMouseLeftPressed(evt);
			}
		}

		public void onMouseLeftReleased(MouseButtonEvent evt) {
			// Pass the event on to the gutter element if there is one
			if (itemElement != null && itemText != null && itemElement instanceof MouseButtonListener) {
				((MouseButtonListener) itemElement).onMouseLeftReleased(evt);
			}
			// else {
			getMenu().itemSelected(getMenu(), this);
			// }
		}

		public void onMouseRightPressed(MouseButtonEvent evt) {
			// Pass the event on to the gutter element if there is one
			if (itemElement != null && itemText != null && itemElement instanceof MouseButtonListener) {
				((MouseButtonListener) itemElement).onMouseRightPressed(evt);
			}
		}

		public void onMouseRightReleased(MouseButtonEvent evt) {
			// Pass the event on to the gutter element if there is one
			if (itemElement != null && itemText != null && itemElement instanceof MouseButtonListener) {
				((MouseButtonListener) itemElement).onMouseRightReleased(evt);
			}
		}

		private void configureFont() {

			final String style = selected ? "ZMenuItem#Selected" : "ZMenuItem#Deselected";

			// Font
			final String fontName = screen.getStyle(style).getString("fontName");
			if (fontName != null) {
				itemText.setFont(screen.getStyle("Font").getString(fontName));
			} else {
				final String fontPath = screen.getStyle(style).getString("font");
				if (fontPath != null) {
					itemText.setFont(fontPath);
				} else {
					itemText.setFont(screen.getStyle("Font").getString("defaultFont"));
				}
			}

			// Font size
			final float fsz = screen.getStyle(style).getFloat("fontSize");
			if (fsz > 0) {
				itemText.setFontSize(fsz);
			}
		}
	}

	@Override
	public void onMouseWheelPressed(MouseButtonEvent evt) {
	}

	@Override
	public void onMouseWheelReleased(MouseButtonEvent evt) {
	}

	@Override
	public void onMouseWheelUp(MouseMotionEvent evt) {
		scroller.scrollYBy(scroller.getTrackInc());
		evt.setConsumed();
	}

	@Override
	public void onMouseWheelDown(MouseMotionEvent evt) {
		scroller.scrollYBy(-scroller.getTrackInc());
		evt.setConsumed();
	}

	@Override
	public void setTabFocus() {
	}

	@Override
	public void resetTabFocus() {
	}
}
