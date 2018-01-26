/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client.delete;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.ScenarioParentWidget;
import org.gwtbootstrap3.client.ui.base.button.AbstractToggleButton;
import org.gwtbootstrap3.client.ui.base.mixin.IconTextMixin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub({AbstractToggleButton.class, IconTextMixin.class})
@RunWith(GwtMockitoTestRunner.class)
public class DeleteExecutionTraceButtonTest {

    @Captor
    private ArgumentCaptor<ClickHandler> clickCaptor;

    @Test
    public void testInit() throws Exception {
        final DeleteExecutionTraceButton testedButton = spy(new DeleteExecutionTraceButton());
        final Scenario scenario = mock(Scenario.class);
        final ExecutionTrace executionTrace = mock(ExecutionTrace.class);
        final ScenarioParentWidget parentWidgetToRender = mock(ScenarioParentWidget.class);
        doReturn(true).when(testedButton).deleteOperationConfirmed();

        testedButton.init(scenario, executionTrace, parentWidgetToRender);

        verify(testedButton).addClickHandler(clickCaptor.capture());
        clickCaptor.getValue().onClick(null);

        verify(scenario).removeExecutionTrace(executionTrace);
        verify(parentWidgetToRender).renderEditor();
    }
}
