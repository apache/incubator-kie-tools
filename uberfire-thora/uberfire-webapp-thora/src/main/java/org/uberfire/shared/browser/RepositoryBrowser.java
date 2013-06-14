package org.uberfire.shared.browser;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface RepositoryBrowser {

    ResultListContent listContent( final Path path );

}
