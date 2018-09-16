package org.iceui.controls;

import java.util.prefs.Preferences;

import org.icelib.Icelib;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserDialog;
import icetone.extras.chooser.ChooserModel;
import icetone.extras.chooser.ChooserPanel;
import icetone.fontawesome.FontAwesome;

public abstract class ChooserFieldControl<I> extends Element {

	public interface ChooserPathTranslater<I> {
		I getChooserPathForValue(String value);

		String getValueForChooserPath(I chooserPath);
	}

	protected Frame chooser;
	protected ChooserModel<I> resources;
	protected Preferences pref;
	protected String chooserTitle = "Choose Resource";
	protected TextField textField;
	protected PushButton chooserButton;
	protected String value;
	protected boolean showName;
	protected boolean showChooserButton;
	protected ChooserPathTranslater<I> chooserPathTranslater;
	protected boolean chooserModal = true;

	public ChooserFieldControl(BaseScreen screen, String initial, ChooserModel<I> imageResources,
			Preferences pref) {
		super(screen);
		init(initial, true, true, imageResources, pref);
	}

	public ChooserFieldControl(BaseScreen screen, String initial, boolean showName, boolean showChooserButton,
			ChooserModel<I> imageResources, Preferences pref) {
		super(screen);
		init(initial, showName, showChooserButton, imageResources, pref);
	}

	public ChooserFieldControl(BaseScreen screen, String styleId, String initial, boolean showName,
			boolean showChooserButton, ChooserModel<I> imageResources, Preferences pref) {
		super(screen, styleId);
		init(initial, showName, showChooserButton, imageResources, pref);
	}

	public boolean isDialogModal() {
		return chooserModal;
	}

	public ChooserFieldControl<I> setModal(boolean chooserModal) {
		this.chooserModal = chooserModal;
		return this;
	}

	public ChooserPathTranslater<I> getChooserPathTranslater() {
		return chooserPathTranslater;
	}

	public BaseElement getChooserButton() {
		return chooserButton;
	}

	public ChooserFieldControl<I> setChooserPathTranslater(ChooserPathTranslater<I> chooserPathTranslater) {
		this.chooserPathTranslater = chooserPathTranslater;
		return this;
	}

	public ChooserFieldControl<I> addToForm(Form form) {
		if (textField != null) {
			form.addFormElement(textField);
		}
		if (chooserButton != null) {
			form.addFormElement(chooserButton);
		}
		return this;
	}

	public ChooserFieldControl<I> setResources(ChooserModel<I> resources) {
		this.resources = resources;
		return this;
	}

	private void init(String initial, boolean showName, boolean showChooserButton, ChooserModel<I> resources,
			Preferences pref) {

		value = initial;

		this.showName = showName;
		this.showChooserButton = showChooserButton;
		this.pref = pref;
		this.resources = resources;

		createLayout();

		// Text field
		if (showName) {
			textField = new TextField(screen);
			textField.setEditable(false);
			addElement(textField, "growx");
		}

		// Chooser
		if (showChooserButton) {
			createChooserButton();
		}

		// Set controls to initial value
		updateControls();
	}

	protected void createChooserButton() {
		chooserButton = new PushButton(screen) {
			{
				setStyleClass("chooser-button");
			}
		};
		FontAwesome.SEARCH.button(16, chooserButton);
		chooserButton.onMouseReleased(evt -> showChooser(evt.getX(), evt.getY()));
		addElement(chooserButton, "wrap, growx, growy");

	}

	public void setValue(String value) {
		this.value = value;
		updateControls();
	}

	public void setValueWithCallback(String value) {
		setValue(value);
		onResourceChosen(value);
	}

	public String getValue() {
		return value;
	}

	public String getChooserTitle() {
		return chooserTitle;
	}

	public void setChooserTitle(String chooserTitle) {
		this.chooserTitle = chooserTitle;
	}

	protected abstract void onResourceChosen(String newResource);

	protected void onBeforeShowChooser() {
	}

	public void hideChooser() {
		if (chooser != null) {
			chooser.destroy();
			chooser = null;
		}
	}

	public void showChooser(float x, float y) {
		onBeforeShowChooser();
		chooser = createChooser();
		float winX = x + 20;
		if (winX + chooser.getWidth() > screen.getWidth()) {
			winX = x - chooser.getWidth() - 20;
		}
		chooser.sizeToContent();
		chooser.setPosition(winX, screen.getHeight() - y - (int) (chooser.getHeight() / 2));
		screen.showElement(chooser);
		if (chooserButton != null) {
			chooserButton.setEnabled(true);
		}
	}

	protected Frame createChooser() {
		ChooserDialog<I> chooser = new ChooserDialog<I>(screen, getStyleId() + "-chooser", chooserTitle, resources,
				pref, createView());
		chooser.onChange(evt -> {
			if (evt.getNewValue() != null && !evt.isTemporary()) {
				setValueFromChooserPath(evt.getNewValue());
				updateControls();
				onResourceChosen(value);
				if(!evt.isTemporary())
					chooser.hide();
			}
		});
		chooser.setModal(chooserModal);
		if (resources == null) {
			retrieveResources(chooser);
		} else {
			if (value != null) {
				chooser.setSelectedFile(getChooserPathFromValue());
			}
		}
		return chooser;
	}

	protected void retrieveResources(ChooserDialog<I> chooser) {
		if (resources == null) {
			resources = loadResources();
			chooser.setResources(resources);
		}
	}

	protected ChooserModel<I> loadResources() {
		throw new UnsupportedOperationException(
				"No resources were supplied at construction, and loadResources() has not been implemented.");
	}

	protected void setValueFromChooserPath(I path) {
		value = chooserPathTranslater == null ? path.toString() : chooserPathTranslater.getValueForChooserPath(path);
	}

	protected I getChooserPathFromValue() {
		return chooserPathTranslater == null ? resources.parse(value)
				: chooserPathTranslater.getChooserPathForValue(value);
	}

	protected void updateControls() {
		if (textField != null) {
			textField.setText(value == null ? "" : Icelib.getFilename(value));
			textField.setCaretPositionToStart();
			textField.setToolTipText(value == null ? null : value);
		}
	}

	protected abstract ChooserPanel.ChooserView<I> createView();

	protected void createLayout() {
		// Configure layout depending on options
		if (showName) {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "ins 0, gap 0", "[grow]1[]", "[shrink 0, grow]"));
			} else {
				setLayoutManager(new MigLayout(screen, "ins 0, gap 0", "[grow]", "[shrink 0, grow]"));
			}
		} else {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "ins 0, gap 0", "[]", "[shrink 0, grow]"));
			} else {
				throw new IllegalStateException();
			}
		}
	}
}
