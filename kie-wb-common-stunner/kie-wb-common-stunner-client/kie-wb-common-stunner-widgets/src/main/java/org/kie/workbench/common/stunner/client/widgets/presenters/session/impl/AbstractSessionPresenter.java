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

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Predicate;

import javax.enterprise.event.Event;

import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.CommandNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationContext;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.notification.ValidationFailedNotification;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinition;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public abstract class AbstractSessionPresenter<D extends Diagram, H extends AbstractCanvasHandler,
        S extends AbstractSession, E extends SessionViewer<S, H, D>>
        implements SessionPresenter<S, H, D> {

    private final DefinitionUtils definitionUtils;
    private final SessionManager sessionManager;
    private final DefaultPaletteFactory<H> paletteFactory;
    private final SessionPresenter.View view;
    private final NotificationsObserver notificationsObserver;
    private final Event<SessionFocusedEvent> sessionFocusedEvent;
    private final Event<CanvasFocusedEvent> canvasFocusedEvent;
    private final Event<SessionLostFocusEvent> sessionLostFocusEvent;
    private final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent;

    private D diagram;
    private Toolbar<S> toolbar;
    private PaletteWidget<PaletteDefinition> palette;
    private boolean hasToolbar = false;
    private boolean hasPalette = false;
    private Optional<Predicate<Notification.Type>> typePredicate;

    @SuppressWarnings("unchecked")
    protected AbstractSessionPresenter(final DefinitionUtils definitionUtils,
                                       final SessionManager sessionManager,
                                       final SessionPresenter.View view,
                                       final DefaultPaletteFactory<H> paletteFactory,
                                       final NotificationsObserver notificationsObserver,
                                       final Event<SessionFocusedEvent> sessionFocusedEvent,
                                       final Event<CanvasFocusedEvent> canvasFocusedEvent,
                                       final Event<SessionLostFocusEvent> sessionLostFocusEvent,
                                       final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent) {
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
        this.paletteFactory = paletteFactory;
        this.notificationsObserver = notificationsObserver;
        this.sessionFocusedEvent = sessionFocusedEvent;
        this.sessionLostFocusEvent = sessionLostFocusEvent;
        this.canvasFocusedEvent = canvasFocusedEvent;
        this.canvasLostFocusEventEvent = canvasLostFocusEventEvent;
        this.view = view;
        this.hasToolbar = true;
        this.hasPalette = true;
        this.typePredicate = Optional.empty();
    }

    public abstract E getDisplayer();

    protected abstract Class<? extends AbstractSession> getSessionType();

    protected abstract Toolbar<S> newToolbar(Annotation qualifier);

    protected abstract void destroyToolbarInstace(Toolbar<S> toolbar);

    @Override
    @SuppressWarnings("unchecked")
    public void open(final D diagram,
                     final SessionPresenterCallback<D> callback) {
        this.diagram = diagram;
        notificationsObserver.onCommandExecutionFailed(this::showCommandError);
        notificationsObserver.onValidationSuccess(this::showNotificationMessage);
        notificationsObserver.onValidationFailed(this::showValidationError);
        sessionManager.newSession(diagram.getMetadata(),
                                  getSessionType(),
                                  session -> open((S) session,
                                                  callback));
    }

    public void open(final S session,
                     final SessionPresenterCallback<D> callback) {
        beforeOpen(session);
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
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError(final ClientRuntimeError error) {
                                    AbstractSessionPresenter.this.showError(error);
                                    callback.onError(error);
                                }
                            });
    }

    public void open(final S session,
                     final int width,
                     final int height,
                     final SessionPresenterCallback<D> callback) {
        beforeOpen(session);
        getDisplayer().open(session,
                            width,
                            height,
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
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError(final ClientRuntimeError error) {
                                    AbstractSessionPresenter.this.showError(error);
                                    callback.onError(error);
                                }
                            });
    }

    @Override
    public SessionPresenter<S, H, D> withToolbar(final boolean hasToolbar) {
        this.hasToolbar = hasToolbar;
        return this;
    }

    @Override
    public SessionPresenter<S, H, D> withPalette(final boolean hasPalette) {
        this.hasPalette = hasPalette;
        return this;
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
        if (null != palette) {
            palette.unbind();
        }
        if (null != toolbar) {
            destroyToolbar();
        }
        getDisplayer().clear();
        diagram = null;
    }

    @Override
    public void destroy() {
        destroyToolbar();
        destroyPalette();
        getSession().ifPresent(sessionManager::destroy);
        getDisplayer().destroy();
        getView().destroy();
        diagram = null;
    }

    public S getInstance() {
        return getDisplayer().getInstance();
    }

    public Optional<S> getSession() {
        return Optional.ofNullable(getInstance());
    }

    @Override
    public Toolbar<S> getToolbar() {
        return toolbar;
    }

    @Override
    public PaletteWidget<PaletteDefinition> getPalette() {
        return palette;
    }

    @Override
    public View getView() {
        return view;
    }

    public H getHandler() {
        return getDisplayer().getHandler();
    }

    protected D getDiagram() {
        return diagram;
    }

    protected void beforeOpen(final S item) {
        getView().showLoading(true);
    }

    @SuppressWarnings("unchecked")
    protected void onSessionOpened(final S session) {
        destroyToolbar();
        destroyPalette();
        initToolbar(session);
        initPalette(session);
        getView().setCanvasWidget(getDisplayer().getView());
        getView().showLoading(false);
    }

    private void fireSessionLostFocus(final ClientSession session) {
        sessionLostFocusEvent.fire(new SessionLostFocusEvent(session));
        canvasLostFocusEventEvent.fire(new CanvasLostFocusEvent(session.getCanvas()));
    }

    private void fireSessionFocused(final ClientSession session) {
        sessionFocusedEvent.fire(new SessionFocusedEvent(session));
        canvasFocusedEvent.fire(new CanvasFocusedEvent(session.getCanvas()));
    }

    @SuppressWarnings("unchecked")
    private void initToolbar(final S session) {
        if (hasToolbar) {
            final Annotation qualifier =
                    definitionUtils.getQualifier(session.getCanvasHandler().getDiagram().getMetadata().getDefinitionSetId());
            toolbar = newToolbar(qualifier);
            if (null != toolbar) {
                toolbar.load(session);
            }
            getView().setToolbarWidget(toolbar.getView());
        }
    }

    @SuppressWarnings("unchecked")
    private void initPalette(final S session) {
        if (hasPalette) {
            palette = (PaletteWidget<PaletteDefinition>) buildPalette(session);
            if (null != palette) {
                getView().setPaletteWidget(palette);
            }
        }
    }

    public void refresh() {
        destroyPalette();
        getSession().ifPresent(this::initPalette);
    }

    private void showMessage(final String message) {
        if (isDisplayNotifications()) {
            getView().showMessage(message);
        }
    }

    private void showWarning() {
        if (isDisplayErrors()) {
            getView().showWarning();
        }
    }

    private void showError(final String message) {
        if (isDisplayErrors()) {
            getView().showError(message);
        }
    }

    private void showError(final ClientRuntimeError error) {
        if (isDisplayErrors()) {
            getView().showLoading(false);
            getView().showError(error.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private PaletteWidget<? extends PaletteDefinition> buildPalette(final S session) {
        if (null != paletteFactory) {
            return paletteFactory.newPalette((H) session.getCanvasHandler());
        }
        return null;
    }

    private void destroyToolbar() {
        if (null != toolbar) {
            toolbar.destroy();
            destroyToolbarInstace(toolbar);
            toolbar = null;
        }
    }

    private void destroyPalette() {
        if (null != palette) {
            palette.destroy();
            palette = null;
        }
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
                showWarning();
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
}
