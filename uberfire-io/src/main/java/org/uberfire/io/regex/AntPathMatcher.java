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
