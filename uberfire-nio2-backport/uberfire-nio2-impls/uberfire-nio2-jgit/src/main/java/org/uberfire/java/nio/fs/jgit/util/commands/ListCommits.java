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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RenameCallback;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.TreeRevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;
import org.uberfire.java.nio.fs.jgit.util.model.CommitHistory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class ListCommits {

    private final Git git;
    private final ObjectId startRange;
    private final ObjectId endRange;
    private final String path;

    public ListCommits(final Git git,
                       final Ref ref,
                       final String path) {
        this.git = git;
        this.path = makeRelative(path);
        this.startRange = null;
        this.endRange = ref.getObjectId();
    }

    private static String makeRelative(String path) {
        return (path != null && path.startsWith("/")) ? path.substring(1) : path;
    }

    public ListCommits(final GitImpl git,
                       final ObjectId startRange,
                       final ObjectId endRange) {
        this.git = git;
        this.startRange = startRange;
        this.endRange = endRange;
        this.path = null;
    }

    public CommitHistory execute() throws IOException, GitAPIException {
        try (final RevWalk rw = buildWalk()) {
            if (path == null || path.isEmpty()) {
                return fullCommitHistory(rw);
            } else {
                return pathCommitHistory(rw);
            }
        }
    }

    private CommitHistory pathCommitHistory(final RevWalk rw) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        final Map<AnyObjectId, String> pathByCommit = new HashMap<>();
        final List<RevCommit> commits = new ArrayList<>();
        final RenameCaptor renameCaptor = new RenameCaptor();
        /*
         * We have to go through all commits and filter ourselves so that we can use the
         * rename callback to map commits to path renames.
         */
        final TreeRevFilter revFilter = createTreeRevFilter(rw, path, renameCaptor);
        String curPath = path;
        for (final RevCommit commit : rw) {
            if (revFilter.include(rw, commit)) {
                @SuppressWarnings("resource")
                final TreeWalk tw = new TreeWalk(rw.getObjectReader());
                tw.setRecursive(true);
                tw.setFilter(PathFilter.create(curPath));
                tw.addTree(commit.getTree());
                // Checks for special case that path wasn't deleted in this commit
                if (tw.next()) {
                    commits.add(commit);
                    // There is a rename to track
                    pathByCommit.put(commit.getId(), curPath);
                    if (renameCaptor.hasCaptured()) {
                        curPath = renameCaptor.getAndReset().getOldPath();
                    }
                }
            }
        }

        return new CommitHistory(commits, pathByCommit, path);
    }

    private CommitHistory fullCommitHistory(final RevWalk rw) {
        final List<RevCommit> commits = stream(rw.spliterator(), false).collect(toList());
        return new CommitHistory(commits, Collections.emptyMap(), null);
    }

    private TreeRevFilter createTreeRevFilter(final RevWalk rw, String curPath, final RenameCallback renameCallback) {
        final FollowFilter followFilter = FollowFilter.create(curPath, git.getRepository().getConfig().get(DiffConfig.KEY));
        followFilter.setRenameCallback(renameCallback);
        final TreeRevFilter revFilter = new TreeRevFilter(rw, followFilter);
        return revFilter;
    }

    private RevWalk buildWalk() throws GitAPIException, IOException {
        final RevWalk rw = new RevWalk(git.getRepository());
        rw.setTreeFilter(TreeFilter.ANY_DIFF);
        rw.markStart(rw.parseCommit(endRange));
        rw.sort(RevSort.TOPO);
        if (startRange != null) {
            rw.markUninteresting(rw.parseCommit(startRange));
        }

        return rw;
    }

    private static class RenameCaptor extends RenameCallback {

        private DiffEntry captured;

        @Override
        public void renamed(final DiffEntry entry) {
            captured = entry;
        }

        public boolean hasCaptured() {
            return captured != null;
        }

        public DiffEntry getAndReset() {
            if (captured == null) {
                throw new NullPointerException("Cannot get DiffEntry when none was captured.");
            }

            final DiffEntry retVal = captured;
            captured = null;

            return retVal;
        }
    }
}
