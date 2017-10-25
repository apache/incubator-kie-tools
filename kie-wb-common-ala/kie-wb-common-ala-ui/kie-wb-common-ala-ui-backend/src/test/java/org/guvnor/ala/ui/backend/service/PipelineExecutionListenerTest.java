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

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.ui.events.PipelineStatusChangeEvent;
import org.guvnor.ala.ui.events.StageStatusChangeEvent;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineExecutionListenerTest {

    private static final String EXECUTION_ID = "EXECUTION_ID";

    private static final String STAGE_NAME = "STAGE_NAME";

    @Mock
    private EventSourceMock<PipelineStatusChangeEvent> pipelineStatusChangeEvent;

    @Mock
    private EventSourceMock<StageStatusChangeEvent> stageStatusChangeEvent;

    private PipelineExecutionListener listener;

    private PipelineExecutionTraceKey traceKey;

    private Pipeline pipeline;

    private Stage stage;

    private Throwable throwable;

    @Before
    public void setUp() {
        traceKey = new PipelineExecutionTraceKey(EXECUTION_ID);
        pipeline = mock(Pipeline.class);

        stage = mock(Stage.class);
        when(stage.getName()).thenReturn(STAGE_NAME);

        throwable = mock(Throwable.class);

        listener = new PipelineExecutionListener(pipelineStatusChangeEvent,
                                                 stageStatusChangeEvent);
    }

    @Test
    public void testBeforePipelineExecution() {
        listener.beforePipelineExecution(new BeforePipelineExecutionEvent(EXECUTION_ID,
                                                                          pipeline));
        verify(pipelineStatusChangeEvent,
               times(1)).fire(new PipelineStatusChangeEvent(traceKey,
                                                            PipelineStatus.RUNNING));
    }

    @Test
    public void testAfterPipelineExecution() {
        listener.afterPipelineExecution(new AfterPipelineExecutionEvent(EXECUTION_ID,
                                                                        pipeline));
        verify(pipelineStatusChangeEvent,
               times(1)).fire(new PipelineStatusChangeEvent(traceKey,
                                                            PipelineStatus.FINISHED));
    }

    @Test
    public void testBeforeStageExecution() {
        listener.beforeStageExecution(new BeforeStageExecutionEvent(EXECUTION_ID,
                                                                    pipeline,
                                                                    stage));
        verify(stageStatusChangeEvent,
               times(1)).fire(new StageStatusChangeEvent(traceKey,
                                                         STAGE_NAME,
                                                         PipelineStatus.RUNNING));
    }

    @Test
    public void testOnStageError() {
        listener.onStageError(new OnErrorStageExecutionEvent(EXECUTION_ID,
                                                             pipeline,
                                                             stage,
                                                             throwable));
        verify(stageStatusChangeEvent,
               times(1)).fire(new StageStatusChangeEvent(traceKey,
                                                         STAGE_NAME,
                                                         PipelineStatus.ERROR));
    }

    @Test
    public void testAfterStageExecution() {
        listener.afterStageExecution(new AfterStageExecutionEvent(EXECUTION_ID,
                                                                  pipeline,
                                                                  stage));
        verify(stageStatusChangeEvent,
               times(1)).fire(new StageStatusChangeEvent(traceKey,
                                                         STAGE_NAME,
                                                         PipelineStatus.FINISHED));
    }

    @Test
    public void testOnPipelineError() {
        listener.onPipelineError(new OnErrorPipelineExecutionEvent(EXECUTION_ID,
                                                                   pipeline,
                                                                   stage,
                                                                   throwable));
        verify(pipelineStatusChangeEvent,
               times(1)).fire(new PipelineStatusChangeEvent(traceKey,
                                                            PipelineStatus.ERROR));
    }
}
