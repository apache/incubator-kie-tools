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

package org.kie.workbench.common.dmn.client.commands.expressions.types.undefined;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.UIModelMapper;
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
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.kie.workbench.common.dmn.client.commands.util.CommandUtils.extractGridCellValue;

public class SetCellValueCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                               VetoUndoCommand {

    private final GridCellValueTuple<ExpressionCellValue> cellTuple;
    private final Optional<String> nodeUUID;
    private final Supplier<UIModelMapper> uiModelMapper;
    private final ExpressionGridCache expressionGridCache;
    private final org.uberfire.mvp.Command executeCanvasOperation;
    private final org.uberfire.mvp.Command undoCanvasOperation;

    private final Optional<GridCellValue<?>> oldCellValue;

    public SetCellValueCommand(final GridCellValueTuple<ExpressionCellValue> cellTuple,
                               final Optional<String> nodeUUID,
                               final Supplier<UIModelMapper> uiModelMapper,
                               final ExpressionGridCache expressionGridCache,
                               final org.uberfire.mvp.Command executeCanvasOperation,
                               final org.uberfire.mvp.Command undoCanvasOperation) {
        this.cellTuple = cellTuple;
        this.nodeUUID = nodeUUID;
        this.uiModelMapper = uiModelMapper;
        this.expressionGridCache = expressionGridCache;
        this.executeCanvasOperation = executeCanvasOperation;
        this.undoCanvasOperation = undoCanvasOperation;

        this.oldCellValue = extractGridCellValue(cellTuple);
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new AbstractGraphCommand() {
            @Override
            protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            @SuppressWarnings("unchecked")
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
                uiModelMapper.get().toDMNModel(cellTuple.getRowIndex(),
                                               cellTuple.getColumnIndex(),
                                               () -> Optional.of(cellTuple.getValue()));

                // The parent of the Expression represented by the ExpressionCellValue
                // is set by UndefinedExpressionUIModelMapper instead of this Command
                // for simplicity.

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                uiModelMapper.get().toDMNModel(cellTuple.getRowIndex(),
                                               cellTuple.getColumnIndex(),
                                               () -> oldCellValue);
                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                nodeUUID.ifPresent(uuid -> expressionGridCache.putExpressionGrid(uuid, cellTuple.getValue().getValue()));

                final GridData gridData = cellTuple.getGridWidget().getModel();
                gridData.setCellValue(cellTuple.getRowIndex(),
                                      cellTuple.getColumnIndex(),
                                      cellTuple.getValue());

                executeCanvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                nodeUUID.ifPresent(expressionGridCache::removeExpressionGrid);

                if (oldCellValue.isPresent()) {
                    cellTuple.getGridWidget().getModel().setCellValue(cellTuple.getRowIndex(),
                                                                      cellTuple.getColumnIndex(),
                                                                      oldCellValue.get());
                } else {
                    cellTuple.getGridWidget().getModel().deleteCell(cellTuple.getRowIndex(),
                                                                    cellTuple.getColumnIndex());
                }

                undoCanvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }
}
