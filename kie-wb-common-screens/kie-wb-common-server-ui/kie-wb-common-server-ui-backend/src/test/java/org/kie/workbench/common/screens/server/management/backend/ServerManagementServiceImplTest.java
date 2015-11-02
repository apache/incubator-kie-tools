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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.KieServerControllerAdmin;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerSetup;
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
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.commons.async.DisposableExecutor;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagementServiceImplTest {

    @Mock
    private EventSourceMock<ServerConnected> serverConnectedEvent;

    @Mock
    private EventSourceMock<ServerDisconnected> serverDisonnectedEvent;

    @Mock
    private EventSourceMock<ServerOnError> serverOnErrorEvent;

    @Mock
    private EventSourceMock<ServerDeleted> serverDeletedEvent;

    @Mock
    private EventSourceMock<ContainerCreated> containerCreatedEvent;

    @Mock
    private EventSourceMock<ContainerStarted> containerStartedEvent;

    @Mock
    private EventSourceMock<ContainerStopped> containerStoppedEvent;

    @Mock
    private EventSourceMock<ContainerDeleted> containerDeletedEvent;

    @Mock
    private EventSourceMock<ContainerUpdated> containerUpdatedEvent;

    @Mock
    private EventSourceMock<ContainerOnError> containerOnErrorEvent;

    @Mock
    private ServerReferenceStorageImpl storage;

    @Mock
    private RemoteAccessImpl remoteAccess;

    @Mock
    private KieServerControllerAdmin controllerAdmin;

    @Mock
    private KieServerControllerStorage controllerStorage;

    @Mock
    private DisposableExecutor executor;

    private ServerManagementServiceImpl serverManagementService;

    @Before
    public void setUp() throws Exception {
        serverManagementService = new ServerManagementServiceImpl( serverConnectedEvent,
                                                                   serverOnErrorEvent,
                                                                   serverDeletedEvent,
                                                                   containerCreatedEvent,
                                                                   containerStartedEvent,
                                                                   containerStoppedEvent,
                                                                   containerDeletedEvent,
                                                                   containerUpdatedEvent,
                                                                   containerOnErrorEvent,
                                                                   serverDisonnectedEvent,
                                                                   storage, remoteAccess, controllerAdmin,controllerStorage, executor );
    }

    @Ignore
    @Test
    public void testEmptyListServers() throws Exception {
        when( storage.listRegisteredServers() ).thenReturn( Collections.<ServerRef>emptyList() );

        final Collection<ServerRef> serverRefs = serverManagementService.listServers();

        verify( storage, times( 1 ) ).listRegisteredServers();
        assertNotNull( serverRefs );
        assertEquals( 0, serverRefs.size() );
    }

    @Ignore
    @Test
    public void testListServers() throws Exception {

        final ArgumentCaptor<Runnable> selectedRunnable = ArgumentCaptor.forClass( Runnable.class );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                selectedRunnable.getValue().run();
                return null;
            }
        } ).when( executor ).execute( selectedRunnable.capture() );

        final Server serverRef1 = new ServerImpl( "server_id1", "server_url1", "my_server1",
                                                  null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                  Collections.<Container>emptyList(),
                                                  Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef3 = new ServerRefImpl( "server_id3", "server_url3", "my_server3",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );

        when( storage.listRegisteredServers() ).thenReturn( Arrays.asList( serverRef1, serverRef2, serverRef3 ) );

        when( remoteAccess.toServer( serverRef1 ) ).thenReturn( serverRef1 );
        when( remoteAccess.toServer( serverRef2 ) ).thenReturn( null );
        when( remoteAccess.toServer( serverRef3 ) ).thenThrow( new RuntimeException() );

        final Collection<ServerRef> serverRefs = serverManagementService.listServers();

        verify( storage, times( 1 ) ).listRegisteredServers();

        verify( executor, times( 3 ) ).execute( any( Runnable.class ) );

        verify( serverOnErrorEvent, times( 2 ) ).fire( any( ServerOnError.class ) );

        final ArgumentCaptor<ServerConnected> serverConnectedCaptor = ArgumentCaptor.forClass( ServerConnected.class );

        verify( serverConnectedEvent, times( 1 ) ).fire( serverConnectedCaptor.capture() );

        assertEquals( serverRef1, serverConnectedCaptor.getValue().getServer() );

        assertNotNull( serverRefs );
        assertEquals( 3, serverRefs.size() );
    }

    @Ignore
    @Test
    public void testRefresh() throws Exception {

        final ArgumentCaptor<Runnable> selectedRunnable = ArgumentCaptor.forClass( Runnable.class );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                selectedRunnable.getValue().run();
                return null;
            }
        } ).when( executor ).execute( selectedRunnable.capture() );

        final Server serverRef1 = new ServerImpl( "server_id1", "server_url1", "my_server1",
                                                  null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                  Collections.<Container>emptyList(),
                                                  Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef3 = new ServerRefImpl( "server_id3", "server_url3", "my_server3",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );

        when( storage.listRegisteredServers() ).thenReturn( Arrays.asList( serverRef1, serverRef2, serverRef3 ) );

        when( remoteAccess.toServer( serverRef1 ) ).thenReturn( serverRef1 );
        when( remoteAccess.toServer( serverRef2 ) ).thenReturn( null );
        when( remoteAccess.toServer( serverRef3 ) ).thenThrow( new RuntimeException() );

        serverManagementService.refresh();

        verify( storage, times( 1 ) ).listRegisteredServers();

        verify( executor, times( 3 ) ).execute( any( Runnable.class ) );

        verify( serverOnErrorEvent, times( 2 ) ).fire( any( ServerOnError.class ) );

        final ArgumentCaptor<ServerConnected> serverConnectedCaptor = ArgumentCaptor.forClass( ServerConnected.class );

        verify( serverConnectedEvent, times( 1 ) ).fire( serverConnectedCaptor.capture() );

        assertEquals( serverRef1, serverConnectedCaptor.getValue().getServer() );
    }



    @Test
    public void testStartContainer() throws Exception {
        final String controllerUserName = "controller";
        final String controllerPassword = "controller123@";
        final String serverId = "server_id";
        final String serverUrl = "server_ur";
        final String containerId = "my_container_id";
        final String groupId = "com.example";
        final String artifactId = "example-artifact";
        final String version = "LATEST";
        final String resolvedVersion = "0.2.Final";

        System.setProperty(KieServerConstants.CFG_KIE_USER, controllerUserName);
        System.setProperty(KieServerConstants.CFG_KIE_PASSWORD, controllerPassword);

        final Container container = new ContainerImpl( serverId, containerId, ContainerStatus.STARTED, new GAV( groupId, artifactId, version ), null, null, new GAV( groupId, artifactId, resolvedVersion ) );

        final KieContainerResource containerResource = new KieContainerResource( containerId, new ReleaseId( groupId, artifactId, version ));

        final KieServerSetup serverSetup = new KieServerSetup();
        serverSetup.getContainers().add(containerResource);

        KieServerInstanceInfo serverInstanceInfo = new KieServerInstanceInfo(serverUrl, KieServerStatus.UP, Arrays.asList("KieServer"));
        Set<KieServerInstanceInfo> serverInstanceInfoMap = new HashSet<KieServerInstanceInfo>();
        serverInstanceInfoMap.add(serverInstanceInfo);

        final KieServerInstance serverInstance = new KieServerInstance();
        serverInstance.setIdentifier( serverId );
        serverInstance.setKieServerSetup( serverSetup );
        serverInstance.setManagedInstances(serverInstanceInfoMap);

        when( controllerAdmin.getKieServerInstance( serverId ) ).thenReturn( serverInstance );

        final ArgumentCaptor<Runnable> selectedRunnable = ArgumentCaptor.forClass( Runnable.class );
        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                selectedRunnable.getValue().run();
                return null;
            }
        } ).when( executor ).execute( selectedRunnable.capture() );

        Map<String, List<String>> containers =  new HashMap<String, List<String>>();
        containers.put(serverId, Arrays.asList(containerId));
        serverManagementService.startContainers( containers );

        final ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass( String.class );
        final ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass( String.class );

        verify( remoteAccess, times( 1 ) ).install( eq(serverId), eq(serverUrl), eq(containerId), usernameCaptor.capture(), passwordCaptor.capture(), any(GAV.class) );

        assertEquals(controllerUserName, usernameCaptor.getValue());
        assertEquals(controllerPassword, passwordCaptor.getValue());

        final ArgumentCaptor<ContainerStarted> containerStartedCaptor = ArgumentCaptor.forClass( ContainerStarted.class );

        verify( containerStartedEvent, times( 1 ) ).fire( containerStartedCaptor.capture() );

        final List<ContainerStarted> values = containerStartedCaptor.getAllValues();
        assertEquals( 1, values.size() );

        assertTrue( values.get( 0 ).getContainer().equals( container ));

        verify( controllerStorage, times( 1 ) ).update( eq(serverInstance) );

        System.clearProperty(KieServerConstants.CFG_KIE_USER);
        System.clearProperty(KieServerConstants.CFG_KIE_PASSWORD);
    }
}
