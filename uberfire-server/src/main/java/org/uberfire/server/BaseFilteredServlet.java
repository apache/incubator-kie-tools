package org.uberfire.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.regex.AntPathMatcher;
import org.uberfire.java.nio.file.Path;

import static javax.servlet.http.HttpServletResponse.*;

public abstract class BaseFilteredServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger( BaseFilteredServlet.class );

    protected Collection<String> includes = new ArrayList<String>();
    protected Collection<String> excludes = new ArrayList<String>();

    @Override
    public void init( final ServletConfig config ) throws ServletException {
        super.init( config );
        final String _includes = config.getInitParameter( "includes-path" );
        if ( _includes != null && !_includes.trim().isEmpty() ) {
            includes.addAll( Arrays.asList( _includes.split( "," ) ) );
        }
        final String _excludes = config.getInitParameter( "excludes-path" );
        if ( _excludes != null && !_excludes.trim().isEmpty() ) {
            excludes.addAll( Arrays.asList( _excludes.split( "," ) ) );
        }
    }

    protected boolean validateAccess( final URI uri,
                                      final HttpServletResponse response ) {
        if ( !AntPathMatcher.filter( includes, excludes, uri ) ) {
            logger.error( "Invalid credentials to path." );
            try {
                response.sendError( SC_FORBIDDEN );
            } catch ( Exception ex ) {
                logger.error( ex.getMessage() );
            }
            return false;
        }
        return true;
    }

    protected boolean validateAccess( final Path path,
                                      final HttpServletResponse response ) {
        if ( !AntPathMatcher.filter( includes, excludes, path ) ) {
            logger.error( "Invalid credentials to path." );
            try {
                response.sendError( SC_FORBIDDEN );
            } catch ( Exception ex ) {
                logger.error( ex.getMessage() );
            }
            return false;
        }
        return true;
    }

}
