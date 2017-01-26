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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BoundsExceededException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

public class UpdateElementPositionCommand extends AbstractGraphCommand {

    private static Logger LOGGER = Logger.getLogger(UpdateElementPositionCommand.class.getName());

    private transient Node<?, Edge> node;

    private final AbstractCanvasHandler canvasContext;
    private final Double x;
    private final Double y;
    private Double oldX;
    private Double oldY;

    public UpdateElementPositionCommand(final AbstractCanvasHandler canvasContext,
                                        final Node<?, Edge> node,
                                        final Double x,
                                        final Double y) {
        this.canvasContext = PortablePreconditions.checkNotNull("canvasContext",
                                                                canvasContext);
        this.node = PortablePreconditions.checkNotNull("node",
                                                       node);
        this.x = PortablePreconditions.checkNotNull("x",
                                                    x);
        this.y = PortablePreconditions.checkNotNull("y",
                                                    y);
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        double rx = x;
        double ry = y;
        final AbstractCanvas<?> canvas = canvasContext.getAbstractCanvas();
        final Shape<?> shape = canvas.getShape(node.getUUID());

        if (shape != null) {
            rx = shape.getShapeView().getShapeX();
            ry = shape.getShapeView().getShapeY();
        }

        final Point2D oldPosition = GraphUtils.getPosition((View) node.getContent());
        final double[] oldSize = GraphUtils.getNodeSize((View) node.getContent());
        this.oldX = oldPosition.getX();
        this.oldY = oldPosition.getY();
        final double w = oldSize[0];
        final double h = oldSize[1];
        final BoundsImpl newBounds = new BoundsImpl(new BoundImpl(rx,
                                                                  ry),
                                                    new BoundImpl(rx + w,
                                                                  ry + h));
        checkBounds(context,
                    newBounds);
        ((View) node.getContent()).setBounds(newBounds);
        LOGGER.log(Level.FINE,
                   "Moving element bounds to [" + rx + "," + ry + "] [" + (rx + w) + "," + (ry + h) + "]");
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
        final UpdateElementPositionCommand undoCommand = new UpdateElementPositionCommand(canvasContext,
                                                                                          node,
                                                                                          oldX,
                                                                                          oldY);
        return undoCommand.execute(context);
    }

    @Override
    public String toString() {
        return "UpdateElementPositionCommand [element=" + node.getUUID() + ", x=" + x + ", y=" + y + "]";
    }
}
