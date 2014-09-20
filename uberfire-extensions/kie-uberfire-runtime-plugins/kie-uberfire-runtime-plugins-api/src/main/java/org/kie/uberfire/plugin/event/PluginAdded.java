package org.kie.uberfire.plugin.event;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.plugin.model.Plugin;
import org.uberfire.rpc.SessionInfo;

@Portable
public class PluginAdded {

    private Plugin plugin;
    private SessionInfo sessionInfo;

    public PluginAdded() {
    }

    public PluginAdded( Plugin plugin,
                        SessionInfo sessionInfo ) {
        this.plugin = plugin;
        this.sessionInfo = sessionInfo;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
}
