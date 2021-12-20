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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapperHelper.RelationSection;
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

    private final Relation relation;
    private final DMNGridData uiModel;
    private final int index;
    private final java.util.List<GridColumn<?>> columns;
    private final org.uberfire.mvp.Command canvasOperation;

    private final int oldIndex;

    public MoveColumnsCommand(final Relation relation,
                              final DMNGridData uiModel,
                              final int index,
                              final java.util.List<GridColumn<?>> columns,
                              final org.uberfire.mvp.Command canvasOperation) {
        this.relation = relation;
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
                final RelationSection section = RelationUIModelMapperHelper.getSection(relation, index);
                return section == RelationSection.INFORMATION_ITEM;
            }

            @Override
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
                return moveInformationItems(index);
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                return moveInformationItems(oldIndex);
            }

            private CommandResult<RuleViolation> moveInformationItems(final int index) {
                final RelationSection section = RelationUIModelMapperHelper.getSection(relation, index);
                if (section == RelationSection.INFORMATION_ITEM) {
                    final int oldIndex = uiModel.getColumns().indexOf(columns.get(0));
                    final int relativeIndex = RelationUIModelMapperHelper.getInformationItemIndex(relation, index);
                    final int relativeOldIndex = RelationUIModelMapperHelper.getInformationItemIndex(relation, oldIndex);

                    final java.util.List<Integer> informationItemIndexesToMove = columns
                            .stream()
                            .map(c -> uiModel.getColumns().indexOf(c))
                            .map(i -> RelationUIModelMapperHelper.getInformationItemIndex(relation, i))
                            .collect(Collectors.toList());
                    moveInformationItems(relativeIndex,
                                         relativeOldIndex,
                                         relation.getColumn(),
                                         informationItemIndexesToMove);
                    CommandUtils.moveComponentWidths(Relation.STATIC_COLUMNS + relativeIndex,
                                                     Relation.STATIC_COLUMNS + relativeOldIndex,
                                                     relation.getComponentWidths(),
                                                     Collections.singletonList(oldIndex));

                    updateRowsData(relativeIndex,
                                   relativeOldIndex,
                                   relation.getRow(),
                                   informationItemIndexesToMove);

                    return GraphCommandResultBuilder.SUCCESS;
                } else {
                    return GraphCommandResultBuilder.failed();
                }
            }

            private <T> void moveInformationItems(final int relativeIndex,
                                                  final int relativeOldIndex,
                                                  final java.util.List<T> informationItems,
                                                  final java.util.List<Integer> informationItemIndexesToMove) {
                final java.util.List<T> informationItemsToMove = informationItemIndexesToMove
                        .stream()
                        .map(informationItems::get)
                        .collect(Collectors.toList());

                informationItems.removeAll(informationItemsToMove);
                if (relativeIndex < relativeOldIndex) {
                    informationItems.addAll(relativeIndex,
                                            informationItemsToMove);
                } else if (relativeIndex > relativeOldIndex) {
                    informationItems.addAll(relativeIndex - informationItemsToMove.size() + 1,
                                            informationItemsToMove);
                }
            }

            private void updateRowsData(final int relativeIndex,
                                        final int relativeOldIndex,
                                        final java.util.List<List> rows,
                                        final java.util.List<Integer> informationItemIndexesToMove) {
                rows.forEach(row -> moveInformationItems(relativeIndex,
                                                         relativeOldIndex,
                                                         row.getExpression(),
                                                         informationItemIndexesToMove));
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

                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                uiModel.moveColumnsTo(oldIndex,
                                      columns);

                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }

    public void updateParentInformation() {
        CommandUtils.updateParentInformation(uiModel);
    }
}
