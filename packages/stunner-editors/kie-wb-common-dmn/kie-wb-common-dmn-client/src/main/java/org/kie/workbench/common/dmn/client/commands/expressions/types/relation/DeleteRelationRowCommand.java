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

import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
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

public class DeleteRelationRowCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                    VetoUndoCommand {

    private final Relation relation;
    private final GridData uiModel;
    private final int uiRowIndex;
    private final org.uberfire.mvp.Command canvasOperation;

    private final List oldRow;
    private final GridRow oldUiModelRow;

    public DeleteRelationRowCommand(final Relation relation,
                                    final GridData uiModel,
                                    final int uiRowIndex,
                                    final org.uberfire.mvp.Command canvasOperation) {
        this.relation = relation;
        this.uiModel = uiModel;
        this.uiRowIndex = uiRowIndex;
        this.canvasOperation = canvasOperation;

        this.oldRow = relation.getRow().get(uiRowIndex);
        this.oldUiModelRow = uiModel.getRow(uiRowIndex);
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
                relation.getRow().remove(uiRowIndex);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                relation.getRow().add(uiRowIndex, oldRow);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                uiModel.deleteRow(uiRowIndex);

                updateRowNumbers();
                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler handler) {
                uiModel.insertRow(uiRowIndex, oldUiModelRow);

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
