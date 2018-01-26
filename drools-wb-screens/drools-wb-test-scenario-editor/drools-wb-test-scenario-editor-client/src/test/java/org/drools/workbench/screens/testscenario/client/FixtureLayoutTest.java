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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.delete.DeleteExecutionTraceButton;
import org.gwtbootstrap3.client.ui.base.button.AbstractToggleButton;
import org.gwtbootstrap3.client.ui.base.mixin.IconTextMixin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub({AbstractToggleButton.class, IconTextMixin.class})
@RunWith(GwtMockitoTestRunner.class)
public class FixtureLayoutTest {

    @Mock
    private DeleteExecutionTraceButton deleteExecutionTraceButton;

    private FixtureLayout testedLayout;

    @Before
    public void setUp() throws Exception {
        testedLayout = spy(new FixtureLayout());
        GwtMockito.useProviderForType(DeleteExecutionTraceButton.class, aClass -> deleteExecutionTraceButton);
    }

    @Test
    public void testDeleteExecutionTraceButtonNoExecutionTraceRenderedAlready() throws Exception {
        final Scenario scenario = mock(Scenario.class);
        final ExecutionTrace executionTrace = null;
        final ScenarioParentWidget parentWidgetToRender = mock(ScenarioParentWidget.class);

        testedLayout.addDeleteExecutionTraceButtonIfNotNull(scenario, executionTrace, parentWidgetToRender);

        verify(testedLayout, never()).setWidget(anyInt(), anyInt(), any(DeleteExecutionTraceButton.class));
    }

    @Test
    public void testDeleteExecutionTraceButtonSomeExecutionTraceRenderedAlready() throws Exception {
        final Scenario scenario = mock(Scenario.class);
        final ExecutionTrace executionTrace = mock(ExecutionTrace.class);
        final ScenarioParentWidget parentWidgetToRender = mock(ScenarioParentWidget.class);

        testedLayout.addDeleteExecutionTraceButtonIfNotNull(scenario, executionTrace, parentWidgetToRender);

        verify(testedLayout).setWidget(eq(0), eq(0), eq(deleteExecutionTraceButton));
        verify(deleteExecutionTraceButton).init(scenario, executionTrace, parentWidgetToRender);
    }
}
