/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionViewImpl;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.AbstractFactoriesTest;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CollectionEditorDOMElementTest extends AbstractFactoriesTest {

    private final static String WIDGET_VALUE = "WIDGET VALUE";

    private final static int TAB_INDEX = 1;

    @Mock
    private ScenarioGridCell scenarioGridCellMock;

    @Mock
    private CollectionViewImpl widgetMock;

    @Mock
    private GridCellValue gridCellValueMock;

    private CollectionEditorDOMElement collectionEditorDOMElementSpy;

    @Before
    public void setup() {
        super.setup();
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(widgetMock.getElement()).thenReturn(elementMock);
        when(widgetMock.getValue()).thenReturn(WIDGET_VALUE);
        when(widgetMock.getTabIndex()).thenReturn(TAB_INDEX);
        when(scenarioGridLayerMock.getDomElementContainer()).thenReturn(new AbsolutePanel());
        when(scenarioGridCellMock.getValue()).thenReturn(gridCellValueMock);
        collectionEditorDOMElementSpy = spy(new CollectionEditorDOMElement(widgetMock, scenarioGridLayerMock, scenarioGridMock) {
            {
                this.context = contextMock;
                this.scenarioGridCell = scenarioGridCellMock;
            }
        });
    }

    @Test
    public void getValue() {
        assertEquals(WIDGET_VALUE, collectionEditorDOMElementSpy.getValue());
    }

    @Test
    public void setValue() {
        final String newValue = "New Value";
        collectionEditorDOMElementSpy.setValue(newValue);
        verify(widgetMock, times(1)).setValue(eq(newValue));
    }

    @Test
    public void getTabIndex() {
        assertEquals(TAB_INDEX, collectionEditorDOMElementSpy.getTabIndex());
    }

    @Test
    public void setTabIndex() {
        final int newIndex = 2;
        collectionEditorDOMElementSpy.setTabIndex(newIndex);
        verify(widgetMock, times(1)).setTabIndex(eq(newIndex));
    }

    @Test
    public void setAccessKey() {
        final char newAccessKey = 'i';
        collectionEditorDOMElementSpy.setAccessKey(newAccessKey);
        verify(widgetMock, times(1)).setAccessKey(eq(newAccessKey));
    }

    @Test
    public void setFocus() {
        collectionEditorDOMElementSpy.setFocus(true);
        verify(widgetMock, times(1)).setFocus(eq(true));
        reset(widgetMock);
        collectionEditorDOMElementSpy.setFocus(false);
        verify(widgetMock, times(1)).setFocus(eq(false));
    }

    @Test
    public void flushNullGridCell() {
        collectionEditorDOMElementSpy.scenarioGridCell = null;
        final String newValue = "New Value";
        collectionEditorDOMElementSpy.flush(newValue);
        verify(scenarioGridCellMock, never()).setEditingMode(anyBoolean());
        verify(scenarioGridCellMock, never()).getValue();
        verify(collectionEditorDOMElementSpy, times(1)).internalFlush(eq(newValue));
    }

    @Test
    public void flushNotNullGridCell() {
        final String newValue = "New Value";
        collectionEditorDOMElementSpy.flush(newValue);
        verify(scenarioGridCellMock, times(1)).setEditingMode(anyBoolean());
        verify(scenarioGridCellMock, times(1)).getValue();
        verify(collectionEditorDOMElementSpy, times(1)).internalFlush(eq(newValue));

    }

    @Test
    public void internalFlush() {
        final String newValue = "New Value";
        collectionEditorDOMElementSpy.internalFlush(newValue);
        verify(eventBusMock, times(1)).fireEvent(isA(SetGridCellValueEvent.class));
    }
}