package org.uberfire.workbench.events;

import org.uberfire.backend.vfs.Path;

public interface ResourceEvent extends ResourceChange {

    public Path getPath();

}
