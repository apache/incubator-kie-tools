package org.uberfire.ext.layout.editor.client.components.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@Templated
public class ContainerView extends Composite
        implements UberView<Container>,
        Container.View {

    private Container presenter;

    @Inject
    PlaceManager placeManager;

    @Inject
    @DataField
    FlowPanel layout;

    @Inject
    @DataField
    private Span mobile;

    @Inject
    @DataField
    private Span tablet;

    @Inject
    @DataField
    private Span desktop;

    @Inject
    private Event<ContainerResizeEvent> resizeEvent;

    @Override
    public void init( Container presenter ) {
        this.presenter = presenter;
    }

    @EventHandler( "mobile" )
    public void mobileSize( ClickEvent e ) {
        layout.getElement().removeClassName( "simulate-sm" );
        layout.getElement().addClassName( "simulate-xs" );
        resizeEvent.fire( new ContainerResizeEvent(  ) );
    }

    @EventHandler( "tablet" )
    public void tabletSize( ClickEvent e ) {
        layout.getElement().addClassName( "simulate-sm" );
        layout.getElement().removeClassName( "simulate-xs" );
        resizeEvent.fire( new ContainerResizeEvent(  ) );
    }

    @EventHandler( "desktop" )
    public void desktopSize( ClickEvent e ) {
        layout.getElement().removeClassName( "simulate-xs" );
        layout.getElement().removeClassName( "simulate-sm" );
        resizeEvent.fire( new ContainerResizeEvent(  ) );
    }


    @Override
    public void addRow( UberView<Row> view ) {
        layout.add( view.asWidget() );
    }

    @Override
    public void clear() {
        layout.clear();
    }

    @Override
    public void addEmptyRow( UberView<EmptyDropRow> emptyDropRow ) {
        layout.add( emptyDropRow.asWidget() );
    }

}
