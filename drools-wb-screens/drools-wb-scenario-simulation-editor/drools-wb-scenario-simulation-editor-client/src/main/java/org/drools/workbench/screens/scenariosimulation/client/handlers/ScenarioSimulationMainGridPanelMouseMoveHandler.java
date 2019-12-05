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

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.types.Point2D;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popover.PopoverView;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * This class is meant to provide common implementations for <b>on hover</b> behavior to be used by both mouse keyboard handler
 */
@Dependent
public class ScenarioSimulationMainGridPanelMouseMoveHandler extends AbstractScenarioSimulationGridPanelHandler
        implements ScenarioSimulationGridPanelMouseMoveHandler {

    /* This parameter must be synchronized with POPOVER_WIDTH static variable in ErrorReportPopoverView.less */
    private static final int POPOVER_WIDTH = 200;
    private static final String NULL = "null";

    protected ErrorReportPopoverPresenter errorReportPopupPresenter;

    protected Integer currentlyShownBodyRowIndex = -1;
    protected Integer currentlyShownBodyColumnIndex = -1;

    @Override
    public void onNodeMouseMove(NodeMouseMoveEvent event) {
        manageCoordinates(event.getX(), event.getY());
    }

    @Override
    public void hidePopover() {
        errorReportPopupPresenter.hide();
    }

    @Override
    public void setErrorReportPopupPresenter(ErrorReportPopoverPresenter errorReportPopupPresenter) {
        this.errorReportPopupPresenter = errorReportPopupPresenter;
    }

    @Override
    protected boolean manageGivenExpectHeaderCoordinates(ScenarioHeaderMetaData clickedScenarioHeaderMetadata, ScenarioGridColumn scenarioGridColumn, String group, Integer uiColumnIndex) {
        return false;
    }

    @Override
    protected boolean manageBodyCoordinates(Integer uiRowIndex, Integer uiColumnIndex) {
        /* In this case, the mouse is out ot the GridLayer, then return false, without perform any action */
        if (uiColumnIndex == -1 || uiRowIndex == -1) {
            return false;
        }
        /* If the mouse position is the same of the previous one and the popover is already open, it does nothing.
         * It returns true because the click happened on an column of a grid row */
        if (uiRowIndex.equals(currentlyShownBodyRowIndex) &&
                uiColumnIndex.equals(currentlyShownBodyColumnIndex) &&
                errorReportPopupPresenter.isShown()) {
            return true;
        }
        /* It updates the coordinates of the current shown cell */
        currentlyShownBodyRowIndex = uiRowIndex;
        currentlyShownBodyColumnIndex = uiColumnIndex;
        final Optional<AbstractScesimModel<? extends AbstractScesimData>> optionalAbstractScesimModel = scenarioGrid.getModel().getAbstractScesimModel();
        final AbstractScesimModel<? extends AbstractScesimData> scesimModel = optionalAbstractScesimModel.orElseThrow(IllegalStateException::new);
        final AbstractScesimData scenarioByIndex = scesimModel.getDataByIndex(uiRowIndex);
        final FactMapping factMapping = scesimModel.getScesimModelDescriptor().getFactMappingByIndex(uiColumnIndex);
        final Optional<FactMappingValue> factMappingValueOptional = scenarioByIndex.getFactMappingValue(factMapping);
        factMappingValueOptional.ifPresent(factMappingValue -> manageFactMappingValue(factMappingValue, uiRowIndex, uiColumnIndex));
        return true;
    }

    protected void manageFactMappingValue(FactMappingValue toManage, Integer uiRowIndex, Integer uiColumnIndex) {
        /* If an error is present in the FactMappingValue, it calculates the coordinates for Popover and show it */
        if (toManage.getStatus() != null && FactMappingValueStatus.SUCCESS != toManage.getStatus()) {
            manageFailedFactMappingValue(toManage, uiRowIndex, uiColumnIndex);
        }
    }

    protected void manageFailedFactMappingValue(FactMappingValue toManage, Integer uiRowIndex, Integer uiColumnIndex) {
        /* It calculates the coordinates */
        final GridColumn<?> column = scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        final Point2D cellXYMiddleCoordinates = retrieveCellMiddleXYPosition(column, uiRowIndex);
        int cellHeight = getCellHeight(column, uiRowIndex);
        PopoverView.Position position = PopoverView.Position.RIGHT;
        int xMiddleWidth = (int) column.getWidth() / 2;
        int xPosition = (int) cellXYMiddleCoordinates.getX() + xMiddleWidth;
        /* Handling scrolling y-position */
        int yPosition = (int) cellXYMiddleCoordinates.getY();
        /* It determines if the popover should be draw on the RIGHT or in the LEFT of the cell */
        if (xPosition + POPOVER_WIDTH > scenarioGrid.getLayer().getWidth()) {
            xPosition = (int) cellXYMiddleCoordinates.getX() - xMiddleWidth;
            position = PopoverView.Position.LEFT;
        }
        /* Popover can't be to the right nor to the left (single column); put it ABOVE the cell */
        if (xPosition <= scenarioGrid.getLayer().getElement().getAbsoluteLeft()) {
            xPosition = (int) cellXYMiddleCoordinates.getX();
            yPosition = (int) cellXYMiddleCoordinates.getY() - cellHeight/2;
            position = PopoverView.Position.TOP;
        }
        int scrollX = scenarioGridPanel.getScrollPanel().getElement().getScrollLeft();
        xPosition -= scrollX;
        int scrollY = scenarioGridPanel.getScrollPanel().getElement().getScrollTop();
        yPosition -= scrollY;
        setupPopupPresenter(toManage, uiRowIndex, uiColumnIndex, xPosition, yPosition, position);
        errorReportPopupPresenter.show();
    }

    protected void setupPopupPresenter(final FactMappingValue toManage, final  int uiRowIndex, final int uiColumnIndex, final int xPosition,final  int yPosition,final  PopoverView.Position position) {
        /* Parameters for the error message */
        final Object expectedValue = toManage.getRawValue();
        final Object errorValue = toManage.getErrorValue();
        /* It shows the popover, the view depends on failed type (ERROR or EXCEPTION) */
        if (FactMappingValueStatus.FAILED_WITH_ERROR == toManage.getStatus()) {
            errorReportPopupPresenter.setup(ScenarioSimulationEditorConstants.INSTANCE.errorReason(),
                                            ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithError(
                                                    expectedValue != null ? expectedValue.toString() : NULL,
                                                    errorValue != null ? errorValue.toString() : NULL),
                                            ScenarioSimulationEditorConstants.INSTANCE.keep(),
                                            ScenarioSimulationEditorConstants.INSTANCE.apply(),
                                            () -> scenarioGrid.getEventBus().fireEvent(
                                                    new SetGridCellValueEvent(scenarioGrid.getGridWidget(),
                                                                              uiRowIndex,
                                                                              uiColumnIndex,
                                                                              errorValue != null ? errorValue.toString() : NULL)),
                                            xPosition,
                                            yPosition,
                                            position);
        } else {
            errorReportPopupPresenter.setup(ScenarioSimulationEditorConstants.INSTANCE.errorReason(),
                                            ScenarioSimulationEditorConstants.INSTANCE.errorPopoverMessageFailedWithException(
                                                    toManage.getExceptionMessage()),
                                            ScenarioSimulationEditorConstants.INSTANCE.close(),
                                            xPosition,
                                            yPosition,
                                            position);
        }
    }

    //Indirection for test
    protected Point2D retrieveCellMiddleXYPosition(GridColumn<?> column, int uiRowIndex) {
        return ScenarioSimulationUtils.getMiddleXYCell(scenarioGrid, column, false, uiRowIndex, (GridLayer) scenarioGrid.getLayer());
    }

    //Indirection for test
    protected int getCellHeight(GridColumn<?> column, int uiRowIndex) {
        return ScenarioSimulationUtils.getCellHeight(scenarioGrid, column, false, uiRowIndex);
    }
}
