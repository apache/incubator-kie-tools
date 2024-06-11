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

import java.util.Arrays;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.types.JsCanvas;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jsinterop.base.Js;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.util.StunnerStateApplier;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.api.JsCanvasWrapper;
import org.kie.workbench.common.stunner.core.client.api.JsStunnerSession;
import org.kie.workbench.common.stunner.core.client.api.JsWindow;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.widgets.client.errorpage.ErrorPage;

import static elemental2.dom.CSSProperties.HeightUnionType;
import static elemental2.dom.CSSProperties.WidthUnionType;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
public class StunnerEditor {

    private final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances;
    private final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances;
    private final ClientTranslationService translationService;
    private final ErrorPage errorPage;
    private boolean hasErrors;

    private SessionDiagramPresenter diagramPresenter;
    private boolean isReadOnly;
    private Consumer<DiagramParsingException> parsingExceptionProcessor;
    private Consumer<Throwable> exceptionProcessor;
    private AlertsControl<AbstractCanvas> alertsControl;

    private static final String ROOT_CONTAINER_LIGHT_CSS = "root-container";
    private static final String ROOT_CONTAINER_DARK_CSS = "root-container root-container-dark";

    // CDI proxy.
    public StunnerEditor() {
        this(null, null, null, null);
    }

    @Inject
    public StunnerEditor(ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                         ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                         ClientTranslationService translationService,
                         ErrorPage errorPage) {
        this.editorSessionPresenterInstances = editorSessionPresenterInstances;
        this.viewerSessionPresenterInstances = viewerSessionPresenterInstances;
        this.translationService = translationService;
        this.isReadOnly = false;
        this.errorPage = errorPage;
        this.parsingExceptionProcessor = e -> {
        };
        this.exceptionProcessor = e -> {
        };
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public void setParsingExceptionProcessor(Consumer<DiagramParsingException> parsingExceptionProcessor) {
        this.parsingExceptionProcessor = parsingExceptionProcessor;
    }

    public void setExceptionProcessor(Consumer<Throwable> exceptionProcessor) {
        this.exceptionProcessor = exceptionProcessor;
    }

    @SuppressWarnings("all")
    public void open(final Diagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        if (isClosed()) {
            if (!isReadOnly) {
                diagramPresenter = editorSessionPresenterInstances.get();
            } else {
                diagramPresenter = viewerSessionPresenterInstances.get();
            }
            diagramPresenter.displayNotifications(type -> true);
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
                ClientSession session = getSession();
                if (isReadOnly) {
                    alertsControl = ((ViewerSession) session).getAlertsControl();
                } else {
                    alertsControl = ((EditorSession) session).getAlertsControl();
                }
                callback.afterCanvasInitialized();
            }

            @Override
            public void onSuccess() {
                alertsControl.clear();
                initializeJsSession((AbstractSession) getSession());
                callback.onSuccess();
            }

            @Override
            public void onError(ClientRuntimeError error) {
                handleError(error);
                callback.onError(error);
            }
        });

        setupRootContainer();
    }

    protected void setupRootContainer() {
        HTMLDivElement rootContainer = (HTMLDivElement) DomGlobal.document.getElementById("root-container");
        removeAllChildren(rootContainer);

        rootContainer.appendChild(diagramPresenter.getView().getElement());
        DomGlobal.window.addEventListener("resize", evt -> {
            resizeTo(rootContainer, DomGlobal.window.innerWidth, DomGlobal.window.innerHeight);
        });
        resize(rootContainer);
    }

    private void resize(HTMLDivElement rootContainer) {
        resizeTo(rootContainer, DomGlobal.document.body.clientWidth,
                 DomGlobal.document.body.clientHeight);
    }

    private void resizeTo(HTMLDivElement rootContainer, int width,
                          int height) {
        CSSStyleDeclaration style = rootContainer.style;
        style.width = WidthUnionType.of(width + "px");
        style.height = HeightUnionType.of(height + "px");
    }

    @SuppressWarnings("all")
    private void initializeJsSession(AbstractSession session) {
        JsStunnerSession jssession = new JsStunnerSession(session);
        JsWindow.getEditor().setSession(jssession);
        initializeJsCanvas(session);
    }

    @SuppressWarnings("all")
    private void initializeJsCanvas(AbstractSession session) {
        LienzoCanvas canvas = (LienzoCanvas) session.getCanvasHandler().getCanvas();
        LienzoPanel panel = (LienzoPanel) canvas.getView().getPanel();
        LienzoBoundsPanel lienzoPanel = panel.getView();
        JsCanvas jsCanvas = JsCanvas.getInstance().init(lienzoPanel, lienzoPanel.getLayer(), new StunnerStateApplier() {
            @Override
            public Shape getShape(String uuid) {
                return canvas.getShape(uuid);
            }
        });
        JsCanvasWrapper jsCanvasWrapper = new JsCanvasWrapper();
        JsWindow.setCanvas(jsCanvasWrapper);
        JsWindow.getEditor().setCanvas(jsCanvasWrapper);
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
        final Throwable throwable = error.getThrowable();
        String message;
        if (throwable instanceof DiagramParsingException) {
            final DiagramParsingException diagramParsingException = (DiagramParsingException) throwable;
            parsingExceptionProcessor.accept(diagramParsingException);
            Exception javaScriptException = Js.uncheckedCast(diagramParsingException.getCause());
            if (javaScriptException != null) {
                message = error.getMessage() + "\r\n";
                message += javaScriptException.getLocalizedMessage();
            } else {
                final String title = translationService.getValue(CoreTranslationMessages.DIAGRAM_LOAD_FAIL_PARSING);
                message = buildErrorMessage(error, throwable, title);
            }
        } else if (throwable instanceof DefinitionNotFoundException) {
            final DefinitionNotFoundException dnfe = (DefinitionNotFoundException) throwable;
            exceptionProcessor.accept(dnfe);
            message = translationService.getValue(CoreTranslationMessages.DIAGRAM_LOAD_FAIL_UNSUPPORTED_ELEMENTS) + "\r\n";
            message += dnfe.getDefinitionId();
        } else {
            exceptionProcessor.accept(error.getThrowable());
            final String title = translationService.getValue(CoreTranslationMessages.DIAGRAM_LOAD_FAIL_GENERIC);
            message = buildErrorMessage(error, throwable, title);
        }

        hasErrors = true;
        if ((diagramPresenter != null) &&
                (diagramPresenter.getView() != null)) {
            addError(message);
        } else {
            clearRootAndDrawError();
        }
    }

    protected void clearRootAndDrawError() {
        HTMLDivElement rootContainer = (HTMLDivElement) DomGlobal.document.getElementById("root-container");
        removeAllChildren(rootContainer);

        // Error page theme
        final boolean isDarkTheme = StunnerTheme.getTheme().isDarkTheme();
        rootContainer.className = isDarkTheme ? ROOT_CONTAINER_DARK_CSS : ROOT_CONTAINER_LIGHT_CSS;
        errorPage.setDarkTheme(isDarkTheme);

        rootContainer.appendChild(errorPage.getElement());
    }

    public StunnerEditor close() {
        clearAlerts();

        if (!isClosed()) {
            alertsControl = null;
            diagramPresenter.destroy();
            diagramPresenter = null;
            editorSessionPresenterInstances.destroyAll();
            viewerSessionPresenterInstances.destroyAll();
        }
        return this;
    }

    public boolean isClosed() {
        return null == diagramPresenter;
    }

    public boolean isReadOnly() {
        return isReadOnly;
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

    public ClientSession getSession() {
        if (diagramPresenter == null) {
            return null;
        }
        return (ClientSession) diagramPresenter.getInstance();
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void addMessage(String message) {
        if (!isClosed()) {
            alertsControl.addInfo(message);
        }
    }

    public void addWarning(String message) {
        if (!isClosed()) {
            alertsControl.addWarning(message);
        }
    }

    public void addError(String message) {
        if (!isClosed()) {
            hasErrors = true;
            alertsControl.addError(message);
        }
    }

    public void clearAlerts() {
        hasErrors = false;
        if (null != alertsControl) {
            alertsControl.clear();
        }
    }

    private String buildErrorMessage(ClientRuntimeError error, Throwable throwable, String errorTitle) {
        String message;
        message = errorTitle + "\r\n";
        if (throwable != null) {
            message += throwable + "\r\n";
            message += "\r\n";
            message += translationService.getValue(CoreTranslationMessages.DIAGRAM_LOAD_FAIL_STACK_TRACE);
            message += Arrays.toString(throwable.getStackTrace());
        } else {
            message += error.toString();
        }
        return message;
    }

    public void setScrollbarColors() {
        if (null != getSession()) {
            LienzoCanvas canvas = (LienzoCanvas) getSession().getCanvasHandler().getCanvas();
            LienzoPanel panel = (LienzoPanel) canvas.getView().getPanel();
            LienzoBoundsPanel lienzoPanel = panel.getView();

            ((ScrollablePanel) lienzoPanel).setScrollbarColors(StunnerTheme.getTheme().getScrollbarColor(),
                                                               StunnerTheme.getTheme().getScrollbarBackgroundColor());
        }
    }

    public void setCanvasBackgroundColor() {
        if (null != getSession()) {
            LienzoCanvas canvas = (LienzoCanvas) getSession().getCanvasHandler().getCanvas();

            canvas.setBackgroundColor(StunnerTheme.getTheme().getCanvasBackgroundColor());
        }
    }

    @PreDestroy
    public void destroy() {
        close();
    }
}
