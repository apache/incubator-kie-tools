package org.uberfire.mvp;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 *
 */
@Portable
public class PathPlaceRequest extends DefaultPlaceRequest {

    private Path path;

    public PathPlaceRequest() {
    }

    public PathPlaceRequest( final Path path ) {
        super( checkNotNull( "path", path ).toURI() );
        this.path = path;
    }

    public PathPlaceRequest( final Path path,
                             final Map<String, String> parameters ) {
        this( path );
        this.parameters.putAll( parameters );
    }

    public PathPlaceRequest( final Path path,
                             final String id ) {
        super( id );
        this.path = path;
    }

    public PathPlaceRequest( final Path path,
                             final String id,
                             final Map<String, String> parameters ) {
        this( path, id );
        this.parameters.putAll( parameters );
    }

    public Path getPath() {
        return path;
    }

    @Override
    public PlaceRequest clone() {
        return new PathPlaceRequest( path, identifier, parameters );
    }

}
