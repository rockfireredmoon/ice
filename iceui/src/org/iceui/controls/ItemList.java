package org.iceui.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.iceui.controls.ChooserFieldControl.ChooserPathTranslater;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.lists.Table;
import icetone.controls.lists.Table.TableRow;
import icetone.controls.text.TextField;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;

public class ItemList<T, C extends ChooserFieldControl> extends Element {
	private C chooser;
	private FancyButton deleteItem;
	private FancyButton upItem;
	private FancyButton downItem;
	private FancyButton newItem;
	private TextField input;
	protected Table items;
	protected int row = -1;

	public ItemList(ElementManager screen, Preferences prefs, Set<String> resources) {
		super(screen);

		chooser = createChooser();
		if (chooser == null) {
			input = new TextField(screen);
		}
		newItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				row = -1;
				setValue(null);
				if (chooser != null)
					chooser.showChooser(evt.getX(), evt.getY());
				else
					input.setTabFocus();
				setAvailable();
			}
		};
		newItem.getMinDimensions().x = 64;
		newItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Icons/new.png");
		newItem.setToolTipText("Add New Item");

		deleteItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				String sel = items.isAnythingSelected() ? (String) items.getSelectedRow().getValue() : null;
				if (sel != null) {
					List<T> a = getValues();
					a.remove(sel);
					setValues(a);
				}
				onValuesChanged(getValues());
			}
		};
		deleteItem.getMinDimensions().x = 64;
		deleteItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Icons/trash.png");
		deleteItem.setToolTipText("Delete Item");

		upItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				int idx = Math.max(0, items.getSelectedRowIndex() - 1);
				TableRow row = items.getSelectedRow();
				items.removeRow(row);
				items.insertRow(idx, row);
				items.setSelectedRowIndex(idx);
				items.scrollToSelected();
				onValuesChanged(getValues());
			}
		};
		upItem.getMinDimensions().x = 64;
		upItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Arrows/arrow_up.png");
		upItem.setToolTipText("Move Item Up");

		downItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				int idx = items.getSelectedRowIndex() + 1;
				TableRow row = items.getSelectedRow();
				items.removeRow(row);
				items.insertRow(idx, row);
				items.setSelectedRowIndex(idx);
				items.scrollToSelected();
				onValuesChanged(getValues());
			}
		};
		downItem.getMinDimensions().x = 64;
		downItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Arrows/arrow_down.png");
		downItem.setToolTipText("Move Item Up");

		Container tools = new Container(screen);
		tools.setLayoutManager(new MigLayout(screen, "wrap 1, ins 0, fill", "[grow]", "[][][][]push"));
		tools.addChild(newItem, "growx");
		tools.addChild(deleteItem, "growx");
		tools.addChild(upItem, "growx");
		tools.addChild(downItem, "growx");

		items = new Table(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg")) {
			@SuppressWarnings("unchecked")
			@Override
			public void onChange() {
				TableRow selectedRow = getSelectedRow();
				setValue(selectedRow == null ? null : (T) selectedRow.getValue());
				row = getSelectedRowIndex();
				setAvailable();
			}
		};
		items.setHeadersVisible(false);
		items.addColumn("Sound");

		setLayoutManager(new MigLayout(screen, "wrap 2", "[grow, fill][]", "[:32:][:128:]"));
		addChild(chooser == null ? input : chooser, "span 2, growx");
		addChild(items, "growx, growy");
		addChild(tools, "growy, growx");

		setAvailable();
	}

	public void setValue(T value) {
		if (chooser == null) {
			input.setText(value == null ? "" : value.toString());
		} else {
			chooser.setValue(value == null ? "" : value.toString());
		}
	}

	public void setChooserPathTranslater(ChooserPathTranslater chooserPathTranslater) {
		if (chooser != null)
			chooser.setChooserPathTranslater(chooserPathTranslater);
	}

	public void setResources(Set<String> resources) {
		if (chooser != null)
			chooser.setResources(resources);
	}

	public void setValues(List<T> audio) {
		items.removeAllRows();
		for (T r : audio) {
			addChoice(r);
		}
		items.scrollToTop();
	}

	@SuppressWarnings("unchecked")
	public List<T> getValues() {
		List<T> a = new ArrayList<>();
		for (TableRow r : items.getRows()) {
			a.add((T) r.getValue());
		}
		return a;
	}

	protected C createChooser() {
		return null;
	}

	protected void onValuesChanged(List<T> values) {
	}

	protected String getDisplay(T value) {
		return value == null ? "" : String.valueOf(value);
	}

	public void setAvailable() {
		if (chooser != null) {
			chooser.getChooserButton().setIsEnabled(items.isAnythingSelected());
		}
		deleteItem.setIsEnabled(items.isAnythingSelected());
		upItem.setIsEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() > 0);
		downItem.setIsEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() < items.getRowCount() - 1);
	}

	public void addChoice(T r) {
		TableRow row = new TableRow(screen, items, r);
		row.addCell(getDisplay(r), r);
		items.addRow(row);
	}
}