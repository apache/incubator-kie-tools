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

package org.kie.workbench.common.screens.server.management.client.container;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfoUpdateEvent;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static junit.framework.Assert.*;
import static org.kie.workbench.common.screens.server.management.client.container.ContainerInfoPresenter.State.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerInfoOperationTest {

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
    public void testScanNow() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), ScannerStatus.STOPPED, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );

        when( service.scanNow( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.UNKNOWN, "scanned", null ) );

        containerInfoPresenter.scanNow();

        assertEquals( ScannerStatus.UNKNOWN, containerInfoPresenter.getScannerStatus() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );
    }

    @Test
    public void testScan() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), ScannerStatus.STOPPED, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );

        when( service.startScanner( "my_id", "my_container", 1000 ) ).thenReturn( new ScannerOperationResult( ScannerStatus.STARTED, "scanned", null ) );

        containerInfoPresenter.startScanner( "1000" );

        assertEquals( ScannerStatus.STARTED, containerInfoPresenter.getScannerStatus() );

        assertEquals( DISABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( DISABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getUpgradeState() );

        when( service.startScanner( "my_id", "my_container", 1000 ) ).thenReturn( new ScannerOperationResult( ScannerStatus.ERROR, "scanned", null ) );

        containerInfoPresenter.startScanner( "1000" );

        assertEquals( ScannerStatus.ERROR, containerInfoPresenter.getScannerStatus() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );

        when( service.stopScanner( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.STOPPED, "scanned", null ) );

        containerInfoPresenter.stopScanner();

        assertEquals( ScannerStatus.STOPPED, containerInfoPresenter.getScannerStatus() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );

        when( service.stopScanner( "my_id", "my_container" ) ).thenReturn( new ScannerOperationResult( ScannerStatus.ERROR, "scanned", null ) );

        containerInfoPresenter.stopScanner();

        assertEquals( ScannerStatus.ERROR, containerInfoPresenter.getScannerStatus() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );

        try {
            containerInfoPresenter.startScanner( " " );
            fail( "expected exception" );
        } catch ( final IllegalArgumentException ex ) {

        } catch ( final Exception ex ) {
            fail( "unexpected exception" );
        }

        try {
            containerInfoPresenter.startScanner( "ss" );
            fail( "expected exception" );
        } catch ( final IllegalArgumentException ex ) {

        } catch ( final Exception ex ) {
            fail( "unexpected exception" );
        }

        try {
            containerInfoPresenter.startScanner( null );
            fail( "expected exception" );
        } catch ( final IllegalArgumentException ex ) {

        } catch ( final Exception ex ) {
            fail( "unexpected exception" );
        }
    }

    @Test
    public void testUpgrade() {
        final Container container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), ScannerStatus.STOPPED, null, new GAV( "com.example", "example-artifact", "0.1.Final" ) );

        when( service.getContainerInfo( "my_id", "my_container" ) ).thenReturn( container );

        containerInfoPresenter.onContainerInfo( new ContainerInfoUpdateEvent( "my_id", "my_container" ) );

        assertEquals( "com.example", containerInfoPresenter.getGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getArtifactId() );
        assertEquals( "LATEST", containerInfoPresenter.getVersion() );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.1.Final", containerInfoPresenter.getResolvedVersion() );

        assertEquals( ENABLED, containerInfoPresenter.getScanNowState() );
        assertEquals( ENABLED, containerInfoPresenter.getStartScannerState() );
        assertEquals( DISABLED, containerInfoPresenter.getStopScannerState() );
        assertEquals( ENABLED, containerInfoPresenter.getUpgradeState() );

        final GAV upgrade = new GAV( "com.example", "example-artifact", "0.2.Final" );
        doAnswer( new Answer<Void>() {
            public Void answer( InvocationOnMock invocation ) {
                final Container _container = new ContainerImpl( "my_id", "my_container", ContainerStatus.STARTED, new GAV( "com.example", "example-artifact", "LATEST" ), ScannerStatus.STOPPED, null, new GAV( "com.example", "example-artifact", "0.2.Final" ) );
                containerInfoPresenter.onContainerUpdated( new ContainerUpdated( _container ) );
                return null;
            }
        } ).when( service ).upgradeContainer( "my_id", "my_container", upgrade );

        containerInfoPresenter.upgrade( upgrade );

        assertEquals( "com.example", containerInfoPresenter.getResolvedGroupId() );
        assertEquals( "example-artifact", containerInfoPresenter.getResolvedArtifactId() );
        assertEquals( "0.2.Final", containerInfoPresenter.getResolvedVersion() );
    }

}
