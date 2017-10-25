package org.guvnor.ala.pipeline.execution.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.enterprise.inject.Instance;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.events.PipelineEventListener;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTaskDef;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.registry.PipelineExecutorRegistry;
import org.guvnor.ala.registry.PipelineRegistry;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PipelineExecutorTaskManagerImplTestBase {

    protected static final int CONFIG_EXECUTORS_SIZE = 5;

    protected static final int PIPELINE_STAGES_SIZE = 6;

    protected static final int PIPELINE_EVENT_LISTENERS = 7;

    protected static final String PIPELINE_ID = "PIPELINE_ID";

    protected static final String TASK_ID = "TASK_ID";

    protected static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    protected Instance<ConfigExecutor> configExecutorsInstance;

    @Mock
    protected Instance<PipelineEventListener> eventListenersInstance;

    @Mock
    protected PipelineExecutorRegistry pipelineExecutorRegistry;

    protected ArgumentCaptor<PipelineExecutorTrace> pipelineExecutorTraceCaptor;

    protected PipelineExecutorTaskManagerImpl taskManager;

    protected PipelineExecutorTaskManagerImplHelper taskManagerHelper;

    @Mock
    protected ExecutorService executorService;

    @Mock
    protected PipelineExecutor pipelineExecutor;

    protected List<ConfigExecutor> configExecutors;

    protected List<PipelineEventListener> externalListeners;

    protected ArgumentCaptor<String> stringCaptor;

    protected ArgumentCaptor<PipelineExecutorTaskManagerImpl.TaskEntry> taskEntryCaptor;

    protected Pipeline pipeline;

    @Mock
    protected PipelineRegistry pipelineRegistry;

    protected List<Stage> stages;

    protected PipelineExecutorTaskDef taskDef;

    protected Input input;

    @Before
    public void setUp() {
        pipelineExecutorTraceCaptor = ArgumentCaptor.forClass(PipelineExecutorTrace.class);
        taskEntryCaptor = ArgumentCaptor.forClass(PipelineExecutorTaskManagerImpl.TaskEntry.class);
        stringCaptor = ArgumentCaptor.forClass(String.class);

        configExecutors = mockConfigExecutors(CONFIG_EXECUTORS_SIZE);
        when(configExecutorsInstance.iterator()).thenReturn(configExecutors.iterator());

        externalListeners = mockEventListeners(PIPELINE_EVENT_LISTENERS);
        when(eventListenersInstance.iterator()).thenReturn(externalListeners.iterator());

        taskManagerHelper = spy(new PipelineExecutorTaskManagerImplHelper(configExecutorsInstance,
                                                                          eventListenersInstance));
        doReturn(executorService).when(taskManagerHelper).createExecutorService();
        doReturn(pipelineExecutor).when(taskManagerHelper).createPipelineExecutor();

        taskManager = spy(new PipelineExecutorTaskManagerImpl(pipelineRegistry,
                                                              configExecutorsInstance,
                                                              eventListenersInstance,
                                                              pipelineExecutorRegistry) {
            {
                super.taskManagerHelper = PipelineExecutorTaskManagerImplTestBase.this.taskManagerHelper;
            }

            @Override
            protected void init() {
                super.init();
                super.futureTaskMap = spy(super.futureTaskMap);
            }
        });
    }

    public static List<ConfigExecutor> mockConfigExecutors(int count) {
        List<ConfigExecutor> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(mock(ConfigExecutor.class));
        }
        return result;
    }

    public static List<Stage> mockStages(int count) {
        List<Stage> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Stage stage = mock(Stage.class);
            when(stage.getName()).thenReturn("Stage.name." + Integer.toString(i));
            result.add(stage);
        }
        return result;
    }

    public static List<PipelineEventListener> mockEventListeners(int count) {
        List<PipelineEventListener> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PipelineEventListener listener = mock(PipelineEventListener.class);
            result.add(listener);
        }
        return result;
    }

    protected void assertHasSameInfo(PipelineExecutorTask expectedTask,
                                     PipelineExecutorTask task) {
        assertEquals(expectedTask.getId(),
                     task.getId());
        assertEquals(expectedTask.getPipelineStatus(),
                     task.getPipelineStatus());
        assertEquals(expectedTask.getPipelineError(),
                     task.getPipelineError());
        assertEquals(expectedTask.getOutput(),
                     task.getOutput());

        assertHasSameInfo(expectedTask.getTaskDef(),
                          task.getTaskDef());

        expectedTask.getTaskDef().getStages().forEach(stage -> {
            assertEquals(expectedTask.getStageStatus(stage),
                         task.getStageStatus(stage));
            assertEquals(expectedTask.getStageError(stage),
                         task.getStageError(stage));
        });
    }

    protected void assertHasSameInfo(PipelineExecutorTaskDef expectedTaskDef,
                                     PipelineExecutorTaskDef taskDef) {
        assertEquals(expectedTaskDef.getInput(),
                     taskDef.getInput());
        assertEquals(expectedTaskDef.getPipeline(),
                     taskDef.getPipeline());
        assertEquals(expectedTaskDef.getProviderId(),
                     taskDef.getProviderId());
        assertEquals(expectedTaskDef.getProviderType(),
                     taskDef.getProviderType());
    }
}
