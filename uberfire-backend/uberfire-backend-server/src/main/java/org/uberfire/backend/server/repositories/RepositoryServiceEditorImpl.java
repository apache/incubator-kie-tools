package org.uberfire.backend.server.repositories;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.repositories.RepositoryServiceEditor;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.java.nio.file.StandardCopyOption.*;

@Service
@ApplicationScoped
public class RepositoryServiceEditorImpl implements RepositoryServiceEditor {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public List<VersionRecord> revertHistory( final String alias,
                                              final Path path,
                                              final String _comment,
                                              final VersionRecord record ) {
        final org.uberfire.java.nio.file.Path history = ioService.get( URI.create( record.uri() ) );

        final String comment;
        if ( _comment == null || _comment.trim().isEmpty() ) {
            comment = "revert history from commit {" + record.comment() + "}";
        } else {
            comment = _comment;
        }

        ioService.move( history, Paths.convert( path ), REPLACE_EXISTING, new CommentedOption( sessionInfo.getId(), sessionInfo.getIdentity().getName(), null, comment ) );

        return new ArrayList<VersionRecord>( repositoryService.getRepositoryInfo( alias ).getInitialVersionList() );
    }

}
