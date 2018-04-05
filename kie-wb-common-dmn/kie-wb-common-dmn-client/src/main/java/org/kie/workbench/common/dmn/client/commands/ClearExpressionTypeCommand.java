/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands;

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.client.commands.general.BaseClearExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class ClearExpressionTypeCommand extends BaseClearExpressionCommand {

    private final String nodeUUID;
    private final ExpressionGridCache expressionGridCache;
    private final Optional<BaseExpressionGrid> oldExpressionGrid;

    public ClearExpressionTypeCommand(final GridCellTuple cellTuple,
                                      final String nodeUUID,
                                      final HasExpression hasExpression,
                                      final ExpressionContainerUIModelMapper uiModelMapper,
                                      final ExpressionGridCache expressionGridCache,
                                      final org.uberfire.mvp.Command canvasOperation) {
        super(cellTuple,
              hasExpression,
              uiModelMapper,
              canvasOperation);
        this.nodeUUID = nodeUUID;
        this.expressionGridCache = expressionGridCache;
        this.oldExpressionGrid = expressionGridCache.getExpressionGrid(nodeUUID);
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                //Clear cache first as its content is used by the UIModelMapper to retrieve existing BaseExpressionGrid
                expressionGridCache.removeExpressionGrid(nodeUUID);

                //Use UIModelMapper to get cell value for null Expressions
                uiModelMapper.fromDMNModel(cellTuple.getRowIndex(),
                                           cellTuple.getColumnIndex());

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                expressionGridCache.putExpressionGrid(nodeUUID, oldExpressionGrid);

                //Simply write back the old value
                oldCellValue.ifPresent(v -> cellTuple.getGridWidget().getModel().setCellValue(cellTuple.getRowIndex(),
                                                                                              cellTuple.getColumnIndex(),
                                                                                              v));

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }
}
