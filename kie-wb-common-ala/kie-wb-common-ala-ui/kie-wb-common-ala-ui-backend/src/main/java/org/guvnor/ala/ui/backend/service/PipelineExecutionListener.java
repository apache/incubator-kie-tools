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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.ui.events.PipelineStatusChangeEvent;
import org.guvnor.ala.ui.events.StageStatusChangeEvent;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineStatus;

/**
 * Observes the events produced by the pipelines launched by the PipelineExecutorTaskManager and raises the required
 * events to the UI or other interested parties.
 */
public class PipelineExecutionListener
        implements PipelineEventListener {

    private Event<PipelineStatusChangeEvent> pipelineStatusChangeEvent;

    private Event<StageStatusChangeEvent> stageStatusChangeEvent;

    public PipelineExecutionListener() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public PipelineExecutionListener(final Event<PipelineStatusChangeEvent> pipelineStatusChangeEvent,
                                     final Event<StageStatusChangeEvent> stageStatusChangeEvent) {
        this.pipelineStatusChangeEvent = pipelineStatusChangeEvent;
        this.stageStatusChangeEvent = stageStatusChangeEvent;
    }

    @Override
    public void beforePipelineExecution(BeforePipelineExecutionEvent bpee) {
        pipelineStatusChangeEvent.fire(new PipelineStatusChangeEvent(new PipelineExecutionTraceKey(bpee.getExecutionId()),
                                                                     PipelineStatus.RUNNING));
    }

    @Override
    public void afterPipelineExecution(AfterPipelineExecutionEvent apee) {
        pipelineStatusChangeEvent.fire(new PipelineStatusChangeEvent(new PipelineExecutionTraceKey(apee.getExecutionId()),
                                                                     PipelineStatus.FINISHED));
    }

    @Override
    public void beforeStageExecution(BeforeStageExecutionEvent bsee) {
        stageStatusChangeEvent.fire(new StageStatusChangeEvent(new PipelineExecutionTraceKey(bsee.getExecutionId()),
                                                               bsee.getStage().getName(),
                                                               PipelineStatus.RUNNING));
    }

    @Override
    public void onStageError(OnErrorStageExecutionEvent oesee) {
        stageStatusChangeEvent.fire(new StageStatusChangeEvent(new PipelineExecutionTraceKey(oesee.getExecutionId()),
                                                               oesee.getStage().getName(),
                                                               PipelineStatus.ERROR));
    }

    @Override
    public void afterStageExecution(AfterStageExecutionEvent asee) {
        stageStatusChangeEvent.fire(new StageStatusChangeEvent(new PipelineExecutionTraceKey(asee.getExecutionId()),
                                                               asee.getStage().getName(),
                                                               PipelineStatus.FINISHED));
    }

    @Override
    public void onPipelineError(OnErrorPipelineExecutionEvent oepee) {
        pipelineStatusChangeEvent.fire(new PipelineStatusChangeEvent(new PipelineExecutionTraceKey(oepee.getExecutionId()),
                                                                     PipelineStatus.ERROR));
    }
}
