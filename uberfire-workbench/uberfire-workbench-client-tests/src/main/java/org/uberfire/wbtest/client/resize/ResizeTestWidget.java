package org.uberfire.wbtest.client.resize;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A widget that reports its current size as its textual content. The text is updated on every call to
 * {@link #onResize()}.
 */
public class ResizeTestWidget extends Label implements RequiresResize, ProvidesResize {

    public static String DEBUG_ID_PREFIX = "ResizeTestWidget-";

    public ResizeTestWidget( String id ) {
        ensureDebugId( DEBUG_ID_PREFIX + id );
        setText( "no onResize yet" );
    }

    @Override
    public void onResize() {
        setText( getOffsetWidth() + "x" + getOffsetHeight() );
    }
}