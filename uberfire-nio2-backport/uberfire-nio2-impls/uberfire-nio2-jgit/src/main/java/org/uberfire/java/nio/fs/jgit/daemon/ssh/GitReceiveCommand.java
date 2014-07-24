package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.security.AuthorizationManager;
import org.uberfire.java.nio.security.Subject;

public class GitReceiveCommand extends BaseGitCommand {

    private final ReceivePackFactory<BaseGitCommand> receivePackFactory;

    public GitReceiveCommand( final String command,
                              final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                              final AuthorizationManager authorizationManager,
                              final ReceivePackFactory<BaseGitCommand> receivePackFactory ) {
        super( command, authorizationManager, repositoryResolver );
        this.receivePackFactory = receivePackFactory;
    }

    @Override
    protected String getCommandName() {
        return "git-receive-pack";
    }

    @Override
    protected void execute( final Subject user,
                            final Repository repository,
                            final InputStream in,
                            final OutputStream out,
                            final OutputStream err ) {
        try {
            final ReceivePack rp = receivePackFactory.create( this, repository );
            rp.receive( in, out, err );
        } catch ( Exception ex ) {
        }
    }
}
