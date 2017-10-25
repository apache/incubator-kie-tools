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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a pipeline execution recording.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface PipelineExecutorTrace {

    /**
     * Shortcut to the task id related with this trace.
     * @return returns the id of the internal task.
     */
    String getTaskId();

    /**
     * Shortcut to the pipeline id related with this trace.
     * @return returns the pipeline id of the pipeline related with this trace.
     */
    String getPipelineId();

    /**
     * Gets the task that was executed.
     * @return returns the internal task.
     */
    PipelineExecutorTask getTask();
}
