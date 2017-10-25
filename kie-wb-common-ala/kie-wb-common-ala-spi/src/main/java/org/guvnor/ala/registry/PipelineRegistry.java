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

package org.guvnor.ala.registry;

import java.util.List;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.runtime.providers.ProviderType;

/**
 * Represents the PipelineRegistry where all the Pipelines are registered
 */
public interface PipelineRegistry {

    String PIPELINE_NAME_SORT = "name";

    /**
     * Registers a Pipeline
     * @param pipeline The Pipeline to be registered.
     */
    void registerPipeline(final Pipeline pipeline);

    /**
     * Registers a Pipeline by associating it to a provider type.
     * @param pipeline The Pipeline to be registered.
     * @param providerType The provider type for associating the Pipeline.
     */
    void registerPipeline(final Pipeline pipeline,
                          final ProviderType providerType);

    /**
     * Gets a Pipeline by Name.
     * @param pipelineId the pipeline id.
     * @return the pipeline corresponding to the pipeline id.
     */
    Pipeline getPipelineByName(final String pipelineId);

    /**
     * Gets all the registered Pipelines
     * @param page the page number
     * @param pageSize the page size
     * @param sort the sort column
     * @param sortOrder the sort order to use: true ascending, false descending
     * @return a list with all the available pipelines.
     */
    List<Pipeline> getPipelines(final int page,
                                final int pageSize,
                                final String sort,
                                final boolean sortOrder);

    /**
     * Gets all the registered Pipelines for a given provider type.
     * @param providerType A provider type name.
     * @param version the provider type version.
     * @param page the page number
     * @param pageSize the page size
     * @param sort the sort column
     * @param sortOrder the sort order to use: true ascending, false descending
     * @return the list of pipelines associated to the given provider.
     */
    List<Pipeline> getPipelines(final String providerType,
                                final String version,
                                final int page,
                                final int pageSize,
                                final String sort,
                                final boolean sortOrder);

    /**
     * Gets the associated provider type for a given pipeline.
     * @param pipelineId a pipeline id.
     * @return a provider type in cases where the pipeline was associated to one, null in any other case.
     */
    ProviderType getProviderType(final String pipelineId);
}