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

package org.drools.workbench.screens.testscenario.client.page.settings;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionWidgetTest {

    @Mock
    private ListBox dateConfigurationChoices;

    @Mock
    private DateTimePicker dateTimePicker;

    @Mock
    private ExecutionTrace executionTrace;

    private ExecutionWidget executionWidget;

    @Before
    public void setUp() throws Exception {
        executionWidget = new ExecutionWidget(dateTimePicker, dateConfigurationChoices);
    }

    @Test
    public void testSetup() throws Exception {
        executionWidget.setup();

        verify(dateConfigurationChoices).addItem(TestScenarioConstants.INSTANCE.UseRealDateAndTime());
        verify(dateConfigurationChoices).addItem(TestScenarioConstants.INSTANCE.UseASimulatedDateAndTime());
        verify(dateConfigurationChoices).setSelectedIndex(0);

        verify(dateTimePicker).setFormat("yyyy-MM-dd HH:mm");
    }

    @Test()
    public void testShowTimePickerNotUsedBefore() throws Exception {
        when(executionTrace.getScenarioSimulatedDate()).thenReturn(null);

        executionWidget.show(executionTrace);

        verify(dateConfigurationChoices).setSelectedIndex(0);
        verify(dateTimePicker).setValue(null);
    }

    @Test()
    public void testShowTimePickerUsedBefore() throws Exception {
        final Date date = new Date();
        when(executionTrace.getScenarioSimulatedDate()).thenReturn(date);

        executionWidget.show(executionTrace);

        verify(dateConfigurationChoices).setSelectedIndex(1);
        verify(dateTimePicker).setValue(date);
    }
}
