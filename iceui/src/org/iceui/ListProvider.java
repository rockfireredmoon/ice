package org.iceui;

import java.util.Collections;
import java.util.List;

public interface ListProvider {    
    public static ListProvider EMPTY = new ListProvider() {
        public List<String> items() {
            return Collections.emptyList();
        }
    };
    
    List<String> items();
}
