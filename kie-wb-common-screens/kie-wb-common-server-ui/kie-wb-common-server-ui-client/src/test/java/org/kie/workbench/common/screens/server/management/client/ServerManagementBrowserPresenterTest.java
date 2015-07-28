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

package org.kie.workbench.common.screens.server.management.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.events.ContainerCreated;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
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
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagementBrowserPresenterTest {

    private ServerManagementBrowserPresenter presenter;

    @Mock
    private ServerManagementBrowserPresenter.View view;

    @Mock
    private ServerManagementService service;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private HeaderPresenter headerPresenter;

    @Mock
    private IOCBeanDef<BoxPresenter> beanDef;

    private Caller<ServerManagementService> caller;

    @Before
    public void setup() {
        caller = new CallerMock<ServerManagementService>( service );

        when( beanManager.lookupBean( BoxPresenter.class ) ).thenReturn( beanDef );

        when( beanDef.newInstance() ).thenAnswer( new Answer<BoxPresenter>() {
            @Override
            public BoxPresenter answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final BoxPresenter mocked = mock( BoxPresenter.class );
                return mocked;
            }
        } );

        presenter = new ServerManagementBrowserPresenter( view, beanManager, headerPresenter, caller );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testEmptyServerList() {
        verify( service, times( 0 ) ).listServers();
        when( service.listServers() ).thenReturn( Collections.<ServerRef>emptyList() );

        presenter.onOpen();

        verify( service, times( 1 ) ).listServers();
        verify( view, times( 0 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 0 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );
    }

    @Test
    public void testServerList() {
        verify( service, times( 0 ) ).listServers();
        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
        }} );

        presenter.onOpen();

        verify( service, times( 1 ) ).listServers();
        verify( view, times( 1 ) ).cleanup();
        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 0 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        presenter.onOpen();

        verify( service, times( 2 ) ).listServers();
        verify( view, times( 2 ) ).cleanup();
        verify( view, times( 4 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 0 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
        }} );

        presenter.onOpen();

        verify( service, times( 3 ) ).listServers();
        verify( view, times( 3 ) ).cleanup();
        verify( view, times( 5 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 0 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );
    }

    @Test
    public void testOnServerConnected() {
        verify( service, times( 0 ) ).listServers();
        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
        }} );

        presenter.onOpen();

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );

        final Server server1 = new ServerImpl( "server_id1", "server_url1", "my_server1",
                                               null, null, ContainerStatus.STARTED, ConnectionType.REMOTE,
                                               Collections.<Container>emptyList(), Collections.<String, String>emptyMap(),
                                               Collections.<ContainerRef>emptyList() );

        presenter.onServerConnected( new ServerConnected( server1 ) );

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );

        final Server server1x = new ServerImpl( "server_id1x", "server_url1x", "my_server1x",
                                                null, null, ContainerStatus.STARTED, ConnectionType.REMOTE,
                                                Collections.<Container>emptyList(), Collections.<String, String>emptyMap(),
                                                Collections.<ContainerRef>emptyList() );

        presenter.onServerConnected( new ServerConnected( server1x ) );

        verify( view, times( 3 ) ).addBox( any( BoxPresenter.class ) );

        final Container container1 = new ContainerImpl( "server_id2x", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final Container container2 = new ContainerImpl( "server_id2x", "my_container_id2", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        final Server server2x = new ServerImpl( "server_id2x", "server_url2x", "my_server2x",
                                                null, null, ContainerStatus.STARTED, ConnectionType.REMOTE,
                                                Arrays.asList( container1, container2 ), Collections.<String, String>emptyMap(),
                                                Collections.<ContainerRef>emptyList() );

        presenter.onServerConnected( new ServerConnected( server2x ) );

        verify( view, times( 4 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );
    }

    @Test
    public void testOnServerError() {
        verify( service, times( 0 ) ).listServers();
        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );

        final Container container1 = new ContainerImpl( "server_id2x", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final Container container2 = new ContainerImpl( "server_id2x", "my_container_id2", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        final Server server2x = new ServerImpl( "server_id2x", "server_url2x", "my_server2x",
                                                null, null, ContainerStatus.STARTED, ConnectionType.REMOTE,
                                                Arrays.asList( container1, container2 ), Collections.<String, String>emptyMap(),
                                                Collections.<ContainerRef>emptyList() );

        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( server2x );
        }} );

        presenter.onOpen();

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        final ServerRef serverRef1Error = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                             null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                             Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );

        presenter.onServerError( new ServerOnError( serverRef1Error, "OPS!" ) );

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );

        final Server server2xErrror = new ServerImpl( "server_id2x", "server_url2x", "my_server2x",
                                                      null, null, ContainerStatus.ERROR, ConnectionType.REMOTE,
                                                      Arrays.asList( container1, container2 ), Collections.<String, String>emptyMap(),
                                                      Collections.<ContainerRef>emptyList() );

        presenter.onServerError( new ServerOnError( server2xErrror, "OPS!" ) );

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );
    }

    @Test
    public void testOnContainerCreated() {
        verify( service, times( 0 ) ).listServers();
        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
        }} );

        presenter.onOpen();

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );

        final Container container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );

        presenter.onContainerCreated( new ContainerCreated( container1 ) );

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 1 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        final Container container2 = new ContainerImpl( "server_id1", "my_container_id2", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );
        presenter.onContainerCreated( new ContainerCreated( container2 ) );

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        final Container container3 = new ContainerImpl( "server_id1", "my_container_id2", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );
        presenter.onContainerCreated( new ContainerCreated( container3 ) );

        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 2 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );
    }

    @Test
    public void testOnContainerDeleted() {
        verify( service, times( 0 ) ).listServers();
        final ContainerRef container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ContainerRef container2 = new ContainerImpl( "server_id1", "my_container_id2", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container1, container2 ) );

        final ContainerRef container3 = new ContainerImpl( "server_id2", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container3 ) );

        final ServerRef serverRef3 = new ServerRefImpl( "server_id3", "server_url3", "my_server3",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
            add( serverRef3 );
        }} );

        presenter.onOpen();

        verify( view, times( 3 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 3 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        presenter.onContainerDeleted( new ContainerDeleted( "server_id1xxx", "my_container_id" ) );

        verify( view, times( 0 ) ).removeBox( any( BoxPresenter.class ) );
        verify( beanManager, times( 0 ) ).destroyBean( any() );

        presenter.onContainerDeleted( new ContainerDeleted( "server_id1", "my_container_id" ) );

        verify( view, times( 1 ) ).removeBox( any( BoxPresenter.class ) );
        verify( beanManager, times( 1 ) ).destroyBean( any() );

        presenter.onContainerDeleted( new ContainerDeleted( "server_id2", "my_container_id" ) );

        verify( view, times( 2 ) ).removeBox( any( BoxPresenter.class ) );
        verify( beanManager, times( 2 ) ).destroyBean( any() );
    }

    @Test
    public void testOnServerDeleted() {
        verify( service, times( 0 ) ).listServers();
        final ContainerRef container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ContainerRef container2 = new ContainerImpl( "server_id1", "my_container_id2", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container1, container2 ) );

        final ContainerRef container3 = new ContainerImpl( "server_id2", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container3 ) );

        final ServerRef serverRef3 = new ServerRefImpl( "server_id3", "server_url3", "my_server3",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
            add( serverRef3 );
        }} );

        presenter.onOpen();

        verify( view, times( 3 ) ).addBox( any( BoxPresenter.class ) );
        verify( view, times( 3 ) ).addBox( any( BoxPresenter.class ), any( BoxPresenter.class ) );

        presenter.onServerDeleted( new ServerDeleted( "cccccc" ) );

        verify( view, times( 0 ) ).removeBox( any( BoxPresenter.class ) );
        verify( beanManager, times( 0 ) ).destroyBean( any() );

        presenter.onServerDeleted( new ServerDeleted( "server_id1" ) );

        verify( view, times( 3 ) ).removeBox( any( BoxPresenter.class ) );
        verify( beanManager, times( 3 ) ).destroyBean( any() );

        presenter.onServerDeleted( new ServerDeleted( "server_id3" ) );

        verify( view, times( 4 ) ).removeBox( any( BoxPresenter.class ) );
        verify( beanManager, times( 4 ) ).destroyBean( any() );
    }
}
