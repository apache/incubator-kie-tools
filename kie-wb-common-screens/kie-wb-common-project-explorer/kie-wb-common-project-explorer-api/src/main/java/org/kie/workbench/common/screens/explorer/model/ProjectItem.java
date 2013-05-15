package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a project
 */
@Portable
public class ProjectItem extends BaseItem {

    public ProjectItem() {
        //For Errai-marshalling
    }

    public ProjectItem( final Path path ) {
        super( path );
    }

    public ProjectItem( final Path path,
                        final String caption ) {
        super( path,
               caption );
        PortablePreconditions.checkNotNull( "path",
                                            path );
    }

    @Override
    public ItemType getType() {
        return ItemType.PROJECT;
    }

}
