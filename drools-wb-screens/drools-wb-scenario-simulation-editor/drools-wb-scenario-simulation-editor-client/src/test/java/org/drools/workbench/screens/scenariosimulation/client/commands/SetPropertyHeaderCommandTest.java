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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetPropertyHeaderCommandTest extends AbstractCommandTest {

    private SetPropertyHeaderCommand setPropertyHeaderCommand;

    @Mock
    private List<GridColumn<?>> mockGridColumns;

    @Before
    public void setup() {
        super.setup();
        when(mockGridColumns.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        setPropertyHeaderCommand = spy(new SetPropertyHeaderCommand(scenarioGridModelMock, FULL_PACKAGE, VALUE, VALUE_CLASS_NAME, scenarioGridPanelMock, scenarioGridLayerMock, true) {

            @Override
            protected ScenarioHeaderTextBoxSingletonDOMElementFactory getHeaderTextBoxFactoryLocal() {
                return scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
            }

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder) {
                return gridColumnMock;
            }
        });
    }

    @Test
    public void executeFalse() {
        setPropertyHeaderCommand.keepData = false;
        setPropertyHeaderCommand.execute();
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(VALUE), eq(VALUE_CLASS_NAME), eq(false));
    }

    @Test
    public void executeTrue() {
        setPropertyHeaderCommand.keepData = true;
        setPropertyHeaderCommand.execute();
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(VALUE), eq(VALUE_CLASS_NAME), eq(true));
    }
}