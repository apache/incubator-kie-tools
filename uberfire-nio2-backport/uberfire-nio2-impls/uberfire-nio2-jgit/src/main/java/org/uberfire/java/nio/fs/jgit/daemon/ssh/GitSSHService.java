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
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;

public class GitSSHService {

    final SshServer sshd = SshServer.setUpDefaultServer();
    private FileSystemAuthenticator fileSystemAuthenticator;
    private FileSystemAuthorizer fileSystemAuthorizer;

    public void setup( final File certDir,
                       final String host,
                       final int port,
                       final FileSystemAuthenticator fileSystemAuthenticator,
                       final FileSystemAuthorizer fileSystemAuthorizer,
                       final ReceivePackFactory receivePackFactory,
                       final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver ) {
        this.fileSystemAuthenticator = fileSystemAuthenticator;
        this.fileSystemAuthorizer = fileSystemAuthorizer;

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
                    return new GitUploadCommand( command, repositoryResolver, fileSystemAuthorizer );
                } else if ( command.startsWith( "git-receive-pack" ) ) {
                    return new GitReceiveCommand( command, repositoryResolver, fileSystemAuthorizer, receivePackFactory );
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
                FileSystemUser user = fileSystemAuthenticator.authenticate( username, password );
                if ( user == null ) {
                    return false;
                }
                session.setAttribute( BaseGitCommand.SUBJECT_KEY, user );
                return true;
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

    public FileSystemAuthenticator getUserPassAuthenticator() {
        return fileSystemAuthenticator;
    }

    public void setUserPassAuthenticator( FileSystemAuthenticator fileSystemAuthenticator ) {
        this.fileSystemAuthenticator = fileSystemAuthenticator;
    }

    public FileSystemAuthorizer getAuthorizationManager() {
        return fileSystemAuthorizer;
    }

    public void setAuthorizationManager( FileSystemAuthorizer fileSystemAuthorizer ) {
        this.fileSystemAuthorizer = fileSystemAuthorizer;
    }
}
