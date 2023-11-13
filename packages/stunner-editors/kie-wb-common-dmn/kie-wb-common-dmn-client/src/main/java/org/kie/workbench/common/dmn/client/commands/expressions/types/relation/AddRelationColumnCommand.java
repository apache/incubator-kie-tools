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

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapperHelper;
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

public class AddRelationColumnCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                    VetoUndoCommand {

    private final Relation relation;
    private final InformationItem informationItem;
    private final GridData uiModel;
    private final Supplier<RelationColumn> uiModelColumnSupplier;
    private final int uiColumnIndex;
    private final RelationUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command executeCanvasOperation;
    private final org.uberfire.mvp.Command undoCanvasOperation;
    private final String name;

    private Optional<RelationColumn> uiModelColumn = Optional.empty();

    public AddRelationColumnCommand(final Relation relation,
                                    final InformationItem informationItem,
                                    final GridData uiModel,
                                    final Supplier<RelationColumn> uiModelColumnSupplier,
                                    final int uiColumnIndex,
                                    final RelationUIModelMapper uiModelMapper,
                                    final org.uberfire.mvp.Command executeCanvasOperation,
                                    final org.uberfire.mvp.Command undoCanvasOperation) {
        this.relation = relation;
        this.informationItem = informationItem;
        this.uiModel = uiModel;
        this.uiModelColumnSupplier = uiModelColumnSupplier;
        this.uiColumnIndex = uiColumnIndex;
        this.uiModelMapper = uiModelMapper;
        this.executeCanvasOperation = executeCanvasOperation;
        this.undoCanvasOperation = undoCanvasOperation;
        this.name = RelationDefaultValueUtilities.getNewColumnName(relation);
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
                relation.getComponentWidths().add(uiColumnIndex, null);

                final int iiIndex = uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT;
                relation.getColumn().add(iiIndex, informationItem);
                informationItem.getName().setValue(name);

                relation.getRow().forEach(row -> {
                    final LiteralExpression le = new LiteralExpression();
                    final HasExpression hasExpression = HasExpression.wrap(row, le);
                    row.getExpression().add(iiIndex, hasExpression);
                    le.setParent(row);
                });

                informationItem.setParent(relation);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                relation.getComponentWidths().remove(uiColumnIndex);

                final int columnIndex = relation.getColumn().indexOf(informationItem);
                relation.getRow().forEach(row -> row.getExpression().remove(columnIndex));
                relation.getColumn().remove(informationItem);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                if (!uiModelColumn.isPresent()) {
                    uiModelColumn = Optional.of(uiModelColumnSupplier.get());
                }
                uiModel.insertColumn(uiColumnIndex,
                                     uiModelColumn.get());
                for (int rowIndex = 0; rowIndex < relation.getRow().size(); rowIndex++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               uiColumnIndex);
                }

                updateParentInformation();

                executeCanvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler handler) {
                //Deleting the GridColumn also deletes the underlying data
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
