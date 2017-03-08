/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.commands.Merge;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.commit;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.createBranch;

public class JGitMergeTest extends AbstractTestInfra {

    public static final String SOURCE_GIT = "source/source";
    private static Logger logger = LoggerFactory.getLogger(JGitMergeTest.class);

    @Test
    public void testMergeSuccessful() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = JGitUtil.newRepository(gitSource,
                                                  true);

        commit(origin,
               "master",
               "name",
               "name@example.com",
               "master-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.txt",
                       tempFile("temp1"));
               }});

        createBranch(origin,
                     "master",
                     "develop");

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file2.txt",
                       tempFile("temp2"));
               }});

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-2",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file3.txt",
                       tempFile("temp3"));
               }});

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-3",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file4.txt",
                       tempFile("temp4"));
               }});

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-4",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file5.txt",
                       tempFile("temp5"));
               }});

        new Merge(origin,
                  "develop",
                  "master").execute();

        final List<DiffEntry> result = JGitUtil.getDiff(origin.getRepository(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "master").toObjectId(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "develop").toObjectId());

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testMergeConflict() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = JGitUtil.newRepository(gitSource,
                                                  true);

        commit(origin,
               "master",
               "name",
               "name@example.com",
               "master-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.txt",
                       tempFile("temp1"));
               }});

        createBranch(origin,
                     "master",
                     "develop");

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.txt",
                       tempFile("temp1"));
               }});

        new Merge(origin,
                  "develop",
                  "master").execute();

        final List<DiffEntry> result = JGitUtil.getDiff(origin.getRepository(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "master").toObjectId(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "develop").toObjectId());

        assertThat(result.size()).isEqualTo(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParametersNotNull() throws IOException, GitAPIException {

        new Merge(null,
                  "develop",
                  "master").execute();
    }

    @Test(expected = GitException.class)
    public void testTryToMergeNonexistentBranch() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = JGitUtil.newRepository(gitSource,
                                                  true);

        commit(origin,
               "master",
               "name",
               "name@example.com",
               "master-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.txt",
                       tempFile("temp1"));
               }});

        createBranch(origin,
                     "master",
                     "develop");

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file2.txt",
                       tempFile("temp2"));
               }});

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-2",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file3.txt",
                       tempFile("temp3"));
               }});

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-3",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file4.txt",
                       tempFile("temp4"));
               }});

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-4",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file5.txt",
                       tempFile("temp5"));
               }});

        new Merge(origin,
                  "develop",
                  "nonexistent").execute();
    }

    @Test(expected = GitException.class)
    public void testMergeBinaryInformationButHasConflicts() throws IOException, GitAPIException {

        final byte[] contentA = this.loadImage("images/drools.png");
        final byte[] contentB = this.loadImage("images/jbpm.png");
        final byte[] contentC = this.loadImage("images/opta.png");

        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = JGitUtil.newRepository(gitSource,
                                                  true);

        commit(origin,
               "master",
               "name",
               "name@example.com",
               "master-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.jpg",
                       tempFile(contentA));
               }});

        createBranch(origin,
                     "master",
                     "develop");

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.jpg",
                       tempFile(contentB));
               }});

        commit(origin,
               "master",
               "name",
               "name@example.com",
               "master-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.jpg",
                       tempFile(contentC));
               }});

        new Merge(origin,
                  "develop",
                  "master").execute();

        final List<DiffEntry> result = JGitUtil.getDiff(origin.getRepository(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "master").toObjectId(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "develop").toObjectId());

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testMergeBinaryInformationSuccessful() throws IOException, GitAPIException {

        final byte[] contentA = this.loadImage("images/drools.png");
        final byte[] contentB = this.loadImage("images/jbpm.png");

        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = JGitUtil.newRepository(gitSource,
                                                  true);

        commit(origin,
               "master",
               "name",
               "name@example.com",
               "master-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.jpg",
                       tempFile(contentA));
               }});

        createBranch(origin,
                     "master",
                     "develop");

        commit(origin,
               "develop",
               "name",
               "name@example.com",
               "develop-1",
               null,
               null,
               false,
               new HashMap<String, File>() {{
                   put("file1.jpg",
                       tempFile(contentB));
               }});

        new Merge(origin,
                  "develop",
                  "master").execute();

        final List<DiffEntry> result = JGitUtil.getDiff(origin.getRepository(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "master").toObjectId(),
                                                        JGitUtil.getTreeRefObjectId(origin.getRepository(),
                                                                                    "develop").toObjectId());

        assertThat(result.size()).isEqualTo(0);
    }
}
