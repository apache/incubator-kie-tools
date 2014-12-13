package org.uberfire.ext.plugin.event;

import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.rpc.SessionInfo;

public abstract class BaseNewPlugin {

    private Plugin plugin;
    private SessionInfo sessionInfo;

    public BaseNewPlugin() {
    }

    public BaseNewPlugin( final Plugin plugin,
                          final SessionInfo sessionInfo ) {
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
