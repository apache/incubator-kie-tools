/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.extensions.FileSystemHookExecutionContext;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.file.extensions.FileSystemHooksConstants;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JGitFileSystemImplProviderPostCommitHookTest extends AbstractTestInfra {

    private static final Integer SUCCESS = 0;
    private static final Integer WARNING = 10;
    private static final Integer ERROR = 50;

    private static final String SCRIPT = "exit ";

    private static final String HOOKS_FOLDER = "hooks";

    private static final String GIT = "git://";

    private static final String REPO_NAME = "repo";

    private static final String NEW_FILE_PATH = "/folder/file.txt";

    @Mock
    private FileSystemHooks.FileSystemHook postCommitHook;

    @Captor
    private ArgumentCaptor<FileSystemHookExecutionContext> contextCaptor;

    private JGitFileSystem fs;

    @Before
    public void init() {
        final URI newRepo = URI.create(GIT + REPO_NAME);

        final Map<String, Object> env = new HashMap<>();
        env.put(FileSystemHooks.PostCommit.name(), postCommitHook);

        fs = (JGitFileSystem) provider.newFileSystem(newRepo, env);

        assertThat(fs).isNotNull();
    }

    @Test
    public void testPostCommitWithoutHook() throws IOException {
        commitFile();

        verify(postCommitHook, never()).execute(any());
    }

    @Test
    public void testPostCommitHookSuccess() throws IOException {

        testPostCommit(SUCCESS);

    }

    @Test
    public void testPostCommitHookWarning() throws IOException {

        testPostCommit(WARNING);

    }

    @Test
    public void testPostCommitHookError() throws IOException {

        testPostCommit(ERROR);

    }

    private void testPostCommit(final Integer exitCode) throws IOException {
        prepareHook(exitCode);

        commitFile();

        verify(postCommitHook).execute(contextCaptor.capture());

        FileSystemHookExecutionContext context = contextCaptor.getValue();

        Assertions.assertThat(context)
                .isNotNull()
                .hasFieldOrPropertyWithValue("fsName", REPO_NAME);

        Assertions.assertThat(context.getParamValue(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE))
                .isNotNull()
                .isEqualTo(exitCode);

    }

    private void prepareHook(final Integer code) throws IOException {

        File destHookFile = fs.getGit().getRepository().getDirectory().toPath().resolve(HOOKS_FOLDER).resolve("post-commit").toFile();

        FileUtils.write(destHookFile, SCRIPT + code, Charset.defaultCharset());

        if (SystemUtils.IS_OS_WINDOWS) {
            destHookFile.setReadable(true);
            destHookFile.setWritable(true);
            destHookFile.setExecutable(true);
        } else {
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            perms.add(PosixFilePermission.OWNER_EXECUTE);

            Files.setPosixFilePermissions(destHookFile.toPath(), perms);
        }
    }

    private void commitFile() throws IOException {
        final Path path = provider.getPath(URI.create(GIT + REPO_NAME + NEW_FILE_PATH));

        final OutputStream outStream = provider.newOutputStream(path);
        assertThat(outStream).isNotNull();
        outStream.write(("my content").getBytes());
        outStream.close();

    }
}
