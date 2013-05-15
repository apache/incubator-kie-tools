package org.kie.guvnor.services.backend.file;

import org.kie.commons.java.nio.file.Path;

/**
 * A Filter to exclude "META-INF" folder from users
 */
public class LinkedMetaInfFolderFilter implements LinkedFilter {

    private LinkedFilter next = null;

    @Override
    public boolean accept( final Path path ) {
        if ( path.getFileName().toString().equalsIgnoreCase( "META-INF" ) ) {
            return false;
        }
        if ( next != null ) {
            return next.accept( path );
        }
        return true;
    }

    @Override
    public void setNextFilter( final LinkedFilter filter ) {
        this.next = filter;
    }

}
