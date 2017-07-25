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

import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;

public class ListCommits {

    private final GitImpl git;
    private final ObjectId startRange;
    private final ObjectId endRange;
    private final Ref ref;
    private final String path;

    public ListCommits(final GitImpl git,
                       final Ref ref,
                       final String path) {
        this.git = git;
        this.ref = ref;
        this.path = path;
        this.startRange = null;
        this.endRange = null;
    }

    public ListCommits(final GitImpl git,
                       final ObjectId startRange,
                       final ObjectId endRange) {
        this.git = git;
        this.startRange = startRange;
        this.endRange = endRange;
        this.ref = null;
        this.path = null;
    }

    public List<RevCommit> execute() throws IOException, GitAPIException {
        final List<RevCommit> list = new ArrayList<>();
        try (final RevWalk rw = buildWalk()) {
            if (ref == null) {
                rw.markStart(rw.parseCommit(endRange));
                if (startRange != null) {
                    rw.markUninteresting(rw.parseCommit(startRange));
                }
            }
            for (RevCommit rev : rw) {
                list.add(rev);
            }
            return list;
        }
    }

    private RevWalk buildWalk() throws GitAPIException, IncorrectObjectTypeException, MissingObjectException {
        if (ref != null) {
            final LogCommand logCommand = git._log().add(ref.getObjectId());
            if (path != null && !path.isEmpty()) {
                logCommand.addPath(path);
            }
            return (RevWalk) logCommand.call();
        }

        return new RevWalk(git.getRepository());
    }
}
