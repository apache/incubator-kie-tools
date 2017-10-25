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

package org.guvnor.ala.ui.backend.service;

import org.guvnor.ala.services.api.RuntimeQueryResultItem;
import org.guvnor.ala.ui.model.Pipeline;
import org.guvnor.ala.ui.model.PipelineError;
import org.guvnor.ala.ui.model.PipelineExecutionTrace;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.Runtime;
import org.guvnor.ala.ui.model.RuntimeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.Stage;

/**
 * Helper class for building RuntimeListItems.
 */
public class RuntimeListItemBuilder {

    private static final String SCHEDULED = "SCHEDULED";

    private static final String RUNNING = "RUNNING";

    private static final String FINISHED = "FINISHED";

    private static final String ERROR = "ERROR";

    private static final String STOPPED = "STOPPED";

    private RuntimeQueryResultItem item;

    private RuntimeListItemBuilder() {
    }

    public static RuntimeListItemBuilder newInstance() {
        return new RuntimeListItemBuilder();
    }

    public RuntimeListItemBuilder withItem(RuntimeQueryResultItem item) {
        this.item = item;
        return this;
    }

    public RuntimeListItem build() {

        RuntimeListItem result;
        PipelineExecutionTrace pipelineTrace;
        String runtimeName;

        if (item.getRuntimeName() != null) {
            runtimeName = item.getRuntimeName();
        } else {
            runtimeName = item.getRuntimeId();
        }

        if (item.getPipelineExecutionId() != null) {
            final Pipeline pipeline = new Pipeline(new PipelineKey(item.getPipelineId()));
            pipelineTrace = new PipelineExecutionTrace(new PipelineExecutionTraceKey(item.getPipelineExecutionId()));
            pipelineTrace.setPipelineStatus(transformToPipelineStatus(item.getPipelineStatus()));
            pipelineTrace.setPipelineError(new PipelineError(item.getPipelineError(),
                                                             item.getPipelineErrorDetail()));
            item.getPipelineStageItems().getItems()
                    .forEach(stage -> {
                                 pipeline.addStage(new Stage(pipeline.getKey(),
                                                             stage.getName()));
                                 pipelineTrace.setStageStatus(stage.getName(),
                                                              transformToPipelineStatus(stage.getStatus()));
                                 pipelineTrace.setStageError(stage.getName(),
                                                             new PipelineError(stage.getStageError(),
                                                                               stage.getStageErrorDetail()));
                             }
                    );
            pipelineTrace.setPipeline(pipeline);
        } else {
            pipelineTrace = null;
        }

        if (item.getRuntimeId() != null) {
            final Runtime runtime = new Runtime(new RuntimeKey(new ProviderKey(new ProviderTypeKey(item.getProviderTypeName(),
                                                                                                   item.getProviderVersion()),
                                                                               item.getProviderId()),
                                                               item.getRuntimeId()),
                                                item.getRuntimeStatus(),
                                                item.getRuntimeEndpoint(),
                                                item.getStartedAt());
            runtime.setPipelineTrace(pipelineTrace);
            result = new RuntimeListItem(runtimeName,
                                         runtime);
        } else {
            result = new RuntimeListItem(runtimeName,
                                         pipelineTrace);
        }
        return result;
    }

    private PipelineStatus transformToPipelineStatus(String status) {
        if (status == null) {
            return null;
        } else {
            switch (status) {
                case SCHEDULED:
                    return PipelineStatus.SCHEDULED;
                case RUNNING:
                    return PipelineStatus.RUNNING;
                case FINISHED:
                    return PipelineStatus.FINISHED;
                case ERROR:
                    return PipelineStatus.ERROR;
                case STOPPED:
                    return PipelineStatus.STOPPED;
            }
            return null;
        }
    }
}
