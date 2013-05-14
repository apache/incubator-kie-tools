package org.kie.workbench.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a Package within a Project
 */
@Portable
public class PackageItem extends BaseItem {

    public PackageItem() {
        //For Errai-marshalling
    }

    public PackageItem( final Path path ) {
        super( path );
    }

    public PackageItem( final Path path,
                        final String caption ) {
        super( path,
               caption );
        PortablePreconditions.checkNotNull( "path",
                                            path );
    }

    @Override
    public ItemType getType() {
        return ItemType.PACKAGE;
    }

}
