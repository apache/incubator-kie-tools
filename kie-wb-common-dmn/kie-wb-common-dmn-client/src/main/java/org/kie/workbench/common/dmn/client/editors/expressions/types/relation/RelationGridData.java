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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.soup.commons.util.Lists;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.MoveColumnsCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DelegatingGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

public class RelationGridData extends DelegatingGridData {

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Optional<Relation> expression;
    private final Command canvasOperation;

    public RelationGridData(final DMNGridData delegate,
                            final SessionManager sessionManager,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final Optional<Relation> expression,
                            final Command canvasOperation) {
        super(delegate);
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
        expression.ifPresent(relation -> sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                       new MoveRowsCommand(relation,
                                                                                           delegate,
                                                                                           index,
                                                                                           rows,
                                                                                           canvasOperation)));
    }

    @Override
    public void moveColumnTo(final int index,
                             final GridColumn<?> column) {
        moveColumnsTo(index,
                      new Lists.Builder<GridColumn<?>>()
                              .add(column)
                              .build()
        );
    }

    @Override
    public void moveColumnsTo(final int index,
                              final List<GridColumn<?>> columns) {
        expression.ifPresent(relation -> sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                       new MoveColumnsCommand(relation,
                                                                                              delegate,
                                                                                              index,
                                                                                              columns,
                                                                                              canvasOperation)));
    }

    @Override
    public void appendColumn(final GridColumn<?> column) {
        delegate.appendColumn(column);
        assertResizableColumns();
    }

    @Override
    public void insertColumn(final int index,
                             final GridColumn<?> column) {
        delegate.insertColumn(index,
                              column);
        assertResizableColumns();
    }

    @Override
    public void deleteColumn(final GridColumn<?> column) {
        delegate.deleteColumn(column);
        assertResizableColumns();
    }

    private void assertResizableColumns() {
        final int lastColumnIndex = getColumnCount() - 1;
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            final GridColumn<?> uiColumn = getColumns().get(columnIndex);
            uiColumn.setResizable(columnIndex != lastColumnIndex);
        }
    }
}
