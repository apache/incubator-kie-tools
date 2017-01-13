/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.session.presenter.impl;

import javax.enterprise.event.Event;

import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.ClientSessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLoadingObserver;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.mvp.Command;

public abstract class AbstractClientSessionPresenter<S extends AbstractClientSession, V extends ClientSessionPresenter.View>
        implements ClientSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S, V> {

    private final AbstractClientSessionManager clientSessionManager;
    private final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent;
    private final V view;
    private S session;
    private boolean displayNotifications;
    private boolean displayErrors;

    public AbstractClientSessionPresenter(final AbstractClientSessionManager clientSessionManager,
                                          final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                                          final V view) {
        this.clientSessionManager = clientSessionManager;
        this.sessionDiagramOpenedEvent = sessionDiagramOpenedEvent;
        this.view = view;
        this.displayErrors = false;
        this.displayNotifications = false;
    }

    protected abstract void doDisposeSession();

    protected abstract void doPauseSession();

    @Override
    @SuppressWarnings("unchecked")
    public ClientSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S, V> initialize(final S session,
                                                                                          final int width,
                                                                                          final int height) {
        this.session = session;
        // Create the canvas with a given size.
        session.getCanvas().initialize(width,
                                       height);
        // Initialize the canvas to handle.
        getCanvasHandler().initialize(session.getCanvas());
        // Initialize the view.
        view.setCanvas(session.getCanvas().getView().asWidget());

        // Enable canvas loading callback.
        session.getCanvas().setLoadingObserverCallback(new CanvasLoadingObserver.Callback() {

            @Override
            public void onLoadingStarted() {
                fireProcessingStarted();
            }

            @Override
            public void onLoadingCompleted() {
                fireProcessingCompleted();
            }
        });
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S, V> open(final Diagram diagram,
                                                                                    final Command callback) {
        // Notify processing starts.
        fireProcessingStarted();
        // Open the session & Draw the graph on the canvas.
        clientSessionManager.open(session);
        getCanvasHandler().draw(diagram);
        // Callback.
        callback.execute();
        // Notify processing ends.
        fireProcessingCompleted();
        sessionDiagramOpenedEvent.fire(new SessionDiagramOpenedEvent(session));
        return this;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return session.getCanvasHandler();
    }

    @Override
    public ClientSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S, V> setDisplayErrors(final boolean showErrors) {
        this.displayErrors = showErrors;
        return this;
    }

    @Override
    public ClientSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S, V> setDisplayNotifications(final boolean showNotifications) {
        this.displayNotifications = showNotifications;
        return this;
    }

    public void disposeSession() {
        // Implementations can clear its state here.
        doDisposeSession();
        // Destroy the view.
        this.view.destroy();
        // Nullify
        this.session = null;
    }

    public void pauseSession() {
        doPauseSession();
    }

    public V getView() {
        return view;
    }

    protected boolean isDisplayNotifications() {
        return displayNotifications;
    }

    protected boolean isDisplayErrors() {
        return displayErrors;
    }

    protected S getSession() {
        return session;
    }

    private void fireProcessingStarted() {
        view.setLoading(true);
    }

    private void fireProcessingCompleted() {
        view.setLoading(false);
    }
}
