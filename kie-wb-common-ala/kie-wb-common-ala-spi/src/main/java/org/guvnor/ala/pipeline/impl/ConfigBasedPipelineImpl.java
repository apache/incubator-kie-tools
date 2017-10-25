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

package org.guvnor.ala.pipeline.impl;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.pipeline.ConfigBasedPipeline;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.pipeline.PipelineConfigStage;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.StageUtil;

public class ConfigBasedPipelineImpl
        extends BasePipeline
        implements ConfigBasedPipeline {

    private PipelineConfig config;

    public ConfigBasedPipelineImpl(PipelineConfig config) {
        setName(config.getName());
        setStages(buildStages(config));
        this.config = config;
    }

    @Override
    public PipelineConfig getConfig() {
        return config;
    }

    private List<Stage> buildStages(PipelineConfig config) {
        List<Stage> stages = new ArrayList<>();
        for (final PipelineConfigStage configStage : config.getConfigStages()) {
            stages.add(StageUtil.config(configStage.getName(),
                                        f -> configStage.getConfig()));
        }
        return stages;
    }
}
