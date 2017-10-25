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

package org.guvnor.ala.pipeline.execution.impl;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

public class PipelineExecutorTaskDefImpl
        implements PipelineExecutorTaskDef {

    private String pipeline;

    private List<String> stages = new ArrayList<>();

    private Input input;

    private ProviderId providerId;

    private ProviderType providerType;

    public PipelineExecutorTaskDefImpl() {
        //no args constructor for marshalling/unmarshalling.
    }

    private PipelineExecutorTaskDefImpl(final Pipeline pipeline) {
        this.pipeline = pipeline.getName();
        pipeline.getStages().forEach(stage -> stages.add(stage.getName()));
    }

    public PipelineExecutorTaskDefImpl(final Pipeline pipeline,
                                       final Input input) {
        this(pipeline);
        this.input = input;
    }

    public PipelineExecutorTaskDefImpl(final Pipeline pipeline,
                                       final Input input,
                                       final ProviderId providerId) {
        this(pipeline);
        this.input = input;
        this.providerId = providerId;
    }

    public PipelineExecutorTaskDefImpl(final Pipeline pipeline,
                                       final Input input,
                                       final ProviderType providerType) {
        this(pipeline);
        this.input = input;
        this.providerType = providerType;
    }

    @Override
    public String getPipeline() {
        return pipeline;
    }

    @Override
    public List<String> getStages() {
        return stages;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    @Override
    public ProviderType getProviderType() {
        return providerId != null ? providerId.getProviderType() : providerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PipelineExecutorTaskDefImpl taskDef = (PipelineExecutorTaskDefImpl) o;

        if (pipeline != null ? !pipeline.equals(taskDef.pipeline) : taskDef.pipeline != null) {
            return false;
        }
        if (stages != null ? !stages.equals(taskDef.stages) : taskDef.stages != null) {
            return false;
        }
        if (input != null ? !input.equals(taskDef.input) : taskDef.input != null) {
            return false;
        }
        if (providerId != null ? !providerId.equals(taskDef.providerId) : taskDef.providerId != null) {
            return false;
        }
        return providerType != null ? providerType.equals(taskDef.providerType) : taskDef.providerType == null;
    }

    @Override
    public int hashCode() {
        int result = pipeline != null ? pipeline.hashCode() : 0;
        result = 31 * result + (stages != null ? stages.hashCode() : 0);
        result = 31 * result + (input != null ? input.hashCode() : 0);
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        result = 31 * result + (providerType != null ? providerType.hashCode() : 0);
        return result;
    }
}