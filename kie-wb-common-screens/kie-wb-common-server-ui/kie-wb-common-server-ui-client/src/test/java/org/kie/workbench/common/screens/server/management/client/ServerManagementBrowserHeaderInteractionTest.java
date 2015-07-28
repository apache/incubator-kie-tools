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
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.events.HeaderClearSelectionEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderDeleteEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderFilterEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderRefreshEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderSelectAllEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStartEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStopEvent;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagementBrowserHeaderInteractionTest {

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

    private List<BoxPresenter> presenterCollection;

    @Before
    public void setup() {
        presenterCollection = new ArrayList<BoxPresenter>();

        caller = new CallerMock<ServerManagementService>( service );

        when( beanManager.lookupBean( BoxPresenter.class ) ).thenReturn( beanDef );

        when( beanDef.newInstance() ).thenAnswer( new Answer<BoxPresenter>() {
            @Override
            public BoxPresenter answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final BoxPresenter mocked = mock( BoxPresenter.class );
                presenterCollection.add( mocked );
                return mocked;
            }
        } );

        final ArgumentCaptor<List> list1 = ArgumentCaptor.forClass( List.class );
        final ArgumentCaptor<List> list2 = ArgumentCaptor.forClass( List.class );
        final ArgumentCaptor<Command> selectedCommand = ArgumentCaptor.forClass( Command.class );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                selectedCommand.getValue().execute();
                return null;
            }
        } ).when( view ).confirmDeleteOperation( list1.capture(), list2.capture(), selectedCommand.capture() );

        presenter = new ServerManagementBrowserPresenter( view, beanManager, headerPresenter, caller );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testOnHeaderSelection() {
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

        for ( int i = 0; i < presenterCollection.size(); i++ ) {
            if ( i % 2 == 0 ) {
                when( presenterCollection.get( i ).isVisible() ).thenReturn( true );
            } else {
                when( presenterCollection.get( i ).isVisible() ).thenReturn( false );
            }
        }

        presenter.onHeaderSelectAll( new HeaderSelectAllEvent( mock( HeaderPresenter.class ) ) );

        for ( final BoxPresenter boxPresenter : presenterCollection ) {
            verify( boxPresenter, times( 0 ) ).select( true );
        }

        presenter.onHeaderSelectAll( new HeaderSelectAllEvent( headerPresenter ) );

        for ( int i = 0; i < presenterCollection.size(); i++ ) {
            if ( i % 2 == 0 ) {
                verify( presenterCollection.get( i ), times( 1 ) ).select( true );
            } else {
                verify( presenterCollection.get( i ), times( 1 ) ).select( false );
            }
        }

        presenter.onHeaderClearSelection( new HeaderClearSelectionEvent( mock( HeaderPresenter.class ) ) );

        for ( int i = 0; i < presenterCollection.size(); i++ ) {
            if ( i % 2 == 0 ) {
                verify( presenterCollection.get( i ), times( 1 ) ).select( true );
            } else {
                verify( presenterCollection.get( i ), times( 1 ) ).select( false );
            }
        }

        presenter.onHeaderClearSelection( new HeaderClearSelectionEvent( headerPresenter ) );

        for ( int i = 0; i < presenterCollection.size(); i++ ) {
            if ( i % 2 == 0 ) {
                verify( presenterCollection.get( i ), times( 1 ) ).select( true );
            } else {
                verify( presenterCollection.get( i ), times( 2 ) ).select( false );
            }
        }
    }

    @Test
    public void testOnRefresh() {
        verify( service, times( 0 ) ).listServers();

        presenter.onOpen();

        verify( service, times( 1 ) ).listServers();

        presenter.onHeaderRefresh( new HeaderRefreshEvent( mock( HeaderPresenter.class ) ) );

        verify( service, times( 0 ) ).refresh();

        presenter.onHeaderRefresh( new HeaderRefreshEvent( headerPresenter ) );

        verify( service, times( 1 ) ).refresh();
    }

    @Test
    public void testOnHeaderFilter() {
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

        for ( final BoxPresenter boxPresenter : presenterCollection ) {
            verify( boxPresenter, times( 0 ) ).filter( anyString() );
        }

        presenter.onHeaderFilter( new HeaderFilterEvent( mock( HeaderPresenter.class ), "xxx" ) );

        for ( final BoxPresenter boxPresenter : presenterCollection ) {
            verify( boxPresenter, times( 0 ) ).filter( anyString() );
        }

        presenter.onHeaderFilter( new HeaderFilterEvent( headerPresenter, "xxx" ) );

        for ( final BoxPresenter boxPresenter : presenterCollection ) {
            verify( boxPresenter, times( 1 ) ).filter( anyString() );
        }

    }

    @Test
    public void testOnHeaderStart() {
        verify( service, times( 0 ) ).listServers();
        final ContainerRef container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );

        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container1 ) );

        final ContainerRef container2 = new ContainerImpl( "server_id2", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container2 ) );

        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
        }} );

        presenter.onOpen();

        presenter.onHeaderStart( new HeaderStartEvent( mock( HeaderPresenter.class ) ) );

        verify( service, times( 0 ) ).startContainers( anyMap() );

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( false ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( true );//server_id1, my_container_id

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );

            presenter.onHeaderStart( new HeaderStartEvent( headerPresenter ) );

            verify( service, times( 1 ) ).startContainers( selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();

            assertNotNull( containers.get( "server_id1" ) );

            assertTrue( containers.get( "server_id1" ).contains( "my_container_id" ) );
        }

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( true ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( false );//server_id1, my_container_id
            when( presenterCollection.get( 2 ).isSelected() ).thenReturn( true );//server_id2

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );

            presenter.onHeaderStart( new HeaderStartEvent( headerPresenter ) );

            verify( service, times( 2 ) ).startContainers( selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();

            assertTrue( containers.isEmpty() );
        }

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( false ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( true );//server_id1, my_container_id

            when( presenterCollection.get( 2 ).isSelected() ).thenReturn( false );//server_id2
            when( presenterCollection.get( 3 ).isSelected() ).thenReturn( true );//server_id2, my_container_id

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );

            presenter.onHeaderStart( new HeaderStartEvent( headerPresenter ) );

            verify( service, times( 3 ) ).startContainers( selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();

            assertNotNull( containers.get( "server_id1" ) );

            assertTrue( containers.get( "server_id1" ).contains( "my_container_id" ) );

            assertNotNull( containers.get( "server_id2" ) );

            assertTrue( containers.get( "server_id2" ).contains( "my_container_id" ) );
        }
    }

    @Test
    public void testOnHeaderStop() {
        verify( service, times( 0 ) ).listServers();
        final ContainerRef container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );

        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container1 ) );

        final ContainerRef container2 = new ContainerImpl( "server_id2", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container2 ) );

        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
        }} );

        presenter.onOpen();

        presenter.onHeaderStop( new HeaderStopEvent( mock( HeaderPresenter.class ) ) );

        verify( service, times( 0 ) ).stopContainers( anyMap() );

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( false ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( true );//server_id1, my_container_id

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );

            presenter.onHeaderStop( new HeaderStopEvent( headerPresenter ) );

            verify( service, times( 1 ) ).stopContainers( selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();

            assertNotNull( containers.get( "server_id1" ) );

            assertTrue( containers.get( "server_id1" ).contains( "my_container_id" ) );
        }

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( true ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( false );//server_id1, my_container_id
            when( presenterCollection.get( 2 ).isSelected() ).thenReturn( true );//server_id2

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );

            presenter.onHeaderStop( new HeaderStopEvent( headerPresenter ) );

            verify( service, times( 2 ) ).stopContainers( selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();

            assertTrue( containers.isEmpty() );
        }

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( false ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( true );//server_id1, my_container_id

            when( presenterCollection.get( 2 ).isSelected() ).thenReturn( false );//server_id2
            when( presenterCollection.get( 3 ).isSelected() ).thenReturn( true );//server_id2, my_container_id

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );

            presenter.onHeaderStop( new HeaderStopEvent( headerPresenter ) );

            verify( service, times( 3 ) ).stopContainers( selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();

            assertNotNull( containers.get( "server_id1" ) );

            assertTrue( containers.get( "server_id1" ).contains( "my_container_id" ) );

            assertNotNull( containers.get( "server_id2" ) );

            assertTrue( containers.get( "server_id2" ).contains( "my_container_id" ) );
        }
    }

    @Test
    public void testOnHeaderDelete() {
        verify( service, times( 0 ) ).listServers();
        final ContainerRef container1 = new ContainerImpl( "server_id1", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );

        final ServerRef serverRef1 = new ServerRefImpl( "server_id1", "server_url1", "my_server1",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container1 ) );

        final ContainerRef container2 = new ContainerImpl( "server_id2", "my_container_id", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
        final ServerRef serverRef2 = new ServerRefImpl( "server_id2", "server_url2", "my_server2",
                                                        null, null, ContainerStatus.LOADING, ConnectionType.REMOTE,
                                                        Collections.<String, String>emptyMap(), Arrays.asList( container2 ) );

        when( service.listServers() ).thenReturn( new ArrayList<ServerRef>() {{
            add( serverRef1 );
            add( serverRef2 );
        }} );

        presenter.onOpen();

        presenter.onHeaderDelete( new HeaderDeleteEvent( mock( HeaderPresenter.class ) ) );

        verify( service, times( 0 ) ).deleteOp( anyCollection(), anyMap() );

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( false ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( true ); //server_id1, my_container_id

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );
            final ArgumentCaptor<List> selectedList = ArgumentCaptor.forClass( List.class );

            presenter.onHeaderDelete( new HeaderDeleteEvent( headerPresenter ) );

            verify( service, times( 1 ) ).deleteOp( selectedList.capture(), selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();
            final List<String> servers = selectedList.getValue();

            assertTrue( servers.isEmpty() );

            assertNotNull( containers.get( "server_id1" ) );

            assertTrue( containers.get( "server_id1" ).contains( "my_container_id" ) );
        }

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( true ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( false );//server_id1, my_container_id
            when( presenterCollection.get( 2 ).isSelected() ).thenReturn( true );//server_id2

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );
            final ArgumentCaptor<List> selectedList = ArgumentCaptor.forClass( List.class );

            presenter.onHeaderDelete( new HeaderDeleteEvent( headerPresenter ) );

            verify( service, times( 2 ) ).deleteOp( selectedList.capture(), selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();
            final List<String> servers = selectedList.getValue();

            assertTrue( servers.contains( "server_id1" ) );

            assertTrue( servers.contains( "server_id2" ) );

            assertTrue( containers.isEmpty() );
        }

        {
            when( presenterCollection.get( 0 ).isSelected() ).thenReturn( false ); //server_id1
            when( presenterCollection.get( 1 ).isSelected() ).thenReturn( true );//server_id1, my_container_id

            when( presenterCollection.get( 2 ).isSelected() ).thenReturn( false );//server_id2
            when( presenterCollection.get( 3 ).isSelected() ).thenReturn( true );//server_id2, my_container_id

            final ArgumentCaptor<Map> selectedMap = ArgumentCaptor.forClass( Map.class );
            final ArgumentCaptor<List> selectedList = ArgumentCaptor.forClass( List.class );

            presenter.onHeaderDelete( new HeaderDeleteEvent( headerPresenter ) );

            verify( service, times( 3 ) ).deleteOp( selectedList.capture(), selectedMap.capture() );

            final Map<String, List<String>> containers = selectedMap.getValue();
            final List<String> servers = selectedList.getValue();

            assertNotNull( containers.get( "server_id1" ) );

            assertTrue( containers.get( "server_id1" ).contains( "my_container_id" ) );

            assertNotNull( containers.get( "server_id2" ) );

            assertTrue( containers.get( "server_id2" ).contains( "my_container_id" ) );

            assertTrue( servers.isEmpty() );
        }
    }
}
