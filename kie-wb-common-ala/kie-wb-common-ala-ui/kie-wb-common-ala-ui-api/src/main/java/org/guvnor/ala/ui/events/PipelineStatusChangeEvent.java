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
import org.guvnor.ala.ui.model.PipelineStatus;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Event for notifying a status change in a pipeline.
 */
@Portable
public class PipelineStatusChangeEvent {

    private PipelineExecutionTraceKey pipelineExecutionTraceKey;

    private PipelineStatus status;

    public PipelineStatusChangeEvent(@MapsTo("pipelineExecutionTraceKey") final PipelineExecutionTraceKey pipelineExecutionTraceKey,
                                     @MapsTo("status") final PipelineStatus status) {
        this.pipelineExecutionTraceKey = pipelineExecutionTraceKey;
        this.status = status;
    }

    public PipelineExecutionTraceKey getPipelineExecutionTraceKey() {
        return pipelineExecutionTraceKey;
    }

    public PipelineStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PipelineStatusChangeEvent that = (PipelineStatusChangeEvent) o;

        if (pipelineExecutionTraceKey != null ? !pipelineExecutionTraceKey.equals(that.pipelineExecutionTraceKey) : that.pipelineExecutionTraceKey != null) {
            return false;
        }
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = pipelineExecutionTraceKey != null ? pipelineExecutionTraceKey.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = ~~result;
        return result;
    }
}