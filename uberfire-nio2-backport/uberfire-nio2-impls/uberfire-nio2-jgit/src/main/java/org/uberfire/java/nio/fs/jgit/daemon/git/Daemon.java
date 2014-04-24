package org.uberfire.java.nio.fs.jgit.daemon.git;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.zip.Deflater;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.transport.resolver.UploadPackFactory;

/**
 * Basic daemon for the anonymous <code>git://</code> transport protocol.
 */
public class Daemon {

    /**
     * 9418: IANA assigned port number for Git.
     */
    public static final int DEFAULT_PORT = 9418;

    private static final int BACKLOG = 5;

    private InetSocketAddress myAddress;

    private final DaemonService[] services;

    private final ThreadGroup processors;

    private boolean run;

    private Thread acceptThread;

    private int timeout;

    private volatile RepositoryResolver<DaemonClient> repositoryResolver;

    private volatile UploadPackFactory<DaemonClient> uploadPackFactory;

    private ServerSocket listenSock = null;

    /**
     * Configure a daemon to listen on any available network port.
     */
    public Daemon() {
        this( null );
    }

    /**
     * Configure a new daemon for the specified network address.
     * @param addr address to listen for connections on. If null, any available
     * port will be chosen on all network interfaces.
     */
    public Daemon( final InetSocketAddress addr ) {
        myAddress = addr;
        processors = new ThreadGroup( "Git-Daemon" );

        repositoryResolver = (RepositoryResolver<DaemonClient>) RepositoryResolver.NONE;

        uploadPackFactory = new UploadPackFactory<DaemonClient>() {
            public UploadPack create( DaemonClient req,
                                      Repository db )
                    throws ServiceNotEnabledException,
                    ServiceNotAuthorizedException {
                final UploadPack up = new UploadPack( db );
                up.setTimeout( getTimeout() );

                final PackConfig config = new PackConfig( db );
                config.setCompressionLevel( Deflater.BEST_COMPRESSION );
                up.setPackConfig( config );

                return up;
            }
        };

        services = new DaemonService[]{ new DaemonService( "upload-pack", "uploadpack" ) {
            {
                setEnabled( true );
            }

            @Override
            protected void execute( final DaemonClient dc,
                                    final Repository db ) throws IOException,
                    ServiceNotEnabledException,
                    ServiceNotAuthorizedException {
                UploadPack up = uploadPackFactory.create( dc, db );
                InputStream in = dc.getInputStream();
                OutputStream out = dc.getOutputStream();
                up.upload( in, out, null );
            }
        } };
    }

    /**
     * @return the address connections are received on.
     */
    public synchronized InetSocketAddress getAddress() {
        return myAddress;
    }

    /**
     * Lookup a supported service so it can be reconfigured.
     * @param name name of the service; e.g. "receive-pack"/"git-receive-pack" or
     * "upload-pack"/"git-upload-pack".
     * @return the service; null if this daemon implementation doesn't support
     * the requested service type.
     */
    public synchronized DaemonService getService( String name ) {
        if ( !name.startsWith( "git-" ) ) {
            name = "git-" + name;
        }
        for ( final DaemonService s : services ) {
            if ( s.getCommandName().equals( name ) ) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return timeout (in seconds) before aborting an IO operation.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the timeout before willing to abort an IO call.
     * @param seconds number of seconds to wait (with no data transfer occurring)
     * before aborting an IO read or write operation with the
     * connected client.
     */
    public void setTimeout( final int seconds ) {
        timeout = seconds;
    }

    /**
     * Set the resolver used to locate a repository by name.
     * @param resolver the resolver instance.
     */
    public void setRepositoryResolver( RepositoryResolver<DaemonClient> resolver ) {
        repositoryResolver = resolver;
    }

    /**
     * Set the factory to construct and configure per-request UploadPack.
     * @param factory the factory. If null upload-pack is disabled.
     */
    @SuppressWarnings("unchecked")
    public void setUploadPackFactory( UploadPackFactory<DaemonClient> factory ) {
        if ( factory != null ) {
            uploadPackFactory = factory;
        } else {
            uploadPackFactory = (UploadPackFactory<DaemonClient>) UploadPackFactory.DISABLED;
        }
    }

    /**
     * Start this daemon on a background thread.
     * @throws IOException the server socket could not be opened.
     * @throws IllegalStateException the daemon is already running.
     */
    public synchronized void start() throws IOException {
        if ( acceptThread != null ) {
            throw new IllegalStateException( JGitText.get().daemonAlreadyRunning );
        }

        this.listenSock = new ServerSocket(
                myAddress != null ? myAddress.getPort() : 0, BACKLOG,
                myAddress != null ? myAddress.getAddress() : null );
        myAddress = (InetSocketAddress) listenSock.getLocalSocketAddress();

        run = true;
        acceptThread = new Thread( processors, "Git-Daemon-Accept" ) {
            public void run() {
                while ( isRunning() ) {
                    try {
                        startClient( listenSock.accept() );
                    } catch ( InterruptedIOException e ) {
                        // Test again to see if we should keep accepting.
                    } catch ( IOException e ) {
                        break;
                    }
                }

                try {
                    if ( !listenSock.isClosed() ) {
                        listenSock.close();
                    }
                } catch ( IOException err ) {
                    //
                } finally {
                    synchronized ( Daemon.this ) {
                        acceptThread = null;
                    }
                }
            }
        };
        acceptThread.start();
    }

    /**
     * @return true if this daemon is receiving connections.
     */
    public synchronized boolean isRunning() {
        return run;
    }

    /**
     * Stop this daemon.
     */
    public synchronized void stop() {
        if ( acceptThread != null ) {
            run = false;
            try {
                listenSock.close();
            } catch ( IOException e ) {
            }
            acceptThread.interrupt();
        }
    }

    private void startClient( final Socket s ) {
        final DaemonClient dc = new DaemonClient( this );

        final SocketAddress peer = s.getRemoteSocketAddress();
        if ( peer instanceof InetSocketAddress ) {
            dc.setRemoteAddress( ( (InetSocketAddress) peer ).getAddress() );
        }

        new Thread( processors, "Git-Daemon-Client " + peer.toString() ) {
            public void run() {
                try {
                    dc.execute( s );
                } catch ( ServiceNotEnabledException e ) {
                    // Ignored. Client cannot use this repository.
                } catch ( ServiceNotAuthorizedException e ) {
                    // Ignored. Client cannot use this repository.
                } catch ( IOException e ) {
                    // Ignore unexpected IO exceptions from clients
                } finally {
                    try {
                        s.getInputStream().close();
                    } catch ( IOException e ) {
                        // Ignore close exceptions
                    }
                    try {
                        s.getOutputStream().close();
                    } catch ( IOException e ) {
                        // Ignore close exceptions
                    }
                }
            }
        }.start();
    }

    synchronized DaemonService matchService( final String cmd ) {
        for ( final DaemonService d : services ) {
            if ( d.handles( cmd ) ) {
                return d;
            }
        }
        return null;
    }

    Repository openRepository( DaemonClient client,
                               String name )
            throws ServiceMayNotContinueException {
        // Assume any attempt to use \ was by a Windows client
        // and correct to the more typical / used in Git URIs.
        //
        name = name.replace( '\\', '/' );

        // git://thishost/path should always be name="/path" here
        //
        if ( !name.startsWith( "/" ) ) {
            return null;
        }

        try {
            return repositoryResolver.open( client, name.substring( 1 ) );
        } catch ( RepositoryNotFoundException e ) {
            // null signals it "wasn't found", which is all that is suitable
            // for the remote client to know.
            return null;
        } catch ( ServiceNotAuthorizedException e ) {
            // null signals it "wasn't found", which is all that is suitable
            // for the remote client to know.
            return null;
        } catch ( ServiceNotEnabledException e ) {
            // null signals it "wasn't found", which is all that is suitable
            // for the remote client to know.
            return null;
        }
    }
}
