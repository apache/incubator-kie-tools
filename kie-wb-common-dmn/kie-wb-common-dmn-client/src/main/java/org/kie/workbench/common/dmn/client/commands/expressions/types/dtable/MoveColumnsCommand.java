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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper.DecisionTableSection;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class MoveColumnsCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                              VetoUndoCommand {

    private final DecisionTable dtable;
    private final DMNGridData uiModel;
    private final int index;
    private final List<GridColumn<?>> columns;
    private final org.uberfire.mvp.Command canvasOperation;

    private final int oldIndex;

    public MoveColumnsCommand(final DecisionTable dtable,
                              final DMNGridData uiModel,
                              final int index,
                              final List<GridColumn<?>> columns,
                              final org.uberfire.mvp.Command canvasOperation) {
        this.dtable = dtable;
        this.uiModel = uiModel;
        this.index = index;
        this.columns = new ArrayList<>(columns);
        this.canvasOperation = canvasOperation;

        this.oldIndex = uiModel.getColumns().indexOf(columns.get(0));
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new AbstractGraphCommand() {
            @Override
            protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
                return isColumnInValidSection() ? GraphCommandResultBuilder.SUCCESS : GraphCommandResultBuilder.failed();
            }

            private boolean isColumnInValidSection() {
                final DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable,
                                                                                                 index);
                return section == DecisionTableSection.INPUT_CLAUSES || section == DecisionTableSection.OUTPUT_CLAUSES;
            }

            @Override
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
                return moveClauses(index);
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                return moveClauses(oldIndex);
            }

            private CommandResult<RuleViolation> moveClauses(final int index) {
                final DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable,
                                                                                                 index);
                if (section == DecisionTableSection.INPUT_CLAUSES) {
                    final int oldIndex = uiModel.getColumns().indexOf(columns.get(0));
                    final int relativeIndex = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable,
                                                                                                  index);
                    final int relativeOldIndex = DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable,
                                                                                                     oldIndex);

                    final List<Integer> uiColumnIndexesToMove = columns
                            .stream()
                            .map(c -> uiModel.getColumns().indexOf(c))
                            .collect(Collectors.toList());
                    final List<Integer> inputClauseIndexesToMove = uiColumnIndexesToMove
                            .stream()
                            .map(i -> DecisionTableUIModelMapperHelper.getInputEntryIndex(dtable, i))
                            .collect(Collectors.toList());
                    moveClauses(relativeIndex,
                                relativeOldIndex,
                                dtable.getInput(),
                                inputClauseIndexesToMove);
                    CommandUtils.moveComponentWidths(index,
                                                     oldIndex,
                                                     dtable.getComponentWidths(),
                                                     uiColumnIndexesToMove);

                    final List<List<UnaryTests>> decisionRulesInputEntries = dtable.getRule()
                            .stream()
                            .map(DecisionRule::getInputEntry)
                            .collect(Collectors.toList());
                    updateDecisionRules(relativeIndex,
                                        relativeOldIndex,
                                        decisionRulesInputEntries,
                                        inputClauseIndexesToMove);

                    return GraphCommandResultBuilder.SUCCESS;
                } else if (section == DecisionTableSection.OUTPUT_CLAUSES) {
                    final int oldIndex = uiModel.getColumns().indexOf(columns.get(0));
                    final int relativeIndex = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable,
                                                                                                   index);
                    final int relativeOldIndex = DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable,
                                                                                                      oldIndex);
                    final List<Integer> uiColumnIndexesToMove = columns
                            .stream()
                            .map(c -> uiModel.getColumns().indexOf(c))
                            .collect(Collectors.toList());
                    final List<Integer> outputClauseIndexesToMove = uiColumnIndexesToMove
                            .stream()
                            .map(i -> DecisionTableUIModelMapperHelper.getOutputEntryIndex(dtable, i))
                            .collect(Collectors.toList());
                    moveClauses(relativeIndex,
                                relativeOldIndex,
                                dtable.getOutput(),
                                outputClauseIndexesToMove);
                    CommandUtils.moveComponentWidths(index,
                                                     oldIndex,
                                                     dtable.getComponentWidths(),
                                                     uiColumnIndexesToMove);

                    final List<List<LiteralExpression>> decisionRulesOutputEntries = dtable.getRule()
                            .stream()
                            .map(DecisionRule::getOutputEntry)
                            .collect(Collectors.toList());
                    updateDecisionRules(relativeIndex,
                                        relativeOldIndex,
                                        decisionRulesOutputEntries,
                                        outputClauseIndexesToMove);

                    return GraphCommandResultBuilder.SUCCESS;
                } else {
                    return GraphCommandResultBuilder.failed();
                }
            }

            private <T> void moveClauses(final int relativeIndex,
                                         final int relativeOldIndex,
                                         final List<T> clauses,
                                         final List<Integer> clauseIndexesToMove) {
                final List<T> clausesToMove = clauseIndexesToMove
                        .stream()
                        .map(clauses::get)
                        .collect(Collectors.toList());

                clauses.removeAll(clausesToMove);
                if (relativeIndex < relativeOldIndex) {
                    clauses.addAll(relativeIndex,
                                   clausesToMove);
                } else if (relativeIndex > relativeOldIndex) {
                    clauses.addAll(relativeIndex - clausesToMove.size() + 1,
                                   clausesToMove);
                }
            }

            private <T> void updateDecisionRules(final int relativeIndex,
                                                 final int relativeOldIndex,
                                                 final List<List<T>> clauses,
                                                 final List<Integer> clauseIndexesToMove) {
                clauses.forEach(row -> moveClauses(relativeIndex,
                                                   relativeOldIndex,
                                                   row,
                                                   clauseIndexesToMove));
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                uiModel.moveColumnsTo(index,
                                      columns);

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                uiModel.moveColumnsTo(oldIndex,
                                      columns);

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }
}
