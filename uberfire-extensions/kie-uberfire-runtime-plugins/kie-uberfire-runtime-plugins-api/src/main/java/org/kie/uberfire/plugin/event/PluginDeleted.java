package org.kie.uberfire.plugin.event;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.plugin.model.PluginType;
import org.uberfire.rpc.SessionInfo;

@Portable
public class PluginDeleted extends BasePluginEvent {

    public PluginDeleted() {
    }

    public PluginDeleted( String pluginName,
                          PluginType type,
                          SessionInfo sessionInfo ) {
        super( pluginName, type, sessionInfo );
    }
}