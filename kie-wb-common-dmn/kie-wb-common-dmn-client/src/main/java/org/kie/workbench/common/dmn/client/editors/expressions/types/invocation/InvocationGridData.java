/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

public class InvocationGridData implements GridData {

    protected final DMNGridData delegate;
    protected final SessionManager sessionManager;
    protected final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected final Optional<Invocation> expression;
    protected final Command canvasOperation;

    public InvocationGridData(final DMNGridData delegate,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final Optional<Invocation> expression,
                              final Command canvasOperation) {
        this.delegate = delegate;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expression = expression;
        this.canvasOperation = canvasOperation;
    }

    // --- Intercepted methods delegated to commands ---

    @Override
    public void moveRowTo(final int index,
                          final GridRow row) {
        moveRowsTo(index,
                   Collections.singletonList(row));
    }

    @Override
    public void moveRowsTo(final int index,
                           final List<GridRow> rows) {
        expression.ifPresent(invocation -> {
            final AbstractCanvasHandler handler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
            sessionCommandManager.execute(handler,
                                          new MoveRowsCommand(invocation,
                                                              delegate,
                                                              index,
                                                              rows,
                                                              canvasOperation));
        });
    }

    // --- Delegated to real class ---

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
    public Range setCell(final int rowIndex,
                         final int columnIndex,
                         final GridCellValue<?> value) {
        return delegate.setCell(rowIndex,
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
}
