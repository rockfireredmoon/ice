package org.icenet;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class Router {

    private static final Logger LOG = Logger.getLogger(Router.class.getName());
    private final URI routerUrl;
    private int routerPort = NetConstants.DEFAULT_ROUTER_PORT;
    private int simulatorPort = -1;
    private String simulatorHost = null;

    public Router(URI routerUrl) {
        this.routerUrl = routerUrl;

        String q = routerUrl.getQuery();
        if (q != null) {
            for (String arg : q.split("\\&")) {
                String[] pair = arg.split("=");
                if (pair[0].equals("routerPort") && pair.length > 1) {
                    routerPort = Integer.parseInt(pair[1]);
                }
            }
        }
    }

    public URI getRouterUrl() {
        return routerUrl;
    }

    public int getRouterPort() {
        return routerPort;
    }

    public int getSimulatorPort() {
        return simulatorPort;
    }

    public String getSimulatorHost() {
        return simulatorHost;
    }

    public void connectToRouter() {
        LOG.info(String.format("Connecting to router at %s:%d", routerUrl.getHost(), routerPort));
        try {
            try (Socket s = new Socket(routerUrl.getHost(), routerPort)) {
                byte[] buf = IOUtils.toByteArray(s.getInputStream());
                final String route = new String(buf);
                int idx = route.indexOf(':');
                simulatorHost = route.substring(0, idx);
                simulatorPort = Integer.parseInt(route.substring(idx + 1));
            }
        } catch (IOException ioe) {
            throw new NetworkException(NetworkException.ErrorType.FAILED_TO_CONNECT_TO_ROUTER, "Failed to connect to router.", ioe);
        }
    }
}
