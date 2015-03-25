package org.kie.workbench.common.screens.server.management.backend;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.server.management.events.ContainerCreated;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.kie.workbench.common.screens.server.management.service.ContainerAlreadyRegisteredException;
import org.kie.workbench.common.screens.server.management.service.ServerAlreadyRegisteredException;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.screens.server.management.model.ConnectionType.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class ServerManagementServiceImpl implements ServerManagementService {

    @Inject
    private Event<ServerConnected> serverConnectedEvent;

    @Inject
    private Event<ServerOnError> serverOnErrorEvent;

    @Inject
    private Event<ServerDeleted> serverDeletedEvent;

    @Inject
    private Event<ContainerCreated> containerCreatedEvent;

    @Inject
    private Event<ContainerStarted> containerStartedEvent;

    @Inject
    private Event<ContainerStopped> containerStoppedEvent;

    @Inject
    private Event<ContainerDeleted> containerDeletedEvent;

    @Inject
    private Event<ContainerUpdated> containerUpdatedEvent;

    @Inject
    private ServerReferenceStorageImpl storage;

    @Inject
    private RemoteAccessImpl remoteAccess;

    @Override
    public Collection<ServerRef> listServers() {
        final Collection<ServerRef> result = storage.listRegisteredServers();

        for ( final ServerRef serverRef : result ) {
            SimpleAsyncExecutorService.getDefaultInstance().execute( new Runnable() {
                @Override
                public void run() {
                    try {
                        final Server server = remoteAccess.toServer( serverRef );
                        if ( server == null ) {
                            serverOnErrorEvent.fire( new ServerOnError( toError( serverRef ), "" ) );
                        } else {
                            storage.forceRegister( server );
                            serverConnectedEvent.fire( new ServerConnected( server ) );
                        }
                    } catch ( final Exception ex ) {
                        serverOnErrorEvent.fire( new ServerOnError( toError( serverRef ), "" ) );
                    }
                }
            } );
        }

        return result;
    }

    private ServerRef toError( final ServerRef serverRef ) {
        return new ServerRefImpl( serverRef.getId(), serverRef.getUrl(), serverRef.getName(), serverRef.getUsername(), serverRef.getPassword(), ContainerStatus.ERROR, serverRef.getConnectionType(), serverRef.getProperties(), serverRef.getContainersRef() );
    }

    @Override
    public void registerServer( final String endpoint,
                                final String name,
                                final String username,
                                final String password,
                                final String controllerUrl ) throws ServerAlreadyRegisteredException {
        checkNotEmpty( "endpoint", endpoint );
        checkNotEmpty( "name", name );
        checkNotEmpty( "controllerUrl", controllerUrl );

        final Server server = remoteAccess.registerServer(endpoint, name, username, password, REMOTE, controllerUrl);

        if ( storage.exists( server ) ) {
            throw new ServerAlreadyRegisteredException( "Server already registered." );
        }

        if ( server != null ) {
            storage.register( server );
            serverConnectedEvent.fire( new ServerConnected( server ) );
        }
    }

    @Override
    public void deleteOp( final Collection<String> servers2Unregister,
                          final Map<String, List<String>> containers2delete ) {
        for ( final Map.Entry<String, List<String>> stringListEntry : containers2delete.entrySet() ) {
            for ( final String containerId : stringListEntry.getValue() ) {
                deleteContainer( stringListEntry.getKey(), containerId );
            }
        }
        for ( final String server2Unregister : servers2Unregister ) {
            unregisterServer( server2Unregister );
        }
    }

    @Override
    public void startContainers( Map<String, List<String>> containers ) {
        for ( Map.Entry<String, List<String>> entry : containers.entrySet() ) {
            final ServerRef serverRef = storage.loadServerRef( entry.getKey() );
            for ( final String containerId : entry.getValue() ) {
                final ContainerRef containerRef = serverRef.getContainerRef( containerId );
                final Container container = remoteAccess.install( serverRef.getUrl(), containerRef.getId(), serverRef.getUsername(), serverRef.getPassword(), containerRef.getReleasedId() );
                containerStartedEvent.fire( new ContainerStarted( container ) );
            }
        }
    }

    @Override
    public void stopContainers( Map<String, List<String>> containers ) {
        for ( Map.Entry<String, List<String>> entry : containers.entrySet() ) {
            final ServerRef serverRef = storage.loadServerRef( entry.getKey() );
            for ( final String containerId : entry.getValue() ) {
                final ContainerRef containerRef = serverRef.getContainerRef( containerId );
                final ContainerRef newContainerRef = new ContainerRefImpl( containerRef.getServerId(), containerRef.getId(), ContainerStatus.STOPPED, containerRef.getReleasedId(), containerRef.getScannerStatus(), containerRef.getPollInterval() );
                serverRef.deleteContainer( containerRef.getId() );
                serverRef.addContainerRef( newContainerRef );
                storage.forceRegister( serverRef );
                remoteAccess.deleteContainer( serverRef.getUrl(), containerRef.getId(), serverRef.getUsername(), serverRef.getPassword() );
                containerStoppedEvent.fire( new ContainerStopped( newContainerRef ) );
            }
        }
    }

    private void deleteContainer( final String serverId,
                                  final String containerId ) {
        final ServerRef serverRef = storage.loadServerRef( serverId );
        storage.deleteContainer( serverId, containerId );
        remoteAccess.deleteContainer( serverRef.getUrl(), containerId, serverRef.getUsername(), serverRef.getPassword() );
        containerDeletedEvent.fire( new ContainerDeleted( serverId, containerId ) );
    }

    @Override
    public void createContainer( final String serverId,
                                 final String containerId,
                                 final GAV gav ) {
        final ServerRef serverRef = storage.loadServerRef( serverId );
        try {
            if (remoteAccess.containerExists(serverRef.getUrl(), containerId, serverRef.getUsername(), serverRef.getPassword())) {
                throw new ContainerAlreadyRegisteredException(containerId);
            }
        } catch (ContainerAlreadyRegisteredException e) {
            throw e;
        } catch (Exception e) {
            // log only to support offline mode
        }
        final ContainerRef containerRef = new ContainerRefImpl( serverId, containerId, ContainerStatus.STOPPED, gav, null, null );
        storage.createContainer( containerRef );
        containerCreatedEvent.fire( new ContainerCreated( containerRef ) );
    }

    @Override
    public void refresh() {
        listServers();
    }

    @Override
    public Container getContainerInfo( final String serverId,
                                       final String container ) {
        final ServerRef serverRef = storage.loadServerRef( serverId );

        final Pair<Boolean, Container> result = remoteAccess.getContainer( serverRef.getUrl(), container, serverRef.getUsername(), serverRef.getPassword() );
        if ( result.getK2() != null ) {
            return result.getK2();
        }

        final ContainerRef containerRef = storage.loadServerRef( serverId ).getContainerRef( container );

        return new ContainerImpl( containerRef.getServerId(), containerRef.getId(), result.getK1() ? containerRef.getStatus() : ContainerStatus.ERROR, containerRef.getReleasedId(), null, containerRef.getPollInterval(), null );
    }

    @Override
    public ScannerOperationResult scanNow( final String serverId,
                                           final String containerId ) {
        final ServerRef serverRef = storage.loadServerRef( serverId );

        final ScannerOperationResult resource = remoteAccess.scanNow( serverRef.getUrl(), containerId, serverRef.getUsername(), serverRef.getPassword() );

        if ( resource != null && resource.getScannerStatus().equals( ScannerStatus.SCANNING ) ) {
            refresh();
        }

        return resource;
    }

    @Override
    public ScannerOperationResult startScanner( final String serverId,
                                                final String containerId,
                                                final long interval ) {
        final ServerRef serverRef = storage.loadServerRef( serverId );

        storage.updateContainer( serverId, containerId, interval );

        return remoteAccess.startScanner( serverRef.getUrl(), containerId, serverRef.getUsername(), serverRef.getPassword(), interval );
    }

    @Override
    public ScannerOperationResult stopScanner( String serverId,
                                               String containerId ) {
        final ServerRef serverRef = storage.loadServerRef( serverId );

        return remoteAccess.stopScanner( serverRef.getUrl(), containerId, serverRef.getUsername(), serverRef.getPassword() );
    }

    @Override
    public void upgradeContainer( final String serverId,
                                  final String containerId,
                                  final GAV releaseId ) {

        final ServerRef serverRef = storage.loadServerRef( serverId );
        try {
            remoteAccess.upgradeContainer( serverRef.getUrl(), containerId, serverRef.getUsername(), serverRef.getPassword(), releaseId );
            storage.updateContainer( serverId, containerId, releaseId );
        } finally {
            containerUpdatedEvent.fire( new ContainerUpdated( getContainerInfo( serverId, containerId ) ) );
        }
    }

    private void unregisterServer( final String id ) {
        storage.unregister( new ServerRefImpl( id, "", "--none--", null, null, null, null, null, null ) );
        serverDeletedEvent.fire( new ServerDeleted( id ) );
    }
}
