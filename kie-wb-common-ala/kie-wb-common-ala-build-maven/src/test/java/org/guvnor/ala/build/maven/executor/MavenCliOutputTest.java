/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.build.maven.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.impl.MavenProjectImpl;
import org.guvnor.ala.build.maven.util.MavenBuildExecutor;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Maven Compilation output parsing to evaluate the process output
 */
public class MavenCliOutputTest {

    public MavenCliOutputTest() {
    }

    private File tempPath;

    private String gitUrl;

    @Before
    public void setUp() throws Exception {
        tempPath = Files.createTempDirectory("zzz").toFile();
        gitUrl = MavenTestUtils.createGitRepoWithPom(tempPath);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void buildAppAndWaitForMavenOutputTest() throws IOException {
        final Optional<Source> _source = new GitConfigExecutor(new InMemorySourceRegistry()).apply(new GitConfigImpl(tempPath.getAbsolutePath(),
                                                                                                                     "master",
                                                                                                                     gitUrl,
                                                                                                                     "drools-workshop",
                                                                                                                     "true"));

        assertTrue(_source.isPresent());
        final Source source = _source.get();

        boolean buildProcessReady = false;
        Throwable error = null;
        PipedOutputStream baosOut = new PipedOutputStream();
        PipedOutputStream baosErr = new PipedOutputStream();
        final PrintStream out = new PrintStream(baosOut,
                                                true);
        final PrintStream err = new PrintStream(baosErr,
                                                true);

        //Build the project in a different thread
        new Thread(() -> {
            buildMavenProject(source,
                              out,
                              err);
        }).start();

        // Use the PipeOutputStream to read the execution output and validate that the application was built. 
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new InputStreamReader(new PipedInputStream(baosOut)));
        String line;

        while (!(buildProcessReady || error != null)) {

            if ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
                if (line.contains("Building war:")) {
                    buildProcessReady = true;
                    out.close();
                    err.close();
                    baosOut.close();
                    baosErr.close();
                }
            }
        }

        assertTrue(sb.toString().contains("Building war:"));
        assertTrue(buildProcessReady);
        assertTrue(error == null);
    }

    /*
     * Build Maven Project from Source using Out and Err PrintStreams for getting the output
     */
    private void buildMavenProject(Source source,
                                   PrintStream out,
                                   PrintStream err) throws org.uberfire.java.nio.IOException, SecurityException, UnsupportedOperationException, IllegalArgumentException {
        List<String> goals = new ArrayList<>();
        goals.add("package");
        Properties p = new Properties();
        p.setProperty("failIfNoTests",
                      "false");

        final InputStream pomStream = org.uberfire.java.nio.file.Files.newInputStream(source.getPath().resolve("pom.xml"));
        MavenProject project = MavenProjectLoader.parseMavenPom(pomStream);

        final String expectedBinary = project.getArtifact().getArtifactId() + "-" + project.getArtifact().getVersion() + "." + project.getArtifact().getType();
        final org.guvnor.ala.build.maven.model.MavenProject mavenProject = new MavenProjectImpl(project.getId(),
                                                                                                project.getArtifact().getType(),
                                                                                                project.getName(),
                                                                                                expectedBinary,
                                                                                                source.getPath(),
                                                                                                source.getPath(),
                                                                                                source.getPath().resolve("target").resolve(expectedBinary).toAbsolutePath(),
                                                                                                null,
                                                                                                null);
        final File pom = new File(getRepositoryVisitor(mavenProject).getRoot(),
                                  "pom.xml");
        MavenBuildExecutor.executeMaven(pom,
                                        out,
                                        err,
                                        p,
                                        goals.toArray(new String[0]));
    }

    private RepositoryVisitor getRepositoryVisitor(final Project project) {
        return new RepositoryVisitor(project.getPath(),
                                     project.getName());
    }
}
