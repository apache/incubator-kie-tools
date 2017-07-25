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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;

public class ListPathContent {

    private final Git git;
    private final String branchName;
    private final String path;

    public ListPathContent(final Git git,
                           final String branchName,
                           final String path) {
        this.git = git;
        this.branchName = branchName;
        this.path = path;
    }

    public List<PathInfo> execute() throws IOException {

        final String gitPath = PathUtil.normalize(path);
        final List<PathInfo> result = new ArrayList<>();
        final ObjectId tree = git.getTreeFromRef(branchName);
        if (tree == null) {
            return result;
        }
        try (final TreeWalk tw = new TreeWalk(git.getRepository())) {
            boolean found = false;
            if (gitPath.isEmpty()) {
                found = true;
            } else {
                tw.setFilter(PathFilter.create(gitPath));
            }
            tw.reset(tree);
            while (tw.next()) {
                if (!found && tw.isSubtree()) {
                    tw.enterSubtree();
                }
                if (tw.getPathString().equals(gitPath)) {
                    found = true;
                    continue;
                }
                if (found) {
                    result.add(new PathInfo(tw.getObjectId(0),
                                            tw.getPathString(),
                                            tw.getFileMode(0)));
                }
            }
            return result;
        }
    }
}
