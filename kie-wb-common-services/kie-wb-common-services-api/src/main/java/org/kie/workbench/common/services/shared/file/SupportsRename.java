package org.kie.workbench.common.services.shared.file;

import org.uberfire.backend.vfs.Path;

public interface SupportsRename {

    Path rename( final Path path,
                 final String newName,
                 final String comment );

}
