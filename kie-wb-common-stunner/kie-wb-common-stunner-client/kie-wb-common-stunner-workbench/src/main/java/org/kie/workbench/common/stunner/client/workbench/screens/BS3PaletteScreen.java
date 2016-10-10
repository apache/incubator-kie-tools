/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.workbench.screens;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.BS3PaletteWidgetImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.CanvasSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@WorkbenchScreen( identifier = BS3PaletteScreen.SCREEN_ID )
public class BS3PaletteScreen {

    public static final String SCREEN_ID = "BS3PaletteScreen";
    public static final String EMPTY_VIEW_BG_COLOR = "#FFFFFF";
    public static final int PADDING_TOP = 50;

    @Inject
    BS3PaletteFactory paletteFactory;

    @Inject
    PlaceManager placeManager;

    private PlaceRequest placeRequest;
    private SessionScreenView sessionScreenView;
    private String shapeSetId;
    private CanvasHandler canvasHandler;
    private BS3PaletteWidget paletteWidget;
    private Menus menu = null;

    @PostConstruct
    public void init() {
        this.sessionScreenView = new SessionScreenViewImpl();
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        // this.menu = makeMenuBar();
        this.shapeSetId = placeRequest.getParameter( "shapeSetId", "" );
        this.sessionScreenView.showEmptySession();
        this.sessionScreenView.setPaddingTop( PADDING_TOP );
        open();
    }

    @OnOpen
    public void onOpen() {
        open();
    }

    @OnClose
    public void onClose() {
        close();
    }

    private void open() {
        if ( null != shapeSetId && shapeSetId.trim().length() > 0 ) {
            this.paletteWidget = paletteFactory
                    .forCanvasHandler( canvasHandler )
                    .newPalette( shapeSetId, null );
            sessionScreenView.showScreenView( paletteWidget.getView() );
            sessionScreenView.setScreenViewBgColor( BS3PaletteWidgetImpl.BG_COLOR );
        }

    }

    private void close() {
        if ( null != paletteWidget ) {
            paletteWidget.unbind();

        }
        this.sessionScreenView.setScreenViewBgColor( EMPTY_VIEW_BG_COLOR );

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
        return sessionScreenView.getView();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "bs3PaletteScreenContext";
    }

    void onCanvasSessionOpened( @Observes SessionOpenedEvent sessionOpenedEvent ) {
        checkNotNull( "sessionOpenedEvent", sessionOpenedEvent );
        doOpenSession( sessionOpenedEvent.getSession() );

    }

    void onCanvasSessionResumed( @Observes SessionResumedEvent sessionResumedEvent ) {
        checkNotNull( "sessionResumedEvent", sessionResumedEvent );
        doOpenSession( sessionResumedEvent.getSession() );
    }

    void onCanvasSessionDisposed( @Observes SessionDisposedEvent sessionDisposedEvent ) {
        checkNotNull( "sessionDisposedEvent", sessionDisposedEvent );
        doDisposeSession();
    }

    void onCanvasSessionPaused( @Observes SessionPausedEvent sessionPausedEvent ) {
        checkNotNull( "sessionPausedEvent", sessionPausedEvent );
        doDisposeSession();
    }

    private void doOpenSession( final CanvasSession canvasSession ) {
        this.canvasHandler = canvasSession.getCanvasHandler();
        this.shapeSetId = canvasHandler.getDiagram().getSettings().getShapeSetId();
        open();

    }

    private void doDisposeSession() {
        // Close the current palette.
        this.shapeSetId = null;
        close();
    }

}
