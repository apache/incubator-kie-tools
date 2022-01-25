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

package org.kie.workbench.common.dmn.client.commands.expressions.types.invocation;

import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper;
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

public class AddParameterBindingCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                      VetoUndoCommand {

    private final Invocation invocation;
    private final Binding binding;
    private final GridData uiModel;
    private final GridRow uiModelRow;
    private final int uiRowIndex;
    private final InvocationUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command canvasOperation;
    private final String name;

    public AddParameterBindingCommand(final Invocation invocation,
                                      final Binding binding,
                                      final GridData uiModel,
                                      final GridRow uiModelRow,
                                      final int uiRowIndex,
                                      final InvocationUIModelMapper uiModelMapper,
                                      final org.uberfire.mvp.Command canvasOperation) {
        this.invocation = invocation;
        this.binding = binding;
        this.uiModel = uiModel;
        this.uiModelRow = uiModelRow;
        this.uiRowIndex = uiRowIndex;
        this.uiModelMapper = uiModelMapper;
        this.canvasOperation = canvasOperation;
        this.name = InvocationDefaultValueUtilities.getNewParameterName(invocation);
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
                invocation.getBinding().add(uiRowIndex, binding);
                final InformationItem informationItem = binding.getParameter();
                informationItem.getName().setValue(name);

                binding.setParent(invocation);
                binding.getParameter().setParent(binding);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                invocation.getBinding().remove(binding);

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
                                           InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX);
                uiModelMapper.fromDMNModel(uiRowIndex,
                                           InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
                uiModelMapper.fromDMNModel(uiRowIndex,
                                           InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);

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
