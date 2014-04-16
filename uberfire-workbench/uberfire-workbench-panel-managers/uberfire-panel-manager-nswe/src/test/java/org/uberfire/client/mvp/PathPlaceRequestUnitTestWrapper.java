package org.uberfire.client.mvp;

import java.util.Map;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

public class PathPlaceRequestUnitTestWrapper extends PathPlaceRequest {

    private final ObservablePath pathFake;

    public PathPlaceRequestUnitTestWrapper( final ObservablePath path ) {
        super();
        this.pathFake = path;
    }

    public PathPlaceRequestUnitTestWrapper( final ObservablePath path,
                             final String id ) {
        this.pathFake = path;
    }

    public PathPlaceRequestUnitTestWrapper( final ObservablePath path,
                             final String id,
                             final Map<String, String> parameters ) {
        this( path, id );
        this.parameters.putAll( parameters );
    }
    public ObservablePath getPath() {
        return pathFake;
    }

    @Override
    public PlaceRequest clone() {
        return new PathPlaceRequestUnitTestWrapper( pathFake, identifier, parameters );
    }

}
