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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.Optional;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popover.PopoverView;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CELL_WIDTH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DY;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ERROR_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.EXCEPTION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LARGE_LAYER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MY;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.NULL;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.RAW_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SCROLL_LEFT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SCROLL_TOP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TINY_LAYER;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationMainGridPanelMouseMoveHandlerTest extends AbstractScenarioSimulationGridHandlerTest {

    private ScenarioSimulationMainGridPanelMouseMoveHandler mouseMoveHandler;

    @Mock
    private ErrorReportPopoverPresenter errorReportPopupPresenterMock;
    @Mock
    private Scenario scenarioMock;
    @Mock
    private FactMappingValue factMappingValueMock;
    @Mock
    private AbsolutePanel scrollPanelMock;
    @Mock
    private NodeMouseMoveEvent mouseMoveEvent;
    @Mock
    private Element elementMock;

    @Before
    public void setup() {
        super.setup();
        mouseMoveHandler = spy(new ScenarioSimulationMainGridPanelMouseMoveHandler() {
            {
                errorReportPopupPresenter = errorReportPopupPresenterMock;
                scenarioGrid = scenarioGridMock;
                scenarioGridPanel = scenarioGridPanelMock;
            }

            @Override
            protected Point2D retrieveCellMiddleXYPosition(GridColumn<?> column, int uiRowIndex) {
                return new Point2D(DX, DY);
            }

            @Override
            protected Point2D convertDOMToGridCoordinateLocal(double canvasX, double canvasY) {
                return new Point2D(MX, MY);
            }
        });
        when(simulationMock.getScenarioByIndex(isA(Integer.class))).thenReturn(scenarioMock);
        when(scenarioMock.getFactMappingValue(any())).thenReturn(Optional.of(factMappingValueMock));
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);
        when(factMappingValueMock.getRawValue()).thenReturn(RAW_VALUE);
        when(factMappingValueMock.getErrorValue()).thenReturn(ERROR_VALUE);
        when(scenarioGridLayerMock.getWidth()).thenReturn(LARGE_LAYER);
        when(gridColumnMock.getWidth()).thenReturn(CELL_WIDTH);
        when(scenarioGridPanelMock.getScrollPanel()).thenReturn(scrollPanelMock);
        when(scrollPanelMock.getElement()).thenReturn(elementMock);
        when(elementMock.getScrollTop()).thenReturn(0);
        when(elementMock.getScrollLeft()).thenReturn(0);
        when(errorReportPopupPresenterMock.isShown()).thenReturn(Boolean.FALSE);
        when(mouseMoveEvent.getX()).thenReturn(MX);
        when(mouseMoveEvent.getY()).thenReturn(MY);
    }

    @Test
    public void onNodeMouseMove() {
        mouseMoveHandler.onNodeMouseMove(mouseMoveEvent);
        verify(mouseMoveHandler, times(1)).manageCoordinates(eq(MX), eq(MY));
    }

    @Test
    public void manageBodyCoordinates_Right() {
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, ROW_INDEX);
        verify(errorReportPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(RAW_VALUE, ERROR_VALUE)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq((int) (CELL_WIDTH / 2) + DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void manageBodyCoordinates_Left() {
        when(scenarioGridLayerMock.getWidth()).thenReturn(TINY_LAYER);
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, ROW_INDEX);
        verify(errorReportPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(RAW_VALUE, ERROR_VALUE)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq((int) (DX - (CELL_WIDTH / 2))),
                eq(DY),
                eq(PopoverView.Position.LEFT));
    }

    @Test
    public void manageBodyCoordinates_NullValues() {
        when(factMappingValueMock.getRawValue()).thenReturn(null);
        when(factMappingValueMock.getErrorValue()).thenReturn(null);
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, ROW_INDEX);
        verify(errorReportPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(NULL, NULL)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq((int) (CELL_WIDTH / 2) + DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void manageBodyCoordinates_Exception() {
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_EXCEPTION);
        when(factMappingValueMock.getExceptionMessage()).thenReturn(EXCEPTION);
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, ROW_INDEX);
        verify(errorReportPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithException(EXCEPTION)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.close()),
                eq((int) (CELL_WIDTH / 2) + DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void manageBodyCoordinates_WithScroll() {
        when(elementMock.getScrollTop()).thenReturn(SCROLL_TOP);
        when(elementMock.getScrollLeft()).thenReturn(SCROLL_LEFT);
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, ROW_INDEX);
        verify(errorReportPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(RAW_VALUE, ERROR_VALUE)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq((int) ((CELL_WIDTH / 2) + DX) - SCROLL_LEFT),
                eq(DY - SCROLL_TOP),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void manageBodyCoordinates_NoError() {
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.SUCCESS);
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, never()).retrieveCellMiddleXYPosition(any(), isA(Integer.class));
        verify(errorReportPopupPresenterMock, never()).show(any(), any(), any(), any(), any(), isA(Integer.class), isA(Integer.class), any());
    }

    @Test
    public void manageBodyCoordinates_sameCellAndIsShown() {
        mouseMoveHandler = spy(new ScenarioSimulationMainGridPanelMouseMoveHandler() {
            {
                currentlyShownBodyColumnIndex = 0;
                currentlyShownBodyRowIndex = 0;
                errorReportPopupPresenter = errorReportPopupPresenterMock;
                scenarioGrid = scenarioGridMock;
            }
        });
        when(errorReportPopupPresenterMock.isShown()).thenReturn(Boolean.TRUE);
        mouseMoveHandler.manageBodyCoordinates(0, 0);
        verify(simulationMock, never()).getScenarioByIndex(isA(Integer.class));
        verify(simulationDescriptorMock, never()).getFactMappingByIndex(isA(Integer.class));
        verify(scenarioMock, never()).getFactMappingValue(any());
        verify(mouseMoveHandler, never()).retrieveCellMiddleXYPosition(any(), isA(Integer.class));
        verify(errorReportPopupPresenterMock, never()).show(any(), any(), any(), any(), any(), isA(Integer.class), isA(Integer.class), any());
    }

    @Test
    public void manageBodyCoordinates_sameCellAndIsNotShown() {
        mouseMoveHandler = spy(new ScenarioSimulationMainGridPanelMouseMoveHandler() {
            {
                currentlyShownBodyColumnIndex = COLUMN_INDEX;
                currentlyShownBodyRowIndex = ROW_INDEX;
                errorReportPopupPresenter = errorReportPopupPresenterMock;
                scenarioGrid = scenarioGridMock;
                scenarioGridPanel = scenarioGridPanelMock;
            }

            @Override
            protected Point2D retrieveCellMiddleXYPosition(GridColumn<?> column, int uiRowIndex) {
                return new Point2D(DX, DY);
            }

            @Override
            protected Point2D convertDOMToGridCoordinateLocal(double canvasX, double canvasY) {
                return new Point2D(MX, MY);
            }
        });
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getScenarioByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, ROW_INDEX);
        verify(errorReportPopupPresenterMock, times(1)).show(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(RAW_VALUE, ERROR_VALUE)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq((int) (CELL_WIDTH / 2) + DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void manageBodyCoordinates_awayFromGrid() {
        mouseMoveHandler = spy(new ScenarioSimulationMainGridPanelMouseMoveHandler() {
            {
                currentlyShownBodyColumnIndex = 0;
                currentlyShownBodyRowIndex = 0;
                errorReportPopupPresenter = errorReportPopupPresenterMock;
                scenarioGrid = scenarioGridMock;
            }
        });
        boolean inGrid = mouseMoveHandler.manageBodyCoordinates(-1, -1);
        verify(simulationMock, never()).getScenarioByIndex(isA(Integer.class));
        verify(simulationDescriptorMock, never()).getFactMappingByIndex(isA(Integer.class));
        verify(scenarioMock, never()).getFactMappingValue(any());
        verify(mouseMoveHandler, never()).retrieveCellMiddleXYPosition(any(), isA(Integer.class));
        verify(errorReportPopupPresenterMock, never()).show(any(), any(), any(), any(), any(), isA(Integer.class), isA(Integer.class), any());
        assertFalse(inGrid);
    }

    @Test
    public void manageBodyCoordinates_notInGrid() {
        boolean inGrid = mouseMoveHandler.manageBodyCoordinates(-1, -1);
        verify(simulationMock, never()).getScenarioByIndex(isA(Integer.class));
        verify(simulationDescriptorMock, never()).getFactMappingByIndex(isA(Integer.class));
        verify(scenarioMock, never()).getFactMappingValue(any());
        verify(mouseMoveHandler, never()).retrieveCellMiddleXYPosition(any(), isA(Integer.class));
        verify(errorReportPopupPresenterMock, never()).show(any(), any(), any(), any(), any(), isA(Integer.class), isA(Integer.class), any());
        assertFalse(inGrid);
    }

    @Test
    public void hidePopover() {
        mouseMoveHandler.hidePopover();
        verify(errorReportPopupPresenterMock, times(1)).hide();
    }
}
