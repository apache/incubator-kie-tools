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
package org.kie.workbench.common.dmn.client.commands.expressions.types.list;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
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
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class MoveRowsCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                           VetoUndoCommand {

    private final List list;
    private final DMNGridData uiModel;
    private final int index;
    private final java.util.List<GridRow> rows;
    private final org.uberfire.mvp.Command canvasOperation;

    private final int oldIndex;

    public MoveRowsCommand(final List list,
                           final DMNGridData uiModel,
                           final int index,
                           final java.util.List<GridRow> rows,
                           final org.uberfire.mvp.Command canvasOperation) {
        this.list = list;
        this.uiModel = uiModel;
        this.index = index;
        this.rows = new ArrayList<>(rows);
        this.canvasOperation = canvasOperation;

        this.oldIndex = uiModel.getRows().indexOf(rows.get(0));
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
                moveRows(index);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                moveRows(oldIndex);

                return GraphCommandResultBuilder.SUCCESS;
            }

            private void moveRows(final int index) {
                final java.util.List<HasExpression> rowsToMove = rows.stream()
                        .map(r -> uiModel.getRows().indexOf(r))
                        .map(i -> list.getExpression().get(i))
                        .collect(Collectors.toList());

                CommandUtils.moveRows(list.getExpression(),
                                      rowsToMove,
                                      index);
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                uiModel.moveRowsTo(index,
                                   rows);

                updateRowNumbers();
                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                uiModel.moveRowsTo(oldIndex,
                                   rows);

                updateRowNumbers();
                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }

    public void updateRowNumbers() {
        CommandUtils.updateRowNumbers(uiModel,
                                      IntStream.range(0,
                                                      uiModel.getRowCount()));
    }

    public void updateParentInformation() {
        CommandUtils.updateParentInformation(uiModel);
    }
}
