/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class MapDiffContent {

    private final Git git;
    private final String branch;
    private final String startCommitId;
    private final String endCommitId;

    public MapDiffContent(final Git git,
                          final String branch,
                          final String startCommitId,
                          final String endCommitId) {
        this.git = checkNotNull("git",
                                git);
        this.branch = checkNotEmpty("branch",
                                    branch);
        this.startCommitId = checkNotEmpty("startCommitId",
                                           startCommitId);
        this.endCommitId = checkNotEmpty("endCommitId",
                                         endCommitId);
    }

    public Map<String, File> execute() {
        BranchUtil.existsBranch(git,
                                branch);

        final RevCommit startCommit = git.getCommit(startCommitId);
        final RevCommit endCommit = git.getCommit(endCommitId);

        if (startCommit == null || endCommit == null) {
            throw new GitException("Given commit ids cannot be found.");
        }

        Map<String, File> content = new HashMap<>();

        final List<DiffEntry> diffs = git.listDiffs(startCommit.getTree(),
                                                    endCommit.getTree());

        diffs.forEach(entry -> {
            if (entry.getChangeType() != DiffEntry.ChangeType.DELETE) {
                try (final InputStream inputStream = git.blobAsInputStream(branch,
                                                                           entry.getNewPath())) {
                    final File file = File.createTempFile("gitz",
                                                          "woot");

                    Files.copy(inputStream,
                               file.toPath(),
                               StandardCopyOption.REPLACE_EXISTING);

                    content.put(entry.getNewPath(),
                                file);
                } catch (IOException e) {
                    throw new GitException("Unable to get content from diffs", e);
                }
            } else {
                content.put(entry.getOldPath(),
                            null);
            }
        });

        return content;
    }
}
