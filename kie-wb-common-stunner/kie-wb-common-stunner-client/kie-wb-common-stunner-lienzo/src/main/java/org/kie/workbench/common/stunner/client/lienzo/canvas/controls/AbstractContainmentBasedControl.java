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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

public abstract class AbstractContainmentBasedControl
        implements CanvasControl<AbstractCanvasHandler>,
                   RequiresCommandManager<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(AbstractContainmentBasedControl.class.getName());

    private AbstractCanvasHandler canvasHandler;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    protected abstract void doEnable(final WiresCanvas.View view);

    protected abstract void doDisable(final WiresCanvas.View view);

    protected abstract boolean isEdgeAccepted(final Edge edge);

    protected abstract Command<AbstractCanvasHandler, CanvasViolation> getAddEdgeCommand(final Node parent,
                                                                                         final Node child);

    protected abstract Command<AbstractCanvasHandler, CanvasViolation> getDeleteEdgeCommand(final Node parent,
                                                                                            final Node child);

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        final WiresCanvas.View canvasView = (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
        doEnable(canvasView);
    }

    @Override
    public void disable() {
        if (null != canvasHandler && null != canvasHandler.getCanvas()) {
            final WiresCanvas.View canvasView = (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
            doDisable(canvasView);
        }
        this.canvasHandler = null;
        this.commandManagerProvider = null;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @SuppressWarnings("unchecked")
    public boolean allow(final Node parent,
                         final Node child) {
        if (parent == null && child == null) {
            return false;
        }
        boolean isAllow = true;
        final Edge edge = getTheEdge(child);
        final boolean isSameParent = isSameParent(parent,
                                                  edge);
        if (!isSameParent) {
            final CommandResult<CanvasViolation> violations = runAllow(parent,
                                                                       child,
                                                                       edge);
            isAllow = isAccept(child,
                               violations);
        }
        return isAllow;
    }

    @SuppressWarnings("unchecked")
    public boolean accept(final Node parent,
                          final Node child) {
        if (parent == null && child == null) {
            return false;
        }
        final Edge edge = getTheEdge(child);
        final boolean isSameParent = isSameParent(parent,
                                                  edge);
        boolean isAccept = true;
        if (!isSameParent) {
            final CommandResult<CanvasViolation> violations = runAccept(parent,
                                                                        child,
                                                                        edge);
            isAccept = isAccept(child,
                                violations);
        }
        return isAccept;
    }

    protected boolean isAccept(final WiresContainer wiresContainer,
                               final WiresShape wiresShape) {
        if (!isEnabled() || !WiresUtils.isWiresShape(wiresContainer) || !WiresUtils.isWiresShape(wiresShape)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<CanvasViolation> runAllow(final Node parent,
                                                      final Node child,
                                                      final Edge edge) {
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> command =
                buildCommands(parent,
                              child,
                              edge);
        return getCommandManager().allow(canvasHandler,
                                         command);
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<CanvasViolation> runAccept(final Node parent,
                                                       final Node child,
                                                       final Edge edge) {
        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> command =
                buildCommands(parent,
                              child,
                              edge);
        return getCommandManager().execute(canvasHandler,
                                           command);
    }

    protected AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    protected CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    private CompositeCommand<AbstractCanvasHandler, CanvasViolation> buildCommands(final Node parent,
                                                                                   final Node child,
                                                                                   final Edge edge) {
        // Remove current relationship, if any.
        final boolean hasSourceNode = null != edge && null != edge.getSourceNode();
        final Command<AbstractCanvasHandler, CanvasViolation> deleteEdgeCommand =
                hasSourceNode ?
                        getDeleteEdgeCommand(edge.getSourceNode(),
                                             child) :
                        null;
        // Add a new relationship, if any.
        final boolean hasNewTarget = null != parent;
        final Command<AbstractCanvasHandler, CanvasViolation> addEdgeCommand =
                hasNewTarget ?
                        getAddEdgeCommand(parent,
                                          child) :
                        null;
        final CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> commandBuilder =
                new CompositeCommandImpl
                        .CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                        .reverse();
        if (null != deleteEdgeCommand) {
            commandBuilder.addCommand(deleteEdgeCommand);
        }
        if (null != addEdgeCommand) {
            commandBuilder.addCommand(addEdgeCommand);
        }
        return commandBuilder.build();
    }

    private boolean isEnabled() {
        return canvasHandler != null;
    }

    private boolean isSameParent(final Node parent,
                                 final Edge<Child, Node> edge) {
        if (null != edge) {
            final Node sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                final String parentUUID = null != parent ? parent.getUUID() : canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
                return null != parentUUID && sourceNode.getUUID().equals(parentUUID);
            }
        }
        return parent == null;
    }

    @SuppressWarnings("unchecked")
    private Edge<Object, Node> getTheEdge(final Node child) {
        if (child != null) {
            final List<Edge> outEdges = child.getInEdges();
            if (null != outEdges && !outEdges.isEmpty()) {
                for (final Edge edge : outEdges) {
                    if (isEdgeAccepted(edge)) {
                        return edge;
                    }
                }
            }
        }
        return null;
    }

    private boolean isAccept(final Node candidate,
                             final CommandResult<CanvasViolation> result) {
        return !CommandUtils.isError(result);
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
