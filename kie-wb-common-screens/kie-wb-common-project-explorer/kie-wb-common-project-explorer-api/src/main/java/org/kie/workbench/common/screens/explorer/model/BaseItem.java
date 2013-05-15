package org.kie.workbench.common.screens.explorer.model;

import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * Base for all Items
 */
public abstract class BaseItem implements Item {

    private Path path;
    private String caption;

    public BaseItem() {
        //For Errai-marshalling
    }

    public BaseItem( final Path path ) {
        this( path,
              path.getFileName() );
    }

    public BaseItem( final Path path,
                     final String caption ) {
        PortablePreconditions.checkNotNull( "caption",
                                            caption );
        this.path = path;
        this.caption = caption;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public String getCaption() {
        return this.caption;
    }

}
