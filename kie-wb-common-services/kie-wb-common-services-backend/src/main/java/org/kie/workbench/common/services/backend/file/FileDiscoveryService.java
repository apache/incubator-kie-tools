package org.kie.workbench.common.services.backend.file;

import java.util.Collection;

import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Path;

/**
 * Service to discover files in a given Path
 */
public interface FileDiscoveryService {

    /**
     * Discover files
     * @param pathToSearch The root Path to search. Sub-folders are not included.
     * @param filter A filter to restrict the matched files.
     * @param recursive True is sub-folders are to be scanned
     * @return
     */
    Collection<Path> discoverFiles( final Path pathToSearch,
                                    final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> filter,
                                    final boolean recursive );

    /**
     * Discover files. Convenience method excluding sub-folders
     * @param pathToSearch The root Path to search. Sub-folders are not included.
     * @param filter A filter to restrict the matched files.
     * @return
     */
    Collection<Path> discoverFiles( final Path pathToSearch,
                                    final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> filter );

}
