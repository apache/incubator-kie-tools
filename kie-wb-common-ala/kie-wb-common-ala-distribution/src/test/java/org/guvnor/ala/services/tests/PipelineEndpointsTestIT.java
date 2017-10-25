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

package org.guvnor.ala.services.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.impl.DockerBuildConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfigStage;
import org.guvnor.ala.pipeline.impl.PipelineConfigImpl;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import static org.junit.Assert.*;

public class PipelineEndpointsTestIT {

    private final String APP_URL = "http://localhost:8080/api/";

    private File tempPath;

    @Before
    public void setUp() throws IOException {
        tempPath = Files.createTempDirectory("xxx").toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Ignore
    public void checkService() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(APP_URL);
        ResteasyWebTarget restEasyTarget = (ResteasyWebTarget) target;
        PipelineService proxyPipeline = restEasyTarget.proxy(PipelineService.class);

        RuntimeProvisioningService proxyRuntime = restEasyTarget.proxy(RuntimeProvisioningService.class);

        ProviderTypeList allProviderTypes = proxyRuntime.getProviderTypes(0,
                                                                          10,
                                                                          "",
                                                                          true);

        assertNotNull(allProviderTypes);
        assertEquals(3,
                     allProviderTypes.getItems().size());

        DockerProviderConfig dockerProviderConfig = new DockerProviderConfigImpl();
        proxyRuntime.registerProvider(dockerProviderConfig);

        ProviderList allProviders = proxyRuntime.getProviders(0,
                                                              10,
                                                              "",
                                                              true);
        assertEquals(1,
                     allProviders.getItems().size());
        assertTrue(allProviders.getItems().get(0) instanceof DockerProvider);

        PipelineConfigsList allPipelines = proxyPipeline.getPipelineConfigs(0,
                                                                            10,
                                                                            "",
                                                                            true);

        assertNotNull(allPipelines);
        assertEquals(0,
                     allPipelines.getItems().size());

        List<PipelineConfigStage> configs = new ArrayList<>();
        configs.add(new PipelineConfigStage("GitConfig",
                                            new GitConfigImpl()));
        configs.add(new PipelineConfigStage("MavenProjectConfig",
                                            new MavenProjectConfigImpl()));
        configs.add(new PipelineConfigStage("MavenBuildConfig",
                                            new MavenBuildConfigImpl()));
        configs.add(new PipelineConfigStage("DockerBuildConfig",
                                            new DockerBuildConfigImpl()));
        configs.add(new PipelineConfigStage("MavenBuildExecConfig",
                                            new MavenBuildExecConfigImpl()));
        configs.add(new PipelineConfigStage("DockerProviderConfig",
                                            new DockerProviderConfigImpl()));
        configs.add(new PipelineConfigStage("ContextAwareDockerProvisioning",
                                            new ContextAwareDockerProvisioningConfig()));
        configs.add(new PipelineConfigStage("ContextAwareDockerRuntimeExec",
                                            new ContextAwareDockerRuntimeExecConfig()));

        String newPipeline = proxyPipeline.newPipeline(new PipelineConfigImpl("mypipe",
                                                                              configs));

        Input input = new Input();

        input.put("repo-name",
                  "drools-workshop");
        input.put("create-repo",
                  "true");
        input.put("branch",
                  "master");
        input.put("out-dir",
                  tempPath.getAbsolutePath());
        input.put("origin",
                  "https://github.com/kiegroup/drools-workshop");
        input.put("project-dir",
                  "drools-webapp-example");

        proxyPipeline.runPipeline("mypipe",
                                  input,
                                  false);

        RuntimeList allRuntimes = proxyRuntime.getRuntimes(0,
                                                           10,
                                                           "",
                                                           true);

        assertEquals(1,
                     allRuntimes.getItems().size());

        proxyRuntime.destroyRuntime(allRuntimes.getItems().get(0).getId(),
                                    true);

        allRuntimes = proxyRuntime.getRuntimes(0,
                                               10,
                                               "",
                                               true);

        assertEquals(0,
                     allRuntimes.getItems().size());
    }
}
