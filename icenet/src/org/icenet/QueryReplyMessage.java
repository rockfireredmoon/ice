package org.icenet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Error message sent from server to client (e.g. login error).
 */
public class QueryReplyMessage extends SimulatorMessage {
    

    public class Reply {

        private final List<String> strings = new ArrayList<String>();
        private int replyNumber;

        public Reply(int replyNumber) {
            this.replyNumber = replyNumber;
        }

        public List<String> getStrings() {
            return strings;
        }

        public int getReplyNumber() {
            return replyNumber;
        }

        @Override
        public String toString() {
            return "Reply{" + "strings=" + strings + '}';
        }
    }

    private static final Logger LOG = Logger.getLogger(QueryReplyMessage.class.getName());
    private final int seq;
    private String errorMessage;
    private List<Reply> replies = new ArrayList<Reply>();

    public QueryReplyMessage(SimulatorMessage msg) {
        super(msg);
        setValidForProtocol(null);
        payload.rewind();

        seq = payload.getInt();
        int records = payload.getShort();

        // TODO Weird ... is this a magic value?
        if (records == 0x7000) {
            errorMessage = readString();
        } else {

            for (int i = 0; i < records; i++) {
                byte numberOfStrings = payload.get();
                Reply reply = new Reply(i);
                for (int j = 0; j < numberOfStrings; j++) {
                    reply.getStrings().add(readString());
                }
                replies.add(reply);
            }
        }

    }
    
    public boolean isError() {
        return errorMessage != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getId() {
        return seq;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    @Override
    public String toString() {
        return "QueryReplyMessage{" + "seq=" + seq + ", replies=" + replies + '}';
    }
}
