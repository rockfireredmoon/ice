/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iceui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.icelib.ChannelType;
import org.icelib.MessageParser;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.CheckBox;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.TextArea;
import icetone.controls.text.TextArea.ReturnMode;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Form;
import icetone.core.Layout.LayoutType;
import icetone.core.Size;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ColorField;
import icetone.extras.chooser.ColorTab;
import icetone.extras.util.ExtrasUtil;
import icetone.extras.windows.PositionableFrame;
import icetone.text.FontSpec;
import icetone.xhtml.XHTMLDisplay;

/**
 * 
 * @author t0neg0d
 */
public abstract class XChatBox extends Element {
	final static Logger LOG = Logger.getLogger(XChatBox.class.getName());

	private Object sbDefaultChannel;
	private String yourName;

	private XHTMLDisplay saChatArea;
	private TextArea tfChatInput;
	private boolean showChannelLabels = true;
	private Form chatForm;
	private PositionableFrame filters = null;
	float filterLineHeight;
	private int sendKey;
	private int chatHistorySize = 30;
	protected List<ChatMessage> chatMessages = new ArrayList<>();
	protected List<ChatChannel> channels = new ArrayList<>();
	private List<String> commandHistory = new ArrayList<String>();
	private String textBeforeSearch;
	private int currentHistoryIndex;
	private Element channelIndicator;
	private String tabName;
	private List<String> styleSheetAssets = new ArrayList<>(Arrays.asList("/Styles/IceUI/XChatBox.css"));

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen   The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public XChatBox(BaseScreen screen, String tabName) {
		this(screen, null, tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen     The screen control the Element is to be added to
	 * @param position   A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the
	 *                   Element
	 */
	public XChatBox(BaseScreen screen, Size dimensions, String tabName) {
		this(screen, null, dimensions, tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen     The screen control the Element is to be added to
	 * @param UID        A unique String identifier for the Element
	 * @param position   A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the
	 *                   Element
	 */
	public XChatBox(BaseScreen screen, String UID, Size dimensions, String tabName) {
		this(screen, UID, null, dimensions, tabName);
	}

	/**
	 * Creates a new instance of the ChatBoxExt control
	 * 
	 * @param screen        The screen control the Element is to be added to
	 * @param UID           A unique String identifier for the Element
	 * @param position      A Vector2f containing the x/y position of the Element
	 * @param dimensions    A Vector2f containing the width/height dimensions of the
	 *                      Element
	 * @param resizeBorders A Vector4f containg the border information used when
	 *                      resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg    The default image to use for the Slider's track
	 */
	public XChatBox(BaseScreen screen, String UID, Vector2f position, Size dimensions, String tabName) {
		super(screen, UID, position, dimensions);

		this.tabName = tabName;
		setIgnoreMouse(true);
		this.setResizable(false);

		setLayoutManager(new MigLayout(screen, "hidemode 2, ins 0", "[][grow, fill]", "[fill, grow][shrink 0]"));

		chatForm = new Form(screen);

		saChatArea = new XHTMLDisplay(screen);
		addElement(saChatArea, "span 2, wrap, growx");

		channelIndicator = new Element(screen);
		channelIndicator.setStyleClass("swatch");
		channelIndicator.setIgnoreGlobalAlpha(true);
		channelIndicator.setVisible(false);
		addElement(channelIndicator, "growx, growy, ay top");

		tfChatInput = new TextArea(screen);
		tfChatInput.setReturnMode(ReturnMode.NEWLINE_ON_SHIFT_RETURN);
		tfChatInput.setRows(3);
		tfChatInput.onKeyboardReleased(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_UP && evt.isCtrl()) {
				previousHistory();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_DOWN && evt.isCtrl()) {
				nextHistory();
				evt.setConsumed();
			} else if (evt.getKeyCode() == sendKey && !evt.isShift()) {
				if (tfChatInput.getText().length() > 0) {
					// tfChatInput.setText(tfChatInput.getText().substring(0,tfChatInput.getText().length()-1));
					sendMsg();
				}
				dirtyLayout(false, LayoutType.boundsChange());
				layoutChildren();
				evt.setConsumed();
			}
		});
		tfChatInput.setIgnoreGlobalAlpha(true);
		tfChatInput.setTextWrap(LineWrapMode.Word);
		tfChatInput.setTextVAlign(BitmapFont.VAlign.Top);
		// addElement(new ScrollPanel(screen, tfChatInput), "growx");
		addElement(tfChatInput, "growx");
		chatForm.addFormElement(tfChatInput);
	}

	public List<String> getStyleSheetAssets() {
		return styleSheetAssets;
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
			filters = new PositionableFrame(screen, getStyleId() + "Filters", 0, VAlign.Center, Align.Center,
					new Size(400, 400), true) {
				{
					setStyleClass("large");
				}
			};

			filters.setWindowTitle("Filters for " + tabName);
			filters.setMovable(false);
			filters.setResizable(false);
			BaseElement content = filters.getContentArea();
			content.setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[grow, fill][24!]", "[grow]"));

			for (final ChatChannel channel : channels) {
				if (channel.getVisibleToUser()) {
					CheckBox cb = new CheckBox(screen);
					cb.setChecked(!channel.getIsFiltered());
					cb.onChange(evt -> {
						channel.setIsFiltered(!evt.getNewValue());
						channelChanged(channel);
						rebuildChat();
					});
					cb.setText(channel.getFilterDisplayText());
					content.addElement(cb);

					ColorField cfc = new ColorField(screen, getColorForCommand(channel.getCommand()), false, false);
					cfc.onChange(evt -> changeChannelColor(channel, evt.getNewValue()));
					cfc.setTabs(ColorTab.WHEEL, ColorTab.RGB);
					cfc.setChooserText("Choose colour for " + channel.getFilterDisplayText());
					content.addElement(cfc);
				}
			}
			filters.setModal(true);
		}
		screen.showElement(filters, ScreenLayoutConstraints.center);
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
		channelIndicator.setVisible(showChannelIndicator);
		layoutChildren();
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
			channelIndicator.setDefaultColor(getColorForCommand(command));
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
	 * @param message message
	 */
	public void sendMsg(String input) {
		if (input.length() > 0) {
			Object command = sbDefaultChannel;
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
	 * @param sender    sender of the message (null for 'system' type messages)
	 * @param recipient recipient of the message (null for 'global' type messages)
	 * @param command   The object associated with the appropriate ChatChannel
	 * @param msg       The String message to display
	 */
	public void receiveMsg(String sender, String recipient, Object command, String msg) {
		ChatChannel channel = null;
		BaseElement wasFocussed = screen.getKeyboardFocus();
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
		rebuildChat();
		// saChatArea.layoutChildren();

		if (wasFocussed != null && wasFocussed.equals(tfChatInput)) {
			screen.setKeyboardFocus(tfChatInput);
		}
	}

	@Override
	public BaseElement setFont(FontSpec font) {
		try {
			return super.setFont(font);
		} finally {
			rebuildChat();
		}
	}

	public List<ChatMessage> getChatMessage() {
		return chatMessages;
	}

	protected void rebuildChat() {
		final StringBuilder bui = new StringBuilder();
		appendBodyHeader(bui);
		for (ChatMessage cm : chatMessages) {
			MessageParser mp = new MessageParser();
			mp.parse(cm.getMsg());
			List<MessageParser.MessageElement> els = mp.getElements();

			boolean bold = false;
			String channelLabel = "";
			final ChannelType channelType = (ChannelType) cm.channel.getCommand();
			if (!channelType.isChat()) {
				if (showChannelLabels) {
					channelLabel = "[" + StringEscapeUtils.escapeXml(cm.getChannel().getName()) + "]:";
				}
			} else {
				channelLabel = cm.sender == null ? (yourName == null ? "You" : yourName) : cm.sender;
				if (mp.isAct()) {
					bold = true;
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
			StringBuilder txt = new StringBuilder();
			txt.append(StringEscapeUtils.escapeXml(channelLabel));
			for (MessageParser.MessageElement el : els) {
				switch (el.getType()) {
				case LINK:
					txt.append(el.getValue());
				default:
					txt.append(el.getValue());
				}
			}

			bui.append("<p class=\"chat-message\" style=\"color: #");

			ColorRGBA cmdcol = getColorForCommand(cm.getChannel().getCommand());
			bui.append(IceUI.toHexNumber(cmdcol));
			bui.append(";");
			FontSpec font = calcFont(this);
			bui.append("font-family: '");
			bui.append(FilenameUtils.getBaseName(font.getFamily()));
			bui.append("';");
			bui.append("font-size: ");
			bui.append((int) font.getSize());
			bui.append("pt;");
			bui.append("\">");
			if (bold)
				bui.append("<strong>");
			bui.append(ExtrasUtil.stripControlCodesForXHTML(StringEscapeUtils.escapeXml(txt.toString())));
			if (bold)
				bui.append("</strong>");
			bui.append("</p>");
		}
		appendBodyTail(bui);
		try {
			saChatArea.setDocumentFromString(bui.toString(), "asset://chat.html");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to parse HTML built from chat text.", e);
			bui.setLength(0);
			appendBodyHeader(bui);
			bui.append("<h1>Error.</h1>");
			if (e.getMessage() != null) {
				bui.append("<p>");
				bui.append(StringEscapeUtils.escapeXml(e.getMessage()));
				bui.append("</p>");
			}
			bui.append("<pre><code>");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			bui.append(sw.toString());
			bui.append("</code></pre>");
			appendBodyTail(bui);
			saChatArea.setDocumentFromString(bui.toString(), "asset://chat.html");
		}
		saChatArea.scrollToBottom();
	}

	protected void appendBodyTail(final StringBuilder bui) {
		bui.append("</body>\n");
		bui.append("</html>\n");
	}

	protected void appendBodyHeader(final StringBuilder bui) {
		bui.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bui.append("<!DOCTYPE html>\n");
		bui.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		bui.append("<head>");
		for (String ss : styleSheetAssets) {
			bui.append(String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\" title=\"Style " + ss
					+ "\" media=\"screen\" />", ss));
		}
		bui.append("</head>");
		bui.append("<body class=\"chat\" style=\"background: inherit; ");
		bui.append("\">\n");
	}

	private void clearHistory() {
		while (chatMessages.size() > chatHistorySize) {
			chatMessages.remove(0);
		}
	}

	// private void createAndAddMessageLabel(int index, ChatMessage cm) {
	// ChatLabel l = createMessageLabel(index, cm);
	// displayMessages.add(l);
	// saChatArea.addScrollableContent(l);
	// }
	//
	// private ChatLabel createMessageLabel(int index, ChatMessage cm) {
	// MessageParser mp = new MessageParser();
	// mp.parse(cm.getMsg());
	// List<MessageParser.MessageElement> els = mp.getElements();
	//
	// ChatLabel l = new ChatLabel(screen, getUID() + ":Label" + index, cm);
	// l.setFontColor(getColorForCommand(cm.getChannel().getCommand()));
	// System.err.println("setting fnt: " + chatFont + " / " + chatFontSize);
	// l.setFont(chatFont);
	// l.setFontSize(chatFontSize);
	// String channelLabel = "";
	// final ChannelType channelType = (ChannelType) cm.channel.getCommand();
	// if (!channelType.isChat()) {
	// if (showChannelLabels) {
	// channelLabel = "[" + cm.getChannel().getName() + "]:";
	// }
	// } else {
	// channelLabel = cm.sender == null ? (yourName == null ? "You" : yourName)
	// : cm.sender;
	// if (mp.isAct()) {
	// l.setFont(chatBoldFont);
	// channelLabel += " ";
	// } else {
	// if (channelType.hasSubChannel()) {
	// channelLabel = cm.sender == null ? "You" : cm.sender;
	// channelLabel += " tell";
	// if (cm.recipient == null) {
	// channelLabel += "s you";
	// } else {
	// channelLabel += " " + cm.recipient;
	// }
	// channelLabel += ":";
	// } else {
	// channelLabel += " says:";
	// }
	// }
	// }
	//
	// // Create text
	// StringBuilder bui = new StringBuilder();
	// bui.append(channelLabel);
	// for (MessageParser.MessageElement el : els) {
	// switch (el.getType()) {
	// case LINK:
	// bui.append(el.getValue());
	// default:
	// bui.append(el.getValue());
	// }
	// }
	// l.setText(bui.toString());
	// return l;
	// }

	// private ChatLabel XXcreateMessageLabel(int index, ChatMessage cm) {
	// String s = cm.getMsg();
	// ChatLabel l = new ChatLabel(screen, getUID() + ":Label" + index, cm);
	// l.setFontColor(getColorForCommand(cm.getChannel().getCommand()));
	// l.setFontSize(chatFontSize);
	// l.setFont(screen.getStyle("ChatBox").getString("defaultFont"));
	// String channelLabel = "";
	// final ChannelType channelType = (ChannelType) cm.channel.getCommand();
	// if (!channelType.isChat()) {
	// if (showChannelLabels) {
	// channelLabel = "[" + cm.getChannel().getName() + "]:";
	// }
	// } else {
	// channelLabel = cm.sender == null ? (yourName == null ? "You" : yourName)
	// : cm.sender;
	// if (s.startsWith("*") && s.endsWith("*")) {
	// s = s.substring(1, s.length() - 1);
	// channelLabel += " ";
	// } else {
	// if (channelType.hasSubChannel()) {
	// channelLabel = cm.sender == null ? "You" : cm.sender;
	// channelLabel += " tell";
	// if (cm.recipient == null) {
	// channelLabel += "s you";
	// } else {
	// channelLabel += " " + cm.recipient;
	// }
	// channelLabel += ":";
	// } else {
	// channelLabel += " says:";
	// }
	// }
	// }
	// l.setText(channelLabel + s);
	// return l;
	// }

	public TextArea getChatInput() {
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
	 * @param command The Object associated with the appropriate ChatChannel for the
	 *                message
	 * @param msg     The String message to display
	 */
	public abstract void onSendMsg(Object command, String msg);

	/**
	 * Adds a ChatChannel that messages are display under and are filtered by
	 * 
	 * @param channel channel
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
	 * @param channel channel
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
		tfChatInput.focus();
	}

	public void unfocusInput() {
		tfChatInput.defocus();
	}

	public boolean isInputFocussed() {
		return tfChatInput.isHovering();
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
