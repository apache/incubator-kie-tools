package org.kie.workbench.common.services.project.service.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a Package within a Project
 */
@Portable
public class Package {

    private Path path;
    private String title;

    public Package() {
        //For Errai-marshalling
    }

    public Package( final Path path,
                    final String title ) {
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
        this.title = PortablePreconditions.checkNotNull( "title",
                                                         title );
    }

    public Path getPath() {
        return this.path;
    }

    public String getTitle() {
        return this.title;
    }

}
