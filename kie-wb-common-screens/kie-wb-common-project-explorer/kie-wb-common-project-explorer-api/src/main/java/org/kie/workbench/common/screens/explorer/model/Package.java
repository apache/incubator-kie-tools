package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a Package within a Project
 */
@Portable
public class Package {

    private Path projectRootPath;
    private String packageName;
    private String caption;

    public Package() {
        //For Errai-marshalling
    }

    public Package( final Path projectRootPath,
                    final String packageName,
                    final String caption ) {
        this.projectRootPath = PortablePreconditions.checkNotNull( "projectRootPath",
                                                                   projectRootPath );
        this.packageName = PortablePreconditions.checkNotNull( "packageName",
                                                               packageName );
        this.caption = PortablePreconditions.checkNotNull( "title",
                                                           caption );
    }

    public Path getProjectRootPath() {
        return this.projectRootPath;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getCaption() {
        return this.caption;
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

        if ( !projectRootPath.equals( aPackage.projectRootPath ) ) {
            return false;
        }
        if ( !packageName.equals( aPackage.packageName ) ) {
            return false;
        }
        if ( !caption.equals( aPackage.caption ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = projectRootPath.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + caption.hashCode();
        return result;
    }
}
