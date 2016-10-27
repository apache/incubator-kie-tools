/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.client.screens;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.event.CreateEmptyDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.navigation.home.HomeNavigationWidget;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@WorkbenchScreen( identifier = HomeNavigatorScreen.SCREEN_ID )
public class HomeNavigatorScreen {

    public static final String SCREEN_ID = "HomeNavigatorScreen";
    // TODO: Do not hardcode it.
    private static final String AUTHORING_PERSPECTIVE_ID = "AuthoringPerspective";

    @Inject
    HomeNavigationWidget homeNavigationWidget;

    @Inject
    PlaceManager placeManager;

    @Inject
    ShapeManager shapeManager;

    private Menus menu = null;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.menu = makeMenuBar();
        homeNavigationWidget.show();
    }

    private Menus makeMenuBar() {
        return null;
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        homeNavigationWidget.clear();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    // TODO: I18n.
    @WorkbenchPartTitle
    public String getTitle() {
        return "Stunner Home";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return homeNavigationWidget.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "homeNavigatorScreenContext";
    }

    void onCreateEmptyDiagramEvent( @Observes CreateEmptyDiagramEvent createEmptyDiagramEvent ) {
        checkNotNull( "createEmptyDiagramEvent", createEmptyDiagramEvent );
        final String shapeSetId = createEmptyDiagramEvent.getShapeSetId();
        final ShapeSet shapeSet = getShapeSet( shapeSetId );
        final String shapSetName = shapeSet.getName();
        final String defSetId = shapeSet.getDefinitionSetId();
        Map<String, String> params = new HashMap<String, String>();
        params.put( "defSetId", defSetId );
        params.put( "shapeSetId", shapeSetId );
        params.put( "title", "New " + shapSetName + " diagram" );
        PlaceRequest placeRequest = new DefaultPlaceRequest( AUTHORING_PERSPECTIVE_ID, params );
        placeManager.goTo( placeRequest );
    }

    void onLoadDiagramEvent( @Observes LoadDiagramEvent loadDiagramEvent ) {
        checkNotNull( "loadDiagramEvent", loadDiagramEvent );
        final String name = loadDiagramEvent.getName();
        Map<String, String> params = new HashMap<String, String>();
        params.put( "name", name );
        PlaceRequest placeRequest = new DefaultPlaceRequest( AUTHORING_PERSPECTIVE_ID, params );
        placeManager.goTo( placeRequest );
    }

    private ShapeSet getShapeSet( final String id ) {
        for ( final ShapeSet set : shapeManager.getShapeSets() ) {
            if ( set.getId().equals( id ) ) {
                return set;
            }
        }
        return null;
    }

}
