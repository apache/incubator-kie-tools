package org.uberfire.mvp.impl;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.util.URIUtil;

/**
 *
 */
@Portable
public class PathPlaceRequest extends DefaultPlaceRequest {

    private ObservablePath path;

    public PathPlaceRequest() {
    }

    public PathPlaceRequest( final Path path ) {
        super( path.toURI() );
        this.path = IOC.getBeanManager().lookupBean( ObservablePath.class ).getInstance().wrap( path );
    }

    public PathPlaceRequest( final Path path,
                             final Map<String, String> parameters ) {
        this( path );
        this.parameters.putAll( parameters );
    }

    public PathPlaceRequest( final Path path,
                             final String id ) {
        super( id );
        this.path = IOC.getBeanManager().lookupBean( ObservablePath.class ).getInstance().wrap( path );
    }

    public PathPlaceRequest( final Path path,
                             final String id,
                             final Map<String, String> parameters ) {
        this( path, id );
        this.parameters.putAll( parameters );
    }

    public ObservablePath getPath() {
        return path;
    }

    @Override
    public String getFullIdentifier() {
        StringBuilder fullIdentifier = new StringBuilder();
        fullIdentifier.append( this.getIdentifier() );

        if ( !this.getIdentifier().equals( path.toURI() ) ) {
            fullIdentifier.append( "?" ).append( "path_uri" ).append( "=" ).append( URIUtil.encode( path.toURI() ) );
        } else if ( this.getParameterNames().size() > 0 ) {
            fullIdentifier.append( "?" );
        }

        for ( String name : this.getParameterNames() ) {
            fullIdentifier.append( name ).append( "=" ).append( this.getParameter( name, null ) );
            fullIdentifier.append( "&" );
        }

        if ( fullIdentifier.length() != 0 && fullIdentifier.lastIndexOf( "&" ) + 1 == fullIdentifier.length() ) {
            fullIdentifier.deleteCharAt( fullIdentifier.length() - 1 );
        }

        return fullIdentifier.toString();
    }

    @Override
    public PlaceRequest clone() {
        return new PathPlaceRequest( path, identifier, parameters );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final PathPlaceRequest that = (PathPlaceRequest) o;

        return path.equals( that.path );
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
