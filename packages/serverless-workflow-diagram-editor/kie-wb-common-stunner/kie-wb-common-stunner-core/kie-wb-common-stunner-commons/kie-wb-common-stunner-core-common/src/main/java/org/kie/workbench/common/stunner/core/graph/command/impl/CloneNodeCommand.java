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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.crysknife.client.ManagedInstance;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getPosition;

/**
 * A Command to clone a node and set as a child of the given parent.
 */
public class CloneNodeCommand extends AbstractGraphCompositeCommand {

    private final Node<Definition, Edge> candidate;
    private final Optional<String> parentUuidOptional;
    private final Point2D position;
    private transient Node<View, Edge> clone;
    private transient Optional<Consumer<Node>> callbackOptional;
    private transient ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor;
    private transient List<Command<GraphCommandExecutionContext, RuleViolation>> childrenCommands;

    private static final Logger LOGGER = Logger.getLogger(CloneNodeCommand.class.getName());

    protected CloneNodeCommand() {
        this(null, null, null, null, null);
    }

    public CloneNodeCommand(final Node candidate, final String parentUuid) {
        this(checkNotNull("candidate", candidate),
             checkNotNull("parentUuid", parentUuid), null, null, null);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public CloneNodeCommand(final Node candidate, final String parentUuid, final Point2D position, final Consumer<Node> callback, final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        this.candidate = candidate;
        this.parentUuidOptional = Optional.ofNullable(parentUuid);
        this.position = position;
        this.callbackOptional = Optional.ofNullable(callback);
        this.childrenTraverseProcessor = childrenTraverseProcessor;
    }

    public CloneNodeCommand(final Node candidate, final String parentUuid, final Point2D position, final Consumer<Node> callback) {
        this(candidate, parentUuid, position, callback, null);
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractCompositeCommand<GraphCommandExecutionContext, RuleViolation> initialize(GraphCommandExecutionContext context) {
        //getting the node parent
        Optional<String> parentUUID = getParentUUID();
        if (!parentUUID.isPresent()) {
            throw new IllegalStateException("Parent not found for node " + candidate);
        }

        final Object bean = candidate.getContent().getDefinition();
        final DefinitionId definitionId = context.getDefinitionManager().adapters().forDefinition().getId(bean);
        clone = (Node<View, Edge>) context.getFactoryManager().newElement(UUID.uuid(), definitionId.value()).asNode();

        cloneNodeContentWithProperties(context);

        //creating node commands to be executed
        createNodeCommands(clone, parentUUID.get(), position);

        return this;
    }

    protected void createNodeCommands(final Node<View, Edge> clone,
                                      final String parentUUID,
                                      final Point2D position) {
        addCommand(new RegisterNodeCommand(clone));
        addCommand(new AddChildNodeCommand(parentUUID, clone, position));
    }

    void cloneNodeContentWithProperties(final GraphCommandExecutionContext context) {

        final View cloneContent = getClone().getContent();
        final View candidateContent = (View) getCandidate().getContent();
        final Bounds candidateBounds = candidateContent.getBounds();
        final Bounds clonedBounds = cloneBounds(candidateBounds);
        final CloneManager cloneManager = context.getDefinitionManager().cloneManager();
        final Object clonedDefinition = cloneManager.clone(candidateContent.getDefinition(), ClonePolicy.ALL);

        cloneContent.setBounds(clonedBounds);
        cloneContent.setDefinition(clonedDefinition);
    }

    Bounds cloneBounds(final Bounds bounds) {
        final Bound ul = bounds.getUpperLeft();
        final Bound lr = bounds.getLowerRight();
        return Bounds.create(ul.getX(), ul.getY(), lr.getX(), lr.getY());
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        CommandResult<RuleViolation> result = super.execute(context);
        if (CommandUtils.isError(result)) {
            return result;
        }

        List<CommandResult<RuleViolation>> commandResults = new ArrayList<>();
        commandResults.add(result);

        //Children cloning process
        childrenCommands = new LinkedList<>();

        //process children nodes
        if (GraphUtils.hasChildren(candidate)) {
            commandResults.addAll(processChildrenNodes(context));
        }

        //process docked nodes on the root candidate
        if (GraphUtils.hasDockedNodes(candidate)) {
            commandResults.addAll(processDockedNodes(context));
        }

        //in case of any error than rollback all commands
        CommandResult<RuleViolation> finalResult = buildResult(commandResults);
        if (CommandUtils.isError(finalResult)) {
            processMultipleFunctions(childrenCommands,
                                     c -> doUndo(context, c),
                                     reverted -> {
                                     });
            processMultipleFunctions(getCommands(),
                                     c -> doUndo(context, c),
                                     reverted -> {
                                     });
            return finalResult;
        }

        callbackOptional.ifPresent(callback -> callback.accept(clone));
        LOGGER.info("Node " + candidate.getUUID() + "was cloned successfully to " + clone.getUUID());
        return finalResult;
    }

    public List<CommandResult<RuleViolation>> processChildrenNodes(GraphCommandExecutionContext context) {
        List<CommandResult<RuleViolation>> commandResults = new ArrayList<>();
        final Map<String, Node<View, Edge>> cloneNodeMapUUID = new HashMap<>();

        //process nodes
        Graph<View, Node<View, Edge>> graph = (Graph<View, Node<View, Edge>>) context.getGraphIndex().getGraph();
        childrenTraverseProcessor.get().setRootUUID(candidate.getUUID()).traverse(graph, new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
            @Override
            public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                              final Node<View, Edge> node) {
                //clone child and map clone uuid
                CloneNodeCommand command = createCloneChildCommand(node,
                                                                   clone.getUUID(),
                                                                   getPosition(node.getContent()),
                                                                   childClone -> cloneNodeMapUUID.put(node.getUUID(), childClone),
                                                                   childrenTraverseProcessor);
                childrenCommands.add(command);
                commandResults.add(command.execute(context));
                //just traverse the first level children of the root node
                return false;
            }
        });

        //process connectors
        //get connector from source node and map to cloned node
        commandResults.addAll(cloneNodeMapUUID.keySet().stream()
                                      .flatMap(sourceUUID -> getNode(context, sourceUUID).getOutEdges().stream())
                                      .filter(edge -> edge.getContent() instanceof ViewConnector)
                                      .filter(edge -> Objects.nonNull(edge.getSourceNode()) && Objects.nonNull(edge.getTargetNode()))
                                      .map(edge -> {
                                          Command<GraphCommandExecutionContext, RuleViolation> command;
                                          Node<View, Edge> candidateToClone = cloneNodeMapUUID.get(edge.getTargetNode().getUUID());
                                          Node<View, Edge> parentToClone = cloneNodeMapUUID.get(edge.getSourceNode().getUUID());
                                          if (Objects.isNull(candidateToClone) || Objects.isNull(parentToClone)) {
                                              return null;
                                          }
                                          //need to check whether the edge for dock or child
                                          if (edge.getContent() instanceof Dock) {
                                              command = new DockNodeCommand(parentToClone, candidateToClone);
                                          } else {
                                              command = new CloneConnectorCommand(edge,
                                                                                  parentToClone.getUUID(),
                                                                                  candidateToClone.getUUID());
                                          }
                                          childrenCommands.add(command);
                                          return command;
                                      })
                                      .filter(Objects::nonNull)
                                      .map(command -> command.execute(context))
                                      .collect(Collectors.toList()));
        return commandResults;
    }

    protected CloneNodeCommand createCloneChildCommand(final Node candidate,
                                                       final String parentUuid,
                                                       final Point2D position,
                                                       final Consumer<Node> callback,
                                                       final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        return new CloneNodeCommand(candidate,
                                    parentUuid,
                                    position,
                                    callback,
                                    childrenTraverseProcessor);
    }

    private List<CommandResult<RuleViolation>> processDockedNodes(GraphCommandExecutionContext context) {
        return candidate.getOutEdges().stream()
                .filter(edge -> edge.getContent() instanceof Dock)
                .map(edge -> edge.getTargetNode())
                .filter(node -> node.getContent() instanceof View)
                .map(targetNode -> {
                         CloneNodeCommand command =
                                 new CloneNodeCommand(targetNode,
                                                      parentUuidOptional.get(),
                                                      GraphUtils.getPosition((View) targetNode.getContent()),
                                                      cloneDock ->
                                                              new DockNodeCommand(clone, cloneDock).execute(context),
                                                      childrenTraverseProcessor);
                         childrenCommands.add(command);
                         return command;
                     }
                ).map(command -> command.execute(context))
                .collect(Collectors.toList());
    }

    private Optional<String> getParentUUID() {
        return parentUuidOptional.isPresent() ? parentUuidOptional : getDefaultParent();
    }

    private Optional<String> getDefaultParent() {
        Optional<? extends Element<?>> parent = Optional.ofNullable(GraphUtils.getParent(candidate));
        if (parent.isPresent()) {
            return Optional.of(parent.get().getUUID());
        }
        return Optional.empty();
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        return new SafeDeleteNodeCommand(clone).execute(context);
    }

    public Node<View, Edge> getClone() {
        return clone;
    }

    void setClone(final Node<View, Edge> clone) {
        this.clone = clone;
    }

    public Node<Definition, Edge> getCandidate() {
        return candidate;
    }

    public List<Command<GraphCommandExecutionContext, RuleViolation>> getChildrenCommands() {
        return childrenCommands;
    }
}
