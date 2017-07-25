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

package org.uberfire.java.nio.fs.jgit.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.jgit.revwalk.RevCommit;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.fs.jgit.AbstractTestInfra;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;

import static org.junit.Assert.*;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(loadDirectory = "target/test-classes", debug = true) // set "debug=true to see debug output
public class ConcurrentJGitUtilTest extends AbstractTestInfra {

    @BeforeClass
    public static void setup() {
        GitImpl.setRetryTimes(5);
    }

    @Test
    @BMScript(value = "byteman/retry/resolve_path.btm")
    public void testRetryResolvePath() throws IOException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "1st commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file1.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "2nd commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();

        try {
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file1.txt"));
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file1.txt"));
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file1.txt"));
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file1.txt"));
        } catch (Exception ex) {
            fail();
        }

        try {
            git.getPathInfo("master",
                            "path/to/file1.txt");
            fail("forced to fail!");
        } catch (RuntimeException ex) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/resolve_inputstream.btm")
    public void testRetryResolveInputStream() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "1st commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file1.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "2nd commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();

        try {
            assertNotNull(git.blobAsInputStream("master",
                                                "path/to/file1.txt"));
            assertNotNull(git.blobAsInputStream("master",
                                                "path/to/file1.txt"));
            assertNotNull(git.blobAsInputStream("master",
                                                "path/to/file1.txt"));
            assertNotNull(git.blobAsInputStream("master",
                                                "path/to/file1.txt"));
        } catch (Exception ex) {
            fail();
        }

        try {
            assertNotNull(git.blobAsInputStream("master",
                                                "path/to/file1.txt"));
            fail("forced to fail!");
        } catch (NoSuchFileException ex) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/list_path_content.btm")
    public void testRetryListPathContent() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "1st commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file1.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "2nd commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();

        try {
            assertNotNull(git.listPathContent("master",
                                              "path/to/"));
            assertNotNull(git.listPathContent("master",
                                              "path/to/"));
            assertNotNull(git.listPathContent("master",
                                              "path/to/"));
            assertNotNull(git.listPathContent("master",
                                              "path/to/"));
        } catch (Exception ex) {
            fail();
        }

        try {
            assertNotNull(git.listPathContent("master",
                                              "path/to/"));
            fail("forced to fail!");
        } catch (RuntimeException ex) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/check_path.btm")
    public void testRetryCheckPath() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "1st commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file1.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "2nd commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();

        try {
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file2.txt"));
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file2.txt"));
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file2.txt"));
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file2.txt"));
        } catch (Exception ex) {
            fail();
        }

        try {
            assertNotNull(git.getPathInfo("master",
                                          "path/to/file2.txt"));
            fail("forced to fail!");
        } catch (RuntimeException ex) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/get_last_commit.btm")
    public void testRetryGetLastCommit() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "1st commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file1.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "2nd commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();

        try {
            assertNotNull(git.getLastCommit("master"));
            assertNotNull(git.getLastCommit("master"));
            assertNotNull(git.getLastCommit("master"));
            assertNotNull(git.getLastCommit("master"));
        } catch (Exception ex) {
            fail();
        }

        try {
            assertNotNull(git.getLastCommit("master"));
            fail("forced to fail!");
        } catch (RuntimeException ex) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/get_commits.btm")
    public void testRetryGetCommits() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File(parentFolder,
                                        "mytest.git");

        final Git git = new CreateRepository(gitFolder).execute().get();

        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "1st commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file1.txt",
                           tempFile("temp2222"));
                   }}).execute();
        new Commit(git,
                   "master",
                   "name",
                   "name@example.com",
                   "2nd commit",
                   null,
                   new Date(),
                   false,
                   new HashMap<String, File>() {{
                       put("path/to/file2.txt",
                           tempFile("temp2222"));
                   }}).execute();

        final RevCommit commit = git.getLastCommit("master");
        try {
            assertNotNull(git.listCommits(null,
                                          commit));
            assertNotNull(git.listCommits(null,
                                          commit));
            assertNotNull(git.listCommits(null,
                                          commit));
        } catch (Exception ex) {
            fail();
        }

        try {
            assertNotNull(git.listCommits(null,
                                          commit));
            fail("forced to fail!");
        } catch (RuntimeException ex) {
        }
    }
}