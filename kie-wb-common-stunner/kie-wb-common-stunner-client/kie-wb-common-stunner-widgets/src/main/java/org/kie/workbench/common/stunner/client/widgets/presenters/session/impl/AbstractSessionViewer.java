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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractSessionViewer<S extends AbstractClientSession, H extends AbstractCanvasHandler>
        implements SessionViewer<S, H, Diagram> {

    private S session;

    protected abstract CanvasCommandManager<H> getCommandManager();

    protected abstract DiagramViewer<Diagram, H> getDiagramViewer();

    protected abstract Diagram getDiagram();

    @Override
    public void open(final S item,
                     final SessionViewer.SessionViewerCallback<S, Diagram> callback) {
        doOpen(item,
               null,
               null,
               callback);
    }

    @Override
    public void open(final S item,
                     final int width,
                     final int height,
                     final SessionViewer.SessionViewerCallback<S, Diagram> callback) {
        doOpen(item,
               width,
               height,
               callback);
    }

    @Override
    public void scale(final int width,
                      final int height) {
        if (null != getDiagramViewer()) {
            getDiagramViewer().scale(width,
                                     height);
        }
    }

    @Override
    public void clear() {
        if (null != getDiagramViewer()) {
            getDiagramViewer().clear();
        }
    }

    @Override
    public void destroy() {
        if (null != getDiagramViewer()) {
            getDiagramViewer().destroy();
            session = null;
        }
    }

    @Override
    public S getInstance() {
        return session;
    }

    @Override
    public H getHandler() {
        return null != getDiagramViewer() ? getDiagramViewer().getHandler() : null;
    }

    public AbstractCanvasHandler getSessionHandler() {
        return null != session ? session.getCanvasHandler() : null;
    }

    public AbstractCanvas getCanvas() {
        return null != getHandler() ? (AbstractCanvas) getHandler().getCanvas() : null;
    }

    @Override
    public IsWidget getView() {
        return getDiagramViewer().getView();
    }

    /**
     * Implementation override this method can perform additional operations once opening the diagram viewer, by
     * using a custom <code>DiagramViewer.DiagramViewerCallback</code> instance.
     */
    protected DiagramViewer.DiagramViewerCallback<Diagram> buildCallback(final SessionViewer.SessionViewerCallback<S, Diagram> callback) {
        return callback;
    }

    private void doOpen(final S item,
                        final Integer width,
                        final Integer height,
                        final SessionViewer.SessionViewerCallback<S, Diagram> callback) {
        this.session = item;
        if (null != getDiagram()) {
            beforeOpen();
            final DiagramViewer.DiagramViewerCallback<Diagram> diagramViewerCallback = buildCallback(callback);
            if (null != width && null != height) {
                getDiagramViewer().open(getDiagram(),
                                        width,
                                        height,
                                        diagramViewerCallback);
            } else {
                getDiagramViewer().open(getDiagram(),
                                        diagramViewerCallback);
            }
        } else {
            clear();
        }
    }

    protected void beforeOpen() {
    }

    @SuppressWarnings("unchecked")
    protected void onCommandExecuted(final CanvasCommandExecutedEvent commandExecutedEvent) {
        checkNotNull("commandExecutedEvent",
                     commandExecutedEvent);
        final H context = (H) commandExecutedEvent.getCanvasHandler();
        final Command<H, CanvasViolation> command = commandExecutedEvent.getCommand();
        final CommandResult<CanvasViolation> result = commandExecutedEvent.getResult();
        onExecute(context,
                  command,
                  result);
    }

    @SuppressWarnings("unchecked")
    protected void onCommandUndoExecuted(final CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        final H context = (H) commandUndoExecutedEvent.getCanvasHandler();
        final Command<H, CanvasViolation> command = commandUndoExecutedEvent.getCommand();
        final CommandResult<CanvasViolation> result = commandUndoExecutedEvent.getResult();
        onUndo(context,
               command,
               result);
    }

    private void onExecute(final H context,
                           final Command<H, CanvasViolation> command,
                           final CommandResult<CanvasViolation> result) {
        if (isOperationAllowed(context,
                               result)) {
            getCommandManager().execute(getDiagramViewer().getHandler(),
                                        command);
        }
    }

    private void onUndo(final H context,
                        final Command<H, CanvasViolation> command,
                        final CommandResult<CanvasViolation> result) {
        if (isOperationAllowed(context,
                               result)) {
            getCommandManager().undo(getDiagramViewer().getHandler(),
                                     command);
        }
    }

    private boolean isOperationAllowed(final H sessionHandlerContext,
                                       final CommandResult<CanvasViolation> result) {
        return isSameContext(sessionHandlerContext) && !CommandUtils.isError(result);
    }

    private boolean isSameContext(final H sessionHandlerContext) {
        return null != getSessionHandler() &&
                getSessionHandler().equals(sessionHandlerContext);
    }
}
