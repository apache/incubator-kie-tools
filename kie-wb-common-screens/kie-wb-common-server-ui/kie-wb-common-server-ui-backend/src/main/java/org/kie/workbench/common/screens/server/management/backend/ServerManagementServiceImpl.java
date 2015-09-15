/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.remote.common.rest.KieRemoteHttpRequestException;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerResource;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.client.KieServicesException;
import org.kie.server.controller.api.KieServerControllerAdmin;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerStatus;
import org.kie.server.controller.api.storage.KieServerControllerStorage;
import org.kie.workbench.common.screens.server.management.events.ContainerCreated;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerOnError;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerDisconnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerInstanceRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.kie.workbench.common.screens.server.management.service.ServerAlreadyRegisteredException;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.SimpleAsyncExecutorService;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class ServerManagementServiceImpl implements ServerManagementService {

    private static final Logger logger = LoggerFactory.getLogger( ServerManagementServiceImpl.class );

    private Event<ServerConnected> serverConnectedEvent;
    private Event<ServerDisconnected> serverDisconnectedEvent;
    private Event<ServerOnError> serverOnErrorEvent;
    private Event<ServerDeleted> serverDeletedEvent;
    private Event<ContainerCreated> containerCreatedEvent;
    private Event<ContainerStarted> containerStartedEvent;
    private Event<ContainerStopped> containerStoppedEvent;
    private Event<ContainerDeleted> containerDeletedEvent;
    private Event<ContainerUpdated> containerUpdatedEvent;
    private Event<ContainerOnError> containerOnErrorEvent;

    private ServerReferenceStorageImpl storage;
    private RemoteAccessImpl remoteAccess;

    private SimpleAsyncExecutorService executor;

    private KieServerControllerAdmin controllerAdmin;
    private KieServerControllerStorage controllerStorage;

    private String controllerUser = "kieserver";
    private String controllerPassword = "kieserver1!";

    //enable proxy
    public ServerManagementServiceImpl() {
    }

    @Inject
    public ServerManagementServiceImpl( final Event<ServerConnected> serverConnectedEvent,
                                        final Event<ServerOnError> serverOnErrorEvent,
                                        final Event<ServerDeleted> serverDeletedEvent,
                                        final Event<ContainerCreated> containerCreatedEvent,
                                        final Event<ContainerStarted> containerStartedEvent,
                                        final Event<ContainerStopped> containerStoppedEvent,
                                        final Event<ContainerDeleted> containerDeletedEvent,
                                        final Event<ContainerUpdated> containerUpdatedEvent,
                                        final Event<ContainerOnError> containerOnErrorEvent,
                                        final Event<ServerDisconnected> serverDisconnectedEvent,
                                        final ServerReferenceStorageImpl storage,
                                        final RemoteAccessImpl remoteAccess,
                                        final KieServerControllerAdmin controllerAdmin,
                                        final KieServerControllerStorage controllerStorage ) {
        this( serverConnectedEvent,
              serverOnErrorEvent,
              serverDeletedEvent,
              containerCreatedEvent,
              containerStartedEvent,
              containerStoppedEvent,
              containerDeletedEvent,
              containerUpdatedEvent,
              containerOnErrorEvent,
              serverDisconnectedEvent,
              storage, remoteAccess, controllerAdmin, controllerStorage,
              SimpleAsyncExecutorService.getDefaultInstance() );
    }

    public ServerManagementServiceImpl( final Event<ServerConnected> serverConnectedEvent,
                                        final Event<ServerOnError> serverOnErrorEvent,
                                        final Event<ServerDeleted> serverDeletedEvent,
                                        final Event<ContainerCreated> containerCreatedEvent,
                                        final Event<ContainerStarted> containerStartedEvent,
                                        final Event<ContainerStopped> containerStoppedEvent,
                                        final Event<ContainerDeleted> containerDeletedEvent,
                                        final Event<ContainerUpdated> containerUpdatedEvent,
                                        final Event<ContainerOnError> containerOnErrorEvent,
                                        final Event<ServerDisconnected> serverDisconnectedEvent,
                                        final ServerReferenceStorageImpl storage,
                                        final RemoteAccessImpl remoteAccess,
                                        final KieServerControllerAdmin controllerAdmin,
                                        final KieServerControllerStorage controllerStorage,
                                        final SimpleAsyncExecutorService executor ) {
        this.serverConnectedEvent = serverConnectedEvent;
        this.serverOnErrorEvent = serverOnErrorEvent;
        this.serverDeletedEvent = serverDeletedEvent;
        this.containerCreatedEvent = containerCreatedEvent;
        this.containerStartedEvent = containerStartedEvent;
        this.containerStoppedEvent = containerStoppedEvent;
        this.containerDeletedEvent = containerDeletedEvent;
        this.containerUpdatedEvent = containerUpdatedEvent;
        this.containerOnErrorEvent = containerOnErrorEvent;
        this.serverDisconnectedEvent = serverDisconnectedEvent;
        this.storage = storage;
        this.remoteAccess = remoteAccess;
        this.controllerAdmin = controllerAdmin;
        this.controllerStorage = controllerStorage;
        this.executor = executor;
    }

    @Override
    public void refresh() {
        listServers();
    }

    @Override
    public Collection<ServerRef> listServers() {
        final Collection<ServerRef> result = new ArrayList<ServerRef>();
        final List<KieServerInstance> instances = controllerAdmin.listKieServerInstances();

        for ( final KieServerInstance instance : instances ) {

            ServerRef serverRef = new ServerRefImpl(
                    instance.getIdentifier(),
                    "url",
                    instance.getName(),
                    "user",
                    "password",
                    instance.getStatus().equals( KieServerStatus.DOWN ) ? ContainerStatus.STOPPED : ContainerStatus.STARTED,
                    ConnectionType.REMOTE,
                    new HashMap<String, String>() {{
                        put( "version", instance.getVersion() );
                    }},
                    null
            );

            // prepare containers
            if ( instance.getKieServerSetup() != null && instance.getKieServerSetup().getContainers() != null ) {

                Set<KieContainerResource> containerResources = instance.getKieServerSetup().getContainers();
                for ( KieContainerResource containerResource : containerResources ) {

                    GAV gav = new GAV( containerResource.getReleaseId().getGroupId(), containerResource.getReleaseId().getArtifactId(), containerResource.getReleaseId().getVersion() );
                    ContainerRef containerRef = new ContainerRefImpl( serverRef.getId(),
                                                                      containerResource.getContainerId(),
                                                                      calculateStatus( containerResource, instance ),
                                                                      gav,
                                                                      containerResource.getScanner() == null ? null : ScannerStatus.valueOf( containerResource.getScanner().getStatus().toString() ),
                                                                      containerResource.getScanner() == null ? null : containerResource.getScanner().getPollInterval() );

                    serverRef.addContainerRef( containerRef );
                }
            }

            // prepare managed instances

            if ( instance.getManagedInstances() != null ) {
                for ( KieServerInstanceInfo instanceInfo : instance.getManagedInstances() ) {
                    ServerInstanceRef instanceRef = new ServerInstanceRefImpl( instanceInfo.getStatus().toString(), instanceInfo.getLocation() );
                    serverRef.addManagedServer( instanceRef );
                }
            }

            result.add( serverRef );

        }

        return result;
    }

    @Override
    public void registerServer( final String endpoint,
                                final String name,
                                final String version ) throws ServerAlreadyRegisteredException {
        checkNotEmpty( "endpoint", endpoint );
        checkNotEmpty( "name", name );

        try {

            final KieServerInfo kieServerInfo = new KieServerInfo();
            kieServerInfo.setServerId( endpoint );
            kieServerInfo.setName( name );
            kieServerInfo.setVersion( version );

            controllerAdmin.addKieServerInstance( kieServerInfo );

            Server serverRef = new ServerImpl(
                    kieServerInfo.getServerId(),
                    "url",
                    kieServerInfo.getName(),
                    "user",
                    "password",
                    ContainerStatus.STOPPED,
                    ConnectionType.REMOTE,
                    null,
                    new HashMap<String, String>() {{
                        put( "version", kieServerInfo.getVersion() );
                    }},
                    null
            );

            serverConnectedEvent.fire( new ServerConnected( serverRef ) );

        } catch ( KieServicesException e ) {
            logger.warn( "Connection failed", e );
            throw e;
        } catch ( KieRemoteHttpRequestException e ) {
            logger.warn( "Connection failed", e );
            throw e;
        }
    }

    private KieContainerResource findContainerById( String containerId,
                                                    Set<KieContainerResource> containerResources ) {
        if ( containerResources != null ) {
            for ( KieContainerResource containerResource : containerResources ) {
                if ( containerResource.getContainerId().equals( containerId ) ) {
                    return containerResource;
                }
            }
        }

        return null;
    }

    private GAV toGAV( ReleaseId releaseId ) {
        return new GAV( releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion() );
    }

    @Override
    public void startContainers( final Map<String, List<String>> containers ) {
        for ( Map.Entry<String, List<String>> entry : containers.entrySet() ) {
            final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( entry.getKey() );
            Set<KieContainerResource> containerResources = serverInstance.getKieServerSetup().getContainers();

            for ( final String containerId : entry.getValue() ) {
                final KieContainerResource containerRef = findContainerById( containerId, containerResources );
                containerRef.setStatus( KieContainerStatus.STARTED );

                executor.execute( new Runnable() {
                    @Override
                    public void run() {

                        for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                            try {
                                remoteAccess.install( serverInstance.getIdentifier(), instanceInfo.getLocation(), containerRef.getContainerId(), controllerUser, controllerPassword, toGAV( containerRef.getReleaseId() ) );

                            } catch ( final Exception ex ) {
                                logger.debug( "Error while broadcasting start container request to server instance {}", instanceInfo.getLocation(), ex );
                            }
                        }

                    }
                } );
                final Container newContainerRef = new ContainerImpl( serverInstance.getIdentifier(), containerRef.getContainerId(), ContainerStatus.STARTED,
                                                                     toGAV( containerRef.getReleaseId() ),
                                                                     containerRef.getScanner() == null ? null : ScannerStatus.valueOf( containerRef.getScanner().getStatus().toString() ),
                                                                     containerRef.getScanner() == null ? null : containerRef.getScanner().getPollInterval(), toGAV( containerRef.getReleaseId() ) );
                // prepare managed instances
                if ( serverInstance.getManagedInstances() != null ) {
                    for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {
                        ServerInstanceRef instanceRef = new ServerInstanceRefImpl( instanceInfo.getStatus().toString(), instanceInfo.getLocation() );
                        newContainerRef.addManagedServer( instanceRef );
                    }
                }

                containerStartedEvent.fire( new ContainerStarted( newContainerRef ) );
            }
            controllerStorage.update( serverInstance );
        }
    }

    @Override
    public void stopContainers( final Map<String, List<String>> containers ) {

        for ( Map.Entry<String, List<String>> entry : containers.entrySet() ) {
            final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( entry.getKey() );
            Set<KieContainerResource> containerResources = serverInstance.getKieServerSetup().getContainers();

            for ( final String containerId : entry.getValue() ) {
                final KieContainerResource containerRef = findContainerById( containerId, containerResources );
                containerRef.setStatus( KieContainerStatus.STOPPED );

                executor.execute( new Runnable() {
                    @Override
                    public void run() {

                        for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                            try {
                                remoteAccess.deleteContainer( instanceInfo.getLocation(), containerRef.getContainerId(), controllerUser, controllerPassword );

                            } catch ( final Exception ex ) {
                                logger.debug( "Error while broadcasting stop container request to server instance {}", instanceInfo.getLocation(), ex );
                            }
                        }

                    }
                } );
                final ContainerRef newContainerRef = new ContainerRefImpl( serverInstance.getIdentifier(), containerRef.getContainerId(), ContainerStatus.STOPPED,
                                                                           toGAV( containerRef.getReleaseId() ),
                                                                           containerRef.getScanner() == null ? null : ScannerStatus.valueOf( containerRef.getScanner().getStatus().toString() ),
                                                                           containerRef.getScanner() == null ? null : containerRef.getScanner().getPollInterval() );

                // prepare managed instances
                if ( serverInstance.getManagedInstances() != null ) {
                    for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {
                        ServerInstanceRef instanceRef = new ServerInstanceRefImpl( instanceInfo.getStatus().toString(), instanceInfo.getLocation() );
                        newContainerRef.addManagedServer( instanceRef );
                    }
                }

                containerStoppedEvent.fire( new ContainerStopped( newContainerRef ) );

            }
            controllerStorage.update( serverInstance );

        }

    }

    @Override
    public void createContainer( final String serverId,
                                 final String containerId,
                                 final GAV gav ) {
        KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );
        KieContainerResource containerResource = new KieContainerResource( containerId, new ReleaseId( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() ) );
        containerResource.setStatus( KieContainerStatus.STOPPED );

        serverInstance.getKieServerSetup().getContainers().add( containerResource );
        controllerStorage.update( serverInstance );

        final ContainerRef containerRef = new ContainerRefImpl( serverId, containerId, ContainerStatus.STOPPED, gav, null, null );
        containerCreatedEvent.fire( new ContainerCreated( containerRef ) );
    }

    @Override
    public Container getContainerInfo( final String serverId,
                                       final String container ) {
        KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );

        KieContainerResource containerResource = findContainerById( container, serverInstance.getKieServerSetup().getContainers() );

        Container containerInstance = new ContainerImpl( serverId, containerResource.getContainerId(),
                                                         calculateStatus( containerResource, serverInstance ),
                                                         toGAV( containerResource.getReleaseId() ),
                                                         containerResource.getScanner() == null ? null : ScannerStatus.valueOf( containerResource.getScanner().getStatus().toString() ),
                                                         containerResource.getScanner() == null ? null : containerResource.getScanner().getPollInterval(), null );

        // prepare managed instances
        if ( serverInstance.getManagedInstances() != null ) {
            for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {
                ServerInstanceRef instanceRef = new ServerInstanceRefImpl( instanceInfo.getStatus().toString(), instanceInfo.getLocation() );
                containerInstance.addManagedServer( instanceRef );
            }
        }

        return containerInstance;

    }

    protected ContainerStatus calculateStatus( KieContainerResource containerResource,
                                               KieServerInstance kieServerInstance ) {
        if ( containerResource.getStatus().equals( KieContainerStatus.STOPPED ) || kieServerInstance.getStatus().equals( KieServerStatus.DOWN ) ) {
            return ContainerStatus.STOPPED;
        }

        return ContainerStatus.STARTED;
    }

    @Override
    public ScannerOperationResult scanNow( final String serverId,
                                           final String containerId ) {

        final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );

        final KieContainerResource containerResource = findContainerById( containerId, serverInstance.getKieServerSetup().getContainers() );

        executor.execute( new Runnable() {
            @Override
            public void run() {

                for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                    try {
                        remoteAccess.scanNow( instanceInfo.getLocation(), containerResource.getContainerId(), controllerUser, controllerPassword );

                    } catch ( final Exception ex ) {
                        logger.debug( "Error while broadcasting start scanning now request to server instance {}", instanceInfo.getLocation(), ex );
                    }
                }
                refresh();
            }
        } );

        return new ScannerOperationResult( ScannerStatus.SCANNING, "Scanning operation initiated on all servers", null );
    }

    @Override
    public ScannerOperationResult startScanner( final String serverId,
                                                final String containerId,
                                                final long interval ) {
        final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );

        final KieContainerResource containerResource = findContainerById( containerId, serverInstance.getKieServerSetup().getContainers() );
        KieScannerResource kieScannerResource = containerResource.getScanner();
        if ( kieScannerResource == null ) {
            kieScannerResource = new KieScannerResource();
            containerResource.setScanner( kieScannerResource );
        }
        kieScannerResource.setPollInterval( interval );
        kieScannerResource.setStatus( KieScannerStatus.STARTED );

        controllerStorage.update( serverInstance );
        executor.execute( new Runnable() {
            @Override
            public void run() {

                for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                    try {
                        remoteAccess.startScanner( instanceInfo.getLocation(), containerResource.getContainerId(), controllerUser, controllerPassword, interval );

                    } catch ( final Exception ex ) {
                        logger.debug( "Error while broadcasting start scanning with interval request to server instance {}", instanceInfo.getLocation(), ex );
                    }
                }
            }
        } );

        return new ScannerOperationResult( ScannerStatus.STARTED, "Scanner start initiated on all servers with interval " + interval, null );
    }

    @Override
    public ScannerOperationResult stopScanner( final String serverId,
                                               final String containerId ) {
        final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );

        final KieContainerResource containerResource = findContainerById( containerId, serverInstance.getKieServerSetup().getContainers() );
        KieScannerResource kieScannerResource = containerResource.getScanner();
        if ( kieScannerResource == null ) {
            kieScannerResource = new KieScannerResource();
            containerResource.setScanner( kieScannerResource );
        }
        kieScannerResource.setStatus( KieScannerStatus.STOPPED );

        controllerStorage.update( serverInstance );
        executor.execute( new Runnable() {
            @Override
            public void run() {

                for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                    try {
                        remoteAccess.stopScanner( instanceInfo.getLocation(), containerResource.getContainerId(), controllerUser, controllerPassword );

                    } catch ( final Exception ex ) {
                        logger.debug( "Error while broadcasting stop scanning request to server instance {}", instanceInfo.getLocation(), ex );
                    }
                }
            }
        } );

        return new ScannerOperationResult( ScannerStatus.STOPPED, "Scanner stop initiated on all servers", null );
    }

    @Override
    public void upgradeContainer( final String serverId,
                                  final String containerId,
                                  final GAV releaseId ) {

        final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );

        try {

            final KieContainerResource containerResource = findContainerById( containerId, serverInstance.getKieServerSetup().getContainers() );
            containerResource.setReleaseId( new ReleaseId( releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion() ) );

            executor.execute( new Runnable() {
                @Override
                public void run() {

                    for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                        try {
                            remoteAccess.upgradeContainer( instanceInfo.getLocation(), containerId, controllerUser, controllerPassword, releaseId );

                        } catch ( final Exception ex ) {
                            logger.debug( "Error while broadcasting update container request to server instance {}", instanceInfo.getLocation(), ex );
                        }
                    }
                }
            } );

            controllerStorage.update( serverInstance );
        } finally {
            containerUpdatedEvent.fire( new ContainerUpdated( getContainerInfo( serverId, containerId ) ) );
        }
    }

    @Override
    public void updateServerStatus( final Collection<String> servers ) {

        for ( final String serverId : servers ) {
            final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );
            executor.execute( new Runnable() {
                @Override
                public void run() {

                    for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {
                        try {
                            boolean alive = remoteAccess.pingServer( instanceInfo.getLocation(), controllerUser, controllerPassword );

                            if ( alive ) {
                                instanceInfo.setStatus( KieServerStatus.UP );

                            } else {
                                instanceInfo.setStatus( KieServerStatus.DOWN );

                            }

                        } catch ( final Exception ex ) {
                            logger.debug( "Error while pinging server instance {}", instanceInfo.getLocation(), ex );
                        }
                    }
                    controllerStorage.update( serverInstance );
                    Server serverRef = ServerUtility.buildServer( serverInstance );
                    if ( serverInstance.getStatus().equals( KieServerStatus.DOWN ) ) {
                        serverDisconnectedEvent.fire( new ServerDisconnected( serverRef ) );
                    } else {
                        serverConnectedEvent.fire( new ServerConnected( serverRef ) );
                    }
                }
            } );

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

    private void deleteContainer( final String serverId,
                                  final String containerId ) {
        final KieServerInstance serverInstance = controllerAdmin.getKieServerInstance( serverId );

        final KieContainerResource containerResource = findContainerById( containerId, serverInstance.getKieServerSetup().getContainers() );
        serverInstance.getKieServerSetup().getContainers().remove( containerResource );

        executor.execute( new Runnable() {
            @Override
            public void run() {

                for ( KieServerInstanceInfo instanceInfo : serverInstance.getManagedInstances() ) {

                    try {
                        remoteAccess.deleteContainer( instanceInfo.getLocation(), containerId, controllerUser, controllerPassword );

                    } catch ( final Exception ex ) {
                        logger.debug( "Error while broadcasting delete container request to server instance {}", instanceInfo.getLocation(), ex );
                    }
                }
            }
        } );

        controllerStorage.update( serverInstance );
        containerDeletedEvent.fire( new ContainerDeleted( serverId, containerId ) );
    }

    private void unregisterServer( final String id ) {
        controllerStorage.delete( id );
        serverDeletedEvent.fire( new ServerDeleted( id ) );
    }
}
