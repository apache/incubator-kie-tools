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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.box.BoxType;
import org.kie.workbench.common.screens.server.management.client.events.HeaderClearSelectionEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderDeleteEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderFilterEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderRefreshEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderSelectAllEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStartEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStopEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderServerStatusUpdateEvent;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.events.ContainerCreated;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.commons.data.Pair;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.Command;

@ApplicationScoped
@WorkbenchScreen( identifier = "ServerManagementBrowser" )
public class ServerManagementBrowserPresenter {

    public interface View extends IsWidget {

        void setHeader( final HeaderPresenter header );

        void addBox( final BoxPresenter container );

        void addBox( final BoxPresenter container,
                     final BoxPresenter parentContainer );

        void removeBox( final BoxPresenter value );

        void cleanup();

        void confirmDeleteOperation( final Collection<String> serverNames,
                                     final Collection<List<String>> container2delete,
                                     final Command command );
    }

    private final View view;

    private final HeaderPresenter header;

    private final Caller<ServerManagementService> service;

    private final SyncBeanManager beanManager;

    private Map<Object, BoxPresenter> containers = new HashMap<Object, BoxPresenter>();

    @Inject
    public ServerManagementBrowserPresenter( final View view,
                                             final SyncBeanManager beanManager,
                                             final HeaderPresenter header,
                                             final Caller<ServerManagementService> service ) {
        this.view = view;
        this.beanManager = beanManager;
        this.header = header;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        this.view.setHeader( header );
    }

    @OnOpen
    public void onOpen() {
        service.call( new RemoteCallback<Collection<ServerRef>>() {
            @Override
            public void callback( final Collection<ServerRef> response ) {
                loadServers( response );
            }
        } ).listServers();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.title();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    void onHeaderRefresh( @Observes final HeaderRefreshEvent event ) {
        if ( event.getContext().equals( header ) ) {
            service.call().refresh();
        }
    }

    void onHeaderFilter( @Observes final HeaderFilterEvent event ) {
        if ( event.getContext().equals( header ) ) {
            for ( final BoxPresenter container : containers.values() ) {
                container.filter( event.getFilter() );
            }
        }
    }

    void onHeaderSelectAll( @Observes final HeaderSelectAllEvent event ) {
        if ( event.getContext().equals( header ) ) {
            for ( final BoxPresenter container : containers.values() ) {
                if ( container.isVisible() ) {
                    container.select( true );
                } else {
                    container.select( false );
                }
            }
        }
    }

    void onHeaderClearSelection( @Observes final HeaderClearSelectionEvent event ) {
        if ( event.getContext().equals( header ) ) {
            for ( final BoxPresenter container : containers.values() ) {
                container.select( false );
            }
        }
    }

    void onHeaderStart( @Observes final HeaderStartEvent event ) {
        if ( event.getContext().equals( header ) ) {
            service.call().startContainers( getSelectedItems().getK2() );
        }
    }

    void onHeaderStop( @Observes final HeaderStopEvent event ) {
        if ( event.getContext().equals( header ) ) {
            service.call().stopContainers( getSelectedItems().getK2() );
        }
    }

    void onHeaderDelete( @Observes final HeaderDeleteEvent event ) {
        if ( event.getContext().equals( header ) ) {
            final Pair<Map<String, String>, Map<String, List<String>>> value = getSelectedItems();

            view.confirmDeleteOperation( value.getK1().values(), value.getK2().values(), new Command() {
                @Override
                public void execute() {
                    service.call().deleteOp( new ArrayList<String>( value.getK1().keySet() ), value.getK2() );
                }
            } );
        }
    }

    void onHeaderUpdateStatus( @Observes final HeaderServerStatusUpdateEvent event ) {
        if ( event.getContext().equals( header ) ) {
            service.call().updateServerStatus( new ArrayList<String>(getSelectedItems().getK1().keySet()) );
        }
    }

    void onServerConnected( @Observes final ServerConnected event ) {
        loadServer( event.getServer() );
    }

    void onServerError( @Observes final ServerOnError event ) {
        loadServer( event.getServer() );
    }

    void onContainerCreated( @Observes final ContainerCreated event ) {
        loadContainer( event.getContainer(), ContainerStatus.STARTED );
    }

    void onServerDeleted( @Observes final ServerDeleted event ) {
        removeServer( event.getServerId() );
    }

    void onContainerDeleted( @Observes final ContainerDeleted event ) {
        removeContainer( event.getServerId(), event.getContainerId() );
    }

    private void loadServers( final Collection<ServerRef> executionServers ) {
        view.cleanup();
        for ( final BoxPresenter container : containers.values() ) {
            beanManager.destroyBean( container );
        }
        containers.clear();
        for ( final ServerRef executionServer : executionServers ) {
            loadServer( executionServer );
        }
    }

    private void loadServer( final ServerRef executionServerRef ) {
        if ( !containers.containsKey( executionServerRef ) ) {
            view.addBox( newContainer( executionServerRef ) );
        }

        for ( final ContainerRef containerRef : executionServerRef.getContainersRef() ) {
            loadContainer( containerRef, executionServerRef.getStatus() );
        }
    }

    private void loadContainer( final ContainerRef container,
                                final ContainerStatus serverStatus ) {
        if ( serverStatus.equals( ContainerStatus.ERROR ) ) {
            container.setStatus( ContainerStatus.ERROR );
        }

        if ( containers.containsKey( container ) ) {
            return;
        }

        BoxPresenter serverContainer = null;
        for ( final Map.Entry<Object, BoxPresenter> entry : containers.entrySet() ) {
            if ( entry.getKey() instanceof ServerRef && ( (ServerRef) entry.getKey() ).getId().equals( container.getServerId() ) ) {
                serverContainer = entry.getValue();
                break;
            }
        }

        if ( serverContainer != null ) {
            view.addBox( newContainer( container ), serverContainer );
        }
    }

    private void removeContainer( final String serverId,
                                  final String containerId ) {
        Object ref = null;
        for ( final Map.Entry<Object, BoxPresenter> entry : containers.entrySet() ) {
            if ( entry.getKey() instanceof ContainerRef && ( (ContainerRef) entry.getKey() ).getServerId().equals( serverId ) && ( (ContainerRef) entry.getKey() ).getId().equals( containerId ) ) {
                ref = entry.getKey();
                view.removeBox( entry.getValue() );
                break;
            }
        }
        if ( ref != null ) {
            destroyBoxPresenter( ref );
        }
    }

    private void removeServer( final String serverId ) {
        final List<Object> refs = new ArrayList<Object>();
        for ( final Map.Entry<Object, BoxPresenter> entry : containers.entrySet() ) {
            if ( entry.getKey() instanceof ServerRef && ( (ServerRef) entry.getKey() ).getId().equals( serverId ) ||
                    entry.getKey() instanceof ContainerRef && ( (ContainerRef) entry.getKey() ).getServerId().equals( serverId ) ) {
                view.removeBox( entry.getValue() );
                refs.add( entry.getKey() );
            }
        }

        if ( !refs.isEmpty() ) {
            for ( Object ref : refs ) {
                destroyBoxPresenter( ref );
            }
        }
    }

    private void destroyBoxPresenter( final Object ref ) {
        final BoxPresenter presenter = containers.remove( ref );
        presenter.select( false );
        beanManager.destroyBean( presenter );
    }

    private BoxPresenter newContainer( final Object container ) {
        final BoxPresenter boxPresenter = beanManager.lookupBean( BoxPresenter.class ).newInstance();

        containers.put( container, boxPresenter );

        boxPresenter.setOnDeselect( new Command() {
            @Override
            public void execute() {
                boolean hasServerSelected = false;
                boolean hasContainerSelected = false;
                boolean hasStartedContainerSelected = false;
                boolean hasStoppedContainerSelected = false;
                for ( final BoxPresenter container : containers.values() ) {
                    if ( container.isSelected() ) {
                        if ( container.getType().equals( BoxType.SERVER ) ) {
                            hasServerSelected = true;
                        } else if ( container.getType().equals( BoxType.CONTAINER ) ) {
                            hasContainerSelected = true;
                            if ( container.getStatus().equals( ContainerStatus.STARTED ) ) {
                                hasStartedContainerSelected = true;
                            } else if ( container.getStatus().equals( ContainerStatus.STOPPED ) ) {
                                hasStoppedContainerSelected = true;
                            }
                        }
                    }
                    if ( hasServerSelected && hasStartedContainerSelected && hasStoppedContainerSelected ) {
                        break;
                    }
                }

                if ( hasContainerSelected ) {
                    header.displayDeleteContainer();
                } else {
                    header.hideDeleteContainer();
                }

                if ( hasStartedContainerSelected ) {
                    header.displayStopContainer();
                } else {
                    header.hideStopContainer();
                }

                if ( hasStoppedContainerSelected ) {
                    header.displayStartContainer();
                } else {
                    header.hideStartContainer();
                }


                if ( hasServerSelected ) {
                    header.displayUpdateStatus();
                } else {
                    header.hideUpdateStatus();
                }
            }
        } );

        if ( container instanceof ContainerRef ) {
            boxPresenter.setOnSelect( new Command() {
                @Override
                public void execute() {
                    header.displayDeleteContainer();
                    if ( boxPresenter.getStatus().equals( ContainerStatus.STARTED ) ) {
                        header.displayStopContainer();
                        header.hideStartContainer();
                    } else if ( boxPresenter.getStatus().equals( ContainerStatus.STOPPED ) ) {
                        header.displayStartContainer();
                        header.hideStopContainer();
                    } else if ( boxPresenter.getStatus().equals( ContainerStatus.ERROR ) ) {
                        header.hideStartContainer();
                        header.hideStopContainer();
                    }
                }
            } );

            boxPresenter.setup( (ContainerRef) container );
        } else if ( container instanceof ServerRef ) {

            boxPresenter.setOnSelect( new Command() {
                @Override
                public void execute() {
                    header.displayUpdateStatus();
                    header.displayDeleteContainer();
                }
            } );
            boxPresenter.setup( (ServerRef) container );
        }

        return boxPresenter;
    }

    private Pair<Map<String, String>, Map<String, List<String>>> getSelectedItems() {
        final Map<String, String> serverNames = new HashMap<String, String>();
        final Map<String, List<String>> container2delete = new HashMap<String, List<String>>();
        for ( final Map.Entry<Object, BoxPresenter> container : containers.entrySet() ) {
            if ( container.getValue().isSelected() ) {
                if ( container.getKey() instanceof ServerRef && ( (ServerRef) container.getKey() ).getConnectionType().equals( ConnectionType.REMOTE ) ) {
                    serverNames.put( ( (ServerRef) container.getKey() ).getId(), ( (ServerRef) container.getKey() ).getName() );
                } else if ( container.getKey() instanceof ContainerRef ) {
                    final ContainerRef simpleContainer = (ContainerRef) container.getKey();
                    if ( !container2delete.containsKey( simpleContainer.getServerId() ) ) {
                        container2delete.put( simpleContainer.getServerId(), new ArrayList<String>() );
                    }
                    container2delete.get( simpleContainer.getServerId() ).add( simpleContainer.getId() );
                }
            }
        }

        return Pair.newPair( serverNames, container2delete );
    }

}
