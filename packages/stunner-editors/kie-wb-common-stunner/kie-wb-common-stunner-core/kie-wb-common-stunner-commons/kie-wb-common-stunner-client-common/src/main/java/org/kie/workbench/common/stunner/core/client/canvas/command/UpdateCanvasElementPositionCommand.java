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
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isError;

public class UpdateCanvasElementPositionCommand extends AbstractCanvasCommand {

    private final Element<? extends View<?>> element;
    private final Point2D location;

    public UpdateCanvasElementPositionCommand(final Element<? extends View<?>> element,
                                              final Point2D location) {
        this.element = element;
        this.location = location;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> allowResult = allow(context);
        if (isError(allowResult)) {
            return allowResult;
        }
        context.updateElementPosition(element, MutationContext.STATIC);
        moveConnectorsToTop(context);
        return allowResult;
    }

    @SuppressWarnings("unchecked")
    private void moveConnectorsToTop(AbstractCanvasHandler context) {
        if (getElement() instanceof Node) {
            ShapeUtils.moveViewConnectorsToTop(context,
                                               (Node<?, Edge>) getElement());
        }
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return execute(context);
    }

    public Element<? extends View<?>> getElement() {
        return element;
    }

    public Point2D getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [element=" + toUUID(element) + "," +
                "position=" + GraphUtils.getPosition(element.getContent()) + "]";
    }
}