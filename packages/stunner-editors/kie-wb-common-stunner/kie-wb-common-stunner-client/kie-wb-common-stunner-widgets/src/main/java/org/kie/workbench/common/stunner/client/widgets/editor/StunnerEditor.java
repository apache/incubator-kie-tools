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


package org.kie.workbench.common.stunner.client.widgets.editor;

import java.util.function.Consumer;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.widgets.client.errorpage.ErrorPage;

@Dependent
public class StunnerEditor {

    private final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances;
    private final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances;
    private final ClientTranslationService translationService;
    private final StunnerEditorView view;
    private final ErrorPage errorPage;

    private SessionDiagramPresenter diagramPresenter;
    private boolean isReadOnly;
    private Consumer<DiagramParsingException> parsingExceptionProcessor;
    private Consumer<Throwable> exceptionProcessor;
    private Consumer<Integer> onResetContentHashProcessor;

    // CDI proxy.
    public StunnerEditor() {
        this(null, null, null, null, null);
    }

    @Inject
    public StunnerEditor(ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                         ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                         ClientTranslationService translationService,
                         StunnerEditorView view,
                         ErrorPage errorPage) {
        this.editorSessionPresenterInstances = editorSessionPresenterInstances;
        this.viewerSessionPresenterInstances = viewerSessionPresenterInstances;
        this.translationService = translationService;
        this.isReadOnly = false;
        this.view = view;
        this.errorPage = errorPage;
        this.parsingExceptionProcessor = e -> {
        };
        this.exceptionProcessor = e -> {
        };
        this.onResetContentHashProcessor = e -> {
        };
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public void setOnResetContentHashProcessor(Consumer<Integer> onResetContentHashProcessor) {
        this.onResetContentHashProcessor = onResetContentHashProcessor;
    }

    public void setParsingExceptionProcessor(Consumer<DiagramParsingException> parsingExceptionProcessor) {
        this.parsingExceptionProcessor = parsingExceptionProcessor;
    }

    public void setExceptionProcessor(Consumer<Throwable> exceptionProcessor) {
        this.exceptionProcessor = exceptionProcessor;
    }

    public void open(final Diagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        if (isClosed()) {
            if (!isReadOnly) {
                diagramPresenter = editorSessionPresenterInstances.get();
            } else {
                diagramPresenter = viewerSessionPresenterInstances.get();
            }
            diagramPresenter.displayNotifications(type -> true);
            diagramPresenter.withPalette(!isReadOnly);
            diagramPresenter.withToolbar(false);
            view.setWidget(diagramPresenter.getView());
        }
        diagramPresenter.open(diagram, new SessionPresenter.SessionPresenterCallback() {
            @Override
            public void onOpen(Diagram diagram) {
                callback.onOpen(diagram);
            }

            @Override
            public void afterSessionOpened() {
                callback.afterSessionOpened();
            }

            @Override
            public void afterCanvasInitialized() {
                callback.afterCanvasInitialized();
            }

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(ClientRuntimeError error) {
                handleError(error);
                callback.onError(error);
            }
        });
    }

    public int getCurrentContentHash() {
        if (null == getSession()) {
            return 0;
        }
        if (null == getCanvasHandler().getDiagram()) {
            return 0;
        }
        return getCanvasHandler().getDiagram().hashCode();
    }

    public void handleError(final ClientRuntimeError error) {
        final Throwable e = error.getThrowable();
        if (e instanceof DiagramParsingException) {
            final DiagramParsingException dpe = (DiagramParsingException) e;
            close();
            parsingExceptionProcessor.accept(dpe);
            errorPage.setTitle(error.getErrorTitle());
            errorPage.setContent(error.getErrorContent());
            errorPage.setErrorContent(error.getErrorMessage());
            view.setWidget(errorPage);
        } else {
            String message = null;
            if (e instanceof DefinitionNotFoundException) {
                final DefinitionNotFoundException dnfe = (DefinitionNotFoundException) e;
                message = translationService.getValue(CoreTranslationMessages.DIAGRAM_LOAD_FAIL_UNSUPPORTED_ELEMENTS,
                                                      dnfe.getDefinitionId());
            } else {
                message = error.getThrowable() != null ?
                        error.getThrowable().getMessage() : error.getErrorMessage();
            }
            showError(message);
            exceptionProcessor.accept(error.getThrowable());
        }
    }

    public StunnerEditor close() {
        if (!isClosed()) {
            diagramPresenter.destroy();
            diagramPresenter = null;
            editorSessionPresenterInstances.destroyAll();
            viewerSessionPresenterInstances.destroyAll();
            view.clear();
        }
        return this;
    }

    @PreDestroy
    public void destroy() {
        close();
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean isClosed() {
        return null == diagramPresenter;
    }

    public ClientSession getSession() {
        return (ClientSession) diagramPresenter.getInstance();
    }

    public CanvasHandler getCanvasHandler() {
        return (CanvasHandler) diagramPresenter.getHandler();
    }

    public Diagram getDiagram() {
        return getCanvasHandler().getDiagram();
    }

    public SessionDiagramPresenter getPresenter() {
        return diagramPresenter;
    }

    public void showMessage(String message) {
        diagramPresenter.getView().showMessage(message);
    }

    public void showError(String message) {
    }

    public IsWidget getView() {
        return view;
    }
}
