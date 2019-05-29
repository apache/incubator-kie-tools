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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

public class GitReceiveCommand extends BaseGitCommand {

    private final ReceivePackFactory<BaseGitCommand> receivePackFactory;

    public GitReceiveCommand(final String command,
                             final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                             final ReceivePackFactory<BaseGitCommand> receivePackFactory,
                             final ExecutorService executorService) {
        super(command,
              repositoryResolver,
              executorService);
        this.receivePackFactory = receivePackFactory;
    }

    @Override
    protected String getCommandName() {
        return "git-receive-pack";
    }

    @Override
    protected void execute(final Repository repository,
                           final InputStream in,
                           final OutputStream out,
                           final OutputStream err) {
        try {
            final ReceivePack rp = receivePackFactory.create(this, repository);
            rp.receive(in, out, err);
            rp.setPostReceiveHook((rp1, commands) -> {
                new Git(repository).gc();
            });
        } catch (final Exception ignored) {
        }
    }
}
