package org.uberfire.client.workbench.widgets.dnd;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DragArea extends SimplePanel implements HasClickHandlers,
                                                     HasMouseDownHandlers {

    public DragArea() {
        super();
    }

    public DragArea( final Widget child ) {
        super( child );
    }

    public void add( final Element element ) {
        getElement().appendChild( element );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return addDomHandler( handler, ClickEvent.getType() );
    }

    @Override
    public HandlerRegistration addMouseDownHandler( MouseDownHandler handler ) {
        return addDomHandler( handler, MouseDownEvent.getType() );
    }
}
