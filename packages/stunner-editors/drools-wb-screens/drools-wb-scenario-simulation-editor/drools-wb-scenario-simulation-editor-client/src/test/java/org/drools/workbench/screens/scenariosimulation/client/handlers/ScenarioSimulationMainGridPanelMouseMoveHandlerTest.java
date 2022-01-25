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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import elemental2.dom.HTMLDivElement;
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
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SMALLEST_LAYER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TINY_LAYER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationMainGridPanelMouseMoveHandlerTest extends AbstractScenarioSimulationGridHandlerTest {

    private final int ABSOLUTE_LEFT = 0;
    private final int CELL_HEIGHT = 30;
    private static final String BR = "<br>";

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
    @Mock
    private HTMLDivElement layerElementMock;

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

            @Override
            protected int getCellHeight(GridColumn<?> column, int uiRowIndex) {
                return CELL_HEIGHT;
            }
        });
        when(simulationMock.getDataByIndex(isA(Integer.class))).thenReturn(scenarioMock);
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
        layerElementMock.offsetLeft = ABSOLUTE_LEFT;
        when(scenarioGridLayerMock.getElement()).thenReturn(layerElementMock);
    }

    @Test
    public void onNodeMouseMove() {
        mouseMoveHandler.onNodeMouseMove(mouseMoveEvent);
        verify(mouseMoveHandler, times(1)).manageCoordinates(eq(MX), eq(MY));
    }

    @Test
    public void hidePopover() {
        mouseMoveHandler.hidePopover();
        verify(errorReportPopupPresenterMock, times(1)).hide();
    }

    @Test
    public void manageBodyCoordinatesNotInGrid() {
        boolean inGrid = mouseMoveHandler.manageBodyCoordinates(-1, -1);
        verify(simulationMock, never()).getDataByIndex(isA(Integer.class));
        verify(simulationDescriptorMock, never()).getFactMappingByIndex(isA(Integer.class));
        verify(scenarioMock, never()).getFactMappingValue(any());
        verify(mouseMoveHandler, never()).retrieveCellMiddleXYPosition(any(), isA(Integer.class));
        verify(errorReportPopupPresenterMock, never()).setup(any(), any(), any(), any(), any(), anyInt(), anyInt(), any());
        assertFalse(inGrid);
    }

    @Test
    public void manageBodyCoordinatesSameCellIsShown() {
        mouseMoveHandler.currentlyShownBodyRowIndex = ROW_INDEX;
        mouseMoveHandler.currentlyShownBodyColumnIndex = COLUMN_INDEX;
        doReturn(true).when(errorReportPopupPresenterMock).isShown();
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(scenarioGridMock, never()).getModel();
        verify(simulationMock, never()).getDataByIndex(eq(ROW_INDEX));
    }

    @Test
    public void manageBodyCoordinatesSameCellNotShown() {
        mouseMoveHandler.currentlyShownBodyRowIndex = ROW_INDEX;
        mouseMoveHandler.currentlyShownBodyColumnIndex = COLUMN_INDEX;
        doReturn(false).when(errorReportPopupPresenterMock).isShown();
        doNothing().when(mouseMoveHandler).manageFactMappingValue(eq(factMappingValueMock),
                                                                  eq(ROW_INDEX),
                                                                  eq(COLUMN_INDEX));
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(scenarioGridMock, times(1)).getModel();
        verify(simulationMock, times(1)).getDataByIndex(eq(ROW_INDEX));
    }

    @Test
    public void manageBodyCoordinatesNotShownWithoutFactMappingValue() {
        doReturn(false).when(errorReportPopupPresenterMock).isShown();
        doReturn(Optional.empty()).when(scenarioMock).getFactMappingValue(eq(factMappingMock));
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(scenarioGridMock, times(1)).getModel();
        verify(simulationMock, times(1)).getDataByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, never()).manageFactMappingValue(
                isA(FactMappingValue.class),
                anyInt(),
                anyInt());
    }

    @Test
    public void manageBodyCoordinatesNotShownWithFactMappingValue() {
        doReturn(false).when(errorReportPopupPresenterMock).isShown();
        doReturn(Optional.of(factMappingValueMock)).when(scenarioMock).getFactMappingValue(eq(factMappingMock));
        doNothing().when(mouseMoveHandler).manageFactMappingValue(eq(factMappingValueMock),
                                                                  eq(ROW_INDEX),
                                                                  eq(COLUMN_INDEX));
        mouseMoveHandler.manageBodyCoordinates(ROW_INDEX, COLUMN_INDEX);
        verify(scenarioGridMock, times(1)).getModel();
        verify(simulationMock, times(1)).getDataByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(mouseMoveHandler, times(1)).manageFactMappingValue(
                eq(factMappingValueMock),
                eq(ROW_INDEX),
                eq(COLUMN_INDEX));
    }

    @Test
    public void manageFactMappingValueSuccess() {
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.SUCCESS);
        mouseMoveHandler.manageFactMappingValue(factMappingValueMock, ROW_INDEX, COLUMN_INDEX);
        verify(mouseMoveHandler, never()).manageFailedFactMappingValue(eq(factMappingValueMock),
                                                                       eq(ROW_INDEX),
                                                                       eq(COLUMN_INDEX));
    }

    @Test
    public void manageFactMappingValueNotSuccess() {
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_EXCEPTION);
        doNothing().when(mouseMoveHandler).manageFailedFactMappingValue(eq(factMappingValueMock),
                                                                        eq(ROW_INDEX),
                                                                        eq(COLUMN_INDEX));
        mouseMoveHandler.manageFactMappingValue(factMappingValueMock, ROW_INDEX, COLUMN_INDEX);
        verify(mouseMoveHandler, times(1)).manageFailedFactMappingValue(eq(factMappingValueMock),
                                                                        eq(ROW_INDEX),
                                                                        eq(COLUMN_INDEX));
    }

    @Test
    public void manageFailedFactMappingValueRight() {
        int expectedDx = (int) (CELL_WIDTH / 2) + DX;
        doNothing().when(mouseMoveHandler).setupPopupPresenter(eq(factMappingValueMock),
                                                               eq(0),
                                                               eq(COLUMN_INDEX),
                                                               eq(expectedDx),
                                                               eq(DY),
                                                               eq(PopoverView.Position.RIGHT));
        mouseMoveHandler.manageFailedFactMappingValue(factMappingValueMock, 0, COLUMN_INDEX);
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, 0);
        verify(mouseMoveHandler, times(1)).setupPopupPresenter(eq(factMappingValueMock),
                                                               eq(0),
                                                               eq(COLUMN_INDEX),
                                                               eq(expectedDx),
                                                               eq(DY),
                                                               eq(PopoverView.Position.RIGHT));
        verify(errorReportPopupPresenterMock, times(1)).show();
    }

    @Test
    public void manageFailedFactMappingValueLeft() {
        when(scenarioGridLayerMock.getWidth()).thenReturn(TINY_LAYER);
        int expectedDx = (int) (DX - (CELL_WIDTH / 2));
        doNothing().when(mouseMoveHandler).setupPopupPresenter(eq(factMappingValueMock),
                                                               eq(0),
                                                               eq(COLUMN_INDEX),
                                                               eq(expectedDx),
                                                               eq(DY),
                                                               eq(PopoverView.Position.RIGHT));
        mouseMoveHandler.manageFailedFactMappingValue(factMappingValueMock, 0, COLUMN_INDEX);
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, 0);
        verify(mouseMoveHandler, times(1)).setupPopupPresenter(eq(factMappingValueMock),
                                                               eq(0),
                                                               eq(COLUMN_INDEX),
                                                               eq(expectedDx),
                                                               eq(DY),
                                                               eq(PopoverView.Position.LEFT));
        verify(errorReportPopupPresenterMock, times(1)).show();
    }

    @Test
    public void manageFailedFactMappingValueTop() {
        when(scenarioGridLayerMock.getWidth()).thenReturn(SMALLEST_LAYER);
        layerElementMock.offsetLeft = 4000;
        int expectedDy = DY - (CELL_HEIGHT / 2);
        doNothing().when(mouseMoveHandler).setupPopupPresenter(eq(factMappingValueMock),
                                                               eq(0),
                                                               eq(COLUMN_INDEX),
                                                               eq(DX),
                                                               eq(expectedDy),
                                                               eq(PopoverView.Position.RIGHT));
        mouseMoveHandler.manageFailedFactMappingValue(factMappingValueMock, 0, COLUMN_INDEX);
        verify(mouseMoveHandler, times(1)).retrieveCellMiddleXYPosition(gridColumnMock, 0);

        verify(mouseMoveHandler, times(1)).setupPopupPresenter(eq(factMappingValueMock),
                                                               eq(0),
                                                               eq(COLUMN_INDEX),
                                                               eq(DX),
                                                               eq(expectedDy),
                                                               eq(PopoverView.Position.TOP));

        verify(errorReportPopupPresenterMock, times(1)).show();
    }

    @Test
    public void setupPopupPresenterWithScrollFailedWithError_NullPath() {
        when(elementMock.getScrollTop()).thenReturn(SCROLL_TOP);
        when(elementMock.getScrollLeft()).thenReturn(SCROLL_LEFT);
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);
        when(factMappingValueMock.getCollectionPathToValue()).thenReturn(null);
        mouseMoveHandler.setupPopupPresenter(factMappingValueMock, ROW_INDEX, COLUMN_INDEX, DX, DY, PopoverView.Position.RIGHT);
        verify(mouseMoveHandler, times(1)).showErrorPopoverWithSuggestion(eq(RAW_VALUE), eq(ERROR_VALUE), eq(ROW_INDEX), eq(COLUMN_INDEX), eq(DX), eq(DY), eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void setupPopupPresenterWithScrollFailedWithError_WithPath() {
        when(elementMock.getScrollTop()).thenReturn(SCROLL_TOP);
        when(elementMock.getScrollLeft()).thenReturn(SCROLL_LEFT);
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);
        when(factMappingValueMock.getCollectionPathToValue()).thenReturn(Arrays.asList("field1"));
        mouseMoveHandler.setupPopupPresenter(factMappingValueMock, ROW_INDEX, COLUMN_INDEX, DX, DY, PopoverView.Position.RIGHT);
        String errorMessage = mouseMoveHandler.getCollectionHTMLErrorMessage(ERROR_VALUE, Arrays.asList("field1"));
        verify(mouseMoveHandler, times(1)).showErrorPopoverWithoutSuggestion(eq(errorMessage), eq(DX), eq(DY), eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void setupPopupPresenterWithScrollFailedWithException() {
        when(elementMock.getScrollTop()).thenReturn(SCROLL_TOP);
        when(elementMock.getScrollLeft()).thenReturn(SCROLL_LEFT);
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_EXCEPTION);
        when(factMappingValueMock.getExceptionMessage()).thenReturn(EXCEPTION);
        mouseMoveHandler.setupPopupPresenter(factMappingValueMock, ROW_INDEX, COLUMN_INDEX, DX, DY, PopoverView.Position.RIGHT);
        verify(mouseMoveHandler, times(1)).showErrorPopoverWithoutSuggestion(eq(EXCEPTION), eq(DX), eq(DY), eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void showErrorPopoverWithoutSuggestion() {
        mouseMoveHandler.showErrorPopoverWithoutSuggestion(ERROR_VALUE, DX, DY, PopoverView.Position.RIGHT);
        verify(errorReportPopupPresenterMock, times(1)).setup(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ERROR_VALUE),
                eq(ScenarioSimulationEditorConstants.INSTANCE.close()),
                eq(DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void showErrorPopoverWithSuggestion() {
        when(elementMock.getScrollTop()).thenReturn(SCROLL_TOP);
        when(elementMock.getScrollLeft()).thenReturn(SCROLL_LEFT);
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);
        mouseMoveHandler.showErrorPopoverWithSuggestion(RAW_VALUE, ERROR_VALUE, ROW_INDEX, COLUMN_INDEX, DX, DY, PopoverView.Position.RIGHT);
        String expectedValue = mouseMoveHandler.decorateWithStrongHTMLTag(RAW_VALUE);
        String wrongValue = mouseMoveHandler.decorateWithStrongHTMLTag(ERROR_VALUE);
        verify(errorReportPopupPresenterMock, times(1)).setup(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(expectedValue, wrongValue)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq(DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void showErrorPopoverWithSuggestion_NULLValues() {
        when(elementMock.getScrollTop()).thenReturn(SCROLL_TOP);
        when(elementMock.getScrollLeft()).thenReturn(SCROLL_LEFT);
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);
        mouseMoveHandler.showErrorPopoverWithSuggestion(null, null, ROW_INDEX, COLUMN_INDEX, DX, DY, PopoverView.Position.RIGHT);
        String expectedValue = mouseMoveHandler.decorateWithStrongHTMLTag(NULL);
        String wrongValue = mouseMoveHandler.decorateWithStrongHTMLTag(NULL);
        verify(errorReportPopupPresenterMock, times(1)).setup(
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorReason()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(expectedValue, wrongValue)),
                eq(ScenarioSimulationEditorConstants.INSTANCE.keep()),
                eq(ScenarioSimulationEditorConstants.INSTANCE.apply()),
                isA(Command.class),
                eq(DX),
                eq(DY),
                eq(PopoverView.Position.RIGHT));
    }

    @Test
    public void getCollectionHTMLErrorMessage_EmptyPath() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverGenericCollectionErrorMessage(),
                     mouseMoveHandler.getCollectionHTMLErrorMessage(ERROR_VALUE, Collections.emptyList()));
    }

    @Test
    public void getCollectionHTMLErrorMessage_OnePathItemWithWrongValue() {
        String field1 = "Field1";
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLFailureMessage(mouseMoveHandler.decorateWithEMHTMLTag(field1))
                             + BR
                             + ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLValue(mouseMoveHandler.decorateWithStrongHTMLTag(ERROR_VALUE)),
                     mouseMoveHandler.getCollectionHTMLErrorMessage(ERROR_VALUE, Arrays.asList(field1)));
    }

    @Test
    public void getCollectionHTMLErrorMessage_OnePathItemWithoutWrongValue() {
        String field1 = "Field1";
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLFailureMessage(mouseMoveHandler.decorateWithEMHTMLTag(field1)),
                     mouseMoveHandler.getCollectionHTMLErrorMessage(null, Arrays.asList(field1)));
    }

    @Test
    public void getCollectionHTMLErrorMessage_PathItemsWithWrongValue() {
        String field1 = "Field1";
        String field2 = "Field2";
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLFailureMessage(mouseMoveHandler.decorateWithEMHTMLTag(field1))
                             + BR
                             + ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLField(mouseMoveHandler.decorateWithEMHTMLTag(mouseMoveHandler.generateFieldsHierarchy(Arrays.asList(field2))))
                             + ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLValue(mouseMoveHandler.decorateWithStrongHTMLTag(ERROR_VALUE)),
                     mouseMoveHandler.getCollectionHTMLErrorMessage(ERROR_VALUE, Arrays.asList(field1, field2)));
    }

    @Test
    public void getCollectionHTMLErrorMessage_PathItemsWithoutWrongValue() {
        String field1 = "Field1";
        String field2 = "Field2";
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLFailureMessage(mouseMoveHandler.decorateWithEMHTMLTag(field1))
                             + BR
                             + ScenarioSimulationEditorConstants.INSTANCE.errorPopoverCollectionHTMLField(mouseMoveHandler.decorateWithEMHTMLTag(mouseMoveHandler.generateFieldsHierarchy(Arrays.asList(field2)))),
                     mouseMoveHandler.getCollectionHTMLErrorMessage(null, Arrays.asList(field1, field2))
        );
    }

    @Test
    public void decorateWithStrongHTMLTag() {
        assertEquals("<strong>\"" + RAW_VALUE + "\"</strong>", mouseMoveHandler.decorateWithStrongHTMLTag(RAW_VALUE));
    }

    @Test
    public void decorateWithEMHTMLTag() {
        assertEquals("<em>" + RAW_VALUE + "</em>", mouseMoveHandler.decorateWithEMHTMLTag(RAW_VALUE));
    }

    @Test
    public void generateFieldsHierarchy() {
        assertEquals("Field1<br>", mouseMoveHandler.generateFieldsHierarchy(Arrays.asList("Field1")));
        assertEquals("<ul><li>Field1<ul><li>Field2<ul><li>Field3</li></ul></li></ul></li></ul>",
                     mouseMoveHandler.generateFieldsHierarchy(Arrays.asList("Field1", "Field2", "Field3")));
    }

}
