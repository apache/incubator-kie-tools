package org.kie.workbench.common.services.shared.readonly;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.impl.PathPlaceRequest;

import java.util.Map;

@Portable
public class ReadOnlyPathPlaceRequest
        extends PathPlaceRequest {

    public ReadOnlyPathPlaceRequest() {
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path ) {
        super( path );
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path,
                                     Map<String, String> parameters ) {
        super( path, parameters );
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path,
                                     String id ) {
        super( path, id );
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path,
                                     String id,
                                     Map<String, String> parameters ) {
        super( path, id, parameters );
        addParameter( "readOnly", "yes" );
    }
}
