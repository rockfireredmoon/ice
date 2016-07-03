package org.icenet.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.apache.commons.lang.SystemUtils;
import org.icenet.client.GameServer.Access;

import com.google.gson.Gson;

public class ServerListManager {

	private final static Logger LOG = Logger.getLogger(ServerListManager.class.getName());
	public final static String SERVER_LIST_URL = System.getProperty("icenet.serverListUrl",
			"http://files.theanubianwar.com/servers/list.json");

	private ServerList serverList;

	public ServerListManager() {
	}

	public synchronized void unload() {
		serverList = null;
	}

	public ServerList getServers() {
		if (serverList == null)
			throw new IllegalStateException("Not loaded.");
		return serverList;
	}

	public boolean isLoaded() {
		return serverList != null;
	}

	boolean isPortInUse(InetAddress addr, int port) throws IOException {
		try {
			ServerSocket ss = new ServerSocket(port, 1, addr);
			ss.close();
			return false;
		} catch (BindException be) {
			return true;
		}

	}

	public synchronized void load() throws IOException {

		// Look for some local servers
		IOException error = null;

		URL replyUrl = new URL(SERVER_LIST_URL);
		HttpURLConnection connection = (HttpURLConnection) replyUrl.openConnection();
		connection.setDoInput(true);
		connection.setRequestProperty("User-Agent", Client.HTTP_USER_AGENT);
		if (connection.getResponseCode() == 200) {
			Gson gson = new Gson();
			serverList = gson.fromJson(new InputStreamReader(connection.getInputStream()), ServerList.class);
			serverList.setUrl(replyUrl.toExternalForm());
		} else {
			error = new IOException(String.format("Service responded with error code %d (%s)",
					connection.getResponseCode(), connection.getResponseMessage()));
			serverList = new ServerList();
		}

		// Add some local servers if there are any
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface nint = en.nextElement();
			for (Enumeration<InetAddress> enAddr = nint.getInetAddresses(); enAddr.hasMoreElements();) {
				InetAddress addr = enAddr.nextElement();
				LOG.info(String.format("Looking for server on %s", addr));
				try {
					if (isPortInUse(addr, 4300)) {

						GameServer gs = new GameServer();
						gs.setAccess(Access.LOCAL);
						gs.setCapacity(-1);
						String hostname = addr.getCanonicalHostName();
						gs.setDescription("Local server on " + hostname);
						gs.setName(hostname);
						gs.setOwner(System.getProperty("user.name"));
						gs.setSimulatorAddress(hostname + ":4300");

						int httpPort = 80;
						if (SystemUtils.IS_OS_UNIX && !"root".equals(System.getProperty("user.name"))) {
							httpPort = 8080;
						}
						if (isPortInUse(addr, httpPort)) {
							gs.setAssetUrl("http://" + addr.getHostName() + (httpPort == 80 ? "" : ":" + httpPort)
									+ "/iceclient-assets");
						} else {
							// Assume assets being hosted externally, pick the
							// first public server
							if (!serverList.getServers().isEmpty()) {
								GameServer first = serverList.getServers().get(0);
								gs.setAssetUrl(first.getAssetUrl());
							} else
								gs.setAssetUrl("http://assets.theanubianwar.com/iceclient-assets");
						}
						serverList.getServers().add(gs);
					}
				} catch (Exception ioe) {
				}
			}
		}

		if (serverList.getServers().isEmpty() && error != null)
			throw error;

	}
}
