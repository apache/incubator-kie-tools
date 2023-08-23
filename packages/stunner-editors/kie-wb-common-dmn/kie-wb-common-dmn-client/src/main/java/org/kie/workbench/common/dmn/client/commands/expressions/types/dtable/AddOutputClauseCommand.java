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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
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

public class AddOutputClauseCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                  VetoUndoCommand {

    private final DecisionTable dtable;
    private final OutputClause outputClause;
    private final GridData uiModel;
    private final Supplier<OutputClauseColumn> uiModelColumnSupplier;
    private final int uiColumnIndex;
    private final DecisionTableUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command executeCanvasOperation;
    private final org.uberfire.mvp.Command undoCanvasOperation;
    private final String name;

    private Optional<OutputClauseColumn> uiModelColumn = Optional.empty();

    public AddOutputClauseCommand(final DecisionTable dtable,
                                  final OutputClause outputClause,
                                  final GridData uiModel,
                                  final Supplier<OutputClauseColumn> uiModelColumnSupplier,
                                  final int uiColumnIndex,
                                  final DecisionTableUIModelMapper uiModelMapper,
                                  final org.uberfire.mvp.Command executeCanvasOperation,
                                  final org.uberfire.mvp.Command undoCanvasOperation) {
        this.dtable = dtable;
        this.outputClause = outputClause;
        this.uiModel = uiModel;
        this.uiModelColumnSupplier = uiModelColumnSupplier;
        this.uiColumnIndex = uiColumnIndex;
        this.uiModelMapper = uiModelMapper;
        this.executeCanvasOperation = executeCanvasOperation;
        this.undoCanvasOperation = undoCanvasOperation;
        this.name = DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable);
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
                dtable.getComponentWidths().add(uiColumnIndex, null);

                final int clauseIndex = uiColumnIndex - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT - dtable.getInput().size();
                dtable.getOutput().add(clauseIndex, outputClause);
                outputClause.setName(name);

                dtable.getRule().forEach(rule -> {
                    final LiteralExpression le = new LiteralExpression();
                    le.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
                    rule.getOutputEntry().add(clauseIndex, le);
                    le.setParent(rule);
                });

                outputClause.setParent(dtable);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                dtable.getComponentWidths().remove(uiColumnIndex);

                final int clauseIndex = dtable.getOutput().indexOf(outputClause);
                dtable.getRule().forEach(rule -> rule.getOutputEntry().remove(clauseIndex));
                dtable.getOutput().remove(outputClause);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                if (!uiModelColumn.isPresent()) {
                    uiModelColumn = Optional.of(uiModelColumnSupplier.get());
                }
                uiModel.insertColumn(uiColumnIndex,
                                     uiModelColumn.get());

                for (int rowIndex = 0; rowIndex < dtable.getRule().size(); rowIndex++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               uiColumnIndex);
                }

                updateParentInformation();

                executeCanvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                uiModelColumn.ifPresent(uiModel::deleteColumn);

                updateParentInformation();

                undoCanvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }

    public void updateParentInformation() {
        CommandUtils.updateParentInformation(uiModel);
    }
}
