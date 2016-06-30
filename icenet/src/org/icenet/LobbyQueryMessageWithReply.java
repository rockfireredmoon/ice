package org.icenet;

import java.util.List;

public class LobbyQueryMessageWithReply extends LobbyQueryMessage {

    public LobbyQueryMessageWithReply(String query, String... strings) {
        super(query, strings);
        setWantsReply(true);
    }

    public LobbyQueryMessageWithReply(String query, List<String> strings) {
        super(query, strings);
        setWantsReply(true);
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof QueryReplyMessage && ((QueryReplyMessage) msg).getId() == getId();
    }
}
