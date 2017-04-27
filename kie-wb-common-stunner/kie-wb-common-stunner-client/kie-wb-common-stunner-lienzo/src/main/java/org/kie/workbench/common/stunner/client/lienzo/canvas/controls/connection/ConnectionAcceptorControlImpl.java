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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.connection;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Dependent
public class ConnectionAcceptorControlImpl implements ConnectionAcceptorControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(ConnectionAcceptorControlImpl.class.getName());

    CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private CommandManagerProvider<AbstractCanvasHandler> provider;

    private AbstractCanvasHandler canvasHandler;
    private CanvasHighlight canvasHighlight;

    @Inject
    public ConnectionAcceptorControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        this.canvasHighlight = new CanvasHighlight(canvasHandler);
        final WiresCanvas.View canvasView = (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
        canvasView.setConnectionAcceptor(CONNECTION_ACCEPTOR);
    }

    @Override
    public void disable() {
        if (null != canvasHandler && null != canvasHandler.getCanvas()) {
            final WiresCanvas.View canvasView = (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
            canvasView.setConnectionAcceptor(IConnectionAcceptor.NONE);
        }
        this.canvasHighlight.destroy();
        this.canvasHighlight = null;
        this.canvasHandler = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean allowSource(final Node source,
                               final Edge<View<?>, Node> connector,
                               final int magnet) {
        if (null == canvasHandler) {
            return false;
        }
        final boolean eq = eq(source,
                              connector.getSourceNode());
        if (!eq) {
            final CommandResult<CanvasViolation> violations = getCommandManager().allow(canvasHandler,
                                                                                        canvasCommandFactory.setSourceNode(source,
                                                                                                                           connector,
                                                                                                                           magnet));
            final boolean accepts = isAccept(violations);
            highlight(source,
                      connector,
                      accepts);
            return accepts;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean allowTarget(final Node target,
                               final Edge<View<?>, Node> connector,
                               final int magnet) {
        if (null == canvasHandler) {
            return false;
        }
        final boolean eq = eq(target,
                              connector.getTargetNode());
        if (!eq) {
            final CommandResult<CanvasViolation> violations = getCommandManager().allow(canvasHandler,
                                                                                        canvasCommandFactory.setTargetNode(target,
                                                                                                                           connector,
                                                                                                                           magnet));
            final boolean accepts = isAccept(violations);
            highlight(target,
                      connector,
                      accepts);
            return accepts;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean acceptSource(final Node source,
                                final Edge<View<?>, Node> connector,
                                final int magnet) {
        if (null == canvasHandler) {
            return false;
        }
        final boolean eq = eq(source,
                              connector.getSourceNode());
        if (!eq) {
            ensureUnHighLight();
            final CommandResult<CanvasViolation> violations = getCommandManager().execute(canvasHandler,
                                                                                          canvasCommandFactory.setSourceNode(source,
                                                                                                                             connector,
                                                                                                                             magnet));
            return isAccept(violations);
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean acceptTarget(final Node target,
                                final Edge<View<?>, Node> connector,
                                final int magnet) {
        if (null == canvasHandler) {
            return false;
        }
        final boolean eq = eq(target,
                              connector.getTargetNode());
        if (!eq) {
            ensureUnHighLight();
            final CommandResult<CanvasViolation> violations = getCommandManager().execute(canvasHandler,
                                                                                          canvasCommandFactory.setTargetNode(target,
                                                                                                                             connector,
                                                                                                                             magnet));
            return isAccept(violations);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static boolean eq(final Node n1,
                              final Node n2) {
        if (n1 == null && n2 == null) {
            return true;
        }
        return null != n1 && n1.equals(n2);
    }

    private final IConnectionAcceptor CONNECTION_ACCEPTOR = new IConnectionAcceptor() {

        // Set the source Node for the connector.
        @Override
        @SuppressWarnings("unchecked")
        public boolean acceptHead(final WiresConnection head,
                                  final WiresMagnet magnet) {
            log(Level.FINE,
                "## Accept Head ##");
            final Edge edge = WiresUtils.getEdge(canvasHandler,
                                                 head.getConnector());
            final Node sourceNode = WiresUtils.getNode(canvasHandler,
                                                       magnet);
            final int mIndex = getMagnetIndex(magnet);
            final String sourceUUID = sourceNode != null ? sourceNode.getUUID() : null;
            final String message = "Executed SetConnectionSourceNodeCommand [source=" + sourceUUID + ", magnet=" + mIndex + "]";
            log(Level.FINE,
                message);
            return acceptSource(sourceNode,
                                edge,
                                mIndex);
        }

        // Set the target Node for the connector.
        @Override
        @SuppressWarnings("unchecked")
        public boolean acceptTail(final WiresConnection tail,
                                  final WiresMagnet magnet) {
            log(Level.FINE,
                "## Accept tail ##");
            final WiresConnection head = tail.getConnector().getHeadConnection();
            final Edge edge = WiresUtils.getEdge(canvasHandler,
                                                 head.getConnector());
            final Node targetNode = WiresUtils.getNode(canvasHandler,
                                                       magnet);
            final int mIndex = getMagnetIndex(magnet);
            final String targetUUID = targetNode != null ? targetNode.getUUID() : null;
            final String message = "Executed SetConnectionTargetNodeCommand [target=" + targetUUID + ", magnet=" + mIndex + "]";
            log(Level.FINE,
                message);
            return acceptTarget(targetNode,
                                edge,
                                mIndex);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean headConnectionAllowed(final WiresConnection head,
                                             final WiresShape shape) {
            log(Level.FINE,
                "## Allow Head ##");
            final Edge<View<?>, Node> edge = WiresUtils.getEdge(canvasHandler,
                                                                head.getConnector());
            final Node sourceNode = WiresUtils.getNode(canvasHandler,
                                                       shape);
            final boolean b = allowSource(sourceNode,
                                          edge,
                                          0);
            final String nUUID = null != sourceNode ? sourceNode.getUUID() : "null";
            log(Level.FINE,
                "  Is head allowed [" + nUUID + "] = " + b);
            return b;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean tailConnectionAllowed(final WiresConnection tail,
                                             final WiresShape shape) {
            log(Level.FINE,
                "## Allow tail ##");
            final Edge<View<?>, Node> edge = WiresUtils.getEdge(canvasHandler,
                                                                tail.getConnector());
            final Node targetNode = WiresUtils.getNode(canvasHandler,
                                                       shape);
            final boolean b = allowTarget(targetNode,
                                          edge,
                                          0);
            final String nUUID = null != targetNode ? targetNode.getUUID() : "null";
            log(Level.FINE,
                "  Is tail allowed [" + nUUID + "] = " + b);
            return b;
        }

        private int getMagnetIndex(final WiresMagnet magnet) {
            if (null != magnet) {
                MagnetManager.Magnets magnets = magnet.getMagnets();
                for (int x = 0; x < magnets.size(); x++) {
                    WiresMagnet _m = magnets.getMagnet(x);
                    if (_m.equals(magnet)) {
                        return x;
                    }
                }
            }
            return -1;
        }
    };

    private boolean isAccept(final CommandResult<CanvasViolation> result) {
        return !CommandUtils.isError(result);
    }

    private void highlight(final Node node,
                           final Edge<View<?>, Node> connector,
                           final boolean valid) {
        canvasHighlight.unhighLight();
        if (null != node && valid) {
            canvasHighlight.highLight(node);
        } else if (null != node) {
            canvasHighlight.invalid(node);
        }
    }

    private void ensureUnHighLight() {
        if (null != canvasHighlight) {
            canvasHighlight.unhighLight();
        }
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.provider = provider;
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return provider.getCommandManager();
    }
}
