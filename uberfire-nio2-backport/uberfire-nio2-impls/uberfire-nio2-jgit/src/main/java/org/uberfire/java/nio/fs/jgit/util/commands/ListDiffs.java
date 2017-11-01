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

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static java.util.Collections.emptyList;

public class ListDiffs {

    private final Git git;
    private final ObjectId oldRef;
    private final ObjectId newRef;

    public ListDiffs(final Git git,
                     final ObjectId oldRef,
                     final ObjectId newRef) {
        this.git = git;
        this.oldRef = oldRef;
        this.newRef = newRef;
    }

    public List<DiffEntry> execute() {
        if (newRef == null || git.getRepository() == null) {
            return emptyList();
        }

        try (final ObjectReader reader = git.getRepository().newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            if (oldRef != null) {
                oldTreeIter.reset(reader,
                                  oldRef);
            }
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader,
                              newRef);
            return new CustomDiffCommand(git).setNewTree(newTreeIter).setOldTree(oldTreeIter).setShowNameAndStatusOnly(true).call();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
