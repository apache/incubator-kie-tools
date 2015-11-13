/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
