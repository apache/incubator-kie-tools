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
package org.guvnor.ala.build.maven.executor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitHub;
import org.guvnor.ala.source.git.GitRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystems;

import static org.junit.Assert.*;

public class RepositoryVisitorTest {

    private File tempPath;

    @Before
    public void setUp() throws IOException {
        tempPath = Files.createTempDirectory("yyy").toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void repositoryVisitorDiffDeletedTest() throws IOException {
        final IOServiceNio2WrapperImpl ioService = new IOServiceNio2WrapperImpl();
        final GitHub gitHub = new GitHub();
        final GitRepository repository = (GitRepository) gitHub.getRepository("mbarkley/appformer-playground",
                                                                              new HashMap<String, String>() {
                                                                                  {
                                                                                      put("out-dir",
                                                                                          tempPath.getAbsolutePath());
                                                                                  }
                                                                              });
        final Source source = repository.getSource("master");

        final InputStream pomStream = org.uberfire.java.nio.file.Files.newInputStream(source.getPath().resolve("users-new").resolve("pom.xml"));
        final MavenProject project = MavenProjectLoader.parseMavenPom(pomStream);

        RepositoryVisitor repositoryVisitor = new RepositoryVisitor(source.getPath().resolve("users-new"),
                                                                    project.getName());

        System.out.println("Root: " + repositoryVisitor.getRoot().getAbsolutePath());

        Map<String, String> identityHash = repositoryVisitor.getIdentityHash();

        final URI originRepo = URI.create("git://" + repository.getName());

        final FileSystem fs = FileSystems.getFileSystem(originRepo);

        ioService.startBatch(fs);
        ioService.write(fs.getPath("/users-new/file.txt"),
                        "temp");
        ioService.write(fs.getPath("/users-new/pom.xml"),
                        "hi there" + UUID.randomUUID().toString());
        ioService.endBatch();
        ioService.delete(source.getPath().resolve("users-new").resolve("demo.iml"));

        RepositoryVisitor newRepositoryVisitor = new RepositoryVisitor(source.getPath().resolve("users-new"),
                                                                       repositoryVisitor.getRoot().getAbsolutePath().trim(),
                                                                       false);

        System.out.println("Root: " + newRepositoryVisitor.getRoot().getAbsolutePath());
        Map<String, String> newIdentityHash = newRepositoryVisitor.getIdentityHash();

        MapDifference<String, String> difference = Maps.difference(identityHash,
                                                                   newIdentityHash);

        Map<String, MapDifference.ValueDifference<String>> entriesDiffering = difference.entriesDiffering();
        System.out.println(" Size of Differences: " + entriesDiffering.size());
        for (String key : entriesDiffering.keySet()) {
            System.out.println("Different Value: " + key);
        }
        assertEquals(1,
                     entriesDiffering.size());
        assertNotNull(entriesDiffering.get("/users-new/pom.xml"));

        Map<String, String> deletedFiles = difference.entriesOnlyOnLeft();
        System.out.println(" Size of Deleted Files: " + deletedFiles.size());
        for (String key : deletedFiles.keySet()) {
            System.out.println("Deleted File: " + key);
        }
        assertEquals(1,
                     deletedFiles.size());
        assertNotNull(deletedFiles.get("/users-new/demo.iml"));
        Map<String, String> addedFiles = difference.entriesOnlyOnRight();
        System.out.println(" Size of added Files: " + addedFiles.size());
        for (String key : addedFiles.keySet()) {
            System.out.println("Added File: " + key);
        }
        assertEquals(1,
                     addedFiles.size());
        assertNotNull(addedFiles.get("/users-new/file.txt"));
    }
}
