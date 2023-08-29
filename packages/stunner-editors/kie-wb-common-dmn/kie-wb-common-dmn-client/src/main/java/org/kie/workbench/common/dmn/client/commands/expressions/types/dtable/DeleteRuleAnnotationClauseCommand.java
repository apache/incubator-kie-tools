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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class DeleteRuleAnnotationClauseCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                             VetoUndoCommand {

    private final DecisionTable decisionTable;
    private final GridData uiModel;
    private final int uiColumnIndex;
    private final DecisionTableUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command executeCanvasOperation;
    private final org.uberfire.mvp.Command undoCanvasOperation;
    private final RuleAnnotationClause oldRuleClause;
    private final List<RuleAnnotationClauseText> oldColumnData;
    private final GridColumn<?> oldUiModelColumn;

    public DeleteRuleAnnotationClauseCommand(final DecisionTable decisionTable,
                                             final GridData uiModel,
                                             final int uiColumnIndex,
                                             final DecisionTableUIModelMapper uiModelMapper,
                                             final org.uberfire.mvp.Command executeCanvasOperation,
                                             final org.uberfire.mvp.Command undoCanvasOperation) {
        this.decisionTable = decisionTable;
        this.uiModel = uiModel;
        this.uiColumnIndex = uiColumnIndex;
        this.uiModelMapper = uiModelMapper;
        this.executeCanvasOperation = executeCanvasOperation;
        this.undoCanvasOperation = undoCanvasOperation;
        this.oldRuleClause = decisionTable.getAnnotations().get(getRuleAnnotationClauseIndex());
        this.oldColumnData = extractColumnData();
        this.oldUiModelColumn = uiModel.getColumns().get(uiColumnIndex);
    }

    org.uberfire.mvp.Command getUndoCanvasOperation() {
        return undoCanvasOperation;
    }

    DecisionTableUIModelMapper getUiModelMapper() {
        return uiModelMapper;
    }

    org.uberfire.mvp.Command getExecuteCanvasOperation() {
        return executeCanvasOperation;
    }

    DecisionTable getDecisionTable() {
        return decisionTable;
    }

    int getUiColumnIndex() {
        return uiColumnIndex;
    }

    GridData getUiModel() {
        return uiModel;
    }

    List<RuleAnnotationClauseText> extractColumnData() {
        final int clauseIndex = getRuleAnnotationClauseIndex();
        return getDecisionTable().getRule()
                .stream()
                .map(row -> row.getAnnotationEntry().get(clauseIndex))
                .collect(Collectors.toList());
    }

    int getRuleAnnotationClauseIndex() {
        return getUiColumnIndex() - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT - getDecisionTable().getInput().size() - getDecisionTable().getOutput().size();
    }

    RuleAnnotationClause getOldRuleClause() {
        return oldRuleClause;
    }

    List<RuleAnnotationClauseText> getOldColumnData() {
        return oldColumnData;
    }

    GridColumn<?> getOldUiModelColumn() {
        return oldUiModelColumn;
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
                getDecisionTable().getComponentWidths().remove(getUiColumnIndex());

                final int clauseIndex = getRuleAnnotationClauseIndex();
                getDecisionTable().getRule().forEach(row -> row.getAnnotationEntry().remove(clauseIndex));
                getDecisionTable().getAnnotations().remove(clauseIndex);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                getDecisionTable().getComponentWidths().add(getUiColumnIndex(), getOldUiModelColumn().getWidth());

                final int clauseIndex = getRuleAnnotationClauseIndex();
                getDecisionTable().getAnnotations().add(clauseIndex,
                                                        getOldRuleClause());
                IntStream.range(0, getDecisionTable().getRule().size())
                        .forEach(rowIndex -> {
                            final RuleAnnotationClauseText value = getOldColumnData().get(rowIndex);
                            getDecisionTable().getRule().get(rowIndex).getAnnotationEntry().add(clauseIndex, value);
                        });

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                final GridColumn<?> gridColumn = getUiModel().getColumns().get(getUiColumnIndex());
                getUiModel().deleteColumn(gridColumn);

                updateParentInformation();

                getExecuteCanvasOperation().execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler handler) {
                getUiModel().insertColumn(getUiColumnIndex(),
                                          getOldUiModelColumn());
                for (int rowIndex = 0; rowIndex < getDecisionTable().getRule().size(); rowIndex++) {
                    getUiModelMapper().fromDMNModel(rowIndex,
                                                    getUiColumnIndex());
                }

                updateParentInformation();

                getUndoCanvasOperation().execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }

    public void updateParentInformation() {
        CommandUtils.updateParentInformation(uiModel);
    }
}
