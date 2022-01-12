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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

/**
 * Clears the canvas.
 * Note: Undo not supported due to ClearGraphCommand does not support it as well.
 */
public class ClearCanvasCommand extends AbstractCanvasCommand {

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        context.clearCanvas();
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        throw new UnsupportedOperationException("Undo operation for Clear Canvas Command is still not supported.");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
