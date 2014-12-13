package org.uberfire.ext.plugin.event;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.rpc.SessionInfo;

@Portable
public class PluginAdded extends BaseNewPlugin {

    public PluginAdded() {
    }

    public PluginAdded( final Plugin plugin,
                        final SessionInfo sessionInfo ) {
        super( plugin, sessionInfo );
    }
}
