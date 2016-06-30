package org.icenet;

import java.util.Arrays;
import java.util.List;

/**
 * Generic "Query" Message sent from client to server in Game mode.
 */
public class GameQueryMessage extends AbstractQueryMessage {
    
    public GameQueryMessage(String query, String... strings) {
        super(MSG_GAME_QUERY, query, Arrays.asList(strings));
    }

    public GameQueryMessage(String query, List<String> strings) {
        super(MSG_GAME_QUERY, query, strings);
    }
    

}
