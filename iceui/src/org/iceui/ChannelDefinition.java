package org.iceui;

public class ChannelDefinition {

    private String UID;
    private String name;
    private Object command;
    private String filterDisplayText;
    private boolean visibleToUser;

    public ChannelDefinition(String UID, String name, Object command, String filterDisplayText, boolean visibleToUser) {
        this.UID = UID;
        this.name = name;
        this.command = command;
        this.filterDisplayText = filterDisplayText;
        this.visibleToUser = visibleToUser;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public Object getCommand() {
        return command;
    }

    public String getFilterDisplayText() {
        return filterDisplayText;
    }

    public boolean isVisibleToUser() {
        return visibleToUser;
    }


    public ChatChannel createChannel() {
        return new ChatChannel(UID, name, command, filterDisplayText, visibleToUser);
    }
}
