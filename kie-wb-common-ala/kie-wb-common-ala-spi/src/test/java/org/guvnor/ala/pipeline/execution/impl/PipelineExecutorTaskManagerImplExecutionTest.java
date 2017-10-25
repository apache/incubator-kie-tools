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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutorException;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskManager;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.StartsWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineExecutorTaskManagerImplExecutionTest
        extends PipelineExecutorTaskManagerImplTestBase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testExecuteAsync() {
        when(taskManagerHelper.generateTaskId()).thenReturn(TASK_ID);

        taskManager.init();

        //prepare the input parameters.
        prepareExecution();

        Future future = mock(Future.class);
        when(executorService.submit(any(Runnable.class))).thenReturn(future);

        String result = taskManager.execute(taskDef,
                                            PipelineExecutorTaskManager.ExecutionMode.ASYNCHRONOUS);
        assertEquals(TASK_ID,
                     result);

        //verify the task to execute was properly initialized and stored
        PipelineExecutorTaskManagerImpl.TaskEntry internalTaskEntry = taskManager.currentTasks.get(TASK_ID);
        verifyInternalTask(internalTaskEntry,
                           true);
        assertEquals(1,
                     taskManager.currentTasks.size());

        //verify the associated future task was properly scheduled and stored.
        verify(executorService,
               times(1)).submit(any(Runnable.class));
        assertEquals(future,
                     taskManager.futureTaskMap.get(TASK_ID));

        //verify the pipeline executor registry was properly updated.
        verify(pipelineExecutorRegistry,
               times(1)).register(pipelineExecutorTraceCaptor.capture());

        assertEquals(PIPELINE_ID,
                     pipelineExecutorTraceCaptor.getValue().getPipelineId());
        assertEquals(TASK_ID,
                     pipelineExecutorTraceCaptor.getValue().getTaskId());
        assertHasSameInfo(internalTaskEntry.getTask(),
                          pipelineExecutorTraceCaptor.getValue().getTask());
    }

    @Test
    public void testExecuteSync() {
        when(taskManagerHelper.generateTaskId()).thenReturn(TASK_ID);

        taskManager.init();

        //prepare the input parameters.
        prepareExecution();

        String result = taskManager.execute(taskDef,
                                            PipelineExecutorTaskManager.ExecutionMode.SYNCHRONOUS);
        assertEquals(TASK_ID,
                     result);

        //verify the task to execute was properly initialized.
        verify(taskManagerHelper,
               times(1)).createTask(taskDef);

        //verify the pipeline was properly executed.
        verify(pipelineExecutor,
               times(1)).execute(eq(taskDef.getInput()),
                                 eq(pipeline),
                                 any(Consumer.class),
                                 eq(taskManager.localListener));

        //verify the pipeline executor registry was properly updated.
        verify(pipelineExecutorRegistry,
               times(1)).register(pipelineExecutorTraceCaptor.capture());

        assertEquals(PIPELINE_ID,
                     pipelineExecutorTraceCaptor.getValue().getPipelineId());
        assertEquals(TASK_ID,
                     pipelineExecutorTraceCaptor.getValue().getTaskId());
    }

    @Test
    public void testStopNonExistingTask() throws PipelineExecutorException {
        taskManager.init();

        expectedException.expectMessage("No PipelineExecutorTask was found for taskId: " + TASK_ID);
        taskManager.stop(TASK_ID);
    }

    @Test
    public void testStopSyncTask() throws PipelineExecutorException {
        PipelineExecutorTaskManagerImpl.TaskEntry taskEntry = mock(PipelineExecutorTaskManagerImpl.TaskEntry.class);
        when(taskEntry.isAsync()).thenReturn(false);
        taskManager.currentTasks.put(TASK_ID,
                                     taskEntry);

        taskManager.init();
        expectedException.expectMessage("Stop operation is not available for taskId: " + TASK_ID +
                                                " running in SYNCHRONOUS mode");
        taskManager.stop(TASK_ID);
    }

    @Test
    public void testStopAsyncTask() throws PipelineExecutorException {
        when(taskManagerHelper.generateTaskId()).thenReturn(TASK_ID);

        taskManager.init();

        //prepare the input parameters.
        prepareExecution();

        Future future = mock(Future.class);
        when(executorService.submit(any(Runnable.class))).thenReturn(future);

        String result = taskManager.execute(taskDef,
                                            PipelineExecutorTaskManager.ExecutionMode.ASYNCHRONOUS);

        assertEquals(TASK_ID,
                     result);

        PipelineExecutorTaskManagerImpl.TaskEntry taskEntry = taskManager.currentTasks.get(TASK_ID);

        taskManager.stop(TASK_ID);

        verify(future,
               times(1)).cancel(true);
        assertFalse(taskManager.currentTasks.containsKey(TASK_ID));
        verify(taskManagerHelper,
               times(1)).setTaskInStoppedStatus(taskEntry.getTask());
        //verify the pipeline executor registry was properly updated.
        verify(pipelineExecutorRegistry,
               times(2)).register(pipelineExecutorTraceCaptor.capture());
        assertHasSameInfo(pipelineExecutorTraceCaptor.getAllValues().get(1).getTask(),
                          pipelineExecutorTraceCaptor.getValue().getTask());
    }

    @Test
    public void testStopTaskInErrorStatus() throws PipelineExecutorException {
        testStopTaskInNonStopeableState(PipelineExecutorTask.Status.ERROR);
    }

    @Test
    public void testStopTaskInStoppedStatus() throws PipelineExecutorException {
        testStopTaskInNonStopeableState(PipelineExecutorTask.Status.STOPPED);
    }

    @Test
    public void testStopTaskInFinishedStatus() throws PipelineExecutorException {
        testStopTaskInNonStopeableState(PipelineExecutorTask.Status.FINISHED);
    }

    private void testStopTaskInNonStopeableState(PipelineExecutorTask.Status notStopeableStatus) throws PipelineExecutorException {
        PipelineExecutorTaskImpl task = mock(PipelineExecutorTaskImpl.class);
        when(task.getId()).thenReturn(TASK_ID);
        when(task.getPipelineStatus()).thenReturn(notStopeableStatus);
        PipelineExecutorTaskManagerImpl.TaskEntry taskEntry = mock(PipelineExecutorTaskManagerImpl.TaskEntry.class);
        when(taskEntry.isAsync()).thenReturn(true);
        when(taskEntry.getTask()).thenReturn(task);
        taskManager.currentTasks.put(TASK_ID,
                                     taskEntry);

        taskManager.init();
        expectedException.expectMessage(new StartsWith("A PipelineExecutorTask in status: " + notStopeableStatus.name() +
                                                               " can not be stopped. Stop operation is available for the following status set:"));
        taskManager.stop(TASK_ID);
    }

    @Test
    public void testDestroyNonExistingTask() throws PipelineExecutorException {
        taskManager.init();

        expectedException.expectMessage("No PipelineExecutorTask was found for taskId: " + TASK_ID);
        taskManager.destroy(TASK_ID);
    }

    @Test
    public void testDestroySyncTask() throws PipelineExecutorException {
        PipelineExecutorTaskManagerImpl.TaskEntry taskEntry = mock(PipelineExecutorTaskManagerImpl.TaskEntry.class);
        when(taskEntry.isAsync()).thenReturn(false);
        taskManager.currentTasks.put(TASK_ID,
                                     taskEntry);

        taskManager.init();
        expectedException.expectMessage("Destroy operation is not available for taskId: " + TASK_ID +
                                                " running in SYNCHRONOUS mode");
        taskManager.destroy(TASK_ID);
    }

    @Test
    public void testDestroyAsyncTask() throws PipelineExecutorException {
        when(taskManagerHelper.generateTaskId()).thenReturn(TASK_ID);

        taskManager.init();

        //prepare the input parameters.
        prepareExecution();

        Future future = mock(Future.class);
        when(executorService.submit(any(Runnable.class))).thenReturn(future);

        String result = taskManager.execute(taskDef,
                                            PipelineExecutorTaskManager.ExecutionMode.ASYNCHRONOUS);

        assertEquals(TASK_ID,
                     result);

        taskManager.destroy(TASK_ID);

        verify(future,
               times(1)).cancel(true);
        assertFalse(taskManager.currentTasks.containsKey(TASK_ID));
        verify(pipelineExecutorRegistry,
               times(1)).deregister(TASK_ID);
    }

    @Test
    public void testDeleteActiveTask() throws Exception {
        PipelineExecutorTaskManagerImpl.TaskEntry taskEntry = mock(PipelineExecutorTaskManagerImpl.TaskEntry.class);
        taskManager.currentTasks.put(TASK_ID,
                                     taskEntry);
        expectedException.expectMessage(new StartsWith("An active PipelineExecutorTask was found for taskId: " + TASK_ID));
        taskManager.delete(TASK_ID);
    }

    @Test
    public void testDeleteTaskInScheduledStatus() throws Exception {
        testDeleteTaskInNonStopeableState(PipelineExecutorTask.Status.SCHEDULED);
    }

    @Test
    public void testDeleteTaskInRunningStatus() throws Exception {
        testDeleteTaskInNonStopeableState(PipelineExecutorTask.Status.RUNNING);
    }

    private void testDeleteTaskInNonStopeableState(PipelineExecutorTask.Status nonStopeableStatus) throws Exception {
        PipelineExecutorTask task = mock(PipelineExecutorTask.class);
        when(task.getPipelineStatus()).thenReturn(nonStopeableStatus);
        PipelineExecutorTrace trace = mock(PipelineExecutorTrace.class);
        when(trace.getTask()).thenReturn(task);
        when(pipelineExecutorRegistry.getExecutorTrace(TASK_ID)).thenReturn(trace);

        expectedException.expectMessage(new StartsWith("A PipelineExecutorTask in status: "
                                                               + nonStopeableStatus + " can not" +
                                                               " be deleted. Delete operation is available for the following status set:"));
        taskManager.delete(TASK_ID);
    }

    @Test
    public void testDeleteNonExistingTask() throws Exception {
        expectedException.expectMessage("No PipelineExecutorTask was found for taskId: " + TASK_ID);
        taskManager.delete(TASK_ID);
    }

    @Test
    public void testDeleteTask() throws Exception {
        PipelineExecutorTrace trace = mock(PipelineExecutorTrace.class);
        PipelineExecutorTask task = mock(PipelineExecutorTask.class);
        PipelineExecutorTask.Status status = PipelineExecutorTask.Status.STOPPED;
        when(task.getPipelineStatus()).thenReturn(status);
        when(trace.getTask()).thenReturn(task);
        when(pipelineExecutorRegistry.getExecutorTrace(TASK_ID)).thenReturn(trace);
        taskManager.delete(TASK_ID);
        verify(pipelineExecutorRegistry,
               times(1)).deregister(TASK_ID);
    }

    @Test
    public void testDestroy() throws Exception {

        int runningTasks = 5;
        List<PipelineExecutorTaskImpl> tasks = new ArrayList<>();
        //emulate a set of currently running tasks
        for (int i = 0; i < runningTasks; i++) {
            String taskId = TASK_ID + i;

            PipelineExecutorTaskImpl task = mock(PipelineExecutorTaskImpl.class);
            when(task.clone()).thenReturn(task);
            when(task.getId()).thenReturn(taskId);
            when(task.getPipelineStatus()).thenReturn(PipelineExecutorTask.Status.RUNNING);
            PipelineExecutorTaskDef taskDef = mock(PipelineExecutorTaskDef.class);
            when(task.getTaskDef()).thenReturn(taskDef);
            Pipeline pipeline = mock(Pipeline.class);
            when(pipeline.getStages()).thenReturn(mock(List.class));
            when(taskDef.getPipeline()).thenReturn(PIPELINE_ID);

            PipelineExecutorTaskManagerImpl.TaskEntry taskEntry = mock(PipelineExecutorTaskManagerImpl.TaskEntry.class);
            when(taskEntry.isAsync()).thenReturn(true);
            when(taskEntry.getTask()).thenReturn(task);
            taskManager.currentTasks.put(taskId,
                                         taskEntry);
            tasks.add(task);
        }

        taskManager.destroy();
        tasks.forEach(task -> verify(taskManagerHelper,
                                     times(1)).setTaskInStoppedStatus(task));
        verify(pipelineExecutorRegistry,
               times(5)).register(pipelineExecutorTraceCaptor.capture());
        Map<String, PipelineExecutorTask> registeredTasks = new HashMap<>();
        pipelineExecutorTraceCaptor.getAllValues().forEach(capture -> registeredTasks.put(capture.getTaskId(),
                                                                                          capture.getTask()));
        tasks.forEach(task -> assertHasSameInfo(task,
                                                registeredTasks.get(task.getId())));
    }

    private void prepareExecution() {
        //mock the execution inputs
        pipeline = mock(Pipeline.class);
        stages = mockStages(PIPELINE_STAGES_SIZE);
        when(pipeline.getStages()).thenReturn(stages);
        when(pipeline.getName()).thenReturn(PIPELINE_ID);
        when(pipelineRegistry.getPipelineByName(PIPELINE_ID)).thenReturn(pipeline);

        taskDef = mock(PipelineExecutorTaskDef.class);
        input = mock(Input.class);
        when(taskDef.getInput()).thenReturn(input);
        when(taskDef.getPipeline()).thenReturn(PIPELINE_ID);
    }

    private void verifyInternalTask(PipelineExecutorTaskManagerImpl.TaskEntry taskEntry,
                                    boolean isAsync) {
        //verify that the generated internal task has the expect settings
        assertNotNull(taskEntry);
        assertEquals(TASK_ID,
                     taskEntry.getTask().getId());
        assertEquals(taskDef,
                     taskEntry.getTask().getTaskDef());
        assertEquals(isAsync,
                     taskEntry.isAsync());
    }
}
