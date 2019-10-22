/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.standalone.client.editor;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = BPMNDiagramsNavigatorScreen.SCREEN_ID)
public class BPMNDiagramsNavigatorScreen {

    public static final String SCREEN_ID = "BPMNDiagramsNavigatorScreen";

    public static final PlaceRequest DIAGRAM_EDITOR = new DefaultPlaceRequest(BPMNStandaloneDiagramEditor.EDITOR_ID);

    private DiagramsNavigator diagramsNavigator;
    private ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder;
    private BPMNStandaloneDiagramWrapper stateHolder;

    @Inject
    @DiagramEditor
    private BPMNStandaloneDiagramEditor diagramEditor;

    private Menus menu = null;
    private LoadDiagramEvent selectedDiagramEvent = null;

    public BPMNDiagramsNavigatorScreen() {
        //CDI proxy
    }

    @Inject
    public BPMNDiagramsNavigatorScreen(final DiagramsNavigator diagramsNavigator,
                                       final ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder,
                                       final BPMNStandaloneDiagramWrapper stateHolder) {
        this.diagramsNavigator = diagramsNavigator;
        this.newDiagramMenuItemsBuilder = newDiagramMenuItemsBuilder;
        this.stateHolder = stateHolder;
    }

    @PostConstruct
    public void init() {
        this.selectedDiagramEvent = null;
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest placeRequest) {
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
                        .respondsWith(() -> {
                            if (Objects.nonNull(selectedDiagramEvent)) {
                                stateHolder.openFile(selectedDiagramEvent.getPath());
                            }
                        })
                        .endMenu();
        m.newTopLevelMenu(newDiagramMenuItemsBuilder.build("Create",
                                                           "Create a new",
                                                           (shapeSet) -> stateHolder.newFile())).endMenu();
        return m.build();
    }

    private void clear() {
        diagramsNavigator.clear();
        selectedDiagramEvent = null;
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

    private void onLoadDiagramEvent(final @Observes LoadDiagramEvent loadDiagramEvent) {
        this.selectedDiagramEvent = loadDiagramEvent;
    }
}