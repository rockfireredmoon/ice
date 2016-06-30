package org.iceui.controls;

import org.iceui.UIConstants;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.scrolling.ScrollArea;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.utils.UIDUtil;

public abstract class FancyAlertBox extends FancyButtonWindow<ScrollArea> {

	private ButtonAdapter btnOk;

	public enum AlertType {

		ERROR, INFORMATION, PROGRESS
	}

	public static FancyAlertBox alert(ElementManager screen, String title, String text, final AlertType alert) {

		final FancyAlertBox dialog = new FancyAlertBox(screen, new Vector2f(15, 15), FancyWindow.Size.LARGE, true) {
			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
				hideWindow();
			}

			@Override
			public void createButtons(Element buttons) {
				if (!alert.equals(AlertType.PROGRESS)) {
					super.createButtons(buttons);
				}
			}
		};
		dialog.setDestroyOnHide(true);
		dialog.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("errorColor"));
		dialog.setWindowTitle(title);
		if (!alert.equals(AlertType.PROGRESS)) {
			dialog.setButtonOkText("Close");
		}
		dialog.setMsg(text);
		dialog.setIsResizable(false);
		dialog.setIsMovable(false);
		if (screen.getUseUIAudio()) {
			switch (alert) {
			case ERROR:
				((Screen) screen).playAudioNode(UIConstants.SOUND_WARNING, 1);
				break;
			}
		}
		dialog.sizeToContent();
		UIUtil.center(screen, dialog);
		screen.addElement(dialog, null, true);
		dialog.showAsModal(true);
		return dialog;
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public FancyAlertBox(ElementManager screen, Vector2f position, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, screen.getStyle(size.toStyleName()).getVector2f("defaultSize"),
				screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public FancyAlertBox(ElementManager screen, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
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
	public FancyAlertBox(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public FancyAlertBox(ElementManager screen, String UID, Vector2f position, Size size, boolean closeable) {
		this(screen, UID, position, screen.getStyle(size.toStyleName()).getVector2f("defaultSize"),
				screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
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
	public FancyAlertBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UID, position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
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
	public FancyAlertBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Size size, boolean closeable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, size, closeable);
	}

	@Override
	protected ScrollArea createContent() {
		ScrollArea dlg = new ScrollArea(screen, true);

		dlg.setIsResizable(false);
		dlg.setScaleEW(true);
		dlg.setScaleNS(true);
		dlg.setClippingLayer(dlg);
		dlg.setTextAlign(BitmapFont.Align.Center);
		dlg.setTextVAlign(BitmapFont.VAlign.Center);
		return dlg;
	}

	/**
	 * Sets the message to display in the AlertBox
	 *
	 * @param text
	 *            String The message
	 */
	public void setMsg(String text) {
		contentArea.setText(text);
	}

	/**
	 * Returns the ScrollArea containing the window message text.
	 *
	 * @return
	 */
	public ScrollArea getTextArea() {
		return this.contentArea;
	}
}
