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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.junit.Test;
import org.uberfire.java.nio.base.options.MergeCopyOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.commands.GetTreeFromRef;
import org.uberfire.java.nio.fs.jgit.util.commands.ListDiffs;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.assertj.core.api.Assertions.assertThat;

public class JGitFileSystemImplProviderMergeTest extends AbstractTestInfra {

    @Test
    public void testMergeSuccessful() throws IOException {
        final URI newRepo = URI.create("git://merge-test-repo");
        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write("my cool content".getBytes());
            outStream.close();
        }

        final Path master = provider.getPath(URI.create("git://master@merge-test-repo"));
        final Path userBranch = provider.getPath(URI.create("git://user_branch@merge-test-repo"));

        provider.copy(master,
                      userBranch);

        {
            final Path path2 = provider.getPath(URI.create("git://user_branch@merge-test-repo/other/path/myfile2.txt"));

            final OutputStream outStream2 = provider.newOutputStream(path2);
            outStream2.write("my cool content".getBytes());
            outStream2.close();
        }
        {
            final Path path3 = provider.getPath(URI.create("git://user_branch@merge-test-repo/myfile3.txt"));

            final OutputStream outStream3 = provider.newOutputStream(path3);
            outStream3.write("my cool content".getBytes());
            outStream3.close();
        }

        provider.copy(userBranch,
                      master,
                      new MergeCopyOption());

        final Git gitRepo = ((JGitFileSystem) master.getFileSystem()).getGit();

        final List<DiffEntry> result = new ListDiffs(gitRepo,
                                                     new GetTreeFromRef(gitRepo,
                                                                        "master").execute(),
                                                     new GetTreeFromRef(gitRepo,
                                                                        "user_branch").execute()).execute();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test(expected = GitException.class)
    public void testMergeConflicts() throws IOException {
        final URI newRepo = URI.create("git://merge-test-repo");
        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write("my cool content".getBytes());
            outStream.close();
        }

        final Path master = provider.getPath(URI.create("git://master@merge-test-repo"));
        final Path userBranch = provider.getPath(URI.create("git://user_branch@merge-test-repo"));

        provider.copy(master,
                      userBranch);

        {
            final Path path2 = provider.getPath(URI.create("git://user_branch@merge-test-repo/other/path/myfile2.txt"));

            final OutputStream outStream2 = provider.newOutputStream(path2);
            outStream2.write("my cool content".getBytes());
            outStream2.close();
        }
        {
            final Path path = provider.getPath(URI.create("git://user_branch@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write("my cool content changed".getBytes());
            outStream.close();
        }
        {
            final Path path3 = provider.getPath(URI.create("git://user_branch@merge-test-repo/myfile3.txt"));

            final OutputStream outStream3 = provider.newOutputStream(path3);
            outStream3.write("my cool content".getBytes());
            outStream3.close();
        }
        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write("my very cool content".getBytes());
            outStream.close();
        }

        provider.copy(userBranch,
                      master,
                      new MergeCopyOption());
    }

    @Test
    public void testMergeBinarySuccessful() throws IOException {
        final URI newRepo = URI.create("git://merge-test-repo");
        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write(this.loadImage("images/drools.png"));
            outStream.close();
        }

        final Path master = provider.getPath(URI.create("git://master@merge-test-repo"));
        final Path userBranch = provider.getPath(URI.create("git://user_branch@merge-test-repo"));

        provider.copy(master,
                      userBranch);

        {
            final Path path2 = provider.getPath(URI.create("git://user_branch@merge-test-repo/other/path/myfile2.txt"));

            final OutputStream outStream2 = provider.newOutputStream(path2);
            outStream2.write(this.loadImage("images/jbpm.png"));
            outStream2.close();
        }
        {
            final Path path3 = provider.getPath(URI.create("git://user_branch@merge-test-repo/myfile3.txt"));

            final OutputStream outStream3 = provider.newOutputStream(path3);
            outStream3.write(this.loadImage("images/opta.png"));
            outStream3.close();
        }

        provider.copy(userBranch,
                      master,
                      new MergeCopyOption());

        final Git gitRepo = ((JGitFileSystem) master.getFileSystem()).getGit();
        final List<DiffEntry> result = new ListDiffs(gitRepo,
                                                     new GetTreeFromRef(gitRepo,
                                                                        "master").execute(),
                                                     new GetTreeFromRef(gitRepo,
                                                                        "user_branch").execute()).execute();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test(expected = GitException.class)
    public void testBinaryMergeConflicts() throws IOException {
        final URI newRepo = URI.create("git://merge-test-repo");
        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write(this.loadImage("images/drools.png"));
            outStream.close();
        }

        final Path master = provider.getPath(URI.create("git://master@merge-test-repo"));
        final Path userBranch = provider.getPath(URI.create("git://user_branch@merge-test-repo"));

        provider.copy(master,
                      userBranch);

        {
            final Path path2 = provider.getPath(URI.create("git://user_branch@merge-test-repo/other/path/myfile2.txt"));

            final OutputStream outStream = provider.newOutputStream(path2);
            outStream.write(this.loadImage("images/jbpm.png"));
            outStream.close();
        }
        {
            final Path path = provider.getPath(URI.create("git://user_branch@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write(this.loadImage("images/jbpm.png"));
            outStream.close();
        }
        {
            final Path path3 = provider.getPath(URI.create("git://user_branch@merge-test-repo/myfile3.txt"));

            final OutputStream outStream = provider.newOutputStream(path3);
            outStream.write(this.loadImage("images/opta.png"));
            outStream.close();
        }
        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write(this.loadImage(""));
            outStream.close();
        }

        provider.copy(userBranch,
                      master,
                      new MergeCopyOption());
    }

    @Test(expected = GitException.class)
    public void testTryToMergeNonexistentBranch() throws IOException {
        final URI newRepo = URI.create("git://merge-test-repo");
        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write("my cool content".getBytes());
            outStream.close();
        }

        final Path master = provider.getPath(URI.create("git://master@merge-test-repo"));
        final Path develop = provider.getPath(URI.create("git://develop@merge-test-repo"));

        provider.copy(develop,
                      master,
                      new MergeCopyOption());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingParemeter() throws IOException {
        final URI newRepo = URI.create("git://merge-test-repo");
        provider.newFileSystem(newRepo,
                               EMPTY_ENV);

        {
            final Path path = provider.getPath(URI.create("git://master@merge-test-repo/myfile1.txt"));

            final OutputStream outStream = provider.newOutputStream(path);
            outStream.write("my cool content".getBytes());
            outStream.close();
        }

        final Path master = provider.getPath(URI.create("git://master@merge-test-repo"));
        final Path develop = provider.getPath(URI.create("git://develop@merge-test-repo"));

        provider.copy(develop,
                      null,
                      new MergeCopyOption());
    }
}
