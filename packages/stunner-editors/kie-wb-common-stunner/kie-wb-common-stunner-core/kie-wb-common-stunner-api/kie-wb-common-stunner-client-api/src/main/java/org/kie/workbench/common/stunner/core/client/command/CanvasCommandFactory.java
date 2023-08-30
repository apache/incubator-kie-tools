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


package org.kie.workbench.common.stunner.core.client.command;

import java.util.Collection;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
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

    CanvasCommand<H> addNode(Node candidate,
                             String shapeSetId);

    CanvasCommand<H> addChildNode(Node parent,
                                  Node candidate,
                                  String shapeSetId);

    CanvasCommand<H> addDockedNode(Node parent,
                                   Node candidate,
                                   String shapeSetId);

    CanvasCommand<H> deleteNode(Node candidate);

    CanvasCommand<H> delete(Collection<Element> candidates);

    CanvasCommand<H> addConnector(Node sourceNode,
                                  Edge candidate,
                                  Connection connection,
                                  String shapeSetId);

    CanvasCommand<H> deleteConnector(Edge candidate);

    CanvasCommand<H> cloneNode(Node candidate,
                               String parentUuid,
                               Point2D cloneLocation,
                               Consumer<Node> callback);

    CanvasCommand<H> cloneConnector(Edge candidate,
                                    String sourceUUID,
                                    String targetUUID,
                                    String shapeSetId,
                                    Consumer<Edge> callback);

    CanvasCommand<H> setChildNode(Node parent,
                                  Node candidate);

    CanvasCommand<H> removeChild(Node parent,
                                 Node candidate);

    CanvasCommand<H> updateChildNode(Node parent,
                                     Node candidate);

    CanvasCommand<H> updateChildren(Node parent,
                                    Collection<Node> candidates);

    CanvasCommand<H> dockNode(Node parent,
                              Node candidate);

    CanvasCommand<H> unDockNode(Node parent,
                                Node candidate);

    CanvasCommand<H> updateDockNode(Node parent,
                                    Node candidate);

    CanvasCommand<H> updateDockNode(Node parent,
                                    Node candidate,
                                    boolean adjustPosition);

    CanvasCommand<H> draw();

    CanvasCommand<H> morphNode(Node<? extends Definition<?>, Edge> candidate,
                               MorphDefinition morphDefinition,
                               String morphTarget,
                               String shapeSetId);

    CanvasCommand<H> updatePosition(Node<View<?>, Edge> element,
                                    Point2D location);

    CanvasCommand<H> resize(Element<? extends View<?>> element,
                            BoundingBox boundingBox);

    CanvasCommand<H> updatePropertyValue(Element element,
                                         String nameField,
                                         Object value);

    CanvasCommand<H> updateDomainObjectPropertyValue(DomainObject domainObject,
                                                     String propertyId,
                                                     Object value);

    CanvasCommand<H> setSourceNode(Node<? extends View<?>, Edge> node,
                                   Edge<? extends ViewConnector<?>, Node> edge,
                                   Connection connection);

    CanvasCommand<H> setTargetNode(Node<? extends View<?>, Edge> node,
                                   Edge<? extends ViewConnector<?>, Node> edge,
                                   Connection connection);

    CanvasCommand<H> addControlPoint(Edge candidate,
                                     ControlPoint controlPoint,
                                     int index);

    CanvasCommand<H> deleteControlPoint(Edge candidate,
                                        int index);

    CanvasCommand<H> updateControlPointPosition(Edge candidate,
                                                ControlPoint[] controlPoints);

    CanvasCommand<H> clearCanvas();
}
