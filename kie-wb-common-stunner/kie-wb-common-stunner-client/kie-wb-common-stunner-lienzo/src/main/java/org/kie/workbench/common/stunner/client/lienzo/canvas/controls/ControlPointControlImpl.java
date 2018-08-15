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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.components.drag.DragBoundsEnforcer;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
@Default
public class ControlPointControlImpl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements ControlPointControl<AbstractCanvasHandler>,
                   CanvasControl.SessionAware<EditorSession> {

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final IControlPointsAcceptor cpAcceptor;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;
    public static final int DRAG_BOUNDS_MARGIN = 10;

    @Inject
    public ControlPointControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.cpAcceptor = new StunnerControlPointsAcceptor(this,
                                                           this::getEdge);
    }

    @Override
    protected void doInit() {
        super.doInit();
        getCanvasView().setControlPointsAcceptor(cpAcceptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        final Shape<?> shape = canvasHandler.getCanvas().getShape(element.getUUID());
        if (checkNotRegistered(element) && isConnector(shape) && supportsControlPoints(shape)) {
            //set the connector drag bounds to avoid dragging control points outside canvas
            DragBoundsEnforcer
                    .forShape(shape.getShapeView())
                    .withMargin(DRAG_BOUNDS_MARGIN)
                    .enforce(canvasHandler.getDiagram().getGraph());
        }
    }

    private boolean isConnector(Shape<?> shape) {
        return shape.getShapeView() instanceof IsConnector;
    }

    private boolean supportsControlPoints(Shape<?> shape) {
        return shape.getShapeView() instanceof HasControlPoints;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    public void addControlPoints(Edge candidate, ControlPoint... controlPoint) {
        execute(canvasCommandFactory.addControlPoint(candidate, controlPoint));
    }

    @Override
    public void bind(final EditorSession session) {
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    @Override
    public void moveControlPoints(final Edge candidate,
                                  final Map<ControlPoint, Point2D> pointsLocation) {
        Command<AbstractCanvasHandler, CanvasViolation> command = null;
        if (pointsLocation.size() == 1) {
            final Map.Entry<ControlPoint, Point2D> entry = pointsLocation.entrySet().iterator().next();
            command = newUpdateCPCommand(candidate,
                                         entry.getKey(),
                                         entry.getValue());
        } else if (pointsLocation.size() > 0) {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder =
                    new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>().forward();
            pointsLocation
                    .forEach((cp, location) -> builder.addCommand(newUpdateCPCommand(candidate,
                                                                                     cp,
                                                                                     location)));
            command = builder.build();
        }
        if (null != command) {
            execute(command);
        }
    }

    private CanvasCommand<AbstractCanvasHandler> newUpdateCPCommand(final Edge candidate,
                                                                    final ControlPoint controlPoint,
                                                                    final Point2D location) {
        return canvasCommandFactory.updateControlPointPosition(candidate,
                                                               controlPoint,
                                                               Point2D.clone(location));
    }

    public void removeControlPoint(final Edge candidate,
                                   final ControlPoint controlPoint) {
        if (validateControlPointState(candidate)) {
            execute(canvasCommandFactory.deleteControlPoint(candidate, controlPoint));
        }
    }

    public static class StunnerControlPointsAcceptor implements IControlPointsAcceptor {

        private final ControlPointControl control;
        private final Function<String, Edge> connectorSupplier;

        public StunnerControlPointsAcceptor(final ControlPointControl control,
                                            final Function<String, Edge> connectorSupplier) {
            this.control = control;
            this.connectorSupplier = connectorSupplier;
        }

        @Override
        public boolean add(final WiresConnector connector,
                           final int index,
                           final com.ait.lienzo.client.core.types.Point2D locatoin) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                control.addControlPoints(edge, ControlPoint.build(locatoin.getX(), locatoin.getY(),
                                                                  index - 1));
                return true;
            }
            return false;
        }

        @Override
        public boolean move(final WiresConnector connector,
                            final Point2DArray pointsLocation) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                final ViewConnector viewConnector = (ViewConnector) edge.getContent();
                final List<ControlPoint> controlPoints = viewConnector.getControlPoints();
                final Map<ControlPoint, Point2D> updatedCPs = new HashMap<>(controlPoints.size());
                for (int i = 1; i < (pointsLocation.size() - 1); i++) {
                    final com.ait.lienzo.client.core.types.Point2D targetLocation = pointsLocation.get(i);
                    final ControlPoint controlPoint = controlPoints.get(i - 1);
                    if (!isAt(controlPoint, targetLocation)) {
                        updatedCPs.put(controlPoint,
                                       Point2D.create(targetLocation.getX(),
                                                      targetLocation.getY()));
                    }
                }
                control.moveControlPoints(edge,
                                          updatedCPs);
                return true;
            }
            return false;
        }

        @Override
        public boolean delete(final WiresConnector connector,
                              final int index) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                final Optional<ControlPoint> cp = getControlPointyByIndex(edge,
                                                                          index);
                cp.ifPresent(instance -> control.removeControlPoint(edge, instance));
            }
            return false;
        }

        private Edge getEdge(final WiresConnector connector) {
            return getEdge(getUUID(connector));
        }

        private Edge getEdge(final String uuid) {
            return connectorSupplier.apply(uuid);
        }

        private static boolean isAt(final ControlPoint controlPoint,
                                    final com.ait.lienzo.client.core.types.Point2D location) {
            final Point2D cpLocation = controlPoint.getLocation();
            return cpLocation.getX() == location.getX() && cpLocation.getY() == location.getY();
        }

        private static String getUUID(final WiresConnector connector) {
            return connector instanceof WiresConnectorView ?
                    ((WiresConnectorView) connector).getUUID() :
                    connector.uuid();
        }
    }

    CommandManagerProvider<AbstractCanvasHandler> getCommandManagerProvider() {
        return commandManagerProvider;
    }

    private CommandResult<CanvasViolation> execute(Command<AbstractCanvasHandler, CanvasViolation> command) {
        return getCommandManager().execute(canvasHandler, command);
    }

    private static Optional<ControlPoint> getControlPointyByIndex(final Edge edge,
                                                                  final int index) {
        ViewConnector viewConnector = (ViewConnector) edge.getContent();
        return viewConnector.getControlPoints().stream()
                .filter(cp -> Objects.nonNull(cp.getIndex()))
                .filter(cp -> cp.getIndex() == index - 1)
                .findFirst();
    }

    private static boolean validateControlPointState(final Edge edge) {
        return (Objects.nonNull(edge) && (edge.getContent() instanceof ViewConnector));
    }

    private Edge getEdge(final String uuid) {
        return canvasHandler.getGraphIndex().getEdge(uuid);
    }

    private WiresCanvas.View getCanvasView() {
        return (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        getCanvasView().setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        commandManagerProvider = null;
    }
}