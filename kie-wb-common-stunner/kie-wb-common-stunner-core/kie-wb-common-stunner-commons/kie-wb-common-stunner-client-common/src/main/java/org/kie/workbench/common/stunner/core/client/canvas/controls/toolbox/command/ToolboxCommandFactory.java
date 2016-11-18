/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command;

import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.MoveShapeDownToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.MoveShapeUpToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.RemoveToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public abstract class ToolboxCommandFactory {

    private final Instance<NewNodeCommand> newNodeCommands;
    private final Instance<NewConnectorCommand> newConnectorCommands;

    @Inject
    public ToolboxCommandFactory( final Instance<NewNodeCommand> newNodeCommands,
                                  final Instance<NewConnectorCommand> newConnectorCommands ) {
        this.newNodeCommands = newNodeCommands;
        this.newConnectorCommands = newConnectorCommands;
    }

    public abstract RemoveToolboxCommand<?> newRemoveToolboxCommand();

    public abstract MoveShapeUpToolboxCommand<?> newMoveShapeUpToolboxCommand();

    public abstract MoveShapeDownToolboxCommand<?> newMoveShapeDownToolboxCommand();

    public NewNodeCommand<?> newNodeCommand() {
        return newNodeCommands.get();
    }

    public NewConnectorCommand<?> newConnectorCommand() {
        return newConnectorCommands.get();
    }

}
