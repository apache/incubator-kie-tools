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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.strategies.BusinessCentralDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.strategies.BusinessCentralDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.handlers.ScenarioSimulationBusinessCentralDocksHandler;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageReportPresenter;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageReportView;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorWrapper;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportService;
import org.drools.workbench.screens.scenariosimulation.service.RunnerReportService;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter.IDENTIFIER;
import static org.drools.workbench.screens.scenariosimulation.service.ImportExportType.CSV;

@Dependent
@WorkbenchEditor(identifier = IDENTIFIER, supportedTypes = {ScenarioSimulationResourceType.class})
/**
 * Wrapper to be used inside Business Central
 */
public class ScenarioSimulationEditorBusinessCentralWrapper extends KieEditor<ScenarioSimulationModel> implements ScenarioSimulationEditorWrapper {

    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;

    private ImportsWidgetPresenter importsWidget;
    private AsyncPackageDataModelOracleFactory oracleFactory;
    private Caller<ScenarioSimulationService> service;
    private Caller<DMNTypeService> dmnTypeService;
    private Caller<ImportExportService> importExportService;
    private Caller<RunnerReportService> runnerReportService;
    private ScenarioSimulationBusinessCentralDocksHandler scenarioSimulationBusinessCentralDocksHandler;
    protected SimulationRunResult lastRunResult;

    public ScenarioSimulationEditorBusinessCentralWrapper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorBusinessCentralWrapper(final Caller<ScenarioSimulationService> service,
                                                          final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter,
                                                          final ImportsWidgetPresenter importsWidget,
                                                          final AsyncPackageDataModelOracleFactory oracleFactory,
                                                          final PlaceManager placeManager,
                                                          final Caller<DMNTypeService> dmnTypeService,
                                                          final Caller<ImportExportService> importExportService,
                                                          final Caller<RunnerReportService> runnerReportService,
                                                          final ScenarioSimulationBusinessCentralDocksHandler scenarioSimulationBusinessCentralDocksHandler) {
        super(scenarioSimulationEditorPresenter.getView());
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.dmnTypeService = dmnTypeService;
        this.service = service;
        this.importExportService = importExportService;
        this.runnerReportService = runnerReportService;
        this.importsWidget = importsWidget;
        this.oracleFactory = oracleFactory;
        this.placeManager = placeManager;
        this.type = scenarioSimulationEditorPresenter.getType();
        this.scenarioSimulationBusinessCentralDocksHandler = scenarioSimulationBusinessCentralDocksHandler;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path, place, type);
        scenarioSimulationEditorPresenter.setWrapper(this);
        scenarioSimulationEditorPresenter.setPath(path);
    }

    @OnClose
    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        scenarioSimulationEditorPresenter.onClose();
        super.onClose();
    }

    @OnMayClose
    public boolean mayClose() {
        return !scenarioSimulationEditorPresenter.isDirty();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @Override
    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    public void showDocks() {
        super.showDocks();
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER);
        scenarioSimulationEditorPresenter.showDocks(placeManager.getStatus(placeRequest));
        registerTestToolsCallback();
        /* Managing TestRunner Report */
        wrappedRegisterDock(ScenarioSimulationBusinessCentralDocksHandler.TEST_RUNNER_REPORTING_PANEL,
                            scenarioSimulationBusinessCentralDocksHandler.getTestRunnerReportingPanelWidget());
        /* It Loads last run info, it exits */
        if (lastRunResult != null) {
            scenarioSimulationBusinessCentralDocksHandler.updateTestRunnerReportingPanelResult(lastRunResult.getTestResultMessage());
        }
    }

    @Override
    public void wrappedRegisterDock(String id, IsWidget widget) {
        registerDock(id, widget);
    }

    @Override
    public void onImport(String fileContents, RemoteCallback<AbstractScesimModel> importCallBack, ErrorCallback<Object> importErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        importExportService.call(importCallBack,
                                 importErrorCallback)
                .importScesimModel(CSV, fileContents, scesimModel);
    }

    @Override
    public void onExportToCsv(RemoteCallback<Object> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        importExportService.call(exportCallBack, scenarioSimulationHasBusyIndicatorDefaultErrorCallback)
                .exportScesimModel(CSV, scesimModel);
    }

    @Override
    public void onDownloadReportToCsv(RemoteCallback<Object> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AuditLog auditLog) {
        runnerReportService.call(exportCallBack, scenarioSimulationHasBusyIndicatorDefaultErrorCallback)
                .getReport(auditLog);
    }

    @Override
    public void hideDocks() {
        super.hideDocks();
        scenarioSimulationEditorPresenter.hideDocks();
        unRegisterTestToolsCallback();
    }

    @Override
    public void onRunScenario(RemoteCallback<SimulationRunResult> refreshModelCallback, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, ScesimModelDescriptor simulationDescriptor, Settings settings, List<ScenarioWithIndex> toRun, Background background) {
        service.call(refreshModelCallback, scenarioSimulationHasBusyIndicatorDefaultErrorCallback)
                .runScenario(versionRecordManager.getCurrentPath(),
                             simulationDescriptor,
                             toRun,
                             settings,
                             background);
    }

    @Override
    public Integer getOriginalHash() {
        return originalHash;
    }

    @Override
    public void addDownloadMenuItem(FileMenuBuilder fileMenuBuilder) {
        scenarioSimulationEditorPresenter.addDownloadMenuItem(fileMenuBuilder, getPathSupplier());
    }

    @Override
    public void validate(Simulation simulation, Settings settings, RemoteCallback<?> callback) {
        scenarioSimulationEditorPresenter.getView().showLoading();
        service.call(
                callback,
                scenarioSimulationEditorPresenter.getValidationFailedCallback())
                .validate(simulation, settings, versionRecordManager.getCurrentPath());
    }

    @Override
    public void onRefreshedModelContent(SimulationRunResult testResult) {
        this.lastRunResult = testResult;
        scenarioSimulationBusinessCentralDocksHandler.updateTestRunnerReportingPanelResult(testResult.getTestResultMessage());
    }

    /**
     * This method is called when the main grid tab (Model) is focused
     */
    @Override
    public void onEditTabSelected() {
        super.onEditTabSelected();
        scenarioSimulationEditorPresenter.onEditTabSelected();
    }

    @Override
    public void onOverviewSelected() {
        super.onOverviewSelected();
        scenarioSimulationEditorPresenter.onOverviewSelected();
    }

    /**
     * This method adds specifically the Background grid and its related onFocus behavior
     * @param scenarioGridWidget
     */
    @Override
    public void addBackgroundPage(final ScenarioGridWidget scenarioGridWidget) {
        final MultiPageEditorViewImpl editorMultiPageView = (MultiPageEditorViewImpl) kieView.getMultiPage().getView();
        final String mainPageTitle = CommonConstants.INSTANCE.EditTabTitle();
        final String backgroundPageTitle = ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle();
        final int mainPageIndex = editorMultiPageView.getPageIndex(mainPageTitle);
        final int backgroundPageIndex = mainPageIndex + 1;
        kieView.getMultiPage().addPage(backgroundPageIndex, new PageImpl(scenarioGridWidget, backgroundPageTitle) {
            @Override
            public void onFocus() {
                super.onFocus();
                onBackgroundTabSelected();
            }
        });
    }

    @Override
    protected void addImportsTab(IsWidget importsWidget) {
        kieView.getMultiPage().addPage(new PageImpl(importsWidget, CommonConstants.INSTANCE.DataObjectsTabTitle()) {
            @Override
            public void onFocus() {
                super.onFocus();
                onImportsTabSelected();
            }
        });
    }

    @Override
    public void selectSimulationTab() {
        final MultiPageEditorViewImpl editorMultiPageView = (MultiPageEditorViewImpl) kieView.getMultiPage().getView();
        final int pageIndex = editorMultiPageView.getPageIndex(CommonConstants.INSTANCE.EditTabTitle());
        final TabListItem item = (TabListItem) editorMultiPageView.getTabBar().getWidget(pageIndex);
        if (item != null) {
            item.showTab(false);
        }
    }

    @Override
    public void selectBackgroundTab() {
        final MultiPageEditorViewImpl editorMultiPageView = (MultiPageEditorViewImpl) kieView.getMultiPage().getView();
        final int pageIndex = editorMultiPageView.getPageIndex(ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle());
        final TabListItem item = (TabListItem) editorMultiPageView.getTabBar().getWidget(pageIndex);
        if (item != null) {
            item.showTab(false);
        }
    }

    @Override
    public AbstractScenarioSimulationDocksHandler getScenarioSimulationDocksHandler() {
        return scenarioSimulationBusinessCentralDocksHandler;
    }

    @Override
    public ScenarioSimulationEditorPresenter getScenarioSimulationEditorPresenter() {
        return scenarioSimulationEditorPresenter;
    }

    @Override
    public void populateDocks(String identifier) {
        if (CoverageReportPresenter.IDENTIFIER.equals(identifier)) {
            scenarioSimulationBusinessCentralDocksHandler.getCoverageReportPresenter().ifPresent(presenter -> {
                setCoverageReport(presenter);
                presenter.setCurrentPath(scenarioSimulationEditorPresenter.getPath());
            });
        } else {
            ScenarioSimulationEditorWrapper.super.populateDocks(identifier);
        }
    }

    protected void registerTestToolsCallback() {
        placeManager.registerOnOpenCallback(new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER), scenarioSimulationEditorPresenter.getPopulateTestToolsCommand());
    }

    protected void unRegisterTestToolsCallback() {
        placeManager.getOnOpenCallbacks(new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER)).remove(scenarioSimulationEditorPresenter.getPopulateTestToolsCommand());
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected Promise<Void> makeMenuBar() {
        scenarioSimulationEditorPresenter.makeMenuBar(fileMenuBuilder);
        if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
            final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().orElseThrow(IllegalStateException::new);
            return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (Boolean.TRUE.equals(canUpdateProject)) {
                    addSave(fileMenuBuilder);
                    addCopy(fileMenuBuilder);
                    addRename(fileMenuBuilder);
                    addDelete(fileMenuBuilder);
                }
                addDownloadMenuItem(fileMenuBuilder);
                addCommonActions(fileMenuBuilder);
                return promises.resolve();
            });
        }
        return promises.resolve();
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return () -> scenarioSimulationEditorPresenter.getModel();
    }

    @Override
    protected void save(final String commitMessage) {
        synchronizeColumnsDimension(scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.SIMULATION),
                                    scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND));
        final ScenarioSimulationModel model = scenarioSimulationEditorPresenter.getModel();
        RemoteCallback<Path> saveSuccessCallback = getSaveSuccessCallback(scenarioSimulationEditorPresenter.getJsonModel(model).hashCode());
        service.call(saveSuccessCallback,
                     new HasBusyIndicatorDefaultErrorCallback(scenarioSimulationEditorPresenter.getView())).save(versionRecordManager.getCurrentPath(),
                                                                                                                 model,
                                                                                                                 metadata,
                                                                                                                 commitMessage);
    }

    @Override
    protected Command getBeforeSaveAndRenameCommand() {
        return () -> synchronizeColumnsDimension(scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.SIMULATION),
                                                 scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND));
    }

    @Override
    protected void addCommonActions(final FileMenuBuilder fileMenuBuilder) {
        scenarioSimulationEditorPresenter.addCommonActions(fileMenuBuilder, versionRecordManager.buildMenu(), alertsButtonMenuItemBuilder.build());
    }

    @Override
    protected void loadContent() {
        scenarioSimulationEditorPresenter.loadContent();
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return service;
    }

    @Override
    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return service;
    }

    @Override
    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return service;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<ScenarioSimulationModel, Metadata>> getSaveAndRenameServiceCaller() {
        return service;
    }

    @Override
    protected String getEditorIdentifier() {
        return IDENTIFIER;
    }

    protected void getModelSuccessCallbackMethod(ScenarioSimulationModelContent content) {
        //Path is set to null when the Editor is closed (which can happen before async calls complete).
        if (versionRecordManager.getCurrentPath() == null) {
            return;
        }
        scenarioSimulationEditorPresenter.setPackageName(content.getDataModel().getPackageName());
        resetEditorPages(content.getOverview());
        DataManagementStrategy dataManagementStrategy;
        if (ScenarioSimulationModel.Type.RULE.equals(content.getModel().getSettings().getType())) {
            dataManagementStrategy = new BusinessCentralDMODataManagementStrategy(oracleFactory);
        } else {
            dataManagementStrategy = new BusinessCentralDMNDataManagementStrategy(dmnTypeService, scenarioSimulationEditorPresenter.getEventBus());
        }
        dataManagementStrategy.manageScenarioSimulationModelContent(versionRecordManager.getCurrentPath(), content);
        ScenarioSimulationModel model = content.getModel();
        if (dataManagementStrategy instanceof BusinessCentralDMODataManagementStrategy) {
            importsWidget.setContent(((BusinessCentralDMODataManagementStrategy) dataManagementStrategy).getOracle(),
                                     model.getImports(),
                                     isReadOnly);
            addImportsTab(importsWidget);
        }
        baseView.hideBusyIndicator();
        setOriginalHash(scenarioSimulationEditorPresenter.getJsonModel(model).hashCode());
        scenarioSimulationEditorPresenter.getModelSuccessCallbackMethod(dataManagementStrategy, model);
    }

    protected void onBackgroundTabSelected() {
        scenarioSimulationEditorPresenter.onBackgroundTabSelected();
    }

    protected void onImportsTabSelected() {
        scenarioSimulationEditorPresenter.onImportsTabSelected();
    }

    private RemoteCallback<ScenarioSimulationModelContent> getModelSuccessCallback() {
        return this::getModelSuccessCallbackMethod;
    }

    protected void setCoverageReport(CoverageReportView.Presenter presenter) {
        ScenarioSimulationModel.Type modelType = scenarioSimulationEditorPresenter.getDataManagementStrategy() instanceof AbstractDMODataManagementStrategy ? ScenarioSimulationModel.Type.RULE : ScenarioSimulationModel.Type.DMN;
        SimulationRunMetadata simulationRunMetadata = lastRunResult != null ? lastRunResult.getSimulationRunMetadata() : null;
        presenter.populateCoverageReport(modelType, simulationRunMetadata);
        if (simulationRunMetadata != null && simulationRunMetadata.getAuditLog() != null) {
            presenter.setDownloadReportCommand(() -> onDownloadReportToCsv(scenarioSimulationEditorPresenter.getExportCallBack(), new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(scenarioSimulationEditorPresenter.getView()), simulationRunMetadata.getAuditLog()));
        }
    }
}
