package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieServerConfig;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteAccessImplTest {

    private RemoteAccessImpl remoteAccess;
    private KieServicesClient kieServicesClientMock;
    private ServiceResponse<KieServerInfo> serviceResponseMock;
    private ServiceResponse<KieContainerResourceList> containerResourcesResponseMock;

    @Before
    public void setUp() throws Exception {
        remoteAccess = new RemoteAccessImpl() {
            @Override
            KieServicesClient getKieServicesClient( String username,
                                                    String password,
                                                    String _endpoint ) {

                return kieServicesClientMock;
            }
        };
        kieServicesClientMock = mock( KieServicesClient.class );
        serviceResponseMock = mock( ServiceResponse.class );
        containerResourcesResponseMock = mock( ServiceResponse.class );

        when( kieServicesClientMock.register( any( String.class ), any( KieServerConfig.class ) ) ).thenReturn( serviceResponseMock );
        when( kieServicesClientMock.listContainers() ).thenReturn( containerResourcesResponseMock );
    }

    private Server createServer( final String id,
                                 final String endpoint,
                                 final String name,
                                 final String username,
                                 final String password,
                                 final ContainerStatus containerStatus,
                                 final ConnectionType connectionType,
                                 Collection<Container> containers,
                                 Map<String, String> properties,
                                 KieContainerResourceList containersConfig ) {
        Collection<ContainerRef> containersList = new ArrayList<ContainerRef>();
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( id, container ) );
        }
        return new ServerImpl( id, endpoint, name, username, password, containerStatus, connectionType, containers, properties, containersList );
    }

    private ServerRefImpl createServerRef( final String id,
                                           final String endpoint,
                                           final String name,
                                           final String username,
                                           final String password,
                                           final ContainerStatus containerStatus,
                                           final ConnectionType connectionType,
                                           Collection<ContainerRef> containers,
                                           Map<String, String> properties ) {
        return new ServerRefImpl( id, endpoint, name, username, password, containerStatus, connectionType, properties, containers );
    }

    private Collection<Container> getContainers( String endPointCleaned,
                                                 KieContainerResourceList containersConfig ) {
        Collection<Container> containersList = new ArrayList<Container>(  );
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( endPointCleaned, container ) );
        }
        return containersList;
    }

    private KieContainerResourceList generateContainers() {
        List<KieContainerResource> containers = new ArrayList<KieContainerResource>();
        containers.add( new KieContainerResource( "1", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );
        containers.add( new KieContainerResource( "2", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );

        KieContainerResourceList kieContainerResource = new KieContainerResourceList( containers );
        return kieContainerResource;
    }

    @Test
    public void testRegisterServerWithServiceResponseSuccess() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        String newVersion = "newVersion";
        String newId = "newId";
        when( serviceResponseMock.getResult() ).thenReturn( new KieServerInfo( newId, newVersion ) );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( containerResourcesResponseMock.getResult() ).thenReturn( generateContainers() );

        String endpoint = "http://uberfire.org/s/rest/";
        final String name = "name";
        final String username = "username";
        final String password = "password";
        ContainerStatus containerStatus = ContainerStatus.STARTED;
        final ConnectionType connectionType = ConnectionType.REMOTE;
        final String controllerUrl = "http://controller.com";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        ArrayList<Container> containers = new ArrayList<Container>();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put( "version", newVersion );

        Server actual = remoteAccess.registerServer( endpoint, name, username, password, connectionType, controllerUrl );
        Server expected = createServer( newId, endPointCleaned, name, username, password, containerStatus, connectionType, containers, properties, generateContainers() );
        assertEquals( expected, actual );
    }

    @Test
    public void testRegisterServerWithoutServiceResponseSuccess() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );

        String endpoint = "http://uberfire.org/s/rest/";
        final String name = "name";
        final String username = "username";
        final String password = "password";
        ContainerStatus containerStatus = ContainerStatus.STARTED;
        final ConnectionType connectionType = ConnectionType.REMOTE;
        final String controllerUrl = "http://controller.com";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        ArrayList<Container> containers = new ArrayList<Container>();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put( "version", null );

        Server actual = remoteAccess.registerServer( endpoint, name, username, password, connectionType, controllerUrl );
        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, containerStatus, connectionType, containers, properties, new KieContainerResourceList() );
        assertEquals( expected, actual );
    }

    @Test
    public void testRegisterServerWithServiceResponseException() throws Exception {

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        String newVersion = "newVersion";
        String newId = "newId";
        when( serviceResponseMock.getResult() ).thenReturn( new KieServerInfo( newId, newVersion ) );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );

        final String controllerUrl = "http://controller.com";
        String controllerURLEncoded = remoteAccess.encodeController( controllerUrl );
        when( kieServicesClientMock.register( eq( controllerURLEncoded ), any( KieServerConfig.class ) ) )
                .thenThrow( Exception.class ).thenReturn( serviceResponseMock );

        String endpoint = "http://uberfire.org/s/rest/";
        final String name = "name";
        final String username = "username";
        final String password = "password";
        ContainerStatus containerStatus = ContainerStatus.STARTED;
        final ConnectionType connectionType = ConnectionType.REMOTE;

        String targetEndPoint = remoteAccess.cleanup( endpoint );
        String targetEndPointWithBaseURI = remoteAccess.addBaseURIToEndpoint( targetEndPoint );
        ArrayList<Container> containers = new ArrayList<Container>();
        Map<String, String> properties = new HashMap<String, String>();
        properties.put( "version", newVersion );

        Server actual = remoteAccess.registerServer( endpoint, name, username, password, connectionType, controllerUrl );
        Server expected = createServer( newId, targetEndPointWithBaseURI, name, username, password, containerStatus, connectionType, containers, properties, new KieContainerResourceList() );
        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRef() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = "serverId";
        String version = "version";
        KieServerInfo serverInfo = new KieServerInfo( expectedId, version );
        properties.put( "version", version );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( serverInfo );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        ServerRef expected = createServerRef( expectedId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRefWithoutServiceResponseSuccess() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        String version = "version";
        properties.put( "version", null );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        ServerRef expected = createServerRef( expectedId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRefWithoutServiceResponseException() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        String expectedEndpoint = remoteAccess.addBaseURIToEndpoint( endPointCleaned );
        String version = "version";
        KieServerInfo serverInfo = new KieServerInfo( expectedId, version );
        properties.put( "version", version );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( serverInfo );
        when( kieServicesClientMock.getServerInfo() ).thenThrow( Exception.class ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionType, containerRefs );
        ServerRef expected = createServerRef( expectedId, expectedEndpoint, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServer() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        properties.put( "version", null );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        KieContainerResourceList containersConfig = generateContainers();

        when( containerResourcesResponseMock.getResult() ).thenReturn( containersConfig );

        ServerRef serverRef = createServerRef( expectedId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionType, containerRefs, properties );


        Server actual = remoteAccess.toServer( serverRef );
        Collection<Container> containersList = getContainers( endPointCleaned, containersConfig );

        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, ContainerStatus.STARTED, connectionType, containersList, properties, containersConfig );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerViaServerRef() throws Exception {
        String endpoint = "http://uberfire.org/s/rest/";
        String endPointCleaned = remoteAccess.cleanup( endpoint );
        final String name = "name";
        final String username = "username";
        final String password = "password";
        final ConnectionType connectionType = ConnectionType.REMOTE;
        Collection<ContainerRef> containerRefs = new ArrayList<ContainerRef>();
        Map<String, String> properties = new HashMap<String, String>();
        String expectedId = endPointCleaned;
        String version = "version";
        properties.put( "version", null );

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        KieContainerResourceList containersConfig = generateContainers();

        when( containerResourcesResponseMock.getResult() ).thenReturn( containersConfig );

        Server actual = remoteAccess.toServer( endpoint, name, username, password, connectionType, containerRefs );
        Collection<Container> containersList = getContainers( endPointCleaned, containersConfig );

        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, ContainerStatus.STARTED, connectionType, containersList, properties, containersConfig );

        assertEquals( expected, actual );
    }

    @Test
    public void testInstall() throws Exception {
        //TODO
    }
}