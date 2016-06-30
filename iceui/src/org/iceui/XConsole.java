/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iceui;

import java.util.ArrayList;
import java.util.List;

import org.iceui.controls.FancyButton;
import org.iceui.controls.UIUtil;

import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.form.Form;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.style.Style;

/**
 *
 * @author t0neg0d
 */
public abstract class XConsole extends Panel {

	public static final String TYPE_STANDARD = "Standard";
	public static final String TYPE_ERROR = "Error";
	private ScrollPanel saConsoleArea;
	private TextField tfChatInput;
	private ButtonAdapter btnCommandExecute;
	private Form chatForm;
	private int sendKey;
	private int outputHistorySize = 30;
	private List<Label> displayMessages = new ArrayList<>();
	private boolean fontSizeSet;
	protected List<OutputMessage> outputMessages = new ArrayList<>();
	private List<String> commandHistory = new ArrayList<String>();
	private String textBeforeSearch;
	private int currentHistoryIndex;
	private boolean hideOnLoseFocus;

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public XConsole(ElementManager screen) {
		this(screen, UIDUtil.getUID(), screen.getStyle("Console").getFloat("defaultHeight"));
	}

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param float
	 *            the number of pixels to drop down from the top of the screen
	 */
	public XConsole(ElementManager screen, float height) {
		this(screen, UIDUtil.getUID(), new Vector2f(screen.getWidth(), height),
				screen.getStyle("Console").getVector4f("resizeBorders"), screen.getStyle("Console").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param float
	 *            the number of pixels to drop down from the top of the screen
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 */
	public XConsole(ElementManager screen, float height, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), new Vector2f(screen.getWidth(), height), resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 */
	public XConsole(ElementManager screen, String UID) {
		this(screen, UID, new Vector2f(screen.getWidth(), screen.getStyle("Console").getFloat("defaultHeight")),
				screen.getStyle("Console").getVector4f("resizeBorders"), screen.getStyle("Console").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param float
	 *            the number of pixels to drop down from the top of the screen
	 */
	public XConsole(ElementManager screen, String UID, float height) {
		this(screen, UID, new Vector2f(screen.getWidth(), screen.getStyle("Console").getFloat("defaultHeight")),
				screen.getStyle("Console").getVector4f("resizeBorders"), screen.getStyle("Console").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param float
	 *            the number of pixels to drop down from the top of the screen
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 */
	public XConsole(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, new Vector2f(0, 0), dimensions, resizeBorders, defaultImg);
		UIUtil.cleanUpWindow(this);

		setIsMovable(false);
		setIsResizable(true);

		chatForm = new Form(screen);

		saConsoleArea = new ScrollPanel(screen, UID + ":ChatArea", Vector2f.ZERO, LUtil.LAYOUT_SIZE);
		final WrappingLayout wrapLayout = (WrappingLayout) saConsoleArea.getScrollContentLayout();
		saConsoleArea.setHorizontalScrollBarMode(ScrollBarMode.Never);
		wrapLayout.setOrientation(Orientation.HORIZONTAL);
		wrapLayout.setWidth(1);
//		wrapLayout.setFill(true);

		// saConsoleArea = new LScrollArea(screen, UID + ":ChatArea", false) {
		// @Override
		// public void controlResizeHook() {
		// float totalHeight = 0;
		//
		// int index = 0;
		// for (Label l : displayMessages) {
		// l.setHeight(l.getTextElement().getHeight());
		// totalHeight += l.getHeight();
		// index++;
		// }
		// if (totalHeight > saConsoleArea.getHeight()) {
		// saConsoleArea.getScrollableArea().setHeight(totalHeight +
		// (saConsoleArea.getPadding() * 2));
		// }
		// totalHeight = 0;
		// for (Label l : displayMessages) {
		// totalHeight += l.getHeight();
		// l.setX(saContentPadding);
		// l.setWidth(saConsoleArea.getWidth() - (saContentPadding * 2));
		// l.setY(saConsoleArea.getScrollableArea().getHeight() - totalHeight);
		// }
		// if (getVScrollBar() != null) {
		// getVScrollBar().setThumbScale();
		// }
		// adjustWidthForScroll();
		// }
		// };
		// final FlowLayout flowLayout = new FlowLayout(0,
		// BitmapFont.VAlign.Top);
		// flowLayout.setFill(true);
		// saConsoleArea.setScrollAreaLayout(flowLayout);
		saConsoleArea.setIsResizable(false);

		// saConsoleArea.setClippingLayer(saConsoleArea);
		// saConsoleArea.getScrollableArea().setIgnoreMouse(true);
		// saConsoleArea.setPadding(2);
		// saConsoleArea.setText("");

		// Input
		tfChatInput = new TextField(screen, UID + ":ChatInput") {
			@Override
			public void onLoseFocus(MouseMotionEvent evt) {
				super.onLoseFocus(evt);
				if (hideOnLoseFocus) {
					XConsole.this.hideWithEffect();
				}
			}

			@Override
			public void controlKeyPressHook(KeyInputEvent evt, String text) {
				if (evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
					onEscape();
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
					previousHistory();
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
					nextHistory();
					evt.setConsumed();
				} else if (evt.getKeyCode() == sendKey) {
					if (tfChatInput.getText().length() > 0) {
						// tfChatInput.setText(tfChatInput.getText().substring(0,tfChatInput.getText().length()-1));
						sendMsg();
					}
				}
			}
		};
		chatForm.addFormElement(tfChatInput);

		// Execute
		btnCommandExecute = new FancyButton(screen, UID + ":ChatSendMsg") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				sendMsg();
			}
		};
		btnCommandExecute.setText("Execute");
		chatForm.addFormElement(btnCommandExecute);

		// This
		setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[grow][]", "[grow][shrink 0]"));
		addChild(saConsoleArea, "span 2, wrap, growx, growy");
		addChild(tfChatInput, "growx");
		addChild(btnCommandExecute);
		setResizeE(false);
		setResizeW(false);

		LUtil.removeEffects(this);
		populateEffects("Console");
	}

	/**
	 * Get whether the console should be hidden which focus is lost from the
	 * text field.
	 * Default value
	 * <code>false</code>.
	 *
	 * @return hide on lose focus
	 */
	public boolean isHideOnLoseFocus() {
		return hideOnLoseFocus;
	}

	/**
	 * GSt whether the console should be hidden which focus is lost from the
	 * text field.
	 * Default value
	 * <code>false</code>.
	 *
	 * @param hideOnLoseFocus
	 *            hide on lose focus
	 */
	public void setHideOnLoseFocus(boolean hideOnLoseFocus) {
		this.hideOnLoseFocus = hideOnLoseFocus;
	}

	public void previousHistory() {
		if (commandHistory.size() > 0) {
			if (textBeforeSearch == null) {
				textBeforeSearch = tfChatInput.getText();
				currentHistoryIndex = commandHistory.size();
			}
			currentHistoryIndex--;
			if (currentHistoryIndex < 0) {
				currentHistoryIndex = 0;
			}
			tfChatInput.setText(commandHistory.get(currentHistoryIndex));
		}
	}

	public void nextHistory() {
		if (commandHistory.size() > 0) {
			if (textBeforeSearch != null) {
				currentHistoryIndex++;
				if (currentHistoryIndex >= commandHistory.size()) {
					// Scrolled back to originally typed type
					tfChatInput.setText(textBeforeSearch);
					textBeforeSearch = null;
					currentHistoryIndex = -1;
				} else {
					tfChatInput.setText(commandHistory.get(currentHistoryIndex));
				}
			}
		}
	}

	@Override
	public void setPosition(Vector2f position) {
		super.setPosition(position);

		// Horrid .. but this lets us layout on SlideIn. for some reason, the
		// scroll area
		// does not show because of the slide in/slide out
		layoutChildren();
	}

	@Override
	public void showWithEffect() {
		super.showWithEffect();
		layoutChildren();
		screen.setTabFocusElement(tfChatInput);
	}

	@Override
	public final void controlShowHook() {
		super.controlShowHook();
		layoutChildren();
		screen.setTabFocusElement(tfChatInput);
	}

	/**
	 * Clear the command history
	 */
	public void clearHistory() {
		commandHistory.clear();
	}

	/**
	 * Set the font size to use for output history. This overrides anything that
	 * might be
	 * set in the style map.
	 *
	 * @param fontSize
	 *            font size
	 */
	public void setConsoleFontSize(float fontSize) {
		fontSizeSet = true;
		getConsoleArea().setFontSize(fontSize);
		rebuildOutput();
	}

	/**
	 * Get the console area.
	 *
	 * @return console area
	 */
	public ScrollPanel getConsoleArea() {
		return saConsoleArea;
	}

	/**
	 * Call this method to display a standard message
	 *
	 * @param msg
	 *            The String message to display
	 */
	public void output(String msg) {
		output(TYPE_STANDARD, msg);
	}

	/**
	 * Call this method to display an error message
	 *
	 * @param msg
	 *            The String message to display
	 */
	public void outputError(String msg) {
		output(TYPE_ERROR, msg);
	}

	/**
	 * Get the maximum number of output messages that will be kept
	 *
	 * @return output history size
	 */
	public int getOutputHistorySize() {
		return outputHistorySize;
	}

	/**
	 * Set the maximum number of output messages that will be kept
	 *
	 * @return output history size
	 */
	public void setOutputHistorySize(int outputHistorySize) {
		this.outputHistorySize = outputHistorySize;
	}

	/**
	 * Call this method to display a message
	 *
	 * @param type
	 *            The type of message. There must be a style with the same name
	 *            in your
	 *            style map.
	 * @param msg
	 *            The String message to display
	 */
	public void output(String type, String msg) {
		// System.out.println(command);

		Element wasFocussed = screen.getTabFocusElement();
		final Style style = screen.getStyle("Console#" + type);
		if (style == null) {
			throw new IllegalArgumentException("No style Console#" + type);
		}
		final OutputMessage outputMessage = new OutputMessage(msg, style.getColorRGBA("fontColor"), style.getFloat("fontSize"),
				style.getString("fontName"));
		if (outputMessages.size() > outputHistorySize) {
			outputMessages.remove(0);
			saConsoleArea.getScrollableArea().removeChild(displayMessages.remove(0));
		}
		int idx = outputMessages.size();
		outputMessages.add(outputMessage);
		addMessage(idx, outputMessage);
//		sa
//		dirtyLayout(true);
//		layoutChildren();
		saConsoleArea.scrollToBottom();
		if (wasFocussed != null && wasFocussed.equals(tfChatInput)) {
			screen.setTabFocusElement(tfChatInput);
		}
	}

	private void sendMsg() {
		final String input = tfChatInput.getText();
		if (input.length() > 0) {

			// Update history and reset history scrolling back to the end
			commandHistory.add(input);
			currentHistoryIndex = -1;
			textBeforeSearch = null;

			// Inform sbuclass
			onCommand(input);

			// Reset and focus
			tfChatInput.setText("");
			chatForm.setSelectedTabIndex(tfChatInput);
		}
	}

	private void addMessage(int index, OutputMessage m) {
		Label l = createMessageLabel(index, m);
		displayMessages.add(l);
		saConsoleArea.addScrollableContent(l);
	}

	private void rebuildOutput() {
		// String displayText = "";
		int index = 0;
		saConsoleArea.getScrollableArea().removeAllChildren();
		for (OutputMessage cm : outputMessages) {
			addMessage(index, cm);
			index++;
		}
		layoutChildren();
		displayMessages.clear();
//		saConsoleArea.scrollToBottom();

		// float totalHeight = 0;
		// for (OutputMessage cm : outputMessages) {
		// Label l = createMessageLabel(index, cm);
		// displayMessages.add(l);
		// saConsoleArea.addScrollableChild(l);
		// l.setHeight(l.getTextElement().getHeight());
		// totalHeight += l.getHeight();
		// index++;
		// }
		// saConsoleArea.getScrollableArea().setHeight(totalHeight +
		// (saConsoleArea.getPadding() * 2));
		// totalHeight = 0;
		// for (Label l : displayMessages) {
		// totalHeight += l.getHeight();
		// l.setX(saContentPadding);
		// l.setWidth(saConsoleArea.getWidth() - (saContentPadding * 2));
		// l.setY(saConsoleArea.getScrollableArea().getHeight() - totalHeight);
		// }
		// saConsoleArea.scrollToBottom();
	}

	private Label createMessageLabel(int index, OutputMessage cm) {
		String s = cm.getMsg();

		Label l = new Label(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		l.setTextWrap(LineWrapMode.Word);
		l.setIsResizable(false);
		l.setIsMovable(false);
		l.setIgnoreMouse(true);
		l.addClippingLayer(saConsoleArea);
		l.setFont(screen.getStyle("Font").getString(cm.getFont()));
		l.setFontColor(cm.getColor());
		l.setFontSize(fontSizeSet ? saConsoleArea.getFontSize() : cm.getFontSize());
		String channelLabel = "";
		l.setText(channelLabel + s);
		l.setTextPadding(0);
		l.setIgnoreMouse(true);

		return l;
	}

	public TextField getChatInput() {
		return this.tfChatInput;
	}

	/**
	 * Sets the keyboard key code to execute commands (in place of the execute
	 * button)
	 *
	 * @param executeKey
	 *            key code to execute command
	 * @see KeyInputEvent
	 */
	public void setExecuteKey(int sendKey) {
		this.sendKey = sendKey;
	}

	/**
	 * Invoked when the text entry field is 'escaped' from.
	 *
	 */
	public void onEscape() {
		screen.resetTabFocusElement();
	}

	/**
	 * Abstract event method called when the user enters a command
	 *
	 * @param command
	 *            The command entered
	 */
	public abstract void onCommand(String command);

	/**
	 * Clear the current text input. Will also reset any current history search.
	 *
	 * @param string
	 */
	public void clearInput() {
		tfChatInput.setText("");
		currentHistoryIndex = -1;
		textBeforeSearch = null;
	}

	public class OutputMessage {

		private String msg;
		private ColorRGBA color;
		private float fontSize;
		private String font;

		public OutputMessage(String msg, ColorRGBA color, float fontSize, String font) {
			this.msg = msg;
			this.color = color;
			this.font = font;
			this.fontSize = fontSize;
		}

		public String getFont() {
			return font;
		}

		public float getFontSize() {
			return fontSize;
		}

		public ColorRGBA getColor() {
			return color;
		}

		public String getMsg() {
			return this.msg;
		}
	}

	/**
	 * Sets the ToolTip text to display for mouse focus of the TextField input
	 *
	 * @param tip
	 */
	public void setToolTipTextInput(String tip) {
		this.tfChatInput.setToolTipText(tip);
	}

	/**
	 * Set the Execute button text
	 *
	 * @param executeButtonText
	 *            Execute button text
	 */
	public void setTextExecuteButton(String executeButtonText) {
		btnCommandExecute.setText(executeButtonText);
	}

	/**
	 * Sets the ToolTip text to display for mouse focus of the Execute button
	 *
	 * @param tip
	 */
	public void setToolTipExecuteButton(String tip) {
		this.btnCommandExecute.setToolTipText(tip);
	}
}
