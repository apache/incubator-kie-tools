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
package org.kie.workbench.common.dmn.showcase.client.screens.editor;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.dmn.showcase.client.screens.ShowcaseDiagramService;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ValidationUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchScreen(identifier = SessionDiagramEditorScreen.SCREEN_ID)
public class SessionDiagramEditorScreen {

    private static Logger LOGGER = Logger.getLogger(SessionDiagramEditorScreen.class.getName());

    public static final String SCREEN_ID = "SessionDiagramEditorScreen";

    private final DefinitionManager definitionManager;
    private final ClientFactoryService clientFactoryServices;
    private final ShowcaseDiagramService diagramService;
    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final SessionEditorPresenter<EditorSession> presenter;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final Event<SessionFocusedEvent> sessionFocusedEvent;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private final MenuDevCommandsBuilder menuDevCommandsBuilder;
    private final ScreenPanelView screenPanelView;
    private final ScreenErrorView screenErrorView;
    private final DecisionNavigatorDock decisionNavigatorDock;
    private final LayoutHelper layoutHelper;
    private PlaceRequest placeRequest;
    private String title = "Authoring Screen";
    private Menus menu = null;

    @Inject
    public SessionDiagramEditorScreen(final DefinitionManager definitionManager,
                                      final ClientFactoryService clientFactoryServices,
                                      final ShowcaseDiagramService diagramService,
                                      final SessionManager sessionManager,
                                      final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                      final SessionEditorPresenter<EditorSession> presenter,
                                      final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                      final Event<SessionFocusedEvent> sessionFocusedEvent,
                                      final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                      final MenuDevCommandsBuilder menuDevCommandsBuilder,
                                      final ScreenPanelView screenPanelView,
                                      final ScreenErrorView screenErrorView,
                                      final DecisionNavigatorDock decisionNavigatorDock,
                                      final LayoutHelper layoutHelper) {
        this.definitionManager = definitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.diagramService = diagramService;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.presenter = presenter;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.sessionFocusedEvent = sessionFocusedEvent;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
        this.screenPanelView = screenPanelView;
        this.screenErrorView = screenErrorView;
        this.decisionNavigatorDock = decisionNavigatorDock;
        this.layoutHelper = layoutHelper;
    }

    @PostConstruct
    public void init() {
        decisionNavigatorDock.init(AuthoringPerspective.PERSPECTIVE_ID);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        this.menu = makeMenuBar();
        final String name = placeRequest.getParameter("name",
                                                      "");
        final boolean isCreate = name == null || name.trim().length() == 0;
        final Command callback = () -> {
            final Diagram diagram = getDiagram();
            if (null != diagram) {
                // Update screen title.
                updateTitle(diagram.getMetadata().getTitle());
            }
        };
        if (isCreate) {
            final String defSetId = placeRequest.getParameter("defSetId",
                                                              "");
            final String shapeSetd = placeRequest.getParameter("shapeSetId",
                                                               "");
            final String title = placeRequest.getParameter("title",
                                                           "");
            // Create a new diagram.
            newDiagram(UUID.uuid(),
                       title,
                       defSetId,
                       shapeSetd,
                       callback);
        } else {
            // Load an existing diagram.
            load(name,
                 callback);
        }
    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                        .newTopLevelMenu("Save")
                        .respondsWith(getSaveCommand())
                        .endMenu();
        m.newTopLevelMenu(menuDevCommandsBuilder.build()).endMenu();
        return m.build();
    }

    private Command getSaveCommand() {
        return this::validateAndSave;
    }

    private void validateAndSave() {
        final Command save = this::save;
        final DMNEditorToolbar toolbar = (DMNEditorToolbar) presenter.getToolbar();
        toolbar
                .getValidateCommand()
                .execute(new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
                    @Override
                    public void onSuccess() {
                        log(Level.INFO,
                            "Validation success.");
                        save.execute();
                    }

                    @Override
                    public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                        log(Level.WARNING,
                            "Validation failed [violations=" + violations.toString() + "].");
                        // Allow saving when only warnings founds.
                        final Violation.Type maxSeverity = ValidationUtils.getMaxSeverity(violations);
                        if (!maxSeverity.equals(Violation.Type.ERROR)) {
                            save.execute();
                        }
                    }
                });
    }

    private void save() {
        diagramService.save(getSession(),
                            new ServiceCallback<Diagram<Graph, Metadata>>() {
                                @Override
                                public void onSuccess(Diagram<Graph, Metadata> item) {
                                    log(Level.INFO,
                                        "Save operation finished for diagram [" + item.getName() + "].");
                                }

                                @Override
                                public void onError(ClientRuntimeError error) {
                                    showError(error);
                                }
                            });
    }

    private void newDiagram(final String uuid,
                            final String title,
                            final String definitionSetId,
                            final String shapeSetId,
                            final Command callback) {
        BusyPopup.showMessage("Loading");
        final Metadata metadata = buildMetadata(definitionSetId,
                                                shapeSetId,
                                                title);
        clientFactoryServices.newDiagram(uuid,
                                         definitionSetId,
                                         metadata,
                                         new ServiceCallback<Diagram>() {
                                             @Override
                                             public void onSuccess(final Diagram diagram) {
                                                 final Metadata metadata = diagram.getMetadata();
                                                 metadata.setShapeSetId(shapeSetId);
                                                 metadata.setTitle(title);
                                                 open(diagram,
                                                      callback);
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 showError(error);
                                                 callback.execute();
                                             }
                                         });
    }

    private Metadata buildMetadata(final String defSetId,
                                   final String shapeSetId,
                                   final String title) {
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setTitle(title)
                .setShapeSetId(shapeSetId)
                .build();
    }

    private void load(final String name,
                      final Command callback) {
        BusyPopup.showMessage("Loading");
        diagramService.loadByName(name,
                                  new ServiceCallback<Diagram>() {
                                      @Override
                                      public void onSuccess(final Diagram diagram) {
                                          open(diagram,
                                               callback);
                                      }

                                      @Override
                                      public void onError(final ClientRuntimeError error) {
                                          showError(error);
                                          callback.execute();
                                      }
                                  });
    }

    void open(final Diagram diagram,
              final Command callback) {
        screenPanelView.setWidget(presenter.getView());
        layoutHelper.applyLayout(diagram);
        presenter
                .withToolbar(true)
                .withPalette(true)
                .displayNotifications(type -> true)
                .open(diagram,
                      new ScreenPresenterCallback(() -> {
                          final ToolbarStateHandler toolbarStateHandler = new StandaloneToolbarStateHandler((DMNEditorToolbar) presenter.getToolbar());
                          final ExpressionEditorView.Presenter expressionEditor = ((DMNSession) sessionManager.getCurrentSession()).getExpressionEditor();
                          expressionEditor.setToolbarStateHandler(toolbarStateHandler);
                          openDock(presenter.getInstance());
                          callback.execute();
                      }));
    }

    @OnFocus
    public void onFocus() {
        final EditorSession session = presenter.getInstance();
        GWT.log("FOCUS [" + session + "]");
        if (null != session) {
            sessionFocusedEvent.fire(new SessionFocusedEvent(session));
        }
    }

    private boolean isSameSession(final ClientSession other) {
        return null != other && null != getSession() && other.equals(getSession());
    }

    @OnClose
    public void onClose() {
        destroyDock();
        destroySession();
    }

    void openDock(final EditorSession session) {
        decisionNavigatorDock.open();
        decisionNavigatorDock.setupContent(session.getCanvasHandler());
    }

    void destroyDock() {
        decisionNavigatorDock.close();
        decisionNavigatorDock.resetContent();
    }

    void destroySession() {
        presenter.destroy();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return screenPanelView.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "sessionDiagramEditorScreenContext";
    }

    private final class ScreenPresenterCallback implements SessionPresenter.SessionPresenterCallback<Diagram> {

        private final Command callback;

        private ScreenPresenterCallback(final Command callback) {
            this.callback = callback;
        }

        @Override
        public void afterSessionOpened() {

        }

        @Override
        public void afterCanvasInitialized() {

        }

        @Override
        public void onSuccess() {
            BusyPopup.close();
            callback.execute();
        }

        @Override
        public void onError(final ClientRuntimeError error) {
            showError(error);
            callback.execute();
        }
    }

    private void updateTitle(final String title) {
        // Change screen title.
        SessionDiagramEditorScreen.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }

    private EditorSession getSession() {
        return null != presenter ? presenter.getInstance() : null;
    }

    private CanvasHandler getCanvasHandler() {
        return null != getSession() ? getSession().getCanvasHandler() : null;
    }

    private Diagram getDiagram() {
        return null != getCanvasHandler() ? getCanvasHandler().getDiagram() : null;
    }

    private void showError(final ClientRuntimeError error) {
        screenErrorView.showError(error);
        screenPanelView.setWidget(screenErrorView.asWidget());
        log(Level.SEVERE,
            error.toString());
        BusyPopup.close();
    }

    private void onSessionErrorEvent(@Observes OnSessionErrorEvent errorEvent) {
        if (isSameSession(errorEvent.getSession())) {
            showError(errorEvent.getError());
        }
    }

    private void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        if (isSameSession(event.getSession())) {
            final DMNSession session = sessionManager.getCurrentSession();
            final ExpressionEditorView.Presenter expressionEditor = session.getExpressionEditor();
            sessionCommandManager.execute(session.getCanvasHandler(),
                                          new NavigateToExpressionEditorCommand(expressionEditor,
                                                                                presenter,
                                                                                sessionManager,
                                                                                sessionCommandManager,
                                                                                refreshFormPropertiesEvent,
                                                                                event.getNodeUUID(),
                                                                                event.getHasExpression(),
                                                                                event.getHasName()));
        }
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
