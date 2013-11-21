package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.security.Subject;
import org.uberfire.security.authz.AuthorizationManager;

public class GitUploadCommand extends BaseGitCommand {

    public GitUploadCommand( final String command,
                             final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                             final AuthorizationManager authorizationManager ) {
        super( command, authorizationManager, repositoryResolver );
    }

    @Override
    protected String getCommandName() {
        return "git-upload-pack";
    }

    @Override
    protected void execute( final Subject user,
                            final Repository repository,
                            final InputStream in,
                            final OutputStream out,
                            final OutputStream err ) {
        final UploadPack up = new UploadPack( repository );

        try {
            up.upload( in, out, err );
        } catch ( IOException e ) {
        }
    }
}
