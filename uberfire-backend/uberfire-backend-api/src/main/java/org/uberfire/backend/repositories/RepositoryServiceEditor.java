package org.uberfire.backend.repositories;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;

@Remote
public interface RepositoryServiceEditor {

    List<VersionRecord> revertHistory( final String alias,
                                       final Path path,
                                       final String comment,
                                       final VersionRecord record );
}
