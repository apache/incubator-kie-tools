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

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.PipelineRegistry;
import org.kie.workbench.common.services.backend.builder.ala.impl.LocalBuildConfigImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

/**
 * This class is responsible for the build pipeline initialization at system startup. Additionally it provides access
 * to a pipeline executor for interested parties on having raw access to the build pipeline execution.
 */
@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class BuildPipelineInitializer {

    public static final String LOCAL_BUILD_PIPELINE = "local-build-pipeline";

    private PipelineRegistry pipelineRegistry;

    private Instance<ConfigExecutor> configExecutors;

    private PipelineExecutor executor;

    public BuildPipelineInitializer() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BuildPipelineInitializer(final PipelineRegistry pipelineRegistry,
                                    final Instance<ConfigExecutor> configExecutors) {
        this.pipelineRegistry = pipelineRegistry;
        this.configExecutors = configExecutors;
    }

    /**
     * Intended mainly for testing
     */
    public BuildPipelineInitializer(PipelineRegistry pipelineRegistry,
                                    Collection<ConfigExecutor> configs) {
        this.pipelineRegistry = pipelineRegistry;
        initLocalBuildPipeline();
        initExecutor(configs);
    }

    /**
     * @return A pipeline executor for executing the initialized pipelines.
     */
    @Produces
    @Named("buildPipelineExecutor")
    public PipelineExecutor getExecutor() {
        return executor;
    }

    @PostConstruct
    private void init() {
        initLocalBuildPipeline();
        initExecutor();
    }

    /**
     * Initializes a build pipeline based on local structures and optimizations required by the workbench.
     */
    private void initLocalBuildPipeline() {
        final Pipeline localBuildPipeline = PipelineFactory
                .newBuilder()
                .addConfigStage("Local Source Config",
                                new LocalSourceConfig() {
                                })
                .addConfigStage("Local Project Config",
                                new LocalProjectConfig() {
                                })
                .addConfigStage("Local Build Config",
                                new LocalBuildConfigImpl())
                .addConfigStage("Local Build Exec",
                                new LocalBuildExecConfig() {
                                })
                .buildAs(LOCAL_BUILD_PIPELINE);
        pipelineRegistry.registerPipeline(localBuildPipeline);
    }

    private void initExecutor() {
        final Collection<ConfigExecutor> configs = new ArrayList<>();
        configExecutors.iterator().forEachRemaining(configs::add);
        initExecutor(configs);
    }

    private void initExecutor(final Collection<ConfigExecutor> configs) {
        executor = new PipelineExecutor(configs);
    }
}