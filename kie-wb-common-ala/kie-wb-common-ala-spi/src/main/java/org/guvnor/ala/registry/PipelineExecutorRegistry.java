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

package org.guvnor.ala.registry;

import java.util.Collection;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.runtime.RuntimeId;

/**
 * Registry for storing the pipeline execution traces produced by pipelines launched by the PipelineExecutorTaskManager.
 */
public interface PipelineExecutorRegistry {

    /**
     * Registers a pipeline executor trace.
     * @param trace a pipeline executor trace for registering.
     */
    void register(final PipelineExecutorTrace trace);

    /**
     * Deregisters a pipeline executor trace.
     * @param pipelineExecutionId the identifier of the trace to deregister.
     */
    void deregister(final String pipelineExecutionId);

    /**
     * Gets a registered pipeline executor trace.
     * @param pipelineExecutionId the identifier of trace.
     * @return the registered pipeline executor trace, when exists, or null in any other case.
     */
    PipelineExecutorTrace getExecutorTrace(final String pipelineExecutionId);

    /**
     * Get the currently registered pipeline executor traces.
     * @return a list of pipeline executor traces.
     */
    Collection<PipelineExecutorTrace> getExecutorTraces();

    /**
     * Gets the pipeline executor trace that produced a runtime.
     * @param runtimeId a runtime for finding the associated pipeline executor trace.
     * @return the pipeline executor trace associated to the runtime if exits, null in any other case.
     */
    PipelineExecutorTrace getExecutorTrace(final RuntimeId runtimeId);
}