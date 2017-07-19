/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions.RemoveToolboxCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A toolbox control provider implementation that provides buttons for common actions that
 * can be executed for the source element.
 * <p/>
 * It provides buttons for:
 * - Removing the element.
 */
@Dependent
public class ActionsToolboxControlProvider extends AbstractToolboxControlProvider {

    private RemoveToolboxCommand removeToolboxCommand;

    protected ActionsToolboxControlProvider() {
        this(null,
             null);
    }

    @Inject
    public ActionsToolboxControlProvider(final ToolboxFactory toolboxFactory,
                                         final ToolboxCommandFactory toolboxCommandFactory) {
        super(toolboxFactory);
        this.removeToolboxCommand = toolboxCommandFactory.newRemoveToolboxCommand();
    }

    @Override
    public boolean supports(final Object definition) {
        return true;
    }

    @Override
    public ToolboxButtonGrid getGrid(final AbstractCanvasHandler context,
                                     final Element item) {
        final ToolboxButtonGridBuilder buttonGridBuilder = toolboxFactory.toolboxGridBuilder();
        return buttonGridBuilder
                .setRows(3)
                .setColumns(1)
                .setPadding(DEFAULT_PADDING)
                .build();
    }

    @Override
    public ToolboxBuilder.Direction getOn() {
        return ToolboxBuilder.Direction.NORTH_WEST;
    }

    @Override
    public ToolboxBuilder.Direction getTowards() {
        return ToolboxBuilder.Direction.SOUTH_WEST;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ToolboxCommand<AbstractCanvasHandler, ?>> getCommands(final AbstractCanvasHandler context,
                                                                      final Element item) {
        return new LinkedList<ToolboxCommand<AbstractCanvasHandler, ?>>() {{
            add(removeToolboxCommand);
        }};
    }
}
