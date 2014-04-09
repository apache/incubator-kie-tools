package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.File;
import java.io.IOException;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.UnknownCommand;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.security.authz.AuthorizationManager;

public class GitSSHService {

    final SshServer sshd = SshServer.setUpDefaultServer();
    private AuthenticationService authenticationService;
    private AuthorizationManager authorizationManager;

    public void setup( final File certDir,
                       final String host,
                       final int port,
                       final AuthenticationService authenticationService,
                       final AuthorizationManager authorizationManager,
                       final ReceivePackFactory receivePackFactory,
                       final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver ) {
        this.authenticationService = authenticationService;
        this.authorizationManager = authorizationManager;

        sshd.getProperties().put( SshServer.IDLE_TIMEOUT, "10000" );
        sshd.setHost( host );
        sshd.setPort( port );
        if ( !certDir.exists() ) {
            certDir.mkdirs();
        }
        sshd.setKeyPairProvider( new SimpleGeneratorHostKeyProvider( new File( certDir, "hostkey.ser" ).getAbsolutePath() ) );
        sshd.setCommandFactory( new CommandFactory() {
            @Override
            public Command createCommand( String command ) {
                if ( command.startsWith( "git-upload-pack" ) ) {
                    return new GitUploadCommand( command, repositoryResolver, getAuthorizationManager() );
                } else if ( command.startsWith( "git-receive-pack" ) ) {
                    return new GitReceiveCommand( command, repositoryResolver, getAuthorizationManager(), receivePackFactory );
                } else {
                    return new UnknownCommand( command );
                }
            }
        } );
        sshd.setPasswordAuthenticator( new PasswordAuthenticator() {
            @Override
            public boolean authenticate( final String username,
                                         final String password,
                                         final ServerSession session ) {
                try {
                    final User result = getAuthenticationManager().login( username, password );
                    if ( result != null ) {
                        session.setAttribute( BaseGitCommand.SUBJECT_KEY, result );
                    }
                    return result != null;
                } catch ( Exception ex ) {
                }
                return false;
            }
        } );
    }

    public void stop() {
        try {
            sshd.stop( true );
        } catch ( final InterruptedException e ) {
        }
    }

    public void start() {
        try {
            sshd.start();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public void setAuthorizationManager( final AuthorizationManager authorizationManager ) {
        this.authorizationManager = authorizationManager;
    }

    private AuthenticationService getAuthenticationManager() {
        return authenticationService;
    }

    public void setAuthenticationManager( final AuthenticationService authenticationService ) {
        this.authenticationService = authenticationService;
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }
}
