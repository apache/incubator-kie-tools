package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a folder
 */
@Portable
public class FolderItem extends BaseItem {

    public FolderItem() {
        //For Errai-marshalling
    }

    public FolderItem( final Path path ) {
        super( path );
    }

    public FolderItem( final Path path,
                       final String caption ) {
        super( path,
               caption );
    }

    @Override
    public ItemType getType() {
        return ItemType.FOLDER;
    }

}
