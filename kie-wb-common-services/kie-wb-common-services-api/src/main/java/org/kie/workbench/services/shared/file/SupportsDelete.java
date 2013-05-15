package org.kie.workbench.services.shared.file;

import org.uberfire.backend.vfs.Path;

public interface SupportsDelete {

    void delete( final Path path,
                 final String comment );

}
