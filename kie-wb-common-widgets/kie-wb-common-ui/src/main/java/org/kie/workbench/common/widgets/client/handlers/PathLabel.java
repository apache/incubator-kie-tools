package org.kie.workbench.common.widgets.client.handlers;

import java.util.Map;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;

/**
 * A Label to show a Path, truncated to the closest folder level
 */
public class PathLabel extends Label {

    @Inject
    private Caller<VFSService> vfsService;

    private Path activePath;

    public void setPath( final Path path ) {
        if ( path == null ) {
            setText( CommonConstants.INSTANCE.ItemUndefinedPath() );
            return;
        }
        try {
            vfsService.call( new RemoteCallback<Map>() {
                @Override
                public void callback( final Map response ) {
                    if ( isRegularFile( response ) ) {
                        activePath = stripFileName( path );
                        setText( activePath.toURI() );
                    } else {
                        activePath = path;
                        setText( activePath.toURI() );
                    }
                }

            } ).readAttributes( path );
        } catch ( Exception e ) {
            //TODO readAttributes currently fails if the Path is a Root
            activePath = path;
            setText( activePath.toURI() );
        }
    }

    private boolean isRegularFile( final Map response ) {
        return response != null && response.containsKey( "isRegularFile" ) && (Boolean) response.get( "isRegularFile" );
    }

    public Path getPath() {
        return this.activePath;
    }

    private Path stripFileName( final Path path ) {
        String uri = path.toURI();
        uri = uri.replace( path.getFileName(), "" );
        return PathFactory.newPathBasedOn( path.getFileName(), uri, path );
    }

}
