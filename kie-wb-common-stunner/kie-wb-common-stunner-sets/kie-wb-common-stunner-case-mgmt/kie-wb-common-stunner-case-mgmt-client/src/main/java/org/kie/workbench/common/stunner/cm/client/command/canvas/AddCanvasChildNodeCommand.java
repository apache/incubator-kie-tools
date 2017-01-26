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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.RemoveCanvasChildCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.graph.Node;

public class AddCanvasChildNodeCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node child;
    private final String shapeSetId;

    public AddCanvasChildNodeCommand(final Node parent,
                                     final Node child,
                                     final String shapeSetId) {
        this.parent = parent;
        this.child = child;
        this.shapeSetId = shapeSetId;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        return new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new AddCanvasNodeCommand(child,
                                                     shapeSetId))
                .addCommand(new SetCanvasChildNodeCommand(parent,
                                                          child))
                .build()
                .execute(context);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new RemoveCanvasChildCommand(parent,
                                                         child))
                .addCommand(new DeleteCanvasNodeCommand(child,
                                                        parent))
                .build()
                .execute(context);
    }
}
