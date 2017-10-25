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

package org.guvnor.ala.ui.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * This class holds the information about a pipeline execution, typically the executed pipeline, the status of
 * the execution, potential errors, etc.
 */
@Portable
public class PipelineExecutionTrace
        extends AbstractHasKeyObject<PipelineExecutionTraceKey> {

    private Pipeline pipeline;

    private PipelineStatus pipelineStatus;

    private PipelineError pipelineError;

    private Map<String, PipelineStatus> stageStatusMap = new HashMap<>();

    private Map<String, PipelineError> stageErrorMap = new HashMap<>();

    public PipelineExecutionTrace(@MapsTo("key") final PipelineExecutionTraceKey key,
                                  @MapsTo("pipeline") final Pipeline pipeline,
                                  @MapsTo("pipelineStatus") final PipelineStatus pipelineStatus,
                                  @MapsTo("pipelineError") final PipelineError pipelineError,
                                  @MapsTo("stageStatusMap") final Map<String, PipelineStatus> stageStatusMap,
                                  @MapsTo("stageErrorMap") final Map<String, PipelineError> stageErrorMap) {
        super(key);
        this.pipeline = pipeline;
        this.pipelineStatus = pipelineStatus;
        this.pipelineError = pipelineError;
        this.stageStatusMap = stageStatusMap;
        this.stageErrorMap = stageErrorMap;
    }

    public PipelineExecutionTrace(PipelineExecutionTraceKey key) {
        super(key);
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public PipelineStatus getPipelineStatus() {
        return pipelineStatus;
    }

    public void setPipelineStatus(PipelineStatus pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
    }

    public PipelineError getPipelineError() {
        return pipelineError;
    }

    public void setPipelineError(PipelineError pipelineError) {
        this.pipelineError = pipelineError;
    }

    public PipelineStatus getStageStatus(String stage) {
        return stageStatusMap.get(stage);
    }

    public void setStageStatus(String stage,
                               PipelineStatus stageStatus) {
        stageStatusMap.put(stage,
                           stageStatus);
    }

    public PipelineError getStageError(String stage) {
        return stageErrorMap.get(stage);
    }

    public void setStageError(String stage,
                              PipelineError error) {
        stageErrorMap.put(stage,
                          error);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PipelineExecutionTrace)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PipelineExecutionTrace trace = (PipelineExecutionTrace) o;

        if (pipeline != null ? !pipeline.equals(trace.pipeline) : trace.pipeline != null) {
            return false;
        }
        if (pipelineStatus != trace.pipelineStatus) {
            return false;
        }
        if (pipelineError != null ? !pipelineError.equals(trace.pipelineError) : trace.pipelineError != null) {
            return false;
        }
        if (stageStatusMap != null ? !stageStatusMap.equals(trace.stageStatusMap) : trace.stageStatusMap != null) {
            return false;
        }
        return stageErrorMap != null ? stageErrorMap.equals(trace.stageErrorMap) : trace.stageErrorMap == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (pipeline != null ? pipeline.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pipelineStatus != null ? pipelineStatus.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (pipelineError != null ? pipelineError.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (stageStatusMap != null ? stageStatusMap.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (stageErrorMap != null ? stageErrorMap.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
