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

package org.kie.workbench.common.stunner.core.client.command;

import java.util.Collection;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public interface CanvasCommandFactory<H extends CanvasHandler> {

    CanvasCommand<H> addNode(final Node candidate,
                             final String shapeSetId);

    CanvasCommand<H> addChildNode(final Node parent,
                                  final Node candidate,
                                  final String shapeSetId);

    CanvasCommand<H> addDockedNode(final Node parent,
                                   final Node candidate,
                                   final String shapeSetId);

    CanvasCommand<H> deleteNode(final Node candidate);

    CanvasCommand<H> delete(final Collection<Element> candidates);

    CanvasCommand<H> addConnector(final Node sourceNode,
                                  final Edge candidate,
                                  final Connection connection,
                                  final String shapeSetId);

    CanvasCommand<H> deleteConnector(final Edge candidate);

    CanvasCommand<H> setChildNode(final Node parent,
                                  final Node candidate);

    CanvasCommand<H> removeChild(final Node parent,
                                 final Node candidate);

    CanvasCommand<H> updateChildNode(final Node parent,
                                     final Node candidate);

    CanvasCommand<H> dockNode(final Node parent,
                              final Node candidate);

    CanvasCommand<H> unDockNode(final Node parent,
                                final Node candidate);

    CanvasCommand<H> updateDockNode(final Node parent,
                                    final Node candidate);

    CanvasCommand<H> updateDockNode(final Node parent,
                                    final Node candidate,
                                    final boolean adjustPosition);

    CanvasCommand<H> draw();

    CanvasCommand<H> morphNode(final Node<? extends Definition<?>, Edge> candidate,
                               final MorphDefinition morphDefinition,
                               final String morphTarget,
                               final String shapeSetId);

    CanvasCommand<H> setSourceNode(final Node<? extends View<?>, Edge> node,
                                   final Edge<? extends ViewConnector<?>, Node> edge,
                                   final Connection connection);

    CanvasCommand<H> setTargetNode(final Node<? extends View<?>, Edge> node,
                                   final Edge<? extends ViewConnector<?>, Node> edge,
                                   final Connection connection);

    CanvasCommand<H> updatePosition(final Node<View<?>, Edge> element,
                                    final Point2D location);

    CanvasCommand<H> updatePropertyValue(final Element element,
                                         final String propertyId,
                                         final Object value);

    CanvasCommand<H> clearCanvas();

    CanvasCommand<H> cloneNode(Node candidate, String parentUuid, Point2D cloneLocation, Consumer<Node> callback);

    CanvasCommand<H> cloneConnector(Edge candidate, String sourceUUID, String targetUUID, String shapeSetId, Consumer<Edge> callback);

    CanvasCommand<H> addControlPoint(Edge candidate, ControlPoint... controlPoints);

    CanvasCommand<H> deleteControlPoint(Edge candidate, ControlPoint... controlPoints);

    CanvasCommand<H> updateControlPointPosition(Edge candidate, ControlPoint controlPoint, Point2D position);
}
