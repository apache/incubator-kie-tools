/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.commands.CreateRepository;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class JGitAddRemoteTest extends AbstractTestInfra {

    private Git git;

    @Before
    public void setup() throws IOException {
        final File parentFolder = createTempDirectory();

        final File gitSource = new File(parentFolder, "source/source.git");

        git = spy(new CreateRepository(gitSource).execute().get());

        commit(git, "master", "Initial commit",
               content("file0", multiline("file0", "content")));
    }

    @Test
    public void addRemoteTest() {
        final String url = "myRemoteUrl";

        git.addRemote("origin",
                      url);

        final String expectedUrl = git.getRepository().getConfig().getString("remote",
                                                                             "origin",
                                                                             "url");

        assertThat(url).isSameAs(expectedUrl);
    }

    @Test(expected = GitException.class)
    public void addRemoteFailTest() {
        doThrow(RuntimeException.class).when(git).getRepository();

        git.addRemote("origin",
                      "myRemoteUrl");
    }
}
