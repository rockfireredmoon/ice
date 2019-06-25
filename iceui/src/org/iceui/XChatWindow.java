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
import org.iceui.controls.ElementStyle;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.containers.TabControl;
import icetone.controls.containers.TabControl.TabButton;
import icetone.controls.menuing.Menu;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.util.ExtrasUtil;
import icetone.extras.windows.DialogBox;
import icetone.extras.windows.InputBox;
import icetone.extras.windows.PersistentPanel;
import icetone.extras.windows.SaveType;

public class XChatWindow extends PersistentPanel {

	private static final Logger LOG = Logger.getLogger(XChatWindow.class.getName());

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

	class XChatTabs extends TabControl {

		XChatTabs(BaseScreen screen, String uid) {
			super(screen);
			setIgnoreMouse(true);
			getSlider().setIgnoreMouse(true);
		}

		@Override
		public BaseElement attachElement(BaseElement child) {
			super.attachElement(child);
			if (child instanceof TabPanel) {
				child.setIgnoreMouse(true);
			}
			return this;
		}
	}

	private final TabControl tabs;
	private final List<XChatBox> chatTabs = new ArrayList<XChatBox>();
	private int sendKey = -1;
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

	public XChatWindow(String configKey, BaseScreen screen, Preferences pref) {
		super(screen, configKey, 0, VAlign.Bottom, Align.Left, null, SaveType.POSITION_AND_SIZE, pref);
		setLayoutManager(new MigLayout(screen, "", "[grow, fill]", "[grow, fill]"));
		setIgnoreMouse(false);
		tabs = new XChatTabs(screen, getStyleId() + ":tabs");
		tabs.setUseSlideEffect(true);
		addTab("Default");
		addElement(tabs);
		onMouseReleased(evt -> {
			showChatTabMenu(evt.getX(), evt.getY());
		}, MouseUIButtonEvent.RIGHT);
		boundsSet = false;
	}

	public void receiveMsg(String sender, String recipient, ChannelType channel, String text) {
		for (XChatBox t : chatTabs) {
			t.receiveMsg(sender, recipient, channel, text);
		}
	}

	public XChatBox getChatTab(int index) {
		return chatTabs.get(index);
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

	public final void addChatChannel(String UID, String name, Object command, String filterDisplayText,
			boolean visibleToUser) {
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
		if (sendKey != -1) {
			tab.setSendKey(sendKey);
		}
		if (yourName != null) {
			tab.setYourName(yourName);
		}
		TabButton tabButton = new TabButton(screen, tab.getTabName());
		tabs.addTab(tabButton, tab);
		tabButton.onMouseReleased(evt -> {
			tabs.setSelectedTabIndex(chatTabs.indexOf(tab));
			showChatTabMenu(evt.getX(), evt.getY());

		}, MouseUIButtonEvent.RIGHT);
		chatTabs.add(tab);
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
				final ColorRGBA colorForCommand = cb.getColorForCommand(cm.getChannel().getCommand());
				writer.print(ExtrasUtil.toHexString(colorForCommand, false));
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
		InputBox fib = new InputBox(screen, Vector2f.ZERO, false) {
			{
				setStyleClass("large");
			}

			@Override
			public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
				hide();
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
				hide();
				newChatTab(text);
			}
		};
		fib.setWindowTitle("New Tab");
		fib.setDestroyOnHide(true);
		ElementStyle.warningColor(fib.getDragBar());
		fib.setButtonOkText("Create");
		fib.setResizable(false);
		fib.setMovable(false);
		fib.setModal(true);
		screen.showElement(fib, ScreenLayoutConstraints.center);
	}

	private void renameChatTab() {
		InputBox fib = new InputBox(screen, Vector2f.ZERO, false) {
			{
				setStyleClass("large");
			}

			@Override
			public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
				hide();
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
				hide();
				renameChatTab(tabs.getSelectedTabIndex(), text);
			}
		};
		fib.setWindowTitle("Rename Tab");
		fib.setDestroyOnHide(true);
		ElementStyle.warningColor(fib.getDragBar());
		fib.setButtonOkText("Rename");
		fib.setText(tabs.getTabTitle(tabs.getSelectedTabIndex()));
		fib.setResizable(false);
		fib.setMovable(false);
		fib.setModal(true);
		screen.showElement(fib, ScreenLayoutConstraints.center);
	}

	private void configureFilters() {
		chatTabs.get(tabs.getSelectedTabIndex()).showChatFiltersWindow();
	}

	private void deleteChatTab() {
		final DialogBox dialog = new DialogBox(screen, new Vector2f(15, 15), true) {
			{
				setStyleClass("large");
			}

			@Override
			public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
				hide();
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
				deleteChatTab(tabs.getSelectedTabIndex());
				hide();
			}
		};
		dialog.setDestroyOnHide(true);
		ElementStyle.warningColor(dialog.getDragBar());
		dialog.setWindowTitle("Delete Tab");
		dialog.setButtonOkText("Delete Tab");
		dialog.setText(
				"Are you sure? This chat tab, it's content and all of it's filter settings will be removed permanently.");
		dialog.setResizable(false);
		dialog.setMovable(false);
		dialog.setModal(true);
		screen.showElement(dialog, ScreenLayoutConstraints.center);
	}

	private void showChatTabMenu(float x, float y) {
		Menu<TabMenuOption> subMenu = new Menu<TabMenuOption>(screen);
		subMenu.onElementEvent(evt -> onShowTabContextMenu(), Type.SHOWN);
		subMenu.onElementEvent(evt -> {
			onHideTabContextMenu();
		}, Type.HIDDEN);
		subMenu.onChanged((evt) -> {
			switch (evt.getNewValue().getValue()) {
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
		});

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

}
