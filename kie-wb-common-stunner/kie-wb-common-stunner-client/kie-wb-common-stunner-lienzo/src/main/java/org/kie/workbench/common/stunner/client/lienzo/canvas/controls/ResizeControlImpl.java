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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Default
public class ResizeControlImpl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler> implements ResizeControl<AbstractCanvasHandler, Element> {

    private static Logger LOGGER = Logger.getLogger(ResizeControlImpl.class.getName());

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    protected ResizeControlImpl() {
        this(null);
    }

    @Inject
    public ResizeControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (checkNotRegistered(element)) {
            final Canvas<?> canvas = canvasHandler.getCanvas();
            final Shape<?> shape = canvas.getShape(element.getUUID());
            if (supportsResize(shape)) {
                registerResizeHandlers(element,
                                       shape);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> resize(final Element element,
                                                 final double width,
                                                 final double height) {
        return doResize(element,
                        width,
                        height);
    }

    @Override
    public void setCommandManagerProvider(final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        this.commandManagerProvider = null;
    }

    /**
     * To enable the resize control next bullets must be met:
     * - shape view must support resize event - for capturing user resize events
     * - shape view must support mouse click event - for enabling resize control points on mouse click
     * - shape view must support control points as well
     */
    private boolean supportsResize(final Shape<?> shape) {
        final ShapeView<?> view = shape.getShapeView();
        final boolean supportsResize = (view instanceof HasEventHandlers) &&
                ((HasEventHandlers) view).supports(ViewEventType.RESIZE);
        final boolean supportsCtrlPoints = (view instanceof HasControlPoints);
        return supportsResize && supportsCtrlPoints;
    }

    @SuppressWarnings("unchecked")
    private void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (event.getIdentifiers().size() == 1) {
            final String uuid = event.getIdentifiers().iterator().next();
            if (isSameCanvas(event) && isRegistered(uuid)) {
                hideALLCPs();
                final HasControlPoints<?> hasControlPoints = getControlPointsInstance(uuid);
                if (!hasControlPoints.areControlsVisible()) {
                    showCPs(hasControlPoints);
                }
            }
        }
    }

    private void CanvasClearSelectionEvent(@Observes CanvasClearSelectionEvent clearSelectionEvent) {
        checkNotNull("clearSelectionEvent",
                     clearSelectionEvent);
        if (isSameCanvas(clearSelectionEvent)) {
            hideALLCPs();
        }
    }

    private void showCPs(final HasControlPoints<?> hasControlPoints) {
        if (!hasControlPoints.areControlsVisible()) {
            hasControlPoints.showControlPoints(HasControlPoints.ControlPointType.RESIZE);
        }
    }

    private void hideALLCPs() {
        getRegisteredElements().forEach(uuid -> getControlPointsInstance(uuid).hideControlPoints());
    }

    private void hideCPs(final HasControlPoints<?> hasControlPoints) {
        if (hasControlPoints.areControlsVisible()) {
            hasControlPoints.hideControlPoints();
        }
    }

    private HasControlPoints<?> getControlPointsInstance(final String uuid) {
        final Shape<?> shape = canvasHandler.getCanvas().getShape(uuid);
        return (HasControlPoints<?>) shape.getShapeView();
    }

    private boolean isSameCanvas(final AbstractCanvasHandlerEvent event) {
        return null != canvasHandler && canvasHandler.equals(event.getCanvasHandler());
    }

    @SuppressWarnings("unchecked")
    private void registerResizeHandlers(final Element element,
                                        final Shape<?> shape) {
        if (shape.getShapeView() instanceof HasEventHandlers) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            final ResizeHandler resizeHandler = new ResizeHandler() {
                @Override
                public void start(final ResizeEvent event) {
                }

                @Override
                public void handle(final ResizeEvent event) {
                }

                @Override
                public void end(final ResizeEvent event) {
                    final CommandResult<CanvasViolation> result =
                            doResize(element,
                                     event.getWidth(),
                                     event.getHeight());
                    if (CommandUtils.isError(result)) {
                        LOGGER.log(Level.SEVERE,
                                   "Command failed at resize end [result=" + result + "]");
                    }
                }
            };
            hasEventHandlers.addHandler(ViewEventType.RESIZE,
                                        resizeHandler);
            registerHandler(element.getUUID(),
                            resizeHandler);
        }
    }

    @SuppressWarnings("unchecked")
    private CommandResult<CanvasViolation> doResize(final Element<? extends View<?>> element,
                                                    final double w,
                                                    final double h) {
        return getCommandManager().execute(canvasHandler,
                                           canvasCommandFactory.resize(element, new BoundingBox(0, 0, w, h)));
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }
}
