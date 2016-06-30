package org.icenet;

import java.util.List;

public class GameQueryMessageWithReply extends GameQueryMessage {

    public GameQueryMessageWithReply(String query, String... strings) {
        super(query, strings);
        setWantsReply(true);
    }

    public GameQueryMessageWithReply(String query, List<String> strings) {
        super(query, strings);
        setWantsReply(true);
    }

    @Override
    public boolean isReply(SimulatorMessage msg) {
        return msg instanceof QueryReplyMessage && ((QueryReplyMessage) msg).getId() == getId();
    }
}
