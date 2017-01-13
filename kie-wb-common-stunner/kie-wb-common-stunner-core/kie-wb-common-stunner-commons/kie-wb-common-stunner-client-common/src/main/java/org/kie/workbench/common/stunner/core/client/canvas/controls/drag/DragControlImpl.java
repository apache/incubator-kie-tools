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

package org.kie.workbench.common.stunner.core.client.canvas.controls.drag;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
public class DragControlImpl extends AbstractCanvasHandlerRegistrationControl
        implements DragControl<AbstractCanvasHandler, Element> {

    private static Logger LOGGER = Logger.getLogger(DragControlImpl.class.getName());
    private static final int delta = 10;
    private final CanvasCommandFactory canvasCommandFactory;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private CanvasGrid dragGrid;

    protected DragControlImpl() {
        this(null,
             null);
    }

    @Inject
    public DragControlImpl(final CanvasCommandFactory canvasCommandFactory,
                           final @Request SessionCommandManager<AbstractCanvasHandler> canvasCommandManager) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.canvasCommandManager = canvasCommandManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        final AbstractCanvas<?> canvas = canvasHandler.getCanvas();
        final Shape<?> shape = canvas.getShape(element.getUUID());
        if (shape.getShapeView() instanceof HasEventHandlers) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            if (hasEventHandlers.supports(ViewEventType.DRAG)) {
                final DragHandler handler = new DragHandler() {

                    private final double[] shapeSize = new double[]{0, 0};
                    private CanvasGrid grid = null;

                    @Override
                    public void start(final DragEvent event) {
                        final double[] size = GraphUtils.getSize((View) element.getContent());
                        shapeSize[0] = size[0];
                        shapeSize[1] = size[1];
                        if (isDragGridEnabled()) {
                            this.grid = canvas.getGrid();
                            if (null == grid) {
                                canvas.setGrid(dragGrid);
                            }
                        }
                    }

                    @Override
                    public void handle(final DragEvent event) {
                        ensureDragConstrains(shape.getShapeView(),
                                             shapeSize);
                    }

                    @Override
                    public void end(final DragEvent event) {
                        final double x = shape.getShapeView().getShapeX();
                        final double y = shape.getShapeView().getShapeY();
                        move(element,
                             x,
                             y);
                        if (isDragGridEnabled()) {
                            canvas.setGrid(this.grid);
                            this.grid = null;
                        }
                    }
                };
                hasEventHandlers.addHandler(ViewEventType.DRAG,
                                            handler);
                registerHandler(element.getUUID(),
                                handler);
            }
        }
    }

    @Override
    public DragControl<AbstractCanvasHandler, Element> setDragGrid(final CanvasGrid grid) {
        this.dragGrid = grid;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> move(final Element element,
                                               final double tx,
                                               final double ty) {
        final UpdateElementPositionCommand c = canvasCommandFactory.updatePosition((Node<View<?>, Edge>) element,
                                                                                   tx,
                                                                                   ty);
        CommandResult<CanvasViolation> result = canvasCommandManager.allow(canvasHandler,
                                                                           c);
        if (!CommandUtils.isError(result)) {
            result = canvasCommandManager.execute(canvasHandler,
                                                  c);
        }
        if (CommandUtils.isError(result)) {
            LOGGER.log(Level.SEVERE,
                       "Update element's position command failed [result=" + result + "]");
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

    /**
     * Setting dragBounds for the shape doesn't work on lienzo side, so
     * ensure drag does not exceed the canvas bounds.
     * @param shapeView The shape view instance being drag.
     */
    private void ensureDragConstrains(final ShapeView<?> shapeView,
                                      final double[] shapeSize) {
        final int mw = canvasHandler.getCanvas().getWidth();
        final int mh = canvasHandler.getCanvas().getHeight();
        final Point2D sa = shapeView.getShapeAbsoluteLocation();
        LOGGER.log(Level.FINE,
                   "Ensuring drag constraints for absolute coordinates at [" + sa.getX() + ", " + sa.getY() + "]");
        final double ax = mw - shapeSize[0];
        final double ay = mh - shapeSize[1];
        final boolean xb = sa.getX() >= ax || sa.getX() < 0;
        final boolean yb = sa.getY() >= ay || sa.getY() < 0;
        if (xb || yb) {
            final double tx = sa.getX() >= ax ? ax : (sa.getX() < 0 ? 0 : sa.getX());
            final double ty = sa.getY() >= ay ? ay : (sa.getY() < 0 ? 0 : sa.getY());
            LOGGER.log(Level.FINE,
                       "Setting constraint coordinates at [" + tx + ", " + ty + "]");
            shapeView.setShapeX(tx);
            shapeView.setShapeY(ty);
        }
    }

    private boolean isDragGridEnabled() {
        return null != dragGrid;
    }
}
