package org.kie.workbench.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * A Bread Crumb item contained in a Bread Crumb trail
 */
@Portable
public class BreadCrumb {

    private Path path;
    private String caption;

    public BreadCrumb() {
        //For Errai-marshalling
    }

    public BreadCrumb( final Path path ) {
        this( path,
              path.getFileName() );
    }

    public BreadCrumb( final Path path,
                       final String caption ) {
        PortablePreconditions.checkNotNull( "caption",
                                            caption );
        this.path = path;
        this.caption = caption;
    }

    public Path getPath() {
        return this.path;
    }

    public String getCaption() {
        return this.caption;
    }

}
