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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import elemental2.dom.DomGlobal;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.CustomBusyPopup;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageReportPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageReportView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SubDockView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestRunnerReportingPanelWrapper;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.file.exports.TextContent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import static org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.drools.workbench.screens.scenariosimulation.service.ImportExportType.CSV;

@Dependent
public class ScenarioSimulationEditorPresenter {

    public static final String IDENTIFIER = "ScenarioSimulationEditor";
    private static final AtomicLong SCENARIO_PRESENTER_COUNTER = new AtomicLong();
    //Package for which this Scenario Simulation relates
    protected String packageName = "";
    protected ObservablePath path;
    protected EventBus eventBus;
    protected ScenarioGridPanel scenarioGridPanel;
    protected PlaceManager placeManager;
    protected DataManagementStrategy dataManagementStrategy;
    protected ScenarioSimulationContext context;
    protected ScenarioSimulationModel model;
    protected TestRunnerReportingPanelWrapper testRunnerReportingPanel;
    protected SimulationRunResult lastRunResult;
    protected long scenarioPresenterId;
    private ScenarioSimulationResourceType type;
    private ScenarioSimulationView view;
    private Command populateTestToolsCommand;
    private TextFileExport textFileExport;
    private ConfirmPopupPresenter confirmPopupPresenter;
    private ScenarioSimulationDocksHandler scenarioSimulationDocksHandler;
    private ScenarioSimulationEditorWrapper scenarioSimulationEditorWrapper;

    public ScenarioSimulationEditorPresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorPresenter(final ScenarioSimulationProducer scenarioSimulationProducer,
                                             final ScenarioSimulationResourceType type,
                                             final PlaceManager placeManager,
                                             // TO BE WRAPPED
                                             final TestRunnerReportingPanelWrapper testRunnerReportingPanel,
                                             final ScenarioSimulationDocksHandler scenarioSimulationDocksHandler,
                                             final TextFileExport textFileExport,
                                             final ConfirmPopupPresenter confirmPopupPresenter) {
        this.view = scenarioSimulationProducer.getScenarioSimulationView();
        this.testRunnerReportingPanel = testRunnerReportingPanel;
        this.scenarioSimulationDocksHandler = scenarioSimulationDocksHandler;
        this.type = type;
        this.placeManager = placeManager;
        this.context = scenarioSimulationProducer.getScenarioSimulationContext();
        this.eventBus = scenarioSimulationProducer.getEventBus();
        this.textFileExport = textFileExport;
        this.confirmPopupPresenter = confirmPopupPresenter;
        scenarioGridPanel = view.getScenarioGridPanel();
        context.setScenarioSimulationEditorPresenter(this);
        view.init(this);
        populateTestToolsCommand = createPopulateTestToolsCommand();
        scenarioGridPanel.select();
        scenarioPresenterId = SCENARIO_PRESENTER_COUNTER.getAndIncrement();
    }

    public void init(ScenarioSimulationEditorWrapper scenarioSimulationEditorWrapper, ObservablePath path) {
        this.scenarioSimulationEditorWrapper = scenarioSimulationEditorWrapper;
        this.path = path;
        testRunnerReportingPanel.reset();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void onClose() {
        scenarioGridPanel.unregister();
    }

    /**
     * @param status <code>PlaceStatus</code> of <b>TestToolsPresenter</b>
     */
    public void showDocks(PlaceStatus status) {
        scenarioSimulationEditorWrapper.wrappedRegisterDock(ScenarioSimulationDocksHandler.TEST_RUNNER_REPORTING_PANEL, testRunnerReportingPanel.asWidget());
        scenarioSimulationDocksHandler.addDocks();
        scenarioSimulationDocksHandler.setScesimEditorId(String.valueOf(scenarioPresenterId));
        expandToolsDock(status);
        registerTestToolsCallback();
        resetDocks();
        populateRightDocks(TestToolsPresenter.IDENTIFIER);
    }

    public void hideDocks() {
        scenarioSimulationDocksHandler.removeDocks();
        view.getScenarioGridLayer().getScenarioGrid().clearSelections();
        unRegisterTestToolsCallback();
        clearTestToolsStatus();
    }

    public void onUberfireDocksInteractionEvent(@Observes final UberfireDocksInteractionEvent uberfireDocksInteractionEvent) {
        if (isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEvent) && !TestToolsPresenter.IDENTIFIER.equals(uberfireDocksInteractionEvent.getTargetDock().getIdentifier())) {
            populateRightDocks(uberfireDocksInteractionEvent.getTargetDock().getIdentifier());
        }
    }

    public void expandToolsDock(PlaceStatus status) {
        if (!PlaceStatus.OPEN.equals(status)) {
            scenarioSimulationDocksHandler.expandToolsDock();
        }
    }

    public ScenarioSimulationView getView() {
        return view;
    }

    public ScenarioSimulationModel getModel() {
        return model;
    }

    public Command getPopulateTestToolsCommand() {
        return populateTestToolsCommand;
    }

    /**
     * To be called to force test tools panel reload
     * @param disable set this to <code>true</code> to <b>also</b> disable the panel
     */
    public void reloadTestTools(boolean disable) {
        populateTestToolsCommand.execute();
        if (disable) {
            getTestToolsPresenter(getCurrentRightDockPlaceRequest(TestToolsPresenter.IDENTIFIER)).ifPresent(TestToolsView.Presenter::onDisableEditorTab);
        }
    }

    public void onRunScenario() {
        List<Integer> indexes = IntStream.range(0, context.getStatus().getSimulation().getUnmodifiableScenarios().size())
                .boxed()
                .collect(Collectors.toList());
        onRunScenario(indexes);
    }

    public void onRunScenario(List<Integer> indexOfScenarioToRun) {
        view.getScenarioGridPanel().getScenarioGrid().getModel().resetErrors();
        model.setSimulation(context.getStatus().getSimulation());
        Simulation simulation = model.getSimulation();
        List<ScenarioWithIndex> toRun = simulation.getScenarioWithIndex().stream()
                .filter(elem -> indexOfScenarioToRun.contains(elem.getIndex() - 1))
                .collect(Collectors.toList());
        view.showBusyIndicator(ScenarioSimulationEditorConstants.INSTANCE.running());
        scenarioSimulationEditorWrapper.onRunScenario(getRefreshModelCallback(), new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(view),
                                                      simulation.getSimulationDescriptor(),
                                                      toRun);
    }

    public void onUndo() {
        eventBus.fireEvent(new UndoEvent());
    }

    public void onRedo() {
        eventBus.fireEvent(new RedoEvent());
    }

    public void setUndoButtonEnabledStatus(boolean enabled) {
        view.getUndoMenuItem().setEnabled(enabled);
    }

    public void setRedoButtonEnabledStatus(boolean enabled) {
        view.getRedoMenuItem().setEnabled(enabled);
    }

    public void addDownloadMenuItem(FileMenuBuilder fileMenuBuilder, Supplier<Path> pathSupplier) {
        fileMenuBuilder.addNewTopLevelMenu(view.getDownloadMenuItem(pathSupplier));
    }

    public DataManagementStrategy getDataManagementStrategy() {
        return dataManagementStrategy;
    }

    public void onImport(String fileContents) {
        scenarioSimulationEditorWrapper.onImport(fileContents, getImportCallBack(), getImportErrorCallback(), context.getStatus().getSimulation());
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ScenarioSimulationContext getContext() {
        return context;
    }

    /**
     * It resets the status of all Docks widgets present in ScenarioSimulation. Considering the docks are
     * marked as ApplicationScoped, this method should be call everytime ScenarioSimulationEditor is opened (or closed)
     */
    protected void resetDocks() {
        getSettingsPresenter(getCurrentRightDockPlaceRequest(SettingsPresenter.IDENTIFIER)).ifPresent(
                SubDockView.Presenter::reset);
        getCheatSheetPresenter(getCurrentRightDockPlaceRequest(CheatSheetPresenter.IDENTIFIER)).ifPresent(
                SubDockView.Presenter::reset);
        getTestToolsPresenter(getCurrentRightDockPlaceRequest(TestToolsPresenter.IDENTIFIER)).ifPresent(
                SubDockView.Presenter::reset);
        getCoverageReportPresenter(getCurrentRightDockPlaceRequest(CoverageReportPresenter.IDENTIFIER)).ifPresent(
                SubDockView.Presenter::reset);
        testRunnerReportingPanel.reset();
    }

    /**
     * Method to verify if the given <code>UberfireDocksInteractionEvent</code> is to be processed by current instance.
     * @param uberfireDocksInteractionEvent
     * @return <code>true</code> if <code>UberfireDocksInteractionEvent.getTargetDock() != null</code> <b>and</b>
     * the <b>scesimpath</b> parameter of <code>UberfireDocksInteractionEvent.getTargetDock().getPlaceRequest()</code>
     * is equals to the <b>path</b> (toString) of the current instance; <code>false</code> otherwise
     */
    protected boolean isUberfireDocksInteractionEventToManage(UberfireDocksInteractionEvent uberfireDocksInteractionEvent) {
        return uberfireDocksInteractionEvent.getTargetDock() != null && uberfireDocksInteractionEvent.getTargetDock().getPlaceRequest().getParameter(SCESIMEDITOR_ID, "").equals(String.valueOf(scenarioPresenterId));
    }

    protected RemoteCallback<SimulationRunResult> getRefreshModelCallback() {
        return this::refreshModelContent;
    }

    protected void refreshModelContent(SimulationRunResult newData) {
        view.hideBusyIndicator();
        if (this.model == null) {
            return;
        }
        Simulation simulation = this.model.getSimulation();
        for (ScenarioWithIndex scenarioWithIndex : newData.getScenarioWithIndex()) {
            int index = scenarioWithIndex.getIndex() - 1;
            simulation.replaceScenario(index, scenarioWithIndex.getScenario());
        }
        view.refreshContent(simulation);
        context.getStatus().setSimulation(simulation);
        scenarioSimulationDocksHandler.expandTestResultsDock();
        testRunnerReportingPanel.onTestRun(newData.getTestResultMessage());
        dataManagementStrategy.setModel(model);

        this.lastRunResult = newData;
    }

    protected void registerTestToolsCallback() {
        placeManager.registerOnOpenCallback(new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER), populateTestToolsCommand);
    }

    protected void unRegisterTestToolsCallback() {
        placeManager.getOnOpenCallbacks(new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER)).remove(populateTestToolsCommand);
    }

    protected ErrorCallback<Object> getImportErrorCallback() {
        return (error, exception) -> {
            confirmPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.importErrorTitle(),
                                       ScenarioSimulationEditorConstants.INSTANCE.importFailedMessage());
            return false;
        };
    }

    /**
     * If you want to customize the menu override this method.
     * @param fileMenuBuilder
     */
    public void makeMenuBar(FileMenuBuilder fileMenuBuilder) {
        fileMenuBuilder.addNewTopLevelMenu(view.getRunScenarioMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getUndoMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getRedoMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getExportToCsvMenuItem());
        fileMenuBuilder.addNewTopLevelMenu(view.getImportMenuItem());
        view.getUndoMenuItem().setEnabled(false);
        view.getRedoMenuItem().setEnabled(false);
    }

    public Supplier<ScenarioSimulationModel> getContentSupplier() {
        return () -> model;
    }

    public void addCommonActions(final FileMenuBuilder fileMenuBuilder, MenuItem versionMenuItem, MenuItem alertsButtonMenuItem) {
        fileMenuBuilder
                .addNewTopLevelMenu(versionMenuItem)
                .addNewTopLevelMenu(alertsButtonMenuItem);
    }

    public void loadContent() {
        CustomBusyPopup.showMessage(CommonConstants.INSTANCE.Loading());
    }

    public boolean isDirty() {
        try {
            view.getScenarioGridPanel().getScenarioGrid().getModel().resetErrors();
            int currentHashcode = MarshallingWrapper.toJSON(model).hashCode();
            return scenarioSimulationEditorWrapper.getOriginalHash() != currentHashcode;
        } catch (Exception ignored) {
            return false;
        }
    }

    protected void onDownload(final Supplier<Path> pathSupplier) {
        final String downloadURL = getFileDownloadURL(pathSupplier);
        open(downloadURL);
    }

    protected void open(final String downloadURL) {
        DomGlobal.window.open(downloadURL);
    }

    protected void showImportDialog() {
        eventBus.fireEvent(new ImportEvent());
    }

    protected void onExportToCsv() {
        scenarioSimulationEditorWrapper.onExportToCsv(getExportCallBack(), new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(view), context.getStatus().getSimulation());
    }


    protected RemoteCallback<Object> getExportCallBack() {
        return rawResult -> {
            TextContent textContent = TextContent.create((String) rawResult);
            textFileExport.export(textContent,
                                  path.getFileName() + CSV.getExtension());
        };
    }

    protected RemoteCallback<Simulation> getImportCallBack() {
        return simulation -> {
            cleanReadOnlyColumn(simulation);
            model.setSimulation(simulation);
            view.setContent(model.getSimulation());
            context.getStatus().setSimulation(model.getSimulation());
            view.onResize();
        };
    }

    /**
     * Read only columns should not contains any values
     * @param simulation
     */
    protected void cleanReadOnlyColumn(Simulation simulation) {
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        for (int i = 0; i < simulation.getUnmodifiableScenarios().size(); i += 1) {
            Scenario scenario = simulation.getScenarioByIndex(i);
            for (FactMapping factMapping : simulationDescriptor.getUnmodifiableFactMappings()) {
                if (isColumnReadOnly(factMapping)) {
                    scenario.getFactMappingValue(factMapping.getFactIdentifier(),
                                                 factMapping.getExpressionIdentifier())
                            .ifPresent(fmv -> fmv.setRawValue(null));
                }
            }
        }
    }

    private boolean isColumnReadOnly(FactMapping factMapping) {
        return !FactMappingType.OTHER.equals(factMapping.getExpressionIdentifier().getType()) &&
                factMapping.getExpressionElements().isEmpty();
    }

    public void populateRightDocks(String identifier) {
        if (dataManagementStrategy != null) {
            final PlaceRequest currentRightDockPlaceRequest = getCurrentRightDockPlaceRequest(identifier);
            switch (identifier) {
                case SettingsPresenter.IDENTIFIER:
                    getSettingsPresenter(currentRightDockPlaceRequest).ifPresent(presenter -> {
                        setSettings(presenter);
                        presenter.setCurrentPath(path);
                    });
                    break;
                case TestToolsPresenter.IDENTIFIER:
                    getTestToolsPresenter(currentRightDockPlaceRequest).ifPresent(this::setTestTools);
                    break;
                case CheatSheetPresenter.IDENTIFIER:
                    getCheatSheetPresenter(currentRightDockPlaceRequest).ifPresent(presenter -> {
                        if (!presenter.isCurrentlyShow(path)) {
                            setCheatSheet(presenter);
                            presenter.setCurrentPath(path);
                        }
                    });
                    break;
                case CoverageReportPresenter.IDENTIFIER:
                    getCoverageReportPresenter(currentRightDockPlaceRequest).ifPresent(presenter -> {
                        setCoverageReport(presenter);
                        presenter.setCurrentPath(path);
                    });
                    break;
            }
        }
    }

    public void getModelSuccessCallbackMethod(DataManagementStrategy dataManagementStrategy, ScenarioSimulationModel model) {
        this.dataManagementStrategy = dataManagementStrategy;
        this.model = model;
        // NOTE: keep here initialization of docks related with model
        populateRightDocks(TestToolsPresenter.IDENTIFIER);
        populateRightDocks(SettingsPresenter.IDENTIFIER);
        view.setContent(model.getSimulation());
        context.getStatus().setSimulation(model.getSimulation());
        CustomBusyPopup.close();
    }

    public ScenarioSimulationResourceType getType() {
        return type;
    }

    protected void setTestTools(TestToolsView.Presenter presenter) {
        context.setTestToolsPresenter(presenter);
        presenter.setEventBus(eventBus);
        dataManagementStrategy.populateTestTools(presenter, scenarioGridPanel.getScenarioGrid().getModel());
    }

    protected void clearTestToolsStatus() {
        getTestToolsPresenter(getCurrentRightDockPlaceRequest(TestToolsPresenter.IDENTIFIER)).ifPresent(TestToolsView.Presenter::onClearStatus);
    }

    protected void setCheatSheet(CheatSheetView.Presenter presenter) {
        Type type = dataManagementStrategy instanceof AbstractDMODataManagementStrategy ? Type.RULE : Type.DMN;
        presenter.initCheatSheet(type);
    }

    protected void setSettings(SettingsView.Presenter presenter) {
        Type type = dataManagementStrategy instanceof AbstractDMODataManagementStrategy ? Type.RULE : Type.DMN;
        presenter.setScenarioType(type, model.getSimulation().getSimulationDescriptor(), path.getFileName());
        presenter.setSaveCommand(getSaveCommand());
    }

    protected void setCoverageReport(CoverageReportView.Presenter presenter) {
        Type type = dataManagementStrategy instanceof AbstractDMODataManagementStrategy ? Type.RULE : Type.DMN;
        SimulationRunMetadata simulationRunMetadata = lastRunResult != null ? lastRunResult.getSimulationRunMetadata() : null;
        presenter.populateCoverageReport(type, simulationRunMetadata);
    }

    public String getJsonModel(ScenarioSimulationModel model) {
        return MarshallingWrapper.toJSON(model);
    }

    protected Optional<CheatSheetView.Presenter> getCheatSheetPresenter(PlaceRequest placeRequest) {
        final Optional<CheatSheetView> cheatSheetView = getCheatSheetView(placeRequest);
        return cheatSheetView.map(CheatSheetView::getPresenter);
    }

    protected Optional<TestToolsView.Presenter> getTestToolsPresenter(PlaceRequest placeRequest) {
        final Optional<TestToolsView> testToolsView = getTestToolsView(placeRequest);
        return testToolsView.map(TestToolsView::getPresenter);
    }

    protected Optional<SettingsView.Presenter> getSettingsPresenter(PlaceRequest placeRequest) {
        final Optional<SettingsView> settingsView = getSettingsView(placeRequest);
        return settingsView.map(SettingsView::getPresenter);
    }

    protected Optional<CoverageReportView.Presenter> getCoverageReportPresenter(PlaceRequest placeRequest) {
        final Optional<CoverageReportView> coverageReportViewMap = getCoverageReportView(placeRequest);
        return coverageReportViewMap.map(CoverageReportView::getPresenter);
    }

    protected Command getSaveCommand() {
        return () -> scenarioSimulationEditorWrapper.wrappedSave(ConstantHolder.SAVE);
    }

    /**
     * Returns a <code>PlaceRequest</code> for the <b>status</b> of the right dock with the given <b>identifier</b>
     * relative to the current instance of <code>ScenarioSimulationEditorPresenter</code>
     * @return
     */
    protected PlaceRequest getCurrentRightDockPlaceRequest(String identifier) {
        PlaceRequest toReturn = new DefaultPlaceRequest(identifier);
        toReturn.addParameter(SCESIMEDITOR_ID, String.valueOf(scenarioPresenterId));
        return toReturn;
    }

    protected String getFileDownloadURL(final Supplier<Path> pathSupplier) {
        return GWT.getModuleBaseURL() + "defaulteditor/download?path=" + pathSupplier.get().toURI();
    }

    private Optional<TestToolsView> getTestToolsView(PlaceRequest placeRequest) {
        final Activity activity = placeManager.getActivity(placeRequest);
        if (activity != null) {
            final AbstractWorkbenchActivity testToolsActivity = (AbstractWorkbenchActivity) activity;
            return Optional.of((TestToolsView) testToolsActivity.getWidget());
        } else {
            return Optional.empty();
        }
    }

    private Optional<CheatSheetView> getCheatSheetView(PlaceRequest placeRequest) {
        final Activity activity = placeManager.getActivity(placeRequest);
        if (activity != null) {
            final AbstractWorkbenchActivity cheatSheetActivity = (AbstractWorkbenchActivity) activity;
            return Optional.of((CheatSheetView) cheatSheetActivity.getWidget());
        } else {
            return Optional.empty();
        }
    }

    private Optional<SettingsView> getSettingsView(PlaceRequest placeRequest) {
        final Activity activity = placeManager.getActivity(placeRequest);
        if (activity != null) {
            final AbstractWorkbenchActivity settingsActivity = (AbstractWorkbenchActivity) activity;
            return Optional.of((SettingsView) settingsActivity.getWidget());
        } else {
            return Optional.empty();
        }
    }

    private Optional<CoverageReportView> getCoverageReportView(PlaceRequest placeRequest) {
        final Activity activity = placeManager.getActivity(placeRequest);
        if (activity != null) {
            final AbstractWorkbenchActivity settingsActivity = (AbstractWorkbenchActivity) activity;
            return Optional.of((CoverageReportView) settingsActivity.getWidget());
        } else {
            return Optional.empty();
        }
    }

    private Command createPopulateTestToolsCommand() {
        return () -> populateRightDocks(TestToolsPresenter.IDENTIFIER);
    }
}
