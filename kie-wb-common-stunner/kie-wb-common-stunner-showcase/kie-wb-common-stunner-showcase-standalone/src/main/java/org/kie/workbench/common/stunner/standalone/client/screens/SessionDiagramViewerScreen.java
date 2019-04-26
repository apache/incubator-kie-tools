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

import java.util.ArrayList;
import java.util.function.Consumer;
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
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
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
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.logging.Level.SEVERE;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

// TODO: i18n.
@Dependent
@WorkbenchScreen(identifier = SessionDiagramViewerScreen.SCREEN_ID)
public class SessionDiagramViewerScreen {

    private static Logger LOGGER = Logger.getLogger(SessionDiagramViewerScreen.class.getName());

    public static final String SCREEN_ID = "SessionDiagramViewerScreen";

    private final ShowcaseDiagramService diagramLoader;
    private final SessionViewerPresenter<ViewerSession> presenter;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final MenuDevCommandsBuilder menuDevCommandsBuilder;
    private final ScreenPanelView screenPanelView;
    private final ScreenErrorView screenErrorView;

    private PlaceRequest placeRequest;
    private String title = "Viewer Screen";
    private Menus menu = null;

    @Inject
    public SessionDiagramViewerScreen(final ShowcaseDiagramService diagramLoader,
                                      final SessionViewerPresenter<ViewerSession> presenter,
                                      final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                      final MenuDevCommandsBuilder menuDevCommandsBuilder,
                                      final ScreenPanelView screenPanelView,
                                      final ScreenErrorView screenErrorView) {
        this.diagramLoader = diagramLoader;
        this.presenter = presenter;
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
        if (isCreate) {
            LOGGER.log(SEVERE,
                       "This screen it's just read only!");
        } else {
            // Load an existing diagram.
            load(name,
                 () -> {
                     final Diagram diagram = getDiagram();
                     if (null != diagram) {
                         // Update screen title.
                         updateTitle(diagram.getMetadata().getTitle());
                     }
                 });
        }
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu("Dev")
                .withItems(new ArrayList<MenuItem>(1) {{
                    add(menuDevCommandsBuilder.build());
                }})
                .endMenu()
                .build();
    }

    private void load(final String name,
                      final Command callback) {
        BusyPopup.showMessage("Loading");
        diagramLoader.loadByName(name,
                                 new ServiceCallback<Diagram>() {
                                     @Override
                                     public void onSuccess(final Diagram diagram) {
                                         open(diagram,
                                              callback);
                                     }

                                     @Override
                                     public void onError(final ClientRuntimeError error) {
                                         SessionDiagramViewerScreen.this.showError(error);
                                         callback.execute();
                                     }
                                 });
    }

    private void open(final Diagram diagram,
                      final Command callback) {
        screenPanelView.setWidget(presenter.getView());
        presenter
                .withToolbar(true)
                .withPalette(false)
                .displayNotifications(type -> true)
                .open(diagram,
                      new SessionPresenter.SessionPresenterCallback<Diagram>() {
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
                              SessionDiagramViewerScreen.this.showError(error);
                              callback.execute();
                          }
                      });
    }

    private void updateTitle(final String title) {
        // Change screen title.
        SessionDiagramViewerScreen.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }

    @OnOpen
    public void onOpen() {
    }

    @OnFocus
    public void onFocus() {
    }

    private boolean isSameSession(final ClientSession other) {
        checkNotNull("session",
                     getSession());
        return null != other && getSession().equals(other);
    }

    @OnLostFocus
    public void OnLostFocus() {
    }

    @OnClose
    public void onClose() {
        presenter.destroy();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
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
        return "sessionDiagramViewerScreenContext";
    }

    private ViewerSession getSession() {
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

    void onSessionErrorEvent(@Observes OnSessionErrorEvent errorEvent) {
        if (null != getSession() && isSameSession(errorEvent.getSession())) {
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
