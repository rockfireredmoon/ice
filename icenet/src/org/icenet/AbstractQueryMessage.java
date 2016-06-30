package org.icenet;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Generic "Query" Message sent from client to server.
 */
public abstract class AbstractQueryMessage extends SimulatorMessage {
    
    public static int ID = 0;
    protected final int id;
    protected final List<String> strings;
    protected final String query;
    
    public AbstractQueryMessage(byte code, String query, String... strings) {
        this(code, query, Arrays.asList(strings));
    }

    public AbstractQueryMessage(byte code, String query, List<String> strings) {
        super(code);
        setValidForProtocol(null);
        
        // Only really for debugging purposes
        this.query = query;
        this.strings = strings;
        
        payload = ByteBuffer.allocate(NetConstants.QUERY_MESSAGE_BUFFER_SIZE);
        id = ID++;
        payload.putInt(id); 
        writeString(query);
        payload.put((byte)strings.size());
        for(String q : strings) {
            writeString(q);
        }
        payload.flip();
    }
    
    public int getId() {
        return id;
    }

    public List<String> getStrings() {
        return strings;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "AbstractQueryMessage{" + "id=" + id + ", query=" + query + ", strings=" + strings + '}';
    }

}
