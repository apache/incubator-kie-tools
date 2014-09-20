package org.kie.uberfire.plugin.client.widget.split;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public abstract class Splitter extends FlowPanel {

    protected static Element glassElem = null;

    protected boolean mouseDown;

    public Splitter() {
        if ( glassElem == null ) {
            glassElem = Document.get().createDivElement();
            glassElem.getStyle().setPosition( Style.Position.ABSOLUTE );
            glassElem.getStyle().setTop( 0,
                                         Style.Unit.PX );
            glassElem.getStyle().setLeft( 0,
                                          Style.Unit.PX );
            glassElem.getStyle().setMargin( 0,
                                            Style.Unit.PX );
            glassElem.getStyle().setPadding( 0,
                                             Style.Unit.PX );
            glassElem.getStyle().setBorderWidth( 0,
                                                 Style.Unit.PX );

            // We need to set the background color or mouse events will go right
            // through the glassElem. If the SplitPanel contains an iframe, the
            // iframe will capture the event and the slider will stop moving.
            glassElem.getStyle().setProperty( "background",
                                              "white" );
            glassElem.getStyle().setOpacity( 0.0 );
        }

        sinkEvents( Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK );
        addDomHandler( new MouseDownHandler() {
            @Override
            public void onMouseDown( final MouseDownEvent event ) {
                mouseDown = true;
                int width = Math.max( Window.getClientWidth(), Document.get().getScrollWidth() );
                int height = Math.max( Window.getClientHeight(), Document.get().getScrollHeight() );
                glassElem.getStyle().setHeight( height,
                                                Style.Unit.PX );
                glassElem.getStyle().setWidth( width,
                                               Style.Unit.PX );
                Document.get().getBody().appendChild( glassElem );

                buildOffset( event );
                Event.setCapture( getElement() );
                event.preventDefault();
            }
        }, MouseDownEvent.getType() );

        addDomHandler( new MouseUpHandler() {
            @Override
            public void onMouseUp( final MouseUpEvent event ) {
                mouseDown = false;

                glassElem.removeFromParent();

                Event.releaseCapture( getElement() );
            }
        }, MouseUpEvent.getType() );

    }

    protected abstract void buildOffset( MouseDownEvent event );
}
