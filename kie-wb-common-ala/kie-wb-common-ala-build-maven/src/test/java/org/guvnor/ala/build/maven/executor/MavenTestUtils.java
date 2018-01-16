/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.build.maven.executor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;

public class MavenTestUtils {

    public static String createGitRepoWithPom(final File path, final File... files) throws Exception {
        final InputStream pom = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-pom.xml");
        return createGitRepoWithPom(path, pom, files);
    }

    public static String createGitRepoWithPom(final File path, final InputStream pom, final File... files) throws Exception {
        File repo = new File(path, "repo");
        if(repo.exists() == false) {
            Files.createDirectory(repo.toPath());
        }
        Git git = Git.init().setDirectory(repo).call();
        String gitUrl = "file://" + repo.getAbsolutePath();

        FileUtils.copyInputStreamToFile(pom, new File(repo, "pom.xml"));
        AddCommand add = git.add();
        add.addFilepattern("pom.xml");
        for (File f : files) {
            add.addFilepattern(f.getName());
        }
        add.call();
        CommitCommand commit = git.commit();
        commit.setMessage("initial commit").call();
        return gitUrl;
    }

}
