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

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

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

    CanvasCommand<H> addConnector(final Node sourceNode,
                                  final Edge candidate,
                                  final Magnet magnet,
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

    CanvasCommand<H> draw();

    CanvasCommand<H> morphNode(final Node<? extends Definition<?>, Edge> candidate,
                               final MorphDefinition morphDefinition,
                               final String morphTarget,
                               final String shapeSetId);

    CanvasCommand<H> setSourceNode(final Node<? extends View<?>, Edge> node,
                                   final Edge<? extends View<?>, Node> edge,
                                   final Magnet magnet,
                                   boolean isNewConnection);

    CanvasCommand<H> setTargetNode(final Node<? extends View<?>, Edge> node,
                                   final Edge<? extends View<?>, Node> edge,
                                   final Magnet magnet,
                                   boolean isNewConnection);

    CanvasCommand<H> updatePosition(final Node<View<?>, Edge> element,
                                    final Double x,
                                    final Double y);

    CanvasCommand<H> updatePropertyValue(final Element element,
                                         final String propertyId,
                                         final Object value);

    CanvasCommand<H> clearCanvas();
}
