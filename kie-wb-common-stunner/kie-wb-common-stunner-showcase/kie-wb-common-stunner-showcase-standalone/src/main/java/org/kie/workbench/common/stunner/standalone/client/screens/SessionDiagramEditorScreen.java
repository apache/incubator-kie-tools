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

package org.kie.workbench.common.stunner.standalone.client.screens;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ValidationUtils;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.logging.Level.FINE;

// TODO: i18n.
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
    private final SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final MenuDevCommandsBuilder menuDevCommandsBuilder;
    private final ScreenPanelView screenPanelView;
    private final ScreenErrorView screenErrorView;

    private SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter;
    private PlaceRequest placeRequest;
    private String title = "Authoring Screen";
    private Menus menu = null;

    @Inject
    public SessionDiagramEditorScreen(final DefinitionManager definitionManager,
                                      final ClientFactoryService clientFactoryServices,
                                      final ShowcaseDiagramService diagramService,
                                      final SessionManager sessionManager,
                                      final SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory,
                                      final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                      final MenuDevCommandsBuilder menuDevCommandsBuilder,
                                      final ScreenPanelView screenPanelView,
                                      final ScreenErrorView screenErrorView) {
        this.definitionManager = definitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.diagramService = diagramService;
        this.sessionManager = sessionManager;
        this.sessionPresenterFactory = sessionPresenterFactory;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
        this.screenPanelView = screenPanelView;
        this.screenErrorView = screenErrorView;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
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
        if (menuDevCommandsBuilder.isEnabled()) {
            m.newTopLevelMenu(menuDevCommandsBuilder.build()).endMenu();
        }
        return m.build();
    }

    private Command getSaveCommand() {
        return this::validateAndSave;
    }

    private void validateAndSave() {
        final Command save = this::save;
        final EditorToolbar toolbar = (EditorToolbar) presenter.getToolbar();
        toolbar
                .getValidateToolbarCommand()
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
                                                 openDiagram(diagram,
                                                             callback);
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 showError(error);
                                                 callback.execute();
                                             }
                                         });
    }

    private void openDiagram(Diagram diagram,
                             Command callback) {
        final Metadata metadata = diagram.getMetadata();
        sessionManager.getSessionFactory(metadata,
                                         ClientFullSession.class)
                .newSession(metadata,
                            s -> {
                                final AbstractClientFullSession session = (AbstractClientFullSession) s;
                                presenter = sessionPresenterFactory.newPresenterEditor();
                                screenPanelView.setWidget(presenter.getView());
                                presenter
                                        .withToolbar(true)
                                        .withPalette(true)
                                        .displayNotifications(type -> true)
                                        .open(diagram,
                                              session,
                                              new ScreenPresenterCallback(callback));
                            });
    }

    private Metadata buildMetadata(final String defSetId,
                                   final String shapeSetId,
                                   final String title) {
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setTitle(title)
                .setRoot(PathFactory.newPath(".", "default://master@stunner/"))
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
                                          openDiagram(diagram,
                                                      callback);
                                      }

                                      @Override
                                      public void onError(final ClientRuntimeError error) {
                                          showError(error);
                                          callback.execute();
                                      }
                                  });
    }

    @OnOpen
    public void onOpen() {
        resume();
    }

    @OnFocus
    public void onFocus() {
        if (null != getSession() && !isSameSession(sessionManager.getCurrentSession())) {
            sessionManager.open(getSession());
        } else if (null != getSession()) {
            log(FINE,
                "Session already active, no action.");
        }
    }

    private boolean isSameSession(final ClientSession other) {
        return null != other && null != getSession() && other.equals(getSession());
    }

    @OnLostFocus
    public void OnLostFocus() {

    }

    @OnClose
    public void onClose() {
        destroySession();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    private void resume() {
        if (null != getSession()) {
            sessionManager.resume(getSession());
        }
    }

    private void destroySession() {
        presenter.destroy();
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

    private final class ScreenPresenterCallback implements SessionPresenter.SessionPresenterCallback<AbstractClientFullSession, Diagram> {

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

    private AbstractClientFullSession getSession() {
        return null != presenter ? presenter.getInstance() : null;
    }

    private CanvasHandler getCanvasHandler() {
        return null != sessionManager.getCurrentSession() ? sessionManager.getCurrentSession().getCanvasHandler() : null;
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

    void onSessionErrorEvent(@Observes OnSessionErrorEvent errorEvent) {
        if (isSameSession(errorEvent.getSession())) {
            showError(errorEvent.getError());
            // TODO executeWithConfirm( "An error happened [" + errorEvent.getError() + "]. Do you want" +
            //         "to refresh the diagram (Last changes can be lost)? ", this::menu_refresh );
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
