/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;

public class GitReceiveCommand extends BaseGitCommand {

    private final ReceivePackFactory<BaseGitCommand> receivePackFactory;

    public GitReceiveCommand( final String command,
                              final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                              final FileSystemAuthorizer fileSystemAuthorizer,
                              final ReceivePackFactory<BaseGitCommand> receivePackFactory ) {
        super( command, fileSystemAuthorizer, repositoryResolver );
        this.receivePackFactory = receivePackFactory;
    }

    @Override
    protected String getCommandName() {
        return "git-receive-pack";
    }

    @Override
    protected void execute( final FileSystemUser user,
                            final Repository repository,
                            final InputStream in,
                            final OutputStream out,
                            final OutputStream err,
                            final JGitFileSystem fileSystem ) {
        try {
            final ReceivePack rp = receivePackFactory.create( this, repository );
            rp.receive( in, out, err );
            JGitUtil.gc( new Git( repository ) );
            fileSystem.resetCommitCount();
        } catch ( Exception ex ) {
        }
    }
}
