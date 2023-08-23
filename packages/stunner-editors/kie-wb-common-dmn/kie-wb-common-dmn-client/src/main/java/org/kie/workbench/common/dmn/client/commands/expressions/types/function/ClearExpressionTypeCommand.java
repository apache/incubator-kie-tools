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

package org.kie.workbench.common.dmn.client.commands.expressions.types.function;

import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.client.commands.general.BaseClearExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.KindUtilities;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class ClearExpressionTypeCommand extends BaseClearExpressionCommand {

    private final FunctionDefinition.Kind oldKind;

    public ClearExpressionTypeCommand(final GridCellTuple cellTuple,
                                      final FunctionDefinition function,
                                      final FunctionUIModelMapper uiModelMapper,
                                      final org.uberfire.mvp.Command executeCanvasOperation,
                                      final org.uberfire.mvp.Command undoCanvasOperation) {
        super(cellTuple,
              function,
              uiModelMapper,
              executeCanvasOperation,
              undoCanvasOperation);
        this.oldKind = KindUtilities.getKind(function);
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
                hasExpression.setExpression(null);
                //An undefined ExpressionType only makes sense for FEEL
                KindUtilities.setKind((FunctionDefinition) hasExpression, FunctionDefinition.Kind.FEEL);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
                hasExpression.setExpression(oldExpression);
                KindUtilities.setKind((FunctionDefinition) hasExpression, oldKind);

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }
}
