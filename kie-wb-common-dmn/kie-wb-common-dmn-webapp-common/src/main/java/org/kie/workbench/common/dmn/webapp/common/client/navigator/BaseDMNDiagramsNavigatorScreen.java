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
package org.kie.workbench.common.dmn.webapp.common.client.navigator;

import java.util.function.Consumer;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

public abstract class BaseDMNDiagramsNavigatorScreen {

    public static final String SCREEN_ID = "DMNDiagramsNavigatorScreen";

    protected DiagramsNavigator diagramsNavigator;
    protected ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder;

    protected Menus menu = null;
    protected LoadDiagramEvent selectedDiagramEvent = null;

    public BaseDMNDiagramsNavigatorScreen() {
        //CDI proxy
    }

    public BaseDMNDiagramsNavigatorScreen(final DiagramsNavigator diagramsNavigator,
                                          final ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder) {
        this.diagramsNavigator = diagramsNavigator;
        this.newDiagramMenuItemsBuilder = newDiagramMenuItemsBuilder;
    }

    protected void init() {
        this.selectedDiagramEvent = null;
    }

    @SuppressWarnings("unused")
    protected void onStartup(final PlaceRequest placeRequest) {
        this.menu = makeMenuBar();
        clear();
    }

    private Menus makeMenuBar() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> m =
                MenuFactory
                        .newTopLevelMenu("Load diagrams from server")
                        .respondsWith(() -> diagramsNavigator.show())
                        .endMenu()
                        .newTopLevelMenu("Edit")
                        .respondsWith(this::edit)
                        .endMenu();
        m.newTopLevelMenu(newDiagramMenuItemsBuilder.build("Create",
                                                           "Create a new",
                                                           this::create)).endMenu();
        return m.build();
    }

    protected abstract void edit();

    protected abstract void create(final ShapeSet shapeSet);

    private void clear() {
        diagramsNavigator.clear();
        selectedDiagramEvent = null;
    }

    protected void onClose() {
        clear();
    }

    protected void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
    }

    protected String getTitle() {
        return "Diagrams Navigator";
    }

    protected IsWidget getWidget() {
        return diagramsNavigator.asWidget();
    }

    protected void onLoadDiagramEvent(final LoadDiagramEvent loadDiagramEvent) {
        this.selectedDiagramEvent = loadDiagramEvent;
    }
}