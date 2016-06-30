package org.iceui.controls;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.form.Form;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public abstract class FancyButtonWindow<T extends Element> extends FancyWindow {

	protected ButtonAdapter btnOk;
	protected Form form;
	protected T contentArea;

	/**
	 * Creates a new instance of the FancyButtonWindow control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public FancyButtonWindow(ElementManager screen, Vector2f position, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle(
				size.toStyleName()).getVector4f("resizeBorders"), screen.getStyle(size.toStyleName()).getString("defaultImg"),
				size, closeable);
	}

	/**
	 * Creates a new instance of the FancyButtonWindow control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public FancyButtonWindow(ElementManager screen, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the FancyButtonWindow control
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
	 *            The default image to use for the AlertBox window
	 */
	public FancyButtonWindow(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, size, closeable);
	}

	/**
	 * Creates a new instance of the FancyButtonWindow control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public FancyButtonWindow(ElementManager screen, String UID, Vector2f position, Size size, boolean closeable) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle(
				size.toStyleName()).getVector4f("resizeBorders"), screen.getStyle(size.toStyleName()).getString("defaultImg"),
				size, closeable);
	}

	/**
	 * Creates a new instance of the FancyButtonWindow control
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
	 */
	public FancyButtonWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UID, position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"), screen.getStyle(
				size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the FancyButtonWindow control
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
	 *            The default image to use for the AlertBox window
	 */
	public FancyButtonWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Size size, boolean closeable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, size, closeable);

		form = new Form(screen);
		float controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		getContentArea().setLayoutManager(new MigLayout(screen, "wrap 1", "[fill, grow]", "[fill, grow][shrink 0]"));

		// Dialog
		contentArea = createContent();
		getContentArea().addChild(contentArea);

		// Button Bar
		Element buttons = new Element(screen, new FlowLayout(4, BitmapFont.Align.Center));
		createButtons(buttons);
		getContentArea().addChild(buttons, "growx");
	}

	/**
	 * Sets the text of the Ok button
	 *
	 * @param text
	 *            String
	 */
	public void setButtonOkText(String text) {
		btnOk.setText(text);
	}

	/**
	 * Sets the tooltip text of the Ok button
	 *
	 * @param text
	 *            String
	 */
	public void setButtonOkTooltipText(String text) {
		btnOk.setToolTipText(text);
	}

	/**
	 * Abstract method for handling Ok button click event
	 *
	 * @param evt
	 *            MouseButtonEvent
	 * @param toggled
	 *            boolean
	 */
	public abstract void onButtonOkPressed(MouseButtonEvent evt, boolean toggled);

	/**
	 * Sets the tooltip text to display when mouse hovers over the Ok button
	 *
	 * @param tip
	 *            String
	 */
	public void setToolTipOkButton(String tip) {
		this.btnOk.setToolTipText(tip);
	}

	protected abstract T createContent();

	protected void createButtons(Element buttons) {
		btnOk = new FancyButton(screen, getUID() + ":btnOk") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				onButtonOkPressed(evt, toggled);
			}
		};
		btnOk.setText("Ok");
		buttons.addChild(btnOk);
		form.addFormElement(btnOk);
	}
}
