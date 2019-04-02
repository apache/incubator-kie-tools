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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Default
public class LocationControlImpl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements LocationControl<AbstractCanvasHandler, Element>,
                   CanvasControl.SessionAware<EditorSession> {

    private final static double LARGE_DISTANCE = 25d;
    private final static double NORMAL_DISTANCE = 5d;
    private final static double SHORT_DISTANCE = 1d;

    private static Logger LOGGER = Logger.getLogger(LocationControlImpl.class.getName());

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final Event<ShapeLocationsChangedEvent> shapeLocationsChangedEvent;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;
    private final Collection<String> selectedIDs = new LinkedList<>();
    private final Event<CanvasSelectionEvent> selectionEvent;

    protected LocationControlImpl() {
        this(null,
             null,
             null);
    }

    @Inject
    public LocationControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                               final Event<ShapeLocationsChangedEvent> shapeLocationsChangedEvent,
                               final Event<CanvasSelectionEvent> selectionEvent) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.shapeLocationsChangedEvent = shapeLocationsChangedEvent;
        this.selectionEvent = selectionEvent;
    }

    @Override
    public void bind(final EditorSession session) {
        // Keyboard event handling.
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    private void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (KeysMatcher.doKeysMatch(keys,
                                    KeyboardEvent.Key.ESC)) {
            getWiresManager().resetContext();
        }

        handleArrowKeys(keys);
    }

    private void handleArrowKeys(final KeyboardEvent.Key... keys) {

        final int selectedIDsCount = selectedIDs.size();

        if (selectedIDsCount == 0) {
            return;
        }

        double movementDistance = NORMAL_DISTANCE;

        if (KeysMatcher.isKeyMatch(keys, KeyboardEvent.Key.CONTROL)) {
            movementDistance = LARGE_DISTANCE;
        } else if (KeysMatcher.isKeyMatch(keys, KeyboardEvent.Key.SHIFT)) {
            movementDistance = SHORT_DISTANCE;
        }

        double horizontalDistance = 0d;
        double verticalDistance = 0d;

        if (KeysMatcher.isKeyMatch(keys, KeyboardEvent.Key.ARROW_LEFT)) {
            horizontalDistance = -movementDistance;
        } else if (KeysMatcher.isKeyMatch(keys, KeyboardEvent.Key.ARROW_RIGHT)) {
            horizontalDistance = movementDistance;
        }

        if (KeysMatcher.isKeyMatch(keys, KeyboardEvent.Key.ARROW_UP)) {
            verticalDistance = -movementDistance;
        } else if (KeysMatcher.isKeyMatch(keys, KeyboardEvent.Key.ARROW_DOWN)) {
            verticalDistance = movementDistance;
        }

        if (verticalDistance == 0 && horizontalDistance == 0) {
            return;
        }

        List<Element> moveNodes = new ArrayList<>();
        List<Point2D> movePositions = new ArrayList<>();

        for (String uuid : selectedIDs) {
            final Node<View<?>, Edge> node = canvasHandler.getGraphIndex().getNode(uuid);
            if (node != null) {
                final Point2D nodePosition = GraphUtils.getPosition(node.getContent());
                final Point2D movePosition = new Point2D(nodePosition.getX() + horizontalDistance, nodePosition.getY() + verticalDistance);
                moveNodes.add(node);
                movePositions.add(movePosition);
            }
        }

        move(moveNodes.toArray(new Element[]{}), movePositions.toArray(new Point2D[]{}));
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    protected void doInit() {
        super.doInit();
        getWiresManager().setLocationAcceptor(LOCATION_ACCEPTOR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (null != element.asNode() && checkNotRegistered(element)) {
            final AbstractCanvas<?> canvas = canvasHandler.getAbstractCanvas();
            final Shape<?> shape = canvas.getShape(element.getUUID());
            final ShapeView shapeView = shape.getShapeView();

            // Enable drag & ensure location constraints are set, if any.
            shapeView.setDragEnabled(true);
            ensureDragConstraints(canvas, shapeView);

            // Register handlers.
            if (shape.getShapeView() instanceof HasEventHandlers) {
                final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;

                // Change mouse cursor, if shape supports it.
                if (supportsMouseEnter(hasEventHandlers) &&
                        supportsMouseExit(hasEventHandlers)) {
                    final MouseEnterHandler overHandler = new MouseEnterHandler() {
                        @Override
                        public void handle(MouseEnterEvent event) {
                            canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.MOVE);
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.MOUSE_ENTER,
                                                overHandler);
                    registerHandler(shape.getUUID(),
                                    overHandler);
                    final MouseExitHandler outHandler = new MouseExitHandler() {
                        @Override
                        public void handle(MouseExitEvent event) {
                            canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.DEFAULT);
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.MOUSE_EXIT,
                                                outHandler);
                    registerHandler(shape.getUUID(),
                                    outHandler);

                    //Adding DragHandler on the shape to check whether the moving shape is not selected, th
                    final DragHandler dragHandler = new DragHandler() {
                        @Override
                        public void start(DragEvent event) {
                            if (Objects.nonNull(selectionEvent)) {
                                //select the moving shape, if not
                                selectionEvent.fire(new CanvasSelectionEvent(canvasHandler, shape.getUUID()));
                            }
                        }

                        @Override
                        public void end(DragEvent event) {

                        }

                        @Override
                        public void handle(DragEvent event) {

                        }
                    };

                    hasEventHandlers.addHandler(ViewEventType.DRAG, dragHandler);
                    registerHandler(shape.getUUID(), dragHandler);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> move(final Element[] elements,
                                               final Point2D[] locations) {
        if (elements.length != locations.length) {
            throw new IllegalArgumentException("The length for the elements to move " +
                                                       "does not match the locations provided.");
        }

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder =
                new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                        .forward();

        for (int i = 0; i < elements.length; i++) {
            final Element element = elements[i];
            builder.addCommand(createMoveCommand(element, locations[i]));
        }

        final Command<AbstractCanvasHandler, CanvasViolation> command = builder.build();

        CommandResult<CanvasViolation> result = getCommandManager().allow(canvasHandler, command);
        if (!CommandUtils.isError(result)) {
            result = getCommandManager().execute(canvasHandler, command);

            if (!CommandUtils.isError(result)) {
                List<String> uuids = Arrays.stream(elements).map(Element::getUUID).collect(Collectors.toList());
                shapeLocationsChangedEvent.fire(new ShapeLocationsChangedEvent(uuids, canvasHandler));
            }
        }

        return result;
    }

    @Override
    protected void doClear() {
        selectedIDs.clear();
        super.doClear();
    }

    @Override
    protected void doDestroy() {
        clear();
        getWiresManager().setLocationAcceptor(ILocationAcceptor.ALL);
        commandManagerProvider = null;
        super.doDestroy();
    }

    void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        checkNotNull("event",
                     event);

        if (checkEventContext(event)) {
            selectedIDs.clear();
            selectedIDs.addAll(event.getIdentifiers());
        }
    }

    void onCanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (checkEventContext(event)) {
            selectedIDs.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private CanvasCommand<AbstractCanvasHandler> createMoveCommand(final Element element,
                                                                   final Point2D location) {
        return canvasCommandFactory.updatePosition((Node<View<?>, Edge>) element,
                                                   location);
    }

    private boolean supportsMouseEnter(final HasEventHandlers shapeView) {
        return shapeView.supports(ViewEventType.MOUSE_ENTER);
    }

    private boolean supportsMouseExit(final HasEventHandlers shapeView) {
        return shapeView.supports(ViewEventType.MOUSE_EXIT);
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    final ILocationAcceptor LOCATION_ACCEPTOR = new ILocationAcceptor() {

        @Override
        public boolean allow(final WiresContainer[] wiresContainers,
                             final com.ait.lienzo.client.core.types.Point2D[] point2DS) {
            return true;
        }

        @Override
        public boolean accept(final WiresContainer[] wiresContainers,
                              final com.ait.lienzo.client.core.types.Point2D[] points) {
            if (wiresContainers.length != points.length) {
                throw new IllegalArgumentException("The location acceptor parameters size do not match.");
            }
            final Element[] elements = new Element[wiresContainers.length];
            final Point2D[] locations = new Point2D[points.length];
            int i = 0;
            for (final WiresContainer container : wiresContainers) {
                if (container instanceof ShapeView) {
                    final ShapeView shapeView = (ShapeView) container;
                    final String uuid = shapeView.getUUID();
                    elements[i] = canvasHandler.getGraphIndex().get(uuid);
                    locations[i] = new Point2D(points[i].getX(),
                                               points[i].getY());
                }
                i++;
            }
            if (elements.length > 0) {
                final CommandResult<CanvasViolation> result =
                        move(elements, locations);
                if (CommandUtils.isError(result)) {
                    LOGGER.log(Level.SEVERE,
                               "Update element's position command failed [result=" + result + "]");
                    return false;
                }
            }
            return true;
        }
    };

    @SuppressWarnings("unchecked")
    private static void ensureDragConstraints(final AbstractCanvas<?> canvas,
                                              final ShapeView shapeView) {
        final Bounds bounds = canvas.getView().getPanel().getLocationConstraints();
        ShapeUtils.enforceLocationConstraints(shapeView,
                                              bounds);
    }

    private WiresManager getWiresManager() {
        final WiresCanvas canvas = (WiresCanvas) canvasHandler.getCanvas();
        return canvas.getWiresManager();
    }
}
