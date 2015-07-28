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

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfoUpdateEvent;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.events.ContainerStopped;
import org.kie.workbench.common.screens.server.management.events.ContainerUpdated;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDisconnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class BoxPresenter {

    public interface View extends UberView<BoxPresenter> {

        void setup( final BoxType type );

        void onSelect();

        void onDeselect();

        void setStatus( final ContainerStatus status );

        void show();

        void hide();

        void setName( final String value );

        void setDescription( final String value );

        void enableAddAction();

        void disableAddAction();

        void enableOpenAction();

        void disableOpenAction();
    }

    private View view;

    private PlaceManager placeManager;

    private Event<ContainerInfoUpdateEvent> containerInfoEvent;

    private String name = null;

    private String serverId;

    private ContainerStatus status;

    private boolean isVisible = true;

    private boolean isSelected = false;

    private String description;

    private BoxType type;

    private boolean supportsOpenCommand = false;

    private Command onSelect = new Command() {
        @Override
        public void execute() {

        }
    };
    private Command onDeselect = new Command() {
        @Override
        public void execute() {

        }
    };

    @Inject
    public BoxPresenter( final View view,
                         final PlaceManager placeManager,
                         final Event<ContainerInfoUpdateEvent> containerInfoEvent ) {
        this.view = view;
        this.placeManager = placeManager;
        this.containerInfoEvent = containerInfoEvent;
        this.view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final ServerRef server ) {
        this.serverId = server.getId();
        this.name = server.getName();
        this.status = server.getStatus();
        this.type = BoxType.SERVER;

        this.description = "(id: '" + serverId + "', version: " + buildDescription( server.getProperties() ) + ")";

        this.supportsOpenCommand = false;

        setupView();
    }

    private String buildDescription( Map<String, String> properties ) {
        if ( properties.get( "version" ) != null && !properties.get( "version" ).isEmpty() ) {
            return  properties.get( "version" );
        }
        return "unknown";
    }

    public void setup( final ContainerRef container ) {
        this.serverId = container.getServerId();
        this.name = container.getId();
        this.status = container.getStatus();
        this.type = BoxType.CONTAINER;

        if ( container instanceof Container ) {
            this.description = buildDescription( container.getReleasedId(), ( (Container) container ).getResolvedReleasedId() );
        } else {
            this.description = buildDescription( container.getReleasedId() );
        }

        this.supportsOpenCommand = true;

        setupView();
    }

    private String buildDescription( final GAV releasedId ) {
        return buildDescription( releasedId, null );
    }

    private String buildDescription( final GAV releasedId,
                                     final GAV resolvedReleasedId ) {
        if ( releasedId != null ) {
            if ( resolvedReleasedId == null || releasedId.equals( resolvedReleasedId ) ) {
                return releasedId.getGroupId() + ":" + releasedId.getArtifactId() + "-" + releasedId.getVersion();
            } else {
                return resolvedReleasedId.getGroupId() + ":" + resolvedReleasedId.getArtifactId() + "-" + resolvedReleasedId.getVersion() + "(" + releasedId.getGroupId() + ":" + releasedId.getArtifactId() + "-" + releasedId.getVersion() + ")";
            }
        }
        return "Unknown Container";
    }

    private void setupView() {
        view.setup( type );
        updateView();
    }

    public void select( boolean selected ) {
        this.isSelected = selected;
        if ( selected ) {
            this.view.onSelect();
        } else {
            this.view.onDeselect();
        }
    }

    private void show() {
        this.view.show();
        this.isVisible = true;
    }

    private void hide() {
        this.view.hide();
        this.isVisible = false;
        this.isSelected = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public void setOnSelect( final Command command ) {
        this.onSelect = command;
    }

    public void setOnDeselect( final Command command ) {
        this.onDeselect = command;
    }

    public boolean isSelected() {
        return isVisible && isSelected;
    }

    public BoxType getType() {
        return type;
    }

    public boolean supportsOpenCommand() {
        return supportsOpenCommand;
    }

    public void openBoxInfo() {
        if ( supportsOpenCommand() ) {
            placeManager.goTo( "ContainerInfo" );
            containerInfoEvent.fire( new ContainerInfoUpdateEvent( serverId, name ) );
        }
    }

    public void openAddScreen() {
        if ( enableAddAction() ) {
            placeManager.goTo( new DefaultPlaceRequest( "NewContainerForm" ).addParameter( "serverId", serverId ) );
        }
    }

    public boolean enableAddAction() {
        return type.equals( BoxType.SERVER );
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void onUnSelect() {
        this.isSelected = false;
        onDeselect.execute();
    }

    public void onSelect() {
        this.isSelected = true;
        onSelect.execute();
    }

    public void filter( final String filter ) {
        if ( filter == null || filter.trim().isEmpty() || this.name.toLowerCase().contains( filter.trim().toLowerCase() ) ) {
            show();
        } else {
            hide();
        }
    }

    void onServerConnected( @Observes final ServerConnected connected ) {
        if ( serverId.equals( connected.getServer().getId() ) ) {
            if ( type.equals( BoxType.SERVER ) ) {
                status = connected.getServer().getStatus();

                if (connected.getServer().getContainersRef() != null) {

                    for (ContainerRef containerRef : connected.getServer().getContainersRef()) {
                        containerInfoEvent.fire(new ContainerInfoUpdateEvent(serverId, containerRef.getId()));
                    }
                }
            } else {
                for ( final ContainerRef containerRef : connected.getServer().getContainersRef() ) {
                    if ( containerRef.getId().equals( getName() ) ) {
                        status = containerRef.getStatus();
                        break;
                    }
                }
            }
            updateView();
        }
    }

    void onServerDisconnected( @Observes final ServerDisconnected disconnected ) {
        if ( serverId.equals( disconnected.getServer().getId() ) ) {
            if ( type.equals( BoxType.SERVER ) ) {
                status = disconnected.getServer().getStatus();

                if (disconnected.getServer().getContainersRef() != null) {

                    for (ContainerRef containerRef : disconnected.getServer().getContainersRef()) {
                        containerInfoEvent.fire(new ContainerInfoUpdateEvent(serverId, containerRef.getId()));
                    }
                }
            } else {
                for ( final ContainerRef containerRef : disconnected.getServer().getContainersRef() ) {
                    if ( containerRef.getId().equals( getName() ) ) {
                        if (disconnected.getServer().getStatus().equals(ContainerStatus.STOPPED)) {
                            status = disconnected.getServer().getStatus();
                        } else {
                            status = containerRef.getStatus();
                        }
                        break;
                    }
                }
            }
            updateView();
        }
    }

    private void updateView() {
        view.setName( name );
        view.setDescription( description );
        view.setStatus( status );

        if ( enableAddAction() ) {
            view.enableAddAction();
        } else {
            view.disableAddAction();
        }

        if ( supportsOpenCommand() ) {
            view.enableOpenAction();
        } else {
            view.disableOpenAction();
        }
    }

    void onServerOnError( @Observes final ServerOnError serverOnError ) {
        if ( serverId.equals( serverOnError.getServer().getId() ) ) {
            status = serverOnError.getServer().getStatus();
            updateView();
        }
    }

    void onContainerStopped( @Observes final ContainerStopped containerStoped ) {
        if ( name.equals( containerStoped.getContainer().getId() ) && serverId.equals( containerStoped.getContainer().getServerId() ) ) {
            status = containerStoped.getContainer().getStatus();
            updateView();
        }
    }

    void onContainerStarted( @Observes final ContainerStarted containerStarted ) {
        if ( name.equals( containerStarted.getContainer().getId() ) && serverId.equals( containerStarted.getContainer().getServerId() ) ) {
            status = containerStarted.getContainer().getStatus();
            description = buildDescription( containerStarted.getContainer().getReleasedId(), containerStarted.getContainer().getResolvedReleasedId() );
            updateView();
        }
    }

    void onContainerUpdated( @Observes final ContainerUpdated containerUpdated ) {
        if ( name.equals( containerUpdated.getContainer().getId() ) && serverId.equals( containerUpdated.getContainer().getServerId() ) ) {
            status = containerUpdated.getContainer().getStatus();
            description = buildDescription( containerUpdated.getContainer().getReleasedId(), containerUpdated.getContainer().getResolvedReleasedId() );
            updateView();
        }
    }
}