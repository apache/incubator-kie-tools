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
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;

/**
 * This screen provides a preview for a diagram.
 * It is session aware - It shows the preview for the current session's diagram.
 */
// TODO: i18n.
@Dependent
@WorkbenchScreen(identifier = SessionDiagramPreviewScreen.SCREEN_ID)
public class SessionDiagramPreviewScreen extends AbstractSessionScreen {

    private static Logger LOGGER = Logger.getLogger(SessionDiagramPreviewScreen.class.getName());

    public static final String SCREEN_ID = "SessionDiagramPreviewScreen";
    public static final String TITLE = "Preview";
    public static final int WIDTH = 420;
    public static final int HEIGHT = 280;

    private final ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;
    private final SessionScreenView view;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    private PlaceRequest placeRequest;
    private String title = TITLE;
    private Menus menu;
    private SessionDiagramPreview<AbstractSession> preview;

    @Inject
    public SessionDiagramPreviewScreen(final @Any @Default ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews,
                                       final SessionScreenView view,
                                       final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent) {
        this.sessionPreviews = sessionPreviews;
        this.view = view;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
    }

    @PostConstruct
    public void init() {
        view.showEmptySession();
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        this.menu = makeMenuBar();
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        close();
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
        return view;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "sessionDiagramPreviewScreenContext";
    }

    @Override
    protected void doOpenDiagram() {
        final AbstractSession session = getSession();
        preview = sessionPreviews.get();
        preview.open(session,
                     WIDTH,
                     HEIGHT,
                     new SessionViewer.SessionViewerCallback<Diagram>() {
                         @Override
                         public void afterCanvasInitialized() {
                         }

                         @Override
                         public void onSuccess() {
                             LOGGER.log(FINE,
                                        "Session's preview completed for [" + session + "]");
                             view.showScreenView(preview.getView());
                         }

                         @Override
                         public void onError(final ClientRuntimeError error) {
                             LOGGER.log(SEVERE,
                                        "Error while showing session preview for [" + session + "]. " +
                                                "Error=[" + error + "]");
                         }
                     });
    }

    @Override
    protected void doCloseSession() {
        view.showEmptySession();
        preview.destroy();
        sessionPreviews.destroy(preview);
        preview = null;
    }

    @Override
    protected void doUpdateTitle(final String title) {
        // Change screen title.
        SessionDiagramPreviewScreen.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }

    private void refresh() {
        final AbstractSession session = getSession();
        open(session);
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu("Refresh")
                .withItems(new ArrayList<MenuItem>(1) {{
                    add(buildRefreshMenuItem());
                }})
                .endMenu()
                .build();
    }

    private MenuItem buildRefreshMenuItem() {
        return MenuUtils.buildItem(new Button() {{
            setIcon(IconType.REFRESH);
            setSize(ButtonSize.SMALL);
            setTitle("Refresh");
            addClickHandler(e -> SessionDiagramPreviewScreen.this.refresh());
        }});
    }
}
