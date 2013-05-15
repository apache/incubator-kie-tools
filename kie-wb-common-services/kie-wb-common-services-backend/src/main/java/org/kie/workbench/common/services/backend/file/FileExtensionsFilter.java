package org.kie.workbench.common.services.backend.file;

import org.kie.commons.java.nio.file.Files;
import org.kie.commons.validation.PortablePreconditions;

/**
 * A Filter only accepting files with the given file extensions
 */
public class FileExtensionsFilter extends DotFileFilter {

    private String[] extensions;

    public FileExtensionsFilter( final String[] extensions ) {
        this.extensions = PortablePreconditions.checkNotNull( "extension",
                                                              extensions );
        for ( int i = 0; i < extensions.length; i++ ) {
            if ( !extensions[ i ].startsWith( "." ) ) {
                extensions[ i ] = "." + extensions[ i ];
            }
        }
    }

    @Override
    public boolean accept( final org.kie.commons.java.nio.file.Path path ) {
        //Check with super class first
        boolean accept = super.accept( path );
        if ( !accept ) {
            return accept;
        }

        //Only match files
        if ( !Files.isRegularFile( path ) ) {
            return false;
        }

        //Assume the Path does not match by default
        accept = false;
        final String uri = path.toUri().toString();
        for ( String extension : extensions ) {
            if ( uri.substring( uri.length() - extension.length() ).equals( extension ) ) {
                accept = true;
                break;
            }
        }
        return accept;
    }

}
