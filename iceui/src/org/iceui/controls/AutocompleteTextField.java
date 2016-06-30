package org.iceui.controls;

import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.menuing.Menu;
import icetone.controls.text.TextField;
import icetone.controls.text.TextFieldLayout;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.listeners.KeyboardListener;
import icetone.listeners.TabFocusListener;

public class AutocompleteTextField<V extends Object> extends TextField {

	private AutocompleteSource<V> source;
	private ZMenu popup;

	public static class AutocompleteItem<V extends Object> implements Comparable<AutocompleteItem<V>> {
		private String text;
		private V value;

		public AutocompleteItem(String text, V value) {
			this.text = text;
			this.value = value;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public String getText() {
			return text;
		}

		public V getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			AutocompleteItem<V> other = (AutocompleteItem<V>) obj;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public int compareTo(AutocompleteItem<V> o) {
			return text.compareTo(o.text);
		}
	}

	public static interface AutocompleteSource<V extends Object> {
		List<AutocompleteItem<V>> getItems(String text);
	}

	public AutocompleteTextField(ElementManager screen, AutocompleteSource<V> source) {
		super(screen);
		init(source);
	}

	public AutocompleteTextField(ElementManager screen, String UID, AutocompleteSource<V> source) {
		super(screen, UID);
		init(source);
	}

	public AutocompleteTextField(ElementManager screen, Vector2f dimensions, AutocompleteSource<V> source) {
		super(screen, dimensions);
		init(source);
	}

	public AutocompleteTextField(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			AutocompleteSource<V> source) {
		super(screen, dimensions, resizeBorders, defaultImg);
		init(source);
	}

	public AutocompleteTextField(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			AutocompleteSource<V> source) {
		super(screen, UID, dimensions, resizeBorders, defaultImg);
		init(source);
	}

	protected void onChange(String value) {
		// For sub-classes to be notified of selection (or RETURN press in text
		// field)
	}

	private void init(AutocompleteSource<V> source) {
		this.source = source;
		setLayoutManager(new AutocompleteLayout());
	}

	public void showCompletion() {
		if (popup == null) {
			popup = new ZMenu(screen) {

				@Override
				protected void onItemSelected(ZMenuItem item) {
					AutocompleteTextField.this.setText((String) item.getValue());
					onChange((String) item.getValue());
				}
			};
		}

		popup.removeAllMenuItems();
		final List<AutocompleteItem<V>> items = source.getItems(getText());
		if (!items.isEmpty()) {

			for (AutocompleteItem<V> i : items) {
				popup.addMenuItem(i.getText(), null, i.getValue());
			}

			// float ah = 0;
			// for (String s : items) {
			// aw = Math.max(aw, (popupFont.getLineWidth(s) * scale) +
			// popup.borders.y + popup.borders.z);
			// popup.addMenuItem(s, s, null);
			// ah += popup.getMenuItemHeight() + ( popup.getMenuPadding() * 2 );
			// }
			// ah = Math.min((popup.getMenuItemHeight() + (
			// popup.getMenuPadding() * 2 ) ) * visibleRowCount, ah);
			// popup.setPreferredSize(new Vector2f(aw, ah));
			// popup.setWidth(aw);

			// System.out.println("aw: " + aw + " ah: " + ah);

			// Pack again because we have changed the width
			// popup.pack();

			// popup.getScrollableArea().updateClipping();

			float ay = getAbsoluteY() - popup.getHeight();
			float ax = getAbsoluteX();

			// int rIndex = popup.getMenuItems().size();
			// float diff = rIndex * popup.getMenuItemHeight() +
			// (popup.getMenuPadding() * 2);
			// popup.scrollThumbYTo(
			// (popup.getHeight() - diff));

			if (screen.getElementById(popup.getUID()) == null) {
				screen.addElement(popup);
			}

			popup.showMenu(null, ax, ay);
		} else {
			popup.close();
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
			evt.setConsumed();
			if (popup == null || !popup.getIsVisible()) {
				showCompletion();
				if (!popup.getMenuItems().isEmpty()) {
					// TODO
					// popup.setHighlight(0);
				}
				screen.setTabFocusElement(popup);
				screen.setKeyboardElement(popup);
			}
			return;
		} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
			evt.setConsumed();
			if (popup == null || !popup.getIsVisible()) {
				onChange(getText());
			}
			return;
		} else if (ctrl && evt.getKeyCode() == KeyInput.KEY_SPACE) {
			evt.setConsumed();
			showCompletion();
			return;
		} else if (evt.getKeyChar() != 0) {
			if (popup != null && popup.getIsVisible()) {
				popup.close();
				evt.setConsumed();
				return;
			}

		}
		super.onKeyRelease(evt);
	}

	/**
	 * Overridable hook for receive tab focus event
	 */
	@Override
	public void controlTextFieldSetTabFocusHook() {
		if (popup != null && popup.getIsVisible()) {
			popup.close();
		}
	}

	abstract class AutoCompletePopup extends Menu<String> implements KeyboardListener, TabFocusListener {

		public AutoCompletePopup(ElementManager screen, String uid) {
			super(screen, uid, new Vector2f(0, 0), true);
		}

		public void onKeyPress(KeyInputEvent evt) {
		}

		@Override
		public void setTabFocus() {
			// screen.setKeyboardElement(this);
		}

		@Override
		public void resetTabFocus() {
			// screen.setKeyboardElement(null);
		}

		// Interaction
		@Override
		public void onKeyRelease(KeyInputEvent evt) {
			System.err.println(evt);
			if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				evt.setConsumed();
				if (currentHighlightIndex < getMenuItems().size() - 1) {
					setSelectedIndex(currentHighlightIndex + 1);
					final float scrolledAmount = getScrolledAmount();
					final float viewPortHeight = getHeight();
					final float maxY = scrolledAmount + viewPortHeight;
					final float rowY = getHighlight().getY();
					final float rowBottom = rowY + getHighlight().getHeight();
					if (rowBottom >= maxY) {
						scrollYBy(rowBottom - maxY);
//						setScrollThumb();
					} else if (rowY < scrolledAmount) {
						scrollYBy(rowY - scrolledAmount);
//						setScrollThumb();
					}
				}
			} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
				evt.setConsumed();
				if (currentHighlightIndex > 0) {
					setSelectedIndex(currentHighlightIndex - 1);
				}
			} else if (evt.getKeyCode() == KeyInput.KEY_RETURN && popup != null && popup.getIsVisible()) {
				AutocompleteTextField.this.setText((String) getMenuItem(currentHighlightIndex).getValue());
			}
		}

		private float getScrolledAmount() {
			return getScrollableArea().getY();
		}

//		private void setScrollThumb() {
//			/*
//			 * All this is to update the scroll thumb to the current scroll
//			 * position. Im sure something like this would be better
//			 */
//			final float trackLength = getVScrollBar().getScrollTrack().getHeight();
//			float scale = (getScrollableHeight()) / (trackLength);
//			float diff = getScrolledAmount();
//			float thumbHeight = trackLength - getVScrollBar().getScrollThumb().getHeight();
//			final int y = (int) (diff / scale);
//			getVScrollBar().getScrollThumb().setY(thumbHeight - y);
//		}
	}

	public class AutocompleteLayout extends TextFieldLayout {

		@Override
		public void layout(Element childElement) {
			super.layout(childElement);
			if (popup != null) {
				popup.pack();
			}
		}

	}
}
