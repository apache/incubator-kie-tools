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

import java.util.List;

import org.guvnor.ala.ui.client.widget.pipeline.stage.StagePresenter;
import org.guvnor.ala.ui.client.widget.pipeline.stage.State;
import org.guvnor.ala.ui.events.PipelineStatusChangeEvent;
import org.guvnor.ala.ui.events.StageStatusChangeEvent;
import org.guvnor.ala.ui.model.PipelineExecutionTrace;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.Stage;
import org.jboss.errai.common.client.api.IsElement;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Common and setups tests.
 */
public class RuntimePresenterSetupsTest
        extends RuntimePresenterTest {

    /**
     * Tests the case when the item is a Runtime with no pipeline execution trace.
     */
    @Test
    public void testSetupRuntimeWithNoTrace() {
        runtime = mockRuntime();
        item = new RuntimeListItem(RUNTIME_NAME,
                                   runtime);
        presenter.setup(item);

        verify(pipelinePresenter,
               times(1)).clearStages();
        verify(view,
               times(1)).setup(RUNTIME_NAME,
                               CREATED_DATE,
                               DEFAULT_PIPELINE_NAME);
        verify(view,
               times(1)).setEndpoint(ENDPOINT);
        verify(pipelinePresenter,
               never()).addStage(any(IsElement.class));
    }

    /**
     * Tests the case when the item is a Runtime with pipeline execution trace.
     */
    @Test
    public void testSetupRuntimeWithTrace() {
        runtime = mockRuntime();

        preparePipelineExecutionTraceSetup();

        item = new RuntimeListItem(RUNTIME_NAME,
                                   trace);
        //set the trace on the runtime.
        runtime.setPipelineTrace(trace);
        item = new RuntimeListItem(RUNTIME_NAME,
                                   runtime);
        presenter.setup(item);

        verify(pipelinePresenter,
               times(2)).clearStages();
        verify(view,
               times(1)).setup(RUNTIME_NAME,
                               CREATED_DATE,
                               PIPELINE_NAME);
        verify(view,
               times(1)).setEndpoint(ENDPOINT);

        verifyPipelineWasSet(trace,
                             displayableStages);
    }

    /**
     * Tests the case when the item is a PipelineExecutionTrace execution trace.
     */
    @Test
    public void testSetupPipelineExecutionTrace() {

        preparePipelineExecutionTraceSetup();

        RuntimeListItem item = new RuntimeListItem(RUNTIME_NAME,
                                                   trace);
        presenter.setup(item);

        verify(pipelinePresenter,
               times(2)).clearStages();
        verify(view,
               times(1)).setup(RUNTIME_NAME,
                               "",
                               PIPELINE_NAME);
        verifyPipelineWasSet(trace,
                             displayableStages);
    }

    /**
     * an item with a pipeline execution trace was initially set and the last stage status has changed.
     */
    @Test
    public void testOnStageStatusChangeLastVisibleStageChanged() {

        preparePipelineExecutionTraceSetup();

        RuntimeListItem item = new RuntimeListItem(RUNTIME_NAME,
                                                   trace);
        //setup the presenter.
        presenter.setup(item);
        int initialStagePresentersSize = stagePresenters.size();

        //emulate the last stage finishing.
        Stage stage = displayableStages.get(displayableStages.size() - 1);

        presenter.onStageStatusChange(new StageStatusChangeEvent(trace.getKey(),
                                                                 stage.getName(),
                                                                 PipelineStatus.FINISHED));

        //the last stage status was changed form EXECUTING to to DONE
        StagePresenter stagePresenter = stagePresenters.get(stagePresenters.size() - 1);
        verify(stagePresenter,
               times(1)).setState(State.EXECUTING);
        verify(stagePresenter,
               times(1)).setState(State.DONE);
        //no additional stages were added.
        verify(stagePresenterInstance,
               times(initialStagePresentersSize)).get();
    }

    /**
     * an item with a pipeline execution trace was initially set and a new stage status not yet drawn has changed.
     */
    @Test
    public void testOnStageStatusChangeNewStageChanged() {

        preparePipelineExecutionTraceSetup();

        RuntimeListItem item = new RuntimeListItem(RUNTIME_NAME,
                                                   trace);
        //setup the presenter.
        presenter.setup(item);
        int initialStagePresentersSize = stagePresenters.size();

        //emulate the next stage consecutive stage finishing.
        Stage stage = item.getPipelineTrace().getPipeline().getStages().get(displayableStages.size());

        presenter.onStageStatusChange(new StageStatusChangeEvent(trace.getKey(),
                                                                 stage.getName(),
                                                                 PipelineStatus.RUNNING));

        //an additional stage was added.
        verify(stagePresenterInstance,
               times(initialStagePresentersSize + 1)).get();

        //the newly stage is set to EXECUTING in the UI
        StagePresenter stagePresenter = stagePresenters.get(stagePresenters.size() - 1);
        verify(stagePresenter,
               times(1)).setup(stage);
        verify(stagePresenter,
               times(1)).setState(State.EXECUTING);
    }

    @Test
    public void testOnPipelineStatusChange() {
        preparePipelineExecutionTraceSetup();

        RuntimeListItem item = new RuntimeListItem(RUNTIME_NAME,
                                                   trace);
        //setup the presenter.
        presenter.setup(item);

        //the pipeline finishes
        presenter.onPipelineStatusChange(new PipelineStatusChangeEvent(trace.getKey(),
                                                                       PipelineStatus.FINISHED));

        verify(view,
               times(1)).setStatus(RuntimePresenterHelper.buildIconStyle(PipelineStatus.FINISHED));
    }

    /**
     * Verify that the pipeline was properly drawn on screen.
     * @param trace the trace to test.
     * @param displayableStages list of stages that we know by construction must be displayed.
     */
    private void verifyPipelineWasSet(PipelineExecutionTrace trace,
                                      List<Stage> displayableStages) {

        int stagesSize = displayableStages.size();
        int transitionsSize = stagesSize > 0 ? (stagesSize - 1) : 0;

        for (int i = 0; i < displayableStages.size(); i++) {
            StagePresenter stagePresenter = stagePresenters.get(i);
            verify(stagePresenter,
                   times(1)).setup(displayableStages.get(i));
        }

        verify(stagePresenterInstance,
               times(stagesSize)).get();

        verify(transitionPresenterInstance,
               times(transitionsSize)).get();

        verify(pipelinePresenter,
               times(stagesSize + transitionsSize)).addStage(any(IsElement.class));
    }
}