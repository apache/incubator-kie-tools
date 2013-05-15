package org.kie.workbench.common.services.shared.file;

import org.uberfire.backend.vfs.Path;

public interface SupportsCopy {

    Path copy( final Path path,
               final String newName,
               final String comment );

}
