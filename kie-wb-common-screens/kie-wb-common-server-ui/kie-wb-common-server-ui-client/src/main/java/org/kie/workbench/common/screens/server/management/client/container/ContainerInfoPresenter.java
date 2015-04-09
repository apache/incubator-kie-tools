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

import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfo;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
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
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;

@Dependent
@WorkbenchScreen(identifier = "ContainerInfo", preferredWidth = 500)
public class ContainerInfoPresenter {

    private boolean isClosed = true;

    public interface View extends UberView<ContainerInfoPresenter> {

        void setup( final Container response );

        void setStatus( final ContainerStatus started );

        void setScannerStatus( final ScannerStatus scannerStatus );

        void setResolvedReleasedId( final GAV resolvedReleasedId );

        void cleanup();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<ServerManagementService> service;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private View view;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest placeRequest;

    private String serverId;

    private String containerId;

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
    }

    private void onContainerInfo( @Observes ContainerInfo containerInfo ) {
        if ( isClosed ) {
            return;
        }
        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest, "Container Info [" + containerInfo.getContainerId() + "]", null ) );
        serverId = containerInfo.getServerId();
        containerId = containerInfo.getContainerId();
        view.cleanup();

        service.call( new RemoteCallback<Container>() {
            @Override
            public void callback( final Container response ) {
                view.setup( response );
            }
        } ).getContainerInfo( containerInfo.getServerId(), containerInfo.getContainerId() );
    }

    public void onServerConnected( @Observes final ServerConnected event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getServer().getId().equals( serverId ) ) {
            view.setStatus( event.getServer().getContainerRef( containerId ).getStatus() );
        }
    }

    public void onServerDeleted( @Observes final ServerDeleted event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getServerId().equals( serverId ) ) {
            close();
        }
    }

    public void onContainerDeleted( @Observes final ContainerDeleted event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getServerId().equals( serverId ) && event.getContainerId().equals( containerId ) ) {
            close();
        }
    }

    public void onContainerUpdated( @Observes final ContainerUpdated event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getContainer().getServerId().equals( serverId ) && event.getContainer().getId().equals( containerId ) ) {
            view.setup( event.getContainer() );
        }
    }

    public void onContainerStarted( @Observes final ContainerStarted event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getContainer().getServerId().equals( serverId ) && event.getContainer().getId().equals( containerId ) ) {
            view.setup( event.getContainer() );
//            view.setStatus( ContainerStatus.STARTED );
//            view.setResolvedReleasedId( event.getContainer().getResolvedReleasedId() );
        }
    }

    public void onServerError( @Observes final ServerOnError event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getServer().getId().equals( serverId ) ) {
            view.setStatus( ContainerStatus.ERROR );
        }
    }

    public void onContainerStopped( @Observes final ContainerStopped event ) {
        if ( isClosed ) {
            return;
        }
        if ( event.getContainer().getServerId().equals( serverId ) && event.getContainer().getId().equals( containerId ) ) {
            view.setStatus( ContainerStatus.STOPPED );
        }
    }

    public void scanNow() {
        service.call( new RemoteCallback<ScannerOperationResult>() {
            @Override
            public void callback( final ScannerOperationResult response ) {
                if ( response != null && response.getScannerStatus().equals( ScannerStatus.ERROR ) && response.getMessage() != null && !response.getMessage().trim().isEmpty() ) {
                    notification.fire( new NotificationEvent( response.getMessage(),
                                                              NotificationEvent.NotificationType.ERROR ) );
                }
                view.setScannerStatus( response.getScannerStatus() );
            }
        } ).scanNow( serverId, containerId );
    }

    public void startScanner( final long interval ) {
        service.call( new RemoteCallback<ScannerOperationResult>() {
            @Override
            public void callback( final ScannerOperationResult response ) {
                if ( response != null && response.getScannerStatus().equals( ScannerStatus.ERROR ) && response.getMessage() != null && !response.getMessage().trim().isEmpty() ) {
                    notification.fire( new NotificationEvent( response.getMessage(),
                                                              NotificationEvent.NotificationType.ERROR ) );
                }
                view.setScannerStatus( response.getScannerStatus() );
            }
        } ).startScanner( serverId, containerId, interval );
    }

    public void stopScanner() {
        service.call( new RemoteCallback<ScannerOperationResult>() {
            @Override
            public void callback( final ScannerOperationResult response ) {
                if ( response != null && response.getScannerStatus().equals( ScannerStatus.ERROR ) && response.getMessage() != null && !response.getMessage().trim().isEmpty() ) {
                    notification.fire( new NotificationEvent( response.getMessage(),
                                                              NotificationEvent.NotificationType.ERROR ) );
                }
                view.setScannerStatus( response.getScannerStatus() );
            }
        } ).stopScanner( serverId, containerId );
    }

    public void upgrade( final GAV releaseId ) {
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
                        return new MenuCustom<Widget>() {

                            @Override
                            public Widget build() {
                                return new Button() {
                                    {
                                        setIcon( IconType.REMOVE );
                                        setTitle( Constants.INSTANCE.remove() );
                                        setSize( MINI );
                                        addClickHandler( new ClickHandler() {
                                            @Override
                                            public void onClick( ClickEvent event ) {
                                                close();
                                            }
                                        } );
                                    }
                                };
                            }

                            @Override
                            public boolean isEnabled() {
                                return false;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {
                            }

                            @Override
                            public String getContributionPoint() {
                                return null;
                            }

                            @Override
                            public String getCaption() {
                                return null;
                            }

                            @Override
                            public MenuPosition getPosition() {
                                return null;
                            }

                            @Override
                            public int getOrder() {
                                return 0;
                            }

                            @Override
                            public void accept( MenuVisitor visitor ) {
                                visitor.visit( this );
                            }

                            @Override
                            public void addEnabledStateChangeListener( EnabledStateChangeListener listener ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return null;
                            }

                            @Override
                            public Collection<String> getRoles() {
                                return null;
                            }

                            @Override
                            public Collection<String> getTraits() {
                                return null;
                            }
                        };
                    }
                } ).endMenu().build();
    }

    private void close() {
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

}
