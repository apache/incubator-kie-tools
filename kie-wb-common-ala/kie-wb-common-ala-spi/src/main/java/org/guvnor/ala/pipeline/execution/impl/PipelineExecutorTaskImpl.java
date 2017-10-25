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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.guvnor.ala.pipeline.execution.PipelineExecutorError;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.pipeline.execution.RegistrableOutput;

import static org.guvnor.ala.pipeline.execution.PipelineExecutor.PIPELINE_EXECUTION_ID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PipelineExecutorTaskImpl
        implements PipelineExecutorTask,
                   Cloneable {

    private PipelineExecutorTaskDef taskDef;

    private String executionId;

    private PipelineExecutorTask.Status pipelineStatus = PipelineExecutorTask.Status.SCHEDULED;

    /**
     * Holds the execution status for the pipeline stages. The state status can change during the pipeline execution.
     */
    @JsonInclude
    private Map<String, Status> stageStatus = new HashMap<>();

    /**
     * Holds the execution error for the stages in case there were errors.
     */
    @JsonInclude
    private Map<String, PipelineExecutorError> stageError = new HashMap<>();

    /**
     * Holds the pipeline error in case the pipeline failed.
     */
    private PipelineExecutorError pipelineError;

    private RegistrableOutput output;

    public PipelineExecutorTaskImpl() {
        //no args constructor for marshalling/unmarshalling.
    }

    public PipelineExecutorTaskImpl(final PipelineExecutorTaskDef taskDef,
                                    final String executionId) {
        this.taskDef = taskDef;
        setId(executionId);
        taskDef.getStages().forEach(stage -> setStageStatus(stage,
                                                            Status.SCHEDULED));
    }

    @Override
    public PipelineExecutorTaskDef getTaskDef() {
        return taskDef;
    }

    @Override
    public String getId() {
        return executionId;
    }

    private void setId(final String executionId) {
        this.executionId = executionId;
        getTaskDef().getInput().put(PIPELINE_EXECUTION_ID,
                                    executionId);
    }

    public Status getPipelineStatus() {
        return pipelineStatus;
    }

    public void setPipelineStatus(final Status pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
    }

    public void setStageStatus(final String stage,
                               final Status status) {
        stageStatus.put(stage,
                        status);
    }

    public Status getStageStatus(final String stage) {
        return stageStatus.get(stage);
    }

    public void setStageError(final String stage,
                              final PipelineExecutorError error) {
        stageError.put(stage,
                       error);
    }

    public PipelineExecutorError getStageError(final String stage) {
        return stageError.get(stage);
    }

    public void setPipelineError(final PipelineExecutorError error) {
        this.pipelineError = error;
    }

    public PipelineExecutorError getPipelineError() {
        return pipelineError;
    }

    @Override
    public RegistrableOutput getOutput() {
        return output;
    }

    public void setOutput(final RegistrableOutput output) {
        this.output = output;
    }

    public void clearErrors() {
        stageError.clear();
        pipelineError = null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        PipelineExecutorTaskImpl clone = new PipelineExecutorTaskImpl(taskDef,
                                                                      executionId);

        clone.setPipelineStatus(this.getPipelineStatus());
        stageStatus.forEach(clone::setStageStatus);
        stageError.forEach(clone::setStageError);
        clone.setPipelineError(pipelineError);
        clone.setOutput(output);
        return clone;
    }
}