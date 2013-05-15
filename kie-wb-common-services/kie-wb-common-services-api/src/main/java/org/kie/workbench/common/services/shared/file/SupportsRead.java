package org.kie.workbench.common.services.shared.file;

import org.uberfire.backend.vfs.Path;

public interface SupportsRead<T> {

    T load( final Path path );

}
