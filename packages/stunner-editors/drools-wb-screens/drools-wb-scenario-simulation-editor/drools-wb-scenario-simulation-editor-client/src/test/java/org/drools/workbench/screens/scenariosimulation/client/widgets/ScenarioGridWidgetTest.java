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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioGridWidgetTest extends AbstractScenarioSimulationTest {

    public static final int WIDTH = 44;
    public static final int HEIGHT = 82;

    @Mock
    private Widget parentWidget;

    private ScenarioGridWidget scenarioGridWidget;

    @Before
    public void setup() {
        super.setup();
        scenarioGridWidget = spy(new ScenarioGridWidget());
        scenarioGridWidget.setScenarioGridPanel(scenarioGridPanelMock);

        when(scenarioGridWidget.getParent()).thenReturn(parentWidget);
        when(parentWidget.getOffsetHeight()).thenReturn(HEIGHT);
        when(parentWidget.getOffsetWidth()).thenReturn(WIDTH);
    }

    @Test
    public void refreshContent() {
        scenarioGridWidget.refreshContent(simulationMock);
        verify(scenarioGridModelMock, times(1)).bindContent(eq(simulationMock));
        verify(scenarioGridModelMock, times(1)).refreshErrors();
        verify(scenarioGridWidget, times(1)).onResize();
    }

    @Test
    public void onResize() {
        scenarioGridWidget.onResize();
        verify(scenarioGridWidget, times(1)).setPixelSize(eq(WIDTH), eq(HEIGHT));
        verify(scenarioGridPanelMock, times(1)).onResize();
    }

    @Test
    public void unregister() {
        scenarioGridWidget.unregister();
        verify(scenarioGridPanelMock, times(1)).unregister();
    }

    @Test
    public void clearSelection() {
        scenarioGridWidget.clearSelections();
        verify(scenarioGridModelMock, times(1)).clearSelections();
    }

    @Test
    public void resetErrors() {
        scenarioGridWidget.resetErrors();
        verify(scenarioGridModelMock, times(1)).resetErrors();
    }

    @Test
    public void selectAndFocus() {
        scenarioGridWidget.selectAndFocus();
        verify(scenarioGridMock, times(1)).select();
        verify(scenarioGridPanelMock, times(1)).setFocus(eq(true));
        verify(scenarioGridPanelMock, times(1)).ensureCellIsSelected();
        assertTrue(scenarioGridWidget.selected);
    }

    @Test
    public void deselectAndUnFocus() {
        scenarioGridWidget.deselectAndUnFocus();
        verify(scenarioGridMock, times(1)).deselect();
        verify(scenarioGridPanelMock, times(1)).setFocus(eq(false));
        assertFalse(scenarioGridWidget.selected);
    }
}
