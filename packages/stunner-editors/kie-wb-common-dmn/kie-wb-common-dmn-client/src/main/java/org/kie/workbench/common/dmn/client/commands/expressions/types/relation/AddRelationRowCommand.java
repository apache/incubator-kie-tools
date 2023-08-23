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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapper;
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
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class AddRelationRowCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                 VetoUndoCommand {

    private final Relation relation;
    private final List row;
    private final GridData uiModel;
    private final GridRow uiModelRow;
    private final int uiRowIndex;
    private final RelationUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command canvasOperation;

    public AddRelationRowCommand(final Relation relation,
                                 final List row,
                                 final GridData uiModel,
                                 final GridRow uiModelRow,
                                 final int uiRowIndex,
                                 final RelationUIModelMapper uiModelMapper,
                                 final org.uberfire.mvp.Command canvasOperation) {
        this.relation = relation;
        this.row = row;
        this.uiModel = uiModel;
        this.uiModelRow = uiModelRow;
        this.uiRowIndex = uiRowIndex;
        this.uiModelMapper = uiModelMapper;
        this.canvasOperation = canvasOperation;
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
                relation.getRow().add(uiRowIndex,
                                      row);
                relation.getColumn().forEach(ii -> {
                    final LiteralExpression le = new LiteralExpression();
                    final HasExpression hasExpression = HasExpression.wrap(ii, le);
                    row.getExpression().add(hasExpression);
                    le.setParent(row);
                });

                row.setParent(relation);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                relation.getRow().remove(row);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                int columnIndex = 0;
                uiModel.insertRow(uiRowIndex,
                                  uiModelRow);
                uiModelMapper.fromDMNModel(uiRowIndex,
                                           columnIndex++);
                for (int ii = 0; ii < relation.getColumn().size(); ii++) {
                    uiModelMapper.fromDMNModel(uiRowIndex,
                                               columnIndex++);
                }

                updateRowNumbers();
                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler handler) {
                final int rowIndex = uiModel.getRows().indexOf(uiModelRow);
                uiModel.deleteRow(rowIndex);

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
