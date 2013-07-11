package org.uberfire.navigator;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;

@Remote
public interface FileNavigatorService {

    NavigatorContent listContent( final Path path );

    List<Repository> listRepositories();

}
