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

package org.guvnor.ala.services.api;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.exceptions.BusinessException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Pipeline Service interface. It allows us to create and run new Pipelines
 * URL: {app-context}/pipelines/
 */
@Path("pipelines")
public interface PipelineService {

    /**
     * Gets all the Pipeline Configurations registered in the service.
     * @return a PipelineConfigsList with the list of pipeline configurations.
     * @see PipelineConfigsList
     */
    @GET
    @Produces(value = APPLICATION_JSON)
    @Consumes(value = APPLICATION_JSON)
    PipelineConfigsList getPipelineConfigs(@QueryParam("page") @DefaultValue("0") Integer page,
                                           @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
                                           @QueryParam("sort") String sort,
                                           @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder)
            throws BusinessException;

    /**
     * Gets the Pipeline Configurations registered in the service and associated to the given provider type.
     * @param providerTypeName a provider type name registered in the system.
     * @param providerTypeVersion the provider type version corresponding to the provider type.
     * @return a PipelineConfigsList with the pipeline configurations associated with the provider type.
     * @see PipelineConfigsList
     */
    @GET
    @Path("providertype")
    @Produces(value = APPLICATION_JSON)
    @Consumes(value = APPLICATION_JSON)
    PipelineConfigsList getPipelineConfigs(@QueryParam("providerTypeName") String providerTypeName,
                                           @QueryParam("providerTypeVersion") String providerTypeVersion,
                                           @QueryParam("page") @DefaultValue("0") Integer page,
                                           @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
                                           @QueryParam("sort") String sort,
                                           @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder)
            throws BusinessException;

    /**
     * Gets the names of the Pipelines associated to the given provider type.
     * @param providerTypeName a provider type name registered in the system.
     * @param providerTypeVersion the provider type version corresponding to the provider type.
     * @return a list with the names of the Pipelines associated with the provider type.
     */
    @GET
    @Path("providertype/names")
    @Produces(value = APPLICATION_JSON)
    @Consumes(value = APPLICATION_JSON)
    List<String> getPipelineNames(@QueryParam("providerTypeName") String providerTypeName,
                                  @QueryParam("providerTypeVersion") String providerTypeVersion,
                                  @QueryParam("page") @DefaultValue("0") Integer page,
                                  @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
                                  @QueryParam("sort") String sort,
                                  @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder)
            throws BusinessException;

    /**
     * Registers a new Pipeline with the provided configuration.
     * @param config the pipeline configuration.
     * @return the pipeline id.
     * @see PipelineConfig
     */
    @POST
    @Consumes(value = APPLICATION_JSON)
    @Produces(value = APPLICATION_JSON)
    String newPipeline(@NotNull PipelineConfig config) throws BusinessException;

    /**
     * Registers a new Pipeline with the provided configuration and associates it with the given provider type.
     * @param config the pipeline configuration.
     * @param providerType A provider type for associating the pipeline.
     * @return String with the pipeline id
     * @see PipelineConfig
     */
    @POST
    @Path("providertype")
    @Consumes(value = APPLICATION_JSON)
    @Produces(value = APPLICATION_JSON)
    String newPipeline(@NotNull PipelineConfig config,
                       @NotNull ProviderType providerType) throws BusinessException;

    /**
     * Run/Execute a registered Pipeline.
     * @param pipelineId of the pipeline to be executed.
     * @param input Input values to be used for the pipeline execution.
     * @param async establishes the execution mode. true for asynchronous execution, false for synchronous execution.
     * @return the pipeline execution id.
     */
    @POST
    @Consumes(value = APPLICATION_JSON)
    @Produces(value = APPLICATION_JSON)
    @Path("execution/{pipelineId}/run")
    String runPipeline(@PathParam("pipelineId") String pipelineId,
                       @NotNull Input input,
                       @NotNull boolean async) throws BusinessException;

    /**
     * Stops a running pipeline execution.
     * @param executionId A pipeline execution id to stop. The pipeline execution id is typically returned by
     * the runPipeline method.
     * @throws BusinessException
     */
    @PUT
    @Path("execution/{executionId}/stop")
    void stopPipelineExecution(@PathParam("executionId") String executionId) throws BusinessException;

    /**
     * Deletes a pipeline execution
     * @param executionId A pipeline execution id to delete. The pipeline execution id is typically returned by
     * the runPipeline method.
     * @throws BusinessException
     */
    @DELETE
    @Path("execution/{executionId}")
    void deletePipelineExecution(@PathParam("executionId") String executionId) throws BusinessException;
}
