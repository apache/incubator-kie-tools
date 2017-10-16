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

import java.util.Iterator;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

import static java.util.logging.Level.FINE;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This session commands clear the canvas and internal graph structure.
 * As ClearCanvasCommand does not support undo, it clears the current session's command registry
 * after a successful execution.
 */
@Dependent
public class ClearSessionCommand extends AbstractClientSessionCommand<ClientFullSession> {

    private static Logger LOGGER = Logger.getLogger(ClearSessionCommand.class.getName());

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    protected ClearSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public ClearSessionCommand(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                               final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        super(false);
        this.canvasCommandFactory = canvasCommandFactory;
        this.sessionCommandManager = sessionCommandManager;
    }

    @Override
    public void bind(final ClientFullSession session) {
        super.bind(session);
        checkState();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        final CommandResult<CanvasViolation> result = getSession()
                .getCommandManager()
                .execute(getSession().getCanvasHandler(),
                         canvasCommandFactory.clearCanvas());
        if (!CommandUtils.isError(result)) {
            cleanSessionRegistry();
            callback.onSuccess();
        } else {
            callback.onError((V) result);
        }
    }

    private void cleanSessionRegistry() {
        LOGGER.log(FINE,
                   "Clear Session Command executed - Cleaning the session's command registry...");
        sessionCommandManager.getRegistry().clear();
    }

    protected void onCommandExecuted(final @Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        checkNotNull("commandExecutedEvent",
                     commandExecutedEvent);
        checkState();
    }

    protected void onCommandUndoExecuted(final @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        checkState();
    }

    private void checkState() {
        setEnabled(getState());
        fire();
    }

    @SuppressWarnings("unchecked")
    private boolean getState() {
        boolean doEnable = false;
        final Diagram diagram = null != getSession() ? getSession().getCanvasHandler().getDiagram() : null;
        if (null != diagram) {
            final Graph graph = diagram.getGraph();
            if (null != graph) {
                final String rootUUID = diagram.getMetadata().getCanvasRootUUID();
                Iterable<Node> nodes = graph.nodes();
                final boolean hasNodes = null != nodes && nodes.iterator().hasNext();
                if (hasNodes) {
                    final Iterator<Node> nodesIt = nodes.iterator();
                    final Node node = nodesIt.next();
                    if (nodesIt.hasNext()) {
                        doEnable = true;
                    } else {
                        doEnable = null == rootUUID || !rootUUID.equals(node.getUUID());
                    }
                }
            }
        }
        return doEnable;
    }
}
