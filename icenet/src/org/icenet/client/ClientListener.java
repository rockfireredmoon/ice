package org.icenet.client;

import org.icelib.ChannelType;
import org.icelib.SceneryItem;
import org.icelib.Zone;

public interface ClientListener {
    void inventoryUpdate();
    
    void zoneChanged(Zone zone);

    void emote(long id, String sender, String emote);

    void disconnected(Exception e);

    void spawned(Spawn spawn);

    void chatMessage(String sender, String recipient, ChannelType channel, String text);

    void message(String message);
    
    void propAddedOrUpdated(SceneryItem prop);
    
    void propDeleted(SceneryItem prop);
    
    void playerLoggedIn(String name);

	void statusChanged(String name, String status);

	void shardChanged(String name, String status);

	void friendAdded(String name);

	void playerLoggedOut(String name);
	
	void popup(String message);
}
