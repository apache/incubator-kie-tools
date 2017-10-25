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

package org.guvnor.ala.pipeline.execution;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * This class defines the information for performing the execution of a Pipeline by using the PipelineExecutorTaskManager.
 * @see PipelineExecutorTaskManager
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface PipelineExecutorTaskDef {

    /**
     * @return The pipeline name that will be executed.
     */
    String getPipeline();

    /**
     * @return The the pipeline stage names.
     */
    List<String> getStages();

    /**
     * @return The pipeline input that will be used for the pipeline execution.
     */
    Input getInput();

    /**
     * @return The provider that will be used by this pipeline execution when this information is known, null in any
     * other case.
     */
    ProviderId getProviderId();

    /**
     * @return The provider type that will be used by this pipeline execution when this information is known, null in
     * any other case.
     */
    ProviderType getProviderType();
}
