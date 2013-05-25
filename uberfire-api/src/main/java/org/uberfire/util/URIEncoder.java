package org.uberfire.util;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

public final class URIEncoder {

    public static String encode( String content ) {
        try {
            return URLEncoder.encode( content, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
        }
        return null;
    }

}
