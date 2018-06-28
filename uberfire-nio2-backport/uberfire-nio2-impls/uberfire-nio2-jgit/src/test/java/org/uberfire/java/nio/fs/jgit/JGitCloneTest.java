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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.hooks.PostCommitHook;
import org.eclipse.jgit.hooks.PreCommitHook;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.commands.Clone;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;
import org.uberfire.java.nio.fs.jgit.util.commands.ListRefs;

public class JGitCloneTest extends AbstractTestInfra {

    private static final String
            TARGET_GIT = "target/target",
            SOURCE_GIT = "source/source";

    @Test
    public void testToCloneSuccess() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");

        final File gitTarget = new File(parentFolder,
                                        TARGET_GIT + ".git");

        final Git origin = setupGitRepo(gitSource, null);

        final Git cloned = new Clone(gitTarget,
                                     gitSource.getAbsolutePath(),
                                     false,
                                     CredentialsProvider.getDefault(),
                                     null,
                                     null).execute().get();

        assertThat(cloned).isNotNull();

        assertThat(new ListRefs(cloned.getRepository()).execute()).hasSize(2);
        assertEquals(new ListRefs(cloned.getRepository()).execute().size(),
                     new ListRefs(origin.getRepository()).execute().size());

        assertThat(new ListRefs(cloned.getRepository()).execute().get(0).getName()).isEqualTo("refs/heads/master");
        assertThat(new ListRefs(cloned.getRepository()).execute().get(1).getName()).isEqualTo("refs/heads/user_branch");
    }

    @Test
    public void cloneShouldOnlyWorksWithEmptyRepos() throws IOException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");

        final File gitTarget = new File(parentFolder,
                                        TARGET_GIT + ".git");

        final Git origin = setupGitRepo(gitSource, null);

        final Git cloned = new Clone(gitTarget,
                                     gitSource.getAbsolutePath(),
                                     false,
                                     CredentialsProvider.getDefault(),
                                     null,
                                     null).execute().get();

        assertThat(cloned).isNotNull();

        assertThat(new ListRefs(cloned.getRepository()).execute()).hasSize(2);
        assertEquals(new ListRefs(cloned.getRepository()).execute().size(),
                     new ListRefs(origin.getRepository()).execute().size());

        assertThatThrownBy(() -> new Clone(gitTarget,
                                           gitSource.getAbsolutePath(),
                                           false,
                                           CredentialsProvider.getDefault(),
                                           null,
                                           null).execute().get())
                .isInstanceOf(Clone.CloneException.class);
    }
    
    @Test
    public void testCloneWithHookDir() throws IOException, GitAPIException {
    	final File hooksDir = createTempDirectory();

        writeMockHook(hooksDir, PostCommitHook.NAME);
        writeMockHook(hooksDir, PreCommitHook.NAME);
    	
    	final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder,
                                        SOURCE_GIT + ".git");

        final File gitTarget = new File(parentFolder,
                                        TARGET_GIT + ".git");
        		

        final Git origin = setupGitRepo(gitSource, hooksDir);

        final Git cloned = new Clone(gitTarget,
                                     gitSource.getAbsolutePath(),
                                     false,
                                     CredentialsProvider.getDefault(),
                                     null,
                                     hooksDir).execute().get();

        assertThat(cloned).isNotNull();

        assertThat(new ListRefs(cloned.getRepository()).execute()).hasSize(2);
        assertEquals(new ListRefs(cloned.getRepository()).execute().size(),
                     new ListRefs(origin.getRepository()).execute().size());

        assertThat(new ListRefs(cloned.getRepository()).execute().get(0).getName()).isEqualTo("refs/heads/master");
        assertThat(new ListRefs(cloned.getRepository()).execute().get(1).getName()).isEqualTo("refs/heads/user_branch");
        
        boolean foundPreCommitHook = false;
        boolean foundPostCommitHook = false;
        File[] hooks = new File(cloned.getRepository().getDirectory(), "hooks").listFiles();
		assertThat(hooks).isNotEmpty().isNotNull();
		assertThat(hooks.length).isEqualTo(2);
        for (File hook : hooks) {
            if (hook.getName().equals(PreCommitHook.NAME)) {
                foundPreCommitHook = hook.canExecute();
            } else if (hook.getName().equals(PostCommitHook.NAME)) {
                foundPostCommitHook = hook.canExecute();
            }
        }
        assertThat(foundPreCommitHook).isTrue();
        assertThat(foundPostCommitHook).isTrue();
    }

    private Git setupGitRepo(File gitSource, File hooksDir) throws IOException {
        final Git origin = new CreateRepository(gitSource, hooksDir).execute().get();

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
        return origin;
    }
}
