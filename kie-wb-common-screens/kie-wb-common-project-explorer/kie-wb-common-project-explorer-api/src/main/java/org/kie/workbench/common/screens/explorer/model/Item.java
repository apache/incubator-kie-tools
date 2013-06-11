package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item in a package
 */
@Portable
public class Item {

    private Path path;
    private String fileName;

    public Item() {
        //For Errai-marshalling
    }

    public Item( final Path path,
                 final String fileName ) {
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
        this.fileName = PortablePreconditions.checkNotNull( "fileName",
                                                            fileName );
    }

    public Path getPath() {
        return this.path;
    }

    public String getFileName() {
        return this.fileName;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Item ) ) {
            return false;
        }

        Item item = (Item) o;

        if ( !path.equals( item.path ) ) {
            return false;
        }
        if ( !fileName.equals( item.fileName ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + fileName.hashCode();
        return result;
    }
}
