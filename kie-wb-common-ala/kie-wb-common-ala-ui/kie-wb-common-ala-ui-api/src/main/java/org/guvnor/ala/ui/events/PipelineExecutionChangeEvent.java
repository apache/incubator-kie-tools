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

package org.guvnor.ala.ui.events;

import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Event for notifying changes in a pipeline execution.
 */
@Portable
public class PipelineExecutionChangeEvent {

    PipelineExecutionChange change;

    private PipelineExecutionTraceKey pipelineExecutionTraceKey;

    public PipelineExecutionChangeEvent(@MapsTo("change") final PipelineExecutionChange change,
                                        @MapsTo("pipelineExecutionTraceKey") final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        this.change = change;
        this.pipelineExecutionTraceKey = pipelineExecutionTraceKey;
    }

    public PipelineExecutionChange getChange() {
        return change;
    }

    public PipelineExecutionTraceKey getPipelineExecutionTraceKey() {
        return pipelineExecutionTraceKey;
    }

    public boolean isStop() {
        return change == PipelineExecutionChange.STOPPED;
    }

    public boolean isDelete() {
        return change == PipelineExecutionChange.DELETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PipelineExecutionChangeEvent that = (PipelineExecutionChangeEvent) o;

        if (change != that.change) {
            return false;
        }
        return pipelineExecutionTraceKey != null ? pipelineExecutionTraceKey.equals(that.pipelineExecutionTraceKey) : that.pipelineExecutionTraceKey == null;
    }

    @Override
    public int hashCode() {
        int result = change != null ? change.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (pipelineExecutionTraceKey != null ? pipelineExecutionTraceKey.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
