package org.uberfire.java.nio.fs.jgit.util;

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
