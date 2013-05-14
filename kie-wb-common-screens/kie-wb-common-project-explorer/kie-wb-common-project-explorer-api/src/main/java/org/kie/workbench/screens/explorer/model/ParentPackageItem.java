package org.kie.workbench.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a parent Package within a Project
 */
@Portable
public class ParentPackageItem extends BaseItem {

    public ParentPackageItem() {
        //For Errai-marshalling
    }

    public ParentPackageItem( final Path path ) {
        super( path );
    }

    public ParentPackageItem( final Path path,
                              final String caption ) {
        super( path,
               caption );
        PortablePreconditions.checkNotNull( "path",
                                            path );
    }

    @Override
    public ItemType getType() {
        return ItemType.PARENT_PACKAGE;
    }

}
