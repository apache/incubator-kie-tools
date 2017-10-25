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

package org.guvnor.ala.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.impl.ConfigBasedPipelineImpl;
import org.guvnor.ala.pipeline.impl.PipelineConfigImpl;

/**
 * Base implementation for the pipeline builders and pipelines instantiation.
 */
public final class PipelineFactory {

    private PipelineFactory() {
    }

    public static ConfigBasedPipelineBuilder newBuilder() {

        return new ConfigBasedPipelineBuilder() {

            private final List<PipelineConfigStage> configStages = new ArrayList<>();

            @Override
            public ConfigBasedPipelineBuilder addConfigStage(final String name,
                                                             final Config config) {
                configStages.add(new PipelineConfigStage(name,
                                                         config));
                return this;
            }

            @Override
            public ConfigBasedPipelineBuilder addConfigStage(final PipelineConfigStage configStage) {
                configStages.add(configStage);
                return this;
            }

            @Override
            public ConfigBasedPipeline buildAs(String name) {
                return new ConfigBasedPipelineImpl(new PipelineConfigImpl(name,
                                                                          configStages));
            }
        };
    }

    public static ConfigBasedPipeline newPipeline(final PipelineConfig pipelineConfig) {
        return new ConfigBasedPipelineImpl(pipelineConfig);
    }
}
