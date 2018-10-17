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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.eclipse.jgit.hooks.PreCommitHook;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;

public class JGitFileSystemImplProviderHookTest extends AbstractTestInfra {

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        try {
            final File hooksDir = createTempDirectory();
            gitPrefs.put("org.uberfire.nio.git.hooks",
                         hooksDir.getAbsolutePath());

            writeMockHook(hooksDir,
                          "post-commit");
            writeMockHook(hooksDir,
                          PreCommitHook.NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gitPrefs;
    }

    @Test
    public void testInstalledHook() {
        final URI newRepo = URI.create("git://hook-repo-name");

        final FileSystem fs = provider.newFileSystem(newRepo,
                                                     EMPTY_ENV);

        assertThat(fs).isNotNull();

        if (fs instanceof JGitFileSystemImpl) {
            File[] hooks = new File(((JGitFileSystemImpl) fs).getGit().getRepository().getDirectory(),
                                    "hooks").listFiles();
            assertThat(hooks).isNotEmpty().isNotNull();
            assertThat(hooks.length).isEqualTo(2);

            boolean foundPreCommitHook = false;
            boolean foundPostCommitHook = false;
            for (File hook : hooks) {
                if (hook.getName().equals("pre-commit")) {
                    foundPreCommitHook = hook.canExecute();
                } else if (hook.getName().equals("post-commit")) {
                    foundPostCommitHook = hook.canExecute();
                }
            }
            assertThat(foundPreCommitHook).isTrue();
            assertThat(foundPostCommitHook).isTrue();
        }
    }

    @Test
    public void testExecutedPostCommitHook() throws IOException {
        testHook("hook-repo-name-executed",
                 "post-commit",
                 true);
    }

    @Test
    public void testNotSupportedPreCommitHook() throws IOException {
        testHook("hook-repo-name-executed-pre-commit",
                 "pre-commit",
                 false);
    }

}
