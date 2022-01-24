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

package org.drools.workbench.screens.scenariosimulation.client.domelements;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.AbstractFactoriesTest;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioCellTextAreaDOMElementTest extends AbstractFactoriesTest {

    @Mock
    private ScenarioGridCell scenarioGridCellMock;
    @Mock
    private GridCellValue gridCellValueMock;
    @Mock
    private Element elementMock;
    @Mock
    private Style styleMock;
    @Mock
    private SimplePanel simplePanelMock;

    private ScenarioCellTextAreaDOMElement scenarioCellTextAreaDOMElementSpy;

    @Before
    public void setup() {
        super.setup();
        when(scenarioGridLayerMock.getDomElementContainer()).thenReturn(new AbsolutePanel());
        when(scenarioGridCellMock.getValue()).thenReturn(gridCellValueMock);
        when(textAreaMock.getElement()).thenReturn(elementMock);
        when(elementMock.getStyle()).thenReturn(styleMock);
        scenarioCellTextAreaDOMElementSpy = spy(new ScenarioCellTextAreaDOMElement(textAreaMock, scenarioGridLayerMock, scenarioGridMock) {
            {
                this.context = contextMock;
                this.scenarioGridCell = scenarioGridCellMock;
            }

            @Override
            protected SimplePanel getContainer() {
                return simplePanelMock;
            }
        });
    }

    @Test
    public void init() {
        verify(styleMock, times(1)).setWidth(100, Style.Unit.PCT);
        verify(styleMock, times(1)).setHeight(100, Style.Unit.PCT);
        verify(styleMock, times(1)).setFontSize(12, Style.Unit.PX);
        verify(styleMock, times(1)).setProperty("resize", "none");

        verify(simplePanelMock, times(1)).setWidget(eq(textAreaMock));
    }

    @Test
    public void flushSameValue() {
        when(gridCellValueMock.getValue()).thenReturn(MULTIPART_VALUE);
        scenarioCellTextAreaDOMElementSpy.flush(MULTIPART_VALUE);
        verify(scenarioGridCellMock, times(1)).setEditingMode(eq(false));
        verify(scenarioCellTextAreaDOMElementSpy, never()).internalFlush(anyString());
    }

    @Test
    public void flushDifferentValue() {
        when(gridCellValueMock.getValue()).thenReturn(TEST);
        scenarioCellTextAreaDOMElementSpy.flush(MULTIPART_VALUE);
        verify(scenarioGridCellMock, times(1)).setEditingMode(eq(false));
        verify(scenarioCellTextAreaDOMElementSpy, times(1)).internalFlush(eq(MULTIPART_VALUE));
    }

    @Test
    public void flushNullString() {
        when(gridCellValueMock.getValue()).thenReturn("");
        scenarioCellTextAreaDOMElementSpy.flush(null);
        verify(scenarioGridCellMock, times(1)).setEditingMode(eq(false));
        verify(scenarioCellTextAreaDOMElementSpy, times(1)).internalFlush(eq(null));
    }

    @Test
    public void flushEmptyStringToNullConversion() {
        when(gridCellValueMock.getValue()).thenReturn("");
        scenarioCellTextAreaDOMElementSpy.flush("");
        verify(scenarioGridCellMock, times(1)).setEditingMode(eq(false));
        // empty strings are converted to null during flush
        verify(scenarioCellTextAreaDOMElementSpy, times(1)).internalFlush(eq(null));
    }

    @Test
    public void internalFlush() {
        scenarioCellTextAreaDOMElementSpy.internalFlush(MULTIPART_VALUE);
        verify(eventBusMock, times(1)).fireEvent(isA(SetGridCellValueEvent.class));
    }

    @Test
    public void testDetachCancelEditMode() {
        scenarioCellTextAreaDOMElementSpy.detach();

        verify(scenarioGridCellMock).setEditingMode(false);
    }
}