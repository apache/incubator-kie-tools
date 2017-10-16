/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.soup.commons.validation.Preconditions.checkInstanceOf;

public class Push {

    private final GitImpl git;
    private final CredentialsProvider credentialsProvider;
    private final Pair<String, String> remote;
    private final boolean force;
    private final Collection<RefSpec> refSpecs;

    public Push(final Git git,
                final CredentialsProvider credentialsProvider,
                final Pair<String, String> remote,
                final boolean force,
                final Collection<RefSpec> refSpecs) {
        this.git = checkInstanceOf("git",
                                   git,
                                   GitImpl.class);
        this.credentialsProvider = credentialsProvider;
        this.remote = checkNotNull("remote",
                                   remote);
        this.force = force;
        this.refSpecs = refSpecs;
    }

    public void execute() throws InvalidRemoteException {
        try {
            final List<RefSpec> specs = new UpdateRemoteConfig(git,
                                                               remote,
                                                               refSpecs).execute();
            git._push()
                    .setCredentialsProvider(credentialsProvider)
                    .setRefSpecs(specs)
                    .setRemote(remote.getK1())
                    .setForce(force)
                    .setPushAll()
                    .call();
        } catch (final InvalidRemoteException e) {
            throw e;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
