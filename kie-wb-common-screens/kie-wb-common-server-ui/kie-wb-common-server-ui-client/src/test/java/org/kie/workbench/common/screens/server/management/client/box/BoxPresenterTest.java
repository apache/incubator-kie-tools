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

package org.kie.workbench.common.screens.server.management.client.box;

import java.util.Collections;
import java.util.HashMap;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfoUpdateEvent;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BoxPresenterTest {

    private BoxPresenter boxPresenter;

    @Mock
    private BoxPresenter.View boxView;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<ContainerInfoUpdateEvent> event;

    @Before
    public void setup() {
        boxPresenter = new BoxPresenter( boxView, placeManager, event );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                boxPresenter.onSelect();
                return null;
            }
        } ).when( boxView ).onSelect();

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                boxPresenter.onUnSelect();
                return null;
            }
        } ).when( boxView ).onDeselect();

        assertEquals( boxView, boxPresenter.getView() );
    }

    @Test
    public void testSetupLoadingServerRef() {
        boxPresenter.setup( new ServerRefImpl( "my_id", "http://localhost", "my_server", "admin", null, ContainerStatus.LOADING, ConnectionType.REMOTE, new HashMap<String, String>() {{
            put( "version", "0.1" );
        }}, Collections.<ContainerRef>emptyList() ) );

        assertEquals( "(id: 'my_id', version: 0.1)", boxPresenter.getDescription() );

        generalServerTest();
    }

    @Test
    public void testSetupLoadingServer() {
        boxPresenter.setup( new ServerImpl( "my_id", "http://localhost", "my_server", null, null, ContainerStatus.LOADING, ConnectionType.REMOTE, Collections.<Container>emptyList(), Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() ) );

        assertEquals( "(id: 'my_id', version: unknown)", boxPresenter.getDescription() );

        generalServerTest();
    }

    @Test
    public void testSetupLoadingContainerRef() {
        boxPresenter.setup( new ContainerRefImpl( "my_id", "my_container_id", ContainerStatus.LOADING, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) );

        assertEquals( "my_container_id", boxPresenter.getName() );
        assertEquals( BoxType.CONTAINER, boxPresenter.getType() );
        assertEquals( "com.example:example-artifact-0.1.Final", boxPresenter.getDescription() );

        boxPresenter.openBoxInfo();

        verify( event, times( 1 ) ).fire( any( ContainerInfoUpdateEvent.class ) );
        verify( placeManager, times( 1 ) ).goTo( "ContainerInfo" );

        boxPresenter.openAddScreen();
        verify( placeManager, times( 0 ) ).goTo( "NewContainerForm" );

        testSelection();

        testVisibility( "my_container_id" );

        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "x_my_id", "my_container_id", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) ) );
        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "my_id", "x_my_container_id", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) ) );
        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "my_id", "my_container_id", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) ) );
        assertEquals( ContainerStatus.STOPPED, boxPresenter.getStatus() );
    }

    @Test
    public void testSetupLoadingContainerRefNoGAV() {
        boxPresenter.setup( new ContainerRefImpl( "my_id", "my_container_id", ContainerStatus.LOADING, null, null, null ) );

        assertEquals( "my_container_id", boxPresenter.getName() );
        assertEquals( BoxType.CONTAINER, boxPresenter.getType() );
        assertEquals( "Unknown Container", boxPresenter.getDescription() );

        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "x_my_id", "my_container_id", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) ) );
        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "my_id", "x_my_container_id", ContainerStatus.STOPPED, null, null, null ) ) );
        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "my_id", "my_container_id", ContainerStatus.STOPPED, null, null, null ) ) );
        assertEquals( ContainerStatus.STOPPED, boxPresenter.getStatus() );
    }

    @Test
    public void testSetupLoadingContainer() {
        final Container container = new ContainerImpl( "my_id", "my_container_id", ContainerStatus.LOADING, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );
        boxPresenter.setup( container );

        assertEquals( "my_container_id", boxPresenter.getName() );
        assertEquals( BoxType.CONTAINER, boxPresenter.getType() );
        assertEquals( "com.example:example-artifact-0.1.Final(com.example:example-artifact-LATEST)", boxPresenter.getDescription() );

        boxPresenter.openBoxInfo();

        verify( event, times( 1 ) ).fire( any( ContainerInfoUpdateEvent.class ) );
        verify( placeManager, times( 1 ) ).goTo( "ContainerInfo" );

        boxPresenter.openAddScreen();
        verify( placeManager, times( 0 ) ).goTo( "NewContainerForm" );

        testSelection();

        testVisibility( "my_container_id" );

        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        container.setStatus( ContainerStatus.STARTED );
        boxPresenter.onContainerStarted( new ContainerStarted( container ) );
        assertEquals( ContainerStatus.STARTED, boxPresenter.getStatus() );

        container.getResolvedReleasedId().setVersion( "0.2.Final" );
        boxPresenter.onContainerUpdated( new ContainerUpdated( container ) );
        assertEquals( "com.example:example-artifact-0.2.Final(com.example:example-artifact-LATEST)", boxPresenter.getDescription() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "my_id", "x_my_container_id", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) ) );
        assertEquals( ContainerStatus.STARTED, boxPresenter.getStatus() );

        boxPresenter.onContainerStopped( new ContainerStopped( new ContainerRefImpl( "my_id", "my_container_id", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "0.1.Final" ), null, null ) ) );
        assertEquals( ContainerStatus.STOPPED, boxPresenter.getStatus() );
    }

    private void testVisibility( String id ) {
        assertEquals( true, boxPresenter.isVisible() );
        boxPresenter.filter( id.substring( 0, 2 ) );
        assertEquals( true, boxPresenter.isVisible() );
        boxPresenter.filter( id );
        assertEquals( true, boxPresenter.isVisible() );
        boxPresenter.filter( id + "xx" );
        assertEquals( false, boxPresenter.isVisible() );

    }

    private void testSelection() {
        assertEquals( false, boxPresenter.isSelected() );
        boxPresenter.select( true );
        assertEquals( true, boxPresenter.isSelected() );

        assertEquals( true, boxPresenter.isSelected() );
        boxPresenter.select( false );
        assertEquals( false, boxPresenter.isSelected() );
    }

    private void generalServerTest() {
        assertEquals( "my_server", boxPresenter.getName() );
        assertEquals( BoxType.SERVER, boxPresenter.getType() );

        boxPresenter.openBoxInfo();

        verify( event, times( 0 ) ).fire( any( ContainerInfoUpdateEvent.class ) );
        verify( placeManager, times( 0 ) ).goTo( "ContainerInfo" );

        boxPresenter.openAddScreen();  // we allow to add containers offline so every time we hit opeAddScreen it will be allowed
        verify( placeManager, times( 1 ) ).goTo(new DefaultPlaceRequest( "NewContainerForm" ).addParameter( "serverId", "my_id" ));

        testSelection();

        testVisibility( "my_server" );

        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );
        boxPresenter.onServerConnected( new ServerConnected( new ServerImpl( "my_id", "http://localhost", "my_server", "admin", null, ContainerStatus.STARTED, ConnectionType.REMOTE, Collections.<Container>emptyList(), Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() ) ) );
        assertEquals( ContainerStatus.STARTED, boxPresenter.getStatus() );

        boxPresenter.openAddScreen();
        verify( placeManager, times( 2 ) ).goTo( new DefaultPlaceRequest( "NewContainerForm" ).addParameter( "serverId", "my_id" ) );
        boxPresenter.onServerConnected( new ServerConnected( new ServerImpl( "my_id", "http://localhost", "my_server", "admin", null, ContainerStatus.LOADING, ConnectionType.REMOTE, Collections.<Container>emptyList(), Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() ) ) );
        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );

        boxPresenter.openAddScreen();
        verify( placeManager, times( 3 ) ).goTo( new DefaultPlaceRequest( "NewContainerForm" ).addParameter( "serverId", "my_id" ) );

        boxPresenter.onServerConnected( new ServerConnected( new ServerImpl( "xmy_id", "http://localhost", "my_server", "admin", null, ContainerStatus.STARTED, ConnectionType.REMOTE, Collections.<Container>emptyList(), Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() ) ) );
        boxPresenter.openAddScreen();
        assertEquals( ContainerStatus.LOADING, boxPresenter.getStatus() );
        verify( placeManager, times( 4 ) ).goTo( new DefaultPlaceRequest( "NewContainerForm" ).addParameter( "serverId", "my_id" ) );

        boxPresenter.onServerOnError( new ServerOnError( new ServerRefImpl( "my_id", "http://localhost", "my_server", "admin", null, ContainerStatus.ERROR, ConnectionType.REMOTE, Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() ), "message" ) );
        assertEquals( ContainerStatus.ERROR, boxPresenter.getStatus() );
        boxPresenter.openAddScreen();
        verify( placeManager, times( 5 ) ).goTo( new DefaultPlaceRequest( "NewContainerForm" ).addParameter( "serverId", "my_id" ) );
    }

}