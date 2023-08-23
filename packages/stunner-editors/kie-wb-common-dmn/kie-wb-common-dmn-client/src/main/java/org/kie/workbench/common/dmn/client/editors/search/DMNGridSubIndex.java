/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell.HasNameAndDataTypeCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

@ApplicationScoped
public class DMNGridSubIndex implements DMNSubIndex {

    private final DMNGridHelper dmnGridHelper;

    @Inject
    public DMNGridSubIndex(final DMNGridHelper dmnGridHelper) {
        this.dmnGridHelper = dmnGridHelper;
    }

    @Override
    public List<DMNSearchableElement> getSearchableElements() {
        return dmnGridHelper
                .getGridWidgets()
                .stream()
                .flatMap(gridWidget -> getSearchableElements(gridWidget).stream())
                .collect(Collectors.toList());
    }

    @Override
    public void onSearchClosed() {
        dmnGridHelper.focusGridPanel();
        dmnGridHelper.clearCellHighlights();
    }

    private List<DMNSearchableElement> getSearchableElements(final GridWidget gridWidget) {

        final List<DMNSearchableElement> elements = new ArrayList<>();
        final GridData model = gridWidget.getModel();
        final int rowCount = model.getRowCount();
        final int columnCount = model.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                final Optional<? extends GridCell<?>> cell = getCell(model, row, column);
                if (cell.isPresent()) {
                    elements.add(makeElement(gridWidget, cell.get(), row, column));
                }
            }
        }

        return elements;
    }

    private Optional<? extends GridCell<?>> getCell(final GridData model,
                                                    final int row,
                                                    final int column) {
        return Optional.ofNullable(model.getCell(row, column));
    }

    private DMNSearchableElement makeElement(final GridWidget gridWidget,
                                             final GridCell<?> cell,
                                             final int row,
                                             final int column) {

        final DMNSearchableElement searchableCell = new DMNSearchableElement();
        final String value = getValue(cell);

        searchableCell.setRow(row);
        searchableCell.setColumn(column);
        searchableCell.setText(value);
        searchableCell.setOnFound(() -> {
            dmnGridHelper.clearCellHighlights();
            dmnGridHelper.highlightCell(row, column, gridWidget);
        });

        return searchableCell;
    }

    String getValue(final GridCell<?> cell) {

        final GridCellValue<?> cellValue = cell.getValue();

        if (cellValue != null) {

            final Object value = cellValue.getValue();

            if (value instanceof HasNameAndDataTypeCell) {
                final HasNameAndDataTypeCell hasName = (HasNameAndDataTypeCell) value;
                return hasName.hasData() ? hasName.getName().getValue() : "";
            } else if (value instanceof String || value instanceof Integer) {
                return String.valueOf(value);
            }
        }

        return "";
    }

    @Override
    public void onNoResultsFound() {
        dmnGridHelper.clearSelections();
        dmnGridHelper.clearCellHighlights();
    }
}
