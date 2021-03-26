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
package org.kie.workbench.common.dmn.showcase.client.navigator;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.showcase.client.alternatives.DMNClientModels;
import org.kie.workbench.common.dmn.showcase.client.feel.FEELDemoEditor;
import org.kie.workbench.common.dmn.webapp.common.client.navigator.BaseDMNDiagramsNavigatorScreen;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.client.widgets.event.LoadDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams.DiagramsNavigator;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.ShapeSetsMenuItemsBuilder;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = BaseDMNDiagramsNavigatorScreen.SCREEN_ID)
public class DMNDiagramsNavigatorScreen extends BaseDMNDiagramsNavigatorScreen implements KogitoScreen {

    private static final PlaceRequest DMN_KOGITO_TESTING_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(DMNDiagramsNavigatorScreen.SCREEN_ID);

    private final DMNVFSService vfsService;

    private final org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService contentService;

    private final PlaceManager placeManager;

    public DMNDiagramsNavigatorScreen() {
        this(null, null, null, null, null);
        // CDI proxy
    }

    @Inject
    public DMNDiagramsNavigatorScreen(final DiagramsNavigator diagramsNavigator,
                                      final ShapeSetsMenuItemsBuilder newDiagramMenuItemsBuilder,
                                      final DMNVFSService vfsService,
                                      final KogitoResourceContentService contentService,
                                      final PlaceManager placeManager) {
        super(diagramsNavigator,
              newDiagramMenuItemsBuilder);
        this.vfsService = vfsService;
        this.contentService = contentService;
        this.placeManager = placeManager;
    }

    @Override
    public PlaceRequest getPlaceRequest() {
        return DMN_KOGITO_TESTING_SCREEN_DEFAULT_REQUEST;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest placeRequest) {
        super.onStartup(placeRequest);
    }

    @Override
    public void edit() {
        if (Objects.nonNull(selectedDiagramEvent)) {
            vfsService.openFile(selectedDiagramEvent.getPath());
        }
    }

    @Override
    protected MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> createMenuBuilder() {
        return superCreateMenuBuilder()
                .newTopLevelMenu("FEEL Editor")
                .respondsWith(onFeelEditorClick())
                .endMenu()
                .newTopLevelMenu("Load diagrams from client")
                .respondsWith(onLoadFromClientClick())
                .order(-1)
                .endMenu();
    }

    Command onFeelEditorClick() {
        return () -> placeManager.goTo(FEELDemoEditor.EDITOR_ID);
    }

    Command onLoadFromClientClick() {
        return () -> contentService.getFilteredItems("**/*.dmn", getItems(), getErrorCallback());
    }

    MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> superCreateMenuBuilder() {
        return super.createMenuBuilder();
    }

    private ErrorCallback<Object> getErrorCallback() {
        return null;
    }

    RemoteCallback<List<String>> getItems() {
        return response -> vfsService.openFile("something.dmn", DMNClientModels.MODEL_WITH_IMPORTS);
    }

    @Override
    public void create(final ShapeSet shapeSet) {
        vfsService.newFile();
    }

    @Override
    @OnClose
    public void onClose() {
        super.onClose();
    }

    @Override
    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @Override
    public void onLoadDiagramEvent(final @Observes LoadDiagramEvent loadDiagramEvent) {
        super.onLoadDiagramEvent(loadDiagramEvent);
    }
}
