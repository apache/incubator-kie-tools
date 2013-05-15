package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a file
 */
@Portable
public class FileItem extends BaseItem {

    public FileItem() {
        //For Errai-marshalling
    }

    public FileItem( final Path path ) {
        super( path );
    }

    public FileItem( final Path path,
                     final String caption ) {
        super( path,
               caption );
        PortablePreconditions.checkNotNull( "path",
                                            path );
    }

    @Override
    public ItemType getType() {
        return ItemType.FILE;
    }

}
