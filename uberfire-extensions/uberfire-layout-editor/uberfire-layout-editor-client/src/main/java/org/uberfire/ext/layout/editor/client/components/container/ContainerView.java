package org.uberfire.ext.layout.editor.client.components.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.uberfire.ext.layout.editor.client.infra.CSSClassNameHelper.*;

@Dependent
@Templated
public class ContainerView
        implements UberElement<Container>,
        Container.View, IsElement {

    private Container presenter;

    @Inject
    PlaceManager placeManager;

    @Inject
    @DataField
    Div layout;

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
        removeClassName( layout, "simulate-sm" );
        addClassName( layout, "simulate-xs" );
        resizeEvent.fire( new ContainerResizeEvent() );
    }

    @EventHandler( "tablet" )
    public void tabletSize( ClickEvent e ) {
        addClassName( layout, "simulate-sm" );
        removeClassName( layout, "simulate-xs" );
        resizeEvent.fire( new ContainerResizeEvent() );
    }

    @EventHandler( "desktop" )
    public void desktopSize( ClickEvent e ) {
        removeClassName( layout, "simulate-xs" );
        removeClassName( layout, "simulate-sm" );
        resizeEvent.fire( new ContainerResizeEvent() );
    }


    @Override
    public void addRow( UberElement<Row> view ) {
        if ( !hasClassName( layout, "container-canvas" ) ) {
            addClassName( layout, "container-canvas" );
        }
        removeClassName( layout, "container-empty" );
        layout.appendChild( view.getElement() );
    }

    @Override
    public void clear() {
        removeAllChildren( layout );
    }

    @Override
    public void addEmptyRow( UberElement<EmptyDropRow> emptyDropRow ) {
        removeClassName( layout, "container-canvas" );
        addClassName( layout, "container-empty" );
        layout.appendChild( emptyDropRow.getElement() );
    }

}
