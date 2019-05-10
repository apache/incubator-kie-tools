/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.uberfire.java.nio.fs.jgit.util.model.PathType.DIRECTORY;
import static org.uberfire.java.nio.fs.jgit.util.model.PathType.FILE;
import static org.uberfire.java.nio.fs.jgit.util.model.PathType.NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Test;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.commands.Clone;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;
import org.uberfire.java.nio.fs.jgit.util.commands.GetTreeFromRef;
import org.uberfire.java.nio.fs.jgit.util.commands.ListDiffs;
import org.uberfire.java.nio.fs.jgit.util.commands.ListRefs;

public class JGitUtilTest extends AbstractTestInfra {

    @Test
    public void testNewRepo() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        assertThat(git).isNotNull();

        assertThat(new ListRefs(git.getRepository()).execute().size()).isEqualTo(0);

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "commit",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file.txt",
                           tempFile("temp"));
                   }}).execute();

        assertThat(new ListRefs(git.getRepository()).execute().size()).isEqualTo(1);
    }

    @Test
    public void testClone() throws IOException, InvalidRemoteException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git origin = new CreateRepository(gitFolder).execute().get();

        new Commit(origin,
                   "user_branch",
                   "name",
                   "name@example.com",
                   "commit!",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file2.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(origin,
                   "master",
                   "name",
                   "name@example.com",
                   "commit",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file.txt",
                           tempFile("temp"));
                   }}).execute();
        new Commit(origin,
                   "master",
                   "name",
                   "name@example.com",
                   "commit",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("file3.txt",
                           tempFile("temp3"));
                   }}).execute();

        final File gitClonedFolder = new File(parentFolder,
                                              "myclone.git");

        final Git git = new Clone(gitClonedFolder,
                                  origin.getRepository().getDirectory().toString(),
                                  false,
                                  null,
                                  CredentialsProvider.getDefault(),
                                  null,
                                  null).execute().get();

        assertThat(git).isNotNull();

        assertThat(new ListRefs(git.getRepository()).execute()).hasSize(2);

        assertThat(new ListRefs(git.getRepository()).execute().get(0).getName()).isEqualTo("refs/heads/master");
        assertThat(new ListRefs(git.getRepository()).execute().get(1).getName()).isEqualTo("refs/heads/user_branch");
    }

    @Test
    public void testPathResolve() throws IOException, InvalidRemoteException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git origin = new CreateRepository(gitFolder).execute().get();

        new Commit(origin,
                   "user_branch",
                   "name",
                   "name@example.com",
                   "commit!",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(origin,
                   "user_branch",
                   "name",
                   "name@example.com",
                   "commit!",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file3.txt",
                           tempFile("temp2222"));
                   }}).execute();

        final File gitClonedFolder = new File(parentFolder,
                                              "myclone.git");

        final Git git = new Clone(gitClonedFolder,
                                  origin.getRepository().getDirectory().toString(),
                                  false,
                                  null,
                                  CredentialsProvider.getDefault(),
                                  null,
                                  null).execute().get();

        assertThat(git.getPathInfo("user_branch",
                                   "pathx/").getPathType()).isEqualTo(NOT_FOUND);
        assertThat(git.getPathInfo("user_branch",
                                   "path/to/file2.txt").getPathType()).isEqualTo(FILE);
        assertThat(git.getPathInfo("user_branch",
                                   "path/to").getPathType()).isEqualTo(DIRECTORY);
    }

    @Test
    public void testAmend() throws IOException, InvalidRemoteException {
        final File parentFolder = createTempDirectory();
        System.out.println("COOL!:" + parentFolder.toString());
        final File gitFolder = new File(parentFolder,
                                        "myxxxtest.git");

        final Git origin = new CreateRepository(gitFolder).execute().get();

        new Commit(origin,
                   "master",
                   "name",
                   "name@example.com",
                   "commit!",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("tempwdf sdf asdf asd2222"));
                   }}).execute();
        new Commit(origin,
                   "master",
                   "name",
                   "name@example.com",
                   "commit!",
                   null,
                   null,
                   true,
                   new HashMap<String, File>() {{
                       put("path/to/file3.txt",
                           tempFile("temp2x d dasdf asdf 222"));
                   }}).execute();

        final File gitClonedFolder = new File(parentFolder,
                                              "myclone.git");

        final Git git = new Clone(gitClonedFolder,
                                  origin.getRepository().getDirectory().toString(),
                                  false,
                                  null,
                                  CredentialsProvider.getDefault(),
                                  null,
                                  null).execute().get();

        assertThat(git.getPathInfo("master",
                                   "pathx/").getPathType()).isEqualTo(NOT_FOUND);
        assertThat(git.getPathInfo("master",
                                   "path/to/file2.txt").getPathType()).isEqualTo(FILE);
        assertThat(git.getPathInfo("master",
                                   "path/to").getPathType()).isEqualTo(DIRECTORY);
    }

    @Test
    public void testBuildVersionAttributes() throws Exception {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "commit 1",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("who"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "commit 2",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("you"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "commit 3",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("gonna"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "commit 4",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("call?"));
                   }}).execute();

        JGitFileSystem jGitFileSystem = mock(JGitFileSystem.class);
        when(jGitFileSystem.getGit()).thenReturn(git);

        final JGitPathImpl path = mock(JGitPathImpl.class);
        when(path.getFileSystem()).thenReturn(jGitFileSystem);
        when(path.getRefTree()).thenReturn("master");
        when(path.getPath()).thenReturn("path/to/file2.txt");

        final VersionAttributes versionAttributes = new JGitVersionAttributeView(path).readAttributes();

        List<VersionRecord> records = versionAttributes.history().records();
        assertEquals("commit 1",
                     records.get(0).comment());
        assertEquals("commit 2",
                     records.get(1).comment());
        assertEquals("commit 3",
                     records.get(2).comment());
        assertEquals("commit 4",
                     records.get(3).comment());
    }

    @Test
    public void testDiffForFileCreatedInEmptyRepositoryOrBranch() throws Exception {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        final ObjectId oldHead = new GetTreeFromRef(git,
                                                    "master").execute();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "commit 1",
                   null,
                   null,
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file.txt",
                           tempFile("who"));
                   }}).execute();

        final ObjectId newHead = new GetTreeFromRef(git,
                                                    "master").execute();

        List<DiffEntry> diff = new ListDiffs(git,
                                             oldHead,
                                             newHead).execute();
        assertNotNull(diff);
        assertFalse(diff.isEmpty());
        assertEquals(ChangeType.ADD,
                     diff.get(0).getChangeType());
        assertEquals("path/to/file.txt",
                     diff.get(0).getNewPath());
    }
}
