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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;
import org.uberfire.java.nio.fs.jgit.util.commands.Fork;
import org.uberfire.java.nio.fs.jgit.util.commands.ListRefs;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class JGitForkTest extends AbstractTestInfra {

    public static final String TARGET_GIT = "target/target";
    public static final String SOURCE_GIT = "source/source";
    private static Logger logger = LoggerFactory.getLogger(JGitForkTest.class);

    @Test
    public void testToForkSuccess() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = new CreateRepository(gitSource).execute().get();

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

        new Fork(parentFolder,
                 SOURCE_GIT,
                 TARGET_GIT,
                 CredentialsProvider.getDefault(),
                 null).execute();

        final File gitCloned = new File(parentFolder,
                                        TARGET_GIT + ".git");
        final Git cloned = Git.createRepository(gitCloned);

        assertThat(cloned).isNotNull();

        assertThat(new ListRefs(cloned.getRepository()).execute()).hasSize(2);

        assertThat(new ListRefs(cloned.getRepository()).execute().get(0).getName()).isEqualTo("refs/heads/master");
        assertThat(new ListRefs(cloned.getRepository()).execute().get(1).getName()).isEqualTo("refs/heads/user_branch");

        final String remotePath = ((GitImpl) cloned)._remoteList().call().get(0).getURIs().get(0).getPath();
        assertThat(remotePath).isEqualTo(gitSource.getPath() + "/");
    }

    @Test(expected = GitException.class)
    public void testToForkAlreadyExists() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");
        final Git origin = new CreateRepository(gitSource).execute().get();

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

        final File gitTarget = new File(parentFolder,
                                        TARGET_GIT + ".git");
        final Git originTarget = new CreateRepository(gitTarget).execute().get();

        new Commit(originTarget,
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

        new Fork(parentFolder,
                 SOURCE_GIT,
                 TARGET_GIT,
                 CredentialsProvider.getDefault(),
                 null).execute();
    }

    @Test
    public void testToForkWrongSource() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        try {
            new Fork(parentFolder,
                     SOURCE_GIT,
                     TARGET_GIT,
                     CredentialsProvider.getDefault(),
                     null).execute();
            fail("If got here is because it could for the repository");
        } catch (InvalidRemoteException e) {
            assertThat(e).isNotNull();
            logger.info(e.getMessage(),
                        e);
        }
    }

    @Test
    public void testForkRepository() throws GitAPIException {

        String SOURCE = "testforkA/source";
        String TARGET = "testforkB/target";

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put(JGitFileSystemProvider.GIT_ENV_KEY_INIT,
                "true");
        }};

        String sourcePath = "git://" + SOURCE;
        final URI sourceUri = URI.create(sourcePath);
        provider.newFileSystem(sourceUri,
                               env);

        final Map<String, ?> forkEnv = new HashMap<String, Object>() {{
            put(JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME,
                SOURCE);
        }};
        String forkPath = "git://" + TARGET;
        final URI forkUri = URI.create(forkPath);
        final JGitFileSystem fs = (JGitFileSystem) provider.newFileSystem(forkUri,
                                                                          forkEnv);

        assertThat(((GitImpl) fs.getGit())._remoteList().call().get(0).getURIs().get(0).toString())
                .isEqualTo(new File(provider.getGitRepoContainerDir(),
                                    SOURCE + ".git").toPath().toUri().toString());
    }

    @Test(expected = FileSystemAlreadyExistsException.class)
    public void testForkRepositoryThatAlreadyExists() throws GitAPIException {

        String SOURCE = "testforkA/source";
        String TARGET = "testforkB/target";

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put(JGitFileSystemProvider.GIT_ENV_KEY_INIT,
                "true");
        }};

        String sourcePath = "git://" + SOURCE;
        final URI sourceUri = URI.create(sourcePath);
        provider.newFileSystem(sourceUri,
                               env);

        final Map<String, ?> forkEnv = new HashMap<String, Object>() {{
            put(JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME,
                SOURCE);
        }};

        String forkPath = "git://" + TARGET;
        final URI forkUri = URI.create(forkPath);
        provider.newFileSystem(forkUri,
                               forkEnv);
        provider.newFileSystem(forkUri,
                               forkEnv);
    }
}
