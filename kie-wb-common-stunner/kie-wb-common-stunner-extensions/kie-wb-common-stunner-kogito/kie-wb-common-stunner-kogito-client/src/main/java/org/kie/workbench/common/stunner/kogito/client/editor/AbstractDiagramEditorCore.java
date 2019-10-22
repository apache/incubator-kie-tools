/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import com.google.gwt.logging.client.LogConfiguration;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.kogito.api.editor.KogitoDiagramResource;
import org.kie.workbench.common.stunner.kogito.client.resources.i18n.KogitoClientConstants;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractDiagramEditorCore<M extends Metadata, D extends Diagram, C extends KogitoDiagramResource<D>,
        P extends DiagramEditorProxy<C>> implements DiagramEditorCore<M, D> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDiagramEditorCore.class.getName());

    private final View baseEditorView;
    private final TextEditorView xmlEditorView;
    private final Event<NotificationEvent> notificationEvent;
    private final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances;
    private final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances;
    private final Optional<AbstractDiagramEditorMenuSessionItems<?>> menuSessionItems;
    private final ErrorPopupPresenter errorPopupPresenter;
    private final DiagramClientErrorHandler diagramClientErrorHandler;
    private final ClientTranslationService translationService;

    private Optional<SessionEditorPresenter<EditorSession>> editorSessionPresenter = Optional.empty();
    private Optional<SessionViewerPresenter<ViewerSession>> viewerSessionPresenter = Optional.empty();

    private P editorProxy = makeEditorProxy();

    public AbstractDiagramEditorCore() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public AbstractDiagramEditorCore(final View baseEditorView,
                                     final TextEditorView xmlEditorView,
                                     final Event<NotificationEvent> notificationEvent,
                                     final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                     final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                     final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                     final ErrorPopupPresenter errorPopupPresenter,
                                     final DiagramClientErrorHandler diagramClientErrorHandler,
                                     final ClientTranslationService translationService) {
        this.baseEditorView = baseEditorView;
        this.xmlEditorView = xmlEditorView;
        this.notificationEvent = notificationEvent;
        this.editorSessionPresenterInstances = editorSessionPresenterInstances;
        this.viewerSessionPresenterInstances = viewerSessionPresenterInstances;
        this.menuSessionItems = Optional.ofNullable(menuSessionItems);
        this.errorPopupPresenter = errorPopupPresenter;
        this.diagramClientErrorHandler = diagramClientErrorHandler;
        this.translationService = translationService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void open(final D diagram) {
        editorProxy = makeStunnerEditorProxy();
        baseEditorView.showLoading();

        //Open applicable SessionPresenter
        if (!isReadOnly()) {
            openSession(diagram);
        } else {
            openReadOnlySession(diagram);
        }
    }

    protected abstract boolean isReadOnly();

    protected abstract C makeDiagramResourceImpl(final D diagram);

    protected abstract C makeDiagramResourceImpl(final String xml);

    protected abstract P makeEditorProxy();

    @SuppressWarnings("unchecked")
    public P makeStunnerEditorProxy() {
        final P proxy = makeEditorProxy();
        proxy.setContentSupplier(() -> makeDiagramResourceImpl(getDiagram()));
        proxy.setHashCodeSupplier(() -> {
            if (null == getDiagram()) {
                return 0;
            }
            int hash = getDiagram().hashCode();
            if (null == getCanvasHandler() ||
                    null == getCanvasHandler().getCanvas() ||
                    null == getCanvasHandler().getCanvas().getShapes()) {
                return hash;
            }
            Collection<Shape> collectionOfShapes = getCanvasHandler().getCanvas().getShapes();
            ArrayList<Shape> shapes = new ArrayList<>();
            shapes.addAll(collectionOfShapes);
            shapes.sort((a, b) -> (a.getShapeView().getShapeX() == b.getShapeView().getShapeX()) ?
                    (int) Math.round(a.getShapeView().getShapeY() - b.getShapeView().getShapeY()) :
                    (int) Math.round(a.getShapeView().getShapeX() - b.getShapeView().getShapeX()));
            for (Shape shape : shapes) {
                hash = HashUtil.combineHashCodes(hash,
                                                 Double.hashCode(shape.getShapeView().getShapeX()),
                                                 Double.hashCode(shape.getShapeView().getShapeY()));
            }
            return hash;
        });

        return proxy;
    }

    public P makeXmlEditorProxy() {
        final P proxy = makeEditorProxy();
        proxy.setContentSupplier(() -> makeDiagramResourceImpl(xmlEditorView.getContent()));
        proxy.setHashCodeSupplier(() -> xmlEditorView.getContent().hashCode());
        return proxy;
    }

    public void openSession(final D diagram) {
        editorSessionPresenter = Optional.ofNullable(newSessionEditorPresenter());
        editorSessionPresenter.ifPresent(p -> p.open(diagram, getSessionPresenterCallback(diagram)));
    }

    private SessionPresenter.SessionPresenterCallback<Diagram> getSessionPresenterCallback(D diagram) {
        return new SessionPresenter.SessionPresenterCallback<Diagram>() {
            @Override
            public void afterSessionOpened() {

            }

            @Override
            public void afterCanvasInitialized() {

            }

            @Override
            public void onSuccess() {
                initialiseKieEditorForSession(diagram);
                menuSessionItems.ifPresent(menuItems -> menuItems.bind(getSession()));
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                onLoadError(error);
            }
        };
    }

    public void openReadOnlySession(final D diagram) {
        viewerSessionPresenter = Optional.ofNullable(newSessionViewerPresenter());
        viewerSessionPresenter.ifPresent(p -> p.open(diagram, getSessionPresenterCallback(diagram)));
    }

    public abstract void onLoadError(final ClientRuntimeError error);

    @Override
    public SessionEditorPresenter<EditorSession> newSessionEditorPresenter() {
        final SessionEditorPresenter<EditorSession> presenter =
                (SessionEditorPresenter<EditorSession>) editorSessionPresenterInstances.get()
                        .withToolbar(false)
                        .withPalette(true)
                        .displayNotifications(type -> true);
        baseEditorView.setWidget(presenter.getView());
        return presenter;
    }

    @Override
    public SessionViewerPresenter<ViewerSession> newSessionViewerPresenter() {
        final SessionViewerPresenter<ViewerSession> presenter =
                (SessionViewerPresenter<ViewerSession>) viewerSessionPresenterInstances.get()
                        .withToolbar(false)
                        .withPalette(false)
                        .displayNotifications(type -> true);
        baseEditorView.setWidget(presenter.getView());
        return presenter;
    }

    @SuppressWarnings("unused")
    void onSessionErrorEvent(final @Observes OnSessionErrorEvent errorEvent) {
        if (isSameSession(errorEvent.getSession())) {
            executeWithConfirm(translationService.getValue(KogitoClientConstants.ON_ERROR_CONFIRM_UNDO_LAST_ACTION,
                                                           errorEvent.getError()),
                               () -> menuSessionItems
                                       .map(AbstractDiagramEditorMenuSessionItems::getCommands)
                                       .map(EditorSessionCommands::getUndoSessionCommand)
                                       .ifPresent(UndoSessionCommand::execute));
        }
    }

    protected boolean isSameSession(final ClientSession other) {
        return null != other && null != getSession() && other.equals(getSession());
    }

    private void executeWithConfirm(final String message,
                                    final Command command) {
        final Command yesCommand = command::execute;
        final Command noCommand = () -> {/*NOP*/};
        final YesNoCancelPopup popup =
                YesNoCancelPopup.newYesNoCancelPopup(message,
                                                     null,
                                                     yesCommand,
                                                     noCommand,
                                                     noCommand);
        popup.show();
    }

    public P getEditorProxy() {
        return editorProxy;
    }

    protected void setEditorProxy(final P editorProxy) {
        this.editorProxy = editorProxy;
    }

    protected View getBaseEditorView() {
        return baseEditorView;
    }

    protected TextEditorView getXMLEditorView() {
        return xmlEditorView;
    }

    protected Event<NotificationEvent> getNotificationEvent() {
        return notificationEvent;
    }

    protected AbstractDiagramEditorMenuSessionItems<?> getMenuSessionItems() {
        return menuSessionItems.orElse(null);
    }

    public void destroySession() {
        //Release existing SessionPresenter
        editorSessionPresenter.ifPresent(session -> {
            session.destroy();
            editorSessionPresenter = Optional.empty();
        });
        viewerSessionPresenter.ifPresent(session -> {
            session.destroy();
            viewerSessionPresenter = Optional.empty();
        });
        editorSessionPresenterInstances.destroyAll();
        viewerSessionPresenterInstances.destroyAll();
    }

    private ClientSession getSession() {
        return null != getSessionPresenter() ? getSessionPresenter().getInstance() : null;
    }

    @Override
    public int getCurrentDiagramHash() {
        return editorProxy.getEditorHashCode();
    }

    @Override
    public CanvasHandler getCanvasHandler() {
        return null != getSession() ? getSession().getCanvasHandler() : null;
    }

    @SuppressWarnings("unchecked")
    public D getDiagram() {
        return null != getCanvasHandler() ? (D) getCanvasHandler().getDiagram() : null;
    }

    @Override
    public void onSaveError(final ClientRuntimeError error) {
        showError(error);
    }

    public void showError(final ClientRuntimeError error) {
        diagramClientErrorHandler.handleError(error, this::showError);
        log(Level.SEVERE, error.toString());
    }

    public void showError(final String message) {
        errorPopupPresenter.showMessage(message);
        baseEditorView.hideBusyIndicator();
    }

    protected void log(final Level level,
                       final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }

    @Override
    public SessionPresenter<? extends ClientSession, ?, Diagram> getSessionPresenter() {
        if (editorSessionPresenter.isPresent()) {
            return editorSessionPresenter.get();
        } else if (viewerSessionPresenter.isPresent()) {
            return viewerSessionPresenter.get();
        }
        return null;
    }

    @Override
    public void doFocus() {
        if (null != getSessionPresenter()) {
            getSessionPresenter().focus();
        }
    }

    @Override
    public void doLostFocus() {
        if (null != getSessionPresenter()) {
            getSessionPresenter().lostFocus();
        }
    }

    //For Unit Testing
    public void setEditorSessionPresenter(final SessionEditorPresenter<EditorSession> presenter) {
        this.editorSessionPresenter = Optional.ofNullable(presenter);
    }

    //For Unit Testing
    public void setReadOnlySessionPresenter(final SessionViewerPresenter<ViewerSession> presenter) {
        this.viewerSessionPresenter = Optional.ofNullable(presenter);
    }
}
