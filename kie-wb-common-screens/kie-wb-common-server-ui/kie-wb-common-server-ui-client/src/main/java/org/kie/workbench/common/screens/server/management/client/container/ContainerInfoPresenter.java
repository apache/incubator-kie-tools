/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.server.management.client.container;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfoUpdateEvent;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerDisconnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.kie.workbench.common.screens.server.management.client.container.ContainerInfoPresenter.State.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
@WorkbenchScreen(identifier = "ContainerInfo", preferredWidth = 500)
public class ContainerInfoPresenter {

    public enum State {
        ENABLED, DISABLED
    }

    public interface View extends UberView<ContainerInfoPresenter> {

        void cleanup();

        void setStatus( final ContainerStatus started );

        void setInterval( final String pollInterval );

        void setGroupId( final String groupId );

        void setArtifactId( final String artifactId );

        void setVersion( final String version );

        void setResolvedGroupId( final String resolvedGroupId );

        void setResolvedArtifactId( final String resolvedArtifactId );

        void setResolvedVersion( final String resolvedVersion );

        void setEndpoint( final List<ServerInstanceRef> endpoint );

        void setStartScannerState( final State state );

        void setStopScannerState( final State state );

        void setScanNowState( final State state );

        void setUpgradeState( final State state );

        IsWidget getCustomMenuItem( final Command onClick );
    }

    private final View view;

    private final PlaceManager placeManager;

    private final Caller<ServerManagementService> service;

    private final Event<NotificationEvent> notification;

    private final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest placeRequest;

    private boolean isClosed;

    private String serverId;
    private String containerId;

    private String pollInterval;
    private ScannerStatus scannerStatus;
    private ContainerStatus status;
    private String groupId;
    private String artifactId;
    private String version;
    private String resolvedGroupId;
    private String resolvedArtifactId;
    private String resolvedVersion;
    private List<ServerInstanceRef> endpoint;

    private State startScannerState;
    private State stopScannerState;
    private State scanNowState;
    private State upgradeState;

    @Inject
    public ContainerInfoPresenter( final View view,
                                   final PlaceManager placeManager,
                                   final Caller<ServerManagementService> service,
                                   final Event<NotificationEvent> notification,
                                   final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent ) {
        this.view = view;
        this.placeManager = placeManager;
        this.service = service;
        this.notification = notification;
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
    }

    @OnOpen
    public void onOpen() {
        isClosed = false;
    }

    @OnClose
    public void onClose() {
        isClosed = true;
        cleanup();
    }

    private void cleanup() {
        this.status = null;
        this.scannerStatus = null;
        this.serverId = this.containerId = this.pollInterval = this.groupId = this.artifactId = this.version = this.resolvedGroupId = this.resolvedArtifactId = this.resolvedVersion = "";
        this.endpoint = new ArrayList<ServerInstanceRef>();
        view.cleanup();
    }

    void onContainerInfo( @Observes final ContainerInfoUpdateEvent containerInfoUpdateEvent ) {
        if ( isClosed ) {
            return;
        }
        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest, "Container Info [" + containerInfoUpdateEvent.getContainerId() + "]" ) );
        serverId = containerInfoUpdateEvent.getServerId();
        containerId = containerInfoUpdateEvent.getContainerId();
        view.cleanup();

        service.call( new RemoteCallback<Container>() {
            @Override
            public void callback( final Container response ) {
                update( response );
            }
        } ).getContainerInfo( containerInfoUpdateEvent.getServerId(), containerInfoUpdateEvent.getContainerId() );
    }

    private void update( final ContainerRef container ) {
        this.status = container.getStatus();
        this.scannerStatus = container.getScannerStatus();

        if ( container.getPollInterval() != null ) {
            this.pollInterval = String.valueOf( container.getPollInterval().longValue() );
        } else {
            this.pollInterval = "";
        }

        if ( container.getReleasedId() != null ) {
            this.groupId = container.getReleasedId().getGroupId();
            this.artifactId = container.getReleasedId().getArtifactId();
            this.version = container.getReleasedId().getVersion();
        } else {
            this.groupId = this.artifactId = this.version = "";
        }

        if ( container.getManagedServers() != null ) {
            this.endpoint = container.getManagedServers();
        }

        if ( container instanceof Container && ( (Container) container ).getResolvedReleasedId() != null ) {
            this.resolvedGroupId = ( (Container) container ).getResolvedReleasedId().getGroupId();
            this.resolvedArtifactId = ( (Container) container ).getResolvedReleasedId().getArtifactId();
            this.resolvedVersion = ( (Container) container ).getResolvedReleasedId().getVersion();
        } else {
            this.resolvedGroupId = this.resolvedArtifactId = this.resolvedVersion = "";
        }

        if ( status.equals( ContainerStatus.STARTED ) ) {
            this.startScannerState = ENABLED;
            this.stopScannerState = ENABLED;
            this.scanNowState = ENABLED;
            this.upgradeState = ENABLED;
        } else {
            this.resolvedGroupId = this.resolvedArtifactId = this.resolvedVersion = "";
            this.startScannerState = DISABLED;
            this.stopScannerState = DISABLED;
            this.scanNowState = DISABLED;
            this.upgradeState = DISABLED;
        }

        setScannerStatus();

        updateView();
    }

    private void updateView() {
        view.setStatus( status );

        view.setStartScannerState( startScannerState );
        view.setStopScannerState( stopScannerState );
        view.setScanNowState( scanNowState );
        view.setUpgradeState( upgradeState );

        updateViewScannerStatus();

        view.setInterval( pollInterval );

        view.setGroupId( groupId );
        view.setArtifactId( artifactId );
        view.setVersion( version );

        view.setResolvedGroupId( resolvedGroupId );
        view.setResolvedArtifactId( resolvedArtifactId );
        view.setResolvedVersion( resolvedVersion );

        view.setEndpoint( endpoint );
    }

    private void setScannerStatus() {
        if ( scannerStatus == null ) {
            this.scannerStatus = ScannerStatus.UNKNOWN;
        }

        switch ( scannerStatus ) {
            case CREATED:
            case STARTED:
            case SCANNING:
                this.startScannerState = DISABLED;
                this.stopScannerState = ENABLED;
                this.scanNowState = DISABLED;
                this.upgradeState = DISABLED;
                break;
            case STOPPED:
            case DISPOSED:
            case ERROR:
                this.startScannerState = ENABLED;
                this.stopScannerState = DISABLED;
                this.scanNowState = ENABLED;
                this.upgradeState = ENABLED;
                break;
            case UNKNOWN:
            default:
                this.startScannerState = ENABLED;
                this.stopScannerState = DISABLED;
                this.scanNowState = ENABLED;
                this.upgradeState = ENABLED;
                break;
        }
    }

    void updateViewScannerStatus() {
        view.setStartScannerState( this.startScannerState );
        view.setStopScannerState( this.stopScannerState );
        view.setScanNowState( this.scanNowState );
    }

    void onServerConnected( @Observes final ServerConnected event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getServer().getId().equals( serverId ) ) {
            final ContainerRef ref = event.getServer().getContainerRef( containerId );
            if ( ref == null ) {
                close();
                return;
            }
            update( ref );
        }
    }

    void onServerDisconnected( @Observes final ServerDisconnected event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getServer().getId().equals( serverId ) ) {
            final ContainerRef ref = event.getServer().getContainerRef( containerId );
            if ( ref == null ) {
                close();
                return;
            }
            update( ref );
        }
    }

    void onContainerUpdated( @Observes final ContainerUpdated event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getContainer().getServerId().equals( serverId ) && event.getContainer().getId().equals( containerId ) ) {
            update( event.getContainer() );
        }
    }

    void onContainerStarted( @Observes final ContainerStarted event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getContainer().getServerId().equals( serverId ) && event.getContainer().getId().equals( containerId ) ) {
            update( event.getContainer() );
        }
    }

    void onContainerStopped( @Observes final ContainerStopped event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getContainer().getServerId().equals( serverId ) && event.getContainer().getId().equals( containerId ) ) {
            update( event.getContainer() );
        }
    }

    void onServerError( @Observes final ServerOnError event ) {
        if ( event.getServer().getId().equals( serverId ) ) {
            close();
        }
    }

    void onServerDeleted( @Observes final ServerDeleted event ) {
        if ( event.getServerId().equals( serverId ) ) {
            close();
        }
    }

    void onContainerDeleted( @Observes final ContainerDeleted event ) {
        if ( event.getServerId().equals( serverId ) && event.getContainerId().equals( containerId ) ) {
            close();
        }
    }

    public void scanNow() {
        service.call( getScannerCallback() ).scanNow( serverId, containerId );
    }

    public void startScanner( final String interval ) {
        checkNotEmpty( "interval", interval );

        final long value;
        try {
            value = Long.valueOf( interval );
        } catch ( Exception ex ) {
            throw new IllegalArgumentException();
        }

        service.call( getScannerCallback() ).startScanner( serverId, containerId, value );
    }

    public void stopScanner() {
        service.call( getScannerCallback() ).stopScanner( serverId, containerId );
    }

    private RemoteCallback<ScannerOperationResult> getScannerCallback() {
        return new RemoteCallback<ScannerOperationResult>() {
            @Override
            public void callback( final ScannerOperationResult response ) {
                if ( response != null ) {
                    if ( response.getScannerStatus().equals( ScannerStatus.ERROR ) && response.getMessage() != null && !response.getMessage().trim().isEmpty() ) {
                        notification.fire( new NotificationEvent( response.getMessage(),
                                                                  NotificationEvent.NotificationType.ERROR ) );
                    }
                    scannerStatus = response.getScannerStatus();
                    setScannerStatus();
                    updateViewScannerStatus();
                }
            }
        };
    }

    public void upgrade( final GAV releaseId ) {
        checkNotNull( "releaseId", releaseId );
        checkNotEmpty( "releaseId.groupId", releaseId.getGroupId() );
        checkNotEmpty( "releaseId.artifactId", releaseId.getArtifactId() );
        checkNotEmpty( "releaseId.version", releaseId.getVersion() );

        service.call().upgradeContainer( serverId, containerId, releaseId );
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<Widget>() {

                            @Override
                            public Widget build() {
                                return view.getCustomMenuItem( new Command() {
                                    @Override
                                    public void execute() {
                                        close();
                                    }
                                } ).asWidget();
                            }
                        };
                    }
                } ).endMenu().build();
    }

    void close() {
        placeManager.forceClosePlace( "ContainerInfo" );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Container Info";
    }

    @WorkbenchPartView
    public UberView<ContainerInfoPresenter> getView() {
        return view;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getServerId() {
        return serverId;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getPollInterval() {
        return pollInterval;
    }

    public ScannerStatus getScannerStatus() {
        return scannerStatus;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getResolvedGroupId() {
        return resolvedGroupId;
    }

    public String getResolvedArtifactId() {
        return resolvedArtifactId;
    }

    public String getResolvedVersion() {
        return resolvedVersion;
    }

    public List<ServerInstanceRef> getEndpoint() {
        return endpoint;
    }

    public State getStartScannerState() {
        return startScannerState;
    }

    public State getStopScannerState() {
        return stopScannerState;
    }

    public State getScanNowState() {
        return scanNowState;
    }

    public State getUpgradeState() {
        return upgradeState;
    }
}
