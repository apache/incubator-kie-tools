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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.MoveCommitContent;

public class CreateMoveCommitTree extends BaseCreateCommitTree<MoveCommitContent> {

    public CreateMoveCommitTree(final Git git,
                                final ObjectId headId,
                                final ObjectInserter inserter,
                                final MoveCommitContent commitContent) {
        super(git,
              headId,
              inserter,
              commitContent);
    }

    public Optional<ObjectId> execute() {
        final Map<String, String> content = commitContent.getContent();
        final DirCacheEditor editor = DirCache.newInCore().editor();
        final List<String> pathsAdded = new ArrayList<>();

        try {
            iterateOverTreeWalk(git,
                                headId,
                                (walkPath, hTree) -> {
                                    final String toPath = content.get(walkPath);
                                    final DirCacheEntry dcEntry = new DirCacheEntry((toPath == null) ? walkPath : toPath);
                                    if (!pathsAdded.contains(dcEntry.getPathString())) {
                                        addToTemporaryInCoreIndex(editor,
                                                                  dcEntry,
                                                                  hTree.getEntryObjectId(),
                                                                  hTree.getEntryFileMode());
                                        pathsAdded.add(dcEntry.getPathString());
                                    }
                                });
            editor.finish();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return buildTree(editor);
    }
}
