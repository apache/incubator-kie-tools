package org.uberfire.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Utilities for working with URIs that functions the same in both client and server code.
 * <p>
 * Implementation note: there is a separate GWT super-source implementation of this class for client-side use. If
 * modifying this class, be sure to go modify that one too.
 */
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
