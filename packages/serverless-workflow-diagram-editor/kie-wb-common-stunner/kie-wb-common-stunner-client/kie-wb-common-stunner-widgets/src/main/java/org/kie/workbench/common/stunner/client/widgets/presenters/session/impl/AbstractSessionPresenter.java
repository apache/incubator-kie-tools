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


package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.event.Event;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.CommandNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationContext;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.notification.ValidationFailedNotification;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

public abstract class AbstractSessionPresenter<D extends Diagram, H extends AbstractCanvasHandler,
        S extends AbstractSession, E extends SessionViewer<S, H, D>>
        implements SessionPresenter<S, H, D> {

    private static final Logger LOGGER = Logger.getLogger(AbstractSessionPresenter.class.getName());

    private final SessionManager sessionManager;
    private final SessionPresenter.View view;
    private final NotificationsObserver notificationsObserver;
    private final Event<SessionFocusedEvent> sessionFocusedEvent;
    private final Event<SessionLostFocusEvent> sessionLostFocusEvent;
    private final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent;

    private D diagram;
    private Optional<Predicate<Notification.Type>> typePredicate;

    @SuppressWarnings("unchecked")
    protected AbstractSessionPresenter(final SessionManager sessionManager,
                                       final SessionPresenter.View view,
                                       final NotificationsObserver notificationsObserver,
                                       final Event<SessionFocusedEvent> sessionFocusedEvent,
                                       final Event<SessionLostFocusEvent> sessionLostFocusEvent,
                                       final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent) {
        this.sessionManager = sessionManager;
        this.notificationsObserver = notificationsObserver;
        this.sessionFocusedEvent = sessionFocusedEvent;
        this.sessionLostFocusEvent = sessionLostFocusEvent;
        this.canvasLostFocusEventEvent = canvasLostFocusEventEvent;
        this.view = view;
        this.typePredicate = Optional.empty();
    }

    public abstract E getDisplayer();

    protected abstract Class<? extends AbstractSession> getSessionType();

    @Override
    @SuppressWarnings("unchecked")
    public void open(final D diagram,
                     final SessionPresenterCallback<D> callback) {
        this.diagram = diagram;
        notificationsObserver.onCommandExecutionFailed(this::showCommandError);
        sessionManager.newSession(diagram.getMetadata(),
                                  getSessionType(),
                                  session -> open((S) session,
                                                  callback));
    }

    public void open(final S session,
                     final SessionPresenterCallback<D> callback) {
        getDisplayer().open(session,
                            new SessionViewer.SessionViewerCallback<D>() {
                                @Override
                                public void afterCanvasInitialized() {
                                    callback.afterCanvasInitialized();
                                    sessionManager.open(session);
                                    callback.afterSessionOpened();
                                }

                                @Override
                                public void onSuccess() {
                                    onSessionOpened(session);
                                    diagram = null;
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError(final ClientRuntimeError error) {
                                    AbstractSessionPresenter.this.showError(error);
                                    diagram = null;
                                    callback.onError(error);
                                }
                            });
    }

    @Override
    public SessionPresenter<S, H, D> displayNotifications(final Predicate<Notification.Type> typePredicate) {
        this.typePredicate = Optional.of(typePredicate);
        return this;
    }

    @Override
    public SessionPresenter<S, H, D> hideNotifications() {
        typePredicate = Optional.empty();
        return this;
    }

    @Override
    public void focus() {
        getSession().ifPresent(sessionManager::open);
        getSession().ifPresent(this::fireSessionFocused);
    }

    @Override
    public void lostFocus() {
        getSession().ifPresent(this::fireSessionLostFocus);
    }

    public void scale(final int width,
                      final int height) {
        getDisplayer().scale(width,
                             height);
    }

    public void clear() {
        getDisplayer().clear();
        diagram = null;
    }

    @Override
    public void destroy() {
        getSession().ifPresent(sessionManager::destroy);
        getDisplayer().destroy();
        getView().destroy();
        diagram = null;
    }

    public S getInstance() {
        if (getDisplayer() == null) {
            return null;
        }
        return getDisplayer().getInstance();
    }

    public Optional<S> getSession() {
        return Optional.ofNullable(getInstance());
    }

    @Override
    public View getView() {
        return view;
    }

    public H getHandler() {
        return getDisplayer().getHandler();
    }

    protected D getDiagram() {
        if (null != diagram) {
            return diagram;
        }
        if (getSession().isPresent()) {
            return (D) getAbstractSession().getCanvasHandler().getDiagram();
        }
        return null;
    }

    private AbstractSession getAbstractSession() {
        return getSession().get();
    }

    @SuppressWarnings("unchecked")
    protected void onSessionOpened(final S session) {
        getView().setCanvasWidget(getDisplayer().getView());
    }

    private void fireSessionLostFocus(final ClientSession session) {
        sessionLostFocusEvent.fire(new SessionLostFocusEvent(session));
        canvasLostFocusEventEvent.fire(new CanvasLostFocusEvent(session.getCanvas()));
    }

    private void fireSessionFocused(final ClientSession session) {
        sessionFocusedEvent.fire(new SessionFocusedEvent(session));
    }

    @SuppressWarnings("unchecked")
    public void refresh() {
    }

    private void showMessage(final String message) {
        if (isDisplayNotifications()) {
            getView().showMessage(message);
        }
    }

    private void showWarning(final String message) {
        if (isDisplayErrors()) {
            getView().showWarning(message);
        }
    }

    private void showError(final String message) {
        if (isDisplayErrors()) {
            getView().showError(message);
        }

        log(message);
    }

    private void showError(final ClientRuntimeError error) {
        if (isDisplayErrors()) {
            getView().showError(error.getMessage());
        }

        log(error.getMessage(), error.getThrowable());
    }

    private void showNotificationMessage(final Notification notification) {
        if (isThisContext(notification)) {
            showMessage(notification.getMessage());
        }
    }

    private void showCommandError(final CommandNotification notification) {
        if (isThisContext(notification)) {
            showError(notification.getMessage());
        }
    }

    private void showValidationError(final ValidationFailedNotification notification) {
        if (isThisContext(notification)) {
            if (Notification.Type.ERROR.equals(notification.getType())) {
                showError(notification.getMessage());
            } else {
                showWarning(notification.getMessage());
            }
        }
    }

    private boolean isThisContext(final Notification notification) {
        try {
            final NotificationContext context = (NotificationContext) notification.getContext();
            return null != getDiagram() && getDiagram().getName().equals(context.getDiagramName());
        } catch (final ClassCastException e) {
            return false;
        }
    }

    private boolean isDisplayNotifications() {
        return typePredicate
                .orElse(t -> false)
                .test(Notification.Type.INFO);
    }

    private boolean isDisplayErrors() {
        return typePredicate
                .orElse(t -> false)
                .or(Notification.Type.WARNING::equals)
                .test(Notification.Type.ERROR);
    }

    private static void log(String message, Throwable throwable) {
        if (null != throwable) {
            LOGGER.log(Level.SEVERE, message, throwable);
        } else {
            log(message);
        }
    }

    private static void log(String message) {
        LOGGER.severe(message);
    }
}
