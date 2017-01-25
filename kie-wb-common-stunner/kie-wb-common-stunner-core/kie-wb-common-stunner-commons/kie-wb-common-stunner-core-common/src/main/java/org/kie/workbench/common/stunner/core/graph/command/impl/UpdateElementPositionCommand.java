/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BoundsExceededException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command to update an element's bounds.
 */
@Portable
public final class UpdateElementPositionCommand extends AbstractGraphCommand {

    private static Logger LOGGER = Logger.getLogger(UpdateElementPositionCommand.class.getName());

    private final String uuid;
    private final Double x;
    private final Double y;
    private Double oldX;
    private Double oldY;
    private transient Node<?, Edge> node;

    public UpdateElementPositionCommand(final @MapsTo("uuid") String uuid,
                                        final @MapsTo("x") Double x,
                                        final @MapsTo("y") Double y) {
        this.uuid = PortablePreconditions.checkNotNull("uuid",
                                                       uuid);
        this.x = PortablePreconditions.checkNotNull("x",
                                                    x);
        this.y = PortablePreconditions.checkNotNull("y",
                                                    y);
        this.node = null;
    }

    public UpdateElementPositionCommand(final Node<?, Edge> node,
                                        final Double x,
                                        final Double y) {
        this(node.getUUID(),
             x,
             y);
        this.node = PortablePreconditions.checkNotNull("node",
                                                       node);
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        checkNodeNotNull(context);
        return GraphCommandResultBuilder.SUCCESS;
    }

    private Node<?, Edge> checkNodeNotNull(final GraphCommandExecutionContext context) {
        if (null == node) {
            node = super.checkNodeNotNull(context,
                                          uuid);
        }
        return node;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final Element<?> element = checkNodeNotNull(context);
        final Point2D oldPosition = GraphUtils.getPosition((View) element.getContent());
        final double[] oldSize = GraphUtils.getNodeSize((View) element.getContent());
        this.oldX = oldPosition.getX();
        this.oldY = oldPosition.getY();
        final double w = oldSize[0];
        final double h = oldSize[1];
        final BoundsImpl newBounds = new BoundsImpl(new BoundImpl(x,
                                                                  y),
                                                    new BoundImpl(x + w,
                                                                  y + h));
        checkBounds(context,
                    newBounds);
        ((View) element.getContent()).setBounds(newBounds);
        LOGGER.log(Level.FINE,
                   "Moving element bounds to [" + x + "," + y + "] [" + (x + w) + "," + (y + h) + "]");
        return GraphCommandResultBuilder.SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private void checkBounds(final GraphCommandExecutionContext context,
                             final Bounds bounds) {
        final Graph<DefinitionSet, Node> graph = (Graph<DefinitionSet, Node>) getGraph(context);
        if (!GraphUtils.checkBounds(graph,
                                    bounds)) {
            final Bounds graphBounds = graph.getContent().getBounds();
            throw new BoundsExceededException(this,
                                              bounds,
                                              graphBounds.getLowerRight().getX(),
                                              graphBounds.getLowerRight().getY());
        }
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final UpdateElementPositionCommand undoCommand = new UpdateElementPositionCommand(checkNodeNotNull(context),
                                                                                          oldX,
                                                                                          oldY);
        return undoCommand.execute(context);
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Node<?, Edge> getNode() {
        return node;
    }

    public Double getOldX() {
        return oldX;
    }

    public Double getOldY() {
        return oldY;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "UpdateElementPositionCommand [element=" + uuid + ", x=" + x + ", y=" + y + "]";
    }
}
