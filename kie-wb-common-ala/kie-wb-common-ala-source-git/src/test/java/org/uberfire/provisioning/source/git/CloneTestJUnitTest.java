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

package org.uberfire.provisioning.source.git;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test shows how the Cloning works
 */
public class CloneTestJUnitTest {

    private File tempPath;

    private String gitUrl;

    @Before
    public void setUp() throws Exception {
        tempPath = Files.createTempDirectory("xxx").toFile();
        File repo = new File(tempPath, "repo");
        Files.createDirectory(repo.toPath());
        Git.init().setDirectory(repo).call();
        gitUrl = "file://" + repo.getAbsolutePath();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void hello() throws Exception {
        final String repoName = "drools-workshop-build";
        final Optional<Source> source = new GitConfigExecutor(new InMemorySourceRegistry()).apply(new GitConfigImpl(tempPath.getAbsolutePath(),
                                                                                                                    "master",
                                                                                                                    gitUrl,
                                                                                                                    repoName,
                                                                                                                    "true"));
        assertTrue(source.isPresent());

        final String targetRepoDir = tempPath.getAbsolutePath() + "/" + repoName + ".git";
        Git git = Git.open(new File(targetRepoDir));

        assertNotNull(git.getRepository().exactRef(Constants.HEAD));
    }
}
