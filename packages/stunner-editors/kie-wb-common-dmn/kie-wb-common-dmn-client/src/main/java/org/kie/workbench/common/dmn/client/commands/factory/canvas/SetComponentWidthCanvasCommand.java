/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class SetComponentWidthCanvasCommand extends AbstractCanvasCommand {

    private final DMNGridColumn uiColumn;
    private final double oldWidth;
    private final double width;

    public SetComponentWidthCanvasCommand(final DMNGridColumn uiColumn,
                                          final double oldWidth,
                                          final double width) {
        this.uiColumn = uiColumn;
        this.oldWidth = oldWidth;
        this.width = width;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        try {
            uiColumn.setWidth(width);
            uiColumn.getGridWidget().batch();
        } catch (Exception e) {
            return CanvasCommandResultBuilder.failed();
        }
        return CanvasCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        try {
            uiColumn.setWidth(oldWidth);
            uiColumn.getGridWidget().batch();
        } catch (Exception e) {
            return CanvasCommandResultBuilder.failed();
        }
        return CanvasCommandResultBuilder.SUCCESS;
    }
}
