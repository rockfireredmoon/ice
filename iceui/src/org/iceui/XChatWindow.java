package org.iceui;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringEscapeUtils;
import org.icelib.ChannelType;
import org.iceui.controls.FancyDialogBox;
import org.iceui.controls.FancyInputBox;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.FancyWindow.Size;
import org.iceui.controls.PersistentPanel;
import org.iceui.controls.SaveType;
import org.iceui.controls.UIUtil;
import org.iceui.controls.XTabControl;
import org.iceui.controls.ZMenu;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;
import icetone.listeners.MouseButtonListener;

public class XChatWindow extends PersistentPanel implements MouseButtonListener {

	private static final Logger LOG = Logger.getLogger(XChatWindow.class.getName());
	private String chatBoldFont;

	public void onMouseLeftPressed(MouseButtonEvent evt) {
	}

	public void onMouseLeftReleased(MouseButtonEvent evt) {
	}

	public void onMouseRightPressed(MouseButtonEvent evt) {
	}

	public void onMouseRightReleased(MouseButtonEvent evt) {
		showChatTabMenu(evt.getX(), evt.getY());
	}

	public Object getChannels() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public enum TabMenuOption {

		ADD_TAB, DELETE_TAB, RENAME_TAB, SAVE_TAB, CONFIGURE_FILTERS
	}

	class XChatTabs extends XTabControl {

		XChatTabs(ElementManager screen, String uid) {
			super(screen, uid, Vector2f.ZERO);
			setIgnoreMouse(true);
			getSlider().setIgnoreMouse(true);
		}

		@Override
		public void addChild(Element child, boolean hide) {
			super.addChild(child, hide);
			if (child instanceof TabPanel) {
				child.setIgnoreMouse(true);
				child.borders.set(Vector4f.ZERO);
				child.setColorMap(String.format("%s/Blank.png", screen.getStyle("Common").getString("textures")));
			}
		}

		@Override
		protected void onTabRightClick(int indexOf, MouseButtonEvent evt) {
			setSelectedTabIndex(indexOf);
			showChatTabMenu(evt.getX(), evt.getY());
		}
	}

	private final XTabControl tabs;
	private final List<XChatBox> chatTabs = new ArrayList<XChatBox>();
	private boolean showSendButton = true;
	private int sendKey = -1;
	private float chatFontSize = -1;
	private String chatFont = null;
	private List<ChannelDefinition> channels = new ArrayList<ChannelDefinition>();
	private String yourName;

	private XChatBox createTab(String tabName) {
		XChatBox tab = new XChatBox(screen, tabName) {
			@Override
			public void onSendMsg(Object command, String msg) {
				onSendChatMsg(this, command, msg);
			}

			@Override
			protected void channelChanged(ChatChannel channel) {
				super.channelChanged(channel);
				XChatWindow.this.channelChanged(this, channel);
			}

			@Override
			protected void configureChannel(ChatChannel channel) {
				super.configureChannel(channel);
				XChatWindow.this.configureChannel(this, channel);
			}

			@Override
			protected void changeChannelColor(ChatChannel channel, ColorRGBA newColor) {
				XChatWindow.this.changeChannelColor(this, channel, newColor);
				super.changeChannelColor(channel, newColor);

				// Rebuild all other tabs too
				for (XChatBox tab : chatTabs) {
					if (!tab.equals(this)) {
						tab.rebuildChat();
					}
				}
			}

			@Override
			public ColorRGBA getColorForCommand(Object command) {
				return getColorForChannelCommand(this, command);
			}

			@Override
			protected void messageClicked(XChatBox.ChatMessage chatMessage, boolean right) {
				// Allow Chat Window client to override behavior, e.g. recognise
				// links in chat
				if (XChatWindow.this.messageClicked(this, chatMessage, right)) {
					super.messageClicked(chatMessage, right);
				}
			}
		};
		tab.setShowChannelIndicator(true);
		tab.setTextPadding(0);
		return tab;
	}

	public XChatWindow(String configKey, ElementManager screen, Preferences pref) {
		super(screen, configKey, screen.getStyle("Common").getInt("defaultWindowOffset"), VPosition.BOTTOM, HPosition.LEFT, screen
				.getStyle("ChatWindow").getVector2f("defaultSize"), screen.getStyle("ChatWindow").getVector4f("resizeBorders"),
				screen.getStyle("ChatWindow").getString("defaultImg"), SaveType.POSITION_AND_SIZE, pref);
		chatFontSize = -1;
		chatFont = null;
		chatBoldFont = null;
		setLayoutManager(new MigLayout(screen, "", "[grow, fill]", "[grow, fill]"));
		setIgnoreMouse(false);
		tabs = new XChatTabs(screen, getUID() + ":tabs");
		tabs.setUseSlideEffect(true);
		addTab("Default");
		addChild(tabs);
	}

	public void receiveMsg(String sender, String recipient, ChannelType channel, String text) {
		for (XChatBox t : chatTabs) {
			t.receiveMsg(sender, recipient, channel, text);
		}
	}

	public XChatBox getChatTab(int index) {
		return chatTabs.get(index);
	}

	public void setChatFontSize(float chatFontSize) {
		this.chatFontSize = chatFontSize;
		for (XChatBox t : chatTabs) {
			t.setChatFontSize(chatFontSize);
		}
	}

	public void setChatFont(String chatFont) {
		this.chatFont = chatFont;
		for (XChatBox t : chatTabs) {
			t.setChatFont(chatFont);
		}
	}

	public void setChatBoldFont(String chatBoldFont) {
		this.chatBoldFont = chatBoldFont;
		for (XChatBox t : chatTabs) {
			t.setChatBoldFont(chatBoldFont);
		}
	}

	public void setYourName(String yourName) {
		this.yourName = yourName;
		for (XChatBox t : chatTabs) {
			t.setYourName(yourName);
		}
	}

	public void setSendKey(int sendKey) {
		this.sendKey = sendKey;
		for (XChatBox t : chatTabs) {
			t.setSendKey(sendKey);
		}
	}

	public void setChannelByCommand(ChannelType channelType) {
		int index = tabs.getTabIndex();
		chatTabs.get(index).setChannelByCommand(channelType);
	}

	public void setInputText(String text) {
		int index = tabs.getTabIndex();
		chatTabs.get(index).setInputText(text);
	}

	public void clearInput() {
		int index = tabs.getTabIndex();
		chatTabs.get(index).clearInput();
	}

	public void sendMsg(String text) {
		int index = tabs.getTabIndex();
		chatTabs.get(index).sendMsg(text);
	}

	public void focusInput() {
		int index = tabs.getTabIndex();
		chatTabs.get(index).focusInput();
	}

	public final void addChatChannel(String UID, String name, Object command, String filterDisplayText, boolean visibleToUser) {
		ChannelDefinition def = new ChannelDefinition(UID, name, command, filterDisplayText, visibleToUser);
		channels.add(def);
		for (XChatBox ct : chatTabs) {
			ct.addChatChannel(def);
		}
	}

	public boolean isInputFocussed() {
		int index = tabs.getTabIndex();
		return chatTabs.get(index).isInputFocussed();
	}

	public void addTab(String tabName) {
		XChatBox tab = createTab(tabName);
		for (ChannelDefinition c : channels) {
			tab.addChatChannel(c);
		}
		int index = chatTabs.size();
		if (chatFontSize != -1) {
			tab.setChatFontSize(chatFontSize);
		}
		if (chatFont != null) {
			tab.setChatFont(chatFont);
		}
		if (chatBoldFont != null) {
			tab.setChatBoldFont(chatBoldFont);
		}
		if (sendKey != -1) {
			tab.setSendKey(sendKey);
		}
		if (yourName != null) {
			tab.setYourName(yourName);
		}
		tabs.addTabWithRMBSupport(tab.getTabName());
		chatTabs.add(tab);
		tabs.addTabChild(index, tab);
		tabs.layoutChildren();
	}

	protected void onShowTabContextMenu() {
	}

	protected void onHideTabContextMenu() {
	}

	public void onSendChatMsg(XChatBox tab, Object o, String text) {
	}

	protected ColorRGBA getColorForChannelCommand(XChatBox tab, Object command) {
		return ColorRGBA.White;
	}

	protected void changeChannelColor(XChatBox tab, ChatChannel channel, ColorRGBA newColor) {
	}

	protected void channelChanged(XChatBox tab, ChatChannel channel) {
	}

	protected void configureChannel(XChatBox tab, ChatChannel channel) {
	}

	protected boolean messageClicked(XChatBox aThis, XChatBox.ChatMessage chatMessage, boolean right) {
		return true;
	}

	protected void newChatTab(String name) {
		addTab(name);
	}

	protected void renameChatTab(int index, String name) {
		chatTabs.get(index).setTabName(name);
		tabs.setTabTitle(index, name);
	}

	protected void deleteChatTab(int index) {
		chatTabs.remove(index);
		tabs.removeTab(index);
	}

	protected void saveChatTab() {
		final int tabIndex = tabs.getSelectedTabIndex();
		XChatBox cb = chatTabs.get(tabIndex);
		String tabName = tabs.getTabTitle(tabIndex);
		final String dir = System.getProperty("user.home");
		XFileSelector sel = XFileSelector.create(dir);
		sel.setSelectedFile(new File(dir, tabName + ".html"));
		sel.setFileSelectionMode(XFileSelector.FILES_AND_DIRECTORIES);
		sel.setDialogType(XFileSelector.SAVE_DIALOG);
		int opt = sel.showDialog(null, "Save Chat Tab '" + tabName + "'");
		if (opt == XFileSelector.APPROVE_OPTION) {
			try {
				saveChat(cb, sel.getSelectedFile());
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to save chat.", e);
			}
		}
	}

	protected void saveChat(XChatBox cb, File selectedFile) throws Exception {
		PrintWriter writer = new PrintWriter(selectedFile);
		try {
			writer.println("<html><body>");
			for (XChatBox.ChatMessage cm : cb.getChatMessage()) {
				writer.print("<p style=\"color: ");
				ChatChannel channel = cm.getChannel();
				final ColorRGBA colorForCommand = cb.getColorForCommand(cm.getChannel().getCommand());
				writer.print(UIUtil.toHexString(colorForCommand, false));
				writer.print(";\">");
				writer.print(StringEscapeUtils.escapeHtml4(cm.getMsg()));
				writer.println("</p>");
			}
			writer.println("</body></html>");
		} finally {
			writer.close();
		}
	}

	private void newChatTab() {
		FancyInputBox fib = new FancyInputBox(screen, orgPosition, FancyWindow.Size.LARGE, isMovable) {
			@Override
			public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
				hideWindow();
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
				hideWindow();
				newChatTab(text);
			}
		};
		fib.setWindowTitle("New Tab");
		fib.setDestroyOnHide(true);
		fib.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
		fib.setButtonOkText("Create");
		fib.sizeToContent();
		fib.setWidth(300);
		fib.setIsResizable(false);
		fib.setIsMovable(false);
		UIUtil.center(screen, fib);
		screen.addElement(fib, null, true);
		fib.showAsModal(true);
	}

	private void renameChatTab() {
		FancyInputBox fib = new FancyInputBox(screen, orgPosition, FancyWindow.Size.LARGE, isMovable) {
			@Override
			public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
				hideWindow();
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
				hideWindow();
				renameChatTab(tabs.getSelectedTabIndex(), text);
			}
		};
		fib.setWindowTitle("Rename Tab");
		fib.setDestroyOnHide(true);
		fib.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
		fib.setButtonOkText("Rename");
		fib.setMsg(tabs.getTabTitle(tabs.getSelectedTabIndex()));
		fib.sizeToContent();
		fib.setWidth(300);
		fib.setIsResizable(false);
		fib.setIsMovable(false);
		UIUtil.center(screen, fib);
		screen.addElement(fib, null, true);
		fib.showAsModal(true);
	}

	private void configureFilters() {
		chatTabs.get(tabs.getSelectedTabIndex()).showChatFiltersWindow();
	}

	private void deleteChatTab() {
		final FancyDialogBox dialog = new FancyDialogBox(screen, new Vector2f(15, 15), Size.LARGE, true) {
			@Override
			public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
				hideWindow();
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
				deleteChatTab(tabs.getSelectedTabIndex());
				hideWindow();
			}
		};
		dialog.setDestroyOnHide(true);
		dialog.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
		dialog.setWindowTitle("Delete Tab");
		dialog.setButtonOkText("Delete Tab");
		dialog.setMsg("Are you sure? This chat tab, it's content and all of it's filter settings will be removed permanently.");
		dialog.sizeToContent();
		dialog.setIsResizable(false);
		dialog.setIsMovable(false);
		UIUtil.center(screen, dialog);
		screen.addElement(dialog, null, true);
		dialog.showAsModal(true);
	}

	private void showChatTabMenu(float x, float y) {
		ZMenu subMenu = new ZMenu(screen) {

			@Override
			protected void onItemSelected(ZMenu.ZMenuItem item) {
				switch ((TabMenuOption) item.getValue()) {
				case SAVE_TAB:
					saveChatTab();
					break;
				case ADD_TAB:
					newChatTab();
					break;
				case RENAME_TAB:
					renameChatTab();
					break;
				case DELETE_TAB:
					deleteChatTab();
					break;
				case CONFIGURE_FILTERS:
					configureFilters();
					break;
				}
			}

			@Override
			public void controlHideHook() {
				super.controlHideHook();
				onHideTabContextMenu();

			}

			@Override
			public void controlShowHook() {
				super.controlShowHook();
				onShowTabContextMenu();
			}
		};

		subMenu.addMenuItem("Add new tab", TabMenuOption.ADD_TAB);
		int tabIndex = tabs.getSelectedTabIndex();
		if (tabIndex > 0) {
			subMenu.addMenuItem("Delete tab", TabMenuOption.DELETE_TAB);
			subMenu.addMenuItem("Rename tab", TabMenuOption.RENAME_TAB);
		}
		subMenu.addMenuItem("Configure Filters", TabMenuOption.CONFIGURE_FILTERS);
		subMenu.addMenuItem("Save Chat", TabMenuOption.SAVE_TAB);

		// Show menu
		screen.addElement(subMenu);
		subMenu.showMenu(null, x, y);
		// screen.updateZOrder(subMenu);
	}

	public String getChatFont() {
		return chatFont;
	}
}
