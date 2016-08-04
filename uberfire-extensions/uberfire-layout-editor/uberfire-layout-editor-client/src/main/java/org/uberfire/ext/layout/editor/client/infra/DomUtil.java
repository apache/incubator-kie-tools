package org.uberfire.ext.layout.editor.client.infra;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public class DomUtil {

    public static native String extractOffSetWidth( HTMLElement el ) /*-{
        return el.offsetWidth;
    }-*/;

    public static native String extractWidth( HTMLElement el ) /*-{
        return el.width;
    }-*/;


    public static native String extractClientY( Event e ) /*-{
        return e.clientY;
    }-*/;

    public static native String extractClientX( Event e ) /*-{
        return e.clientX;
    }-*/;

    public static int extractAbsoluteTop( HTMLElement el ) {
        ElementWrapperWidget<?> widget = ElementWrapperWidget.getWidget( el );
        return widget.getElement().getAbsoluteTop();
    }

    public static int extractAbsoluteBottom( HTMLElement el ) {
        ElementWrapperWidget<?> widget = ElementWrapperWidget.getWidget( el );
        return widget.getElement().getAbsoluteBottom();
    }



}
