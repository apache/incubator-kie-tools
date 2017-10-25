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

package org.guvnor.ala.ui.client.provider.status.runtime;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.provider.status.runtime.actions.RuntimeActionItemPresenter;
import org.guvnor.ala.ui.client.provider.status.runtime.actions.RuntimeActionItemSeparatorPresenter;
import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.widget.pipeline.PipelinePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.stage.StagePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.transition.TransitionPresenter;
import org.guvnor.ala.ui.model.Pipeline;
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
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeKey;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Base test for the RuntimePresenter.
 */
@RunWith(GwtMockitoTestRunner.class)
public class RuntimePresenterTest {

    protected static final String RUNTIME_NAME = "RUNTIME_NAME";

    protected static final String RUNTIME_ID = "RUNTIME_ID";

    protected static final String RUNTIME_STATUS = "RUNTIME_STATUS";

    protected static final String ENDPOINT = "ENDPOINT";

    protected static final String CREATED_DATE = "CREATED_DATE";

    protected static final String DEFAULT_PIPELINE_NAME = "<system>";

    protected static final String PIPELINE_NAME = "PIPELINE_NAME";

    protected static final String EXECUTION_ID = "EXECUTION_ID";

    protected static final int STAGE_NUMBER = 10;

    @Mock
    protected RuntimePresenter.View view;

    @Mock
    protected PipelinePresenter pipelinePresenter;

    @Mock
    protected ManagedInstance<StagePresenter> stagePresenterInstance;

    @Mock
    protected ManagedInstance<TransitionPresenter> transitionPresenterInstance;

    @Mock
    protected ManagedInstance<RuntimeActionItemPresenter> actionItemPresenterInstance;

    @Mock
    protected ManagedInstance<RuntimeActionItemSeparatorPresenter> actionItemSeparatorPresenterInstance;

    @Mock
    protected RuntimeService runtimeService;

    protected Caller<RuntimeService> runtimeServiceCaller;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected PopupHelper popupHelper;

    @Mock
    protected TranslationService translationService;

    protected RuntimePresenter presenter;

    protected List<TransitionPresenter> transitionPresenters = new ArrayList<>();

    protected List<StagePresenter> stagePresenters = new ArrayList<>();

    protected List<RuntimeActionItemPresenter> actionItemPresenters = new ArrayList<>();

    protected List<RuntimeActionItemSeparatorPresenter> separatorItemPresenters = new ArrayList<>();

    protected RuntimeActionItemPresenter startActionPresenter;

    protected RuntimeActionItemPresenter stopActionPresenter;

    protected RuntimeActionItemPresenter deleteActionPresenter;

    protected RuntimeActionItemPresenter showErrorActionPresenter;

    protected RuntimeActionItemSeparatorPresenter separatorPresenter;

    protected RuntimeActionItemSeparatorPresenter secondarySeparatorPresenter;

    protected Runtime runtime;

    protected PipelineExecutionTrace trace;

    protected RuntimeListItem item;

    protected List<Stage> displayableStages;

    @Before
    public void setUp() {
        runtimeServiceCaller = new CallerMock<>(runtimeService);

        presenter = spy(new RuntimePresenter(view,
                                             pipelinePresenter,
                                             stagePresenterInstance,
                                             transitionPresenterInstance,
                                             actionItemPresenterInstance,
                                             actionItemSeparatorPresenterInstance,
                                             runtimeServiceCaller,
                                             notificationEvent,
                                             popupHelper,
                                             translationService) {

            @Override
            protected StagePresenter newStagePresenter() {
                StagePresenter stagePresenter = mock(StagePresenter.class);
                when(stagePresenterInstance.get()).thenReturn(stagePresenter);
                stagePresenters.add(stagePresenter);
                return super.newStagePresenter();
            }

            @Override
            protected TransitionPresenter newTransitionPresenter() {
                TransitionPresenter transitionPresenter = mock(TransitionPresenter.class);
                when(transitionPresenterInstance.get()).thenReturn(transitionPresenter);
                transitionPresenters.add(transitionPresenter);
                return super.newTransitionPresenter();
            }

            @Override
            protected RuntimeActionItemPresenter newActionItemPresenter() {
                RuntimeActionItemPresenter actionItemPresenter = mock(RuntimeActionItemPresenter.class);
                RuntimeActionItemPresenter.View view = mock(RuntimeActionItemPresenter.View.class);
                when(actionItemPresenter.getView()).thenReturn(view);
                when(actionItemPresenterInstance.get()).thenReturn(actionItemPresenter);
                actionItemPresenters.add(actionItemPresenter);
                return super.newActionItemPresenter();
            }

            @Override
            protected RuntimeActionItemSeparatorPresenter newSeparatorItem() {
                RuntimeActionItemSeparatorPresenter separatorItemPresenter = mock(RuntimeActionItemSeparatorPresenter.class);
                RuntimeActionItemSeparatorPresenter.View view = mock(RuntimeActionItemSeparatorPresenter.View.class);
                when(separatorItemPresenter.getView()).thenReturn(view);
                when(actionItemSeparatorPresenterInstance.get()).thenReturn(separatorItemPresenter);
                separatorItemPresenters.add(separatorItemPresenter);
                return super.newSeparatorItem();
            }
        });
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(actionItemPresenterInstance,
               times(4)).get();
        verify(actionItemSeparatorPresenterInstance,
               times(2)).get();

        startActionPresenter = actionItemPresenters.get(0);
        stopActionPresenter = actionItemPresenters.get(1);
        deleteActionPresenter = actionItemPresenters.get(2);
        showErrorActionPresenter = actionItemPresenters.get(3);
        separatorPresenter = separatorItemPresenters.get(0);
        secondarySeparatorPresenter = separatorItemPresenters.get(1);
    }

    @Test
    public void testDestroy() {
        presenter.destroy();
        verify(actionItemPresenterInstance,
               times(1)).destroy(startActionPresenter);
        verify(actionItemPresenterInstance,
               times(1)).destroy(stopActionPresenter);
        verify(actionItemPresenterInstance,
               times(1)).destroy(deleteActionPresenter);
        verify(actionItemSeparatorPresenterInstance,
               times(1)).destroy(separatorPresenter);
        verify(actionItemSeparatorPresenterInstance,
               times(1)).destroy(secondarySeparatorPresenter);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    protected void preparePipelineExecutionTraceSetup() {
        Pipeline pipeline = mockPipeline(PIPELINE_NAME,
                                         STAGE_NUMBER);
        trace = mockPipelineExecutionTrace(EXECUTION_ID,
                                           pipeline,
                                           PipelineStatus.RUNNING,
                                           PipelineStatus.SCHEDULED);

        //set an arbitrary number of consecutive stages as finished and the last as running.
        displayableStages = new ArrayList<>();
        int finishedStages = 5;
        for (int i = 0; i < finishedStages; i++) {
            trace.setStageStatus(pipeline.getStages().get(i).getName(),
                                 PipelineStatus.FINISHED);
            displayableStages.add(pipeline.getStages().get(i));
        }
        trace.setStageStatus(pipeline.getStages().get(finishedStages).getName(),
                             PipelineStatus.RUNNING);
        displayableStages.add(pipeline.getStages().get(finishedStages));
    }

    protected Runtime mockRuntime() {
        ProviderTypeKey providerTypeKey = mockProviderTypeKey("1");
        ProviderKey providerKey = mockProviderKey(providerTypeKey,
                                                  "1");

        RuntimeKey runtimeKey = new RuntimeKey(providerKey,
                                               RUNTIME_ID);

        Runtime runtime = new Runtime(runtimeKey,
                                      RUNTIME_NAME,
                                      null,
                                      RUNTIME_STATUS,
                                      ENDPOINT,
                                      CREATED_DATE);
        return runtime;
    }

    protected Pipeline mockPipeline(String pipelineId,
                                    int stages) {
        PipelineKey pipelineKey = new PipelineKey(pipelineId);
        Pipeline pipeline = new Pipeline(pipelineKey);

        for (int i = 0; i < stages; i++) {
            Stage stage = new Stage(pipelineKey,
                                    "Stage.name." + Integer.toString(i));
            pipeline.addStage(stage);
        }
        return pipeline;
    }

    protected PipelineExecutionTrace mockPipelineExecutionTrace(String executionId,
                                                                Pipeline pipeline,
                                                                PipelineStatus initialPipelineStatus,
                                                                PipelineStatus initialStagesStatus) {
        PipelineExecutionTrace trace = new PipelineExecutionTrace(new PipelineExecutionTraceKey(executionId));
        trace.setPipeline(pipeline);
        trace.setPipelineStatus(initialPipelineStatus);
        pipeline.getStages().forEach(stage -> trace.setStageStatus(stage.getName(),
                                                                   initialStagesStatus));
        return trace;
    }
}