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

package org.guvnor.ala.services.rest.tests;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.SystemPipelineDescriptor;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.wildfly.model.WildflyProviderType;

/**
 * Dummy class used by the RestPipelineImplTest for producing system pipelines that will be registered during startup.
 */
@ApplicationScoped
public class MockSystemPipelines {

    public static final String SYSTEM_PIPELINE1 = "system-pipeline1";

    public static final String SYSTEM_PIPELINE2 = "system-pipeline2";

    public MockSystemPipelines() {
        //empty constructor for Weld proxying
    }

    /**
     * Dummy pipeline for testing the SystemPipelines startup registration.
     */
    @Produces
    public SystemPipelineDescriptor getSystemPipeline1() {
        return new SystemPipelineDescriptor() {

            @Override
            public Optional<ProviderType> getProviderType() {
                //assigned to the Wildfly provider.
                return Optional.of(WildflyProviderType.instance());
            }

            @Override
            public Pipeline getPipeline() {
                return createDummyPipeline(SYSTEM_PIPELINE1);
            }
        };
    }

    /**
     * Dummy pipeline for testing the SystemPipelines startup registration.
     */
    @Produces
    public SystemPipelineDescriptor getSystemPipeline2() {
        return new SystemPipelineDescriptor() {

            @Override
            public Optional<ProviderType> getProviderType() {
                //not assigned to any provider.
                return Optional.empty();
            }

            @Override
            public Pipeline getPipeline() {
                return createDummyPipeline(SYSTEM_PIPELINE2);
            }
        };
    }

    private Pipeline createDummyPipeline(String pipelineName) {
        final GitConfig gitConfig = new GitConfigImpl();
        final Pipeline pipeline = PipelineFactory
                .newBuilder()
                .addConfigStage("GigConfig",
                           gitConfig)
                .buildAs(pipelineName);

        return pipeline;
    }
}
