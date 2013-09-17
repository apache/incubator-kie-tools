package org.uberfire.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public final class URIUtil {

    public static String encode( final String content ) {
        try {
            return URLEncoder.encode( content, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
        }
        return null;
    }

    public static boolean isValid( final String uri ) {
        try {
            URI.create( uri );
            return true;
        } catch ( final Exception ignored ) {
        }
        return false;
    }

}
