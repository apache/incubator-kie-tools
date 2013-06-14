package org.uberfire.shared.source;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface SourceService {

    SourceLinedContent getLinedContent( final Path path );

    SourceContent getContent( final Path path,
                              final String fileName );

    void commit( final String repo,
                 final Path path,
                 final String fileName,
                 final String content,
                 final String userName,
                 final String email,
                 final String commitSummary,
                 final String commitDescription );
}
