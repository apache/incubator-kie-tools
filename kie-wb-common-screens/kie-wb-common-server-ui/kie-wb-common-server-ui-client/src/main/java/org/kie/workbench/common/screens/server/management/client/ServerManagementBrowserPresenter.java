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

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.uberfire.client.common.YesNoCancelPopup;
import org.kie.workbench.common.screens.server.management.client.artifact.NewContainerForm;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.box.BoxType;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.client.registry.ServerRegistryEndpointForm;
import org.kie.workbench.common.screens.server.management.events.ContainerCreated;
import org.kie.workbench.common.screens.server.management.events.ContainerDeleted;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDeleted;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
@WorkbenchScreen(identifier = "ServerManagementBrowser")
public class ServerManagementBrowserPresenter {

    public interface View extends UberView<ServerManagementBrowserPresenter> {

        void loadServer( final Server executionServer );

        void loadServer( final ServerRef executionServerRef );

        void setHeader( final HeaderPresenter header );

        void remove( final BoxPresenter value );

        void loadContainer( final ContainerRef container,
                            final BoxPresenter serverContainer );
    }

    @Inject
    private View view;

    @Inject
    private HeaderPresenter header;

    @Inject
    private Caller<ServerManagementService> service;

    @Inject
    private PlaceManager placeManager;

    private Map<Object, BoxPresenter> containers = new HashMap<Object, BoxPresenter>();

    @Inject
    private ServerRegistryEndpointForm serverRegistryEndpoint;

    @Inject
    private NewContainerForm newContainerForm;

    private int selectedItems = 0;

    private final Command unselectContainerCommand = new Command() {
        @Override
        public void execute() {
            boolean hasServerSelected = false;
            boolean hasContainerSelected = false;
            boolean hasStartedContainerSelected = false;
            boolean hasStoppedContainerSelected = false;
            for ( BoxPresenter container : containers.values() ) {
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
        }
    };

    @PostConstruct
    public void init() {
        view.setHeader( header );

        header.setOnFilterChange( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String parameter ) {
                for ( final BoxPresenter container : containers.values() ) {
                    container.filter( parameter );
                }
            }
        } );

        header.setOnClearSelection( new Command() {
            @Override
            public void execute() {
                for ( final BoxPresenter container : containers.values() ) {
                    container.select( false );
                }
            }
        } );

        header.setOnDelete( new Command() {
            @Override
            public void execute() {
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

                YesNoCancelPopup.newYesNoCancelPopup(
                        "Delete",
                        buildMessage( serverNames, container2delete ),
                        new Command() {
                            @Override
                            public void execute() {
                                service.call().deleteOp( new ArrayList<String>( serverNames.keySet() ), container2delete );
                            }
                        },
                        org.kie.uberfire.client.resources.i18n.CommonConstants.INSTANCE.YES(),
                        ButtonType.DANGER,
                        IconType.EXCLAMATION_SIGN,

                        new Command() {
                            @Override
                            public void execute() {
                            }
                        },
                        org.kie.uberfire.client.resources.i18n.CommonConstants.INSTANCE.NO(),
                        ButtonType.DEFAULT,
                        null,

                        null,
                        null,
                        null,
                        null ).show();
            }
        } );

        header.setOnStart( new Command() {
            @Override
            public void execute() {
                final Map<String, List<String>> container2Start = new HashMap<String, List<String>>();
                for ( final Map.Entry<Object, BoxPresenter> container : containers.entrySet() ) {
                    if ( container.getValue().isSelected() ) {
                        if ( container.getKey() instanceof ContainerRef && ( (ContainerRef) container.getKey() ).getStatus().equals( ContainerStatus.STOPPED ) ) {
                            final ContainerRef simpleContainer = (ContainerRef) container.getKey();
                            if ( !container2Start.containsKey( simpleContainer.getServerId() ) ) {
                                container2Start.put( simpleContainer.getServerId(), new ArrayList<String>() );
                            }
                            container2Start.get( simpleContainer.getServerId() ).add( simpleContainer.getId() );
                        }
                    }
                }

                service.call().startContainers( container2Start );
            }
        } );

        header.setOnStop( new Command() {
            @Override
            public void execute() {
                final Map<String, List<String>> container2Stop = new HashMap<String, List<String>>();
                for ( final Map.Entry<Object, BoxPresenter> container : containers.entrySet() ) {
                    if ( container.getValue().isSelected() ) {
                        if ( container.getKey() instanceof ContainerRef && ( (ContainerRef) container.getKey() ).getStatus().equals( ContainerStatus.STARTED ) ) {
                            final ContainerRef simpleContainer = (ContainerRef) container.getKey();
                            if ( !container2Stop.containsKey( simpleContainer.getServerId() ) ) {
                                container2Stop.put( simpleContainer.getServerId(), new ArrayList<String>() );
                            }
                            container2Stop.get( simpleContainer.getServerId() ).add( simpleContainer.getId() );
                        }
                    }
                }

                service.call().stopContainers( container2Stop );
            }
        } );

        header.setOnRefresh( new Command() {
            @Override
            public void execute() {
                service.call().refresh();
            }
        } );

        header.setOnRegisterServer( new Command() {
            @Override
            public void execute() {
                serverRegistryEndpoint.show();
            }
        } );

        header.setOnSelectAll( new Command() {
            @Override
            public void execute() {
                for ( final BoxPresenter container : containers.values() ) {
                    if ( container.isVisible() ) {
                        container.select( true );
                    } else {
                        container.select( false );
                    }
                }
            }
        } );
    }

    @AfterInitialization
    public void loadContent() {
        service.call( new RemoteCallback<Collection<ServerRef>>() {
            @Override
            public void callback( final Collection<ServerRef> response ) {
                loadContent( response );
            }
        } ).listServers();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Server Management Browser";
    }

    @WorkbenchPartView
    public UberView<ServerManagementBrowserPresenter> getView() {
        return view;
    }

    private String buildMessage( final Map<String, String> serverNames,
                                 final Map<String, List<String>> container2delete ) {
        final StringBuilder sb = new StringBuilder();
        if ( !serverNames.isEmpty() ) {
            sb.append( "Are you sure you want delete the following server(s):<br/>" );
            for ( final String s : serverNames.values() ) {
                sb.append( s ).append( " ," );
            }
            sb.setLength( sb.length() - 2 );
            sb.append( "." );
        }
        if ( !container2delete.isEmpty() ) {
            if ( serverNames.isEmpty() ) {
                sb.append( "Are you sure you want delete the following container(s):<br/>" );
            } else {
                sb.append( "<br/>And the following container(s):<br/>" );
            }
            for ( final Map.Entry<String, List<String>> entry : container2delete.entrySet() ) {
                for ( String s : entry.getValue() ) {
                    sb.append( s ).append( " ," );
                }
            }
            sb.setLength( sb.length() - 2 );
            sb.append( "." );
        }
        return sb.toString();
    }

    public BoxPresenter newContainer( final Object container ) {
        final BoxPresenter boxPresenter = IOC.getBeanManager().lookupBean( BoxPresenter.class ).getInstance();

        containers.put( container, boxPresenter );

        boxPresenter.onUnSelect( unselectContainerCommand );

        if ( container instanceof ServerRef ) {
            boxPresenter.setOnAddAction( new Command() {
                @Override
                public void execute() {
                    newContainerForm.show( (ServerRef) container );
                }
            } );
        }

        return boxPresenter;
    }

    public void loadContent( final Collection<ServerRef> executionServers ) {
        for ( final ServerRef executionServer : executionServers ) {
            loadServer( executionServer );
        }
    }

    public void onServerConnected( @Observes final ServerConnected event ) {
        loadServer( event.getServer() );
    }

    public void onContainerCreated( @Observes final ContainerCreated event ) {
        loadContainer( event.getContainer(), ContainerStatus.STARTED );
    }

    public void onContainerStarted( @Observes final ContainerStarted event ) {
        loadContainer( event.getContainer(), ContainerStatus.STARTED );
    }

    public void onServerError( @Observes final ServerOnError event ) {
        loadServer( event.getServer() );
    }

    public void onServerDeleted( @Observes final ServerDeleted event ) {
        removeServer( event.getServerId() );
    }

    public void onContainerDeleted( @Observes final ContainerDeleted event ) {
        removeContainer( event.getServerId(), event.getContainerId() );
    }

    public void onContainerStopped( @Observes final ContainerStopped event ) {
        loadContainer( event.getContainer(), ContainerStatus.STARTED );
    }

    public void onContainerUpdated( @Observes final ContainerUpdated event ) {
        loadContainer( event.getContainer(), ContainerStatus.STARTED );
    }

    private void removeContainer( final String serverId,
                                  final String containerId ) {
        Object ref = null;
        for ( final Map.Entry<Object, BoxPresenter> entry : containers.entrySet() ) {
            if ( entry.getKey() instanceof ContainerRef && ( (ContainerRef) entry.getKey() ).getServerId().equals( serverId ) && ( (ContainerRef) entry.getKey() ).getId().equals( containerId ) ) {
                ref = entry.getKey();
                view.remove( entry.getValue() );
                break;
            }
        }
        if ( ref != null ) {
            final BoxPresenter presenter = containers.remove( ref );
            presenter.select( false );
            IOC.getBeanManager().destroyBean( presenter );
        }
    }

    private void removeServer( final String serverId ) {
        final List<Object> refs = new ArrayList<Object>();
        for ( final Map.Entry<Object, BoxPresenter> entry : containers.entrySet() ) {
            if ( entry.getKey() instanceof ServerRef && ( (ServerRef) entry.getKey() ).getId().equals( serverId ) ||
                    entry.getKey() instanceof ContainerRef && ( (ContainerRef) entry.getKey() ).getServerId().equals( serverId ) ) {
                view.remove( entry.getValue() );
                refs.add( entry.getKey() );
            }
        }

        if ( !refs.isEmpty() ) {
            for ( Object ref : refs ) {
                final BoxPresenter presenter = containers.remove( ref );
                presenter.select( false );
                IOC.getBeanManager().destroyBean( presenter );
            }
        }
    }

    public void loadServer( final ServerRef executionServerRef ) {
        if ( containers.containsKey( executionServerRef ) ) {
            final BoxPresenter presenter = containers.get( executionServerRef );
            presenter.setup( executionServerRef );
        } else {
            view.loadServer( executionServerRef );
        }

        for ( final ContainerRef containerRef : executionServerRef.getContainersRef() ) {
            loadContainer( containerRef, executionServerRef.getStatus() );
        }
    }

    public void loadServer( final Server executionServer ) {
        if ( containers.containsKey( executionServer ) ) {
            final BoxPresenter presenter = containers.get( executionServer );
            presenter.setup( executionServer );
        } else {
            view.loadServer( executionServer );
        }

        for ( final ContainerRef container : executionServer.getContainersRef() ) {
            loadContainer( container, executionServer.getStatus() );
        }
    }

    private void loadContainer( final ContainerRef container,
                                final ContainerStatus serverStatus ) {
        if ( serverStatus.equals( ContainerStatus.ERROR ) ) {
            container.setStatus( serverStatus );
        }

        if ( containers.containsKey( container ) ) {
            final BoxPresenter presenter = containers.remove( container );
            containers.put( container, presenter );
            presenter.setup( container );
            return;
        }

        BoxPresenter serverContainer = null;
        for ( final Map.Entry<Object, BoxPresenter> entry : containers.entrySet() ) {
            if ( entry.getKey() instanceof ServerRef && ( (ServerRef) entry.getKey() ).getId().equals( container.getServerId() ) ) {
                serverContainer = entry.getValue();
            }
        }

        if ( serverContainer != null ) {
            view.loadContainer( container, serverContainer );
        }
    }

}
