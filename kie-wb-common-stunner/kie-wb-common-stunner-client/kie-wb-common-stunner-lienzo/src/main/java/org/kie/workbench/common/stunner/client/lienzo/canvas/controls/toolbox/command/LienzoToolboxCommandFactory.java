/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.toolbox.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Shape;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.util.LirnzoSvgPaths;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.RemoveToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;

@ApplicationScoped
public class LienzoToolboxCommandFactory extends ToolboxCommandFactory {

    private final ManagedInstance<RemoveToolboxCommand> removeToolboxCommands;
    private final ManagedInstance<NewNodeCommand> newNodeCommands;
    private final ManagedInstance<NewConnectorCommand> newConnectorCommands;

    @Inject
    public LienzoToolboxCommandFactory(final ManagedInstance<RemoveToolboxCommand> removeToolboxCommands,
                                       final ManagedInstance<NewNodeCommand> newNodeCommands,
                                       final ManagedInstance<NewConnectorCommand> newConnectorCommands) {
        this.removeToolboxCommands = removeToolboxCommands;
        this.newNodeCommands = newNodeCommands;
        this.newConnectorCommands = newConnectorCommands;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RemoveToolboxCommand<?> newRemoveToolboxCommand() {
        final RemoveToolboxCommand<Shape<?>> c = removeToolboxCommands.get();
        c.setIcon(LirnzoSvgPaths.createSVGIcon(LirnzoSvgPaths.getTrashIcon()));
        return c;
    }

    @Override
    public NewNodeCommand<?> newNodeToolboxCommand() {
        return newNodeCommands.get();
    }

    @Override
    public NewConnectorCommand<?> newConnectorToolboxCommand() {
        return newConnectorCommands.get();
    }
}
