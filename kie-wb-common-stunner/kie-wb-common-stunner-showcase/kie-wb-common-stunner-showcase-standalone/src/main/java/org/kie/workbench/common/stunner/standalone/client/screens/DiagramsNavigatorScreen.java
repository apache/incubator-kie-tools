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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This screen lists the first 10 diagrams found on the server.
 * It provides an additional screen button for creating new diagrams as well.
 */
// TODO: I18n.
@Dependent
@WorkbenchScreen(identifier = DiagramsNavigatorScreen.SCREEN_ID)
public class DiagramsNavigatorScreen {

    private static Logger LOGGER = Logger.getLogger(DiagramsNavigatorScreen.class.getName());

    public static final String SCREEN_ID = "DiagramsNavigatorScreen";
    public static final String NO_ITEM_SELECTED = "No diagram selected!";

    @Inject
    protected DiagramsNavigator diagramsNavigator;

    @Inject
    protected ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder;

    @Inject
    private ErrorPopupPresenter errorPopupPresenter;

    @Inject
    PlaceManager placeManager;

    private Menus menu = null;
    private String selectedDiagramName;

    @PostConstruct
    public void init() {
        this.selectedDiagramName = null;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.menu = makeMenuBar();
        clear();
    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                        .newTopLevelMenu("Load diagrams from server")
                        .respondsWith(this::show)
                        .endMenu()
                        .newTopLevelMenu("View")
                        .respondsWith(this::view)
                        .endMenu()
                        .newTopLevelMenu("Edit")
                        .respondsWith(this::edit)
                        .endMenu();
        m.newTopLevelMenu(newDiagramMenuItemsBuilder.build("Create",
                                                           "Create a new",
                                                           DiagramsNavigatorScreen.this::create)).endMenu();
        return m.build();
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        clear();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Diagrams Navigator";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return diagramsNavigator.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "diagramsNavigatorScreenContext";
    }

    public void show() {
        diagramsNavigator.show();
    }

    public void view() {
        openSelected(true);
    }

    public void edit() {
        openSelected(false);
    }

    public void clear() {
        diagramsNavigator.clear();
        selectedDiagramName = null;
    }

    void onLoadDiagramEvent(@Observes LoadDiagramEvent loadDiagramEvent) {
        checkNotNull("loadDiagramEvent",
                     loadDiagramEvent);
        this.selectedDiagramName = loadDiagramEvent.getName();
    }

    public void openSelected(final boolean viewMode) {
        if (checkItemSelected()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("name",
                       this.selectedDiagramName);
            open(params,
                 viewMode);
        }
    }

    private boolean checkItemSelected() {
        if (null != selectedDiagramName) {
            return true;
        } else {
            LOGGER.log(Level.WARNING,
                       NO_ITEM_SELECTED);
            errorPopupPresenter.showMessage(NO_ITEM_SELECTED);
            return false;
        }
    }

    protected void open(final Map<String, String> params,
                        final boolean viewMode) {
        PlaceRequest diagramScreenPlaceRequest =
                new DefaultPlaceRequest(viewMode ? SessionDiagramViewerScreen.SCREEN_ID : SessionDiagramEditorScreen.SCREEN_ID,
                                        params);
        placeManager.goTo(diagramScreenPlaceRequest);
    }

    private void create(final ShapeSet shapeSet) {
        final String shapSetName = shapeSet.getDescription();
        final String defSetId = shapeSet.getDefinitionSetId();
        Map<String, String> params = new HashMap<String, String>();
        params.put("defSetId",
                   defSetId);
        params.put("shapeSetId",
                   shapeSet.getId());
        params.put("title",
                   "New " + shapSetName + " diagram");
        open(params,
             false);
    }
}
