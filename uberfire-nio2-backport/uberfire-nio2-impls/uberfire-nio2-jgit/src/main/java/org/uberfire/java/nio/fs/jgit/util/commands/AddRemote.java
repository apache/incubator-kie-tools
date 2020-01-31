/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.util.commands;

import org.eclipse.jgit.lib.StoredConfig;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

public class AddRemote {

    private static final String REMOTE_KEY = "remote";
    private static final String URL_KEY = "url";

    private Git git;
    private String remote;
    private String url;

    public AddRemote(final Git git,
                     final String remote,
                     final String url) {
        this.git = git;
        this.remote = remote;
        this.url = url;
    }

    public void execute() {
        try {
            final StoredConfig config = git.getRepository().getConfig();
            config.setString(REMOTE_KEY,
                             remote,
                             URL_KEY,
                             url);
            config.save();
        } catch (Exception e) {
            throw new GitException("Error when trying to add remote",
                                   e);
        }
    }
}