package org.kie.uberfire.plugin.event;

import org.kie.uberfire.plugin.model.PluginType;
import org.uberfire.rpc.SessionInfo;

public abstract class BasePluginEvent {

    private String pluginName;
    private PluginType type;
    private SessionInfo sessionInfo;

    public BasePluginEvent() {
    }

    protected BasePluginEvent( final String pluginName,
                               final PluginType type,
                               final SessionInfo sessionInfo ) {
        this.pluginName = pluginName;
        this.type = type;
        this.sessionInfo = sessionInfo;
    }

    public String getPluginName() {
        return pluginName;
    }

    public PluginType getType() {
        return type;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
}
