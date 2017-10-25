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
package org.guvnor.ala.wildfly.executor.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.arquillian.cube.CubeController;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
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
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.impl.WildflyAccessInterfaceImpl;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.config.impl.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.executor.WildflyProviderConfigExecutor;
import org.guvnor.ala.wildfly.executor.WildflyRuntimeExecExecutor;
import org.guvnor.ala.wildfly.executor.tests.requirement.RequiresNotWindows;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.service.WildflyRuntimeManager;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.STOPPED;
import static org.junit.Assert.*;

/**
 * Test the Wildfly Provider by starting a docker image of wildfly and deploying
 * an application there.
 */
@RunWith(ArquillianConditionalRunner.class)
@RequiresNotWindows
public class WildflyExecutorTest {

    private static final String CONTAINER = "swarm";
    private static File tempPath;

    @HostIp
    private String ip;

    @ArquillianResource
    private CubeController cc;

    @BeforeClass
    public static void setUp() {
        try {
            tempPath = Files.createTempDirectory("xxx").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    @InSequence(1)
    public void shouldBeAbleToCreateAndStartTest() {
        cc.create(CONTAINER);
        cc.start(CONTAINER);
    }

    @Test
    @InSequence(2)
    public void testAPI() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final WildflyAccessInterface wildflyAccessInterface = new WildflyAccessInterfaceImpl();

        final Pipeline pipe = PipelineFactory
                .newBuilder()
                .addConfigStage("Git Source",
                                new GitConfig() {
                                })
                .addConfigStage("Maven Project",
                                new MavenProjectConfig() {
                                })
                .addConfigStage("Maven Build Config",
                                new MavenBuildConfig() {
                                })
                .addConfigStage("Maven Build",
                                new MavenBuildExecConfig() {
                                })
                .addConfigStage("Wildfly Provider Config",
                                new WildflyProviderConfig() {
                                })
                .addConfigStage("Wildfly Runtime Exec",
                                new ContextAwareWildflyRuntimeExecConfig())
                .buildAs("my pipe");

        WildflyRuntimeExecExecutor wildflyRuntimeExecExecutor = new WildflyRuntimeExecExecutor(runtimeRegistry,
                                                                                               wildflyAccessInterface);
        final PipelineExecutor executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                                                                      new MavenProjectConfigExecutor(sourceRegistry),
                                                                      new MavenBuildConfigExecutor(),
                                                                      new MavenBuildExecConfigExecutor(buildRegistry),
                                                                      new WildflyProviderConfigExecutor(runtimeRegistry),
                                                                      wildflyRuntimeExecExecutor));

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
                                     "https://github.com/salaboy/drools-workshop");
                                 put("project-dir",
                                     "drools-webapp-example");
                                 put("provider-name",
                                     "wildlfy-test");
                                 put("wildfly-user",
                                     "admin");
                                 put("wildfly-password",
                                     "Admin#70365");
                                 put("host",
                                     ip);
                                 put("port",
                                     "8080");
                                 put("management-port",
                                     "9990");
                             }
                         },
                         pipe,
                         System.out::println);

        List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allRuntimes.size());

        Runtime runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof WildflyRuntime);

        WildflyRuntime wildflyRuntime = (WildflyRuntime) runtime;

        WildflyRuntimeManager runtimeManager = new WildflyRuntimeManager(runtimeRegistry,
                                                                         wildflyAccessInterface);

        runtimeManager.start(wildflyRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof WildflyRuntime);

        wildflyRuntime = (WildflyRuntime) runtime;

        assertEquals(RUNNING,
                     wildflyRuntime.getState().getState());

        runtimeManager.stop(wildflyRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof WildflyRuntime);

        wildflyRuntime = (WildflyRuntime) runtime;

        assertEquals(STOPPED,
                     wildflyRuntime.getState().getState());

        wildflyRuntimeExecExecutor.destroy(wildflyRuntime);

        wildflyAccessInterface.dispose();
    }

    @Test
    @InSequence(3)
    public void testRedeploy() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final WildflyAccessInterface wildflyAccessInterface = new WildflyAccessInterfaceImpl();

        final Pipeline pipe = PipelineFactory
                .newBuilder()
                .addConfigStage("Git Source",
                                new GitConfig() {
                                })
                .addConfigStage("Maven Project",
                                new MavenProjectConfig() {
                                })
                .addConfigStage("Maven Build Config",
                                new MavenBuildConfig() {
                                })
                .addConfigStage("Maven Build",
                                new MavenBuildExecConfig() {
                                })
                .addConfigStage("Wildfly Provider Config",
                                new WildflyProviderConfig() {
                                })
                .addConfigStage("Wildfly Runtime Exec",
                                new ContextAwareWildflyRuntimeExecConfig())
                .buildAs("my pipe");

        WildflyRuntimeExecExecutor wildflyRuntimeExecExecutor = new WildflyRuntimeExecExecutor(runtimeRegistry,
                                                                                               wildflyAccessInterface);
        final PipelineExecutor executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                                                                      new MavenProjectConfigExecutor(sourceRegistry),
                                                                      new MavenBuildConfigExecutor(),
                                                                      new MavenBuildExecConfigExecutor(buildRegistry),
                                                                      new WildflyProviderConfigExecutor(runtimeRegistry),
                                                                      wildflyRuntimeExecExecutor));

        executor.execute(new Input() {
                             {
                                 put("repo-name",
                                     "drools-workshop");
                                 put("branch",
                                     "master");
                                 put("origin",
                                     "https://github.com/salaboy/drools-workshop");
                                 put("project-dir",
                                     "drools-webapp-example");
                                 put("provider-name",
                                     "wildlfy-test");
                                 put("wildfly-user",
                                     "admin");
                                 put("wildfly-password",
                                     "Admin#70365");
                                 put("host",
                                     ip);
                                 put("port",
                                     "8080");
                                 put("management-port",
                                     "9990");
                             }
                         },
                         pipe,
                         System.out::println);

        List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allRuntimes.size());

        Runtime runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof WildflyRuntime);

        WildflyRuntime wildflyRuntime = (WildflyRuntime) runtime;

        WildflyRuntimeManager runtimeManager = new WildflyRuntimeManager(runtimeRegistry,
                                                                         wildflyAccessInterface);

        runtimeManager.start(wildflyRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof WildflyRuntime);

        wildflyRuntime = (WildflyRuntime) runtime;

        assertEquals(RUNNING,
                     wildflyRuntime.getState().getState());
        runtimeManager.stop(wildflyRuntime);

        allRuntimes = runtimeRegistry.getRuntimes(0,
                                                  10,
                                                  "",
                                                  true);

        assertEquals(1,
                     allRuntimes.size());

        runtime = allRuntimes.get(0);

        assertTrue(runtime instanceof WildflyRuntime);

        wildflyRuntime = (WildflyRuntime) runtime;

        assertEquals(STOPPED,
                     wildflyRuntime.getState().getState());

        wildflyRuntimeExecExecutor.destroy(wildflyRuntime);

        wildflyAccessInterface.dispose();
    }

    @Test
    @InSequence(4)
    public void shouldBeAbleToStopAndDestroyTest() {
        cc.stop(CONTAINER);
        cc.destroy(CONTAINER);
    }
}
