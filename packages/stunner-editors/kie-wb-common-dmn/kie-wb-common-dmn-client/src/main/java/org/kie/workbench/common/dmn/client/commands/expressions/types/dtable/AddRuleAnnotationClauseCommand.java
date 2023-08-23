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
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumn;
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

public class AddRuleAnnotationClauseCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                          VetoUndoCommand {

    private final DecisionTable decisionTable;
    private final RuleAnnotationClause ruleAnnotationClause;
    private final GridData uiModel;
    private final Supplier<RuleAnnotationClauseColumn> uiModelColumnSupplier;
    private final int uiColumnIndex;
    private final DecisionTableUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command executeCanvasOperation;
    private final org.uberfire.mvp.Command undoCanvasOperation;
    private String name;

    private Optional<RuleAnnotationClauseColumn> uiModelColumn = Optional.empty();

    public AddRuleAnnotationClauseCommand(final DecisionTable decisionTable,
                                          final RuleAnnotationClause ruleAnnotationClause,
                                          final GridData uiModel,
                                          final Supplier<RuleAnnotationClauseColumn> uiModelColumnSupplier,
                                          final int uiColumnIndex,
                                          final DecisionTableUIModelMapper uiModelMapper,
                                          final org.uberfire.mvp.Command executeCanvasOperation,
                                          final org.uberfire.mvp.Command undoCanvasOperation) {
        this.decisionTable = decisionTable;
        this.ruleAnnotationClause = ruleAnnotationClause;
        this.uiModel = uiModel;
        this.uiModelColumnSupplier = uiModelColumnSupplier;
        this.uiColumnIndex = uiColumnIndex;
        this.uiModelMapper = uiModelMapper;
        this.executeCanvasOperation = executeCanvasOperation;
        this.undoCanvasOperation = undoCanvasOperation;
        this.name = getNewRuleAnnotationClauseName();
    }

    Optional<RuleAnnotationClauseColumn> getUiModelColumn() {
        return uiModelColumn;
    }

    private String getName() {
        return this.name;
    }

    String getNewRuleAnnotationClauseName() {
        return DecisionTableDefaultValueUtilities.getNewRuleAnnotationClauseName(decisionTable);
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
                decisionTable.getComponentWidths().add(uiColumnIndex, null);

                final int clauseIndex = getClauseIndex();
                decisionTable.getAnnotations().add(clauseIndex, ruleAnnotationClause);
                ruleAnnotationClause.getName().setValue(getName());

                decisionTable.getRule().forEach(rule -> {
                    final RuleAnnotationClauseText ruleAnnotationClauseText = new RuleAnnotationClauseText();
                    ruleAnnotationClauseText.getText().setValue(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_EXPRESSION_TEXT);
                    rule.getAnnotationEntry().add(clauseIndex, ruleAnnotationClauseText);
                    ruleAnnotationClauseText.setParent(rule);
                });

                ruleAnnotationClause.setParent(decisionTable);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                decisionTable.getComponentWidths().remove(uiColumnIndex);

                final int clauseIndex = decisionTable.getAnnotations().indexOf(ruleAnnotationClause);
                decisionTable.getRule().forEach(rule -> rule.getAnnotationEntry().remove(clauseIndex));
                decisionTable.getAnnotations().remove(ruleAnnotationClause);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    int getClauseIndex() {
        return uiColumnIndex - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT - decisionTable.getInput().size() - decisionTable.getOutput().size();
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                if (!getUiModelColumn().isPresent()) {
                    uiModelColumn = Optional.of(uiModelColumnSupplier.get());
                }
                getUiModelColumn().ifPresent(c -> uiModel.insertColumn(uiColumnIndex, c));

                for (int rowIndex = 0; rowIndex < decisionTable.getRule().size(); rowIndex++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               uiColumnIndex);
                }

                updateParentInformation();

                executeCanvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                getUiModelColumn().ifPresent(uiModel::deleteColumn);

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
