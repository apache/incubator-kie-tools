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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;

/**
 * Represents a pipeline execution recording.
 */
public class PipelineExecutorTraceImpl
        implements PipelineExecutorTrace {

    /**
     * The pipeline task that was executed in this pipeline run.
     */
    private PipelineExecutorTask task;

    public PipelineExecutorTraceImpl() {
        //no args constructor for marshalling/unmarshalling.
    }

    public PipelineExecutorTraceImpl(PipelineExecutorTask task) {
        this.task = task;
    }

    /**
     * Shortcut to the task id.
     * @return returns the id of the internal task.
     */
    @JsonIgnore
    public String getTaskId() {
        return getTask().getId();
    }

    @JsonIgnore
    @Override
    public String getPipelineId() {
        return getTask().getTaskDef().getPipeline();
    }

    public PipelineExecutorTask getTask() {
        return task;
    }
}
