/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.components.proxies;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.gwtproject.timer.client.Timer;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.fireElementSelectedEvent;

@Dependent
public class ElementProxy implements ShapeProxy {

    private final SessionCommandManager<AbstractCanvasHandler> commandManager;
    private final Event<CanvasSelectionEvent> selectionEvent;
    private final ManagedInstance<DefaultCanvasCommandFactory> commandFactories;
    private final DefinitionUtils definitionUtils;
    private SessionManager sessionManager;

    private AbstractCanvasHandler canvasHandler;
    private ShapeProxyView<ElementShape> view;
    private Supplier<ElementShape> proxyBuilder;

    @Inject
    public ElementProxy(final SessionCommandManager<AbstractCanvasHandler> commandManager,
                        final Event<CanvasSelectionEvent> selectionEvent,
                        final @Any ManagedInstance<DefaultCanvasCommandFactory> commandFactories,
                        final DefinitionUtils definitionUtils,
                        final SessionManager sessionManager) {
        this.commandManager = commandManager;
        this.selectionEvent = selectionEvent;
        this.commandFactories = commandFactories;
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
    }

    @SuppressWarnings("unchecked")
    public ElementProxy setView(final ShapeProxyView<? extends ElementShape> view) {
        this.view = (ShapeProxyView<ElementShape>) view;
        return this;
    }

    public ElementProxy setProxyBuilder(final Supplier<ElementShape> proxyBuilder) {
        this.proxyBuilder = proxyBuilder;
        return this;
    }

    public ElementProxy setCanvasHandler(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        return this;
    }

    @Override
    public void start(final double x,
                      final double y) {
        view
                .onCreate(this::createProxy)
                .onAccept(this::acceptProxy)
                .onDestroy(this::destroyProxy)
                .setCanvas(getCanvas())
                .start(x, y);
    }

    @Override
    public void destroy() {
        if (null != view) {
            view.destroy();
        }
        commandFactories.destroyAll();
        canvasHandler = null;
        view = null;
        proxyBuilder = null;
    }

    public CommandResult<CanvasViolation> execute(final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return commandManager.execute(canvasHandler, command);
    }

    void handleCancelKey() {
        ((EditorSession)(sessionManager.getCurrentSession()))
                .getKeyboardControl()
                .addKeyShortcutCallback(
                        new KeyboardControl.KogitoKeyPress(
                                new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC},
                                "Unselect",
                                this::destroy
                        ));
    }

    public Canvas getCanvas() {
        return canvasHandler.getCanvas();
    }

    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    private ElementShape createProxy() {
        commandManager.start();
        ElementShape instance = proxyBuilder.get();
        instance.applyState(ShapeState.SELECTED);
        return instance;
    }

    private void acceptProxy(final ElementShape shape) {
        commandManager.complete();
        select(shape.getUUID());
    }

    void select(final String uuid) {
        final Timer t = new Timer() {
            @Override
            public void run() {
                fireElementSelectedEvent(selectionEvent,
                                         canvasHandler,
                                         uuid);
            }
        };
        t.schedule(150);
    }

    private void destroyProxy(final ElementShape shape) {
        commandManager.rollback();
        commandManager.complete();
    }

    CanvasCommandFactory<AbstractCanvasHandler> lookupCanvasFactory() {
        final Diagram diagram = getCanvasHandler().getDiagram();
        final String id = diagram.getMetadata().getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(id);
        return InstanceUtils.lookup(commandFactories, qualifier);
    }
}
