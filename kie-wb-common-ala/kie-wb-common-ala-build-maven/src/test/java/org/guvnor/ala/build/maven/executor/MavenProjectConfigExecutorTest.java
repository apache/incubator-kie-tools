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
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.guvnor.ala.build.Binary;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryBuildRegistry;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.source.Repository;
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class MavenProjectConfigExecutorTest {

    private File tempPath;

    private String gitUrl;

    @Before
    public void setUp() throws Exception {
        tempPath = Files.createTempDirectory("yyy").toFile();
        gitUrl = MavenTestUtils.createGitRepoWithPom(tempPath);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void testAPI() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();

        final Pipeline pipe = PipelineFactory
                .newBuilder()
                .addConfigStage("Git Source",
                                new MyGitConfig())
                .addConfigStage("Maven Project",
                                new MavenProjectConfigImpl())
                .addConfigStage("Maven Build Config",
                                new MavenBuildConfigImpl())
                .addConfigStage("Maven Build",
                                new MavenBuildExecConfigImpl())
                .buildAs("my pipe");

        final PipelineExecutor executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                                                                      new MavenProjectConfigExecutor(sourceRegistry),
                                                                      new MavenBuildConfigExecutor(),
                                                                      new MavenBuildExecConfigExecutor(buildRegistry)));
        executor.execute(new Input() {
                             {
                                 put("repo-name",
                                     "drools-workshop-pipe");
                                 put("create-repo",
                                     "true");
                                 put("branch",
                                     "master");
                                 put("out-dir",
                                     tempPath.getAbsolutePath());
                                 put("origin",
                                     gitUrl);
                             }
                         },
                         pipe,
                         System.out::println);

        List<Repository> allRepositories = sourceRegistry.getAllRepositories();
        assertEquals(1,
                     allRepositories.size());
        Repository repo = allRepositories.get(0);
        List<Project> allProjects = sourceRegistry.getAllProjects(repo);
        assertEquals(1,
                     allProjects.size());
        List<Binary> allBinaries = buildRegistry.getAllBinaries();
        assertEquals(1,
                     allBinaries.size());
        assertMavenBinary(allBinaries.get(0),
                          allProjects.get(0));

        final String tempDir = sourceRegistry.getAllProjects(repo).get(0).getTempDir();

        executor.execute(new Input() {
                             {
                                 put("project-temp-dir",
                                     tempDir);
                                 put("repo-name",
                                     "drools-workshop-pipe");
                                 put("branch",
                                     "master");
                             }
                         },
                         pipe,
                         System.out::println);

        allRepositories = sourceRegistry.getAllRepositories();
        assertEquals(1,
                     allRepositories.size());
        repo = allRepositories.get(0);
        allProjects = sourceRegistry.getAllProjects(repo);
        assertEquals(2,
                     allProjects.size());
        allBinaries = buildRegistry.getAllBinaries();
        assertEquals(1,
                     allBinaries.size());
        assertMavenBinary(allBinaries.get(0),
                          allProjects.get(1));
    }

    @Test
    public void testReuseTmpDirectoryAPI() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();

        final Pipeline pipe = PipelineFactory
                .newBuilder()
                .addConfigStage("Git Source",
                                new MyGitConfig())
                .addConfigStage("Maven Project",
                                new MavenProjectConfigImpl())
                .addConfigStage("Maven Build Config",
                                new MavenBuildConfigImpl())
                .addConfigStage("Maven Build",
                                new MavenBuildExecConfigImpl())
                .buildAs("my pipe");

        final PipelineExecutor executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                                                                      new MavenProjectConfigExecutor(sourceRegistry),
                                                                      new MavenBuildConfigExecutor(),
                                                                      new MavenBuildExecConfigExecutor(buildRegistry)));
        executor.execute(new Input() {
                             {
                                 put("repo-name",
                                     "drools-workshop-pipe2");
                                 put("create-repo",
                                     "true");
                                 put("branch",
                                     "master");
                                 put("out-dir",
                                     tempPath.getAbsolutePath());
                                 put("origin",
                                     gitUrl);
                             }
                         },
                         pipe,
                         System.out::println);

        List<Repository> allRepositories = sourceRegistry.getAllRepositories();
        assertEquals(1,
                     allRepositories.size());
        Repository repo = allRepositories.get(0);
        List<Project> allProjects = sourceRegistry.getAllProjects(repo);
        assertEquals(1,
                     allProjects.size());
        List<Binary> allBinaries = buildRegistry.getAllBinaries();
        assertEquals(1,
                     allBinaries.size());
        assertMavenBinary(allBinaries.get(0),
                          allProjects.get(0));

        final String tempDir = sourceRegistry.getAllProjects(repo).get(0).getTempDir();

        executor.execute(new Input() {
                             {
                                 put("project-temp-dir",
                                     tempDir);
                                 put("repo-name",
                                     "drools-workshop-pipe2");
                                 put("branch",
                                     "master");
                             }
                         },
                         pipe,
                         System.out::println);

        allRepositories = sourceRegistry.getAllRepositories();
        assertEquals(1,
                     allRepositories.size());
        repo = allRepositories.get(0);
        allProjects = sourceRegistry.getAllProjects(repo);
        assertEquals(2,
                     allProjects.size());
        allBinaries = buildRegistry.getAllBinaries();
        assertEquals(1,
                     allBinaries.size());
        assertMavenBinary(allBinaries.get(0),
                          allProjects.get(1));
    }

    private static void assertMavenBinary(final Binary binary,
                                          final Project project) {
        assertTrue(binary instanceof MavenBinary);
        final MavenBinary mavenBinary = (MavenBinary) binary;
        assertEquals("Maven",
                     mavenBinary.getType());
        final File pom = new File(project.getTempDir(),
                                  "pom.xml");
        final MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pom);
        assertEquals(mavenProject.getGroupId(),
                     mavenBinary.getGroupId());
        assertEquals(mavenProject.getArtifactId(),
                     mavenBinary.getArtifactId());
        assertEquals(mavenProject.getVersion(),
                     mavenBinary.getVersion());
        assertEquals(project.getTempDir() + "/target/" + project.getExpectedBinary(),
                     mavenBinary.getPath().toString());
    }

    static class MyGitConfig implements GitConfig,
                                        ContextAware {

        private Map<String, ?> context;

        @Override
        public void setContext(final Map<String, ?> context) {
            this.context = context;
        }

        @Override
        public String getCreateRepo() {
            return (((Map) context.get("input")).get("create-repo") != null) ? ((Map) context.get("input")).get("create-repo").toString() : "false";
        }

        @Override
        public String getRepoName() {
            return ((Map) context.get("input")).get("repo-name").toString();
        }

        @Override
        public String getBranch() {
            return "master";
        }
    }
}