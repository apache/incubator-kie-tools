/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.docker.executor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenTestUtils;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.impl.DockerBuildConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.service.DockerRuntimeManager;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryBuildRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryRuntimeRegistry;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.STOPPED;
import static org.junit.Assert.*;

/**
 * Simple test using the Pipeline API and the docker Provider & Executors
 */
@Ignore
public class DockerExecutorTest {

    private File tempPath;

    private String gitUrl;

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
    }

    @Before
    public void setUp() throws Exception {
        tempPath = Files.createTempDirectory("xxx").toFile();
        final InputStream pom = Thread.currentThread().getContextClassLoader().getResourceAsStream("docker-test-pom.xml");
        gitUrl = MavenTestUtils.createGitRepoWithPom(tempPath, pom);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void testAPI() throws InterruptedException {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final DockerAccessInterface dockerAccessInterface = new DockerAccessInterfaceImpl();

        final Pipeline pipe = PipelineFactory
                .newBuilder()
                .addConfigStage("Git Source",
                                new GitConfigImpl())
                .addConfigStage("Maven Project",
                                new MavenProjectConfigImpl())
                .addConfigStage("Maven Build Config",
                                new MavenBuildConfigImpl())
                .addConfigStage("Docker Build Config",
                                new DockerBuildConfigImpl())
                .addConfigStage("Maven Build",
                                new MavenBuildExecConfigImpl())
                .addConfigStage("Docker Provider Config",
                                new DockerProviderConfigImpl())
                .addConfigStage("Docker Runtime Config",
                                new ContextAwareDockerProvisioningConfig())
                .addConfigStage("Docker Runtime Exec",
                                new ContextAwareDockerRuntimeExecConfig())
                .buildAs("my pipe");

        DockerRuntimeExecExecutor dockerRuntimeExecExecutor = new DockerRuntimeExecExecutor(runtimeRegistry,
                                                                                            dockerAccessInterface);

        final PipelineExecutor executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                                                                      new MavenProjectConfigExecutor(sourceRegistry),
                                                                      new MavenBuildConfigExecutor(),
                                                                      new MavenBuildExecConfigExecutor(buildRegistry),
                                                                      new DockerBuildConfigExecutor(),
                                                                      new DockerProviderConfigExecutor(runtimeRegistry),
                                                                      new DockerProvisioningConfigExecutor(),
                                                                      dockerRuntimeExecExecutor));

        executor.execute(new Input() {
                             {
                                 put("repo-name",
                                     "drools-workshop");
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
                         (Runtime b) -> System.out.println(b));

        List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allRuntimes.size());

        Runtime runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof DockerRuntime);

        DockerRuntime dockerRuntime = (DockerRuntime) runtime;

        DockerRuntimeManager runtimeManager = new DockerRuntimeManager(runtimeRegistry,
                                                                       dockerAccessInterface);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(RUNNING,
                     dockerRuntime.getState().getState());

        runtimeManager.stop(dockerRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(STOPPED,
                     dockerRuntime.getState().getState());

        dockerRuntimeExecExecutor.destroy(runtime);

        dockerAccessInterface.dispose();
    }

    @Test
    public void testFlexAPI() throws InterruptedException {
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final DockerAccessInterface dockerAccessInterface = new DockerAccessInterfaceImpl();

        final Pipeline pipe = PipelineFactory
                .newBuilder()
                .addConfigStage("Docker Provider Config",
                                new DockerProviderConfig() {
                                })
                .addConfigStage("Docker Runtime Config",
                                new ContextAwareDockerProvisioningConfig() {
                                })
                .addConfigStage("Docker Runtime Exec",
                                new ContextAwareDockerRuntimeExecConfig())
                .buildAs("my pipe");

        DockerRuntimeExecExecutor dockerRuntimeExecExecutor = new DockerRuntimeExecExecutor(runtimeRegistry,
                                                                                            dockerAccessInterface);
        final PipelineExecutor executor = new PipelineExecutor(asList(new DockerProviderConfigExecutor(runtimeRegistry),
                                                                      new DockerProvisioningConfigExecutor(),
                                                                      dockerRuntimeExecExecutor));
        executor.execute(new Input() {
                             {
                                 put("image-name",
                                     "kitematic/hello-world-nginx");
                                 put("port-number",
                                     "8080");
                                 put("docker-pull",
                                     "true");
                             }
                         },
                         pipe,
                         (Runtime b) -> System.out.println(b));

        List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allRuntimes.size());

        Runtime runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof DockerRuntime);

        DockerRuntime dockerRuntime = (DockerRuntime) runtime;

        DockerRuntimeManager runtimeManager = new DockerRuntimeManager(runtimeRegistry,
                                                                       dockerAccessInterface);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(RUNNING,
                     dockerRuntime.getState().getState());

        runtimeManager.stop(dockerRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        dockerRuntime = (DockerRuntime) runtime;

        assertEquals(STOPPED,
                     dockerRuntime.getState().getState());

        dockerRuntimeExecExecutor.destroy(runtime);

        dockerAccessInterface.dispose();
    }
}
