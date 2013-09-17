package org.uberfire.util;

import com.google.gwt.http.client.URL;

public final class URIUtil {

    public static String encode( String content ) {
        return URL.encode( content );
    }

    public native static boolean isValid( final String uri ) /*-{
        var components = $wnd.URI.parse(uri);
        if (typeof components.errors !== 'undefined' && components.errors.length > 0) {
            return false;
        }
        if (components.reference != "absolute" ) {
            return false;
        }
        return true;
    }-*/;
}