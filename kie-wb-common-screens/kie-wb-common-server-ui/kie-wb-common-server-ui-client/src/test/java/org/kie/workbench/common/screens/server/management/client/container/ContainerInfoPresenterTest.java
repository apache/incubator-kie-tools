package org.kie.workbench.common.screens.server.management.client.container;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfoUpdateEvent;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.Menus;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerInfoPresenterTest {

    private ContainerInfoPresenter containerInfoPresenter;

    @Mock
    private ContainerInfoPresenter.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ServerManagementService service;

    private CallerMock<ServerManagementService> caller;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Before
    public void setup() {
        caller = new CallerMock<ServerManagementService>( service );

        containerInfoPresenter = new ContainerInfoPresenter( view, placeManager, caller, notification, changeTitleWidgetEvent );

        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                containerInfoPresenter.onClose();
                return null;
            }
        } ).when( placeManager ).forceClosePlace( "ContainerInfo" );

        assertEquals( view, containerInfoPresenter.getView() );
        containerInfoPresenter.onStartup( new DefaultPlaceRequest( "ContainerInfo" ) );
        containerInfoPresenter.onOpen();
    }

    @Test
    public void testGeneralContainerUpdate() {
        Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        assertEquals( "my_id", containerInfoPresenter.getServerId() );
        assertEquals( "my_container", containerInfoPresenter.getContainerId() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( ContainerStatus.STARTED, containerInfoPresenter.getStatus() );
        assertEquals( ScannerStatus.UNKNOWN, containerInfoPresenter.getScannerStatus() );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "my_id/containers/my_container", containerInfoPresenter.getEndpoint() );

        container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );
        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );
        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        verify( changeTitleWidgetEvent, times( 2 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 2 ) ).getContainerInfo( "my_id", "my_container" );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), ScannerStatus.STOPPED, 1L, new GAV( "com.example", "example-artifact", "0.1.Final" ) );
        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );
        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "1", containerInfoPresenter.getPollInterval() );
        assertEquals( ContainerStatus.STARTED, containerInfoPresenter.getStatus() );
        assertEquals( ScannerStatus.STOPPED, containerInfoPresenter.getScannerStatus() );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        containerInfoPresenter.onClose();
        assertTrue( containerInfoPresenter.isClosed() );

        container = new ContainerImpl( "xx", "xx", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), ScannerStatus.STOPPED, 1L, new GAV( "com.example", "example-artifact", "0.1.Final" ) );
        when( service.getContainerInfo( "xx", "xx" ) ).thenReturn( container );
        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "xx", "xx" ) );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( null, containerInfoPresenter.getStatus() );
        assertEquals( null, containerInfoPresenter.getScannerStatus() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "", containerInfoPresenter.getServerId() );
        assertEquals( "", containerInfoPresenter.getContainerId() );
    }

    @Test
    public void testOnServerConnected() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        containerInfoPresenter.onServerConnected( new ServerConnected( new ServerImpl( "my_id", "http://localhost", null, null, null, ContainerStatus.STARTED, ConnectionType.REMOTE, new ArrayList<Container>() {{
            add( container );
        }}, null, null ) ) );

        assertEquals( "my_id", containerInfoPresenter.getServerId() );
        assertEquals( "my_container", containerInfoPresenter.getContainerId() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( ContainerStatus.STARTED, containerInfoPresenter.getStatus() );
        assertEquals( ScannerStatus.UNKNOWN, containerInfoPresenter.getScannerStatus() );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        containerInfoPresenter.onServerConnected( new ServerConnected( new ServerImpl( "my_id", "http://localhost", null, null, null, ContainerStatus.STARTED, ConnectionType.REMOTE, new ArrayList<Container>(), null, null ) ) );

        assertTrue( containerInfoPresenter.isClosed() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( null, containerInfoPresenter.getStatus() );
        assertEquals( null, containerInfoPresenter.getScannerStatus() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "", containerInfoPresenter.getServerId() );
        assertEquals( "", containerInfoPresenter.getContainerId() );

        containerInfoPresenter.onServerConnected( new ServerConnected( new ServerImpl( "my_id", "http://localhost", null, null, null, ContainerStatus.STARTED, ConnectionType.REMOTE, new ArrayList<Container>() {{
            add( container );
        }}, null, null ) ) );

        assertTrue( containerInfoPresenter.isClosed() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( null, containerInfoPresenter.getStatus() );
        assertEquals( null, containerInfoPresenter.getScannerStatus() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "", containerInfoPresenter.getServerId() );
        assertEquals( "", containerInfoPresenter.getContainerId() );
    }

    @Test
    public void testOnContainerUpdated() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        final Container container2 = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        containerInfoPresenter.onContainerUpdated( new ContainerUpdated( container2 ) );

        assertEquals( "my_id", containerInfoPresenter.getServerId() );
        assertEquals( "my_container", containerInfoPresenter.getContainerId() );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        placeManager.forceClosePlace( "ContainerInfo" );

        assertTrue( containerInfoPresenter.isClosed() );

        containerInfoPresenter.onContainerUpdated( new ContainerUpdated( container2 ) );

        assertTrue( containerInfoPresenter.isClosed() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( null, containerInfoPresenter.getStatus() );
        assertEquals( null, containerInfoPresenter.getScannerStatus() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "", containerInfoPresenter.getServerId() );
        assertEquals( "", containerInfoPresenter.getContainerId() );
    }

    @Test
    public void testOnContainerStarted() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        final Container container2 = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        containerInfoPresenter.onContainerStarted( new ContainerStarted( container2 ) );

        assertEquals( "my_id", containerInfoPresenter.getServerId() );
        assertEquals( "my_container", containerInfoPresenter.getContainerId() );

        assertEquals( ContainerStatus.STARTED, containerInfoPresenter.getStatus() );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        placeManager.forceClosePlace( "ContainerInfo" );

        assertTrue( containerInfoPresenter.isClosed() );

        containerInfoPresenter.onContainerStarted( new ContainerStarted( container2 ) );

        assertTrue( containerInfoPresenter.isClosed() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( null, containerInfoPresenter.getStatus() );
        assertEquals( null, containerInfoPresenter.getScannerStatus() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "", containerInfoPresenter.getServerId() );
        assertEquals( "", containerInfoPresenter.getContainerId() );
    }

    @Test
    public void testOnContainerStopped() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        final Container container2 = new ContainerImpl( "my_id", "my_container", ContainerStatus.STOPPED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        containerInfoPresenter.onContainerStopped( new ContainerStopped( container2 ) );

        assertEquals( "my_id", containerInfoPresenter.getServerId() );
        assertEquals( "my_container", containerInfoPresenter.getContainerId() );

        assertEquals( ContainerStatus.STOPPED, containerInfoPresenter.getStatus() );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        placeManager.forceClosePlace( "ContainerInfo" );

        assertTrue( containerInfoPresenter.isClosed() );

        containerInfoPresenter.onContainerStopped( new ContainerStopped( container2 ) );

        assertTrue( containerInfoPresenter.isClosed() );

        assertEquals( "", containerInfoPresenter.getPollInterval() );
        assertEquals( null, containerInfoPresenter.getStatus() );
        assertEquals( null, containerInfoPresenter.getScannerStatus() );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        assertEquals( "", containerInfoPresenter.getServerId() );
        assertEquals( "", containerInfoPresenter.getContainerId() );
    }

    @Test
    public void testOnServerError() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        final ServerRef serverRef = mock( ServerRef.class );
        when( serverRef.getId() ).thenReturn( "my_id" );

        containerInfoPresenter.onServerError( new ServerOnError( serverRef, "error" ) );

        assertTrue( containerInfoPresenter.isClosed() );
    }

    @Test
    public void testOnServerDeleted() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        containerInfoPresenter.onServerDeleted( new ServerDeleted( "my_id" ) );

        assertTrue( containerInfoPresenter.isClosed() );
    }

    @Test
    public void testOnContainerDeleted() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        containerInfoPresenter.onContainerDeleted( new ContainerDeleted( "my_id", "my_container" ) );

        assertTrue( containerInfoPresenter.isClosed() );
    }

    @Test
    public void testScanNow() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        when( service.scanNow( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.UNKNOWN, "scanned", null ) );

        containerInfoPresenter.scanNow();

        assertEquals( ScannerStatus.UNKNOWN, containerInfoPresenter.getScannerStatus() );

        assertFalse( containerInfoPresenter.isClosed() );

        verify( notification, times( 0 ) ).fire( any( NotificationEvent.class ) );

        when( service.scanNow( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.ERROR, "scanned", null ) );

        containerInfoPresenter.scanNow();

        assertEquals( ScannerStatus.ERROR, containerInfoPresenter.getScannerStatus() );

        assertFalse( containerInfoPresenter.isClosed() );

        verify( notification, times( 1 ) ).fire( any( NotificationEvent.class ) );

    }

    @Test
    public void testScan() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "", containerInfoPresenter.getResolvedVersion() );

        verify( changeTitleWidgetEvent, times( 1 ) ).fire( any( ChangeTitleWidgetEvent.class ) );
        verify( service, times( 1 ) ).getContainerInfo( "my_id", "my_container" );

        when( service.startScanner( "my_id", "my_container", 1000 ) ).thenReturn( new ScannerOperationResult( ScannerStatus.STARTED, "scanned", 1000L ) );

        containerInfoPresenter.startScanner( "1000" );

        verify( service, times( 1 ) ).startScanner( "my_id", "my_container", 1000 );

        assertEquals( ScannerStatus.STARTED, containerInfoPresenter.getScannerStatus() );

        assertFalse( containerInfoPresenter.isClosed() );

        verify( notification, times( 0 ) ).fire( any( NotificationEvent.class ) );

        when( service.startScanner( "my_id", "my_container", 1000 ) ).thenReturn( new ScannerOperationResult( ScannerStatus.ERROR, "scanned", null ) );

        containerInfoPresenter.startScanner( "1000" );

        verify( service, times( 2 ) ).startScanner( "my_id", "my_container", 1000 );

        assertEquals( ScannerStatus.ERROR, containerInfoPresenter.getScannerStatus() );

        assertFalse( containerInfoPresenter.isClosed() );

        verify( notification, times( 1 ) ).fire( any( NotificationEvent.class ) );

        when( service.stopScanner( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.STOPPED, "scanned", null ) );

        containerInfoPresenter.stopScanner();

        assertEquals( ScannerStatus.STOPPED, containerInfoPresenter.getScannerStatus() );

        verify( service, times( 1 ) ).stopScanner( "my_id", "my_container" );

        when( service.stopScanner( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.ERROR, "scanned", null ) );

        containerInfoPresenter.stopScanner();

        assertEquals( ScannerStatus.ERROR, containerInfoPresenter.getScannerStatus() );

        verify( service, times( 2 ) ).stopScanner( "my_id", "my_container" );
        verify( notification, times( 2 ) ).fire( any( NotificationEvent.class ) );
    }

    @Test
    public void testMenu() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), null, null, null );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );
        when( view.getCustomMenuItem( any( Command.class ) ) ).thenAnswer( new Answer<IsWidget>() {
            @Override
            public IsWidget answer( InvocationOnMock invocationOnMock ) throws Throwable {
                containerInfoPresenter.close();
                return mock( IsWidget.class );
            }
        } );

        assertFalse( containerInfoPresenter.isClosed() );

        final Menus menus = containerInfoPresenter.buildMenu();

        assertNotNull( menus );

        assertEquals( 1, menus.getItems().size() );
        assertTrue( menus.getItems().get( 0 ) instanceof MenuCustom );

        final MenuCustom menuCustom = (MenuCustom) menus.getItems().get( 0 );
        //triggers the mock exec - hack for now
        menuCustom.build();

        assertTrue( containerInfoPresenter.isClosed() );
    }
}
