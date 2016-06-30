/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iceui;

import java.util.ArrayList;
import java.util.List;

import org.icelib.ChannelType;
import org.icelib.MessageParser;
import org.iceui.controls.FancyPositionableWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.Swatch;
import org.iceui.controls.UIUtil;
import org.iceui.controls.color.ColorFieldControl;
import org.iceui.controls.color.XColorSelector;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.CheckBox;
import icetone.controls.form.Form;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.TextElement;
import icetone.controls.text.TextField;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseButtonListener;

/**
 * 
 * @author t0neg0d
 */
public abstract class XChatBox extends Element {

	private float chatFontSize;
	private Object sbDefaultChannel;
	private String yourName;
	private String chatFont;
	private String chatBoldFont;

	class ChatLabel extends TextElement implements MouseButtonListener {

		private final ChatMessage chatMessage;

		ChatLabel(ElementManager screen, String uid, ChatMessage chatMessage) {
			super(screen);
			this.chatMessage = chatMessage;
			setIgnoreMouse(false);
			setIgnoreMouseWheel(true);
			setIgnoreMouseLeftButton(true);
			setTextWrap(LineWrapMode.Word);
			setTextVAlign(BitmapFont.VAlign.Top);
			addClippingLayer(this);
			setIsResizable(false);
			setIsMovable(false);
		}

		@Override
		public void setDimensions(Vector2f dimensions) {
			// TODO Auto-generated method stub
			super.setDimensions(dimensions);
			System.err.println("setDimensions(" + dimensions + ")");
		}

		@Override
		public void setWidth(float width) {
			// TODO Auto-generated method stub
			super.setWidth(width);
			System.err.println("setWidth(" + width + ")");
		}

		public void onMouseLeftPressed(MouseButtonEvent evt) {
		}

		public void onMouseLeftReleased(MouseButtonEvent evt) {
			messageClicked(chatMessage, false);
		}

		public void onMouseRightPressed(MouseButtonEvent evt) {
		}

		public void onMouseRightReleased(MouseButtonEvent evt) {
			messageClicked(chatMessage, true);
		}

		@Override
		public void onUpdate(float tpf) {
		}

		@Override
		public void onEffectStart() {
		}

		@Override
		public void onEffectStop() {
		}
	}

	private ScrollPanel saChatArea;
	private TextField tfChatInput;
	private boolean showChannelLabels = true;
	private Form chatForm;
	private FancyPositionableWindow filters = null;
	float filterLineHeight;
	float controlSpacing, controlSize, buttonWidth;
	Vector4f indents;
	private int sendKey;
	private int chatHistorySize = 30;
	protected List<ChatMessage> chatMessages = new ArrayList<>();
	protected List<ChatChannel> channels = new ArrayList<>();
	private String defaultCommand;
	List<ChatLabel> displayMessages = new ArrayList<>();
	private boolean inputFocussed;
	private List<String> commandHistory = new ArrayList<String>();
	private String textBeforeSearch;
	private int currentHistoryIndex;
	private Swatch channelIndicator;
	private String tabName;

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public XChatBox(ElementManager screen, String tabName) {
		this(screen, UIDUtil.getUID(), screen.getStyle("ChatBox").getVector2f("defaultSize"),
				screen.getStyle("ChatBox").getVector4f("resizeBorders"), screen.getStyle("ChatBox").getString("defaultImg"),
				tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public XChatBox(ElementManager screen, Vector2f dimensions, String tabName) {
		this(screen, UIDUtil.getUID(), dimensions, screen.getStyle("ChatBox").getVector4f("resizeBorders"),
				screen.getStyle("ChatBox").getString("defaultImg"), tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
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
	 *            The default image to use for the Slider's track
	 */
	public XChatBox(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, String tabName) {
		this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg, tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public XChatBox(ElementManager screen, String UID, String tabName) {
		this(screen, UID, screen.getStyle("ChatBox").getVector2f("defaultSize"),
				screen.getStyle("ChatBox").getVector4f("resizeBorders"), screen.getStyle("ChatBox").getString("defaultImg"),
				tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
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
	public XChatBox(ElementManager screen, String UID, Vector2f dimensions, String tabName) {
		this(screen, UID, dimensions, screen.getStyle("ChatBox").getVector4f("resizeBorders"),
				screen.getStyle("ChatBox").getString("defaultImg"), tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
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
	 *            The default image to use for the Slider's track
	 */
	public XChatBox(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			String tabName) {
		super(screen, UID, dimensions, resizeBorders, defaultImg);

		this.tabName = tabName;
		// this.setIsMovable(false);
		setIgnoreMouse(true);
		this.setIsResizable(false);

		setLayoutManager(new MigLayout(screen, "hidemode 2, ins 0, gap 0", "[][grow, fill]", "[fill, grow][]"));

		chatFontSize = screen.getStyle("ChatBox").getFloat("fontSize");
		chatFont = screen.getStyle("ChatBox").getString("defaultFont");
		chatBoldFont = screen.getStyle("ChatBox").getString("strongFont");
		chatForm = new Form(screen);

		setFontSize(screen.getStyle("Common").getFloat("fontSize"));
		indents = screen.getStyle("ChatBox").getVector4f("contentIndents");
		controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");
		controlSize = screen.getStyle("Common").getFloat("defaultControlSize");
		buttonWidth = screen.getStyle("Button").getVector2f("defaultSize").x;

		saChatArea = new ScrollPanel(screen, UID + ":ChatArea", Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		saChatArea.setTextPadding(0);
		saChatArea.setClipPadding(0);
		saChatArea.setResizeBorders(0);
		saChatArea.setIgnoreMouseLeftButton(true);
		saChatArea.getScrollBounds().setIgnoreMouseLeftButton(true);
		saChatArea.getScrollableArea().setIgnoreMouseLeftButton(true);
		saChatArea.setIsResizable(false);
		// saChatArea.setIsMovable(false);
		((WrappingLayout) saChatArea.getScrollContentLayout()).setOrientation(Element.Orientation.HORIZONTAL);
		((WrappingLayout) saChatArea.getScrollContentLayout()).setFill(true);
		// final float insets =
		// screen.getStyle("ChatBox").getFloat("scrollAreaInsets");
		// ((WrappingLayout) saChatArea.getScrollContentLayout()).setMargin(new
		// Vector4f(insets,insets,insets,insets));
		saChatArea.setText("");
		addChild(saChatArea, "span 2, wrap, growx");

		channelIndicator = new Swatch(screen) {
			public void onMouseLeftReleased(MouseButtonEvent evt) {
			}
		};
		channelIndicator.setIgnoreGlobalAlpha(true);
		channelIndicator.setIsVisible(false);
		addChild(channelIndicator, "growx, growy, ay top");

		tfChatInput = new TextField(screen, UID + ":ChatInput") {
			@Override
			public void controlKeyPressHook(KeyInputEvent evt, String text) {
				if (evt.getKeyCode() == KeyInput.KEY_UP) {
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
				} else if (evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
					screen.setKeyboardElement(null);
					screen.resetTabFocusElement();
				}
			}

			@Override
			public void onGetFocus(MouseMotionEvent evt) {
				super.onGetFocus(evt);
				inputFocussed = true;
			}

			@Override
			public void onLoseFocus(MouseMotionEvent evt) {
				super.onLoseFocus(evt);
				inputFocussed = false;
			}
		};
		tfChatInput.setIgnoreGlobalAlpha(true);
		tfChatInput.setTextWrap(LineWrapMode.Word);
		tfChatInput.setTextVAlign(BitmapFont.VAlign.Top);
		addChild(tfChatInput, "growx");
		chatForm.addFormElement(tfChatInput);

		LUtil.removeEffects(this);
		populateEffects("ChatBox");
		LUtil.noScaleNoDock(this);
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	protected void channelChanged(ChatChannel channel) {
	}

	public List<ChatChannel> getChannels() {
		return channels;
	}

	public ColorRGBA getColorForCommand(Object command) {
		return ColorRGBA.White;
	}

	public void showChatFiltersWindow() {
		if (filters == null) {
			filters = new FancyPositionableWindow(screen, getUID() + "Filters", 0, VPosition.MIDDLE, HPosition.CENTER,
					new Vector2f(400, 400), FancyWindow.Size.LARGE, true);

			filters.setWindowTitle("Filters for " + tabName);
			filters.setIsMovable(false);
			filters.setIsResizable(false);
			UIUtil.center(screen, filters);
			Element content = filters.getContentArea();
			content.setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[grow, fill][24!]", "[grow]"));

			for (final ChatChannel channel : channels) {
				if (channel.getVisibleToUser()) {
					CheckBox cb = new CheckBox(screen) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
							channel.setIsFiltered(!isToggled);
							channelChanged(channel);
							rebuildChat();
						}
					};
					cb.setIsCheckedNoCallback(!channel.getIsFiltered());
					cb.setLabelText(channel.getFilterDisplayText());
					content.addChild(cb);

					ColorFieldControl cfc = new ColorFieldControl(screen, getColorForCommand(channel.getCommand()), false, false,
							false) {
						@Override
						protected void onChangeColor(ColorRGBA newColor) {
							changeChannelColor(channel, newColor);
						}
					};
					cfc.setTabs(XColorSelector.ColorTab.PALETTE, XColorSelector.ColorTab.WHEEL);
					cfc.setChooserText("Choose colour for " + channel.getFilterDisplayText());
					content.addChild(cfc);
				}
			}
			UIUtil.center(screen, filters);
			screen.addElement(filters, null, true);
			filters.showAsModal(true);
		} else {
			filters.showAsModal(true);
		}

	}

	public String getTabName() {
		return tabName;
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

	public void setShowChannelIndicator(boolean showChannelIndicator) {
		channelIndicator.setIsVisible(showChannelIndicator);
		layoutChildren();
	}

	public void setChatFontSize(float chatFontSize) {
		this.chatFontSize = chatFontSize;
		rebuildChat();
	}

	public void setChatFont(String chatFont) {
		if (chatFont == null) {
			throw new IllegalArgumentException();
		}
		this.chatFont = chatFont;
		rebuildChat();
	}

	public void setChatBoldFont(String chatBoldFont) {
		if (chatBoldFont == null) {
			throw new IllegalArgumentException();
		}
		this.chatBoldFont = chatBoldFont;
		rebuildChat();
	}

	public ScrollPanel getChatArea() {
		return saChatArea;
	}

	protected void changeChannelColor(ChatChannel channel, ColorRGBA newColor) {
		rebuildChat();
	}

	protected void onChannelChange(Object command) {
		ChatChannel ch = getChannelByCommand(command);
		if (ch != null) {
			channelIndicator.setColor(getColorForCommand(command));
		}
	}

	private void sendMsg() {
		final String input = tfChatInput.getText();

		// Update history and reset history scrolling back to the end
		commandHistory.add(input);
		currentHistoryIndex = -1;
		textBeforeSearch = null;

		sendMsg(input);
	}

	/**
	 * Send a message on the currentl channel.
	 * 
	 * @param message
	 *            message
	 */
	public void sendMsg(String input) {
		if (input.length() > 0) {
			Object command = sbDefaultChannel;
			System.out.println("sending: " + input);
			onSendMsg(command, input);
			tfChatInput.setText("");
			focusInput();
		}
	}

	public void setYourName(String yourName) {
		this.yourName = yourName;
		rebuildChat();
	}

	/**
	 * Call this method to display a message
	 * 
	 * @param sender
	 *            sender of the message (null for 'system' type messages)
	 * @param recipient
	 *            recipient of the message (null for 'global' type messages)
	 * @param command
	 *            The object associated with the appropriate ChatChannel
	 * @param msg
	 *            The String message to display
	 */
	public void receiveMsg(String sender, String recipient, Object command, String msg) {
		ChatChannel channel = null;
		Element wasFocussed = screen.getTabFocusElement();
		if (command instanceof String) {
			channel = getChannelByStringCommand((String) command);
		} else {
			channel = getChannelByCommand(command);
		}
		if (channel == null) {
			throw new IllegalArgumentException("No channel for " + command);
		}
		final ChatMessage chatMessage = new ChatMessage(channel, msg, sender, recipient);
		chatMessages.add(chatMessage);
		clearHistory();
		if (!channel.getIsFiltered()) {
			createAndAddMessageLabel(displayMessages.size(), chatMessage);
		}
		saChatArea.layoutChildren();

		if (wasFocussed != null && wasFocussed.equals(tfChatInput)) {
			screen.setTabFocusElement(tfChatInput);
		}
	}

	public List<ChatMessage> getChatMessage() {
		return chatMessages;
	}

	protected void rebuildChat() {
		String displayText = "";
		int index = 0;
		saChatArea.getScrollableArea().removeAllChildren();
		//
		displayMessages.clear();

		float totalHeight = 0;
		for (ChatMessage cm : chatMessages) {
			if (!cm.getChannel().getIsFiltered()) {
				createAndAddMessageLabel(index, cm);
				index++;
			}
		}
		layoutChildren();
		saChatArea.scrollToBottom();
	}

	private void clearHistory() {
		while (chatMessages.size() > chatHistorySize) {
			chatMessages.remove(0);
		}
	}

	private void createAndAddMessageLabel(int index, ChatMessage cm) {
		ChatLabel l = createMessageLabel(index, cm);
		displayMessages.add(l);
		saChatArea.addScrollableContent(l);
	}

	private ChatLabel createMessageLabel(int index, ChatMessage cm) {
		MessageParser mp = new MessageParser();
		mp.parse(cm.getMsg());
		List<MessageParser.MessageElement> els = mp.getElements();

		ChatLabel l = new ChatLabel(screen, getUID() + ":Label" + index, cm);
		l.setFontColor(getColorForCommand(cm.getChannel().getCommand()));
		System.err.println("setting fnt: " + chatFont + " / " + chatFontSize);
		l.setFont(chatFont);
		l.setFontSize(chatFontSize);
		String channelLabel = "";
		final ChannelType channelType = (ChannelType) cm.channel.getCommand();
		if (!channelType.isChat()) {
			if (showChannelLabels) {
				channelLabel = "[" + cm.getChannel().getName() + "]:";
			}
		} else {
			channelLabel = cm.sender == null ? (yourName == null ? "You" : yourName) : cm.sender;
			if (mp.isAct()) {
				l.setFont(chatBoldFont);
				channelLabel += " ";
			} else {
				if (channelType.hasSubChannel()) {
					channelLabel = cm.sender == null ? "You" : cm.sender;
					channelLabel += " tell";
					if (cm.recipient == null) {
						channelLabel += "s you";
					} else {
						channelLabel += " " + cm.recipient;
					}
					channelLabel += ":";
				} else {
					channelLabel += " says:";
				}
			}
		}

		// Create text
		StringBuilder bui = new StringBuilder();
		bui.append(channelLabel);
		for (MessageParser.MessageElement el : els) {
			switch (el.getType()) {
			case LINK:
				bui.append(el.getValue());
			default:
				bui.append(el.getValue());
			}
		}
		l.setText(bui.toString());
		return l;
	}

	private ChatLabel XXcreateMessageLabel(int index, ChatMessage cm) {
		String s = cm.getMsg();
		ChatLabel l = new ChatLabel(screen, getUID() + ":Label" + index, cm);
		l.setFontColor(getColorForCommand(cm.getChannel().getCommand()));
		l.setFontSize(chatFontSize);
		l.setFont(screen.getStyle("ChatBox").getString("defaultFont"));
		String channelLabel = "";
		final ChannelType channelType = (ChannelType) cm.channel.getCommand();
		if (!channelType.isChat()) {
			if (showChannelLabels) {
				channelLabel = "[" + cm.getChannel().getName() + "]:";
			}
		} else {
			channelLabel = cm.sender == null ? (yourName == null ? "You" : yourName) : cm.sender;
			if (s.startsWith("*") && s.endsWith("*")) {
				s = s.substring(1, s.length() - 1);
				l.setFont(screen.getStyle("ChatBox").getString("strongFont"));
				channelLabel += " ";
			} else {
				if (channelType.hasSubChannel()) {
					channelLabel = cm.sender == null ? "You" : cm.sender;
					channelLabel += " tell";
					if (cm.recipient == null) {
						channelLabel += "s you";
					} else {
						channelLabel += " " + cm.recipient;
					}
					channelLabel += ":";
				} else {
					channelLabel += " says:";
				}
			}
		}
		l.setText(channelLabel + s);
		return l;
	}

	public TextField getChatInput() {
		return this.tfChatInput;
	}

	/**
	 * Sets the keyboard key code to send messages (in place of the send button)
	 * 
	 * @param sendKey
	 */
	public void setSendKey(int sendKey) {
		this.sendKey = sendKey;
	}

	/**
	 * Abstract event method called when the user sends a message
	 * 
	 * @param command
	 *            The Object associated with the appropriate ChatChannel for the
	 *            message
	 * @param msg
	 *            The String message to display
	 */
	public abstract void onSendMsg(Object command, String msg);

	/**
	 * Adds a ChatChannel that messages are display under and are filtered by
	 * 
	 * @param channel
	 *            channel
	 */
	public final void addChatChannel(ChannelDefinition def) {
		ChatChannel channel = def.createChannel();
		channels.add(channel);
		configureChannel(channel);
		if (sbDefaultChannel == null) {
			setChannelByCommand(channel.getCommand());
			onChannelChange(sbDefaultChannel);
		}
	}

	protected void configureChannel(ChatChannel channel) {
	}

	public void removeChatChannel(String name) {
		ChatChannel channel = getChannelByName(name);
		if (channel != null) {
			channels.remove(channel);
		}
	}

	private ChatChannel getChannelByCommand(Object command) {
		ChatChannel c = null;
		for (ChatChannel channel : channels) {
			if (channel.getCommand() == command) {
				c = channel;
				break;
			}
		}
		return c;
	}

	private ChatChannel getChannelByStringCommand(String command) {
		ChatChannel c = null;
		for (ChatChannel channel : channels) {
			if (channel.getCommand().equals(String.valueOf(command))) {
				c = channel;
				break;
			}
		}
		return c;
	}

	private ChatChannel getChannelByName(String name) {
		ChatChannel c = null;
		for (ChatChannel channel : channels) {
			if (channel.getName().equals(name)) {
				c = channel;
				break;
			}
		}
		return c;
	}

	/**
	 * Set the current channel
	 * 
	 * @param channel
	 *            channel
	 */
	public void setChannelByCommand(Object command) {
		sbDefaultChannel = command;
		onChannelChange(command);
	}

	public void setShowChannelLabels(boolean showChannelLabels) {
		this.showChannelLabels = showChannelLabels;
		layoutChildren();
	}

	public void focusInput() {
		System.err.println("FOCUS!");
		screen.setTabFocusElement(tfChatInput);
		screen.setKeyboardElement(tfChatInput);
	}

	public void unfocusInput() {
		System.err.println("Unfocussing");
		screen.resetTabFocusElement();
	}

	public boolean isInputFocussed() {
		return inputFocussed;
	}

	public void setInputText(String inputText) {
		tfChatInput.setText(inputText);
		tfChatInput.setCaretPositionToEnd();
		focusInput();
	}

	public void clearInput() {
		tfChatInput.setText("");
	}

	protected void messageClicked(ChatMessage chatMessage, boolean right) {
		if (!right) {
			setChannelByCommand(chatMessage.getChannel().getCommand());
			focusInput();
		}
	}

	public class ChatMessage {

		private ChatChannel channel;
		private String msg;
		private String sender;
		private final String recipient;

		public ChatMessage(ChatChannel channel, String msg, String sender, String recipient) {
			this.channel = channel;
			this.msg = msg;
			this.sender = sender;
			this.recipient = recipient;
		}

		public String getSender() {
			return sender;
		}

		public String getRecipient() {
			return recipient;
		}

		public ChatChannel getChannel() {
			return channel;
		}

		public String getMsg() {
			return this.msg;
		}
	}

	/**
	 * Called by the Chat Filter Window.
	 * 
	 * @param channel
	 * @param filter
	 */
	public void setChannelFiltered(ChatChannel channel, boolean filter) {
		channel.setIsFiltered(filter);
		rebuildChat();
	}

	/**
	 * Sets the ToolTip text to display for mouse focus of the TextField input
	 * 
	 * @param tip
	 */
	public void setToolTipTextInput(String tip) {
		this.tfChatInput.setToolTipText(tip);
	}
}
