package org.kie.workbench.common.services.project.service.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a Package within a Project
 */
@Portable
public class Package {

    private Path projectRootPath;
    private Path packageMainSrcPath;
    private Path packageMainResourcesPath;
    private Path packageTestSrcPath;
    private Path packageTestResourcesPath;
    private String packageName;
    private String caption;

    public Package() {
        //For Errai-marshalling
    }

    public Package( final Path projectRootPath,
                    final Path packageMainSrcPath,
                    final Path packageMainResourcesPath,
                    final Path packageTestSrcPath,
                    final Path packageTestResourcesPath,
                    final String packageName,
                    final String caption ) {
        this.projectRootPath = PortablePreconditions.checkNotNull( "projectRootPath",
                                                                   projectRootPath );
        this.packageMainSrcPath = PortablePreconditions.checkNotNull( "packageMainSrcPath",
                                                                      packageMainSrcPath );
        this.packageMainResourcesPath = PortablePreconditions.checkNotNull( "packageMainResourcesPath",
                                                                            packageMainResourcesPath );
        this.packageTestSrcPath = PortablePreconditions.checkNotNull( "packageTestSrcPath",
                                                                      packageTestSrcPath );
        this.packageTestResourcesPath = PortablePreconditions.checkNotNull( "packageTestResourcesPath",
                                                                            packageTestResourcesPath );
        this.packageName = PortablePreconditions.checkNotNull( "packageName",
                                                               packageName );
        this.caption = PortablePreconditions.checkNotNull( "title",
                                                           caption );
    }

    public Path getProjectRootPath() {
        return this.projectRootPath;
    }

    public Path getPackageMainSrcPath() {
        return packageMainSrcPath;
    }

    public Path getPackageMainResourcesPath() {
        return packageMainResourcesPath;
    }

    public Path getPackageTestSrcPath() {
        return packageTestSrcPath;
    }

    public Path getPackageTestResourcesPath() {
        return packageTestResourcesPath;
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

        if ( !caption.equals( aPackage.caption ) ) {
            return false;
        }
        if ( !packageMainResourcesPath.equals( aPackage.packageMainResourcesPath ) ) {
            return false;
        }
        if ( !packageMainSrcPath.equals( aPackage.packageMainSrcPath ) ) {
            return false;
        }
        if ( !packageName.equals( aPackage.packageName ) ) {
            return false;
        }
        if ( !packageTestResourcesPath.equals( aPackage.packageTestResourcesPath ) ) {
            return false;
        }
        if ( !packageTestSrcPath.equals( aPackage.packageTestSrcPath ) ) {
            return false;
        }
        if ( !projectRootPath.equals( aPackage.projectRootPath ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = projectRootPath.hashCode();
        result = 31 * result + packageMainSrcPath.hashCode();
        result = 31 * result + packageMainResourcesPath.hashCode();
        result = 31 * result + packageTestSrcPath.hashCode();
        result = 31 * result + packageTestResourcesPath.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + caption.hashCode();
        return result;
    }
}
