package org.kie.workbench.services.shared.file;

import org.uberfire.backend.vfs.Path;

public interface SupportsCreate<T> {

    Path create( final Path context,
                 final String fileName,
                 final T content,
                 final String comment );

}
