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

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.events.AfterPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.AfterStageExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforePipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.BeforeStageExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorPipelineExecutionEvent;
import org.guvnor.ala.pipeline.events.OnErrorStageExecutionEvent;
import org.guvnor.ala.pipeline.events.PipelineEvent;
import org.guvnor.ala.pipeline.execution.PipelineExecutorError;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineExecutorTaskManagerImplEventsTest
        extends PipelineExecutorTaskManagerImplTestBase {

    private PipelineExecutorTaskImpl task;

    private PipelineExecutorTaskManagerImpl.TaskEntry taskEntry;

    private Stage stage;

    private Throwable error;

    protected ArgumentCaptor<PipelineExecutorError> pipelineExecutorErrorCaptor;

    @Before
    public void setUp() {
        super.setUp();

        error = mock(Throwable.class);
        when(error.getMessage()).thenReturn(ERROR_MESSAGE);
        pipelineExecutorErrorCaptor = ArgumentCaptor.forClass(PipelineExecutorError.class);

        pipeline = mock(Pipeline.class);
        stages = mockStages(PIPELINE_STAGES_SIZE);
        when(pipeline.getStages()).thenReturn(stages);
        when(pipeline.getName()).thenReturn(PIPELINE_ID);
        when(pipelineRegistry.getPipelineByName(PIPELINE_ID)).thenReturn(pipeline);

        //pick an arbitrary stage for events testing.
        stage = stages.get(0);

        taskDef = mock(PipelineExecutorTaskDef.class);
        input = mock(Input.class);
        when(taskDef.getInput()).thenReturn(input);
        when(taskDef.getPipeline()).thenReturn(PIPELINE_ID);

        task = spy(taskManagerHelper.createTask(taskDef,
                                                TASK_ID));

        taskEntry = mock(PipelineExecutorTaskManagerImpl.TaskEntry.class);
        when(taskEntry.getTask()).thenReturn(task);

        //emulate there's a running task.
        taskManager.currentTasks.put(TASK_ID,
                                     taskEntry);

        taskManager.init();
    }

    @Test
    public void testBeforePipelineExecutionEventForAsyncTask() {
        testBeforePipelineExecutionEvent(true);
    }

    @Test
    public void testBeforePipelineExecutionEventForSyncTask() {
        testBeforePipelineExecutionEvent(false);
    }

    private void testBeforePipelineExecutionEvent(boolean async) {
        when(taskEntry.isAsync()).thenReturn(async);

        BeforePipelineExecutionEvent event = new BeforePipelineExecutionEvent(TASK_ID,
                                                                              pipeline);
        taskManager.localListener.beforePipelineExecution(event);

        verify(task,
               times(1)).setPipelineStatus(PipelineExecutorTask.Status.RUNNING);

        verifyExecutorRegistryUpdated(async);

        verifyExternalListenersNotified(event);
    }

    @Test
    public void testAfterPipelineExecutionEventForAsyncTask() {
        testAfterPipelineExecutionEvent(true);
    }

    @Test
    public void testAfterPipelineExecutionEventForSyncTask() {
        testAfterPipelineExecutionEvent(true);
    }

    private void testAfterPipelineExecutionEvent(boolean async) {
        when(taskEntry.isAsync()).thenReturn(async);

        AfterPipelineExecutionEvent event = new AfterPipelineExecutionEvent(TASK_ID,
                                                                            pipeline);
        taskManager.localListener.afterPipelineExecution(event);

        verify(task,
               times(1)).setPipelineStatus(PipelineExecutorTask.Status.FINISHED);

        verifyExecutorRegistryUpdated(async);

        verifyExternalListenersNotified(event);
    }

    @Test
    public void testBeforeStageExecutionEventForAsyncTask() {
        testBeforeStageExecutionEvent(true);
    }

    @Test
    public void testBeforeStageExecutionEventForSyncTask() {
        testBeforeStageExecutionEvent(false);
    }

    private void testBeforeStageExecutionEvent(boolean async) {
        when(taskEntry.isAsync()).thenReturn(async);

        BeforeStageExecutionEvent event = new BeforeStageExecutionEvent(TASK_ID,
                                                                        pipeline,
                                                                        stage);
        taskManager.localListener.beforeStageExecution(event);

        verify(task,
               times(1)).setStageStatus(stage.getName(),
                                        PipelineExecutorTask.Status.RUNNING);

        verifyExecutorRegistryUpdated(async);

        verifyExternalListenersNotified(event);
    }

    @Test
    public void testOnStageErrorExecutionEventForAsyncTask() {
        testOnStageErrorExecutionEvent(true);
    }

    @Test
    public void testOnStageErrorExecutionEventForSyncTask() {
        testOnStageErrorExecutionEvent(false);
    }

    private void testOnStageErrorExecutionEvent(boolean async) {
        when(taskEntry.isAsync()).thenReturn(async);

        OnErrorStageExecutionEvent event = new OnErrorStageExecutionEvent(TASK_ID,
                                                                          pipeline,
                                                                          stage,
                                                                          error);
        taskManager.localListener.onStageError(event);

        verify(task,
               times(1)).setPipelineStatus(PipelineExecutorTask.Status.ERROR);
        verify(task,
               times(1)).setStageStatus(stage.getName(),
                                        PipelineExecutorTask.Status.ERROR);
        verify(task,
               times(1)).setStageError(eq(stage.getName()),
                                       pipelineExecutorErrorCaptor.capture());

        assertEquals(new PipelineExecutorError(ERROR_MESSAGE,
                                               error),
                     pipelineExecutorErrorCaptor.getValue());

        verifyExecutorRegistryUpdated(async);

        verifyExternalListenersNotified(event);
    }

    @Test
    public void testAfterStageExecutionEventForAsyncTask() {
        testAfterStageExecutionEvent(true);
    }

    @Test
    public void testAfterStageExecutionEventForSyncTask() {
        testAfterStageExecutionEvent(false);
    }

    private void testAfterStageExecutionEvent(boolean async) {
        when(taskEntry.isAsync()).thenReturn(async);

        AfterStageExecutionEvent event = new AfterStageExecutionEvent(TASK_ID,
                                                                      pipeline,
                                                                      stage);
        taskManager.localListener.afterStageExecution(event);

        verify(task,
               times(1)).setStageStatus(stage.getName(),
                                        PipelineExecutorTask.Status.FINISHED);

        verifyExecutorRegistryUpdated(async);

        verifyExternalListenersNotified(event);
    }

    @Test
    public void testOnPipelineErrorExecutionEventForAsyncTask() {
        testOnPipelineErrorExecutionEvent(true);
    }

    @Test
    public void testOnPipelineErrorExecutionEventForSyncTask() {
        testOnPipelineErrorExecutionEvent(false);
    }

    private void testOnPipelineErrorExecutionEvent(boolean async) {
        when(taskEntry.isAsync()).thenReturn(async);

        OnErrorPipelineExecutionEvent event = new OnErrorPipelineExecutionEvent(TASK_ID,
                                                                                pipeline,
                                                                                stage,
                                                                                error);
        taskManager.localListener.onPipelineError(event);

        verify(task,
               times(1)).setPipelineStatus(PipelineExecutorTask.Status.ERROR);
        verify(task,
               times(1)).setPipelineError(pipelineExecutorErrorCaptor.capture());

        assertEquals(new PipelineExecutorError(ERROR_MESSAGE,
                                               error),
                     pipelineExecutorErrorCaptor.getValue());
        verifyExecutorRegistryUpdated(async);

        verifyExternalListenersNotified(event);
    }

    private void verifyExecutorRegistryUpdated(boolean async) {
        if (async) {
            //verify the pipeline executor registry was properly updated.
            verify(pipelineExecutorRegistry,
                   times(1)).register(pipelineExecutorTraceCaptor.capture());
            assertHasSameInfo(task,
                              pipelineExecutorTraceCaptor.getValue().getTask());
        } else {
            verify(pipelineExecutorRegistry,
                   never()).register(anyObject());
        }
    }

    private void verifyExternalListenersNotified(PipelineEvent event) {
        if (event instanceof BeforePipelineExecutionEvent) {
            externalListeners.forEach(listener -> verify(listener,
                                                         times(1)).beforePipelineExecution((BeforePipelineExecutionEvent) event));
        } else if (event instanceof BeforeStageExecutionEvent) {
            externalListeners.forEach(listener -> verify(listener,
                                                         times(1)).beforeStageExecution((BeforeStageExecutionEvent) event));
        } else if (event instanceof AfterStageExecutionEvent) {
            externalListeners.forEach(listener -> verify(listener,
                                                         times(1)).afterStageExecution((AfterStageExecutionEvent) event));
        } else if (event instanceof AfterPipelineExecutionEvent) {
            externalListeners.forEach(listener -> verify(listener,
                                                         times(1)).afterPipelineExecution((AfterPipelineExecutionEvent) event));
        } else if (event instanceof OnErrorPipelineExecutionEvent) {
            externalListeners.forEach(listener -> verify(listener,
                                                         times(1)).onPipelineError((OnErrorPipelineExecutionEvent) event));
        } else if (event instanceof OnErrorStageExecutionEvent) {
            externalListeners.forEach(listener -> verify(listener,
                                                         times(1)).onStageError((OnErrorStageExecutionEvent) event));
        }
    }
}
