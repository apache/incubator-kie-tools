/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

import static org.eclipse.jgit.api.ListBranchCommand.ListMode.*;
import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitUtilTest extends AbstractTestInfra {

    @Test
    public void testNewRepo() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder, "mytest.git");

        final Git git = JGitUtil.newRepository(gitFolder);

        assertThat(git).isNotNull();

        assertThat(branchList(git).size()).isEqualTo(0);

        commit(git, "master", "file.txt", tempFile("temp"), "name", "name@example.com", "commit", null, null);

        assertThat(branchList(git).size()).isEqualTo(1);
    }

    @Test
    public void testClone() throws IOException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder, "mytest.git");

        final Git origin = JGitUtil.newRepository(gitFolder);

        commit(origin, "user_branch", "file2.txt", tempFile("temp2222"), "name", "name@example.com", "commit!", null, null);
        commit(origin, "master", "file.txt", tempFile("temp"), "name", "name@example.com", "commit", null, null);
        commit(origin, "master", "file3.txt", tempFile("temp3"), "name", "name@example.com", "commit", null, null);

        final File gitClonedFolder = new File(parentFolder, "myclone.git");

        final Git git = cloneRepository(gitClonedFolder, origin.getRepository().getDirectory().toString(), CredentialsProvider.getDefault());

        assertThat(git).isNotNull();

        assertThat(branchList(git, ALL)).hasSize(4);

        assertThat(branchList(git, ALL).get(0).getName()).isEqualTo("refs/heads/master");
        assertThat(branchList(git, ALL).get(1).getName()).isEqualTo("refs/heads/user_branch");
        assertThat(branchList(git, ALL).get(2).getName()).isEqualTo("refs/remotes/origin/master");
        assertThat(branchList(git, ALL).get(3).getName()).isEqualTo("refs/remotes/origin/user_branch");
    }

    @Test
    public void testPathResolve() throws IOException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder, "mytest.git");

        final Git origin = JGitUtil.newRepository(gitFolder);

        commit(origin, "user_branch", "path/to/file2.txt", tempFile("temp2222"), "name", "name@example.com", "commit!", null, null);
        commit(origin, "user_branch", "path/to/file3.txt", tempFile("temp2222"), "name", "name@example.com", "commit!", null, null);

        final File gitClonedFolder = new File(parentFolder, "myclone.git");

        final Git git = cloneRepository(gitClonedFolder, origin.getRepository().getDirectory().toString(), CredentialsProvider.getDefault());

        assertThat(JGitUtil.checkPath(git, "user_branch", "pathx/").getK1()).isEqualTo(NOT_FOUND);
        assertThat(JGitUtil.checkPath(git, "user_branch", "path/to/file2.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(git, "user_branch", "path/to").getK1()).isEqualTo(DIRECTORY);
    }

}
