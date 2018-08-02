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
package org.drools.workbench.screens.scenariosimulation.client.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;

public class ScenarioGridModel extends BaseGridData {

    private Optional<Map<Integer, String>> optionalHeadersMap = Optional.empty();
    private Optional<Map<Integer, Map<Integer, String>>> optionalRowsMap = Optional.empty();

    public ScenarioGridModel() {
        super();
    }

    public ScenarioGridModel(boolean isMerged) {
        super(isMerged);
    }

    /**
     * Method to bind the data serialized inside backend <code>ScenarioSimulationModel</code>
     * @param headersMap
     * @param rowsMap
     */
    public void bindContent(Map<Integer, String> headersMap, Map<Integer, Map<Integer, String>> rowsMap) {
        this.optionalHeadersMap = Optional.ofNullable(headersMap);
        this.optionalRowsMap = Optional.ofNullable(rowsMap);
    }

    @Override
    public void appendColumn(GridColumn<?> column) {
        super.appendColumn(column);
        optionalHeadersMap.ifPresent(headersMap -> {
            int columnIndex = getColumnCount() - 1;
            headersMap.put(columnIndex, column.getHeaderMetaData().get(0).getTitle());
        });
    }

    @Override
    public Range setCell(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        Range toReturn = super.setCell(rowIndex, columnIndex, cellSupplier);
        optionalRowsMap.ifPresent(rowsMap -> {
            Optional<?> optionalValue = getCellValue(getCell(rowIndex, columnIndex));
            optionalValue.ifPresent((Consumer<Object>) rawValue -> {
                if (rawValue instanceof String) { // Just to avoid unchecked cast - BaseGridData/GridRow should be generified
                    final String cellValue = (String) rawValue;
                    rowsMap.computeIfPresent(rowIndex, (integer, integerStringMap) -> {
                        integerStringMap.put(columnIndex, cellValue);
                        return integerStringMap;
                    });
                    rowsMap.computeIfAbsent(rowIndex, integer -> {
                        Map<Integer, String> toReturn1 = new HashMap<>(1);
                        toReturn1.put(columnIndex, cellValue);
                        return toReturn1;
                    });
                }
            });
        });
        return toReturn;
    }

    public void clear() {
        // Deleting rows
        int to = getRowCount();
        IntStream.range(0, to)
                .map(i -> to - i - 1)
                .forEach(this::deleteRow);
        optionalRowsMap.ifPresent(Map::clear);
        // Deleting columns
        List<GridColumn<?>> copyList = new ArrayList<>(getColumns());
        copyList.forEach(this::deleteColumn);
        optionalHeadersMap.ifPresent(Map::clear);
    }

    protected Optional<Map<Integer, String>> getOptionalHeadersMap() {
        return optionalHeadersMap;
    }

    protected Optional<Map<Integer, Map<Integer, String>>> getOptionalRowsMap() {
        return optionalRowsMap;
    }

    // Helper method to avoid potential NPE
    private Optional<?> getCellValue(GridCell<?> gridCell) {
        return Optional.ofNullable(gridCell).map(GridCell::getValue).map(GridCellValue::getValue);
    }
}