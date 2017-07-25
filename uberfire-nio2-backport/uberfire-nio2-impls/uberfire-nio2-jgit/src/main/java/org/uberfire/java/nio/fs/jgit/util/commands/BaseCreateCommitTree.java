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

import java.util.Optional;
import java.util.function.BiConsumer;

import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.CommitContent;

abstract class BaseCreateCommitTree<T extends CommitContent> {

    final T commitContent;
    final Git git;
    final ObjectId headId;
    final ObjectInserter odi;

    BaseCreateCommitTree(final Git git,
                         final ObjectId headId,
                         final ObjectInserter inserter,
                         final T commitContent) {
        this.git = git;
        this.headId = headId;
        this.odi = inserter;
        this.commitContent = commitContent;
    }

    Optional<ObjectId> buildTree(final DirCacheEditor editor) {
        try {
            return Optional.ofNullable(editor.getDirCache().writeTree(odi));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void iterateOverTreeWalk(final Git git,
                             final ObjectId headId,
                             final BiConsumer<String, CanonicalTreeParser> consumer) {
        if (headId != null) {
            try (final TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
                final int hIdx = treeWalk.addTree(new RevWalk(git.getRepository()).parseTree(headId));
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    final String walkPath = treeWalk.getPathString();
                    final CanonicalTreeParser hTree = treeWalk.getTree(hIdx,
                                                                       CanonicalTreeParser.class);

                    consumer.accept(walkPath,
                                    hTree);
                }
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    void addToTemporaryInCoreIndex(final DirCacheEditor editor,
                                   final DirCacheEntry dcEntry,
                                   final ObjectId objectId,
                                   final FileMode fileMode) {
        editor.add(new DirCacheEditor.PathEdit(dcEntry) {
            @Override
            public void apply(final DirCacheEntry ent) {
                ent.setObjectId(objectId);
                ent.setFileMode(fileMode);
            }
        });
    }
}
