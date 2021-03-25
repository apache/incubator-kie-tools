/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.project.client.toolbox;

import java.util.ArrayList;
import java.util.List;

import elemental2.promise.Promise;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.client.service.ClientProjectOpenReusableSubprocessService;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants.SubprocessIdNotSpecified;
import static org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants.SubprocessNotFound;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(MockitoJUnitRunner.class)
public class OpenSubprocessToolboxActionTest {

    private static String UUID = "SOME_NODE_ID";
    private static String PROCESS_ID = "SomeProcess";
    private static String SUBPROCESS_ID_NOT_FOUND = "Subprocess ID is not specified.";
    private static String SUBPROCESS_NOT_FOUND = "Subprocess not found.";
    private static String PATH = "default://path";

    @Mock
    private ClientTranslationService translationService;
    @Mock
    private ClientProjectOpenReusableSubprocessService openSubprocessService;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private Promise<List<String>> promise;
    @Mock
    private Promise<Object> promiseResult;

    private OpenSubprocessToolboxAction action;

    private MouseClickEvent event = new MouseClickEvent(0, 0, 0, 0);

    @Before
    public void setUp() {
        action = mock(OpenSubprocessToolboxAction.class,
                      withSettings().useConstructor(translationService, openSubprocessService));
        doCallRealMethod().when(action).openSubprocess(any(), any());
        when(action.onMouseClick(any(), any(), any())).thenCallRealMethod();

        when(openSubprocessService.call(any())).thenReturn(promise);
        when(promise.then(any())).thenReturn(promiseResult);
        when(promiseResult.catch_(any())).thenReturn(promiseResult);
    }

    @Test
    public void testNoProcessIdSpecified() {
        when(translationService.getValue(SubprocessIdNotSpecified)).thenReturn(SUBPROCESS_ID_NOT_FOUND);

        when(action.getProcessId(any(), any())).thenReturn("");

        action.onMouseClick(canvasHandler, UUID, event);
        verify(action).showNotification(SUBPROCESS_ID_NOT_FOUND);
    }

    @Test
    public void testProcessIdSpecified() {
        when(action.getProcessId(any(), any())).thenReturn(PROCESS_ID);

        action.onMouseClick(canvasHandler, UUID, event);
        verify(action, never()).showNotification(any());
        verify(openSubprocessService).call(PROCESS_ID);
    }

    @Test
    public void testOpenSubprocessNotFound() {
        when(translationService.getValue(SubprocessNotFound, UUID)).thenReturn(SUBPROCESS_NOT_FOUND);

        List<String> emptyList = new ArrayList<>();
        action.openSubprocess(emptyList, UUID);

        verify(action).showNotification(SUBPROCESS_NOT_FOUND);
        verify(openSubprocessService, never()).openReusableSubprocess(any());
    }

    @Test
    public void testOpenSubprocessInvoked() {
        List<String> parameters = new ArrayList<>();
        parameters.add(UUID);
        parameters.add(PATH);
        action.openSubprocess(parameters, UUID);

        verify(action, never()).showNotification(any());
        verify(openSubprocessService).openReusableSubprocess(parameters);
    }
}