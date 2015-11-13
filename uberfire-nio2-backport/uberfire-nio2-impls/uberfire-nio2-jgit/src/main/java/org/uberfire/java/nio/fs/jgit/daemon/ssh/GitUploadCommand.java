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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.UploadPack;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
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
                            final OutputStream err,
                            final JGitFileSystem fileSystem ) {
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
