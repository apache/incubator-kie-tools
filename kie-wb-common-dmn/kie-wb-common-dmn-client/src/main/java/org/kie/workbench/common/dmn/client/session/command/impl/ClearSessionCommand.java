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

package org.kie.workbench.common.dmn.client.session.command.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;

@DMNEditor
@Dependent
public class ClearSessionCommand extends org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand {

    public ClearSessionCommand() {
        //CDI proxy
    }

    @Inject
    public ClearSessionCommand(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                               final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        super(canvasCommandFactory,
              sessionCommandManager);
    }

    @Override
    protected void onCommandExecuted(final @Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        if (commandExecutedEvent.getCommand() instanceof VetoExecutionCommand) {
            return;
        }
        super.onCommandExecuted(commandExecutedEvent);
    }

    @Override
    protected void onCommandUndoExecuted(final @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        if (commandUndoExecutedEvent.getCommand() instanceof VetoUndoCommand) {
            return;
        }
        super.onCommandUndoExecuted(commandUndoExecutedEvent);
    }
}
