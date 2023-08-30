/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getDockedNodes;

public class ResizeNodeCommand extends AbstractCanvasCompositeCommand {

    private final Element<? extends View> candidate;
    private final BoundingBox boundingBox;
    private final BiFunction<Shape, Integer, Point2D> magnetLocationProvider;
    private final Consumer<Shape> onResize;

    private double widthBefore;
    private double heightBefore;

    public ResizeNodeCommand(final Element<? extends View> candidate,
                             final BoundingBox boundingBox,
                             final BiFunction<Shape, Integer, Point2D> magnetLocationProvider) {
        this(candidate, boundingBox, magnetLocationProvider, shape -> {
        });
    }

    public ResizeNodeCommand(final Element<? extends View> candidate,
                             final BoundingBox boundingBox,
                             final BiFunction<Shape, Integer, Point2D> magnetLocationProvider,
                             final Consumer<Shape> onResize) {
        this.candidate = candidate;
        this.boundingBox = boundingBox;
        this.magnetLocationProvider = magnetLocationProvider;
        this.onResize = onResize;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> initialize(final AbstractCanvasHandler context) {
        super.initialize(context);

        widthBefore = candidate.getContent().getBounds().getWidth();
        heightBefore = candidate.getContent().getBounds().getHeight();
        final double w = boundingBox.getMaxX();
        final double h = boundingBox.getMaxY();

        // Execute the update position and update property/ies command/s on the bean instance to achieve the new bounds.
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = getResizeCommands(context,
                                                                                                 w,
                                                                                                 h);
        if (null != commands) {
            final Node<View<?>, Edge> node = (Node<View<?>, Edge>) candidate;

            commands.forEach(this::addCommand);

            //Updating Docked nodes position
            if (GraphUtils.hasDockedNodes(node)) {
                updateDockedNodesPosition(context, node);
            }

            //Updating connection positions to the node
            if (GraphUtils.hasConnections(node)) {
                updateConnectionsPositions(context, node);
            }
        }

        return this;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = super.execute(context);
        return postOperation(context, result, boundingBox.getMaxX(), boundingBox.getMaxY());
    }

    @Override
    public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = super.undo(context);
        return postOperation(context, result, widthBefore, heightBefore);
    }

    public Element<? extends View> getCandidate() {
        return candidate;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public BiFunction<Shape, Integer, Point2D> getMagnetLocationProvider() {
        return magnetLocationProvider;
    }

    public Consumer<Shape> getOnResize() {
        return onResize;
    }

    CommandResult<CanvasViolation> postOperation(final AbstractCanvasHandler context,
                                                 final CommandResult<CanvasViolation> result,
                                                 final double width,
                                                 final double height) {
        if (!CommandUtils.isError(result)) {
            final Point2D current = GraphUtils.getPosition(candidate.getContent());
            final Bounds newBounds = Bounds.create(current.getX(),
                                                   current.getY(),
                                                   current.getX() + width,
                                                   current.getY() + height);

            candidate.getContent().setBounds(newBounds);
            final ShapeView shapeView = getShape(context, candidate.getUUID()).getShapeView();
            ShapeUtils.setSizeFromBoundingBox(shapeView, width, height);
            onResize.accept(getShape(context, candidate.getUUID()));
        }
        return result;
    }

    private void updateConnectionsPositions(final AbstractCanvasHandler canvasHandler,
                                            final Node<View<?>, Edge> node) {
        GraphUtils.getSourceConnections(node)
                .forEach(edge -> edge.getContent()
                        .getSourceConnection()
                        .ifPresent(connection -> handleConnections(canvasHandler, node, () -> connection, () -> new SetConnectionSourceNodeCommand(node, edge, connection)))
                );

        GraphUtils.getTargetConnections(node)
                .forEach(edge -> edge.getContent()
                        .getTargetConnection()
                        .ifPresent(connection -> handleConnections(canvasHandler, node, () -> connection, () -> new SetConnectionTargetNodeCommand(node, edge, connection))
                        )
                );
    }

    private void handleConnections(final AbstractCanvasHandler canvasHandler,
                                   final Node<View<?>, Edge> node,
                                   final Supplier<Connection> connectionSupplier,
                                   final Supplier<CanvasCommand<AbstractCanvasHandler>> commandSupplier) {
        final Connection connection = connectionSupplier.get();
        if (Objects.isNull(connection) || !(connection instanceof MagnetConnection)) {
            return;
        }

        final MagnetConnection magnetConnection = (MagnetConnection) connection;
        magnetConnection.getMagnetIndex().ifPresent(index -> {
            final Shape shape = getShape(canvasHandler, node.getUUID());
            if (null != shape) {
                final Point2D location = magnetLocationProvider.apply(shape, index);
                magnetConnection.setLocation(location);
                addCommand(commandSupplier.get());
            }
        });
    }

    private void updateDockedNodesPosition(final AbstractCanvasHandler canvasHandler,
                                           final Node<View<?>, Edge> node) {
        getDockedNodes(node)
                .stream()
                .forEach(docked -> {
                    final Shape shape = getShape(canvasHandler, docked.getUUID());
                    final double dockedX = shape.getShapeView().getShapeX();
                    final double dockedY = shape.getShapeView().getShapeY();
                    addCommand(new UpdateElementPositionCommand(docked, new Point2D(dockedX, dockedY)));
                });
    }

    private static Shape getShape(final AbstractCanvasHandler canvasHandler,
                                  final String uuid) {
        return canvasHandler.getCanvas().getShape(uuid);
    }

    /**
     * It provides the necessary canvas commands in order to update the domain model with new values that will met
     * the new bounding box size.
     * It always updates the element's position, as resize can update it, and it updates as well some of the bean's properties.
     */
    private List<Command<AbstractCanvasHandler, CanvasViolation>> getResizeCommands(final AbstractCanvasHandler canvasHandler,
                                                                                    final double w,
                                                                                    final double h) {
        final Definition content = candidate.getContent();
        final Object def = content.getDefinition();
        final DefinitionAdapter<Object> adapter =
                canvasHandler.getDefinitionManager()
                        .adapters().registry().getDefinitionAdapter(def.getClass());
        final List<Command<AbstractCanvasHandler, CanvasViolation>> result =
                new LinkedList<>();
        final String widthField = adapter.getMetaPropertyField(def, PropertyMetaTypes.WIDTH);
        if (null != widthField) {
            appendCommandForModelProperty(canvasHandler,
                                          widthField,
                                          w,
                                          result);
        }
        final String heightField = adapter.getMetaPropertyField(def, PropertyMetaTypes.HEIGHT);
        if (null != heightField) {
            appendCommandForModelProperty(canvasHandler,
                                          heightField,
                                          h,
                                          result);
        }
        final String radiusField = adapter.getMetaPropertyField(def, PropertyMetaTypes.RADIUS);
        if (null != radiusField) {
            final double r = w > h ? (h / 2) : (w / 2);
            appendCommandForModelProperty(canvasHandler,
                                          radiusField,
                                          r,
                                          result);
        }
        return result;
    }

    private void appendCommandForModelProperty(final AbstractCanvasHandler canvasHandler,
                                               final String field,
                                               final Object value,
                                               final List<Command<AbstractCanvasHandler, CanvasViolation>> result) {
        result.add(new UpdateElementPropertyCommand(candidate,
                                                    field,
                                                    value));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + AbstractCanvasCommand.toUUID(candidate) + "," +
                "box=" + boundingBox + "]";
    }
}
