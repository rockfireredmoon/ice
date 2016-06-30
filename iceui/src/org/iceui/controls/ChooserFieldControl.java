package org.iceui.controls;

import java.util.Collection;
import java.util.Set;
import java.util.prefs.Preferences;

import org.icelib.Icelib;
import org.iceui.controls.chooser.ChooserDialog;
import org.iceui.controls.chooser.ChooserPanel;

import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.form.Form;
import icetone.controls.text.TextField;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public abstract class ChooserFieldControl extends Element {
	
	public interface ChooserPathTranslater {
		String getChooserPathForValue(String value);
		String getValueForChooserPath(String chooserPath);
	}

	protected FancyWindow chooser;
	protected Collection<String> resources;
	protected Preferences pref;
	protected String chooserTitle = "Choose Resource";
	protected TextField textField;
	protected Button chooserButton;
	protected String value;
	protected boolean showName;
	protected boolean showChooserButton;
	protected ChooserPathTranslater chooserPathTranslater;

	public ChooserFieldControl(ElementManager screen, String initial, Collection<String> imageResources, Preferences pref) {
		super(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE);
		init(initial, true, true, imageResources, pref);
	}

	public ChooserFieldControl(ElementManager screen, String initial, boolean showName,
			boolean showChooserButton, Collection<String> imageResources, Preferences pref) {
		super(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE);
		init(initial, showName, showChooserButton, imageResources, pref);
	}

	public ChooserFieldControl(ElementManager screen, String UID, String initial, boolean showName, boolean showChooserButton,
			Collection<String> imageResources, Preferences pref) {
		super(screen, UID, LUtil.LAYOUT_SIZE);
		init(initial, showName, showChooserButton, imageResources, pref);
	}

	public ChooserPathTranslater getChooserPathTranslater() {
		return chooserPathTranslater;
	}

	public Element getChooserButton() {
		return chooserButton;
	}

	public void setChooserPathTranslater(ChooserPathTranslater chooserPathTranslater) {
		this.chooserPathTranslater = chooserPathTranslater;
	}

	public void addToForm(Form form) {
		if (textField != null) {
			form.addFormElement(textField);
		}
		if (chooserButton != null) {
			form.addFormElement(chooserButton);
		}
	}
	
	public void setResources(Set<String> resources) {
		this.resources = resources;
	}

	private void init(String initial, boolean showName, boolean showChooserButton, Collection<String> resources, Preferences pref) {

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
			addChild(textField, "growx");
		}

		// Chooser
		if (showChooserButton) {
			createChooserButton();
		}

		// Set controls to initial value
		updateControls();
	}

	protected void createChooserButton() {
		chooserButton = new ButtonAdapter(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				showChooser(evt.getX(), evt.getY());
			}
		};
		chooserButton.setButtonIcon(10, 10, getIconPath());
		addChild(chooserButton, "wrap, growx, growy");

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
			chooser.hideWithEffect();
			screen.removeElement(chooser);
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
		chooser.pack(false);
		chooser.setPosition(winX, screen.getHeight() - y - (int) (chooser.getHeight() / 2));
		screen.addElement(chooser);
		if (chooserButton != null) {
			chooserButton.setIsEnabled(true);
		}
	}

	protected FancyWindow createChooser() {
		ChooserDialog chooser = new ChooserDialog(screen, chooserTitle, resources, pref, createView()) {
			@Override
			public boolean onChosen(String path) {
				if (path != null) {
					setValueFromChooserPath(path);
					updateControls();
					onResourceChosen(value);
				}
				return true;
			}
		};
		if (resources == null) {
			retrieveResources(chooser);
		} else {
			if (value != null) {
				chooser.setSelectedFile(getChooserPathFromValue(), false);
			}
		}
		return chooser;
	}

	protected void retrieveResources(ChooserDialog chooser) {
		if (resources == null) {
			resources = loadResources();
			chooser.setResources(resources);
		}
	}

	protected Collection<String> loadResources() {
		throw new UnsupportedOperationException(
				"No resources were supplied at construction, and loadResources() has not been implemented.");
	}

	protected void setValueFromChooserPath(String path) {
		value = chooserPathTranslater == null ? path : chooserPathTranslater.getValueForChooserPath(path);
	}

	protected String getChooserPathFromValue() {
		return chooserPathTranslater == null ? value : chooserPathTranslater.getChooserPathForValue(value);
	}

	protected void updateControls() {
		if (textField != null) {
			textField.setText(value == null ? "" : Icelib.getFilename(value));
			textField.setCaretPositionToStart();
			textField.setToolTipText(value == null ? null : value);
		}
	}

	protected abstract ChooserPanel.ChooserView createView();

	protected void createLayout() {
		// Configure layout depending on options
		if (showName) {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "ins 0", "[grow][]", "[shrink 0, grow]"));
			} else {
				setLayoutManager(new MigLayout(screen, "ins 0", "[grow]", "[shrink 0, grow]"));
			}
		} else {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "ins 0", "[]", "[shrink 0, grow]"));
			} else {
				throw new IllegalStateException();
			}
		}
	}

	protected abstract String getIconPath();
}
