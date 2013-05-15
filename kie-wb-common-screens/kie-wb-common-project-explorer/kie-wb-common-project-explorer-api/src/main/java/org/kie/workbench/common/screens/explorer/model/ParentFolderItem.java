package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a parent Folder
 */
@Portable
public class ParentFolderItem extends BaseItem {

    public ParentFolderItem() {
        //For Errai-marshalling
    }

    public ParentFolderItem( final Path path ) {
        super( path );
    }

    public ParentFolderItem( final Path path,
                             final String caption ) {
        super( path,
               caption );
    }

    @Override
    public ItemType getType() {
        return ItemType.PARENT_FOLDER;
    }

}
