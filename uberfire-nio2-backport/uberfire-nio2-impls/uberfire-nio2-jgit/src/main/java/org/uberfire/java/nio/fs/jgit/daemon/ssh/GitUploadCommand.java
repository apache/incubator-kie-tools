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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.zip.Deflater;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.RefFilter;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.transport.resolver.UploadPackFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.daemon.filters.HiddenBranchRefFilter;

public class GitUploadCommand extends BaseGitCommand {

    private UploadPackFactory<BaseGitCommand> uploadPackFactory;

    public GitUploadCommand(final String command,
                            final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                            final UploadPackFactory uploadPackFactory,
                            final ExecutorService executorService) {
        super(command,
              repositoryResolver,
              executorService);
        this.uploadPackFactory = uploadPackFactory;
    }

    @Override
    protected String getCommandName() {
        return "git-upload-pack";
    }

    @Override
    protected void execute(final Repository repository,
                           final InputStream in,
                           final OutputStream out,
                           final OutputStream err) {
        try {
            final UploadPack up = uploadPackFactory.create(this,
                                                           repository);

            final PackConfig config = new PackConfig(repository);
            config.setCompressionLevel(Deflater.BEST_COMPRESSION);
            up.setPackConfig(config);

            if (up.getRefFilter() == RefFilter.DEFAULT) {
                up.setRefFilter(new HiddenBranchRefFilter());
            }

            up.upload(in,
                      out,
                      err);
        } catch (final Exception ignored) {
        }
    }
}
