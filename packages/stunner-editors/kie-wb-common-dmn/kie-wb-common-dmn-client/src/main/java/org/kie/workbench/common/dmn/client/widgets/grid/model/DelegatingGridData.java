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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.List;
import java.util.function.Supplier;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

/**
 * A convenience class to support intercepting GridData mutations and defer to Commands.
 */
public class DelegatingGridData implements GridData {

    protected final DMNGridData delegate;

    public DelegatingGridData(final DMNGridData delegate) {
        this.delegate = delegate;
    }

    @Override
    public void moveRowTo(final int index,
                          final GridRow row) {
        delegate.moveRowTo(index,
                           row);
    }

    @Override
    public void moveRowsTo(final int index,
                           final List<GridRow> rows) {
        delegate.moveRowsTo(index,
                            rows);
    }

    @Override
    public void moveColumnTo(final int index,
                             final GridColumn<?> column) {
        delegate.moveColumnTo(index,
                              column);
    }

    @Override
    public void moveColumnsTo(final int index,
                              final List<GridColumn<?>> columns) {
        delegate.moveColumnsTo(index,
                               columns);
    }

    @Override
    public Range selectCell(final int rowIndex,
                            final int columnIndex) {
        return delegate.selectCell(rowIndex,
                                   columnIndex);
    }

    @Override
    public Range selectCells(final int rowIndex,
                             final int columnIndex,
                             final int width,
                             final int height) {
        return delegate.selectCells(rowIndex,
                                    columnIndex,
                                    width,
                                    height);
    }

    @Override
    public Range selectHeaderCell(final int headerRowIndex,
                                  final int headerColumnIndex) {
        return delegate.selectHeaderCell(headerRowIndex,
                                         headerColumnIndex);
    }

    @Override
    public List<SelectedCell> getSelectedHeaderCells() {
        return delegate.getSelectedHeaderCells();
    }

    @Override
    public Range setCell(final int rowIndex,
                         final int columnIndex,
                         final Supplier<GridCell<?>> cellSupplier) {
        return delegate.setCell(rowIndex,
                                columnIndex,
                                cellSupplier);
    }

    @Override
    public Range setCellValue(final int rowIndex,
                              final int columnIndex,
                              final GridCellValue<?> value) {
        return delegate.setCellValue(rowIndex,
                                     columnIndex,
                                     value);
    }

    @Override
    public Range deleteCell(final int rowIndex,
                            final int columnIndex) {
        return delegate.deleteCell(rowIndex,
                                   columnIndex);
    }

    @Override
    public List<GridColumn<?>> getColumns() {
        return delegate.getColumns();
    }

    @Override
    public int getColumnCount() {
        return delegate.getColumnCount();
    }

    @Override
    public void appendColumn(final GridColumn<?> column) {
        delegate.appendColumn(column);
    }

    @Override
    public void insertColumn(final int index,
                             final GridColumn<?> column) {
        delegate.insertColumn(index,
                              column);
    }

    @Override
    public void deleteColumn(final GridColumn<?> column) {
        delegate.deleteColumn(column);
    }

    @Override
    public List<GridRow> getRows() {
        return delegate.getRows();
    }

    @Override
    public void expandCell(final int rowIndex,
                           final int columnIndex) {
        delegate.expandCell(rowIndex,
                            columnIndex);
    }

    @Override
    public void collapseCell(final int rowIndex,
                             final int columnIndex) {
        delegate.collapseCell(rowIndex,
                              columnIndex);
    }

    @Override
    public void setColumnDraggingEnabled(final boolean enabled) {
        delegate.setColumnDraggingEnabled(enabled);
    }

    @Override
    public boolean isColumnDraggingEnabled() {
        return delegate.isColumnDraggingEnabled();
    }

    @Override
    public void setRowDraggingEnabled(final boolean enabled) {
        delegate.setRowDraggingEnabled(enabled);
    }

    @Override
    public boolean isRowDraggingEnabled() {
        return delegate.isRowDraggingEnabled();
    }

    @Override
    public void setMerged(final boolean isMerged) {
        delegate.setMerged(isMerged);
    }

    @Override
    public boolean isMerged() {
        return delegate.isMerged();
    }

    @Override
    public void updateColumn(final int index,
                             final GridColumn<?> column) {
        delegate.updateColumn(index,
                              column);
    }

    @Override
    public void clearSelections() {
        delegate.clearSelections();
    }

    @Override
    public List<SelectedCell> getSelectedCells() {
        return delegate.getSelectedCells();
    }

    @Override
    public SelectedCell getSelectedCellsOrigin() {
        return delegate.getSelectedCellsOrigin();
    }

    @Override
    public GridCell<?> getCell(final int rowIndex,
                               final int columnIndex) {
        return delegate.getCell(rowIndex,
                                columnIndex);
    }

    @Override
    public void setHeaderRowCount(final int headerRowCount) {
        delegate.setHeaderRowCount(headerRowCount);
    }

    @Override
    public int getHeaderRowCount() {
        return delegate.getHeaderRowCount();
    }

    @Override
    public int getRowCount() {
        return delegate.getRowCount();
    }

    @Override
    public Range deleteRow(final int rowIndex) {
        return delegate.deleteRow(rowIndex);
    }

    @Override
    public void insertRow(final int rowIndex,
                          final GridRow row) {
        delegate.insertRow(rowIndex, row);
    }

    @Override
    public void appendRow(final GridRow row) {
        delegate.appendRow(row);
    }

    @Override
    public GridRow getRow(final int rowIndex) {
        return delegate.getRow(rowIndex);
    }

    @Override
    public boolean refreshWidth() {
        return delegate.refreshWidth();
    }

    @Override
    public boolean refreshWidth(final double currentWidth) {
        return delegate.refreshWidth(currentWidth);
    }

    @Override
    public boolean setVisibleSizeAndRefresh(final int width, final int height) {
        return delegate.setVisibleSizeAndRefresh(width, height);
    }

    @Override
    public int getVisibleWidth() {
        return delegate.getVisibleWidth();
    }

    @Override
    public int getVisibleHeight() {
        return delegate.getVisibleHeight();
    }
}
