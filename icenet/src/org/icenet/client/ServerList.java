package org.icenet.client;

import java.util.List;

public class ServerList {

	private List<GameServer> servers;
	private String url;

	public void setUrl(String url) {
		this.url = url;
	}

	public List<GameServer> getServers() {
		return servers;
	}

	public void setServers(List<GameServer> servers) {
		this.servers = servers;
	}

	public String getUrl() {
		return url;
	}

}
