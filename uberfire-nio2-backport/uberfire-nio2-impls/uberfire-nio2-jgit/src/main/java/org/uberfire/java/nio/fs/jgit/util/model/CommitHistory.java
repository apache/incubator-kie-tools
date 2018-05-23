/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.java.nio.fs.jgit.util.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitHistory {

    private final List<RevCommit> commits;
    private final Map<AnyObjectId, String> pathsByCommit;
    private final String trackedPath;

    public CommitHistory(final List<RevCommit> commits,
                         final Map<AnyObjectId, String> pathsByCommit,
                         final String trackedPath) {
        this.commits = commits;
        this.pathsByCommit = pathsByCommit;
        this.trackedPath = trackedPath;
    }

    public List<RevCommit> getCommits() {
        return commits;
    }

    /**
     * @return The initial file path that was followed, or else the root path (/) if none was given.
     */
    public String getTrackedFilePath() {
        return (trackedPath == null) ? "/" : trackedPath;
    }

    public String trackedFileNameChangeFor(final AnyObjectId commitId) {
        return Optional.ofNullable(pathsByCommit.get(commitId))
                       .map(path -> "/" + path)
                       .orElseGet(() -> getTrackedFilePath());
    }

}
