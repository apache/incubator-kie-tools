package org.kie.workbench.common.screens.server.management.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerInfo;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

@Dependent
public class ServerManagementBrowserView extends Composite implements ServerManagementBrowserPresenter.View {

    private final FlowPanel panel = new FlowPanel();

    private HeaderPresenter header = null;
    private ServerManagementBrowserPresenter presenter = null;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ContainerInfo> containerInfoEvent;

    @PostConstruct
    public void setup() {
        initWidget( panel );
        panel.getElement().getStyle().setProperty( "minWidth", "550px" );
    }

    @Override
    public void init( final ServerManagementBrowserPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setHeader( HeaderPresenter header ) {
        if ( this.header == null ) {
            this.header = header;

            panel.getElement().getStyle().setPaddingLeft( 20, Style.Unit.PX );
            panel.getElement().getStyle().setPaddingRight( 20, Style.Unit.PX );
            panel.add( header.getView() );
        }
    }

    @Override
    public void remove( BoxPresenter value ) {
        panel.remove( value.getView() );
    }

    @Override
    public void loadContainer( final ContainerRef containerRef,
                               final BoxPresenter serverContainer ) {

        final BoxPresenter boxPresenter = presenter.newContainer( containerRef );

        boxPresenter.setup( containerRef );
        boxPresenter.onSelect( new Command() {
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

        panel.insert( boxPresenter.getView(), panel.getWidgetIndex( serverContainer.getView() ) + 1 );
    }

    @Override
    public void loadServer( final Server executionServer ) {
        loadServer( (ServerRef) executionServer );

        for ( final Container container : executionServer.containers() ) {
            final BoxPresenter boxPresenter = presenter.newContainer( container );

            boxPresenter.setup( container );
            boxPresenter.onSelect( new Command() {
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

            panel.add( boxPresenter.getView() );
        }
    }

    @Override
    public void loadServer( final ServerRef serverRef ) {
        final BoxPresenter serverContainer = presenter.newContainer( serverRef );
        serverContainer.setup( serverRef );
        serverContainer.onSelect( new Command() {
            @Override
            public void execute() {
                header.displayDeleteContainer();
            }
        } );

        panel.add( serverContainer.getView() );
    }
}
