/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package org.iceui.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.buttons.SelectableItem;
import icetone.controls.scrolling.ScrollPanel;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.BaseScreen;
import icetone.core.layout.WrappingLayout;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class SelectArea extends ScrollPanel {
	private List<SelectableItem> listItems = new ArrayList<>();
	private List<Integer> selectedIndexes = new ArrayList<>();
	private boolean isMultiselect = false;
	private boolean shift = false, ctrl = false;

	/**
	 * Creates a new instance of the SelectArea control
	 */
	public SelectArea() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the SelectArea control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public SelectArea(BaseScreen screen) {
		super(screen);

		((WrappingLayout) getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		((WrappingLayout) getScrollContentLayout()).setEqualSizeCells(false);
		((WrappingLayout) getScrollContentLayout()).setWidth(1);
		((WrappingLayout) getScrollContentLayout()).setFill(true);

		onMouseReleased(evt -> {
			float y = evt.getY() - scrollableArea.getAbsoluteY() + getAllPadding().z;
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
		});

		onMouseReleased(evt -> {
			float y = evt.getY() - scrollableArea.getAbsoluteY() + getAllPadding().z;
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
		}, MouseUIButtonEvent.RIGHT);

		onKeyboardReleased(evt -> {

			if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
				ctrl = false;
			} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
				shift = false;
			}
		});
		onKeyboardPressed(evt -> {

			if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
				ctrl = true;
			} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
				shift = true;
			}
		});

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

	@Override
	public ScrollPanel addScrollableContent(BaseElement el, Object constraints) {
		if (el instanceof SelectableItem)
			this.listItems.add((SelectableItem) el);
		return super.addScrollableContent(el, constraints);
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
			for (BaseElement mi : listItems) {
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
		getScrollableArea().invalidate();
		for (SelectableItem item : listItems) {
			getScrollableArea().removeElement(item);
		}
		getScrollableArea().validate();
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
			listItems.get(i).setIsToggled(false);
		}
		selectedIndexes = new ArrayList<>();
		selectedIndexes.add(index);
		listItems.get(index).setIsToggled(true);
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
				listItems.get(i).setIsToggled(false);
				selectedIndexes.remove(i);
			}
		}
		for (int i = 0; i < indexes.length; i++) {
			if (!selectedIndexes.contains(indexes[i])) {
				selectedIndexes.add(indexes[i]);
				listItems.get(i).setIsToggled(true);
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
				listItems.get(i).setIsToggled(false);
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
			listItems.get(index).setIsToggled(true);
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
			listItems.get(index).setIsToggled(false);
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
		for (int i = 0; i < listItems.size(); i++) {
			SelectableItem item = listItems.get(i);
			float it = item.getY();
			if (y >= it && y <= it + item.getHeight()) {
				return i;
			}
		}
		return -1;
	}

	public void onChange() {
	}

	protected void onRightClickSelection(MouseButtonEvent evt) {
	}
}
