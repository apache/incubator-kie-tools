/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import elemental2.dom.DomGlobal;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.BackgroundDataWithIndex;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.CustomBusyPopup;
import org.drools.workbench.screens.scenariosimulation.client.producers.AbstractScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorI18nServerManager;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.file.exports.TextContent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import static org.drools.workbench.screens.scenariosimulation.service.ImportExportType.CSV;

@ApplicationScoped
public class ScenarioSimulationEditorPresenter {

    public static final String IDENTIFIER = "ScenarioSimulationEditor";
    private static final AtomicLong SCENARIO_PRESENTER_COUNTER = new AtomicLong();
    //Package for which this Scenario Simulation relates
    protected String packageName = "";
    protected ObservablePath path;
    protected EventBus eventBus;
    protected ScenarioGridWidget scenarioMainGridWidget;
    protected ScenarioGridWidget scenarioBackgroundGridWidget;
    protected DataManagementStrategy dataManagementStrategy;
    protected ScenarioSimulationContext context;
    protected ScenarioSimulationModel model;
    protected long scenarioPresenterId;
    protected ScenarioSimulationEditorWrapper scenarioSimulationEditorWrapper;
    private ScenarioSimulationResourceType type;
    private ScenarioSimulationView view;
    private Command populateTestToolsCommand;
    private TextFileExport textFileExport;
    private ConfirmPopupPresenter confirmPopupPresenter;
    private AbstractScenarioSimulationDocksHandler abstractScenarioSimulationDocksHandler;

    public ScenarioSimulationEditorPresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorPresenter(final AbstractScenarioSimulationProducer abstractScenarioSimulationProducer,
                                             final ScenarioSimulationResourceType type,
                                             final AbstractScenarioSimulationDocksHandler abstractScenarioSimulationDocksHandler,
                                             final TextFileExport textFileExport,
                                             final ConfirmPopupPresenter confirmPopupPresenter) {
        this.view = abstractScenarioSimulationProducer.getScenarioSimulationView();
        this.abstractScenarioSimulationDocksHandler = abstractScenarioSimulationDocksHandler;
        this.type = type;
        this.eventBus = abstractScenarioSimulationProducer.getEventBus();
        this.textFileExport = textFileExport;
        this.confirmPopupPresenter = confirmPopupPresenter;
        view.init();
        abstractScenarioSimulationProducer.setScenarioSimulationEditorPresenter(this);
        scenarioMainGridWidget = view.getScenarioGridWidget();
        scenarioMainGridWidget.getScenarioSimulationContext().setScenarioSimulationEditorPresenter(this);
        scenarioBackgroundGridWidget = abstractScenarioSimulationProducer.getScenarioBackgroundGridWidget();
        scenarioBackgroundGridWidget.getScenarioSimulationContext().setScenarioSimulationEditorPresenter(this);
        populateTestToolsCommand = createPopulateTestToolsCommand();
        scenarioPresenterId = SCENARIO_PRESENTER_COUNTER.getAndIncrement();
        context = abstractScenarioSimulationProducer.getScenarioSimulationContext();
    }

    public void setWrapper(ScenarioSimulationEditorWrapper scenarioSimulationEditorWrapper) {
        this.scenarioSimulationEditorWrapper = scenarioSimulationEditorWrapper;
        this.scenarioSimulationEditorWrapper.addBackgroundPage(scenarioBackgroundGridWidget);
    }

    public void setPath(ObservablePath path) {
        this.path = path;
    }

    public ObservablePath getPath() {
        return path;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void onClose() {
        scenarioMainGridWidget.unregister();
        scenarioBackgroundGridWidget.unregister();
    }

    public void initializeDocks() {
        abstractScenarioSimulationDocksHandler.addDocks();
        abstractScenarioSimulationDocksHandler.setScesimEditorId(String.valueOf(scenarioPresenterId));
        expandToolsDock();
        registerTestToolsCallback();
        resetDocks();
    }

    public void hideDocks() {
        abstractScenarioSimulationDocksHandler.removeDocks();
        scenarioMainGridWidget.clearSelections();
        scenarioBackgroundGridWidget.clearSelections();
        unRegisterTestToolsCallback();
        clearTestToolsStatus();
    }

    public void onUberfireDocksInteractionEvent(@Observes final UberfireDocksInteractionEvent uberfireDocksInteractionEvent) {
        if (isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEvent) && !TestToolsPresenter.IDENTIFIER.equals(uberfireDocksInteractionEvent.getTargetDock().getIdentifier())) {
            populateRightDocks(uberfireDocksInteractionEvent.getTargetDock().getIdentifier());
        }
    }

    public void expandToolsDock() {
        abstractScenarioSimulationDocksHandler.expandToolsDock();
    }

    public void expandSettingsDock() {
        abstractScenarioSimulationDocksHandler.expandSettingsDock();
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
        populateRightDocks(TestToolsPresenter.IDENTIFIER);
        if (disable) {
            abstractScenarioSimulationDocksHandler.getTestToolsPresenter().onDisableEditorTab();
        }
    }

    /**
     * To be called to force settings panel reload
     */
    public void reloadSettingsDock() {
        this.updateSettings(abstractScenarioSimulationDocksHandler.getSettingsPresenter());
    }

    public void onRunScenario() {
        List<Integer> indexes = IntStream.range(0, context.getStatus().getSimulation().getUnmodifiableData().size())
                .boxed()
                .collect(Collectors.toList());
        onRunScenario(indexes);
    }

    public void onRunScenario(List<Integer> indexOfScenarioToRun) {
        scenarioMainGridWidget.resetErrors();
        scenarioBackgroundGridWidget.resetErrors();
        model.setSimulation(scenarioMainGridWidget.getScenarioSimulationContext().getStatus().getSimulation());
        model.setBackground(scenarioMainGridWidget.getScenarioSimulationContext().getStatus().getBackground());
        Simulation simulation = model.getSimulation();
        List<ScenarioWithIndex> toRun = simulation.getScenarioWithIndex().stream()
                .filter(elem -> indexOfScenarioToRun.contains(elem.getIndex() - 1))
                .collect(Collectors.toList());
        view.showBusyIndicator(ScenarioSimulationEditorConstants.INSTANCE.running());
        scenarioSimulationEditorWrapper.onRunScenario(getRefreshModelCallback(),
                                                      new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(view),
                                                      simulation.getScesimModelDescriptor(),
                                                      model.getSettings(),
                                                      toRun,
                                                      model.getBackground());
    }

    public void onUndo() {
        eventBus.fireEvent(new UndoEvent());
    }

    public void onRedo() {
        eventBus.fireEvent(new RedoEvent());
    }

    public DataManagementStrategy getDataManagementStrategy() {
        return dataManagementStrategy;
    }

    public void onImport(String fileContents, GridWidget gridWidget) {
        switch (gridWidget) {
            case SIMULATION:
                scenarioSimulationEditorWrapper.onImport(fileContents, getImportCallBack(), getImportErrorCallback(), context.getStatus().getSimulation());
                break;
            case BACKGROUND:
                scenarioSimulationEditorWrapper.onImport(fileContents, getImportCallBack(), getImportErrorCallback(), context.getStatus().getBackground());
                break;
            default:
                throw new IllegalArgumentException("Illegal GridWidget " + gridWidget);
        }
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
        abstractScenarioSimulationDocksHandler.resetDocks();
    }

    /**
     * Method to verify if the given <code>UberfireDocksInteractionEvent</code> is to be processed by current instance.
     * @param uberfireDocksInteractionEvent
     * @return <code>true</code> if <code>UberfireDocksInteractionEvent.getTargetDock() != null</code> <b>and</b>
     * the <b>scesimpath</b> parameter of <code>UberfireDocksInteractionEvent.getTargetDock().getPlaceRequest()</code>
     * is equals to the <b>path</b> (toString) of the current instance; <code>false</code> otherwise
     */
    protected boolean isUberfireDocksInteractionEventToManage(UberfireDocksInteractionEvent uberfireDocksInteractionEvent) {
        return true;
    }

    protected RemoteCallback<SimulationRunResult> getRefreshModelCallback() {
        return this::refreshModelContent;
    }

    protected void refreshModelContent(SimulationRunResult newData) {
        view.hideBusyIndicator();
        if (this.model == null) {
            return;
        }
        // refresh simulation data
        Simulation simulation = this.model.getSimulation();
        for (ScenarioWithIndex scenarioWithIndex : newData.getScenarioWithIndex()) {
            int index = scenarioWithIndex.getIndex() - 1;
            simulation.replaceData(index, scenarioWithIndex.getScesimData());
        }
        scenarioMainGridWidget.refreshContent(simulation);
        context.getStatus().setSimulation(simulation);

        // refresh background data
        boolean hasBackgroundError = false;
        Background background = this.model.getBackground();
        for (BackgroundDataWithIndex backgroundDataWithIndex : newData.getBackgroundDataWithIndex()) {
            int index = backgroundDataWithIndex.getIndex() - 1;
            BackgroundData scesimData = backgroundDataWithIndex.getScesimData();
            background.replaceData(index, scesimData);
            hasBackgroundError |= scesimData.getUnmodifiableFactMappingValues().stream().anyMatch(elem -> !FactMappingValueStatus.SUCCESS.equals(elem.getStatus()));
        }
        scenarioBackgroundGridWidget.refreshContent(background);
        context.getStatus().setBackground(background);

        if (hasBackgroundError) {
            sendNotification(ScenarioSimulationEditorConstants.INSTANCE.backgroundErrorNotification(), NotificationEvent.NotificationType.ERROR);
            selectBackgroundTab();
        }

        dataManagementStrategy.setModel(model);
        abstractScenarioSimulationDocksHandler.expandTestResultsDock();
        scenarioSimulationEditorWrapper.onRefreshedModelContent(newData);
    }

    public void sendNotification(String text, NotificationEvent.NotificationType type) {
        eventBus.fireEvent(new ScenarioNotificationEvent(text, type));
    }

    public void sendNotification(String text, NotificationEvent.NotificationType type, boolean autoHide) {
        eventBus.fireEvent(new ScenarioNotificationEvent(text, type, autoHide));
    }

    protected void registerTestToolsCallback() {
    }

    protected void unRegisterTestToolsCallback() {
    }

    protected ErrorCallback<Object> getImportErrorCallback() {
        return (error, exception) -> {
            confirmPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.importErrorTitle(),
                                       ScenarioSimulationEditorConstants.INSTANCE.importFailedMessage());
            return false;
        };
    }

    public void loadContent() {
        CustomBusyPopup.showMessage(CommonConstants.INSTANCE.Loading());
    }

    public boolean isDirty() {
        return false;
    }

    public void onEditTabSelected() {
        scenarioMainGridWidget.selectAndFocus();
        scenarioBackgroundGridWidget.deselectAndUnFocus();
    }

    public void onBackgroundTabSelected() {
        scenarioBackgroundGridWidget.selectAndFocus();
        scenarioMainGridWidget.deselectAndUnFocus();
    }

    public void onOverviewSelected() {
        scenarioMainGridWidget.deselectAndUnFocus();
        scenarioBackgroundGridWidget.deselectAndUnFocus();
    }

    public void onImportsTabSelected() {
        scenarioMainGridWidget.deselectAndUnFocus();
        scenarioBackgroundGridWidget.deselectAndUnFocus();
    }

    public void validateSimulation() {
        scenarioSimulationEditorWrapper.validate(model.getSimulation(),
                                                 model.getSettings(),
                                                 getValidationCallback());
    }

    public void selectSimulationTab() {
        scenarioSimulationEditorWrapper.selectSimulationTab();
    }

    public void selectBackgroundTab() {
        scenarioSimulationEditorWrapper.selectBackgroundTab();
    }

    protected void onDownload(final Supplier<Path> pathSupplier) {
        final String downloadURL = getFileDownloadURL(pathSupplier);
        open(downloadURL);
    }

    protected void open(final String downloadURL) {
        DomGlobal.window.open(downloadURL);
    }

    protected void showImportDialog() {
        context.getSelectedGridWidget().ifPresent(gridWidget -> eventBus.fireEvent(new ImportEvent(gridWidget)));
    }

    protected void onExportToCsv() {
        context.getSelectedGridWidget().ifPresent(gridWidget ->
                                                          scenarioSimulationEditorWrapper
                                                                  .onExportToCsv(getExportCallBack(),
                                                                                 new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(view),
                                                                                 context.getAbstractScesimModelByGridWidget(gridWidget)));
    }

    public RemoteCallback<String> getExportCallBack() {
        return rawResult -> {
            TextContent textContent = TextContent.create(rawResult);
            textFileExport.export(textContent,
                                  path.getFileName() + CSV.getExtension());
        };
    }

    protected RemoteCallback<AbstractScesimModel> getImportCallBack() {
        return scesimModel -> {
            cleanReadOnlyColumn(scesimModel);
            if (scesimModel instanceof Simulation) {
                model.setSimulation((Simulation) scesimModel);
                scenarioMainGridWidget.setContent(model.getSimulation(), model.getSettings().getType());
                context.getStatus().setSimulation(model.getSimulation());
                scenarioMainGridWidget.onResize();
            } else if (scesimModel instanceof Background) {
                model.setBackground((Background) scesimModel);
                scenarioBackgroundGridWidget.setContent(model.getBackground(), model.getSettings().getType());
                context.getStatus().setBackground(model.getBackground());
                scenarioBackgroundGridWidget.onResize();
            }
        };
    }

    protected Command getValidateCommand() {
        return this::validateSimulation;
    }

    protected RemoteCallback<List<FactMappingValidationError>> getValidationCallback() {
        return result -> {
            view.hideBusyIndicator();

            if (result != null && !result.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder(ScenarioSimulationEditorConstants.INSTANCE.validationErrorMessage());
                errorMessage.append(":<br/>");
                for (FactMappingValidationError validationError : result) {
                    String message = validationError.getErrorMessage() != null ? validationError.getErrorMessage() :
                            ScenarioSimulationEditorI18nServerManager.retrieveMessage(validationError);
                    errorMessage.append("<b>");
                    errorMessage.append(validationError.getErrorId());
                    errorMessage.append("</b> - ");
                    errorMessage.append(message);
                    errorMessage.append("<br/>");
                }

                confirmPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.validationErrorTitle(),
                                           errorMessage.toString());
            } else {
                sendNotification(ScenarioSimulationEditorConstants.INSTANCE.validationSucceed(), NotificationEvent.NotificationType.SUCCESS);
            }
        };
    }

    public ErrorCallback<Boolean> getValidationFailedCallback() {
        return (message, exception) -> {
            CustomBusyPopup.close();
            BusyPopup.close();
            sendNotification(ScenarioSimulationEditorConstants.INSTANCE.validationFailedNotification(), NotificationEvent.NotificationType.ERROR);
            return false;
        };
    }

    /**
     * Read only columns should not contains any values
     * @param abstractScesimModel
     */
    protected void cleanReadOnlyColumn(AbstractScesimModel abstractScesimModel) {
        ScesimModelDescriptor scesimModelDescriptor = abstractScesimModel.getScesimModelDescriptor();
        for (int i = 0; i < abstractScesimModel.getUnmodifiableData().size(); i += 1) {
            AbstractScesimData abstractScesimData = abstractScesimModel.getDataByIndex(i);
            for (FactMapping factMapping : scesimModelDescriptor.getUnmodifiableFactMappings()) {
                if (isColumnReadOnly(factMapping)) {
                    abstractScesimData.getFactMappingValue(factMapping.getFactIdentifier(),
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
            scenarioSimulationEditorWrapper.populateDocks(identifier);
        }
    }

    public void getModelSuccessCallbackMethod(DataManagementStrategy dataManagementStrategy, ScenarioSimulationModel model) {
        this.dataManagementStrategy = dataManagementStrategy;
        this.model = model;
        context.getStatus().setSimulation(model.getSimulation());
        context.getStatus().setBackground(model.getBackground());
        scenarioMainGridWidget.setContent(model.getSimulation(), model.getSettings().getType());
        scenarioBackgroundGridWidget.setContent(model.getBackground(), model.getSettings().getType());
        // NOTE: keep here initialization of docks related with model
        initializeDocks();
        populateRightDocks(TestToolsPresenter.IDENTIFIER);
        populateRightDocks(SettingsPresenter.IDENTIFIER);
        CustomBusyPopup.close();
        // Selecting and focusing current selected widget after a data model load
        context.getBackgroundGrid().select();

        context.getSelectedScenarioGridWidget().ifPresent(ScenarioGridWidget::selectAndFocus);
        // check if structure is valid
        getValidateCommand().execute();
        //force tab to be refreshed
        scenarioMainGridWidget.onResize();
    }

    public ScenarioSimulationResourceType getType() {
        return type;
    }

    public void setTestTools(TestToolsView.Presenter presenter) {
        context.setTestToolsPresenter(presenter);
        presenter.setEventBus(eventBus);
        GridWidget gridWidget = scenarioBackgroundGridWidget.isSelected() ? GridWidget.BACKGROUND : GridWidget.SIMULATION;
        dataManagementStrategy.populateTestTools(presenter, context, gridWidget);
    }

    protected void clearTestToolsStatus() {
        abstractScenarioSimulationDocksHandler.getTestToolsPresenter().onClearStatus();
    }

    public void setCheatSheet(CheatSheetView.Presenter presenter) {
        Type modelType = dataManagementStrategy instanceof AbstractDMODataManagementStrategy ? Type.RULE : Type.DMN;
        presenter.initCheatSheet(modelType);
    }

    public void setSettings(SettingsView.Presenter presenter) {
        Type modelType = dataManagementStrategy instanceof AbstractDMODataManagementStrategy ? Type.RULE : Type.DMN;
        presenter.setEventBus(eventBus);
        presenter.setScenarioType(modelType, model.getSettings(), path.getFileName());
    }

    protected void updateSettings(SettingsView.Presenter presenter) {
        presenter.updateSettingsData(model.getSettings());
    }

    public String getJsonModel(ScenarioSimulationModel model) {
        return "{}";
    }

    protected String getFileDownloadURL(final Supplier<Path> pathSupplier) {
        return GWT.getModuleBaseURL() + "defaulteditor/download?path=" + pathSupplier.get().toURI();
    }

    private Command createPopulateTestToolsCommand() {
        return () -> populateRightDocks(TestToolsPresenter.IDENTIFIER);
    }

    public void unpublishTestResultsAlerts() {
        scenarioSimulationEditorWrapper.unpublishTestResultsAlerts();
    }

    public Command getUpdateDMNMetadataCommand() { return () -> scenarioSimulationEditorWrapper.getDMNMetadata();}
}
