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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.DefaultCommitContent;

import static org.eclipse.jgit.lib.FileMode.REGULAR_FILE;

public class CreateDefaultCommitTree extends BaseCreateCommitTree<DefaultCommitContent> {

    public CreateDefaultCommitTree(final Git git,
                                   final ObjectId headId,
                                   final ObjectInserter inserter,
                                   final DefaultCommitContent commitContent) {
        super(git,
              headId,
              inserter,
              commitContent);
    }

    public Optional<ObjectId> execute() {
        final Map<String, File> content = commitContent.getContent();
        final Map<String, Pair<File, ObjectId>> paths = new HashMap<>(content.size());
        final Set<String> path2delete = new HashSet<>();

        final DirCacheEditor editor = DirCache.newInCore().editor();

        try {
            for (final Map.Entry<String, File> pathAndContent : content.entrySet()) {
                final String gPath = PathUtil.normalize(pathAndContent.getKey());
                if (pathAndContent.getValue() == null) {
                    path2delete.addAll(searchPathsToDelete(git,
                                                           headId,
                                                           gPath));
                } else {
                    paths.putAll(storePathsIntoHashMap(odi,
                                                       pathAndContent,
                                                       gPath));
                }
            }

            iterateOverTreeWalk(git,
                                headId,
                                (walkPath, hTree) -> {
                                    if (paths.containsKey(walkPath) && paths.get(walkPath).getK2().equals(hTree.getEntryObjectId())) {
                                        paths.remove(walkPath);
                                    }

                                    if (paths.get(walkPath) == null && !path2delete.contains(walkPath)) {
                                        addToTemporaryInCoreIndex(editor,
                                                                  new DirCacheEntry(walkPath),
                                                                  hTree.getEntryObjectId(),
                                                                  hTree.getEntryFileMode());
                                    }
                                });

            paths.forEach((key, value) -> {
                if (value.getK1() != null) {
                    editor.add(new DirCacheEditor.PathEdit(new DirCacheEntry(key)) {
                        @Override
                        public void apply(final DirCacheEntry ent) {
                            ent.setLength(value.getK1().length());
                            ent.setLastModified(value.getK1().lastModified());
                            ent.setFileMode(REGULAR_FILE);
                            ent.setObjectId(value.getK2());
                        }
                    });
                }
            });

            editor.finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (path2delete.isEmpty() && paths.isEmpty()) {
            editor.getDirCache().clear();
            return Optional.empty();
        }

        return buildTree(editor);
    }

    private static Map<String, Pair<File, ObjectId>> storePathsIntoHashMap(final ObjectInserter inserter,
                                                                           final Map.Entry<String, File> pathAndContent,
                                                                           final String gPath) {
        try (final InputStream inputStream = new FileInputStream(pathAndContent.getValue())) {
            final Map<String, Pair<File, ObjectId>> paths = new HashMap<>();
            final ObjectId objectId = inserter.insert(Constants.OBJ_BLOB,
                                                      pathAndContent.getValue().length(),
                                                      inputStream);
            paths.put(gPath,
                      Pair.newPair(pathAndContent.getValue(),
                                   objectId));
            return paths;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Set<String> searchPathsToDelete(final Git git,
                                                   final ObjectId headId,
                                                   final String gPath) throws java.io.IOException {
        try (final TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
            final Set<String> path2delete = new HashSet<>();
            treeWalk.addTree(new RevWalk(git.getRepository()).parseTree(headId));
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(gPath));

            while (treeWalk.next()) {
                path2delete.add(treeWalk.getPathString());
            }
            return path2delete;
        }
    }
}
