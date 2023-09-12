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

package org.kie.workbench.common.dmn.client.commands.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.KindUtilities;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
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
import org.uberfire.mvp.ParameterizedCommand;

import static org.kie.workbench.common.dmn.client.commands.util.CommandUtils.extractGridCellValue;

public class SetKindCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                          VetoUndoCommand {

    private final GridCellTuple cellTuple;
    private final FunctionDefinition function;
    private final FunctionDefinition.Kind kind;
    private final Optional<Expression> expression;
    private final ParameterizedCommand<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> executeCanvasOperation;
    private final org.uberfire.mvp.Command undoCanvasOperation;
    private final Supplier<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> editorSupplier;

    private Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = Optional.empty();
    private final FunctionDefinition.Kind oldKind;
    private final Optional<Expression> oldExpression;
    private final Optional<GridCellValue<?>> oldCellValue;

    public SetKindCommand(final GridCellTuple cellTuple,
                          final FunctionDefinition function,
                          final FunctionDefinition.Kind kind,
                          final Optional<Expression> expression,
                          final ParameterizedCommand<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> executeCanvasOperation,
                          final org.uberfire.mvp.Command undoCanvasOperation,
                          final Supplier<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> editorSupplier) {
        this.cellTuple = cellTuple;
        this.function = function;
        this.kind = kind;
        this.expression = expression;
        this.executeCanvasOperation = executeCanvasOperation;
        this.undoCanvasOperation = undoCanvasOperation;
        this.editorSupplier = editorSupplier;

        this.oldKind = KindUtilities.getKind(function);
        this.oldExpression = Optional.ofNullable(function.getExpression());
        this.oldCellValue = extractGridCellValue(cellTuple);
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler handler) {
        return new AbstractGraphCommand() {
            @Override
            protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext gce) {
                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext gce) {
                KindUtilities.setKind(function, kind);
                function.setExpression(expression.orElse(null));

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                KindUtilities.setKind(function, oldKind);
                function.setExpression(oldExpression.orElse(null));

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                if (!editor.isPresent()) {
                    editor = editorSupplier.get();
                }

                final ExpressionCellValue value = new ExpressionCellValue(editor);
                final GridData gridData = cellTuple.getGridWidget().getModel();
                gridData.setCellValue(cellTuple.getRowIndex(),
                                      cellTuple.getColumnIndex(),
                                      value);

                executeCanvasOperation.execute(editor);

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler handler) {
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
