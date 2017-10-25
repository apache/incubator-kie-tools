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

import java.util.List;

import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.pipeline.PipelineConfigStage;

public class PipelineConfigImpl
        implements PipelineConfig {

    private String name;
    private List<PipelineConfigStage> configStages;

    public PipelineConfigImpl() {
    }

    public PipelineConfigImpl(String name,
                              List<PipelineConfigStage> configStages) {
        this.name = name;
        this.configStages = configStages;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<PipelineConfigStage> getConfigStages() {
        return configStages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConfigStages(List<PipelineConfigStage> configStages) {
        this.configStages = configStages;
    }

    @Override
    public String toString() {
        return "PipelineConfigImpl{" +
                "name='" + name + '\'' +
                ", configStages=" + configStages +
                '}';
    }
}
