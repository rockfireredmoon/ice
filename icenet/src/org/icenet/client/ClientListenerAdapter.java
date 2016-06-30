package org.icenet.client;

import org.icelib.ChannelType;
import org.icelib.SceneryItem;
import org.icelib.Zone;

public class ClientListenerAdapter implements ClientListener {

    @Override
    public void inventoryUpdate() {
    }

    @Override
    public void emote(long id, String sender, String emote) {
    }

    @Override
    public void disconnected(Exception e) {
    }

    @Override
    public void spawned(Spawn spawn) {
    }

    @Override
    public void chatMessage(String sender, String recipient, ChannelType channel, String text) {
    }

    @Override
    public void message(String message) {
    }

    @Override
    public void zoneChanged(Zone zone) {
    }

    @Override
    public void propAddedOrUpdated(SceneryItem prop) {
    }

    @Override
    public void propDeleted(SceneryItem prop) {
    }

    @Override
    public void playerLoggedIn(String name) {
    }

	@Override
	public void statusChanged(String name, String status) {
	}

	@Override
	public void shardChanged(String name, String status) {
	}

	@Override
	public void friendAdded(String name) {
	}

	@Override
	public void playerLoggedOut(String name) {
	}

	@Override
	public void popup(String message) {
	}

}
