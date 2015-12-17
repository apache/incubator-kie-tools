/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.GAV;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerResource;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.KieServerConfig;
import org.kie.server.api.model.KieServerConfigItem;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesFactory;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.kie.workbench.common.screens.server.management.service.ContainerAlreadyRegisteredException;
import org.kie.workbench.common.screens.server.management.service.RemoteOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.screens.server.management.model.ConnectionType.*;
import static org.kie.workbench.common.screens.server.management.model.ContainerStatus.*;

@ApplicationScoped
public class RemoteAccessImpl {

    private static final Logger logger = LoggerFactory.getLogger(RemoteAccessImpl.class);

    private static final String BASE_URI = "/services/rest/server";

    public Server registerServer( final String endpoint,
                                  final String name,
                                  final String username,
                                  final String password,
                                  final ConnectionType connectionType,
                                  final String controllerUrl ) {

        String _endpoint = cleanup( endpoint );

        KieServerConfig kieServerConfig = new KieServerConfig();
        kieServerConfig.addConfigItem( new KieServerConfigItem( "executor.interval", "2", Integer.class.getName() ) );

        KieServicesClient client = getKieServicesClient( username, password, _endpoint );
        String _version = null;
        String _id = _endpoint;


        final Collection<Container> containers = new ArrayList<Container>();

        final ServiceResponse<KieContainerResourceList> containerResourcesResponse = client.listContainers();
        if ( containerResourcesResponse.getType().equals( ServiceResponse.ResponseType.SUCCESS ) &&
                containerResourcesResponse.getResult().getContainers() != null ) {
            for ( final KieContainerResource kieContainerResource : containerResourcesResponse.getResult().getContainers() ) {
                containers.add( toContainer( _id, kieContainerResource ) );
            }
        }
        final String version = _version;

        return new ServerImpl( _id, _endpoint, name, username, password, STARTED, connectionType, containers, new HashMap<String, String>() {{
            put( "version", version );
        }}, null );
    }

    String addBaseURIToEndpoint( String _endpoint ) {
        return _endpoint.concat( BASE_URI );
    }

    KieServicesClient getKieServicesClient( String username,
                                            String password,
                                            String _endpoint ) {
        return KieServicesFactory.newKieServicesRestClient( _endpoint, username, password );
    }

    public ServerRef toServerRef( final String endpoint,
                                  final String name,
                                  final String username,
                                  final String password,
                                  final ConnectionType connectionType,
                                  final Collection<ContainerRef> containerRefs ) {

        String _endpoint = cleanup( endpoint );

        KieServicesClient client = getKieServicesClient( username, password, _endpoint );
        String _version = null;
        String _id = _endpoint;

        try {

            final ServiceResponse<KieServerInfo> response = client.getServerInfo();
            if ( response.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                _version = response.getResult().getVersion();
                _id = response.getResult().getServerId();
            }
        } catch ( final Exception ex ) {
            _endpoint = addBaseURIToEndpoint( _endpoint );

            client = getKieServicesClient( username, password, _endpoint );
            final ServiceResponse<KieServerInfo> response = client.getServerInfo();
            if ( response.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                _version = response.getResult().getVersion();
                _id = response.getResult().getServerId();
            }
        }

        final String version = _version;

        return new ServerRefImpl( _id, _endpoint, name, username, password, ContainerStatus.LOADING, connectionType, new HashMap<String, String>() {{
            put( "version", version );
        }}, containerRefs );
    }

    String encodeController( String controllerUrl ) {
        if ( controllerUrl != null ) {
            try {
                controllerUrl = URLEncoder.encode( controllerUrl, "UTF-8" );

            } catch ( UnsupportedEncodingException e ) {
                logger.debug("Unsupported encoding when encoding controller {}", controllerUrl, e);
            }
        }

        return controllerUrl;
    }

    String cleanup( final String endpoint ) {
        if ( endpoint.endsWith( "/" ) ) {
            return cleanup( endpoint.substring( 0, endpoint.length() - 1 ) );
        }
        return endpoint;
    }

    public boolean pingServer( final String endpoint, final String username, final String password) {
        try {
            final KieServicesClient client = getKieServicesClient( username, password, endpoint );
            final Collection<Container> containers = new ArrayList<Container>();

            final ServiceResponse<KieServerInfo> containerResourcesResponse = client.getServerInfo();
            if ( containerResourcesResponse.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                return true;
            }

        } catch ( final Exception ex ) {
            logger.warn("Ping to server {} failed due to {}", endpoint, ex.getMessage());
        }
        return false;
    }

    public Server toServer( final String endpoint,
                            final String name,
                            final String username,
                            final String password,
                            final ConnectionType connectionType,
                            final Collection<ContainerRef> containerRefs ) {

        final ServerRef serverRef = toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        try {
            final KieServicesClient client = getKieServicesClient( serverRef.getUsername(), serverRef.getPassword(), serverRef.getUrl() );
            final Collection<Container> containers = new ArrayList<Container>();

            final ServiceResponse<KieContainerResourceList> containerResourcesResponse = client.listContainers();
            if ( containerResourcesResponse.getType().equals( ServiceResponse.ResponseType.SUCCESS ) &&
                    containerResourcesResponse.getResult().getContainers() != null ) {
                for ( final KieContainerResource kieContainerResource : containerResourcesResponse.getResult().getContainers() ) {
                    containers.add( toContainer( serverRef.getId(), kieContainerResource ) );
                }
            }

            return new ServerImpl( serverRef.getId(), serverRef.getUrl(), serverRef.getName(), serverRef.getUsername(), serverRef.getPassword(), STARTED, connectionType, containers, serverRef.getProperties(), serverRef.getContainersRef() );

        } catch ( final Exception ex ) {
            return null;
        }
    }

    public Server toServer( final ServerRef serverRef ) {
        try {
            final KieServicesClient client = getKieServicesClient( serverRef.getUsername(), serverRef.getPassword(), serverRef.getUrl() );
            final Collection<Container> containers = new ArrayList<Container>();

            final ServiceResponse<KieContainerResourceList> containerResourcesResponse = client.listContainers();
            if ( containerResourcesResponse.getType().equals( ServiceResponse.ResponseType.SUCCESS ) &&
                    containerResourcesResponse.getResult().getContainers() != null ) {
                for ( final KieContainerResource kieContainerResource : containerResourcesResponse.getResult().getContainers() ) {
                    final Container container = toContainer( serverRef.getId(), kieContainerResource );
                    if ( container.getReleasedId() != null && !container.getStatus().equals( ERROR ) ) {
                        containers.add( container );
                    } else {
                        final ContainerRef containerRef = serverRef.getContainerRef( kieContainerResource.getContainerId() );
                        containers.add( new ContainerImpl( containerRef.getServerId(), containerRef.getId(), ERROR, containerRef.getReleasedId(), ScannerStatus.ERROR, null, null ) );
                    }
                }
            }

            return new ServerImpl( serverRef.getId(), serverRef.getUrl(), serverRef.getName(), serverRef.getUsername(), serverRef.getPassword(), STARTED, REMOTE, containers, serverRef.getProperties(), serverRef.getContainersRef() );

        } catch ( final Exception ex ) {
            return null;
        }
    }

    public Container install( final String serverId,
                              final String serverUrl,
                              final String containerId,
                              final String username,
                              final String password,
                              final GAV gav ) {

        try {
            final KieServicesClient client = getKieServicesClient( username, password, serverUrl );

            final ServiceResponse<KieContainerResource> response = client.createContainer( containerId, new KieContainerResource( new ReleaseId( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() ) ) );

            if ( response.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                return toContainer( serverId, response.getResult() );
            } else {
                throw new ContainerAlreadyRegisteredException( response.getMsg() );
            }
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public boolean stop( final String serverId,
                         final String serverUrl,
                         final String containerId,
                         final String username,
                         final String password ) {
        try {
            final KieServicesClient client = getKieServicesClient( username, password, serverId );
            final ServiceResponse<KieScannerResource> response = client.updateScanner( containerId, new KieScannerResource( KieScannerStatus.STOPPED ) );

            return response.getType().equals( ServiceResponse.ResponseType.SUCCESS );

        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    Container toContainer( final String serverId,
                           final KieContainerResource kieContainerResource ) {
        return new ContainerImpl( serverId, kieContainerResource.getContainerId(), toStatus( kieContainerResource.getStatus() ), toGAV( kieContainerResource.getReleaseId() ), toStatus( kieContainerResource.getScanner() ), kieContainerResource.getScanner() == null ? null : toSeconds( kieContainerResource.getScanner().getPollInterval() ), toGAV( kieContainerResource.getResolvedReleaseId() ) );
    }

    private GAV toGAV( final ReleaseId releaseId ) {
        if ( releaseId == null || releaseId.getGroupId() == null ) {
            return null;
        }
        return new GAV( releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion() );
    }

    public ScannerStatus toStatus( final KieScannerResource scanner ) {
        if ( scanner == null ) {
            return ScannerStatus.STOPPED;
        }
        switch ( scanner.getStatus() ) {
            case UNKNOWN:
                return ScannerStatus.UNKNOWN;
            case STOPPED:
                return ScannerStatus.STOPPED;
            case CREATED:
                return ScannerStatus.CREATED;
            case STARTED:
                return ScannerStatus.STARTED;
            case SCANNING:
                return ScannerStatus.SCANNING;
            case DISPOSED:
                return ScannerStatus.DISPOSED;
            default:
                return ScannerStatus.ERROR;
        }
    }

    public ContainerStatus toStatus( final KieContainerStatus status ) {
        switch ( status ) {
            case CREATING:
                return ContainerStatus.LOADING;
            case DISPOSING:
                return ContainerStatus.STOPPED;
            case STARTED:
                return ContainerStatus.STARTED;
            case FAILED:
                return ContainerStatus.ERROR;
            default:
                return ContainerStatus.ERROR;
        }
    }

    public boolean deleteContainer( final String serverUrl,
                                    final String containerId,
                                    final String username,
                                    final String password ) {
        try {
            final KieServicesClient client = getKieServicesClient( username, password, serverUrl );
            return client.disposeContainer( containerId ).getType().equals( ServiceResponse.ResponseType.SUCCESS );
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public boolean containerExists( final String serverUrl,
                                    final String containerId,
                                    final String username,
                                    final String password ) {
        try {
            return getKieServicesClient( username, password, serverUrl ).getContainerInfo( containerId ).getType().equals( ServiceResponse.ResponseType.SUCCESS );
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public Server toServer( final String endpoint,
                            final String name,
                            final String username,
                            final String password,
                            final ConnectionType remote ) {
        return toServer( endpoint, name, username, password, remote, null );
    }

    public Pair<Boolean, Container> getContainer( final String serverId,
                                                  final String serverUrl,
                                                  final String containerId,
                                                  final String username,
                                                  final String password ) {
        try {
            final KieServicesClient client = getKieServicesClient( username, password, serverUrl );
            final ServiceResponse<KieContainerResource> response = client.getContainerInfo( containerId );

            if ( response.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                return Pair.newPair( true, toContainer( serverId, response.getResult() ) );
            } else {
                return Pair.newPair( true, null );
            }
        } catch ( final Exception ignored ) {
        }

        return Pair.newPair( false, null );
    }

    public ScannerOperationResult stopScanner( final String serverId,
                                               final String containerId,
                                               final String username,
                                               final String password ) {
        return changeScannerStatus( serverId, containerId, username, password, KieScannerStatus.STOPPED, null );
    }

    public ScannerOperationResult startScanner( final String serverId,
                                                final String containerId,
                                                final String username,
                                                final String password,
                                                long interval ) {
        return changeScannerStatus( serverId, containerId, username, password, KieScannerStatus.STARTED, interval );
    }

    public ScannerOperationResult scanNow( final String serverId,
                                           final String containerId,
                                           final String username,
                                           final String password ) {
        return changeScannerStatus( serverId, containerId, username, password, KieScannerStatus.SCANNING, null );
    }

    private ScannerOperationResult changeScannerStatus( final String serverId,
                                                        final String containerId,
                                                        final String username,
                                                        final String password,
                                                        final KieScannerStatus status,
                                                        final Long interval ) {
        try {
            final KieServicesClient client = getKieServicesClient( username, password, serverId );
            final KieScannerResource resource;
            if ( interval == null ) {
                resource = new KieScannerResource( status );
            } else {
                resource = new KieScannerResource( status, toMillis( interval ) );
            }
            final ServiceResponse<KieScannerResource> response = client.updateScanner( containerId, resource );

            if ( response.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                return new ScannerOperationResult( toStatus( response.getResult() ), response.getMsg(), response.getResult().getPollInterval() );
            }

            return new ScannerOperationResult( ScannerStatus.ERROR, response.getMsg(), null );
        } catch ( final Exception ex ) {
            return new ScannerOperationResult( ScannerStatus.ERROR, ex.getMessage(), null );
        }
    }

    public void upgradeContainer( final String serverId,
                                  final String containerId,
                                  final String username,
                                  final String password,
                                  final GAV releaseId ) {
        try {
            final KieServicesClient client = getKieServicesClient( username, password, serverId );
            final ServiceResponse<ReleaseId> response = client.updateReleaseId( containerId, new ReleaseId( releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion() ) );

            if ( !response.getType().equals( ServiceResponse.ResponseType.SUCCESS ) ) {
                throw new RemoteOperationFailedException( response.getMsg() );
            }
        } catch ( final Exception ex ) {
            if ( ex instanceof RemoteOperationFailedException ) {
                throw (RemoteOperationFailedException) ex;
            }
            throw new RuntimeException( ex.getMessage() );
        }
    }

    private long toMillis( final Long duration ) {
        if ( duration == null ) {
            return 0;
        }
        return TimeUnit.SECONDS.toMillis( duration );
    }

    private long toSeconds( final Long duration ) {
        if ( duration == null ) {
            return 0;
        }
        return TimeUnit.MILLISECONDS.toSeconds( duration );
    }

}
