package org.kie.workbench.common.screens.server.management.backend;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.kie.workbench.common.screens.server.management.service.ContainerAlreadyRegisteredException;
import org.kie.workbench.common.screens.server.management.service.ContainerNotFoundException;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class ServerReferenceStorageImpl {

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    private XStream xs = new XStream();

    public boolean exists( final ServerRef serverRef ) {
        return ioService.exists( buildPath( serverRef ) );
    }

    public void forceRegister( final ServerRef serverRef ) {
        final Path path = buildPath( serverRef );
        ioService.write( path, xs.toXML( serverRef ) );
    }

    public void register( final ServerRef serverRef ) {
        if ( !exists( serverRef ) ) {
            final Path path = buildPath( serverRef );
            ioService.write( path, xs.toXML( serverRef ) );
        } else {
            throw new RuntimeException();
        }
    }

    public Collection<ServerRef> listRegisteredServers() {
        final Collection<ServerRef> result = new ArrayList<ServerRef>();
        final Path dir = buildPath( (String) null );

        try {
            for ( final Path registeredServer : ioService.newDirectoryStream( dir ) ) {
                try {
                    result.add( restoreConfig( registeredServer ) );
                } catch ( final Exception ignore ) {
                    ioService.delete( registeredServer );
                }
            }
        } catch ( final NotDirectoryException ignore ) {
        }

        return result;
    }

    private ServerRef restoreConfig( final Path registeredServer ) {
        try {
            final ServerRef serverRef = (ServerRef) xs.fromXML( ioService.readAllString( registeredServer ) );
            return new ServerRefImpl( serverRef.getId(), serverRef.getName(), serverRef.getUsername(), serverRef.getPassword(), ContainerStatus.LOADING, serverRef.getConnectionType(), serverRef.getProperties(), serverRef.getContainersRef() );
        } catch ( Exception ex ) {
        }
        return null;
    }

    public void unregister( final ServerRef serverRef ) {
        ioService.delete( buildPath( serverRef ) );
    }

    private Path buildPath( final ServerRef serverRef ) {
        if ( serverRef == null ) {
            return buildPath( (String) null );
        }
        return buildPath( serverRef.getId() );
    }

    private Path buildPath( final String endpoint ) {
        if ( endpoint != null ) {
            return fileSystem.getPath( "servers", "remote", toHex( endpoint ) + ".xml" );
        } else {
            return fileSystem.getPath( "servers", "remote" );
        }
    }

    public String toHex( final String arg ) {
        return String.format( "%x", new BigInteger( 1, arg.toLowerCase().getBytes( Charset.forName( "UTF-8" ) ) ) );
    }

    public void createContainer( final ContainerRef containerRef ) {
        final Path path = buildPath( containerRef.getServerId() );
        final ServerRef serverRef = loadServerRef( containerRef.getServerId() );
        if ( serverRef != null ) {
            if ( serverRef.hasContainerRef( containerRef.getId() ) ) {
                throw new ContainerAlreadyRegisteredException( containerRef.getId() );
            }
            serverRef.addContainerRef( containerRef );
            ioService.write( path, xs.toXML( serverRef ) );
        }
    }

    public ServerRef loadServerRef( String serverId ) {
        final Path path = buildPath( serverId );
        return restoreConfig( path );
    }

    public void deleteContainer( String serverId,
                                 String containerId ) {
        final Path path = buildPath( serverId );
        final ServerRef serverRef = loadServerRef( serverId );
        if ( serverRef != null ) {
            serverRef.deleteContainer( containerId );
            ioService.write( path, xs.toXML( serverRef ) );
        }

    }

    public void updateContainer( String serverId,
                                 String containerId,
                                 GAV releaseId ) {
        final Path path = buildPath( serverId );
        final ServerRef serverRef = loadServerRef( serverId );
        if ( serverRef != null ) {
            final ContainerRef containerRef = serverRef.getContainerRef( containerId );
            if ( containerRef == null ) {
                throw new ContainerNotFoundException( containerId );
            }

            serverRef.deleteContainer( containerId );

            serverRef.addContainerRef( new ContainerRefImpl( serverId, containerId, containerRef.getStatus(), releaseId, containerRef.getScannerStatus() ) );
            ioService.write( path, xs.toXML( serverRef ) );
        }
    }
}
