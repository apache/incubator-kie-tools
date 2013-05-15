package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a repository
 */
@Portable
public class RepositoryItem extends BaseItem {

    public RepositoryItem() {
        //For Errai-marshalling
    }

    public RepositoryItem( final Path path ) {
        super( path );
    }

    public RepositoryItem( final Path path,
                           final String caption ) {
        super( path,
               caption );
        PortablePreconditions.checkNotNull( "path",
                                            path );
    }

    @Override
    public ItemType getType() {
        return ItemType.REPOSITORY;
    }

}
