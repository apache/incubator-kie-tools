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

package org.drools.workbench.screens.scenariosimulation.client.menu;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationGridHandlerTest;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioContextMenuRegistryTest extends AbstractScenarioSimulationGridHandlerTest {

    @Mock
    private OtherContextMenu otherContextMenuMock;
    @Mock
    private HeaderGivenContextMenu headerGivenContextMenuMock;
    @Mock
    private HeaderExpectedContextMenu headerExpectedContextMenuMock;
    @Mock
    private GivenContextMenu givenContextMenuMock;
    @Mock
    private ExpectedContextMenu expectedContextMenuMock;
    @Mock
    private GridContextMenu gridContextMenuMock;
    @Mock
    private UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenuMock;
    @Mock
    private ContextMenuEvent contextMenuEventMock;
    @Mock
    private NativeEvent contextNativeEventMock;
    @Mock
    private Element contextMenuEventTargetMock;
    @Mock
    private Document contextMenuEventTargetOwnerMock;

    private ScenarioContextMenuRegistry scenarioContextMenuRegistry;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        doReturn(contextNativeEventMock).when(contextMenuEventMock).getNativeEvent();
        doReturn(contextMenuEventTargetMock).when(contextMenuEventMock).getRelativeElement();
        doReturn(contextMenuEventTargetOwnerMock).when(contextMenuEventTargetMock).getOwnerDocument();

        scenarioContextMenuRegistry = new ScenarioContextMenuRegistry(otherContextMenuMock,
                                                                      headerGivenContextMenuMock,
                                                                      headerExpectedContextMenuMock,
                                                                      givenContextMenuMock,
                                                                      expectedContextMenuMock,
                                                                      gridContextMenuMock,
                                                                      unmodifiableColumnGridContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick() {
        final int clickPointX = 5;
        final int clickPointy = 6;
        doReturn(clickPointX).when(contextNativeEventMock).getClientX();
        doReturn(clickPointy).when(contextNativeEventMock).getClientY();
        assertThat(scenarioContextMenuRegistry.manageRightClick(scenarioGridMock, contextMenuEventMock))
                .as("Click to [0,0] header cell")
                .isTrue();

        verify(expectedContextMenuMock).show(clickPointX,
                                             clickPointy,
                                             0,
                                             COLUMN_GROUP, false,
                                             simulationDescriptorMock.getType().equals(ScenarioSimulationModel.Type.RULE));
        verifyZeroInteractions(headerExpectedContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_ClickOutsideHeader() {
        final int clickPointX = 5;
        final int clickPointY = 11;
        doReturn(clickPointX).when(contextNativeEventMock).getClientX();
        doReturn(clickPointY).when(contextNativeEventMock).getClientY();
        assertThat(scenarioContextMenuRegistry.manageRightClick(scenarioGridMock, contextMenuEventMock))
                .as("Click point y more then header max y")
                .isFalse();

        verifyZeroInteractions(expectedContextMenuMock);
        verifyZeroInteractions(headerExpectedContextMenuMock);
    }

    @Test
    public void testManageHeaderRightClick_NoColumnGroup() {
        final int clickPointX = 5;
        final int clickPointY = 6;
        doReturn(clickPointX).when(contextNativeEventMock).getClientX();
        doReturn(clickPointY).when(contextNativeEventMock).getClientY();
        doReturn("").when(informationHeaderMetaDataMock).getColumnGroup();
        doReturn("EXPECT").when(informationHeaderMetaDataMock).getTitle();
        assertThat(scenarioContextMenuRegistry.manageRightClick(scenarioGridMock, contextMenuEventMock))
                .as("Context menu according to column title")
                .isTrue();

        verify(headerExpectedContextMenuMock).show(clickPointX, clickPointY);
        verifyZeroInteractions(expectedContextMenuMock);
    }

    @Test
    public void testManageBodyRightClick() {
        final int clickPointX = 5;
        final int clickPointY = 11;
        final double widgetHeight = 50.0;
        final double rowHeight = widgetHeight - HEADER_HEIGHT;
        doReturn(clickPointX).when(contextNativeEventMock).getClientX();
        doReturn(clickPointY).when(contextNativeEventMock).getClientY();
        doReturn(widgetHeight).when(scenarioGridMock).getHeight();
        doReturn(1).when(scenarioGridModelMock).getRowCount();
        final GridRow gridRowMock = mock(GridRow.class);
        doReturn(gridRowMock).when(scenarioGridModelMock).getRow(0);
        doReturn(rowHeight).when(gridRowMock).getHeight();
        assertThat(scenarioContextMenuRegistry.manageRightClick(scenarioGridMock, contextMenuEventMock))
                .as("Click to expect/given body cell")
                .isTrue();

        verify(gridContextMenuMock).show(clickPointX,
                                         clickPointY,
                                         0,
                                         0,
                                         COLUMN_GROUP,
                                         true,
                                         simulationDescriptorMock.getType().equals(ScenarioSimulationModel.Type.RULE));
    }

    @Test
    public void testManageBodyRightClick_Unmodifiable() {
        final int clickPointX = 5;
        final int clickPointY = 11;
        final double widgetHeight = 50.0;
        final double rowHeight = widgetHeight - HEADER_HEIGHT;
        doReturn(clickPointX).when(contextNativeEventMock).getClientX();
        doReturn(clickPointY).when(contextNativeEventMock).getClientY();
        doReturn(widgetHeight).when(scenarioGridMock).getHeight();
        doReturn(1).when(scenarioGridModelMock).getRowCount();
        final GridRow gridRowMock = mock(GridRow.class);
        doReturn(gridRowMock).when(scenarioGridModelMock).getRow(0);
        doReturn(rowHeight).when(gridRowMock).getHeight();
        doReturn("").when(informationHeaderMetaDataMock).getColumnGroup();
        assertThat(scenarioContextMenuRegistry.manageRightClick(scenarioGridMock, contextMenuEventMock))
                .as("Click to row number/description body cell")
                .isTrue();

        verify(unmodifiableColumnGridContextMenuMock).show(clickPointX, clickPointY, 0);
    }
}
