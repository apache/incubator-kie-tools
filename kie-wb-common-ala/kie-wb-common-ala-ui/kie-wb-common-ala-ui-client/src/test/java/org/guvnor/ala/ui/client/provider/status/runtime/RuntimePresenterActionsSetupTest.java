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

import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.RuntimeStatus;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.mvp.Command;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionDeleteAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionShowErrorAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_PipelineExecutionStopAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeDeleteAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStartAction;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.RuntimePresenter_RuntimeStopAction;
import static org.mockito.Mockito.*;

/**
 * Tests the properly initialization of the available actions for the RuntimePresenter.
 */
public class RuntimePresenterActionsSetupTest
        extends RuntimePresenterTest {

    private static final String STOP_PIPELINE_ACTION = "STOP_PIPELINE_ACTION";

    private static final String DELETE_PIPELINE_ACTION = "DELETE_PIPELINE_ACTION";

    private static final String STOP_RUNTIME_ACTION = "STOP_RUNTIME_ACTION";

    private static final String START_RUNTIME_ACTION = "START_RUNTIME_ACTION";

    private static final String DELETE_RUNTIME_ACTION = "DELETE_RUNTIME_ACTION";

    private static final String SHOW_PIPELINE_ERROR_ACTION = "SHOW_PIPELINE_ERROR_ACTION";

    @Before
    public void setUp() {
        super.setUp();

        when(translationService.getTranslation(RuntimePresenter_PipelineExecutionStopAction)).thenReturn(STOP_PIPELINE_ACTION);
        when(translationService.getTranslation(RuntimePresenter_PipelineExecutionDeleteAction)).thenReturn(DELETE_PIPELINE_ACTION);
        when(translationService.getTranslation(RuntimePresenter_PipelineExecutionShowErrorAction)).thenReturn(SHOW_PIPELINE_ERROR_ACTION);
        when(translationService.getTranslation(RuntimePresenter_RuntimeStartAction)).thenReturn(START_RUNTIME_ACTION);
        when(translationService.getTranslation(RuntimePresenter_RuntimeStopAction)).thenReturn(STOP_RUNTIME_ACTION);
        when(translationService.getTranslation(RuntimePresenter_RuntimeDeleteAction)).thenReturn(DELETE_RUNTIME_ACTION);
    }

    @Test
    public void testPipelineActionsSetupForScheduledPipeline() {
        preparePipelineActionsSetupTest(PipelineStatus.SCHEDULED);
        verifyPipelineActionsCommonSetup();

        verify(stopActionPresenter,
               times(1)).setEnabled(true);
        verify(deleteActionPresenter,
               never()).setEnabled(true);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    @Test
    public void testPipelineActionsSetupForRunningPipeline() {
        preparePipelineActionsSetupTest(PipelineStatus.RUNNING);
        verifyPipelineActionsCommonSetup();

        verify(stopActionPresenter,
               times(1)).setEnabled(true);
        verify(deleteActionPresenter,
               never()).setEnabled(true);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    @Test
    public void testPipelineActionsSetupForErrorPipeline() {
        preparePipelineActionsSetupTest(PipelineStatus.ERROR);
        verifyPipelineActionsCommonSetup();

        verify(stopActionPresenter,
               never()).setEnabled(true);
        verify(deleteActionPresenter,
               times(1)).setEnabled(true);
        verify(showErrorActionPresenter,
               times(1)).setEnabled(true);
    }

    @Test
    public void testPipelineActionsSetupForStoppedPipeline() {
        preparePipelineActionsSetupTest(PipelineStatus.STOPPED);
        verifyPipelineActionsCommonSetup();

        verify(stopActionPresenter,
               never()).setEnabled(true);
        verify(deleteActionPresenter,
               times(1)).setEnabled(true);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    private void preparePipelineActionsSetupTest(final PipelineStatus status) {
        preparePipelineExecutionTraceSetup();
        trace.setPipelineStatus(status);
        RuntimeListItem item = new RuntimeListItem(RUNTIME_NAME,
                                                   trace);
        presenter.setup(item);
    }

    private void verifyPipelineActionsCommonSetup() {
        verify(view,
               times(1)).clearActionItems();
        verify(stopActionPresenter,
               times(1)).setup(eq(STOP_PIPELINE_ACTION),
                               any(Command.class));
        verify(deleteActionPresenter,
               times(1)).setup(eq(DELETE_PIPELINE_ACTION),
                               any(Command.class));
        verify(showErrorActionPresenter,
               times(1)).setup(eq(SHOW_PIPELINE_ERROR_ACTION),
                               any(Command.class));
        verify(view,
               times(1)).addActionItem(stopActionPresenter.getView());
        verify(view,
               times(1)).addActionItem(separatorPresenter.getView());
        verify(view,
               times(1)).addActionItem(deleteActionPresenter.getView());
        verify(view,
               never()).addActionItem(startActionPresenter.getView());
        verify(stopActionPresenter,
               times(1)).setEnabled(false);
        verify(startActionPresenter,
               times(1)).setEnabled(false);
        verify(deleteActionPresenter,
               times(1)).setEnabled(false);
    }

    @Test
    public void testRuntimeActionsSetupForRunningRuntime() {
        prepareRuntimeActionsSetupTest(RuntimeStatus.RUNNING.name());
        verifyRuntimeActionsCommonSetup();

        verify(startActionPresenter,
               times(1)).setEnabled(false);
        verify(stopActionPresenter,
               never()).setEnabled(false);
        verify(deleteActionPresenter,
               never()).setEnabled(false);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    @Test
    public void testRuntimeActionsForStoppedRuntime() {
        prepareRuntimeActionsSetupTest(RuntimeStatus.STOPPED.name());
        verifyRuntimeActionsCommonSetup();

        verify(startActionPresenter,
               never()).setEnabled(false);
        verify(stopActionPresenter,
               times(1)).setEnabled(false);
        verify(deleteActionPresenter,
               never()).setEnabled(false);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    @Test
    public void testRuntimeActionsForUnknownStateRuntime() {
        prepareRuntimeActionsSetupTest(RuntimeStatus.UNKNOWN.name());
        verifyRuntimeActionsCommonSetup();

        verify(startActionPresenter,
               never()).setEnabled(false);
        verify(stopActionPresenter,
               never()).setEnabled(false);
        verify(deleteActionPresenter,
               never()).setEnabled(false);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    @Test
    public void testRuntimeActionsForUndefinedStateRuntime() {
        prepareRuntimeActionsSetupTest("undefined value");
        verifyRuntimeActionsCommonSetup();

        verify(startActionPresenter,
               never()).setEnabled(false);
        verify(stopActionPresenter,
               never()).setEnabled(false);
        verify(deleteActionPresenter,
               never()).setEnabled(false);
        verify(showErrorActionPresenter,
               never()).setEnabled(true);
    }

    private void prepareRuntimeActionsSetupTest(final String status) {

        runtime = mockRuntime();
        runtime.setStatus(status);
        item = new RuntimeListItem(RUNTIME_NAME,
                                   runtime);
        presenter.setup(item);
    }

    private void verifyRuntimeActionsCommonSetup() {
        verify(view,
               times(1)).clearActionItems();
        verify(startActionPresenter,
               times(1)).setup(eq(START_RUNTIME_ACTION),
                               any(Command.class));
        verify(stopActionPresenter,
               times(1)).setup(eq(STOP_RUNTIME_ACTION),
                               any(Command.class));
        verify(deleteActionPresenter,
               times(1)).setup(eq(DELETE_RUNTIME_ACTION),
                               any(Command.class));
        verify(view,
               times(1)).addActionItem(stopActionPresenter.getView());
        verify(view,
               times(1)).addActionItem(deleteActionPresenter.getView());
        verify(view,
               times(1)).addActionItem(separatorPresenter.getView());
        verify(view,
               times(1)).addActionItem(deleteActionPresenter.getView());
        verify(stopActionPresenter,
               times(1)).setEnabled(true);
        verify(startActionPresenter,
               times(1)).setEnabled(true);
        verify(deleteActionPresenter,
               times(1)).setEnabled(true);
    }
}