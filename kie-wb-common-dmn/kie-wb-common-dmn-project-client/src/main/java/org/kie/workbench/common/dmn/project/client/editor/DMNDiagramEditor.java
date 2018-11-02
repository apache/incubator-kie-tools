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
package org.kie.workbench.common.dmn.project.client.editor;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactory;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = DMNDiagramEditor.EDITOR_ID, supportedTypes = {DMNDiagramResourceType.class})
public class DMNDiagramEditor extends AbstractProjectDiagramEditor<DMNDiagramResourceType> {

    public static final String EDITOR_ID = "DMNDiagramEditor";

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private final DecisionNavigatorDock decisionNavigatorDock;
    private final LayoutHelper layoutHelper;

    @Inject
    public DMNDiagramEditor(final View view,
                            final PlaceManager placeManager,
                            final ErrorPopupPresenter errorPopupPresenter,
                            final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                            final SavePopUpPresenter savePopUpPresenter,
                            final DMNDiagramResourceType resourceType,
                            final ClientProjectDiagramService projectDiagramServices,
                            final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                            final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                            final DMNProjectEditorMenuSessionItems menuSessionItems,
                            final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                            final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                            final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                            final ProjectMessagesListener projectMessagesListener,
                            final DiagramClientErrorHandler diagramClientErrorHandler,
                            final ClientTranslationService translationService,
                            final TextEditorView xmlEditorView,
                            final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                            final SessionManager sessionManager,
                            final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final DecisionNavigatorDock decisionNavigatorDock,
                            final LayoutHelper layoutHelper) {
        super(view,
              placeManager,
              errorPopupPresenter,
              changeTitleNotificationEvent,
              savePopUpPresenter,
              resourceType,
              projectDiagramServices,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              menuSessionItems,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              projectMessagesListener,
              diagramClientErrorHandler,
              translationService,
              xmlEditorView,
              projectDiagramResourceServiceCaller);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.decisionNavigatorDock = decisionNavigatorDock;
        this.layoutHelper = layoutHelper;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        superDoStartUp(path, place);
        decisionNavigatorDock.init(PerspectiveIds.LIBRARY);
    }

    @Override
    public void open(final ProjectDiagram diagram) {
        this.layoutHelper.applyLayout(diagram);
        super.open(diagram);
    }

    @Override
    protected int getCanvasWidth() {
        return (int) DMNGraphFactory.GRAPH_DEFAULT_WIDTH;
    }

    @Override
    protected int getCanvasHeight() {
        return (int) DMNGraphFactory.GRAPH_DEFAULT_HEIGHT;
    }

    @OnOpen
    public void onOpen() {
        super.doOpen();
    }

    @OnClose
    public void onClose() {
        superOnClose();
        decisionNavigatorDock.close();
        decisionNavigatorDock.resetContent();
    }

    @Override
    protected void onDiagramLoad() {
        final Optional<CanvasHandler> canvasHandler = Optional.ofNullable(getCanvasHandler());

        canvasHandler.ifPresent(c -> {
            final ExpressionEditorView.Presenter expressionEditor = ((DMNSession) sessionManager.getCurrentSession()).getExpressionEditor();
            expressionEditor.setToolbarStateHandler(new ProjectToolbarStateHandler(getMenuSessionItems()));
            decisionNavigatorDock.setupContent(c);
            decisionNavigatorDock.open();
        });
    }

    @OnFocus
    public void onFocus() {
        superDoFocus();
        onDiagramLoad();
    }

    @OnLostFocus
    public void onLostFocus() {
        super.doLostFocus();
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    @WorkbenchMenu
    public Menus getMenus() {
        return super.getMenus();
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getCurrentDiagramHash());
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        if (isSameSession(event.getSession())) {
            final DMNSession session = sessionManager.getCurrentSession();
            final ExpressionEditorView.Presenter expressionEditor = session.getExpressionEditor();
            sessionCommandManager.execute(session.getCanvasHandler(),
                                          new NavigateToExpressionEditorCommand(expressionEditor,
                                                                                getSessionPresenter(),
                                                                                sessionManager,
                                                                                sessionCommandManager,
                                                                                refreshFormPropertiesEvent,
                                                                                event.getNodeUUID(),
                                                                                event.getHasExpression(),
                                                                                event.getHasName()));
        }
    }

    void superDoFocus() {
        super.doFocus();
    }

    void superOnClose() {
        super.doClose();
    }

    void superDoStartUp(final ObservablePath path,
                        final PlaceRequest place) {
        super.doStartUp(path, place);
    }
}
