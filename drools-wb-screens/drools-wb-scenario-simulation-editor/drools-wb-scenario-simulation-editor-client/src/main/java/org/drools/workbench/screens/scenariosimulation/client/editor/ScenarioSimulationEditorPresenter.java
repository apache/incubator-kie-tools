/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioSimulationViewProvider;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.RightPanelMenuItem;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter.IDENTIFIER;

@Dependent
@WorkbenchEditor(identifier = IDENTIFIER, supportedTypes = {ScenarioSimulationResourceType.class})
public class ScenarioSimulationEditorPresenter
        extends KieEditor<ScenarioSimulationModel> {

    public static final String IDENTIFIER = "ScenarioSimulationEditor";

    private ImportsWidgetPresenter importsWidget;

    private AsyncPackageDataModelOracleFactory oracleFactory;

    private ScenarioSimulationModel model;
    private Caller<ScenarioSimulationService> service;

    private ScenarioSimulationResourceType type;

    private AsyncPackageDataModelOracle oracle;

    private ScenarioSimulationView view;

    @Inject
    private RightPanelMenuItem rightPanelMenuItem;

    public ScenarioSimulationEditorPresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorPresenter(final Caller<ScenarioSimulationService> service,
                                             final ScenarioSimulationResourceType type,
                                             final ImportsWidgetPresenter importsWidget,
                                             final AsyncPackageDataModelOracleFactory oracleFactory,
                                             final PlaceManager placeManager) {
        super();
        this.view = newScenarioSimulationView();   // Indirection added for test-purpose
        this.baseView = view;
        this.service = service;
        this.type = type;
        this.importsWidget = importsWidget;
        this.oracleFactory = oracleFactory;
        this.placeManager = placeManager;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
        view.getScenarioGridPanel().getDefaultGridLayer().enterPinnedMode(view.getScenarioGridPanel().getScenarioGrid(), () -> {
        });  // Hack to overcome default implementation
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
        if (PlaceStatus.OPEN.equals(placeManager.getStatus(RightPanelPresenter.IDENTIFIER))) {
            placeManager.closePlace(RightPanelPresenter.IDENTIFIER);
            this.getView().showLoading();
        }
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(model);
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    // Observing to show RightPanel when ScenarioSimulationScreen is put in foreground
    public void onPlaceGainFocusEvent(@Observes PlaceGainFocusEvent placeGainFocusEvent) {
        PlaceRequest placeRequest = placeGainFocusEvent.getPlace();
        if (placeRequest.getIdentifier().equals(ScenarioSimulationEditorPresenter.IDENTIFIER) && PlaceStatus.CLOSE.equals(placeManager.getStatus(RightPanelPresenter.IDENTIFIER))) {
            placeManager.goTo(RightPanelPresenter.IDENTIFIER);
        }
    }

    // Observing to hide RightPanel when ScenarioSimulationScreen is put in background
    public void onPlaceHiddenEvent(@Observes PlaceHiddenEvent placeHiddenEvent) {
        PlaceRequest placeRequest = placeHiddenEvent.getPlace();
        if (placeRequest.getIdentifier().equals(ScenarioSimulationEditorPresenter.IDENTIFIER) && PlaceStatus.OPEN.equals(placeManager.getStatus(RightPanelPresenter.IDENTIFIER))) {
            placeManager.closePlace(RightPanelPresenter.IDENTIFIER);
        }
    }

    public ScenarioSimulationView getView() {
        return view;
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected void makeMenuBar() {
        super.makeMenuBar();
        addRightPanelMenuItem(fileMenuBuilder);
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return () -> model;
    }

    @Override
    protected void save(final String commitMessage) {
        service.call(getSaveSuccessCallback(model.hashCode()),
                     new HasBusyIndicatorDefaultErrorCallback(baseView)).save(versionRecordManager.getCurrentPath(),
                                                                              model,
                                                                              metadata,
                                                                              commitMessage);
    }

    @Override
    protected void addCommonActions(final FileMenuBuilder fileMenuBuilder) {
        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    protected void loadContent() {
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    // Needed by test
    protected ScenarioSimulationView newScenarioSimulationView() {
        return ScenarioSimulationViewProvider.newScenarioSimulationView();
    }

    private RemoteCallback<ScenarioSimulationModelContent> getModelSuccessCallback() {
        return content -> {
            //Path is set to null when the Editor is closed (which can happen before async calls complete).
            if (versionRecordManager.getCurrentPath() == null) {
                return;
            }
            resetEditorPages(content.getOverview());
            model = content.getModel();
            oracle = oracleFactory.makeAsyncPackageDataModelOracle(versionRecordManager.getCurrentPath(),
                                                                   model,
                                                                   content.getDataModel());
            importsWidget.setContent(oracle,
                                     model.getImports(),
                                     isReadOnly);
            addImportsTab(importsWidget);
            baseView.hideBusyIndicator();
            view.setContent(model.getHeadersMap(), model.getRowsMap());
            createOriginalHash(model.hashCode());
        };
    }

    private void addRightPanelMenuItem(final FileMenuBuilder fileMenuBuilder) {
        fileMenuBuilder.addNewTopLevelMenu(rightPanelMenuItem);
    }
}
