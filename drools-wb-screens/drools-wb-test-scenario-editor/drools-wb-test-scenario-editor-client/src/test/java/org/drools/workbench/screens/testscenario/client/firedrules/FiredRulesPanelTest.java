/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client.firedrules;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FiredRulesPanelTest {

    @Mock
    private ExecutionTrace executionTrace;

    @Mock
    private FiredRulesTable table;

    @Mock
    private ShowFiredRulesButton showFiredRulesButton;

    @Mock
    private HideFiredRulesButton hideFiredRulesButton;

    private FiredRulesPanel panel;

    @Before
    public void setUp() throws Exception {
        panel = spy(new FiredRulesPanel(executionTrace));
    }

    @Test
    public void testInit() throws Exception {
        doReturn(table).when(panel).firedRulesTable();
        doReturn(showFiredRulesButton).when(panel).showFiredRulesButton();
        doReturn(hideFiredRulesButton).when(panel).hideFiredRulesButton();

        panel.init();

        verify(table).init();
        verify(showFiredRulesButton).init(table, hideFiredRulesButton);
        verify(hideFiredRulesButton).init(table, showFiredRulesButton);

        verify(panel).add(table);
        verify(panel).add(showFiredRulesButton);
        verify(panel).add(hideFiredRulesButton);
    }
}
