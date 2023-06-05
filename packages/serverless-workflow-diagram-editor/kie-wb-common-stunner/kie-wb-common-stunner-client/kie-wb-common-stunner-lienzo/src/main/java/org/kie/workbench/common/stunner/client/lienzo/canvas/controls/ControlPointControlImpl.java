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

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

@Dependent
@Default
public class ControlPointControlImpl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements ControlPointControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(ControlPointControlImpl.class.getName());

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final StunnerControlPointsAcceptor cpAcceptor;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Inject
    public ControlPointControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                   final Event<CanvasSelectionEvent> selectionEvent) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.cpAcceptor = new StunnerControlPointsAcceptor(this,
                                                           this::getEdge);
        this.selectionEvent = selectionEvent;
    }

    @Override
    protected void doInit() {
        super.doInit();
        getWiresManager().setControlPointsAcceptor(cpAcceptor);
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        getWiresManager().setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        commandManagerProvider = null;
    }

    @Override
    public void register(final Element element) {
        // this gets called on authoring
        final String uuid = element.getUUID();
        final Shape shape = canvasHandler.getCanvas().getShape(uuid);

        if (shape.getShapeView() instanceof WiresConnector) {
            final WiresConnector shapeViewConnector = (WiresConnector) shape.getShapeView();
            getWiresManager().addHandlers(shapeViewConnector);
        }
    }

    @Override
    public void addControlPoint(final Edge candidate,
                                final ControlPoint controlPoint,
                                final int index) {
        selectionEvent.fire(new CanvasSelectionEvent(canvasHandler, candidate.getUUID()));
        execute(canvasCommandFactory.addControlPoint(candidate, controlPoint, index));
    }

    @Override
    public void updateControlPoints(final Edge candidate,
                                    final ControlPoint[] controlPoints) {
        execute(canvasCommandFactory.updateControlPointPosition(candidate, controlPoints));
    }

    @Override
    public void deleteControlPoint(final Edge candidate,
                                   final int index) {
        execute(canvasCommandFactory.deleteControlPoint(candidate, index));
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    public static class StunnerControlPointsAcceptor implements IControlPointsAcceptor {

        private final ControlPointControl control;
        private final Function<String, Edge> connectorSupplier;

        private HandlerRegistration handlerRegistration;
        private WiresConnector connector;
        private int index;
        private com.ait.lienzo.client.core.types.Point2D location;

        public StunnerControlPointsAcceptor(final ControlPointControl control,
                                            final Function<String, Edge> connectorSupplier) {
            this.control = control;
            this.connectorSupplier = connectorSupplier;
        }

        @Override
        public boolean add(final WiresConnector connector,
                           final int index,
                           final com.ait.lienzo.client.core.types.Point2D location) {
            resetAddingOp();
            proposeAdd(connector, index, location);
            return true;
        }

        @Override
        public boolean move(final WiresConnector connector,
                            final Point2DArray pointsLocation) {
            completeAddOrMove(connector, pointsLocation);
            resetAddingOp();
            return true;
        }

        @Override
        public boolean delete(final WiresConnector connector,
                              final int index) {
            deleteAtIndex(connector, index);
            resetAddingOp();
            return true;
        }

        /**
         * Instead of executing a command once just adding a new CP, let's
         * delay the command execution until mouse is up, as it may be that the new CP
         * is also being dragged, so moved to a different target location.
         * The goal is to fire the execution of a single command, even if the new CP is also
         * being dragged (so the IControlPointsAcceptor#move() called as well).
         */
        private void proposeAdd(final WiresConnector connector,
                                final int index,
                                final com.ait.lienzo.client.core.types.Point2D location) {
            this.connector = connector;
            this.index = index;
            this.location = location.copy();
            this.handlerRegistration = connector.getGroup().getLayer().addNodeMouseUpHandler(event -> performAdd());
            connector.addControlPoint(location.getX(), location.getY(), index);
        }

        private void completeAddOrMove(final WiresConnector connector,
                                       final Point2DArray pointsLocation) {
            if (null != this.connector && this.connector.equals(connector)) {
                location = pointsLocation.get(index).copy();
                performAdd();
            } else {
                final int size = pointsLocation.size() - 2;
                final Edge edge = getEdge(connector);
                final ControlPoint[] controlPoints = new ControlPoint[size];
                for (int i = 1; i <= size; i++) {
                    com.ait.lienzo.client.core.types.Point2D point = pointsLocation.get(i);
                    controlPoints[i - 1] = ControlPoint.create(Point2D.create(point.getX(),
                                                                             point.getY()));
                }
                control.updateControlPoints(edge,
                                            controlPoints);
            }
        }

        private void performAdd() {
            if (null != connector) {
                connector.destroyControlPoints(new int[]{index});
                final Edge edge = getEdge(connector);
                control.addControlPoint(edge,
                                        ControlPoint.build(location.getX(), location.getY()),
                                        index - 1);
                resetAddingOp();
            }
        }

        private void deleteAtIndex(final WiresConnector connector,
                                   final int index) {
            final Edge edge = getEdge(connector);
            control.deleteControlPoint(edge, index - 1);
        }

        private void resetAddingOp() {
            connector = null;
            index = -1;
            location = null;
            if (null != handlerRegistration) {
                handlerRegistration.removeHandler();
            }
        }

        private Edge getEdge(final WiresConnector connector) {
            return getEdge(getUUID(connector));
        }

        private Edge getEdge(final String uuid) {
            return connectorSupplier.apply(uuid);
        }

        private static String getUUID(final WiresConnector connector) {
            return connector instanceof WiresConnectorView ?
                    ((WiresConnectorView) connector).getUUID() :
                    connector.uuid();
        }
    }

    private void execute(final CanvasCommand<AbstractCanvasHandler> command) {
        final CommandResult<CanvasViolation> result = getCommandManager().execute(canvasHandler, command);
        if (CommandUtils.isError(result)) {
            LOGGER.log(Level.SEVERE,
                       "Cannot execute command " +
                               "[command= " + command + ", " +
                               "result=" + result + "]");
        }
    }

    private Edge getEdge(final String uuid) {
        return canvasHandler.getGraphIndex().getEdge(uuid);
    }

    private WiresManager getWiresManager() {
        return ((WiresCanvas) canvasHandler.getCanvas()).getWiresManager();
    }
}