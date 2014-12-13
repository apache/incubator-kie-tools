package org.uberfire.ext.plugin.event;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class PluginRenamed extends BaseNewPlugin {

    private String oldPluginName;

    public PluginRenamed() {
    }

    public PluginRenamed( final String oldPluginName,
                          final Plugin plugin,
                          final SessionInfo sessionInfo ) {
        super( plugin, sessionInfo );
        this.oldPluginName = checkNotEmpty( "oldPluginName", oldPluginName );
    }

    public String getOldPluginName() {
        return oldPluginName;
    }
}