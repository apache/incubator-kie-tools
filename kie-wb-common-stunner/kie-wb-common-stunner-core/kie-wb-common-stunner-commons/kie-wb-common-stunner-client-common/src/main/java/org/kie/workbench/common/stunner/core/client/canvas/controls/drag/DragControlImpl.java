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

package org.kie.workbench.common.stunner.core.client.canvas.controls.drag;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDragBounds;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragContext;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
public class DragControlImpl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements DragControl<AbstractCanvasHandler, Element> {

    private static Logger LOGGER = Logger.getLogger(DragControlImpl.class.getName());

    private static final int delta = 10;

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    protected final double[] dragShapeSize = new double[]{0, 0};

    protected DragContext dragContext;

    protected Element selectedElement;

    protected DragControlImpl() {
        this(null);
    }

    @Inject
    public DragControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    public void bind(ClientFullSession session) {
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (KeysMatcher.doKeysMatch(keys,
                                    KeyboardEvent.Key.ESC)) {
            if (Objects.nonNull(dragContext) && Objects.nonNull(selectedElement)) {
                dragContext.reset();
                deregister(selectedElement);
                clear();
            }
        }
    }

    private void clear() {
        dragContext = null;
        selectedElement = null;
    }

    @Override
    public void unbind() {
        //nothing to unbind on KeyboardControl
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (checkNotRegistered(element)) {
            final AbstractCanvas<?> canvas = canvasHandler.getAbstractCanvas();
            final Shape<?> shape = canvas.getShape(element.getUUID());
            if (shape.getShapeView() instanceof HasEventHandlers) {
                final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
                // Register the drag handler.
                if (supportsDrag(hasEventHandlers)) {
                    final DragHandler handler = new DragHandler() {

                        @Override
                        public void start(final DragEvent event) {
                            doDragStart(element);
                            dragContext = event.getDragContext();
                            selectedElement = element;
                        }

                        @Override
                        public void handle(final DragEvent event) {
                        }

                        @Override
                        public void end(final DragEvent event) {
                            final CommandResult<CanvasViolation> result = doDragEnd(element);
                            if (CommandUtils.isError(result)) {
                                LOGGER.log(Level.SEVERE,
                                           "Update element's position command failed [result=" + result + "]");
                                event.getDragContext().reset();
                            }
                            clear();
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.DRAG,
                                                handler);
                    registerHandler(element.getUUID(),
                                    handler);
                }

                // Set the drag bounds.
                if (shape.getShapeView() instanceof HasDragBounds) {
                    ensureDragConstraints((HasDragBounds<?>) shape.getShapeView());
                }

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
                            canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.AUTO);
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.MOUSE_EXIT,
                                                outHandler);
                    registerHandler(shape.getUUID(),
                                    outHandler);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> move(final Element element,
                                               final double tx,
                                               final double ty) {
        final CanvasCommand<AbstractCanvasHandler> c = canvasCommandFactory.updatePosition((Node<View<?>, Edge>) element,
                                                                                           tx,
                                                                                           ty);
        CommandResult<CanvasViolation> result = getCommandManager().allow(canvasHandler,
                                                                          c);
        if (!CommandUtils.isError(result)) {
            result = getCommandManager().execute(canvasHandler,
                                                 c);
        }
        return result;
    }

    public CommandResult<CanvasViolation> moveUp(final Element element) {
        return translate(element,
                         0,
                         -delta);
    }

    public CommandResult<CanvasViolation> moveDown(final Element element) {
        return translate(element,
                         0,
                         delta);
    }

    public CommandResult<CanvasViolation> moveLeft(final Element element) {
        return translate(element,
                         -delta,
                         0);
    }

    public CommandResult<CanvasViolation> moveRight(final Element element) {
        return translate(element,
                         delta,
                         0);
    }

    public CommandResult<CanvasViolation> translate(final Element element,
                                                    final double dx,
                                                    final double dy) {
        final Point2D p;
        try {
            p = GraphUtils.getPosition((View) element.getContent());
        } catch (ClassCastException e) {
            LOGGER.log(Level.WARNING,
                       "Update element's position command only cannot be applied to View elements.");
            return CanvasCommandResultBuilder.FAILED;
        }
        final double tx = p.getX() + dx;
        final double ty = p.getY() + dy;
        return move(element,
                    tx,
                    ty);
    }

    @Override
    protected void doDisable() {
        super.doDisable();
        commandManagerProvider = null;
    }

    @Override
    public void disable() {
        super.disable();
        clear();
    }

    @SuppressWarnings("unchecked")
    protected void ensureDragConstraints(final HasDragBounds<?> shapeView) {
        final Graph<DefinitionSet, ? extends Node> graph = canvasHandler.getGraphIndex().getGraph();
        final Bounds bounds = graph.getContent().getBounds();
        shapeView.setDragBounds(bounds.getUpperLeft().getX(),
                                bounds.getUpperLeft().getY(),
                                bounds.getLowerRight().getX(),
                                bounds.getLowerRight().getY());
    }

    private void doDragStart(final Element element) {
        final double[] size = GraphUtils.getNodeSize((View) element.getContent());
        dragShapeSize[0] = size[0];
        dragShapeSize[1] = size[1];
    }

    private CommandResult<CanvasViolation> doDragEnd(final Element element) {
        final AbstractCanvas<?> canvas = canvasHandler.getAbstractCanvas();
        final Shape<?> shape = canvas.getShape(element.getUUID());
        final double x = shape.getShapeView().getShapeX();
        final double y = shape.getShapeView().getShapeY();
        return move(element,
                    x,
                    y);
    }

    private boolean supportsDrag(final HasEventHandlers shapeView) {
        return shapeView.supports(ViewEventType.DRAG);
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
}
