package org.iceui;

public class ChatChannel {

    private String UID;
    private String name;
    private String filterDisplayText;
    private Object command;
    private boolean visibleToUser;
    private boolean isFiltered = false;

    public ChatChannel(String UID, String name, Object command, String filterDisplayText, boolean visibleToUser) {
        this.UID = UID;
        this.name = name;
        this.command = command;
        this.filterDisplayText = filterDisplayText;
        this.visibleToUser = visibleToUser;
    }

    public String getUID() {
        return this.UID;
    }

    public String getName() {
        return this.name;
    }

    public Object getCommand() {
        return this.command;
    }

    public boolean getVisibleToUser() {
        return visibleToUser;
    }

    public void setIsFiltered(boolean isFiltered) {
        this.isFiltered = isFiltered;
    }

    public boolean getIsFiltered() {
        return this.isFiltered;
    }

    public String getFilterDisplayText() {
        return filterDisplayText;
    }
}
