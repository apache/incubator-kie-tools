package org.drools.guvnor.client.editors.jbpm;

import org.jboss.bpm.console.client.model.PluginInfo;
import org.jboss.bpm.console.client.model.ServerStatus;

public class ServerPlugins {

    private static ServerStatus status;

    public static void setStatus(ServerStatus s) {
        status = s;
    }

    public static ServerStatus getStatus() {
        return status;
    }

    public static boolean has(String type) {
        boolean match = false;

        if (status != null) {
            for (PluginInfo p : status.getPlugins()) {
                if (p.getType().equals(type)) {
                    match = p.isAvailable();
                    break;
                }
            }
        }
        return match;
    }
}
