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

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;

/**
 * Base Pipeline implementation
 */
public abstract class BasePipeline
        implements Pipeline {

    private String name;
    private List<Stage> stages;

    public BasePipeline() {
    }

    public BasePipeline(String name,
                        List<Stage> stages) {
        this.name = name;
        this.stages = stages;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Stage> getStages() {
        return stages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }
}
