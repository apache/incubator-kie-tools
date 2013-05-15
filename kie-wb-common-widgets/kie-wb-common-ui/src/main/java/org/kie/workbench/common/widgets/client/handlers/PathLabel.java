package org.kie.workbench.common.widgets.client.handlers;

import java.util.Map;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Paragraph;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.AttrsUtil;
import org.uberfire.backend.vfs.BasicFileAttributes;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;

/**
 * A Label to show a Path, truncated to the closest folder level
 */
public class PathLabel extends Paragraph {

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
                    final BasicFileAttributes attrs = AttrsUtil.toBasicFileAttributes( response );
                    if ( attrs.isRegularFile() ) {
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

    public Path getPath() {
        return this.activePath;
    }

    private Path stripFileName( final Path path ) {
        String uri = path.toURI();
        uri = uri.replace( path.getFileName(), "" );
        return PathFactory.newPath( path.getFileSystem(), path.getFileName(), uri );
    }

}
