package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.UploadPack;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;

public class GitUploadCommand extends BaseGitCommand {

    public GitUploadCommand( final String command,
                             final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                             final FileSystemAuthorizer fileSystemAuthorizer ) {
        super( command, fileSystemAuthorizer, repositoryResolver );
    }

    @Override
    protected String getCommandName() {
        return "git-upload-pack";
    }

    @Override
    protected void execute( final FileSystemUser user,
                            final Repository repository,
                            final InputStream in,
                            final OutputStream out,
                            final OutputStream err ) {
        final UploadPack up = new UploadPack( repository );

        final PackConfig config = new PackConfig( repository );
        config.setCompressionLevel( Deflater.BEST_COMPRESSION );
        up.setPackConfig( config );

        try {
            up.upload( in, out, err );
        } catch ( IOException e ) {
        }
    }
}
