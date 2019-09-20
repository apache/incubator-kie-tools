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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateBranch;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.assertj.core.api.Assertions.assertThat;

public class JGitConflictBranchesCheckerTest extends AbstractTestInfra {

    private Git git;

    private static final String MASTER_BRANCH = "master";
    private static final String DEVELOP_BRANCH = "develop";

    private static final List<String> TXT_FILES =
            Stream.of("file0", "file1", "file2", "file3", "file4")
                    .collect(Collectors.toList());

    private static final String[] COMMON_TXT_LINES = {"Line1", "Line2", "Line3", "Line4"};

    @Before
    public void setup() throws IOException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder, "source/source.git");

        git = new CreateRepository(gitSource).execute().get();

        commit(git, MASTER_BRANCH, "Adding files into master",
               content(TXT_FILES.get(0), multiline(TXT_FILES.get(0), COMMON_TXT_LINES)),
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), COMMON_TXT_LINES)),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), COMMON_TXT_LINES)));

        new CreateBranch((GitImpl) git, MASTER_BRANCH, DEVELOP_BRANCH).execute();
    }

    @Test
    public void testReportConflictsAllFiles() throws IOException {
        commit(git, DEVELOP_BRANCH, "Updating files",
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), "Line1", "Line2ChangedDev", "Line3", "Line4")),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), "Line1", "Line2ChangedDev", "Line3", "Line4")));

        commit(git, MASTER_BRANCH, "Updating files",
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), "Line1", "Line2ChangedMaster", "Line3", "Line4")),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), "Line1", "Line2ChangedMaster", "Line3", "Line4")));

        List<String> conflicts = git.conflictBranchesChecker(MASTER_BRANCH, DEVELOP_BRANCH);

        assertThat(conflicts).isNotEmpty();
        assertThat(conflicts).hasSize(2);
        assertThat(conflicts.get(0)).isEqualTo(TXT_FILES.get(1));
        assertThat(conflicts.get(1)).isEqualTo(TXT_FILES.get(2));
    }

    @Test
    public void testReportConflictsSomeFiles() throws IOException {
        commit(git, DEVELOP_BRANCH, "Updating files",
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), "Line1", "Line2ChangedDev", "Line3", "Line4")),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), "Line1", "Line2ChangedDev", "Line3", "Line4")));

        commit(git, MASTER_BRANCH, "Updating files",
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), "Line1", "Line2ChangedMaster", "Line3", "Line4")),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), "Line1", "Line2", "Line3", "Line4ChangedMaster")));

        List<String> conflicts = git.conflictBranchesChecker(MASTER_BRANCH, DEVELOP_BRANCH);

        assertThat(conflicts).isNotEmpty();
        assertThat(conflicts).hasSize(1);
        assertThat(conflicts.get(0)).isEqualTo(TXT_FILES.get(1));
    }

    @Test
    public void testReportConflictsNoFiles() throws IOException {
        commit(git, DEVELOP_BRANCH, "Updating files",
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), "Line1", "Line2ChangedDev", "Line3", "Line4")),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), "Line1", "Line2ChangedDev", "Line3", "Line4")));

        commit(git, MASTER_BRANCH, "Updating files",
               content(TXT_FILES.get(1), multiline(TXT_FILES.get(1), "Line1", "Line2", "Line3", "Line4ChangedMaster")),
               content(TXT_FILES.get(2), multiline(TXT_FILES.get(2), "Line1", "Line2", "Line3", "Line4ChangedMaster")));

        List<String> conflicts = git.conflictBranchesChecker(MASTER_BRANCH, DEVELOP_BRANCH);

        assertThat(conflicts).isEmpty();
    }

    @Test(expected = GitException.class)
    public void testInvalidBranch() {
        git.conflictBranchesChecker(MASTER_BRANCH, "invalid-branch");
    }
}
