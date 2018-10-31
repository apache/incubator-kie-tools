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

package org.drools.workbench.screens.scenariosimulation.client.factories;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioCellTextBoxDOMElementTest {

    @Mock
    private TextBox textBoxMock;
    @Mock
    private ScenarioGridLayer scenarioGridLayerMock;
    @Mock
    private ScenarioGrid scenarioGridMock;
    @Mock
    private ScenarioGridModel scenarioGridModelMock;
    @Mock
    private GridBodyCellRenderContext contextMock;
    @Mock
    private Element elementMock;
    @Mock
    private Style styleMock;

    private final static int ROW_INDEX = 1;
    private final static int COLUMN_INDEX = 2;

    private ScenarioCellTextBoxDOMElement scenarioCellTextBoxDOMElement;

    @Before
    public void setup() {
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(textBoxMock.getElement()).thenReturn(elementMock);
        when(contextMock.getRowIndex()).thenReturn(ROW_INDEX);
        when(contextMock.getColumnIndex()).thenReturn(COLUMN_INDEX);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        scenarioCellTextBoxDOMElement = spy(new ScenarioCellTextBoxDOMElement(textBoxMock, scenarioGridLayerMock, scenarioGridMock) {
            {
                this.context = contextMock;
            }
        });
    }

    @Test
    public void flush() {
        scenarioCellTextBoxDOMElement.flush("");
        verify(scenarioGridModelMock, times(1)).setCellValue(eq(ROW_INDEX), eq(COLUMN_INDEX), isA(ScenarioGridCellValue.class));
        verify(scenarioGridModelMock, times(1)).resetErrors(anyInt());
    }
}