package org.uberfire.util;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;

/**
 * GWT client-side implementation of URIUtil which relies on URI.js.
 */
public final class URIUtil {

    private static final Resources RESOURCES = GWT.create(Resources.class);
    
    interface Resources extends ClientBundle {
        @Source("uri.min.js")
        TextResource uriDotJs();
    }
    
    static {
        ScriptInjector.fromString( RESOURCES.uriDotJs().getText() ).inject();
    }
    
    public static String encode( String content ) {
        return URL.encode( content );
    }

    public native static boolean isValid( final String uri ) /*-{
        var components = URI.parse(uri);
        if (typeof components.errors !== 'undefined' && components.errors.length > 0) {
            return false;
        }
        if (components.reference != "absolute" ) {
            return false;
        }
        return true;
    }-*/;
}