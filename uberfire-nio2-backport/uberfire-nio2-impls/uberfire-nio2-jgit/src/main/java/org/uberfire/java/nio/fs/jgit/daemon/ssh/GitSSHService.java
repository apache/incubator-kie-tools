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
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.security.AuthorizationManager;
import org.uberfire.java.nio.security.Session;
import org.uberfire.java.nio.security.Subject;
import org.uberfire.java.nio.security.UserPassAuthenticator;

public class GitSSHService {

    final SshServer sshd = SshServer.setUpDefaultServer();
    private UserPassAuthenticator userPassAuthenticator;
    private AuthorizationManager authorizationManager;

    public void setup( final File certDir,
                       final String host,
                       final int port,
                       final UserPassAuthenticator userPassAuthenticator,
                       final AuthorizationManager authorizationManager,
                       final ReceivePackFactory receivePackFactory,
                       final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver ) {
        this.userPassAuthenticator = userPassAuthenticator;
        this.authorizationManager = authorizationManager;

        sshd.getProperties().put( SshServer.IDLE_TIMEOUT, "10000" );
        sshd.setHost( host );
        sshd.setPort( port );
        if ( !certDir.exists() ) {
            certDir.mkdirs();
        }
        sshd.setKeyPairProvider( new SimpleGeneratorHostKeyProvider( new File( certDir, "hostkey.ser" ).getAbsolutePath() ) );
        sshd.setCommandFactory( new CommandFactory() {
            public Command createCommand( String command ) {
                if ( command.startsWith( "git-upload-pack" ) ) {
                    return new GitUploadCommand( command, repositoryResolver, authorizationManager );
                } else if ( command.startsWith( "git-receive-pack" ) ) {
                    return new GitReceiveCommand( command, repositoryResolver, authorizationManager, receivePackFactory );
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
                return userPassAuthenticator.authenticate( username, password, new Session() {
                    @Override
                    public void setSubject( final Subject value ) {
                        session.setAttribute( BaseGitCommand.SUBJECT_KEY, value );
                    }

                    @Override
                    public Subject getSubject() {
                        return session.getAttribute( BaseGitCommand.SUBJECT_KEY );
                    }
                } );
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

    public UserPassAuthenticator getUserPassAuthenticator() {
        return userPassAuthenticator;
    }

    public void setUserPassAuthenticator( UserPassAuthenticator userPassAuthenticator ) {
        this.userPassAuthenticator = userPassAuthenticator;
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public void setAuthorizationManager( AuthorizationManager authorizationManager ) {
        this.authorizationManager = authorizationManager;
    }
}
