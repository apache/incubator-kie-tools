package org.kie.workbench.common.services.backend.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.validation.PortablePreconditions;

/**
 * Default implementation of FileDiscoveryService
 */
@ApplicationScoped
public class FileDiscoveryServiceImpl implements FileDiscoveryService {

    @Override
    public Collection<Path> discoverFiles( final Path pathToSearch,
                                           final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> filter,
                                           final boolean recursive ) {
        PortablePreconditions.checkNotNull( "pathToSearch",
                                            pathToSearch );
        PortablePreconditions.checkNotNull( "filter",
                                            filter );

        final List<Path> discoveredFiles = new ArrayList<Path>();

        //The pathToSearch could be a file, and of the type we need
        if ( Files.isRegularFile( pathToSearch ) ) {
            if ( filter.accept( pathToSearch ) ) {
                discoveredFiles.add( pathToSearch );
                return discoveredFiles;
            }
        }

        //This check should never match, but it's included as a safe-guard
        if ( !Files.isDirectory( pathToSearch ) ) {
            return discoveredFiles;
        }

        //Path represents a Folder, so check and recursively add it's content, if applicable
        final DirectoryStream<Path> paths = Files.newDirectoryStream( pathToSearch );
        for ( final Path path : paths ) {
            if ( Files.isRegularFile( path ) ) {
                if ( filter.accept( path ) ) {
                    discoveredFiles.add( path );
                }
            } else if ( recursive && Files.isDirectory( path ) ) {
                discoveredFiles.addAll( discoverFiles( path,
                                                       filter,
                                                       recursive ) );
            }
        }

        return discoveredFiles;
    }

    @Override
    public Collection<Path> discoverFiles( final Path pathToSearch,
                                           final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> filter ) {
        return discoverFiles( pathToSearch,
                              filter,
                              false );
    }

}
