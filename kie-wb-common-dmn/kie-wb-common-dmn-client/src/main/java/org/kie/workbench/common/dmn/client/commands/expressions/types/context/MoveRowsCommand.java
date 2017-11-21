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

package org.kie.workbench.common.dmn.client.commands.expressions.types.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class MoveRowsCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                           VetoUndoCommand {

    private final Context context;
    private final DMNGridData uiModel;
    private final int index;
    private final List<GridRow> rows;
    private final org.uberfire.mvp.Command canvasOperation;

    private final int oldIndex;

    public MoveRowsCommand(final Context context,
                           final DMNGridData uiModel,
                           final int index,
                           final List<GridRow> rows,
                           final org.uberfire.mvp.Command canvasOperation) {
        this.context = context;
        this.uiModel = uiModel;
        this.index = index;
        this.rows = new ArrayList<>(rows);
        this.canvasOperation = canvasOperation;

        this.oldIndex = uiModel.getRows().indexOf(rows.get(0));
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler ach) {
        return new AbstractGraphCommand() {
            @Override
            protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext gcec) {
                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext gcec) {
                moveRows(index);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gcec) {
                moveRows(oldIndex);

                return GraphCommandResultBuilder.SUCCESS;
            }

            private void moveRows(final int index) {
                final int oldIndex = uiModel.getRows().indexOf(rows.get(0));
                final List<ContextEntry> rowsToMove = rows
                        .stream()
                        .map(r -> uiModel.getRows().indexOf(r))
                        .map(i -> context.getContextEntry().get(i))
                        .collect(Collectors.toList());

                final List<ContextEntry> rows = context.getContextEntry();

                rows.removeAll(rowsToMove);

                if (index < oldIndex) {
                    rows.addAll(index,
                                rowsToMove);
                } else if (index > oldIndex) {
                    rows.addAll(index - rowsToMove.size() + 1,
                                rowsToMove);
                }
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler ach) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler ach) {
                uiModel.moveRowsTo(index,
                                   rows);
                updateRowNumbers();
                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler ach) {
                uiModel.moveRowsTo(oldIndex,
                                   rows);
                updateRowNumbers();
                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            private void updateRowNumbers() {
                final Optional<RowNumberColumn> rowNumberColumn = uiModel
                        .getColumns()
                        .stream()
                        .filter(c -> c instanceof RowNumberColumn)
                        .map(c -> (RowNumberColumn) c)
                        .findFirst();

                rowNumberColumn.ifPresent(c -> {
                    final int columnIndex = uiModel.getColumns().indexOf(c);
                    for (int rowIndex = 0; rowIndex < uiModel.getRowCount(); rowIndex++) {
                        uiModel.setCell(rowIndex,
                                        columnIndex,
                                        new BaseGridCellValue<>(rowIndex + 1));
                    }
                });
            }

            private void updateParentInformation() {
                final Optional<ExpressionEditorColumn> expressionColumn = uiModel
                        .getColumns()
                        .stream()
                        .filter(c -> c instanceof ExpressionEditorColumn)
                        .map(c -> (ExpressionEditorColumn) c)
                        .findFirst();

                expressionColumn.ifPresent(c -> {
                    final int columnIndex = uiModel.getColumns().indexOf(c);
                    for (int rowIndex = 0; rowIndex < uiModel.getRowCount(); rowIndex++) {
                        final GridCell<?> cell = uiModel.getCell(rowIndex, columnIndex);
                        final GridCellValue<?> value = cell.getValue();
                        if (value instanceof DMNExpressionCellValue) {
                            final DMNExpressionCellValue ecv = (DMNExpressionCellValue) value;
                            if (ecv.getValue().isPresent()) {
                                final GridWidget gw = ecv.getValue().get();
                                if (gw instanceof BaseExpressionGrid) {
                                    final BaseExpressionGrid beg = (BaseExpressionGrid) gw;
                                    beg.getParentInformation().setRowIndex(rowIndex);
                                }
                            }
                        }
                    }
                });
            }
        };
    }
}
