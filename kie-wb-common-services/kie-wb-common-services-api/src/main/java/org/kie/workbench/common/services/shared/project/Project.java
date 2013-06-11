package org.kie.workbench.common.services.shared.project;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a project
 */
@Portable
public class Project {

    private Path path;
    private String title;

    public Project() {
        //For Errai-marshalling
    }

    public Project( final Path path,
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Project ) ) {
            return false;
        }

        Project project = (Project) o;

        if ( !path.equals( project.path ) ) {
            return false;
        }
        if ( !title.equals( project.title ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
}
