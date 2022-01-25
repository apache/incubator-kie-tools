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

package org.kie.workbench.common.dmn.client.commands.general;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
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

import static org.kie.workbench.common.dmn.client.commands.util.CommandUtils.extractGridCellValue;

public class DeleteCellValueCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                  VetoUndoCommand {

    private final GridCellTuple cellTuple;
    private final Supplier<UIModelMapper> uiModelMapper;
    private final org.uberfire.mvp.Command canvasOperation;

    private final Optional<GridCellValue<?>> oldCellValue;

    public DeleteCellValueCommand(final GridCellTuple cellTuple,
                                  final Supplier<UIModelMapper> uiModelMapper,
                                  final org.uberfire.mvp.Command canvasOperation) {
        this.cellTuple = cellTuple;
        this.uiModelMapper = uiModelMapper;
        this.canvasOperation = canvasOperation;

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
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
                uiModelMapper.get().toDMNModel(cellTuple.getRowIndex(),
                                               cellTuple.getColumnIndex(),
                                               Optional::empty);
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
                cellTuple.getGridWidget().getModel().setCellValue(cellTuple.getRowIndex(),
                                                                  cellTuple.getColumnIndex(),
                                                                  null);
                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                oldCellValue.ifPresent(v -> cellTuple.getGridWidget().getModel().setCellValue(cellTuple.getRowIndex(),
                                                                                              cellTuple.getColumnIndex(),
                                                                                              v));
                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }
}
