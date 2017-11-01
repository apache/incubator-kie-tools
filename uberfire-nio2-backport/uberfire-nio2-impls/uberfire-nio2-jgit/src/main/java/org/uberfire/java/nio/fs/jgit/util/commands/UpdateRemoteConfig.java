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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class UpdateRemoteConfig {

    private final Git git;
    private final Pair<String, String> remote;
    private final Collection<RefSpec> refSpecs;

    public UpdateRemoteConfig(final Git git,
                              final Pair<String, String> remote,
                              final Collection<RefSpec> refSpecs) {
        this.git = git;
        this.remote = remote;
        this.refSpecs = refSpecs;
    }

    public List<RefSpec> execute() throws IOException, URISyntaxException {
        final List<RefSpec> specs = new ArrayList<>();
        if (refSpecs == null || refSpecs.isEmpty()) {
            specs.add(new RefSpec("+refs/heads/*:refs/remotes/" + remote.getK1() + "/*"));
            specs.add(new RefSpec("+refs/tags/*:refs/tags/*"));
            specs.add(new RefSpec("+refs/notes/*:refs/notes/*"));
        } else {
            specs.addAll(refSpecs);
        }

        final StoredConfig config = git.getRepository().getConfig();
        final String url = config.getString("remote",
                                            remote.getK1(),
                                            "url");
        if (url == null) {
            final RemoteConfig remoteConfig = new RemoteConfig(git.getRepository().getConfig(),
                                                               remote.getK1());
            remoteConfig.addURI(new URIish(remote.getK2()));
            specs.forEach(remoteConfig::addFetchRefSpec);
            remoteConfig.update(git.getRepository().getConfig());
            git.getRepository().getConfig().save();
        }
        return specs;
    }
}
