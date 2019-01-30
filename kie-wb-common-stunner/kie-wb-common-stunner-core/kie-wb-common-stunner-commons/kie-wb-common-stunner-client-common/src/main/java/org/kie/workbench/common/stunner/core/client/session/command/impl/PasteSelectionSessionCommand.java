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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.ReverseCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.Counter;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

/**
 * This session command obtains the selected elements on the clipboard and clone each one of them.
 */
@Dependent
@Default
public class PasteSelectionSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    public static final int DEFAULT_PADDING = 15;
    private static Logger LOGGER = Logger.getLogger(PasteSelectionSessionCommand.class.getName());

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private final Map<String, String> clonedElements;
    private final CopySelectionSessionCommand copySelectionSessionCommand;
    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;
    private transient DoubleSummaryStatistics yPositionStatistics;
    private final DefinitionUtils definitionUtils;
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    protected PasteSelectionSessionCommand() {
        this(null, null, null, null, null);
    }

    @Inject
    public PasteSelectionSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                        @Any ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
                                        final Event<CanvasSelectionEvent> selectionEvent,
                                        final CopySelectionSessionCommand copySelectionSessionCommand,
                                        final DefinitionUtils definitionUtils) {
        super(true);
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactoryInstance = canvasCommandFactoryInstance;
        this.selectionEvent = selectionEvent;
        this.clonedElements = new HashMap<>();
        this.copySelectionSessionCommand = copySelectionSessionCommand;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        this.clipboardControl = session.getClipboardControl();
        this.copySelectionSessionCommand.bind(session);
        this.canvasCommandFactory = this.loadCanvasFactory(canvasCommandFactoryInstance, definitionUtils);
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    void onKeyDownEvent(final Key... keys) {
        if (isEnabled()) {
            handleCtrlV(keys);
        }
    }

    private void handleCtrlV(final Key[] keys) {
        if (doKeysMatch(keys,
                        Key.CONTROL,
                        Key.V)) {
            this.execute();
        }
    }

    @Override
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);

        if (clipboardControl.hasElements()) {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> nodesCommandBuilder = createCommandBuilder();

            Counter processedNodesCountdown = new Counter((int) clipboardControl.getElements().stream()
                    .filter(element -> element instanceof Node).count());

            //first processing nodes
            nodesCommandBuilder.addCommands(clipboardControl.getElements().stream()
                                                    .filter(element -> element instanceof Node)
                                                    .filter(Objects::nonNull)
                                                    .map(node -> (Node<View<?>, Edge>) node)
                                                    .map(node -> {
                                                        String newParentUUID = getNewParentUUID(node);
                                                        return canvasCommandFactory.cloneNode(node, newParentUUID, calculateNewLocation(node, newParentUUID), cloneNodeCallback(node, processedNodesCountdown));
                                                    })
                                                    .collect(Collectors.toList()));

            if (Objects.equals(nodesCommandBuilder.size(), 0)) {
                return;
            }

            // Execute the command for cloning nodes
            CommandResult<CanvasViolation> finalResult;
            if (wasNodesDeletedFromGraph()) {
                //in case of a cut command the source elements were deleted from graph, so first undo the command to take node back into canvas
                clipboardControl.getRollbackCommands().forEach(command -> nodesCommandBuilder.addFirstCommand(new ReverseCommand(command)));
                //after the clone execution than delete source elements again
                clipboardControl.getRollbackCommands().forEach(nodesCommandBuilder::addCommand);

                finalResult = executeCommands(nodesCommandBuilder, processedNodesCountdown);
            } else {
                //if elements are still on the graph, in case copy command, just execute the clone commands
                finalResult = executeCommands(nodesCommandBuilder, processedNodesCountdown);
            }

            if (CommandUtils.isError(finalResult)) {
                LOGGER.severe("Error pasting selection." + getCanvasViolations(finalResult));
                return;
            }

            fireSelectedElementEvent();
            callback.onSuccess();
            clear();

            //copy the cloned node to the clipboard to allow pasting several times
            copySelectionSessionCommand.execute();
        }
    }

    private CommandResult<CanvasViolation> executeCommands(CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder, Counter processedNodesCountdown) {
        CommandResult<CanvasViolation> nodesResult = sessionCommandManager.execute(getCanvasHandler(), commandBuilder.build());
        if (CommandUtils.isError(nodesResult)) {
            return nodesResult;
        }
        // Processing connectors: after all nodes has been cloned (this is necessary because we need the cloned nodes UUIDs to than clone the Connectors
        CommandResult<CanvasViolation> connectorsResult = processConnectors(processedNodesCountdown);

        //After nodes and connectors command execution than it is necessary to update the command registry (to allow a single undo/redo)
        if (!CommandUtils.isError(connectorsResult)) {
            updateCommandsRegistry();
        }

        return new CanvasCommandResultBuilder()
                .setType(nodesResult.getType())
                .addViolations((Objects.nonNull(nodesResult.getViolations()) ?
                        StreamSupport.stream(nodesResult.getViolations().spliterator(), false).collect(Collectors.toList()) :
                        Collections.emptyList()))
                .addViolations((Objects.nonNull(connectorsResult.getViolations()) ?
                        StreamSupport.stream(connectorsResult.getViolations().spliterator(), false).collect(Collectors.toList()) :
                        Collections.emptyList()))
                .build();
    }

    private void updateCommandsRegistry() {
        Command<AbstractCanvasHandler, CanvasViolation> connectorsExecutedCommand = sessionCommandManager.getRegistry().pop();
        Command<AbstractCanvasHandler, CanvasViolation> nodesExecutedCommand = sessionCommandManager.getRegistry().pop();
        sessionCommandManager.getRegistry().register(new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                                                             .addCommand(nodesExecutedCommand)
                                                             .addCommand(connectorsExecutedCommand)
                                                             .reverse()
                                                             .build());
    }

    private CommandResult<CanvasViolation> processConnectors(Counter processedNodesCountdown) {
        if (processedNodesCountdown.equalsToValue(0)) {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = createCommandBuilder();
            commandBuilder.addCommands(clipboardControl.getElements().stream()
                                               .filter(element -> element instanceof Edge)
                                               .filter(Objects::nonNull)
                                               .map(edge -> (Edge) edge)
                                               .filter(edge -> Objects.nonNull(edge.getSourceNode()) &&
                                                       Objects.nonNull(clonedElements.get(edge.getSourceNode().getUUID())) &&
                                                       Objects.nonNull(edge.getTargetNode()) &&
                                                       Objects.nonNull(clonedElements.get(edge.getTargetNode().getUUID())))
                                               .map(edge -> canvasCommandFactory.cloneConnector(edge,
                                                                                                clonedElements.get(edge.getSourceNode().getUUID()),
                                                                                                clonedElements.get(edge.getTargetNode().getUUID()),
                                                                                                getCanvasHandler().getDiagram().getMetadata().getShapeSetId(),
                                                                                                cloneEdgeCallback(edge)))
                                               .collect(Collectors.toList()));

            return sessionCommandManager.execute(getCanvasHandler(), commandBuilder.build());
        }

        return new CanvasCommandResultBuilder().build();
    }

    private CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> createCommandBuilder() {
        return new CompositeCommand.Builder<>();
    }

    private Consumer<Edge> cloneEdgeCallback(Edge candidate) {
        return clone -> clonedElements.put(candidate.getUUID(), clone.getUUID());
    }

    public boolean wasNodesDeletedFromGraph() {
        return clipboardControl.getElements().stream().allMatch(element -> Objects.isNull(getElement(element.getUUID())));
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        clear();
        clipboardControl = null;
    }

    public void clear() {
        if (null != clipboardControl) {
            clipboardControl.clear();
        }
        clonedElements.clear();
        yPositionStatistics = null;
    }

    public String getCanvasViolations(CommandResult<CanvasViolation> result) {
        if (Objects.nonNull(result) && Objects.nonNull(result.getViolations())) {
            return CommandUtils.toList(result.getViolations()).stream().map(Objects::toString).collect(Collectors.joining());
        }
        return "";
    }

    protected void onCopySelectionCommandExecuted(@Observes CopySelectionSessionCommandExecutedEvent event) {
        checkNotNull("event", event);
        if (Objects.equals(getSession(), event.getClientSession())) {
            setEnabled(true);
            fire();
        }
    }

    protected void onCutSelectionCommandExecuted(@Observes CutSelectionSessionCommandExecutedEvent event) {
        checkNotNull("event", event);
        if (Objects.equals(getSession(), event.getClientSession())) {
            setEnabled(true);
            fire();
        }
    }

    private Consumer<Node> cloneNodeCallback(Node candidate, Counter processedNodesCountdown) {
        return clone -> {
            clonedElements.put(candidate.getUUID(), clone.getUUID());
            processedNodesCountdown.decrement();
        };
    }

    private void fireSelectedElementEvent() {
        selectionEvent.fire(new CanvasSelectionEvent(getCanvasHandler(), clonedElements.values()));
    }

    private String getNewParentUUID(Node node) {
        //getting parent if selected
        Optional<Element> selectedParent = getSelectedParentElement(node.getUUID());
        if (selectedParent.isPresent() && !Objects.equals(selectedParent.get().getUUID(), node.getUUID()) && checkIfExistsOnCanvas(selectedParent.get().getUUID())) {
            return selectedParent.get().getUUID();
        }

        //getting node parent if no different parent is selected
        String nodeParentUUID = clipboardControl.getParent(node.getUUID());
        if (selectedParent.isPresent() &&
                Objects.equals(selectedParent.get().getUUID(), node.getUUID()) &&
                Objects.nonNull(nodeParentUUID) && checkIfExistsOnCanvas(nodeParentUUID)) {
            return nodeParentUUID;
        }

        //return default parent that is the canvas in case no parent matches
        return getCanvasRootUUID();
    }

    private boolean checkIfExistsOnCanvas(String nodeParentUUID) {
        return Objects.nonNull(getElement(nodeParentUUID));
    }

    private String getCanvasRootUUID() {
        return getCanvasHandler().getDiagram().getMetadata().getCanvasRootUUID();
    }

    private Optional<Element> getSelectedParentElement(String nodeUUID) {
        if (null != getSession().getSelectionControl()) {
            Collection<String> selectedItems = getSession().getSelectionControl().getSelectedItems();
            if (Objects.nonNull(selectedItems) && !selectedItems.isEmpty()) {
                Optional<String> selectedParent = selectedItems.stream()
                        .filter(Objects::nonNull)
                        .filter(item -> Objects.equals(item, nodeUUID))
                        .findFirst();
                return (selectedParent.isPresent() ?
                        selectedParent : selectedItems.stream().filter(Objects::nonNull).findFirst())
                        .map(this::getElement);
            }
        }
        return Optional.empty();
    }

    private Point2D calculateNewLocation(final Node<? extends View<?>, Edge> node, String newParentUUID) {
        Point2D position = GraphUtils.getPosition(node.getContent());

        //new parent different from the source node
        if (hasParentChanged(node, newParentUUID)) {
            return new Point2D(DEFAULT_PADDING, DEFAULT_PADDING);
        }

        //node is still on canvas (not deleted)
        if (existsOnCanvas(node)) {
            double x = position.getX();
            double max = getYPositionStatistics().getMax();
            double min = getYPositionStatistics().getMin();
            double y = max + (position.getY() - min) + DEFAULT_PADDING;
            return new Point2D(x, y);
        }

        //default or node was deleted
        return position;
    }

    private DoubleSummaryStatistics getYPositionStatistics() {
        if (Objects.isNull(yPositionStatistics)) {
            yPositionStatistics = Stream.concat(clipboardControl.getElements().stream()
                                                        .filter(element -> element instanceof Node)
                                                        .map(element -> ((View) element.getContent()).getBounds().getLowerRight()),
                                                clipboardControl.getElements().stream().filter(element -> element instanceof Node)
                                                        .map(element -> ((View) element.getContent()).getBounds().getUpperLeft())).mapToDouble(bound -> bound.getY()).summaryStatistics();
        }
        return yPositionStatistics;
    }

    private boolean hasParentChanged(Node<? extends View<?>, Edge> node, String newParentUUID) {
        return !Objects.equals(clipboardControl.getParent(node.getUUID()), newParentUUID);
    }

    private boolean existsOnCanvas(Node<? extends View<?>, Edge> node) {
        return Objects.nonNull(getCanvasHandler().getGraphIndex().getNode(node.getUUID()));
    }
}