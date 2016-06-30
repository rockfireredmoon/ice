package org.icenet;

import java.util.Arrays;
import java.util.List;

/**
 * Generic "Query" Message sent from client to server in Lobby mode.
 */
public class LobbyQueryMessage extends AbstractQueryMessage {
    
    public LobbyQueryMessage(String query, String... strings) {
        super(MSG_LOBBY_QUERY, query, Arrays.asList(strings));
    }

    public LobbyQueryMessage(String query, List<String> strings) {
        super(MSG_LOBBY_QUERY, query, strings);
    }
    

}
