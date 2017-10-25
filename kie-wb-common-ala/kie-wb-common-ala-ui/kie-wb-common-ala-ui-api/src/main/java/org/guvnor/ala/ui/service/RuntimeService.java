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

package org.guvnor.ala.ui.service;

import java.util.Collection;
import java.util.Map;

import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.RuntimeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RuntimeService {

    /**
     * Gets the information about the runtimes associated to a given provider.
     * @param providerKey a provider que for getting the runtimes.
     * @return a list of RuntimeListItem.
     */
    Collection<RuntimeListItem> getRuntimeItems(final ProviderKey providerKey);

    /**
     * Gets the runtime information for a given pipeline execution.
     * @param pipelineExecutionTraceKey the identifier for a pipeline execution.
     * @return the RuntimeListItem associated to the pipeline execution when exists, false in any other case.
     */
    RuntimeListItem getRuntimeItem(final PipelineExecutionTraceKey pipelineExecutionTraceKey);

    /**
     * Gets the runtime information for a given runtime.
     * @param runtimeKey the identifier for a runtime.
     * @return the RuntimeListItem associated to the runtime when exists, false in any other case.
     */
    RuntimeListItem getRuntimeItem(final RuntimeKey runtimeKey);

    /**
     * Gests the pipeline names for the pipelines associated to a given provider type.
     * @param providerTypeKey a provider type key.
     * @return a list with the keys of the associated pipelines.
     */
    Collection<PipelineKey> getPipelines(final ProviderTypeKey providerTypeKey);

    /**
     * Creates a runtime by associating it with a given provider. A provider may have multiple runtimes associated.
     * @param providerKey the provider key for creating the runtime.
     * @param runtimeName a name for the runtime to be created.
     * @param pipelineKey the key of a pipeline to use for performing all the required operations for building the
     * runtime.
     * @return returns the pipeline execution id.
     */
    PipelineExecutionTraceKey createRuntime(final ProviderKey providerKey,
                                            final String runtimeName,
                                            final PipelineKey pipelineKey,
                                            final Map<String, String> params);

    /**
     * Stops a running pipeline execution.
     * @param pipelineExecutionTraceKey identifier for the pipeline execution.
     */
    void stopPipelineExecution(final PipelineExecutionTraceKey pipelineExecutionTraceKey);

    /**
     * Deletes a pipeline execution trace from the system.
     * @param pipelineExecutionTraceKey identifier of the pipeline execution.
     */
    void deletePipelineExecution(final PipelineExecutionTraceKey pipelineExecutionTraceKey);

    /**
     * Stops a runtime.
     * @param runtimeKey the key of the runtime to stop.
     */
    void stopRuntime(final RuntimeKey runtimeKey);

    /**
     * Starts a runtime.
     * @param runtimeKey the key of the runtime to start.
     */
    void startRuntime(final RuntimeKey runtimeKey);

    /**
     * Deletes a runtime.
     * @param runtimeKey the key of the runtime to delete.
     * @param forced indicates if the runtime must be deleted from the guvnor-ala registries independently of the
     * connectivity with the external provider. e.g. if it was not possible to connect an external WF where the runtime
     * is running.
     */
    void deleteRuntime(final RuntimeKey runtimeKey,
                       final boolean forced);
}