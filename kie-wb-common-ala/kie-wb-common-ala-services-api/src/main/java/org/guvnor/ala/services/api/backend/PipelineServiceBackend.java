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

package org.guvnor.ala.services.api.backend;

import java.util.List;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Pipeline Service Backend interface. It allows the creation and execution of pipelines.
 * Backend @Remote implementation to be used in CDI environments with Errai
 */
@Remote
public interface PipelineServiceBackend {

    /**
     * Get all the Pipeline Configurations registered in the service
     * @return a list with all the pipeline configurations
     */
    List<PipelineConfig> getPipelineConfigs(final Integer page,
                                            final Integer pageSize,
                                            final String sort,
                                            final boolean sortOrder) throws BusinessException;

    /**
     * Gets the Pipeline Configurations registered in the service and associated with the given provider type.
     * @param providerType a provider type registered in the system.
     * @return a list with the pipeline configurations associated with the provider type.
     */
    List<PipelineConfig> getPipelineConfigs(final ProviderType providerType,
                                            final Integer page,
                                            final Integer pageSize,
                                            final String sort,
                                            boolean sortOrder) throws BusinessException;

    /**
     * Gets the names of the Pipelines associated to the given provider type.
     * @param providerType a provider type registered in the system.
     * @return a list with the names of the Pipelines associated with the provider type.
     */
    List<String> getPipelineNames(final ProviderType providerType,
                                  final Integer page,
                                  final Integer pageSize,
                                  final String sort,
                                  final boolean sortOrder) throws BusinessException;

    /**
     * Registers a new Pipeline with the provided configuration
     * @param pipelineConfig the pipeline configuration.
     * @return String with the pipeline id
     * @see PipelineConfig
     */
    String newPipeline(final PipelineConfig pipelineConfig) throws BusinessException;

    /**
     * Registers a new Pipeline with the provided configuration and associates it to a provider type.
     * @param pipelineConfig the pipeline configuration.
     * @param providerType the provider type for associating the pipeline with.
     * @return String with the pipeline id.
     * @see PipelineConfig
     */
    String newPipeline(final PipelineConfig pipelineConfig,
                       final ProviderType providerType) throws BusinessException;

    /**
     * Execute a registered Pipeline
     * @param id of the pipeline to be executed
     * @param input to be used for the pipeline execution
     * @param async establishes the execution mode. true for asynchronous execution, false for synchronous execution.
     * @return the pipeline execution id.
     */
    String runPipeline(final String id,
                       final Input input,
                       final boolean async) throws BusinessException;

    /**
     * Stops a running pipeline execution.
     * @param executionId A pipeline execution id to stop. The pipeline execution id is typically returned by
     * the runPipeline method.
     * @throws BusinessException
     */
    void stopPipelineExecution(final String executionId) throws BusinessException;

    /**
     * Deletes a pipeline execution
     * @param executionId A pipeline execution id to delete. The pipeline execution id is typically returned by
     * the runPipeline method.
     * @throws BusinessException
     */
    void deletePipelineExecution(final String executionId) throws BusinessException;
}
