package org.iceui.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollPanel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.layout.WrappingLayout;
import icetone.core.utils.UIDUtil;
import icetone.listeners.KeyboardListener;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.TabFocusListener;

/**
 *
 * @author t0neg0d
 */
public class SelectArea extends ScrollPanel implements MouseButtonListener, TabFocusListener, KeyboardListener {
	private List<SelectableItem> listItems = new ArrayList<>();
	private List<Integer> selectedIndexes = new ArrayList<>();
	private boolean isMultiselect = false;
	private boolean shift = false, ctrl = false;

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public SelectArea() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public SelectArea(ElementManager screen) {
		this(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, screen.getStyle("SelectArea").getVector4f("resizeBorders"),
				screen.getStyle("SelectArea").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public SelectArea(ElementManager screen, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), dimensions, screen.getStyle("SelectArea").getVector4f("resizeBorders"),
				screen.getStyle("SelectArea").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Menu
	 */
	public SelectArea(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 */
	public SelectArea(ElementManager screen, String UID) {
		this(screen, UID, LUtil.LAYOUT_SIZE, screen.getStyle("SelectArea").getVector4f("resizeBorders"),
				screen.getStyle("SelectArea").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public SelectArea(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, dimensions, screen.getStyle("SelectArea").getVector4f("resizeBorders"),
				screen.getStyle("SelectArea").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the SelectList control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 */
	public SelectArea(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
		setTextPadding(screen.getStyle("SelectArea").getVector4f("textPadding"));
		setTextClipPadding(screen.getStyle("SelectArea").getVector4f("textPadding"));
		scrollableArea.setTextPadding(screen.getStyle("SelectArea").getVector4f("scrollPadding"));
		scrollableArea.setTextClipPadding(screen.getStyle("SelectArea").getVector4f("scrollPadding"));
		setHorizontalScrollBarMode(ScrollBarMode.Never);
		((WrappingLayout) getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		((WrappingLayout) getScrollContentLayout()).setEqualSizeCells(false);
		((WrappingLayout) getScrollContentLayout()).setWidth(1);
		((WrappingLayout) getScrollContentLayout()).setFill(true);
		getScrollBounds().setIgnoreMouseButtons(true);
		getScrollableArea().setIgnoreMouse(true);
	}

	public SelectableItem getSelectedItem() {
		if (selectedIndexes.isEmpty()) {
			return null;
		}
		return listItems.get(selectedIndexes.get(0));
	}

	public boolean isAnySelected() {
		return !selectedIndexes.isEmpty();
	}

	public void setIsMultiselect(boolean isMultiselect) {
		this.isMultiselect = isMultiselect;
	}

	public boolean getIsMultiselect() {
		return this.isMultiselect;
	}

	/**
	 * Adds a ListItem to the Menu
	 *
	 * @param caption
	 *            The display caption of the MenuItem
	 * @param value
	 *            The value to associate with the MenuItem
	 * 
	 * @deprecated use {@link #addScrollableContent(Element)} and variants.
	 */
	@Deprecated
	public void addListItem(SelectableItem listItem) {
		addScrollableContent(listItem);
	}

	@Override
	public void addScrollableContent(Element el, boolean reshape, Object constraints) {
		if (el instanceof SelectableItem)
			this.listItems.add((SelectableItem) el);
		super.addScrollableContent(el, reshape, constraints);
	}

	/**
	 * Inserts a new ListItem at the provided index
	 *
	 * @param index
	 *            The index to insert into
	 * @param caption
	 *            The display caption of the MenuItem
	 * @param value
	 *            The value to associate with the MenuItem
	 */
	public void insertListItem(int index, SelectableItem listItem) {
		if (!listItems.isEmpty()) {
			if (index >= 0 && index < listItems.size()) {
				this.listItems.add(index, listItem);
				insertScrollableContent(listItem, index);
			}
		}
	}

	/**
	 * Remove the ListItem at the provided index
	 *
	 * @param index
	 *            int
	 */
	public void removeListItem(int index) {
		if (!listItems.isEmpty()) {
			if (index >= 0 && index < listItems.size()) {
				listItems.remove(index).removeFromParent();
				layoutChildren();
			}
		}
	}

	/**
	 * Remove the first ListItem that contains the provided value
	 *
	 * @param value
	 *            Object
	 */
	public void removeListItem(SelectableItem value) {
		if (!listItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (Element mi : listItems) {
				if (mi == value) {
					index = count;
					break;
				}
				count++;
			}
			removeListItem(index);
		}
	}

	/**
	 * Removes the first ListItem in the SelectList
	 */
	public void removeFirstListItem() {
		removeListItem(0);
	}

	/**
	 * Removes the last ListItem in the SelectList
	 */
	public void removeLastListItem() {
		if (!listItems.isEmpty()) {
			removeListItem(listItems.size() - 1);
		}
	}

	public void removeAllListItems() {
		for (SelectableItem item : listItems) {
			getScrollableArea().removeChild(item, false);
		}
		layoutChildren();
		this.listItems = new ArrayList<>();
		this.selectedIndexes = new ArrayList<>();
	}

	/**
	 * Sets the current selected index for single select SelectLists
	 *
	 * @param index
	 *            int
	 */
	public void setSelectedIndex(Integer index) {
		for (int i : selectedIndexes) {
			listItems.get(i).setSelected(false);
		}
		selectedIndexes = new ArrayList<>();
		selectedIndexes.add(index);
		listItems.get(index).setSelected(true);
		onChange();
	}

	/**
	 * Sets the current list of selected indexes to the specified indexes
	 *
	 * @param indexes
	 */
	public void setSelectedIndexes(Integer... indexes) {
		List<Integer> selectionList = Arrays.asList(indexes);
		for (int i : new ArrayList<Integer>(selectedIndexes)) {
			if (!selectionList.contains(i)) {
				listItems.get(i).setSelected(false);
				selectedIndexes.remove(i);
			}
		}
		for (int i = 0; i < indexes.length; i++) {
			if (!selectedIndexes.contains(indexes[i])) {
				selectedIndexes.add(indexes[i]);
				listItems.get(i).setSelected(true);
			}
		}
		onChange();
	}

	/**
	 * Clear the selection
	 */
	public void clearSelection() {
		if (selectedIndexes.size() > 0) {
			for (int i : selectedIndexes) {
				listItems.get(i).setSelected(false);
			}
			selectedIndexes.clear();
			onChange();
		}
	}

	/**
	 * Adds the specified index to the list of selected indexes
	 *
	 * @param index
	 *            int
	 */
	public void addSelectedIndex(Integer index) {
		if (!selectedIndexes.contains(index)) {
			listItems.get(index).setSelected(true);
			selectedIndexes.add(index);
			onChange();
		}
	}

	/**
	 * Removes the specified index from the list of selected indexes
	 *
	 * @param index
	 *            int
	 */
	public void removeSelectedIndex(Integer index) {
		if (selectedIndexes.contains(index)) {
			listItems.get(index).setSelected(false);
			selectedIndexes.remove(index);
			onChange();
		}
	}

	/**
	 * Returns the first (or only) index in the list of selected indexes
	 *
	 * @return int
	 */
	public int getSelectedIndex() {
		if (selectedIndexes.isEmpty()) {
			return -1;
		} else {
			return selectedIndexes.get(0);
		}
	}

	/**
	 * Returns the entire list of selected indexes
	 *
	 * @return List<Integer>
	 */
	public List<Integer> getSelectedIndexes() {
		return this.selectedIndexes;
	}

	/**
	 * Returns the ListItem at the specified index
	 *
	 * @param index
	 *            int
	 * @return ListItem
	 */
	public SelectableItem getListItem(int index) {
		if (!listItems.isEmpty()) {
			if (index >= 0 && index < listItems.size()) {
				return listItems.get(index);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns a List containing all ListItems corresponding to the list of
	 * selectedIndexes
	 *
	 * @return List<ListItem>
	 */
	public List<SelectableItem> getSelectedListItems() {
		List<SelectableItem> ret = new ArrayList<>();
		for (Integer i : selectedIndexes) {
			ret.add(getListItem(i));
		}
		return ret;
	}

	public List<SelectableItem> getListItems() {
		return this.listItems;
	}

	private int yToIndex(float y) {
		System.out.println("y to i " + y);
		for (int i = 0; i < listItems.size(); i++) {
			SelectableItem item = listItems.get(i);
			float it = LUtil.getY(item);
			if (y >= it && y <= it + item.getHeight()) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		evt.setConsumed();
	}

	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		float y = evt.getY() - LUtil.getAbsoluteY(scrollableArea) + textPadding.z;
		int idx = yToIndex(y);
		if (isMultiselect) {
			if (shift || ctrl) {
				if (!selectedIndexes.contains(idx)) {
					addSelectedIndex(idx);
				} else {
					removeSelectedIndex(idx);
				}
			} else {
				setSelectedIndex(idx);
			}
		} else {
			if (idx >= 0 && idx < listItems.size()) {
				setSelectedIndex(idx);
			} else {
				clearSelection();
			}
		}
		evt.setConsumed();
	}

	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {

		evt.setConsumed();
	}

	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {
		float y = evt.getY() - LUtil.getAbsoluteY(scrollableArea) + textPadding.z;
		int idx = yToIndex(y);
		if (isMultiselect && idx > -1) {
			if (shift || ctrl) {
				if (!selectedIndexes.contains(idx)) {
					addSelectedIndex(idx);
					onRightClickSelection(evt);
				} else {
					removeSelectedIndex(idx);
				}
			} else {
				setSelectedIndex(idx);
				onRightClickSelection(evt);
			}
		} else {
			if (idx >= 0 && idx < listItems.size()) {
				setSelectedIndex(idx);
				onRightClickSelection(evt);
			} else {
				clearSelection();
			}
		}
		evt.setConsumed();
	}

	@Override
	public void onKeyPress(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = true;
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = false;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = false;
		}
	}

	@Override
	public void setTabFocus() {
		screen.setKeyboardElement(this);
	}

	@Override
	public void resetTabFocus() {
		screen.setKeyboardElement(null);
	}

	public void onChange() {
	}

	protected void onRightClickSelection(MouseButtonEvent evt) {
	}
}
