/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.io.regex;

import java.net.URI;
import java.util.Collection;

import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.validation.Preconditions.*;

public final class AntPathMatcher {

    private static org.uberfire.commons.regex.util.AntPathMatcher matcher = new org.uberfire.commons.regex.util.AntPathMatcher();

    public static boolean filter( final Collection<String> includes,
                                  final Collection<String> excludes,
                                  final Path path ) {
        checkNotNull( "includes", includes );
        checkNotNull( "excludes", excludes );
        checkNotNull( "path", path );
        if ( includes.isEmpty() && excludes.isEmpty() ) {
            return true;
        } else if ( includes.isEmpty() ) {
            return !( excludes( excludes, path ) );
        } else if ( excludes.isEmpty() ) {
            return includes( includes, path );
        }
        return includes( includes, path ) && !( excludes( excludes, path ) );
    }

    public static boolean filter( final Collection<String> includes,
                                  final Collection<String> excludes,
                                  final URI uri ) {
        checkNotNull( "includes", includes );
        checkNotNull( "excludes", excludes );
        checkNotNull( "uri", uri );
        if ( includes.isEmpty() && excludes.isEmpty() ) {
            return true;
        } else if ( includes.isEmpty() ) {
            return !( excludes( excludes, uri ) );
        } else if ( excludes.isEmpty() ) {
            return includes( includes, uri );
        }
        return includes( includes, uri ) && !( excludes( excludes, uri ) );
    }

    public static boolean includes( final Collection<String> patterns,
                                    final Path path ) {
        checkNotNull( "patterns", patterns );
        checkNotNull( "path", path );
        return matches( patterns, path );
    }

    public static boolean includes( final Collection<String> patterns,
                                    final URI uri ) {
        checkNotNull( "patterns", patterns );
        checkNotNull( "uri", uri );
        return matches( patterns, uri );
    }

    public static boolean excludes( final Collection<String> patterns,
                                    final URI uri ) {
        checkNotNull( "patterns", patterns );
        checkNotNull( "uri", uri );
        return matches( patterns, uri );
    }

    public static boolean excludes( final Collection<String> patterns,
                                    final Path path ) {
        checkNotNull( "patterns", patterns );
        checkNotNull( "path", path );
        return matches( patterns, path );
    }

    private static boolean matches( final Collection<String> patterns,
                                    final Path path ) {
        return matches( patterns, path.toUri() );
    }

    private static boolean matches( final Collection<String> patterns,
                                    final URI uri ) {
        for ( final String pattern : patterns ) {
            if ( matcher.match( pattern, uri.toString() ) ) {
                return true;
            }
        }
        return false;
    }

}
