package org.kie.workbench.common.screens.server.management.client.box;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfo;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

@Dependent
public class BoxPresenter {

    public interface View extends IsWidget {

        void setup( final BoxPresenter presenter );

        void select();

        void unSelect();

        void setStatus( final ContainerStatus status );

        void show();

        void hide();

        boolean isVisible();
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ContainerInfo> containerInfoEvent;

    public View getView() {
        return view;
    }

    public String name = null;

    private String serverId;

    private ContainerStatus status;

    boolean isVisible = true;

    boolean isSelected = false;

    private String description;

    private BoxType type;

    private Command onSelect = new Command() {
        @Override
        public void execute() {

        }
    };
    private Command onUnSelect = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onAddAction = null;

    public void setup( final ServerRef server ) {
        this.serverId = server.getId();
        this.name = server.getName();
        this.status = server.getStatus();
        this.type = BoxType.SERVER;
        if ( server.getProperties().containsKey( "version" ) ) {
            this.description = "Server v." + server.getProperties().get( "version" );
        } else {
            this.description = "Unknown Server";
        }

        this.view.setup( this );
    }

    public void setup( final ContainerRef container ) {
        if ( container instanceof Container ) {
            setup( (Container) container );
            return;
        }
        this.serverId = container.getServerId();
        this.name = container.getId();
        this.status = container.getStatus();
        this.type = BoxType.CONTAINER;
        if ( isSelected ) {
            this.select( true );
        }

        if ( container.getReleasedId() != null ) {
            this.description = container.getReleasedId().getGroupId() + ":" + container.getReleasedId().getArtifactId() + "-" + container.getReleasedId().getVersion();
        } else {
            this.description = "Unknown Container";
        }

        this.view.setup( this );
    }

    public void setup( final Container container ) {
        this.serverId = container.getServerId();
        this.name = container.getId();
        this.status = container.getStatus();
        this.type = BoxType.CONTAINER;

        if ( isSelected ) {
            this.select( true );
        }

        if ( container.getReleasedId() != null ) {
            if ( container.getReleasedId().equals( container.getResolvedReleasedId() ) ) {
                this.description = container.getReleasedId().getGroupId() + ":" + container.getReleasedId().getArtifactId() + "-" + container.getReleasedId().getVersion();
            } else {
                this.description = container.getResolvedReleasedId().getGroupId() + ":" + container.getResolvedReleasedId().getArtifactId() + "-" + container.getResolvedReleasedId().getVersion() + "(" + container.getReleasedId().getGroupId() + ":" + container.getReleasedId().getArtifactId() + "-" + container.getReleasedId().getVersion() + ")";
            }
        } else {
            this.description = "Unknown Container";
        }

        this.view.setup( this );
    }

    public void select( boolean selected ) {
        this.isSelected = selected;
        if ( selected ) {
            this.view.select();
        } else {
            this.view.unSelect();
        }
    }

    public void show() {
        this.view.show();
        this.isVisible = true;
    }

    public void hide() {
        this.view.hide();
        this.isVisible = false;
        this.isSelected = false;
    }

    public void filter( final String parameter ) {
        if ( parameter == null || parameter.trim().isEmpty() || this.name.toLowerCase().contains( parameter.trim().toLowerCase() ) ) {
            show();
        } else {
            hide();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public void onSelect( final Command command ) {
        this.onSelect = command;
    }

    public void onUnSelect( final Command command ) {
        this.onUnSelect = command;
    }

    public boolean isSelected() {
        return isVisible && isSelected;
    }

    public BoxType getType() {
        return type;
    }

    public Command getOnOpenAction() {
        if ( getType().equals( BoxType.CONTAINER ) ) {
            return new Command() {
                @Override
                public void execute() {
                    if ( type.equals( BoxType.CONTAINER ) ) {
                        placeManager.goTo( "ContainerInfo" );
                        containerInfoEvent.fire( new ContainerInfo( serverId, name ) );
                    }
                }
            };
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public Command getOnAddAction() {
        return onAddAction;
    }

    public void setOnAddAction( Command onAddAction ) {
        this.onAddAction = onAddAction;
    }

    public String getName() {
        return name;
    }

    public void onUnSelect() {
        this.isSelected = false;
        onUnSelect.execute();
    }

    public void onSelect() {
        this.isSelected = true;
        onSelect.execute();
    }

}
