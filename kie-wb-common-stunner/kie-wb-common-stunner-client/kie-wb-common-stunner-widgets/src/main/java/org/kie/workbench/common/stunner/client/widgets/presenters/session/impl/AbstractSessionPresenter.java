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

import java.util.Optional;
import java.util.function.Predicate;

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
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinition;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

public abstract class AbstractSessionPresenter<D extends Diagram, H extends AbstractCanvasHandler,
        S extends AbstractClientReadOnlySession, E extends SessionViewer<S, H, D>>
        implements SessionPresenter<S, H, D> {

    private final SessionManager sessionManager;
    private final Optional<ToolbarFactory<S>> toolbarFactory;
    private final Optional<DefaultPaletteFactory<H>> paletteFactory;
    private final SessionPresenter.View view;
    private final NotificationsObserver notificationsObserver;

    private D diagram;
    private Toolbar<S> toolbar;
    private PaletteWidget<PaletteDefinition> palette;
    private boolean hasToolbar = false;
    private boolean hasPalette = false;
    private Optional<Predicate<Notification.Type>> typePredicate;

    @SuppressWarnings("unchecked")
    protected AbstractSessionPresenter(final SessionManager sessionManager,
                                       final SessionPresenter.View view,
                                       final Optional<? extends ToolbarFactory<S>> toolbarFactory,
                                       final Optional<DefaultPaletteFactory<H>> paletteFactory,
                                       final NotificationsObserver notificationsObserver) {
        this.sessionManager = sessionManager;
        this.toolbarFactory = (Optional<ToolbarFactory<S>>) toolbarFactory;
        this.paletteFactory = paletteFactory;
        this.notificationsObserver = notificationsObserver;
        this.view = view;
        this.hasToolbar = true;
        this.hasPalette = true;
        this.typePredicate = Optional.empty();
    }

    public abstract E getDisplayer();

    @Override
    public void open(final D diagram,
                     final S session,
                     final SessionPresenterCallback<S, D> callback) {
        this.diagram = diagram;
        notificationsObserver.onCommandExecutionFailed(this::showCommandError);
        notificationsObserver.onValidationSuccess(this::showNotificationMessage);
        notificationsObserver.onValidationFailed(this::showValidationError);
        open(session,
             callback);
    }

    public void open(final S item,
                     final SessionPresenterCallback<S, D> callback) {
        beforeOpen(item);
        getDisplayer().open(item,
                            new SessionViewer.SessionViewerCallback<S, D>() {
                                @Override
                                public void afterCanvasInitialized() {
                                    callback.afterCanvasInitialized();
                                    sessionManager.open(getInstance());
                                    callback.afterSessionOpened();
                                }

                                @Override
                                public void onSuccess() {
                                    onSessionOpened(item);
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError(final ClientRuntimeError error) {
                                    AbstractSessionPresenter.this.showError(error);
                                    callback.onError(error);
                                }
                            });
    }

    public void open(final S item,
                     final int width,
                     final int height,
                     final SessionPresenterCallback<S, D> callback) {
        beforeOpen(item);
        getDisplayer().open(item,
                            width,
                            height,
                            new SessionViewer.SessionViewerCallback<S, D>() {
                                @Override
                                public void afterCanvasInitialized() {
                                    callback.afterCanvasInitialized();
                                    sessionManager.open(getInstance());
                                    callback.afterSessionOpened();
                                }

                                @Override
                                public void onSuccess() {
                                    onSessionOpened(item);
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

    public void scale(final int width,
                      final int height) {
        getDisplayer().scale(width,
                             height);
    }

    public void clear() {
        if (null != getPalette()) {
            getPalette().unbind();
        }
        if (null != getToolbar()) {
            getToolbar().clear();
        }
        getDisplayer().clear();
        diagram = null;
    }

    @Override
    public void destroy() {
        destroyToolbar();
        destroyPalette();
        sessionManager.destroy();
        getDisplayer().destroy();
        getView().destroy();
        diagram = null;
    }

    public S getInstance() {
        return getDisplayer().getInstance();
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

    protected void beforeOpen(final S item) {
        getView().showLoading(true);
    }

    @SuppressWarnings("unchecked")
    protected void onSessionOpened(final S session) {
        destroyToolbar();
        destroyPalette();
        if (hasToolbar) {
            toolbar = buildToolbar(session);
            getView().setToolbarWidget(toolbar.getView());
        }
        if (hasPalette) {
            this.palette = (PaletteWidget<PaletteDefinition>) buildPalette(session);
            getView().setPaletteWidget(getPalette());
        }
        getView().setCanvasWidget(getDisplayer().getView());
        getView().showLoading(false);
    }

    private void showMessage(final String message) {
        if (isDisplayNotifications()) {
            getView().showMessage(message);
        }
    }

    private void showWarning(final String error) {
        if (isDisplayErrors()) {
            getView().showWarning(error);
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

    private Toolbar<S> buildToolbar(final S session) {
        if (!toolbarFactory.isPresent()) {
            throw new UnsupportedOperationException("This session presenter with type [" + this.getClass().getName() + "] does not supports the toolbar.");
        }
        return toolbarFactory.get().build(session);
    }

    @SuppressWarnings("unchecked")
    private PaletteWidget<? extends PaletteDefinition> buildPalette(final S session) {
        if (!paletteFactory.isPresent()) {
            throw new UnsupportedOperationException("This session presenter with type [" + this.getClass().getName() + "] does not supports the palette.");
        }
        return paletteFactory.get()
                .newPalette((H) session.getCanvasHandler());
    }

    private void destroyToolbar() {
        if (null != getToolbar()) {
            getToolbar().destroy();
            toolbar = null;
        }
    }

    private void destroyPalette() {
        if (null != getPalette()) {
            getPalette().unbind();
            getPalette().destroy();
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

    protected D getDiagram() {
        return diagram;
    }

    protected SessionManager getSessionManager() {
        return sessionManager;
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
