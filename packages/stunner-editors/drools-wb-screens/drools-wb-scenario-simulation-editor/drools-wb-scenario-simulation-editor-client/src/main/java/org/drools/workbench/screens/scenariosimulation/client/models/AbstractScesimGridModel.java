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
package org.drools.workbench.screens.scenariosimulation.client.models;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioExpressionCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias;

/**
 * Abstract <code>BaseGridData</code> to be extended by actual implementations (<b>Simulation</b> and <code>Background</code>)
 */
public abstract class AbstractScesimGridModel<T extends AbstractScesimModel<E>, E extends AbstractScesimData> extends BaseGridData {

    public static final int HEADER_ROW_COUNT = 3;

    protected T abstractScesimModel;

    protected EventBus eventBus;

    protected AtomicInteger columnCounter = new AtomicInteger(0);

    protected GridColumn<?> selectedColumn = null;

    protected Set<String> simpleJavaTypeInstancesName;

    protected CollectionEditorSingletonDOMElementFactory collectionEditorSingletonDOMElementFactory;
    protected ScenarioCellTextAreaSingletonDOMElementFactory scenarioCellTextAreaSingletonDOMElementFactory;
    protected ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactory;
    protected ScenarioExpressionCellTextAreaSingletonDOMElementFactory scenarioExpressionCellTextAreaSingletonDOMElementFactory;

    public AbstractScesimGridModel() {
    }

    public AbstractScesimGridModel(boolean isMerged) {
        super(isMerged);
        setHeaderRowCount(HEADER_ROW_COUNT);
    }

    public abstract GridWidget getGridWidget();

    /**
     * Method to bind the data serialized inside backend <code>AbstractScesimModel</code>
     * @param abstractScesimModel
     */
    public void bindContent(T abstractScesimModel) {
        this.abstractScesimModel = abstractScesimModel;
        checkSimulation();
        columnCounter.set(abstractScesimModel.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public int nextColumnCount() {
        return columnCounter.getAndIncrement();
    }

    public Map.Entry<String, String> getValidPlaceholders() {
        String instanceTitle;
        String propertyTitle;
        do {
            int nextColumnCount = nextColumnCount();
            instanceTitle = FactMapping.getInstancePlaceHolder(nextColumnCount);
            propertyTitle = FactMapping.getPropertyPlaceHolder(nextColumnCount);
        } while (!isNewInstanceName(instanceTitle) ||
                !isNewPropertyName(propertyTitle));
        return new AbstractMap.SimpleEntry<>(instanceTitle, propertyTitle);
    }

    public CollectionEditorSingletonDOMElementFactory getCollectionEditorSingletonDOMElementFactory() {
        return collectionEditorSingletonDOMElementFactory;
    }

    public void setCollectionEditorSingletonDOMElementFactory(CollectionEditorSingletonDOMElementFactory collectionEditorSingletonDOMElementFactory) {
        this.collectionEditorSingletonDOMElementFactory = collectionEditorSingletonDOMElementFactory;
    }

    public ScenarioCellTextAreaSingletonDOMElementFactory getScenarioCellTextAreaSingletonDOMElementFactory() {
        return scenarioCellTextAreaSingletonDOMElementFactory;
    }

    public void setScenarioCellTextAreaSingletonDOMElementFactory(ScenarioCellTextAreaSingletonDOMElementFactory scenarioCellTextAreaSingletonDOMElementFactory) {
        this.scenarioCellTextAreaSingletonDOMElementFactory = scenarioCellTextAreaSingletonDOMElementFactory;
    }

    public ScenarioHeaderTextBoxSingletonDOMElementFactory getScenarioHeaderTextBoxSingletonDOMElementFactory() {
        return scenarioHeaderTextBoxSingletonDOMElementFactory;
    }

    public void setScenarioHeaderTextBoxSingletonDOMElementFactory(ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactory) {
        this.scenarioHeaderTextBoxSingletonDOMElementFactory = scenarioHeaderTextBoxSingletonDOMElementFactory;
    }

    public ScenarioExpressionCellTextAreaSingletonDOMElementFactory getScenarioExpressionCellTextAreaSingletonDOMElementFactory() {
        return scenarioExpressionCellTextAreaSingletonDOMElementFactory;
    }

    public void setScenarioExpressionCellTextAreaSingletonDOMElementFactory(ScenarioExpressionCellTextAreaSingletonDOMElementFactory scenarioExpressionCellTextAreaSingletonDOMElementFactory) {
        this.scenarioExpressionCellTextAreaSingletonDOMElementFactory = scenarioExpressionCellTextAreaSingletonDOMElementFactory;
    }

    /**
     * It detaches all DomElement resources for selected DOMElementFactorys
     */
    public void destroyAllTextAreaDOMElementFactoryResources() {
        scenarioCellTextAreaSingletonDOMElementFactory.destroyResources();
        scenarioExpressionCellTextAreaSingletonDOMElementFactory.destroyResources();
        scenarioHeaderTextBoxSingletonDOMElementFactory.destroyResources();
    }

    /**
     * This method <i>append</i> a new row to the grid <b>and</b> to the underlying model
     * @param row
     */
    @Override
    public void appendRow(GridRow row) {
        checkSimulation();
        super.appendRow(row);
        int rowIndex = getRowCount() - 1;
        commonAddRow(rowIndex);
    }

    /**
     * This method <i>insert</i> a row to the grid and populate it with values taken from given <code>AbstractScesimData</code>
     * @param row
     */
    public abstract void insertRowGridOnly(final int rowIndex,
                                           final GridRow row,
                                           final E abstractScesimData);

    /**
     * This method <i>insert</i> a row to the grid
     * @param row
     */
    public void insertRowGridOnly(final int rowIndex, final GridRow row) {
        checkSimulation();
        super.insertRow(rowIndex, row);
    }

    /**
     * This method <i>insert</i> a new row to the grid <b>and</b> to the underlying model
     * @param row
     */
    @Override
    public void insertRow(int rowIndex, GridRow row) {
        insertRowGridOnly(rowIndex, row);
        commonAddRow(rowIndex);
    }

    /**
     * This method <i>delete</i> the row at the given index from both the grid <b>and</b> the underlying model
     * @param rowIndex
     */
    @Override
    public Range deleteRow(int rowIndex) {
        checkSimulation();
        Range toReturn = super.deleteRow(rowIndex);
        abstractScesimModel.removeDataByIndex(rowIndex);
        updateIndexColumn();
        return toReturn;
    }

    /**
     * This method <i>duplicate</i> the row at the given index from both the grid <b>and</b> the underlying model
     * and insert just below the original one
     * @param rowIndex
     */
    public void duplicateRow(int rowIndex, GridRow row) {
        checkSimulation();
        int newRowIndex = rowIndex + 1;
        final E toDuplicate = abstractScesimModel.cloneData(rowIndex, newRowIndex);
        insertRowGridOnly(newRowIndex, row, toDuplicate);
    }

    /**
     * This method <i>duplicates</i> the row values at the source column index from both the grid <b>and</b> the underlying model
     * and inserts at the target column index
     * @param originalColumnIndex
     * @param newColumnIndex
     */
    public void duplicateColumnValues(int originalColumnIndex, int newColumnIndex) {
        checkSimulation();
        List<GridCellValue<?>> originalValues = new ArrayList<>();
        IntStream.range(0, getRowCount())
                .forEach(rowIndex -> originalValues.add(getCell(rowIndex, originalColumnIndex).getValue()));
        IntStream.range(0, getRowCount())
                .forEach(rowIndex -> setCellValue(rowIndex, newColumnIndex, originalValues.get(rowIndex)));
    }

    /**
     * This method <i>insert</i> a new column to the grid <b>without</b> modify underlying model
     * @param index
     * @param column
     */
    public void insertColumnGridOnly(final int index, final GridColumn<?> column) {
        checkSimulation();
        super.insertColumn(index, column);
    }

    /**
     * This method <i>insert</i> a new column to the grid <b>and</b> to the underlying model
     * @param index
     * @param column
     */
    @Override
    public void insertColumn(final int index, final GridColumn<?> column) {
        checkSimulation();
        commonAddColumn(index, column);
    }

    /**
     * This method <i>delete</i> the column at the given index from both the grid <b>and</b> the underlying model
     * @param columnIndex
     */
    public void deleteColumn(int columnIndex) {
        checkSimulation();
        final GridColumn<?> toDelete = getColumns().get(columnIndex);
        deleteColumn(toDelete);
        abstractScesimModel.removeFactMappingByIndex(columnIndex);
    }

    /**
     * This method <i>delete</i> the <b>whole instance</b> of the column at the given index from both the grid <b>and</b> the underlying model
     * @param columnIndex
     */
    public void deleteInstance(int columnIndex) {
        checkSimulation();
        Range instanceRange = getInstanceLimits(columnIndex);
        IntStream.iterate(instanceRange.getMaxRowIndex(), i -> i - 1)
                .limit(instanceRange.getMaxRowIndex() - instanceRange.getMinRowIndex() + 1L)
                .forEach(this::deleteColumn);
    }

    /**
     * This method update the instance mapped inside a given column
     * @param columnIndex
     * @param column
     */
    public void updateColumnInstance(int columnIndex, final GridColumn<?> column) {
        checkSimulation();
        replaceColumn(columnIndex, column);
    }

    /**
     * This method update the type mapped inside a give column and updates the underlying model
     * @param columnIndex
     * @param column
     * @param propertyNameElements
     * @param lastLevelClassName
     * @param keepData
     */
    public void updateColumnProperty(int columnIndex, final GridColumn<?> column, List<String> propertyNameElements, String lastLevelClassName, boolean keepData, FactMappingValueType valueType, ScenarioSimulationModel.Type type) {
        checkSimulation();
        List<GridCellValue<?>> originalValues = new ArrayList<>();
        if (keepData) {
            IntStream.range(0, getRowCount())
                    .forEach(rowIndex -> originalValues.add(getCell(rowIndex, columnIndex).getValue()));
        }
        replaceColumn(columnIndex, column);
        final FactMapping factMappingByIndex = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
        factMappingByIndex.setFactMappingValueType(valueType);
        List<String> propertyNameElementsClone = getPropertyNameElementsWithoutAlias(propertyNameElements,
                                                                                     factMappingByIndex.getFactIdentifier(),
                                                                                     type);
        // This is because the value starts with the alias of the fact; i.e. it may be Book.name but also Bookkk.name,
        // while the first element of ExpressionElements is always the class name
        IntStream.range(0, propertyNameElementsClone.size())
                .forEach(stepIndex -> factMappingByIndex.addExpressionElement(propertyNameElementsClone.get(stepIndex), lastLevelClassName));
        if (keepData) {
            IntStream.range(0, getRowCount())
                    .forEach(rowIndex -> setCellValue(rowIndex, columnIndex, originalValues.get(rowIndex)));
        }
    }

    /**
     * This method replace a column at columnIndex position with a new column. It also save and restore width of the columns
     * @param columnIndex
     * @param column
     */
    protected void replaceColumn(int columnIndex, GridColumn<?> column) {
        List<Double> widthsToRestore = getColumns().stream().map(GridColumn::getWidth).collect(Collectors.toList());
        deleteColumn(columnIndex);
        String group = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnGroup();
        String columnId = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnId();
        ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(columnIndex, column, ei);
        /* Restoring the expected columns dimension, overriding the automatic resizing */
        IntStream.range(0, widthsToRestore.size())
                .forEach(index -> getColumns().get(index).setWidth(widthsToRestore.get(index)));
    }

    /**
     * This method <i>set</i> a cell value to the grid <b>without</b> modify underlying model
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier
     */
    public Range setCellGridOnly(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        return super.setCell(rowIndex, columnIndex, cellSupplier);
    }

    /**
     * This method <i>set</i> a cell value to the grid <b>and</b> to the underlying model
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier
     */
    @Override
    public Range setCell(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        Range toReturn = super.setCell(rowIndex, columnIndex, cellSupplier);
        try {
            Optional<?> optionalValue = getCellValue(getCell(rowIndex, columnIndex));
            Object rawValue = optionalValue.orElse(null);
            String cellValue = (rawValue instanceof String) ? (String) rawValue : null;
            E scenarioByIndex = abstractScesimModel.getDataByIndex(rowIndex);
            FactMapping factMappingByIndex = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
            FactIdentifier factIdentifier = factMappingByIndex.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = factMappingByIndex.getExpressionIdentifier();
            scenarioByIndex.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, cellValue);
        } catch (Exception e) {
            toReturn = super.deleteCell(rowIndex, columnIndex);
            eventBus.fireEvent(new ScenarioGridReloadEvent(getGridWidget()));
        }
        return toReturn;
    }

    @Override
    public Range setCellValue(int rowIndex, int columnIndex, GridCellValue<?> value) {
        return setCell(rowIndex, columnIndex, () -> {
            ScenarioGridCell newCell = new ScenarioGridCell((ScenarioGridCellValue) value);
            FactMapping factMappingByIndex = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
            if (ScenarioSimulationSharedUtils.isCollectionOrMap((factMappingByIndex.getClassName()))) {
                newCell.setListMap(ScenarioSimulationSharedUtils.isList((factMappingByIndex.getClassName())));
            }
            return newCell;
        });
    }

    @Override
    public Range deleteCell(int rowIndex, int columnIndex) {
        FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
        abstractScesimModel.getDataByIndex(rowIndex)
                .removeFactMappingValueByIdentifiers(factMapping.getFactIdentifier(), factMapping.getExpressionIdentifier());
        return super.deleteCell(rowIndex, columnIndex);
    }

    /**
     * This methods returns the <code>Range</code> of a <b>single</b> block of columns of the same instance/data object.
     * A <code>single</code> block is made of all the columns immediately to the left and right of the selected one with the same "label".
     * If there is another column with the same "label" but separated by a different column, it is not part of the group.
     * @param columnIndex
     * @return
     */
    public abstract Range getInstanceLimits(int columnIndex);

    /**
     * This methods returns the <code>List&lt;ScenarioGridColumn&gt;</code> of a <b>single</b> block of columns of the same instance/data object.
     * A <code>single</code> block contains the selected column and all the columns immediately to the left and right of it with the same "label".
     * If there is another column with the same "label" but separated by a different column, it is not part of the group.
     * @param columnIndex
     * @return
     */
    public List<ScenarioGridColumn> getInstanceScenarioGridColumns(int columnIndex) {
        Range instanceRange = getInstanceLimits(columnIndex);
        return columns.subList(instanceRange.getMinRowIndex(), instanceRange.getMaxRowIndex() + 1)
                .stream()
                .map(gridColumn -> (ScenarioGridColumn) gridColumn)
                .collect(Collectors.toList());
    }

    /**
     * This methods returns the <code>List&lt;ScenarioGridColumn&gt;</code> of a <b>single</b> block of columns of the same instance/data object.
     * A <code>single</code> block contains the selected column and all the columns immediately to the left and right of it with the same "label".
     * If there is another column with the same "label" but separated by a different column, it is not part of the group.
     * @param selectedColumn
     * @return
     */
    public List<ScenarioGridColumn> getInstanceScenarioGridColumns(ScenarioGridColumn selectedColumn) {
        int columnIndex = columns.indexOf(selectedColumn);
        return getInstanceScenarioGridColumns(columnIndex);
    }

    /**
     * Return the first index to the left of the given group, i.e. <b>excluded</b> the left-most index of <b>that</b> group
     * @param groupName
     * @return
     */
    public int getFirstIndexLeftOfGroup(String groupName) {
        // HORRIBLE TRICK BECAUSE gridColumn.getIndex() DOES NOT REFLECT ACTUAL POSITION, BUT ONLY ORDER OF INSERTION
        final List<GridColumn<?>> columns = this.getColumns();
        final Optional<Integer> first = columns    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup().equals(groupName))  // filtering by group name
                .findFirst()
                .map(gridColumn -> {
                    int indexOfColumn = columns.indexOf(gridColumn);
                    return indexOfColumn > -1 ? indexOfColumn : 0;   // mapping the retrieved column to its index inside the list, or 0
                });
        return first.orElseGet(() -> 0); // returning the retrieved value or, if null, 0
    }

    /**
     * Return the first index to the right of the given group, i.e. <b>excluded</b> the right-most index of <b>that</b> group
     * @param groupName
     * @return
     */
    public int getFirstIndexRightOfGroup(String groupName) {
        // HORRIBLE TRICK BECAUSE gridColumn.getIndex() DOES NOT REFLECT ACTUAL POSITION, BUT ONLY ORDER OF INSERTION
        final Optional<Integer> last = this.getColumns()    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup().equals(groupName))  // filtering by group name
                .reduce((first, second) -> second)  // reducing to have only the last element
                .map(gridColumn -> {
                    int indexOfColumn = this.getColumns().indexOf(gridColumn);
                    return indexOfColumn > -1 ? indexOfColumn + 1 : getColumnCount();   // mapping the retrieved column to its index inside the list +1, or to the total number of columns
                });
        return last.orElseGet(this::getColumnCount); // returning the retrieved value or, if null, the total number of columns
    }

    /**
     * Returns how many columns are already in place for the given group
     * @param groupName
     * @return
     */
    public long getGroupSize(String groupName) {
        return this.getColumns()
                .stream()
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup().equals(groupName))
                .count();
    }

    /**
     * Returns the count of instantiated facts given a classname.
     * @param className
     * @return
     */
    public int getInstancesCount(String className) {
        return abstractScesimModel.getScesimModelDescriptor().getUnmodifiableFactMappings()
                .stream()
                .filter(factMapping -> factMapping.getFactIdentifier().getClassName().equals(className))
                .collect(Collectors.groupingBy(FactMapping::getFactAlias))
                .size();
    }

    public void updateHeader(int columnIndex, int headerRowIndex, String headerCellValue) {
        final ScenarioHeaderMetaData editedMetadata = (ScenarioHeaderMetaData) getColumns().get(columnIndex).getHeaderMetaData().get(headerRowIndex);
        // do not update if old and new value are the same
        if (Objects.equals(editedMetadata.getTitle(), headerCellValue)) {
            return;
        }
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        FactMapping factMappingToEdit = simulationDescriptor.getFactMappingByIndex(columnIndex);
        ScenarioHeaderMetaData.MetadataType metadataType = editedMetadata.getMetadataType();
        IntStream.range(0, getColumnCount()).forEach(index -> updateFactMapping(simulationDescriptor, factMappingToEdit, index, headerCellValue, metadataType));
        if (editedMetadata.getMetadataType().equals(ScenarioHeaderMetaData.MetadataType.INSTANCE)) {
            eventBus.fireEvent(new ReloadTestToolsEvent(false));
        }
    }

    public void clear() {
        // Deleting rows
        int to = getRowCount();
        IntStream.range(0, to)
                .map(i -> to - i - 1)
                .forEach(super::deleteRow);
        List<GridColumn<?>> copyList = new ArrayList<>(getColumns());
        copyList.forEach(super::deleteColumn);
        // clear can be called before bind
        if (abstractScesimModel != null) {
            abstractScesimModel.clear();
        }
    }

    @Override
    public void clearSelections() {
        super.clearSelections();
        selectedColumn = null;
    }

    /**
     * It forces a <code>internalRefreshWidth</code> refresh
     */
    public boolean forceRefreshWidth() {
        return internalRefreshWidth(true, OptionalDouble.empty());
    }

    /**
     * It synchronizes all columns related <code>factMapping</code> columnnWidths
     */
    public void synchronizeFactMappingsWidths() {
        getColumns().forEach(this::synchronizeFactMappingWidth);
    }

    /**
     * It updates a column related <code>factMapping</code> columnWidth
     * @param column
     */
    public void synchronizeFactMappingWidth(final GridColumn<?> column) {
        if (!column.isVisible()) {
            return;
        }
        final int columnIndex = getColumns().indexOf(column);
        final FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
        factMapping.setColumnWidth(column.getWidth());
    }

    /**
     * It retrieves the stored columnWidths and assigns them to every grid column.
     */
    public void loadFactMappingsWidth() {
        for (final GridColumn<?> column : getColumns()) {
            final int columnIndex = getColumns().indexOf(column);
            final FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
            if (factMapping.getColumnWidth() != null) {
                column.setWidth(factMapping.getColumnWidth());
            }
        }
    }

    /**
     * It puts the column present in the given columnIndex as the current selected one.
     * @param columnIndex
     */
    public void selectColumn(int columnIndex) {
        if (columnIndex > getColumnCount() - 1 || columnIndex < 0) {
            throw new IllegalArgumentException("Wrong column index: " + columnIndex);
        }
        selectedColumn = getColumns().get(columnIndex);
    }

    /**
     * Select all the cells of the given row
     * @param rowIndex
     */
    public void selectRow(int rowIndex) {
        if (rowIndex > getRowCount() - 1) {
            return;
        }
        int columns = getColumnCount();
        IntStream.range(0, columns).forEach(columnIndex -> selectCell(rowIndex, columnIndex));
    }

    public GridColumn<?> getSelectedColumn() {
        return selectedColumn;
    }

    public Optional<T> getAbstractScesimModel() {
        return Optional.ofNullable(abstractScesimModel);
    }

    /**
     * Returns <code>true</code> if all the grid cells of the selected column are empty, i.e. the GridCell.getValue() == null OR
     * GridCell.getValue().getValue() == null
     * @return
     */
    public boolean isSelectedColumnEmpty() {
        return selectedColumn == null || isColumnEmpty(getColumns().indexOf(selectedColumn));
    }

    /**
     * Returns <code>true</code> if all the grid cells of the column at given index are empty, i.e. the GridCell.getValue() == null OR
     * GridCell.getValue().getValue() == null
     * @param columnIndex
     * @return
     */
    public boolean isColumnEmpty(int columnIndex) {
        return IntStream.range(0, getRowCount())
                .noneMatch(rowIndex -> getCellValue(getCell(rowIndex, columnIndex)).isPresent());
    }

    /**
     * Returns <code>true</code> if property mapped to the selected column is already assigned to
     * another column of the same <b>instance</b>
     * @param propertyNameElements
     * @return
     */
    public boolean isAlreadyAssignedProperty(List<String> propertyNameElements) {
        boolean toReturn = selectedColumn == null;
        if (!toReturn) {
            try {
                checkAlreadyAssignedProperty(getColumns().indexOf(selectedColumn), propertyNameElements);
            } catch (Exception e) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    /**
     * Check if property mapped to the column at given index is already assigned to
     * another column of the same <b>instance</b>
     * @param columnIndex
     * @param propertyNameElements
     * @throws IllegalStateException if the given <code>propertyNameElements</code> are already mapped to a column of the same <b>instance</b>
     */
    public void checkAlreadyAssignedProperty(int columnIndex, List<String> propertyNameElements) {
        Range instanceLimits = getInstanceLimits(columnIndex);
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        FactIdentifier factIdentifier = simulationDescriptor.getFactMappingByIndex(columnIndex).getFactIdentifier();
        List<String> propertyNameElementsClone = new ArrayList<>(); // We have to keep the original List unmodified
        propertyNameElementsClone.add(factIdentifier.getClassNameWithoutPackage());
        propertyNameElementsClone.addAll(propertyNameElements.subList(1, propertyNameElements.size()));
        if (IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                .filter(index -> index != columnIndex)
                .mapToObj(simulationDescriptor::getFactMappingByIndex)
                .anyMatch(factMapping -> {
                    List<String> factMappingPropertyNameElements = factMapping.getExpressionElements()
                            .stream()
                            .map(ExpressionElement::getStep)
                            .collect(Collectors.toList());
                    return Objects.equals(factMappingPropertyNameElements, propertyNameElementsClone);
                })) {
            throw new IllegalStateException(String.join(".", propertyNameElements) + " has already been used in the current instance.");
        }
    }

    /**
     * Returns <code>true</code> if property mapped to the selected column is the same as the provided one
     * @param propertyNameElements
     * @param factMappingValueType
     * @return
     */
    public boolean isSameSelectedColumnProperty(List<String> propertyNameElements, FactMappingValueType factMappingValueType) {
        return selectedColumn == null || isSameSelectedColumnProperty(getColumns().indexOf(selectedColumn), propertyNameElements, factMappingValueType);
    }

    /**
     * Returns <code>true</code> if property mapped to the column at given index is the same as the provided one
     * @param columnIndex
     * @param propertyNameElements
     * @param factMappingValueType
     * @return
     */
    public boolean isSameSelectedColumnProperty(int columnIndex, List<String> propertyNameElements, FactMappingValueType factMappingValueType) {
        String propertyName = String.join(".", propertyNameElements);
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
        String expressionElement = factMappingByIndex.getExpressionElements().stream().map(ExpressionElement::getStep).collect(Collectors.joining("."));
        return Objects.equals(expressionElement, propertyName) && Objects.equals(factMappingValueType, factMappingByIndex.getFactMappingValueType());
    }

    /**
     * Returns <code>true</code> if no column is selected <b>OR</b> if type of the property mapped to the selected column is the same as the provided one
     * @param className
     * @return
     */
    public boolean isSameSelectedColumnType(String className) {
        return selectedColumn == null || isSameSelectedColumnType(getColumns().indexOf(selectedColumn), className);
    }

    /**
     * Returns <code>true</code> if type of the property mapped to the column at given index is the same as the provided one
     * @param columnIndex
     * @param className
     * @return
     */
    public boolean isSameSelectedColumnType(int columnIndex, String className) {
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
        return factMappingByIndex.getClassName().equals(className);
    }

    /**
     * Returns <code>true</code> if no column is selected <b>OR</b> if type of the <b>INSTANCE</b> mapped to the selected column is the same as the provided one
     * @param className
     * @return
     */
    public boolean isSameInstanceType(String className) {
        return selectedColumn == null || isSameInstanceType(getColumns().indexOf(selectedColumn), className);
    }

    /**
     * Check if given <b>headerName</b> is the same as the <b>Fact</b> mapped to the
     * column at given index
     * @param columnIndex
     * @param headerName
     * @throws IllegalStateException if the given <b>headerName</b> is not the name of the class mapped to the given column
     */
    public void checkSameInstanceHeader(int columnIndex, String headerName) {
        if (!isSameInstanceType(columnIndex, headerName)) {
            throw new IllegalStateException(headerName + " is not the class of the current column.");
        }
    }

    /**
     * Returns <code>true</code> if type of the <b>INSTANCE</b> mapped to the column at given index is the same as the provided one
     * @param columnIndex
     * @param headerName
     * @return
     */
    public boolean isSameInstanceType(int columnIndex, String headerName) {
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        final FactIdentifier factIdentifierByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex).getFactIdentifier();
        return Objects.equals(factIdentifierByIndex.getClassNameWithoutPackage(), headerName);
    }

    /**
     * Check if given <b>headerName</b> is the same as the <b>element steps</b> mapped to the given column
     * @param columnIndex
     * @param propertyNameElements
     * @throws IllegalStateException if the given <b>propertyNameElements</b> (corrected for the class name) represents the <b>element steps</b> of the given column
     */
    public void checkSamePropertyHeader(int columnIndex, List<String> propertyNameElements)  {
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        final FactMapping factMapping = simulationDescriptor.getFactMappingByIndex(columnIndex);
        List<String> columnPropertyName = factMapping.getExpressionElementsWithoutClass().stream().map(ExpressionElement::getStep).collect(Collectors.toList());
        if (!Objects.equals(columnPropertyName, propertyNameElements)) {
            throw new IllegalStateException(String.join(".", propertyNameElements) + " is not the same property of the current column.");
        }
    }

    /**
     * It resets the <code>FactMappingValue</code> status for all CELLS
     */
    public void resetErrors() {
        IntStream.range(0, getRowCount()).forEach(this::resetErrors);
    }

    /**
     * It resets the <code>FactMappingValue</code> status for a specific ROW
     * @param rowIndex
     */
    public void resetErrors(int rowIndex) {
        E scesimDataByIndex = abstractScesimModel.getDataByIndex(rowIndex);
        scesimDataByIndex.resetErrors();
        refreshErrors();
    }

    /**
     * It resets the <code>FactMappingValue</code> status for a specific CELL
     * @param rowIndex
     * @param columnIndex
     */
    public void resetError(int rowIndex, int columnIndex) {
        E scesimDataByIndex = abstractScesimModel.getDataByIndex(rowIndex);
        FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
        Optional<FactMappingValue> factMappingValue = scesimDataByIndex.getFactMappingValue(factMapping);
        factMappingValue.ifPresent(FactMappingValue::resetStatus);
        refreshErrors();
    }

    public void refreshErrors() {
        IntStream.range(0, getRowCount()).forEach(this::refreshErrorsRow);
    }

    /**
     * Set the names of already existing Simple Java Types/Instances, used inside updateHeaderValidation
     * @param simpleJavaTypeInstancesName
     */
    public void setSimpleJavaTypeInstancesName(Set<String> simpleJavaTypeInstancesName) {
        this.simpleJavaTypeInstancesName = simpleJavaTypeInstancesName;
    }

    /**
     * Check validity of given <b>instanceHeaderCellValue</b>
     * @param instanceHeaderCellValue
     * @param columnIndex
     * @param isADataType
     * @throws Exception with message specific to failed check
     */
    public void validateInstanceHeaderUpdate(String instanceHeaderCellValue, int columnIndex, boolean isADataType) {
        if (isADataType) {
            checkSameInstanceHeader(columnIndex, instanceHeaderCellValue);
            checkValidAndUniqueInstanceHeaderTitle(instanceHeaderCellValue, columnIndex);
        } else {
            checkValidAndUniqueInstanceHeaderTitle(instanceHeaderCellValue, columnIndex);
        }
    }

    /**
     * Check validity of given <b>propertyHeaderCellValue</b>
     * @param propertyHeaderCellValue
     * @param columnIndex
     * @param isPropertyType
     * @throws Exception with message specific to failed check
     */
    public void validatePropertyHeaderUpdate(String propertyHeaderCellValue, int columnIndex, boolean isPropertyType) {
        List<String> propertyNameElements = Collections.unmodifiableList(Arrays.asList(propertyHeaderCellValue.split("\\.")));
        if (isPropertyType) {
            checkSamePropertyHeader(columnIndex, propertyNameElements);
            checkUniquePropertyHeaderTitle(propertyHeaderCellValue, columnIndex);
        } else {
            checkAlreadyAssignedProperty(columnIndex, propertyNameElements);
            checkValidAndUniquePropertyHeaderTitle(propertyHeaderCellValue, columnIndex);
        }
    }

    public boolean isSimpleType(String factClassName) {
        return simpleJavaTypeInstancesName != null && simpleJavaTypeInstancesName.contains(factClassName);
    }

    /**
     * This methods returns the <code>Range</code> of a <b>single</b> block of columns of the same instance/data object.
     * A <code>single</code> block is made of all the columns immediately to the left and right of the selected one with the same "label".
     * If there is another column with the same "label" but separated by a different column, it is not part of the group.
     * @param columnIndex
     * @param columnIndexStart the leftmost index to consider when evaluating the range
     * @return
     */
    protected Range getInstanceLimits(int columnIndex, int columnIndexStart) {
        final ScenarioGridColumn column = (ScenarioGridColumn) columns.get(columnIndex);
        final String originalColumnGroup = column.getInformationHeaderMetaData().getColumnGroup();
        final ScenarioHeaderMetaData selectedInformationHeaderMetaData = column.getInformationHeaderMetaData();
        String originalColumnTitle = selectedInformationHeaderMetaData.getTitle();
        int leftPosition = columnIndex;
        while (leftPosition > columnIndexStart && ((ScenarioGridColumn) columns.get(leftPosition - 1)).getInformationHeaderMetaData().getColumnGroup().equals(originalColumnGroup) && ((ScenarioGridColumn) columns.get(leftPosition - 1)).getInformationHeaderMetaData().getTitle().equals(originalColumnTitle)) {
            leftPosition--;
        }
        int rightPosition = columnIndex;
        while (rightPosition < columns.size() - 1 && ((ScenarioGridColumn) columns.get(rightPosition + 1)).getInformationHeaderMetaData().getColumnGroup().equals(originalColumnGroup) && ((ScenarioGridColumn) columns.get(rightPosition + 1)).getInformationHeaderMetaData().getTitle().equals(originalColumnTitle)) {
            rightPosition++;
        }
        return new Range(leftPosition, rightPosition);
    }

    /**
     * If the <code>FactIdentifier</code> of the given <code>FactMapping</code> equals the one at <b>index</b>, update the <code>FactMapping.FactAlias</code> at <b>index</b>
     * position with the provided <b>value</b>
     * @param simulationDescriptor
     * @param factMappingReference
     * @param index
     * @param value
     */
    protected void updateFactMapping(ScesimModelDescriptor simulationDescriptor, FactMapping factMappingReference, int index, String value, ScenarioHeaderMetaData.MetadataType metadataType) {
        final FactIdentifier factIdentifierReference = factMappingReference.getFactIdentifier();
        FactMapping factMappingToCheck = simulationDescriptor.getFactMappingByIndex(index);
        final FactIdentifier factIdentifierToCheck = factMappingToCheck.getFactIdentifier();
        boolean toUpdate = (Objects.equals(FactIdentifier.EMPTY, factIdentifierReference) &&
                (Objects.equals(factIdentifierToCheck, factIdentifierReference) && Objects.equals(factMappingReference.getFactAlias(), factMappingToCheck.getFactAlias())))
                || (Objects.equals(factIdentifierToCheck, factIdentifierReference));
        if (toUpdate) {
            switch (metadataType) {
                case INSTANCE:
                    ((ScenarioGridColumn) columns.get(index)).getInformationHeaderMetaData().setTitle(value);
                    factMappingToCheck.setFactAlias(value);
                    break;
                case PROPERTY:
                    if (Objects.equals(factMappingToCheck.getFullExpression(), factMappingReference.getFullExpression())) {
                        ((ScenarioGridColumn) columns.get(index)).getPropertyHeaderMetaData().setTitle(value);
                        factMappingToCheck.setExpressionAlias(value);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This method <i>add</i> or <i>insert</i> a new column to the grid <b>and</b> to the underlying model, depending on the index value.
     * If index == -1 -> add, otherwise insert. It automatically creates default <code>FactIdentifier</code>  (for String class) and <code>ExpressionIdentifier</code>
     * @param index
     * @param column
     */
    protected void commonAddColumn(final int index, final GridColumn<?> column) {
        String group = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnGroup();
        String columnId = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getColumnId();
        final ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(index, column, ei);
    }

    /**
     * This method <i>add</i> or <i>insert</i> a new column to the grid <b>and</b> to the underlying model, depending on the index value.
     * If index == -1 -> add, otherwise insert.
     * @param index
     * @param column
     * @param ei
     */
    protected void commonAddColumn(final int index, final GridColumn<?> column, ExpressionIdentifier ei) {
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        String instanceTitle = ((ScenarioGridColumn) column).getInformationHeaderMetaData().getTitle();
        String propertyTitle = ((ScenarioGridColumn) column).getPropertyHeaderMetaData().getTitle();
        final int columnIndex = index == -1 ? getColumnCount() : index;
        try {
            final FactMapping createdFactMapping = simulationDescriptor.addFactMapping(columnIndex, instanceTitle, ((ScenarioGridColumn) column).getFactIdentifier(), ei);
            createdFactMapping.setExpressionAlias(propertyTitle);
            if (index == -1) {  // This is actually an append
                super.appendColumn(column);
            } else {
                super.insertColumn(index, column);
            }
            final Range instanceLimits = getInstanceLimits(columnIndex);
            IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                    .filter(currentIndex -> currentIndex != columnIndex)
                    .forEach(currentIndex -> simulationDescriptor.getFactMappingByIndex(currentIndex).setFactAlias(createdFactMapping.getFactAlias()));
        } catch (Exception e) {
            eventBus.fireEvent(new ScenarioNotificationEvent("Error during column creation: " + e.getMessage(), NotificationEvent.NotificationType.ERROR));
            eventBus.fireEvent(new ScenarioGridReloadEvent(getGridWidget()));
            return;
        }

        final List<E> unmodifiableScesimData = abstractScesimModel.getUnmodifiableData();
        String placeHolder = ((ScenarioGridColumn) column).getPlaceHolder();
        IntStream.range(0, unmodifiableScesimData.size())
                .forEach(rowIndex -> setCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(null, placeHolder))));
    }

    protected abstract void commonAddRow(int rowIndex);

    protected void commonAddRow(int rowIndex, int columnIndexStart)  {
        E scesimData = abstractScesimModel.addData(rowIndex);
        final ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        IntStream.range(columnIndexStart, getColumnCount()).forEach(columnIndex -> {
            final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
            scesimData.addMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier(), null);
            ScenarioGridColumn column = ((ScenarioGridColumn) columns.get(columnIndex));
            String placeHolder = ScenarioSimulationUtils.getPlaceHolder(column.isInstanceAssigned(),
                                                                        column.isPropertyAssigned(),
                                                                        factMappingByIndex.getFactMappingValueType(),
                                                                        factMappingByIndex.getClassName());
            setCell(rowIndex, columnIndex, () -> {
                ScenarioGridCell newCell = new ScenarioGridCell(new ScenarioGridCellValue(null, placeHolder));
                if (ScenarioSimulationSharedUtils.isCollectionOrMap((factMappingByIndex.getClassName()))) {
                    newCell.setListMap(ScenarioSimulationSharedUtils.isList((factMappingByIndex.getClassName())));
                }
                return newCell;
            });
        });
    }

    protected void updateIndexColumn() {
        final Optional<GridColumn<?>> indexColumn = this.getColumns()    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getTitle().equals(FactIdentifier.INDEX.getName()))  // filtering by group name
                .findFirst();
        indexColumn.ifPresent(column -> {
            int indexOfColumn = getColumns().indexOf(column);
            IntStream.range(0, getRowCount())
                    .forEach(rowIndex -> {
                        String value = String.valueOf(rowIndex + 1);
                        setCellValue(rowIndex, indexOfColumn, new ScenarioGridCellValue(value));
                    });
        });
    }

    protected void checkSimulation() {
        Objects.requireNonNull(abstractScesimModel, "Bind a simulation to the ScenarioGridModel to use it");
    }

    /**
     * Verify the given value is not already used as instance header name <b>between different groups</b>
     * @param instanceHeaderCellValue
     * @param columnIndex
     * @throws IllegalArgumentException if the given <b>instanceHeaderCellValue</b> contains a <i>dot</i> <b>OR</b>
     * it has already been used inside the <b>group (GIVEN/EXPECT)</b> of the given column
     */
    protected void checkValidAndUniqueInstanceHeaderTitle(String instanceHeaderCellValue, int columnIndex) {
        if (instanceHeaderCellValue.contains(".")) {
            throw new IllegalArgumentException(ScenarioSimulationEditorConstants.INSTANCE.instanceTitleWithPeriodsError());
        }
        Range instanceLimits = getInstanceLimits(columnIndex);
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        FactIdentifier factIdentifier = simulationDescriptor.getFactMappingByIndex(columnIndex).getFactIdentifier();
        if (IntStream.range(0, getColumnCount())
                .filter(index -> index < instanceLimits.getMinRowIndex() || index > instanceLimits.getMaxRowIndex())
                .filter(index -> !Objects.equals(factIdentifier, simulationDescriptor.getFactMappingByIndex(index).getFactIdentifier()))
                .mapToObj(index -> (ScenarioGridColumn) getColumns().get(index))
                .filter(elem -> elem.getInformationHeaderMetaData() != null)
                .map(ScenarioGridColumn::getInformationHeaderMetaData)
                .anyMatch(elem -> Objects.equals(elem.getTitle(), instanceHeaderCellValue))) {
            throw new IllegalArgumentException(ScenarioSimulationEditorConstants.INSTANCE.
                    instanceTitleAssignedError(instanceHeaderCellValue));
        }
    }

    /**
     * Verify if the given value is not already used as property header name <b>inside the same instance</b>
     * @param propertyHeaderCellValue
     * @param columnIndex
     * @throws IllegalArgumentException if the given <b>propertyHeaderCellValue</b> contains a <i>dot</i> <b>OR</b>
     * it has already been used inside the <b>instance</b> of the given column
     */
    protected void checkValidAndUniquePropertyHeaderTitle(String propertyHeaderCellValue, int columnIndex) {
        if (propertyHeaderCellValue.contains(".")) {
            throw new IllegalArgumentException(ScenarioSimulationEditorConstants.INSTANCE.propertyTitleWithPeriodsError());
        }
        checkUniquePropertyHeaderTitle(propertyHeaderCellValue, columnIndex);
    }

    /**
     * Verify if the given value is not already used as property header name <b>inside the same instance</b>
     * @param propertyHeaderCellValue
     * @param columnIndex
     * @throws IllegalArgumentException if the given <b>propertyHeaderCellValue</b> has already been used
     * inside the <b>instance</b> of the given column
     */
    protected void checkUniquePropertyHeaderTitle(String propertyHeaderCellValue, int columnIndex) {
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        FactIdentifier factIdentifier = simulationDescriptor.getFactMappingByIndex(columnIndex).getFactIdentifier();
        if (IntStream.range(0, getColumnCount())
                .filter(index -> index != columnIndex)
                .filter(index -> Objects.equals(factIdentifier, simulationDescriptor.getFactMappingByIndex(index).getFactIdentifier()))
                .mapToObj(index -> (ScenarioGridColumn) getColumns().get(index))
                .filter(elem -> elem.getPropertyHeaderMetaData() != null)
                .map(ScenarioGridColumn::getPropertyHeaderMetaData)
                .anyMatch(elem -> Objects.equals(elem.getTitle(), propertyHeaderCellValue))) {
            throw new IllegalArgumentException(ScenarioSimulationEditorConstants.INSTANCE.
                    propertyTitleAssignedError(propertyHeaderCellValue));
        }
    }

    protected boolean isNewInstanceName(String value) {
        return getColumns().stream()
                .map(elem -> ((ScenarioGridColumn) elem).getInformationHeaderMetaData())
                .filter(Objects::nonNull)
                .noneMatch(elem -> Objects.equals(elem.getTitle(), value));
    }

    protected boolean isNewPropertyName(String value) {
        return getColumns().stream()
                .map(elem -> ((ScenarioGridColumn) elem).getPropertyHeaderMetaData())
                .filter(Objects::nonNull)
                .noneMatch(elem -> Objects.equals(elem.getTitle(), value));
    }

    protected void refreshErrorsRow(int rowIndex) {
        ScesimModelDescriptor simulationDescriptor = abstractScesimModel.getScesimModelDescriptor();
        E scesimDataByIndex = abstractScesimModel.getDataByIndex(rowIndex);
        IntStream.range(0, getColumnCount()).forEach(columnIndex -> {
            ScenarioGridCell cell = (ScenarioGridCell) getCell(rowIndex, columnIndex);
            if (cell == null) {
                return;
            }
            final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
            Optional<FactMappingValue> factMappingValue = scesimDataByIndex.getFactMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier());
            if (factMappingValue.isPresent()) {
                cell.setErrorMode(FactMappingValueStatus.SUCCESS != factMappingValue.get().getStatus());
            } else {
                cell.setErrorMode(false);
            }
        });
    }

    /**
     * Returns the correct <b>DOMElement</b> factory to the given data
     * @param modelType
     * @param valueType
     */
    public BaseSingletonDOMElementFactory getDOMElementFactory(String className,
                                                               ScenarioSimulationModel.Type modelType,
                                                               FactMappingValueType valueType) {
        boolean isRuleScenario = Objects.equals(ScenarioSimulationModel.Type.RULE, modelType);
        if (ScenarioSimulationSharedUtils.isCollectionOrMap(className) && Objects.equals(FactMappingValueType.NOT_EXPRESSION, valueType)) {
            return collectionEditorSingletonDOMElementFactory;
        }
        if (Objects.equals(FactMappingValueType.EXPRESSION, valueType) && isRuleScenario) {
            return scenarioExpressionCellTextAreaSingletonDOMElementFactory;
        }
        return scenarioCellTextAreaSingletonDOMElementFactory;
    }

    // Helper method to avoid potential NPE
    private Optional<?> getCellValue(GridCell<?> gridCell) {
        if (gridCell == null || gridCell.getValue() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(gridCell.getValue().getValue());
    }
}