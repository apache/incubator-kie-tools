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

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class JGitGetCommitTest extends AbstractTestInfra {

    private Git git;

    private static final String MASTER_BRANCH = "master";

    @Before
    public void setup() throws IOException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder, "source/source.git");

        git = new CreateRepository(gitSource).execute().get();
    }

    @Test
    public void successTest() throws IOException {
        commit(git, MASTER_BRANCH, "Adding file", content("file.txt", "file content"));

        RevCommit lastCommit = git.getLastCommit(MASTER_BRANCH);

        RevCommit commit = git.getCommit(lastCommit.getName());

        assertThat(commit.getName()).isEqualTo(lastCommit.getName());
    }

    @Test
    public void notFoundTest() {
        RevCommit commit = git.getCommit("non-existent-commit-id");

        assertThat(commit).isNull();
    }
}