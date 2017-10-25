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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.enterprise.inject.Instance;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskManagerImplTestBase.mockConfigExecutors;
import static org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskManagerImplTestBase.mockEventListeners;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineExecutorTaskManagerImplHelperTest {

    private static final int CONFIG_EXECUTORS_SIZE = 5;

    private static final int PIPELINE_EVENT_LISTENERS = 7;

    private static final int STAGES_COUNT = 7;

    @Mock
    private Instance<ConfigExecutor> configExecutorsInstance;

    @Mock
    private Instance<PipelineEventListener> eventListenersInstance;

    private List<ConfigExecutor> configExecutors;

    private List<PipelineEventListener> externalListeners;

    private PipelineExecutorTaskManagerImplHelper taskManagerHelper;

    @Before
    public void setUp() {
        clearDefaultProperties();

        configExecutors = mockConfigExecutors(CONFIG_EXECUTORS_SIZE);
        when(configExecutorsInstance.iterator()).thenReturn(configExecutors.iterator());

        externalListeners = mockEventListeners(PIPELINE_EVENT_LISTENERS);
        when(eventListenersInstance.iterator()).thenReturn(externalListeners.iterator());

        taskManagerHelper = spy(new PipelineExecutorTaskManagerImplHelper(configExecutorsInstance,
                                                                          eventListenersInstance));
    }

    @Test
    public void testCreateExecutorServiceWithDefaultValue() {
        //verify the creation when the THREAD_POOL_SIZE_PROPERTY_NAME property was not set.
        //The by default value must be used.
        ExecutorService executorService = taskManagerHelper.createExecutorService();
        verifyExecutorServiceAndDestroy(executorService,
                                        PipelineExecutorTaskManagerImpl.DEFAULT_THREAD_POOL_SIZE);
    }

    @Test
    public void testCreateExecutorServiceWithManualValueCorrect() {
        //verify the creation when the THREAD_POOL_SIZE_PROPERTY_NAME property was set to a correct value.
        int valueToSet = 1234;
        System.setProperty(PipelineExecutorTaskManagerImpl.THREAD_POOL_SIZE_PROPERTY_NAME,
                           Integer.toString(valueToSet));
        ExecutorService executorService = taskManagerHelper.createExecutorService();
        verifyExecutorServiceAndDestroy(executorService,
                                        valueToSet);
    }

    @Test
    public void testCreateExecutorServiceWithManualValueIncorrect() {
        //verify the creation when the THREAD_POOL_SIZE_PROPERTY_NAME property was set to a wrong value.
        String valueToSet = "an invalid integer";
        System.setProperty(PipelineExecutorTaskManagerImpl.THREAD_POOL_SIZE_PROPERTY_NAME,
                           valueToSet);
        ExecutorService executorService = taskManagerHelper.createExecutorService();
        verifyExecutorServiceAndDestroy(executorService,
                                        PipelineExecutorTaskManagerImpl.DEFAULT_THREAD_POOL_SIZE);
    }

    private void verifyExecutorServiceAndDestroy(ExecutorService executorService,
                                                 int expectedSize) {
        assertNotNull(executorService);
        assertTrue(executorService instanceof ThreadPoolExecutor);
        assertEquals(expectedSize,
                     ((ThreadPoolExecutor) executorService).getCorePoolSize());

        try {
            executorService.shutdown();
        } catch (Exception e) {
            //destroy the ExecutorService just for not letting an orphan instance. Has nothing to do with the test.
        }
    }

    @Test
    public void testCreatePipelineExecutor() {
        assertNotNull(taskManagerHelper.createPipelineExecutor());
        verify(configExecutorsInstance,
               times(1)).iterator();
        verify(taskManagerHelper,
               times(1)).newPipelineExecutor(configExecutors);
    }

    @Test
    public void testCreateExternalListeners() {
        List<PipelineEventListener> result = taskManagerHelper.createExternalListeners();
        verify(eventListenersInstance,
               times(1)).iterator();
        assertEquals(externalListeners,
                     result);
    }

    @Test
    public void testSetTaskInStoppedStatus() {

        List<String> scheduledStages = mockStages(STAGES_COUNT,
                                                  "scheduled");
        List<String> runningStages = mockStages(STAGES_COUNT,
                                                "running");
        List<String> finishedStages = mockStages(STAGES_COUNT,
                                                 "finished");
        List<String> errorStages = mockStages(STAGES_COUNT,
                                              "error");
        List<String> stoppedStages = mockStages(STAGES_COUNT,
                                                "stopped");
        List<String> stages = new ArrayList<>();
        stages.addAll(scheduledStages);
        stages.addAll(runningStages);
        stages.addAll(finishedStages);
        stages.addAll(errorStages);
        stages.addAll(stoppedStages);

        PipelineExecutorTaskDef taskDef = mock(PipelineExecutorTaskDef.class);
        Input input = mock(Input.class);
        when(taskDef.getInput()).thenReturn(input);
        when(taskDef.getStages()).thenReturn(stages);

        PipelineExecutorTaskImpl task = new PipelineExecutorTaskImpl(taskDef,
                                                                     "executionId");
        //set the pipeline e.g. in running status
        task.setPipelineStatus(PipelineExecutorTask.Status.RUNNING);

        //set the stages in the corresponding status
        setStagesInStatus(task,
                          scheduledStages,
                          PipelineExecutorTask.Status.SCHEDULED);
        setStagesInStatus(task,
                          runningStages,
                          PipelineExecutorTask.Status.RUNNING);
        setStagesInStatus(task,
                          finishedStages,
                          PipelineExecutorTask.Status.FINISHED);
        setStagesInStatus(task,
                          errorStages,
                          PipelineExecutorTask.Status.ERROR);
        setStagesInStatus(task,
                          stoppedStages,
                          PipelineExecutorTask.Status.STOPPED);

        taskManagerHelper.setTaskInStoppedStatus(task);

        //verify all stages were set in the expected status.
        //the scheduled stages must have been set to STOPPED
        assertStagesInStatus(task,
                             scheduledStages,
                             PipelineExecutorTask.Status.STOPPED);
        //the running stages must have been set to STOPPED
        assertStagesInStatus(task,
                             runningStages,
                             PipelineExecutorTask.Status.STOPPED);
        //the finished stages must remain in FINISHED status
        assertStagesInStatus(task,
                             finishedStages,
                             PipelineExecutorTask.Status.FINISHED);
        //the error stages must remain in ERROR status
        assertStagesInStatus(task,
                             errorStages,
                             PipelineExecutorTask.Status.ERROR);

        //the stopped stages must remain in STOPPED status
        assertStagesInStatus(task,
                             stoppedStages,
                             PipelineExecutorTask.Status.STOPPED);

        //the pipeline must have been stopped
        assertEquals(PipelineExecutorTask.Status.STOPPED,
                     task.getPipelineStatus());
    }

    private void clearDefaultProperties() {
        System.getProperties().remove(PipelineExecutorTaskManagerImpl.THREAD_POOL_SIZE_PROPERTY_NAME);
    }

    private List<String> mockStages(final int count,
                                    final String suffix) {
        List<String> stages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String stage = "Stage." + suffix + Integer.toString(i);
            stages.add(stage);
        }
        return stages;
    }

    private void setStagesInStatus(final PipelineExecutorTaskImpl task,
                                   final List<String> stages,
                                   final PipelineExecutorTask.Status status) {
        stages.forEach(stage -> task.setStageStatus(stage,
                                                    status));
    }

    private void assertStagesInStatus(final PipelineExecutorTaskImpl task,
                                      final List<String> stages,
                                      final PipelineExecutorTask.Status status) {
        stages.forEach(stage -> assertEquals(status,
                                             task.getStageStatus(stage)));
    }
}
