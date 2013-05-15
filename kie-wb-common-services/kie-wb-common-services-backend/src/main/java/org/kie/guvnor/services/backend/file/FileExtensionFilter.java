package org.kie.guvnor.services.backend.file;

import org.kie.commons.java.nio.file.Files;
import org.kie.commons.validation.PortablePreconditions;

/**
 * A Filter only accepting files with the given file extension
 */
public class FileExtensionFilter extends DotFileFilter {

    private String extension;

    public FileExtensionFilter( final String extension ) {
        this.extension = PortablePreconditions.checkNotNull( "extension",
                                                             extension );
        if ( !extension.startsWith( "." ) ) {
            this.extension = "." + extension;
        }
    }

    @Override
    public boolean accept( final org.kie.commons.java.nio.file.Path path ) {
        boolean accept = super.accept( path );
        if ( !accept ) {
            return accept;
        }
        if ( !Files.isRegularFile( path ) ) {
            return false;
        }
        final String uri = path.toUri().toString();
        if ( uri.substring( uri.length() - extension.length() ).equals( extension ) ) {
            return true;
        }
        return false;
    }

}
