package org.iceui.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.iceui.controls.ChooserFieldControl.ChooserPathTranslater;

import icetone.controls.buttons.PushButton;
import icetone.controls.table.Table;
import icetone.controls.table.TableRow;
import icetone.controls.text.TextField;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.layout.Border;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserModel;

public class ItemList<T, C extends ChooserFieldControl<String>> extends Element {
	private C chooser;
	private PushButton deleteItem;
	private PushButton upItem;
	private PushButton downItem;
	private PushButton newItem;
	private TextField input;
	protected Table items;
	protected int row = -1;

	public ItemList(BaseScreen screen, Preferences prefs, Set<String> resources) {
		super(screen);

		chooser = createChooser();
		if (chooser == null) {
			input = new TextField(screen);
		}
		newItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		newItem.onMouseReleased(evt -> {
			row = -1;
			setValue(null);
			if (chooser != null)
				chooser.showChooser(evt.getX(), evt.getY());
			else
				input.focus();
			setAvailable();
		});
		newItem.getButtonIcon().addStyleClass("button-icon icon-new");
		newItem.setToolTipText("Add New Item");

		deleteItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		deleteItem.onMouseReleased(evt -> {
			String sel = items.isAnythingSelected() ? (String) items.getSelectedRow().getValue() : null;
			if (sel != null) {
				List<T> a = getValues();
				a.remove(sel);
				setValues(a);
			}
			onValuesChanged(getValues());
		});
		deleteItem.getButtonIcon().addStyleClass("button-icon icon-trash");
		deleteItem.setToolTipText("Delete Item");

		upItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		upItem.onMouseReleased(evt -> {
			int idx = Math.max(0, items.getSelectedRowIndex() - 1);
			TableRow row = items.getSelectedRow();
			items.removeRow(row);
			items.insertRow(idx, row);
			items.setSelectedRowIndex(idx);
			items.scrollToSelected();
			onValuesChanged(getValues());
		});
		ElementStyle.arrowButton(upItem, Border.NORTH);
		upItem.setToolTipText("Move Item Up");

		downItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		downItem.onMouseReleased(evt -> {
			int idx = items.getSelectedRowIndex() + 1;
			TableRow row = items.getSelectedRow();
			items.removeRow(row);
			items.insertRow(idx, row);
			items.setSelectedRowIndex(idx);
			items.scrollToSelected();
			onValuesChanged(getValues());
		});
		ElementStyle.arrowButton(downItem, Border.SOUTH);
		downItem.setToolTipText("Move Item Up");

		StyledContainer tools = new StyledContainer(screen);
		tools.setLayoutManager(new MigLayout(screen, "wrap 1, ins 0, fill", "[grow]", "[][][][]push"));
		tools.addElement(newItem, "growx");
		tools.addElement(deleteItem, "growx");
		tools.addElement(upItem, "growx");
		tools.addElement(downItem, "growx");

		items = new Table(screen);
		items.onChanged(evt -> {
			TableRow selectedRow = evt.getSource().getSelectedRow();
			setValue(selectedRow == null ? null : (T) selectedRow.getValue());
			row = evt.getSource().getSelectedRowIndex();
			setAvailable();
		});
		configureTable();

		setLayoutManager(new MigLayout(screen, "wrap 2", "[grow, fill][]", "[:32:][:128:]"));
		addElement(chooser == null ? input : chooser, "span 2, growx");
		addElement(items, "growx, growy");
		addElement(tools, "growy, growx");

		setAvailable();
	}

	protected void configureTable() {
		items.setHeadersVisible(false);
		items.addColumn("Sound");
	}

	public void setValue(T value) {
		if (chooser == null) {
			input.setText(value == null ? "" : value.toString());
		} else {
			chooser.setValue(value == null ? "" : value.toString());
		}
	}

	public void setChooserPathTranslater(ChooserPathTranslater<String> chooserPathTranslater) {
		if (chooser != null)
			chooser.setChooserPathTranslater(chooserPathTranslater);
	}

	public void setResources(ChooserModel<String> resources) {
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
			chooser.getChooserButton().setEnabled(items.isAnythingSelected());
		}
		deleteItem.setEnabled(items.isAnythingSelected());
		upItem.setEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() > 0);
		downItem.setEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() < items.getRowCount() - 1);
	}

	public void addChoice(T r) {
		TableRow row = new TableRow(screen, items, r);
		configureRow(r, row);
		items.addRow(row);
	}

	protected void configureRow(T r, TableRow row) {
		row.addCell(getDisplay(r), r);
	}
}