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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

/**
 * This session command obtains the selected elements on the clipboard and clone each one of them.
 */
@Dependent
public class PasteSelectionSessionCommand extends AbstractClientSessionCommand<ClientFullSession> {

    public static final int DEFAULT_PADDING = 15;
    private static Logger LOGGER = Logger.getLogger(PasteSelectionSessionCommand.class.getName());

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private final List<String> clonedElements;
    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;
    private final CopySelectionSessionCommand copySelectionSessionCommand;

    protected PasteSelectionSessionCommand() {
        this(null, null, null, null);
    }

    @Inject
    public PasteSelectionSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                        final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                        final Event<CanvasSelectionEvent> selectionEvent,
                                        final SessionCommandFactory sessionCommandFactory) {
        super(true);
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.selectionEvent = selectionEvent;
        this.clonedElements = new ArrayList<>();
        this.copySelectionSessionCommand = sessionCommandFactory.newCopySelectionCommand();
    }

    @Override
    public void bind(final ClientFullSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        this.clipboardControl = session.getClipboardControl();
        this.copySelectionSessionCommand.bind(session);
    }

    void onKeyDownEvent(final Key... keys) {
        handleCtrlV(keys);
    }

    private void handleCtrlV(Key[] keys) {
        if (doKeysMatch(keys, Key.CONTROL, Key.V)) {
            this.execute(newDefaultCallback("Error while trying to paste selected items. Message="));
        }
    }

    @Override
    public void unbind() {
        super.unbind();
        clear();
    }

    @Override
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);

        if (clipboardControl.hasElements()) {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();

            //for now just pasting Nodes not Edges
            commandBuilder.addCommands(clipboardControl.getElements().stream()
                                               .filter(element -> element instanceof Node)
                                               .map(Element::asNode)
                                               .filter(Objects::nonNull)
                                               .map(node -> (Node<View<?>, Edge>) node)
                                               .map(node -> {
                                                   String newParentUUID = getNewParentUUID(node);
                                                   return canvasCommandFactory.cloneNode(node, newParentUUID, calculateNewLocation(node, newParentUUID), cloneNodeCallback());
                                               })
                                               .collect(Collectors.toList()));

            // Execute the command.
            if (Objects.equals(commandBuilder.size(), 0)) {
                return;
            }

            final CommandResult<CanvasViolation> result;
            if (wasNodesDeletedFromGraph()) {
                //in case of a cut command the source elements were deleted from graph, so first undo the command to take node back into canvas
                clipboardControl.getRollbackCommands().forEach(command -> command.undo(getCanvasHandler()));
                result = sessionCommandManager.execute(getCanvasHandler(), commandBuilder.build());
                //after the clone execution than delete source elements again
                clipboardControl.getRollbackCommands().forEach(command -> command.execute(getCanvasHandler()));
            } else {
                //if elements are still on the graph, in case copy command, just execute the clone commands
                result = sessionCommandManager.execute(getCanvasHandler(), commandBuilder.build());
            }

            if (CommandUtils.isError(result)) {
                LOGGER.severe("Error on paste selection." + getCanvasViolations(result));
                return;
            }

            fireSelectedElementEvent();
            callback.onSuccess();
            clear();

            //copy the cloned node to the clipboard to allow pasting several times
            copySelectionSessionCommand.execute();
        }
    }

    public boolean wasNodesDeletedFromGraph() {
        return clipboardControl.getElements().stream().allMatch(element -> Objects.isNull(getElement(element.getUUID())));
    }

    public void clear() {
        clipboardControl.clear();
        clonedElements.clear();
    }

    public String getCanvasViolations(CommandResult<CanvasViolation> result) {
        if (Objects.nonNull(result) && Objects.nonNull(result.getViolations())) {
            return CommandUtils.toList(result.getViolations()).stream().map(Objects::toString).collect(Collectors.joining());
        }
        return "";
    }

    private Consumer<Node> cloneNodeCallback() {
        return clone -> clonedElements.add(clone.getUUID());
    }

    private void fireSelectedElementEvent() {
        clonedElements.stream().forEach(uuid -> selectionEvent.fire(new CanvasSelectionEvent(getCanvasHandler(), uuid)));
    }

    private String getNewParentUUID(Node node) {
        //getting parent if selected
        Optional<Element> selectedParent = getSelectedParentElement();
        if (selectedParent.isPresent() && !Objects.equals(selectedParent.get().getUUID(), node.getUUID()) && checkIfExistsOnCanvas(selectedParent.get().getUUID())) {
            return selectedParent.get().getUUID();
        }

        //getting node parent if no different parent is selected
        String nodeParentUUID = clipboardControl.getParent(node.getUUID());
        if (selectedParent.isPresent() && Objects.equals(selectedParent.get().getUUID(), node.getUUID()) && Objects.nonNull(nodeParentUUID) && checkIfExistsOnCanvas(nodeParentUUID)) {
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

    private Optional<Element> getSelectedParentElement() {
        if (null != getSession().getSelectionControl()) {
            Collection<String> selectedItems = getSession().getSelectionControl().getSelectedItems();
            if (Objects.nonNull(selectedItems) && !selectedItems.isEmpty()) {
                String selectedUUID = selectedItems.stream().filter(Objects::nonNull).findFirst().orElse(null);
                return Optional.ofNullable(getElement(selectedUUID));
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
            return new Point2D(position.getX() + DEFAULT_PADDING, position.getY() + DEFAULT_PADDING);
        }

        //default or node was deleted
        return position;
    }

    private boolean hasParentChanged(Node<? extends View<?>, Edge> node, String newParentUUID) {
        return !Objects.equals(clipboardControl.getParent(node.getUUID()), newParentUUID);
    }

    private boolean existsOnCanvas(Node<? extends View<?>, Edge> node) {
        return Objects.nonNull(getCanvasHandler().getGraphIndex().getNode(node.getUUID()));
    }
}