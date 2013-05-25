package org.uberfire.util;

import com.google.gwt.http.client.URL;

public final class URIEncoder {

    public static String encode( String content ) {
        return URL.encode( content );
    }

}
