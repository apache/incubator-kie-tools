/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragEndEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPointImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Default
public class ControlPointControlImpl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements ControlPointControl<AbstractCanvasHandler>,
                   CanvasControl.SessionAware<EditorSession> {

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;
    private Edge selectedEdge;
    private ControlPoint selectedControlPoint;

    @Inject
    public ControlPointControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (checkNotRegistered(element)) {
            final Shape<?> shape = getCanvas().getShape(element.getUUID());
            if (supportsControlPoints(shape)) {
                registerHandlers(element, shape);
            }
        }
    }

    private Canvas getCanvas() {
        return canvasHandler.getCanvas();
    }

    private boolean supportsControlPoints(Shape<?> shape) {
        return shape.getShapeView() instanceof HasControlPoints;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    /**
     * For testing purposes
     */
    CommandManagerProvider<AbstractCanvasHandler> getCommandManagerProvider() {
        return commandManagerProvider;
    }

    @Override
    public void addControlPoint(Edge candidate, ControlPoint... controlPoint) {
        checkAndExecuteCommand(canvasCommandFactory.addControlPoint(candidate, controlPoint));
    }

    @Override
    public void bind(final EditorSession session) {
    }

    @SuppressWarnings("unchecked")
    private void registerHandlers(final Element element,
                                  final Shape<?> shape) {
        if (shape.getShapeView() instanceof HasEventHandlers
                && element instanceof Edge
                && element.getContent() instanceof ViewConnector) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();

            //Register handler on the Connector to Add a ControlPoint
            MouseDoubleClickHandler eventHandler = new MouseDoubleClickHandler() {
                @Override
                public void handle(MouseDoubleClickEvent event) {
                    addControlPoint((Edge) element, new ControlPointImpl(event.getX(), event.getY()));
                }
            };
            hasEventHandlers.addHandler(ViewEventType.MOUSE_DBL_CLICK, eventHandler);
            registerHandler(element.getUUID(), eventHandler);
        }
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    private boolean validateEvent(AbstractCanvasHandlerEvent event) {
        checkNotNull("event", event);
        return Objects.equals(canvasHandler, event.getCanvasHandler());
    }

    protected void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        if (!validateEvent(event)) {
            return;
        }

        if (event.getIdentifiers().size() == 1) {
            final String uuid = event.getIdentifiers().iterator().next();
            Optional.ofNullable(canvasHandler.getGraphIndex().getEdge(uuid)).ifPresent(edge -> {
                this.selectedEdge = edge;
                if (!isRegistered(edge.getUUID())) {
                    register(edge);
                }
            });
        }
    }

    protected void onCanvasClearSelectionEvent(@Observes CanvasClearSelectionEvent event) {
        if (!validateEvent(event)) {
            return;
        }
        clear();
    }

    protected void onControlPointDragStartEvent(@Observes CanvasControlPointDragStartEvent event) {
        if (!validateControlPointState()) {
            return;
        }
        selectedControlPoint = getControlPointyByPosition(event.getPosition());
    }

    private ControlPoint getControlPointyByPosition(Point2D position) {
        ViewConnector viewConnector = (ViewConnector) selectedEdge.getContent();
        return viewConnector.getControlPoints().stream()
                .filter(cp -> Objects.equals(cp.getLocation(), position))
                .findFirst()
                .orElse(null);
    }

    protected void onCanvasControlPointDragEndEvent(@Observes CanvasControlPointDragEndEvent event) {
        if (!validateControlPointState() || Objects.isNull(selectedControlPoint)) {
            return;
        }
        moveControlPoint(selectedControlPoint, event.getPosition());
    }

    protected void onCanvasControlPointDoubleClickEvent(@Observes CanvasControlPointDoubleClickEvent event) {
        if (!validateControlPointState()) {
            return;
        }
        this.selectedControlPoint = getControlPointyByPosition(event.getPosition());
        removeControlPoint(this.selectedControlPoint);
    }

    public void removeControlPoint(ControlPoint controlPoint) {
        validateControlPointState();
        checkAndExecuteCommand(canvasCommandFactory.deleteControlPoint(selectedEdge, controlPoint));
    }

    private CommandResult<CanvasViolation> checkAndExecuteCommand(CanvasCommand<AbstractCanvasHandler> command) {
        CommandResult<CanvasViolation> allowResult = getCommandManager().allow(canvasHandler, command);
        if (CommandUtils.isError(allowResult)) {
            return allowResult;
        }
        return getCommandManager().execute(canvasHandler, command);
    }

    private boolean validateControlPointState() {
        return (Objects.nonNull(this.selectedEdge) && (selectedEdge.getContent() instanceof ViewConnector));
    }

    @Override
    public void moveControlPoint(ControlPoint controlPoint, Point2D position) {
        validateControlPointState();
        if (Objects.isNull(controlPoint)) {
            throw new IllegalStateException("ControlPoint is null.");
        }
        checkAndExecuteCommand(canvasCommandFactory.updateControlPointPosition(selectedEdge, controlPoint, position));
    }

    protected Edge getSelectedEdge() {
        return selectedEdge;
    }

    protected ControlPoint getSelectedControlPoint() {
        return selectedControlPoint;
    }

    @Override
    protected void doClear() {
        super.doClear();
        this.selectedEdge = null;
        this.selectedControlPoint = null;
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        clear();
        commandManagerProvider = null;
    }
}