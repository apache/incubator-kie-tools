package org.uberfire.ext.layout.editor.client.infra;

import org.jboss.errai.common.client.dom.Event;

public class HTML5DnDHelper {

    public static native String extractDndData( Event e ) /*-{
        return e.dataTransfer.getData("text");
    }-*/;

    public static native String setDndData( Event e, String content ) /*-{
        return e.dataTransfer.setData("text", content);
    }-*/;

}
