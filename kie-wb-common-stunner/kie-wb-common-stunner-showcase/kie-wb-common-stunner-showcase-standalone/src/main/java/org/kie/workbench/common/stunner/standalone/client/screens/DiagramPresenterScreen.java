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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramPresenterFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenErrorView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * This screen wraps the diagram viewer & editor widgets
 * and provides the menu items for both loading or editing a diagram.
 */
@Dependent
@WorkbenchScreen(identifier = DiagramPresenterScreen.SCREEN_ID)
public class DiagramPresenterScreen {

    private static Logger LOGGER = Logger.getLogger(DiagramPresenterScreen.class.getName());

    public static final String SCREEN_ID = "DiagramPresenterScreen";
    public static final String DIAGRAM_NAME = "evaluation2";

    /**
     * A loader helper instance for loading the diagram that will be used by <code>diagramViewer</code>
     */
    @Inject
    ShowcaseDiagramService diagramLoader;

    /**
     * The diagram presenter factory instance.
     */
    @Inject
    DiagramPresenterFactory<Diagram> diagramPresenterFactory;

    @Inject
    ScreenPanelView screenPanelView;

    @Inject
    ScreenErrorView screenErrorView;

    private Menus menu = null;

    private Viewer<Diagram, ?, ?, ?> presenter;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.menu = makeMenuBar();
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu("View " + DIAGRAM_NAME)
                .respondsWith(this::show)
                .endMenu()
                .newTopLevelMenu("Edit " + DIAGRAM_NAME)
                .respondsWith(this::edit)
                .endMenu()
                .build();
    }

    private void show() {
        Logger.getLogger("org.kie.workbench.common.stunner").setLevel(Level.FINE);
        BusyPopup.showMessage("Loading");
        destroy();
        diagramLoader.loadByName(DIAGRAM_NAME,
                                 new ServiceCallback<Diagram>() {
                                     @Override
                                     public void onSuccess(final Diagram diagram) {
                                         final DiagramViewer<Diagram, ?> diagramViewer =
                                                 diagramPresenterFactory.newViewer(diagram);
                                         screenPanelView.setWidget(diagramViewer.getView());
                                         DiagramPresenterScreen.this.presenter = diagramViewer;
                                         diagramViewer.open(diagram,
                                                            new ScreenViewerCallback());
                                     }

                                     @Override
                                     public void onError(final ClientRuntimeError error) {
                                         showError(error);
                                     }
                                 });
    }

    private void edit() {
        Logger.getLogger("org.kie.workbench.common.stunner").setLevel(Level.FINE);
        BusyPopup.showMessage("Loading");
        destroy();
        diagramLoader.loadByName(DIAGRAM_NAME,
                                 new ServiceCallback<Diagram>() {
                                     @Override
                                     public void onSuccess(final Diagram diagram) {
                                         final DiagramEditor<Diagram, ?> diagramEditor =
                                                 diagramPresenterFactory.newEditor(diagram);
                                         screenPanelView.setWidget(diagramEditor.getView());
                                         DiagramPresenterScreen.this.presenter = diagramEditor;
                                         diagramEditor.open(diagram,
                                                            new ScreenViewerCallback());
                                     }

                                     @Override
                                     public void onError(ClientRuntimeError error) {
                                         showError(error);
                                     }
                                 });
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        clear();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Diagram Presenter";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return screenPanelView;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "diagramPresenterScreenContext";
    }

    private final class ScreenViewerCallback implements DiagramViewer.DiagramViewerCallback<Diagram> {

        @Override
        public void afterCanvasInitialized() {
        }

        @Override
        public void onSuccess() {
            LOGGER.log(Level.FINE,
                       DIAGRAM_NAME + " loaded!.");
            BusyPopup.close();
        }

        @Override
        public void onError(final ClientRuntimeError error) {
            showError(error);
        }
    }

    private void clear() {
        if (null != presenter) {
            presenter.clear();
        }
    }

    private void destroy() {
        if (null != presenter) {
            presenter.destroy();
        }
    }

    private void showError(final ClientRuntimeError error) {
        screenErrorView.showError(error);
        screenPanelView.setWidget(screenErrorView.asWidget());
        log(Level.SEVERE,
            DIAGRAM_NAME + " cannot be loaded! [Error=" + error + "]");
        BusyPopup.close();
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
