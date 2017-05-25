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
import java.util.Optional;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

public abstract class AbstractAcceptorControl
        implements CanvasControl<AbstractCanvasHandler>,
                   RequiresCommandManager<AbstractCanvasHandler> {

    private AbstractCanvasHandler canvasHandler;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    protected abstract void onEnable(final WiresCanvas.View view);

    protected abstract void onDisable(final WiresCanvas.View view);

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        final WiresCanvas.View canvasView = (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
        onEnable(canvasView);
    }

    @Override
    public void disable() {
        if (null != canvasHandler && null != canvasHandler.getCanvas()) {
            final WiresCanvas.View canvasView = (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
            onDisable(canvasView);
        }
        this.canvasHandler = null;
        this.commandManagerProvider = null;
    }

    public boolean isEnabled() {
        return canvasHandler != null;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    protected AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    protected CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    @SuppressWarnings("unchecked")
    protected Optional<Edge<?, Node>> getFirstIncomingEdge(final Node child,
                                                           final Predicate<Edge> predicate) {
        return getAnyEdge(child.getInEdges(),
                          predicate);
    }

    @SuppressWarnings("unchecked")
    protected Optional<Edge<?, Node>> getFirstOutgoingEdge(final Node child,
                                                           final Predicate<Edge> predicate) {
        return getAnyEdge(child.getOutEdges(),
                          predicate);
    }

    protected boolean isCommandSuccess(final Node candidate,
                                       final CommandResult<CanvasViolation> result) {
        return !CommandUtils.isError(result);
    }

    protected boolean isWiresViewAccept(final WiresContainer wiresContainer,
                                        final WiresShape wiresShape) {
        if (!isEnabled() || !WiresUtils.isWiresShape(wiresContainer) || !WiresUtils.isWiresShape(wiresShape)) {
            return false;
        }
        return true;
    }

    protected Optional<Edge<?, Node>> getAnyEdge(final List<Edge<?, Node>> edges,
                                                 final Predicate<Edge> predicate) {
        if (null != edges) {
            return edges.stream()
                    .filter(predicate)
                    .findAny();
        }
        return Optional.empty();
    }
}
