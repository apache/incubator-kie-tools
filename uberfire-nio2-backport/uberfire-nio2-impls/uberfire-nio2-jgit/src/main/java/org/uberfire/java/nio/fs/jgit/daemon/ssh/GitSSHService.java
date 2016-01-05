/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;

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

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class GitSSHService {

    private final SshServer sshd = SshServer.setUpDefaultServer();
    private FileSystemAuthenticator fileSystemAuthenticator;
    private FileSystemAuthorizer fileSystemAuthorizer;

    public void setup( final File certDir,
                       final InetSocketAddress inetSocketAddress,
                       final String sshIdleTimeout,
                       final ReceivePackFactory receivePackFactory,
                       final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver ) {
        checkNotNull( "certDir", certDir );
        checkNotEmpty( "sshIdleTimeout", sshIdleTimeout );
        checkNotNull( "receivePackFactory", receivePackFactory );
        checkNotNull( "repositoryResolver", repositoryResolver );

        sshd.getProperties().put( SshServer.IDLE_TIMEOUT, sshIdleTimeout );

        if ( inetSocketAddress != null ) {
            sshd.setHost( inetSocketAddress.getHostName() );
            sshd.setPort( inetSocketAddress.getPort() );
        }

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
                FileSystemUser user = getUserPassAuthenticator().authenticate( username, password );
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
        } catch ( final InterruptedException ignored ) {
        }
    }

    public void start() {
        try {
            sshd.start();
        } catch ( IOException e ) {
            throw new RuntimeException( "Couldn't start SSH daemon at " + sshd.getHost() + ":" + sshd.getPort(), e );
        }
    }

    public boolean isRunning() {
        return !( sshd.isClosed() || sshd.isClosing() );
    }

    SshServer getSshServer() {
        return sshd;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap( sshd.getProperties() );
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
