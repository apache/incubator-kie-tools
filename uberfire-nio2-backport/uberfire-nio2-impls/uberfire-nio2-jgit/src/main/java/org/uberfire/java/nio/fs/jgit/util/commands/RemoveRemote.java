/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.java.nio.fs.jgit.util.commands;

import org.eclipse.jgit.lib.RefUpdate;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

public class RemoveRemote {

    final PathUtil pathUtil = new PathUtil();
    private Git git;
    private String ref;
    private String remote;

    public RemoveRemote(final Git git,
                        final String remote,
                        final String ref) {

        this.git = git;
        this.ref = ref;
        this.remote = remote;
    }

    public void execute() {
        try {
            // AF-1715: Cleaning origin to prevent errors while importing the new generated repo.
            git.getRepository().getConfig().unsetSection("remote",
                                                         remote);
            git.getRepository().getConfig().save();
            RefUpdate updateRef = git.getRepository().updateRef(ref,
                                                                false);
            updateRef.setRefLogMessage(ref + " packed-ref deleted",
                                       false);
            updateRef.setForceUpdate(true);
            updateRef.delete();
        } catch (Exception e) {
            throw new GitException("Error when trying to remove remote",
                                   e);
        }
    }
}
