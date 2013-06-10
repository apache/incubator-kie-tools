package org.kie.workbench.common.services.project.service.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a Package within a Project
 */
@Portable
public class Package {

    private Path relativePath;
    private String title;

    public Package() {
        //For Errai-marshalling
    }

    public Package( final Path relativePath,
                    final String title ) {
        this.relativePath = PortablePreconditions.checkNotNull( "relativePath",
                                                                relativePath );
        this.title = PortablePreconditions.checkNotNull( "title",
                                                         title );
    }

    public Path getRelativePath() {
        return this.relativePath;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Package ) ) {
            return false;
        }

        Package aPackage = (Package) o;

        if ( !relativePath.equals( aPackage.relativePath ) ) {
            return false;
        }
        if ( !title.equals( aPackage.title ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = relativePath.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
}
