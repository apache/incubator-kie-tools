/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.common.Session;
import org.apache.sshd.common.channel.ChannelOutputStream;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.security.FileSystemResourceAdaptor;
import org.uberfire.security.authz.AuthorizationManager;

public abstract class BaseGitCommand implements Command,
                                                SessionAware,
                                                Runnable {

    public final static Session.AttributeKey<User> SUBJECT_KEY = new Session.AttributeKey<User>();

    protected final String command;
    protected final String repositoryName;
    protected final AuthorizationManager authorizationManager;
    protected final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver;

    private InputStream in;
    private OutputStream out;
    private OutputStream err;
    private ExitCallback callback;
    private User user;

    public BaseGitCommand( final String command,
                           final AuthorizationManager authorizationManager,
                           final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver ) {
        this.command = command;
        this.authorizationManager = authorizationManager;
        this.repositoryName = buildRepositoryName( command );
        this.repositoryResolver = repositoryResolver;
    }

    private String buildRepositoryName( String command ) {
        int start = getCommandName().length() + 2;
        final String temp = command.substring( start );
        return temp.substring( 0, temp.indexOf( "'" ) );
    }

    protected abstract String getCommandName();

    public void setInputStream( InputStream in ) {
        this.in = in;
    }

    public void setOutputStream( OutputStream out ) {
        this.out = out;
        if ( out instanceof ChannelOutputStream ) {
            ( (ChannelOutputStream) out ).setNoDelay( true );
        }
    }

    public void setErrorStream( OutputStream err ) {
        this.err = err;
        if ( err instanceof ChannelOutputStream ) {
            ( (ChannelOutputStream) err ).setNoDelay( true );
        }
    }

    public void setExitCallback( ExitCallback callback ) {
        this.callback = callback;
    }

    public void start( final Environment env ) throws IOException {
        new Thread( this ).start();
    }

    public void run() {
        try {
            final Repository repository = openRepository( repositoryName );
            if ( repository != null ) {
                final FileSystem fileSystem = repositoryResolver.resolveFileSystem( repository );
                if ( authorizationManager.authorize( new FileSystemResourceAdaptor( fileSystem ), user ) ) {
                    execute( user, repository, in, out, err );
                } else {
                    err.write( "Invalid credentials.".getBytes() );
                }
            } else {
                err.write( "Can't resolve repository name.".getBytes() );
            }
        } catch ( final Throwable t ) {
        }
        if ( callback != null ) {
            callback.onExit( 0 );
        }
    }

    private Repository openRepository( String name )
            throws ServiceMayNotContinueException {
        // Assume any attempt to use \ was by a Windows client
        // and correct to the more typical / used in Git URIs.
        //
        name = name.replace( '\\', '/' );

        // git://thishost/path should always be name="/path" here
        //
        if ( !name.startsWith( "/" ) ) {
            return null;
        }

        try {
            return repositoryResolver.open( this, name.substring( 1 ) );
        } catch ( RepositoryNotFoundException e ) {
            // null signals it "wasn't found", which is all that is suitable
            // for the remote client to know.
            return null;
        } catch ( ServiceNotAuthorizedException e ) {
            // null signals it "wasn't found", which is all that is suitable
            // for the remote client to know.
            return null;
        } catch ( ServiceNotEnabledException e ) {
            // null signals it "wasn't found", which is all that is suitable
            // for the remote client to know.
            return null;
        }
    }

    protected abstract void execute( final User user,
                                     final Repository repository,
                                     final InputStream in,
                                     final OutputStream out,
                                     final OutputStream err );

    public void destroy() {
    }

    public User getUser() {
        return user;
    }

    @Override
    public void setSession( final ServerSession session ) {
        this.user = session.getAttribute( BaseGitCommand.SUBJECT_KEY );
    }
}
