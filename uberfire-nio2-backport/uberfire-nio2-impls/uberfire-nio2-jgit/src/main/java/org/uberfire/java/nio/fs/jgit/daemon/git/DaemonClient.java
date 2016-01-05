/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.eclipse.jgit.transport.PacketLineIn;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.util.io.SafeBufferedOutputStream;

public class DaemonClient {

    private final Daemon daemon;

    private InetAddress peer;

    private InputStream rawIn;

    private OutputStream rawOut;

    DaemonClient( final Daemon d ) {
        daemon = d;
    }

    void setRemoteAddress( final InetAddress ia ) {
        peer = ia;
    }

    /**
     * @return the daemon which spawned this client.
     */
    public Daemon getDaemon() {
        return daemon;
    }

    /**
     * @return Internet address of the remote client.
     */
    public InetAddress getRemoteAddress() {
        return peer;
    }

    /**
     * @return input stream to read from the connected client.
     */
    public InputStream getInputStream() {
        return rawIn;
    }

    /**
     * @return output stream to send data to the connected client.
     */
    public OutputStream getOutputStream() {
        return rawOut;
    }

    void execute( final Socket sock ) throws IOException,
            ServiceNotEnabledException, ServiceNotAuthorizedException {
        rawIn = new BufferedInputStream( sock.getInputStream() );
        rawOut = new SafeBufferedOutputStream( sock.getOutputStream() );

        if ( 0 < daemon.getTimeout() ) {
            sock.setSoTimeout( daemon.getTimeout() * 1000 );
        }
        String cmd = new PacketLineIn( rawIn ).readStringRaw();
        final int nul = cmd.indexOf( '\0' );
        if ( nul >= 0 ) {
            // Newer clients hide a "host" header behind this byte.
            // Currently we don't use it for anything, so we ignore
            // this portion of the command.
            //
            cmd = cmd.substring( 0, nul );
        }

        final DaemonService srv = getDaemon().matchService( cmd );
        if ( srv == null ) {
            return;
        }
        sock.setSoTimeout( 0 );
        srv.execute( this, cmd );
    }
}
