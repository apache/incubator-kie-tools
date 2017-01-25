/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.screens;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This screen wraps the BS3Palette widget.
 * It is session aware - It shows the palette view for the current session's diagram.
 * Only enabled when using full sessions.
 */
@Dependent
@WorkbenchScreen(identifier = SessionPaletteScreen.SCREEN_ID)
public class SessionPaletteScreen {

    private static Logger LOGGER = Logger.getLogger(SessionPaletteScreen.class.getName());

    public static final String SCREEN_ID = "SessionPaletteScreen";
    public static final String EMPTY_VIEW_BG_COLOR = "#FFFFFF";
    public static final int PADDING_TOP = 50;

    @Inject
    SessionManager clientSessionManager;

    @Inject
    ShapeManager shapeManager;

    @Inject
    BS3PaletteFactory paletteFactory;

    @Inject
    SessionScreenView sessionScreenView;

    @Inject
    PlaceManager placeManager;

    private PlaceRequest placeRequest;
    private BS3PaletteWidget paletteWidget;
    private Menus menu = null;
    private ClientFullSession session = null;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        this.sessionScreenView.showEmptySession();
        this.sessionScreenView.setPaddingTop(PADDING_TOP);
    }

    @OnOpen
    public void onOpen() {
        open(session);
    }

    @OnClose
    public void onClose() {
        close();
    }

    private void open(final ClientFullSession session) {
        this.session = session;
        log(Level.INFO,
            "Opening palette screen...");
        if (null != getDiagram() && !isAlreadyOpen(getDiagram())) {
            final Diagram diagram = getDiagram();
            final String ssid = diagram.getMetadata().getShapeSetId();
            this.paletteWidget = paletteFactory.newPalette(ssid,
                                                           session.getCanvasHandler());
            log(Level.INFO,
                "Palette built for shape set [" + ssid + "]");
            sessionScreenView.showScreenView(paletteWidget.getView());
            sessionScreenView.setScreenViewBgColor(BS3PaletteWidgetImpl.BG_COLOR);
        } else {
            log(Level.INFO,
                "Palette not built as no session present or it is already open.");
        }
    }

    private void close() {
        log(Level.INFO,
            "Closing palette screen...");
        if (null != paletteWidget) {
            paletteWidget.unbind();
            log(Level.INFO,
                "Palette unbind.");
        }
        this.sessionScreenView.setScreenViewBgColor(EMPTY_VIEW_BG_COLOR);
        this.session = null;
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return sessionScreenView;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "sessionPaletteScreenContext";
    }

    void onCanvasSessionOpened(final @Observes SessionOpenedEvent sessionOpenedEvent) {
        checkNotNull("sessionOpenedEvent",
                     sessionOpenedEvent);
        doOpenSession(sessionOpenedEvent.getSession());
    }

    void onCanvasSessionDestroyed(final @Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent",
                     sessionDestroyedEvent);
        log(Level.INFO,
            "Disposing session...");
        doDestroySession();
    }

    void onSessionDiagramOpenedEvent(final @Observes SessionDiagramOpenedEvent sessionDiagramOpenedEvent) {
        checkNotNull("sessionDiagramOpenedEvent",
                     sessionDiagramOpenedEvent);
        if (null != getCanvas() && getCanvas().equals(sessionDiagramOpenedEvent.getSession().getCanvas())) {
            // Force to reload current session, for example, when a new diagram is just created.
            open(session);
        }
    }

    private Canvas getCanvas() {
        return null != session ? session.getCanvas() : null;
    }

    private CanvasHandler getCanvasHandler() {
        return null != session ? session.getCanvasHandler() : null;
    }

    private Diagram getDiagram() {
        return null != getCanvasHandler() ? getCanvasHandler().getDiagram() : null;
    }

    private boolean isAlreadyOpen(final Diagram diagram) {
        return null != paletteWidget
                && null != paletteWidget.getDefinition()
                && null != paletteWidget.getDefinition().getDefinitionSetId()
                && paletteWidget.getDefinition().getDefinitionSetId().equals(diagram.getMetadata().getDefinitionSetId());
    }

    private void doOpenSession(final ClientSession session) {
        if (session instanceof ClientFullSession) {
            log(Level.INFO,
                "Trying to open session [" + (null != session ? session.toString() : "null") + "]...");
            open((ClientFullSession) session);
        } else {
            log(Level.INFO,
                "No session palette enabled as session is not instance of full session");
        }
    }

    private void doDestroySession() {
        close();
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
