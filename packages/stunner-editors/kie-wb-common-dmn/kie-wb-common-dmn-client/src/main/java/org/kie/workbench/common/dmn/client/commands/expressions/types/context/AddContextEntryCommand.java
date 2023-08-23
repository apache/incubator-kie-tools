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

package org.kie.workbench.common.dmn.client.commands.expressions.types.context;

import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextEntryDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
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

public class AddContextEntryCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                  VetoUndoCommand {

    private final Context context;
    private final ContextEntry contextEntry;
    private final GridData uiModel;
    private final GridRow uiModelRow;
    private final int uiRowIndex;
    private final ContextUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command canvasOperation;
    private final String name;

    public AddContextEntryCommand(final Context context,
                                  final ContextEntry contextEntry,
                                  final GridData uiModel,
                                  final GridRow uiModelRow,
                                  final int uiRowIndex,
                                  final ContextUIModelMapper uiModelMapper,
                                  final org.uberfire.mvp.Command canvasOperation) {
        this.context = context;
        this.contextEntry = contextEntry;
        this.uiModel = uiModel;
        this.uiModelRow = uiModelRow;
        this.uiRowIndex = uiRowIndex;
        this.uiModelMapper = uiModelMapper;
        this.canvasOperation = canvasOperation;
        this.name = ContextEntryDefaultValueUtilities.getNewContextEntryName(context);
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
                context.getContextEntry().add(uiRowIndex, contextEntry);
                final InformationItem informationItem = contextEntry.getVariable();
                informationItem.getName().setValue(name);

                contextEntry.setParent(context);
                contextEntry.getVariable().setParent(contextEntry);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                context.getContextEntry().remove(contextEntry);
                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                uiModel.insertRow(uiRowIndex,
                                  uiModelRow);
                uiModelMapper.fromDMNModel(uiRowIndex,
                                           0);
                uiModelMapper.fromDMNModel(uiRowIndex,
                                           1);
                uiModelMapper.fromDMNModel(uiRowIndex,
                                           2);

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
                                                      uiModel.getRowCount() - 1));
    }

    public void updateParentInformation() {
        CommandUtils.updateParentInformation(uiModel);
    }
}
