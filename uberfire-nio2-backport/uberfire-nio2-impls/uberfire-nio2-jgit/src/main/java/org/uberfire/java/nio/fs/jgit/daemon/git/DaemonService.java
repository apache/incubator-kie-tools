/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.daemon.git;

import java.io.IOException;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Config.SectionParser;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.transport.DaemonClient;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

public abstract class DaemonService {

    private final String command;

    private final SectionParser<ServiceConfig> configKey;

    private boolean enabled;

    private boolean overridable;

    DaemonService( final String cmdName,
                   final String cfgName ) {
        command = cmdName.startsWith( "git-" ) ? cmdName : "git-" + cmdName;
        configKey = new SectionParser<ServiceConfig>() {
            public ServiceConfig parse( final Config cfg ) {
                return new ServiceConfig( DaemonService.this, cfg, cfgName );
            }
        };
        overridable = true;
    }

    private static class ServiceConfig {

        final boolean enabled;

        ServiceConfig( final DaemonService service,
                       final Config cfg,
                       final String name ) {
            enabled = cfg.getBoolean( "daemon", name, service.isEnabled() );
        }
    }

    /**
     * @return is this service enabled for invocation?
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param on true to allow this service to be used; false to deny it.
     */
    public void setEnabled( final boolean on ) {
        enabled = on;
    }

    /**
     * @return can this service be configured in the repository config file?
     */
    public boolean isOverridable() {
        return overridable;
    }

    /**
     * @param on true to permit repositories to override this service's enabled
     * state with the <code>daemon.servicename</code> config setting.
     */
    public void setOverridable( final boolean on ) {
        overridable = on;
    }

    /**
     * @return name of the command requested by clients.
     */
    public String getCommandName() {
        return command;
    }

    /**
     * Determine if this service can handle the requested command.
     * @param commandLine input line from the client.
     * @return true if this command can accept the given command line.
     */
    public boolean handles( final String commandLine ) {
        return command.length() + 1 < commandLine.length()
                && commandLine.charAt( command.length() ) == ' '
                && commandLine.startsWith( command );
    }

    void execute( final org.uberfire.java.nio.fs.jgit.daemon.git.DaemonClient client,
                  final String commandLine )
            throws IOException, ServiceNotEnabledException,
            ServiceNotAuthorizedException {
        final String name = commandLine.substring( command.length() + 1 );
        Repository db;
        try {
            db = client.getDaemon().openRepository( client, name );
        } catch ( ServiceMayNotContinueException e ) {
            // An error when opening the repo means the client is expecting a ref
            // advertisement, so use that style of error.
            PacketLineOut pktOut = new PacketLineOut( client.getOutputStream() );
            pktOut.writeString( "ERR " + e.getMessage() + "\n" );
            db = null;
        }
        if ( db == null ) {
            return;
        }
        try {
            if ( isEnabledFor( db ) ) {
                execute( client, db );
            }
        } finally {
            db.close();
        }
    }

    private boolean isEnabledFor( final Repository db ) {
        if ( isOverridable() ) {
            return db.getConfig().get( configKey ).enabled;
        }
        return isEnabled();
    }

    abstract void execute( org.uberfire.java.nio.fs.jgit.daemon.git.DaemonClient client,
                           Repository db )
            throws IOException, ServiceNotEnabledException,
            ServiceNotAuthorizedException;
}
