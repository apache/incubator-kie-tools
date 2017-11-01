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

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class GetTreeFromRef {

    private final Git git;
    private final String treeRefName;

    public GetTreeFromRef(final Git git,
                          final String treeRefName) {
        this.git = git;
        this.treeRefName = treeRefName;
    }

    public ObjectId execute() {
        final RevCommit commit = git.getLastCommit(treeRefName);
        if (commit == null) {
            return null;
        }
        return commit.getTree().getId();
    }
}
