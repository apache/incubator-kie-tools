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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

// TODO: I18n.
@Dependent
@WorkbenchScreen( identifier = NavigatorScreen.SCREEN_ID )
public class NavigatorScreen {

    public static final String SCREEN_ID = "NavigatorScreen";

    @Inject
    DiagramsNavigator diagramsNavigator;
    ;

    @Inject
    ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder;

    @Inject
    PlaceManager placeManager;

    private Menus menu = null;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.menu = makeMenuBar();
        diagramsNavigator.clear();
    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                        .newTopLevelMenu( "Load" )
                        .respondsWith( getLoadDiagramsCommand() )
                        .endMenu();
        m.newTopLevelMenu( newDiagramMenuItemsBuilder.build( "Create",
                                                             "Create a new",
                                                             NavigatorScreen.this::create ) ).endMenu();
        return m.build();
    }

    private Command getLoadDiagramsCommand() {
        return () -> diagramsNavigator.show();
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        diagramsNavigator.clear();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Stunner - Home";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return diagramsNavigator.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "navigatorScreenContext";
    }

    void onLoadDiagramEvent( @Observes LoadDiagramEvent loadDiagramEvent ) {
        checkNotNull( "loadDiagramEvent",
                      loadDiagramEvent );
        final String name = loadDiagramEvent.getName();
        Map<String, String> params = new HashMap<String, String>();
        params.put( "name",
                    name );
        open( params );
    }

    private void create( final ShapeSet shapeSet ) {
        final String shapSetName = shapeSet.getName();
        final String defSetId = shapeSet.getDefinitionSetId();
        Map<String, String> params = new HashMap<String, String>();
        params.put( "defSetId",
                    defSetId );
        params.put( "shapeSetId",
                    shapeSet.getId() );
        params.put( "title",
                    "New " + shapSetName + " diagram" );
        open( params );
    }

    private void open( final Map<String, String> params ) {
        PlaceRequest diagramScreenPlaceRequest = new DefaultPlaceRequest( DiagramScreen.SCREEN_ID,
                                                                          params );
        placeManager.goTo( diagramScreenPlaceRequest );
    }
}
