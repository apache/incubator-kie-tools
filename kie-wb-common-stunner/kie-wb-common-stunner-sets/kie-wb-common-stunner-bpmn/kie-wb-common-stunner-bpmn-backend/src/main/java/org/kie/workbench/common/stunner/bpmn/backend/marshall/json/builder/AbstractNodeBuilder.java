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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

// TODO: Improve error handling.
public abstract class AbstractNodeBuilder<W, T extends Node<View<W>, Edge>>
        extends AbstractObjectBuilder<W, T> implements NodeObjectBuilder<W, T> {

    protected final Class<?> definitionClass;
    protected final String definitionId;
    protected Set<String> childNodeIds;

    public AbstractNodeBuilder(final Class<?> definitionClass,
                               final String definitionId) {

        this.definitionClass = definitionClass;
        this.definitionId = definitionId;
        this.childNodeIds = new LinkedHashSet<String>();
    }

    @Override
    public Class<?> getDefinitionClass() {
        return definitionClass;
    }

    @Override
    public AbstractNodeBuilder<W, T> child(final String nodeId) {
        childNodeIds.add(nodeId);
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + " [defClass=" + definitionClass.getName() + ", defId=" + definitionId + "] " +
                "[childrenIds=" + childNodeIds + "] ";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doBuild(final BuilderContext context) {
        if (context.getIndex().getNode(this.nodeId) == null) {
            FactoryManager factoryManager = context.getFactoryManager();
            // Build the graph node for the definition.
            String definitionId = getDefinitionToBuild(context);
            T result = (T) factoryManager.newElement(this.nodeId,
                                                     definitionId);
            // Set the def properties.
            setProperties(context,
                          (BPMNDefinition) result.getContent().getDefinition());
            // View Bounds.
            setBounds(context,
                      result);
            AddNodeCommand addNodeCommand = context.getCommandFactory().addNode(result);
            if (doExecuteCommand(context,
                                 addNodeCommand)) {
                // Post processing.
                afterNodeBuild(context,
                               result);
            } else {
                // TODO: throw an exception and handle the error.
            }
            return result;
        } else {
            return (T) context.getIndex().getNode(this.nodeId);
        }
    }

    protected String getDefinitionToBuild(final BuilderContext context) {
        if (null != definitionId) {
            return definitionId;
        }
        return context.getOryxManager().getMappingsManager().getDefinitionId(definitionClass);
    }

    protected void setBounds(BuilderContext context,
                             T node) {
        if (null != boundUL && null != boundLR) {
            Bounds bounds = new BoundsImpl(
                    new BoundImpl(boundUL[0],
                                  boundUL[1]),
                    new BoundImpl(boundLR[0],
                                  boundLR[1]));
            node.getContent().setBounds(bounds);
            setSize(context,
                    node);
        }
    }

    protected void setSize(final BuilderContext context,
                           final T node) {
        final double[] size = GraphUtils.getNodeSize(node.getContent());
        setSize(context,
                node,
                size[0],
                size[1]);
    }

    protected void setSize(final BuilderContext context,
                           final T node,
                           final double width,
                           final double height) {
        Object definition = node.getContent().getDefinition();
        Width w = null;
        Height h = null;
        Set<?> properties = context.getDefinitionManager().adapters().forDefinition().getProperties(definition);
        if (null != properties) {
            // Look for w/h or radius and set the values.
            for (Object property : properties) {
                if (property instanceof Radius) {
                    Radius radius = (Radius) property;
                    double r = getRadius(width,
                                         height);
                    radius.setValue(r);
                    break;
                }
                if (property instanceof Width) {
                    w = (Width) property;
                    w.setValue(width);
                    if (h != null) {
                        break;
                    }
                }
                if (property instanceof Height) {
                    h = (Height) property;
                    h.setValue(height);
                    if (w != null) {
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void afterNodeBuild(final BuilderContext context,
                                  final T node) {
        // Outgoing connections.
        if (outgoingResourceIds != null && !outgoingResourceIds.isEmpty()) {
            for (String outgoingNodeId : outgoingResourceIds) {
                GraphObjectBuilder<?, ?> outgoingBuilder = getBuilder(context,
                                                                      outgoingNodeId);
                if (outgoingBuilder == null) {
                    throw new RuntimeException("No outgoing edge builder for " + outgoingNodeId);
                }
                final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = new LinkedList<>();
                // If outgoing element it's a node means that it's docked.
                if (outgoingBuilder instanceof AbstractNodeBuilder) {
                    // Command - Create the docked node.
                    Node docked = (Node) outgoingBuilder.build(context);
                    commands.add(context.getCommandFactory().addDockedNode(node,
                                                                           docked));
                    // Obtain docked position and use those for the docked node.
                    final List<Double[]> dockers = ((AbstractNodeBuilder) outgoingBuilder).dockers;
                    if (!dockers.isEmpty()) {
                        // TODO: Use not only first docker coordinates?
                        Double[] dCoords = dockers.get(0);
                        double x = dCoords[0];
                        double y = dCoords[1];
                        commands.add(context.getCommandFactory().updatePosition(docked,
                                                                                new Point2D(x,
                                                                                            y)));
                    }
                } else {
                    // Create the outgoing edge.

                    AbstractEdgeBuilder edgeBuilder = (AbstractEdgeBuilder) outgoingBuilder;
                    Edge edge = (Edge) edgeBuilder.build(context);
                    if (edge != null) {
                        // Command - Execute the graph command to set the node as the edge connection's source..
                        Double sourceDocker[] = null;
                        final List<Double[]> dockers = ((AbstractEdgeBuilder) outgoingBuilder).dockers;
                        if (dockers != null && dockers.size() > 1) {
                            sourceDocker = dockers.get(0);
                        }

                        Connection sourceConnection = null;
                        if (null != sourceDocker) {
                            sourceConnection = MagnetConnection.Builder
                                    .at(sourceDocker[0],
                                        sourceDocker[1])
                                    .setAuto(edgeBuilder.isSourceAutoConnection());
                        }
                        commands.add(context.getCommandFactory().setSourceNode(node,
                                                                               edge,
                                                                               sourceConnection));
                    }
                }
                if (!commands.isEmpty()) {
                    for (Command<GraphCommandExecutionContext, RuleViolation> command : commands) {
                        doExecuteCommand(context,
                                         command);
                    }
                }
            }
        }
        // Children connections.
        if (childNodeIds != null && !childNodeIds.isEmpty()) {
            for (String childNodeId : childNodeIds) {
                GraphObjectBuilder<?, ?> childNodeBuilder = getBuilder(context,
                                                                       childNodeId);
                if (childNodeBuilder == null) {
                    throw new RuntimeException("No child node builder for " + childNodeId);
                }
                Command<GraphCommandExecutionContext, RuleViolation> command = null;
                if (childNodeBuilder instanceof NodeObjectBuilder) {
                    // Command - Create the child node and the parent-child relationship.
                    Node childNode = (Node) childNodeBuilder.build(context);
                    command = context.getCommandFactory().addChildNode(node,
                                                                       childNode);
                }
                if (null != command) {
                    doExecuteCommand(context,
                                     command);
                }
            }
        }
    }

    private boolean doExecuteCommand(final BuilderContext context,
                                     final Command<GraphCommandExecutionContext, RuleViolation> command) {
        CommandResult<RuleViolation> results = context.execute(command);

        if (hasErrors(results)) {
            throw new RuntimeException("Error building BPMN graph. " +
                                               "Command = [" + command.toString() + "] " +
                                               " Resutls = [" + results.toString() + "]");
        }
        return true;
    }

    private double getRadius(final double width,
                             final double height) {
        return width / 2;
    }
}
