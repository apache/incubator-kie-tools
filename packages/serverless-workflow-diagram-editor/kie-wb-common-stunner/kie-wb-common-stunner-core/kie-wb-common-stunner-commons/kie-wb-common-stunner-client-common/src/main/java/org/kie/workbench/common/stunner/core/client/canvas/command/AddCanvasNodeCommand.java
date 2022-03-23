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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * Registers a node shape into de canvas.
 */
public class AddCanvasNodeCommand extends AbstractRegistrationCanvasNodeCommand {

    public AddCanvasNodeCommand(final Node<? extends View<?>, Edge> candidate,
                                final String shapeSetId) {
        super(candidate,
              shapeSetId);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new DeleteCanvasNodeCommand(getCandidate()).execute(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + toUUID(getCandidate()) + "," +
                "shapeSet=" + getShapeSetId() + "]";
    }
}
