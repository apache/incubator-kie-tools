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

package org.guvnor.ala.services.api.itemlist;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.services.api.ItemList;

public class PipelineConfigsList implements ItemList<PipelineConfig> {

    private PipelineConfig[] pipelineConfigs;

    /*
     * No-args constructor for enabling marshalling to work, please do not remove. 
    */
    public PipelineConfigsList() {
    }

    public PipelineConfigsList(List<PipelineConfig> pipelineConfigs) {
        this.pipelineConfigs = pipelineConfigs.toArray(new PipelineConfig[pipelineConfigs.size()]);
    }

    public PipelineConfigsList(PipelineConfig[] pipelineConfigs) {
        this.pipelineConfigs = pipelineConfigs;
    }

    public PipelineConfig[] getPipelineConfigs() {
        return pipelineConfigs;
    }

    public void setPipelineConfigs(PipelineConfig[] pipelineConfigs) {
        this.pipelineConfigs = pipelineConfigs;
    }

    @Override
    @JsonIgnore
    public List<PipelineConfig> getItems() {
        if (pipelineConfigs == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(pipelineConfigs);
    }
}
