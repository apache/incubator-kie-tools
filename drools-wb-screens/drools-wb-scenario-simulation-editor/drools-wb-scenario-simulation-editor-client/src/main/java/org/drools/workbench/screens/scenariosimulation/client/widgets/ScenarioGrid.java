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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridWidgetMouseEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn.ColumnWidthMode;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetCellSelectorMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.isSimpleJavaType;

public class ScenarioGrid extends BaseGridWidget {

    private ScenarioContextMenuRegistry scenarioContextMenuRegistry;
    private ScenarioSimulationContext scenarioSimulationContext;
    private EventBus eventBus;
    private ScenarioSimulationModel.Type type;

    public ScenarioGrid(AbstractScesimGridModel model,
                        ScenarioGridLayer scenarioGridLayer,
                        ScenarioGridRenderer renderer,
                        ScenarioContextMenuRegistry scenarioContextMenuRegistry) {
        super(model, scenarioGridLayer, scenarioGridLayer, renderer);
        this.scenarioContextMenuRegistry = scenarioContextMenuRegistry;
        setDraggable(false);
        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);
    }

    public void setContent(AbstractScesimModel abstractScesimModel, ScenarioSimulationModel.Type type) {
        this.type = type;
        ((AbstractScesimGridModel) model).clear();
        ((AbstractScesimGridModel) model).bindContent(abstractScesimModel);
        setHeaderColumns(abstractScesimModel, type);
        appendRows(abstractScesimModel);
        ((AbstractScesimGridModel) model).loadFactMappingsWidth();
        ((AbstractScesimGridModel) model).forceRefreshWidth();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        ((AbstractScesimGridModel) model).setEventBus(eventBus);
        scenarioContextMenuRegistry.setEventBus(eventBus);
    }

    public ScenarioSimulationContext getScenarioSimulationContext() {
        return scenarioSimulationContext;
    }

    public void setScenarioSimulationContext(ScenarioSimulationContext scenarioSimulationContext) {
        this.scenarioSimulationContext = scenarioSimulationContext;
    }

    @Override
    public AbstractScesimGridModel getModel() {
        return (AbstractScesimGridModel) model;
    }

    public ScenarioSimulationModel.Type getType() {
        return type;
    }

    /**
     * Unselect all cells/columns from model {@see GridData.clearSelections()}
     */
    public void clearSelections() {
        model.clearSelections();
        getLayer().batch();
    }

    /**
     * Set the current <b>selectedColumn</b> given its columnIndex
     * @param columnIndex
     */
    public void setSelectedColumn(int columnIndex) {
        ((AbstractScesimGridModel) model).selectColumn(columnIndex);
    }

    /**
     * Set the <b>selectedColumn</b> status of the model and select the header cell actually clicked
     * This should be used when header cells selection is NOT handled natively by the extended widget
     * @param columnIndex
     */
    public void setSelectedColumnAndHeader(int headerRowIndex, int columnIndex) {
        selectHeaderCell(headerRowIndex, columnIndex, false, false);
        setSelectedColumn(columnIndex);
        getLayer().batch();
    }

    /**
     * Select body cell on the model
     * @param columnIndex
     */
    public void setSelectedCell(int rowIndex, int columnIndex) {
        selectCell(rowIndex, columnIndex, false, false);
        getLayer().batch();
    }

    @Override
    protected List<NodeMouseEventHandler> getNodeMouseClickEventHandlers(final GridSelectionManager selectionManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new DefaultGridWidgetCellSelectorMouseEventHandler(selectionManager));
        return handlers;
    }

    @Override
    protected List<NodeMouseEventHandler> getNodeMouseDoubleClickEventHandlers(final GridSelectionManager selectionManager,
                                                                               final GridPinnedModeManager pinnedModeManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new ScenarioSimulationGridWidgetMouseEventHandler());
        return handlers;
    }

    protected void setHeaderColumns(AbstractScesimModel abstractScesimModel, ScenarioSimulationModel.Type type) {
        final List<FactMapping> factMappings = abstractScesimModel.getScesimModelDescriptor().getUnmodifiableFactMappings();
        boolean editableHeaders = !type.equals(ScenarioSimulationModel.Type.DMN);
        IntStream.range(0, factMappings.size())
                .forEach(columnIndex -> setHeaderColumn(columnIndex, factMappings.get(columnIndex), editableHeaders));
    }

    protected void setHeaderColumn(int columnIndex, FactMapping factMapping, boolean editableHeaders) {
        final FactIdentifier factIdentifier = factMapping.getFactIdentifier();
        String columnId = factMapping.getExpressionIdentifier().getName();
        String instanceTitle = factMapping.getFactAlias();
        String propertyTitle = factMapping.getExpressionAlias();
        String columnGroup = factMapping.getExpressionIdentifier().getType().name();
        boolean isInstanceAssigned = isInstanceAssigned(factIdentifier);
        boolean isPropertyAssigned = isPropertyAssigned(isInstanceAssigned, factMapping);
        String placeHolder = getPlaceholder(isPropertyAssigned, factMapping);
        ScenarioGridColumn scenarioGridColumn = getScenarioGridColumnLocal(instanceTitle, propertyTitle, columnId, columnGroup, factMapping.getExpressionIdentifier().getType(), placeHolder);
        scenarioGridColumn.setInstanceAssigned(isInstanceAssigned);
        scenarioGridColumn.setPropertyAssigned(isPropertyAssigned);
        scenarioGridColumn.setFactIdentifier(factIdentifier);
        scenarioGridColumn.setEditableHeaders(editableHeaders);
        if (FactMappingType.OTHER.equals(factMapping.getExpressionIdentifier().getType())) {
            scenarioGridColumn.setColumnWidthMode(ColumnWidthMode.FIXED);
            scenarioGridColumn.setMinimumWidth(scenarioGridColumn.getWidth());
        }
        if (isPropertyAssigned) {
            setDOMElementFactory(scenarioGridColumn, factMapping.getClassName());
        }
        ((AbstractScesimGridModel) model).insertColumnGridOnly(columnIndex, scenarioGridColumn);
    }

    /**
     * Set the correct <b>DOMElement</b> factory to the given <code>ScenarioGridColumn</code>
     * @param scenarioGridColumn
     * @param className
     */
    protected void setDOMElementFactory(ScenarioGridColumn scenarioGridColumn, String className) {
        if (ScenarioSimulationSharedUtils.isCollection(className)) {
            scenarioGridColumn.setFactory(((AbstractScesimGridModel) model).getCollectionEditorSingletonDOMElementFactory());
        }
    }

    /**
     * Returns <code>true</code> when
     * <p>
     * factIdentifier == FactIdentifier.DESCRIPTION
     * </p><p>
     * <b>or</b>
     * <p>
     * factIdentifier != FactIdentifier.EMPTY
     * </p><p>
     * <b>and</b>
     * </p><p>
     * factIdentifier != FactIdentifier.INDEX
     * </p><p>
     * @param factIdentifier
     * @return
     */
    protected boolean isInstanceAssigned(FactIdentifier factIdentifier) {
        if (FactIdentifier.DESCRIPTION.equals(factIdentifier)) {
            return true;
        } else {
            return !(FactIdentifier.EMPTY.equals(factIdentifier) || FactIdentifier.INDEX.equals(factIdentifier));
        }
    }

    /**
     * Returns <code>true</code> when
     * <p>
     * instanceAssigned == true
     * </p><p>
     * <b>and</b>
     * </p><p>
     * !factMapping.getExpressionElements().isEmpty()
     * </p>
     * @param instanceAssigned
     * @param factMapping
     * @return
     */
    protected boolean isPropertyAssigned(boolean instanceAssigned, FactMapping factMapping) {
        if (FactIdentifier.DESCRIPTION.equals(factMapping.getFactIdentifier())) {
            return true;
        } else {
            return instanceAssigned && (isSimpleJavaType(factMapping.getClassName()) || !factMapping.getExpressionElements().isEmpty());
        }
    }

    /**
     * Returns <code>ScenarioSimulationEditorConstants.INSTANCE.insertValue()</code> if <code>isPropertyAssigned == true</code>, <code>ScenarioSimulationEditorConstants.INSTANCE.defineValidType()</code> otherwise
     * @param isPropertyAssigned
     * @return
     */
    protected String getPlaceholder(boolean isPropertyAssigned, FactMapping factMapping) {
        String editableCellPlaceholder = ScenarioSimulationUtils.getPlaceholder(factMapping.getClassName());
        return isPropertyAssigned ? editableCellPlaceholder : ScenarioSimulationEditorConstants.INSTANCE.defineValidType();
    }

    protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String
            columnId, String columnGroup, FactMappingType factMappingType, String placeHolder) {
        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = getHeaderBuilderLocal(instanceTitle, propertyTitle, columnId, columnGroup, factMappingType);
        return getScenarioGridColumnLocal(headerBuilder, placeHolder);
    }

    protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder, String placeHolder) {
        return ScenarioSimulationUtils.getScenarioGridColumn(headerBuilder, ((AbstractScesimGridModel) model).getScenarioCellTextAreaSingletonDOMElementFactory(), placeHolder);
    }

    protected ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilderLocal(String instanceTitle, String
            propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType) {
        return ScenarioSimulationUtils.getHeaderBuilder(instanceTitle, propertyTitle, columnId, columnGroup, factMappingType, ((AbstractScesimGridModel) model).getScenarioHeaderTextBoxSingletonDOMElementFactory());
    }

    protected <T extends AbstractScesimData> void appendRows(AbstractScesimModel<T> abstractScesimModel) {
        List<T> scesimData = abstractScesimModel.getUnmodifiableData();
        IntStream.range(0, scesimData.size()).forEach(rowIndex -> appendRow(rowIndex, scesimData.get(rowIndex)));
    }

    protected <T extends AbstractScesimData> void appendRow(int rowIndex, T scesimData) {
        ((AbstractScesimGridModel) model).insertRowGridOnly(rowIndex, new ScenarioGridRow(), scesimData);
    }

    @Override
    public boolean adjustSelection(final SelectionExtension direction, final boolean isShiftKeyDown) {
        final boolean selectionChanged = super.adjustSelection(direction, isShiftKeyDown);

        signalTestTools();
        scenarioContextMenuRegistry.hideMenus();
        scenarioContextMenuRegistry.hideErrorReportPopover();

        return selectionChanged;
    }

    protected void signalTestTools() {
        eventBus.fireEvent(new DisableTestToolsEvent());

        if (!model.getSelectedHeaderCells().isEmpty()) {
            final GridData.SelectedCell cell = model.getSelectedHeaderCells().get(0);

            final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                             cell.getColumnIndex());
            final int uiRowIndex = cell.getRowIndex();

            setSelectedColumn(uiColumnIndex);

            final GridColumn column = model.getColumns().get(uiColumnIndex);
            if (uiRowIndex > 0 && column instanceof ScenarioGridColumn) {
                signalTestToolsHeaderCellSelected((ScenarioGridColumn) column,
                                                  cell,
                                                  uiColumnIndex);
            }
        }
    }

    protected void signalTestToolsHeaderCellSelected(final ScenarioGridColumn scenarioGridColumn,
                                                     final GridData.SelectedCell selectedHeaderCell,
                                                     final int uiColumnIndex) {
        final ScenarioHeaderMetaData scenarioHeaderMetaData = getColumnScenarioHeaderMetaData(scenarioGridColumn,
                                                                                              selectedHeaderCell.getRowIndex());
        if (scenarioGridColumn.isInstanceAssigned() && scenarioHeaderMetaData.getMetadataType().equals(ScenarioHeaderMetaData.MetadataType.INSTANCE)) {
            eventBus.fireEvent(new ReloadTestToolsEvent(true, true));
        } else {
            String group = ScenarioSimulationUtils.getOriginalColumnGroup(scenarioHeaderMetaData.getColumnGroup());
            final EnableTestToolsEvent enableTestToolsEvent = getEnableTestToolsEvent(this,
                                                                                      scenarioGridColumn,
                                                                                      scenarioHeaderMetaData,
                                                                                      uiColumnIndex,
                                                                                      group);
            eventBus.fireEvent(enableTestToolsEvent);
        }
    }

    //Indirection for tests
    protected ScenarioHeaderMetaData getColumnScenarioHeaderMetaData(final ScenarioGridColumn scenarioGridColumn,
                                                                     final int rowIndex) {
        return ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData(scenarioGridColumn, rowIndex);
    }

    //Indirection for tests
    protected EnableTestToolsEvent getEnableTestToolsEvent(final ScenarioGrid scenarioGrid,
                                                           final ScenarioGridColumn scenarioGridColumn,
                                                           final ScenarioHeaderMetaData scenarioHeaderMetaData,
                                                           int uiColumnIndex,
                                                           String group) {
        return ScenarioSimulationGridHeaderUtilities.getEnableTestToolsEvent(scenarioGrid,
                                                                             scenarioGridColumn,
                                                                             scenarioHeaderMetaData,
                                                                             uiColumnIndex,
                                                                             group);
    }
}