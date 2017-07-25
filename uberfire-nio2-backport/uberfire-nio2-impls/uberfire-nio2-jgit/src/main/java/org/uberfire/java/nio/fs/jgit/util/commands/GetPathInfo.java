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

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathType;

import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;

public class GetPathInfo {

    private final Git git;
    private final String branchName;
    private final String path;

    public GetPathInfo(final Git git,
                       final String branchName,
                       final String path) {
        this.git = git;
        this.branchName = branchName;
        this.path = path;
    }

    public PathInfo execute() throws IOException {

        final String gitPath = PathUtil.normalize(path);

        if (gitPath.isEmpty()) {
            return new PathInfo(null,
                                gitPath,
                                PathType.DIRECTORY);
        }

        final ObjectId tree = git.getTreeFromRef(branchName);
        if (tree == null) {
            return new PathInfo(null,
                                gitPath,
                                PathType.NOT_FOUND);
        }
        try (final TreeWalk tw = new TreeWalk(git.getRepository())) {
            tw.setFilter(PathFilter.create(gitPath));
            tw.reset(tree);
            while (tw.next()) {
                if (tw.getPathString().equals(gitPath)) {
                    if (tw.getFileMode(0).equals(FileMode.TYPE_TREE)) {
                        return new PathInfo(tw.getObjectId(0),
                                            gitPath,
                                            PathType.DIRECTORY);
                    } else if (tw.getFileMode(0).equals(FileMode.TYPE_FILE) ||
                            tw.getFileMode(0).equals(FileMode.EXECUTABLE_FILE) ||
                            tw.getFileMode(0).equals(FileMode.REGULAR_FILE)) {
                        final long size = tw.getObjectReader().getObjectSize(tw.getObjectId(0),
                                                                             OBJ_BLOB);
                        return new PathInfo(tw.getObjectId(0),
                                            gitPath,
                                            PathType.FILE,
                                            size);
                    }
                }
                if (tw.isSubtree()) {
                    tw.enterSubtree();
                }
            }
        }
        return new PathInfo(null,
                            gitPath,
                            PathType.NOT_FOUND);
    }
}
