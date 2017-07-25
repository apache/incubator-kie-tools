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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class BlobAsInputStream {

    private static final Logger LOG = LoggerFactory.getLogger(BlobAsInputStream.class);

    private final Git git;
    private final String treeRef;
    private final String path;

    public BlobAsInputStream(final Git git,
                             final String treeRef,
                             final String path) {
        this.git = git;
        this.treeRef = treeRef;
        this.path = path;
    }

    public Optional<InputStream> execute() {
        try (final TreeWalk tw = new TreeWalk(git.getRepository())) {
            final ObjectId tree = git.getTreeFromRef(treeRef);
            tw.setFilter(PathFilter.create(path));
            tw.reset(tree);
            while (tw.next()) {
                if (tw.isSubtree() && !path.equals(tw.getPathString())) {
                    tw.enterSubtree();
                    continue;
                }
                return Optional.of(new ByteArrayInputStream(git.getRepository().open(tw.getObjectId(0),
                                                                                     Constants.OBJ_BLOB).getBytes()));
            }
        } catch (final Throwable t) {
            LOG.debug("Unexpected exception, this will trigger a NoSuchFileException.",
                      t);
            throw new NoSuchFileException("Can't find '" + path + "' in tree '" + treeRef + "'");
        }
        throw new NoSuchFileException("Can't find '" + path + "' in tree '" + treeRef + "'");
    }
}
