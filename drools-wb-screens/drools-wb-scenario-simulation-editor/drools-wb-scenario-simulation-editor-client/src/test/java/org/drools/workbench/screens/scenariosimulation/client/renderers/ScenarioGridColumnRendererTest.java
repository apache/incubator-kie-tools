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

package org.drools.workbench.screens.scenariosimulation.client.renderers;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridColumnRendererTest {

    private static final String PLACEHOLDER = "PLACEHOLDER";
    private static final String VALUE = "VALUE";
    @Mock
    private GridBodyCellRenderContext contextMock;
    @Mock
    private GridRenderer rendererMock;
    @Mock
    private ScenarioGridRendererTheme themeMock;
    @Mock
    private Text textMock;
    @Mock
    private Node<?> nodeMock;

    private ScenarioGridColumnRenderer scenarioGridColumnRenderer;

    @Before
    public void setUp() {
        doReturn(nodeMock).when(textMock).asNode();
        when(themeMock.getPlaceholderText()).thenReturn(textMock);
        when(themeMock.getBodyText()).thenReturn(textMock);
        when(rendererMock.getTheme()).thenReturn(themeMock);
        when(contextMock.getRenderer()).thenReturn(rendererMock);
        scenarioGridColumnRenderer = spy(new ScenarioGridColumnRenderer());
    }

    @Test
    public void renderCell() {
        GridCell<String> cell = null;
        Group retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNull(retrieved);
        verify(scenarioGridColumnRenderer, never()).renderPlaceholderCell(any(), eq(contextMock));
        cell = new ScenarioGridCell(null);
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNull(retrieved);
        verify(scenarioGridColumnRenderer, never()).renderPlaceholderCell(any(), eq(contextMock));
        cell = new ScenarioGridCell(new ScenarioGridCellValue(null));
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNull(retrieved);
        verify(scenarioGridColumnRenderer, never()).renderPlaceholderCell(any(), eq(contextMock));
        cell = new ScenarioGridCell(new ScenarioGridCellValue(VALUE));
        retrieved = scenarioGridColumnRenderer.renderCell(cell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, never()).renderPlaceholderCell(any(), eq(contextMock));
        ScenarioGridCell scenarioGridCell = new ScenarioGridCell(new ScenarioGridCellValue(VALUE, PLACEHOLDER));
        retrieved = scenarioGridColumnRenderer.renderCell(scenarioGridCell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, never()).renderPlaceholderCell(eq(scenarioGridCell), eq(contextMock));
        scenarioGridCell = new ScenarioGridCell(new ScenarioGridCellValue(null, PLACEHOLDER));
        retrieved = scenarioGridColumnRenderer.renderCell(scenarioGridCell, contextMock);
        assertNotNull(retrieved);
        verify(scenarioGridColumnRenderer, times(1)).renderPlaceholderCell(eq(scenarioGridCell), eq(contextMock));
    }

    @Test
    public void renderPlaceholderCell() {
        ScenarioGridCell scenarioGridCell = new ScenarioGridCell(new ScenarioGridCellValue(VALUE, PLACEHOLDER));
        Group retrieved = scenarioGridColumnRenderer.renderPlaceholderCell(scenarioGridCell, contextMock);
        assertNotNull(retrieved);
        verify(themeMock, times(1)).getPlaceholderText();
    }
}