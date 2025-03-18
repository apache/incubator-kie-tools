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


package org.drools.workbench.screens.scenariosimulation.kogito.client.editor;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.SCESIMMainJs;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMUnmarshallCallback;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenarioSimulationModelType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.SCESIM;
import org.drools.workbench.scenariosimulation.kogito.marshaller.mapper.JSIName;
import org.drools.workbench.scenariosimulation.kogito.marshaller.mapper.JsUtils;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorWrapper;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoScenarioSimulationBuilder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.ScenarioSimulationKogitoDMNDataManager;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.model.KogitoDMNModel;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmo.KogitoAsyncPackageDataModelOracle;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.handlers.ScenarioSimulationKogitoDocksHandler;
import org.drools.workbench.screens.scenariosimulation.kogito.client.popup.ScenarioSimulationKogitoCreationPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.errorpage.ErrorPage;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.scenariosimulation.kogito.client.converters.ApiJSInteropConverter.getJSIScenarioSimulationModelType;
import static org.drools.workbench.screens.scenariosimulation.kogito.client.converters.JSInteropApiConverter.getScenarioSimulationModel;

/**
 * Wrapper to be used inside Kogito
 */
@Dependent
public class ScenarioSimulationEditorKogitoWrapper extends MultiPageEditorContainerPresenter<ScenarioSimulationModel>
        implements ScenarioSimulationEditorWrapper {

    protected static final String DEFAULT_PACKAGE = "com";
    protected static final String NEW_FILE_NAME = "new-file.scesim";
    protected static final String SCESIM = "scesim";

    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    protected AuthoringEditorDock authoringWorkbenchDocks;
    protected SCESIM scesimContainer;
    protected Promises promises;
    protected Path currentPath;
    protected ScenarioSimulationKogitoDMNDataManager dmnDataManager;
    protected KogitoAsyncPackageDataModelOracle kogitoOracle;
    protected TranslationService translationService;
    protected ScenarioSimulationKogitoCreationPopupPresenter scenarioSimulationKogitoCreationPopupPresenter;
    protected KogitoScenarioSimulationBuilder scenarioSimulationBuilder;
    protected ScenarioSimulationKogitoDocksHandler scenarioSimulationKogitoDocksHandler;
    protected ScenarioSimulationKogitoDMNMarshallerService scenarioSimulationKogitoDMNMarshallerService;
    protected ErrorPage errorPage;

    public ScenarioSimulationEditorKogitoWrapper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorKogitoWrapper(
            final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter,
            final MultiPageEditorContainerView multiPageEditorContainerView,
            final AuthoringEditorDock authoringWorkbenchDocks,
            final Promises promises,
            final ScenarioSimulationKogitoDMNDataManager dmnDataManager,
            final KogitoAsyncPackageDataModelOracle kogitoOracle,
            final TranslationService translationService,
            final ScenarioSimulationKogitoCreationPopupPresenter scenarioSimulationKogitoCreationPopupPresenter,
            final KogitoScenarioSimulationBuilder scenarioSimulationBuilder,
            final ScenarioSimulationKogitoDocksHandler scenarioSimulationKogitoDocksHandler,
            final ScenarioSimulationKogitoDMNMarshallerService scenarioSimulationKogitoDMNMarshallerService,
            final ErrorPage errorPage) {
        super(scenarioSimulationEditorPresenter.getView(), multiPageEditorContainerView);
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.authoringWorkbenchDocks = authoringWorkbenchDocks;
        this.promises = promises;
        this.dmnDataManager = dmnDataManager;
        this.kogitoOracle = kogitoOracle;
        this.translationService = translationService;
        this.scenarioSimulationBuilder = scenarioSimulationBuilder;
        this.scenarioSimulationKogitoCreationPopupPresenter = scenarioSimulationKogitoCreationPopupPresenter;
        this.scenarioSimulationKogitoDocksHandler = scenarioSimulationKogitoDocksHandler;
        this.scenarioSimulationKogitoDMNMarshallerService = scenarioSimulationKogitoDMNMarshallerService;
        this.errorPage = errorPage;
    }

    @PostConstruct
    public void init() {
        scenarioSimulationEditorPresenter.getView().onResize();
        HTMLCollection<Element> elements = DomGlobal.document.getElementsByClassName("tab-content");
        if(elements.length == 1) {
            HTMLDivElement element = (HTMLDivElement) elements.getAt(0);
            element.style.height = CSSProperties.HeightUnionType.of("calc(100vh - 36px)");
        }
    }

    @Override
    public Promise<String> getContent() {
        return promises.create(this::prepareContent);
    }

    @Override
    public Promise<Void> setContent(String fullPath, String content) {
        return promises.create((success, failure) -> manageContent(fullPath, content, success, failure));
    }

    /**
     * It manages the logic invoked in the Promise created during a <code>setContent()</code> call
     * @param fullPath
     * @param content
     * @param success
     * @param failure
     */
    protected void manageContent(String fullPath,
                                 String content,
                                 Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Void> success,
                                 Promise.PromiseExecutorCallbackFn.RejectCallbackFn failure) {
        try {
            /* Retrieving file name and its relative path */
            String finalName = NEW_FILE_NAME;
            String pathString = "/";
            if (fullPath != null && !fullPath.isEmpty()) {
                int idx = fullPath.replaceAll("\\\\", "/").lastIndexOf('/');
                finalName = idx >= 0 ? fullPath.substring(idx + 1) : fullPath;
                pathString = idx >= 0 ? fullPath.substring(0, idx + 1) : pathString;
            }
            final Path path = PathFactory.newPath(finalName, pathString);
            /* If given content is null, a new file has to be created. */
            /* Otherwise, the content is un-marshalled and shown in the editor */
            if (content == null || content.isEmpty()) {
                showScenarioSimulationCreationPopup(path);
            } else {
                gotoPath(path);
                unmarshallContent(content);
            }
            success.onInvoke((Void) null);
        } catch (Exception e) {
            /* If any exception occurs, promise returns a failure */
            setErrorPage(e.getMessage());
            failure.onInvoke(e.getMessage());
        }
    }

    private void setErrorPage(final String errorMessage) {
        errorPage.setTitle(ScenarioSimulationEditorConstants.INSTANCE.scenarioParsingError());
        errorPage.setContent(ScenarioSimulationEditorConstants.INSTANCE.scenarioParsingErrorContent());
        errorPage.setErrorContent(errorMessage);
        scenarioSimulationEditorPresenter.getView().setContentWidget(errorPage);
        scenarioSimulationEditorPresenter.hideDocks();
        scenarioSimulationEditorPresenter.getView().setScenarioTabBarVisibility(false);
    }

    private void ensureScenarioGridIsSet() {
        scenarioSimulationEditorPresenter.getView().setScenarioGridWidgetAsContent();
        scenarioSimulationEditorPresenter.getView().setScenarioTabBarVisibility(true);
    }

    public void prepareContent(Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> success,
                               Promise.PromiseExecutorCallbackFn.RejectCallbackFn failure) {
        try {
            synchronizeColumnsDimension(scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.SIMULATION),
                                        scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND));
            marshallContent(scenarioSimulationEditorPresenter.getModel(), success);
        } catch (Exception e) {
            /* If any exception occurs, promise returns a failure */
            scenarioSimulationEditorPresenter.sendNotification(e.getMessage(), NotificationEvent.NotificationType.ERROR);
            failure.onInvoke(e.getMessage());
        }
    }

    @Override
    public void wrappedRegisterDock(String id, IsWidget widget) {
        //
    }

    @Override
    public void onImport(String fileContents, RemoteCallback<AbstractScesimModel> importCallBack, ErrorCallback<Object> importErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        //
    }

    @Override
    public void onExportToCsv(RemoteCallback<String> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        //
    }

    @Override
    public void onDownloadReportToCsv(RemoteCallback<String> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, SimulationRunMetadata simulationRunMetadata, ScenarioSimulationModel.Type modelType) {
        //
    }

    @Override
    public void validate(Simulation simulation, Settings settings, RemoteCallback<?> callback) {
        //
    }

    @Override
    public void onRefreshedModelContent(SimulationRunResult testResultMessage) {
        /* No actions required after data refresh in Kogito */
    }

    /**
     * This method is called when the main grid tab (Model) is focused
     */
    public void onEditTabSelected() {
        scenarioSimulationEditorPresenter.onEditTabSelected();
    }

    /**
     * This method adds specifically the Background grid and its related onFocus behavior
     * @param backgroundGridWidget
     */
    @Override
    public void addBackgroundPage(final ScenarioGridWidget backgroundGridWidget) {
        final String backgroundPageTitle = ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle();
        getWidget().getMultiPage().addPage(new PageImpl(backgroundGridWidget, backgroundPageTitle) {
            @Override
            public void onFocus() {
                super.onFocus();
                onBackgroundTabSelected();
            }
        });
    }

    @Override
    public void selectSimulationTab() {
    }

    @Override
    public void selectBackgroundTab() {
    }

    @Override
    public void unpublishTestResultsAlerts() {
        // Not used in Kogito
    }

    @Override
    public void getDMNMetadata() {
        final String dmnFilePath = getScenarioSimulationEditorPresenter().getModel().getSettings().getDmnFilePath();
        final String dmnFileName = dmnFilePath.substring(dmnFilePath.lastIndexOf('/') + 1);
        final Path dmnPath = PathFactory.newPath(dmnFileName, dmnFilePath);
        scenarioSimulationKogitoDMNMarshallerService.getDMNContent(
                dmnPath,
                getUpdateDMNMetadataCallback(),
                getDMNContentErrorCallback(dmnFilePath));
    }

    private Callback<KogitoDMNModel> getUpdateDMNMetadataCallback() {
        return kogitoDMNModel -> {
            getScenarioSimulationEditorPresenter().getModel().getSettings().setDmnName(kogitoDMNModel.getName());
            getScenarioSimulationEditorPresenter().getModel().getSettings().setDmnNamespace(kogitoDMNModel.getNamespace());
            getScenarioSimulationEditorPresenter().reloadSettingsDock();
        };
    }

    @Override
    public AbstractScenarioSimulationDocksHandler getScenarioSimulationDocksHandler() {
        return scenarioSimulationKogitoDocksHandler;
    }

    @Override
    public ScenarioSimulationEditorPresenter getScenarioSimulationEditorPresenter() {
        return scenarioSimulationEditorPresenter;
    }

    public void resetContentHash() {
        //
    }

    public void onStartup(final PlaceRequest place) {
        super.init(place);
        resetEditorPages();
        authoringWorkbenchDocks.setup("AuthoringPerspective", place);
        scenarioSimulationEditorPresenter.setWrapper(this);
    }

    public void gotoPath(Path path) {
        kogitoOracle.init(path);
        currentPath = path;
        scenarioSimulationEditorPresenter.setPath(new ObservablePathImpl().wrap(path));
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public boolean mayClose() {
        return !scenarioSimulationEditorPresenter.isDirty();
    }

    @Override
    public MultiPageEditorContainerView getWidget() {
        return super.getWidget();
    }

    @Override
    public void onRunScenario(RemoteCallback<SimulationRunResult> refreshModelCallback, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, ScesimModelDescriptor simulationDescriptor, Settings settings, List<ScenarioWithIndex> toRun, Background background) {
        scenarioSimulationEditorPresenter.getView().hideBusyIndicator();
        new PopupPanel().show();
    }

    @Override
    public Integer getOriginalHash() {
        return -1;
    }

    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return () -> scenarioSimulationEditorPresenter.getModel();
    }

    protected void marshallContent(ScenarioSimulationModel scenarioSimulationModel, Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallbackFn) {
        if (scesimContainer == null) {
            scesimContainer = Js.uncheckedCast(JsUtils.newWrappedInstance());
            JsUtils.setNameOnWrapped(scesimContainer, makeJSINameForSCESIM());
        }
        JsUtils.setValueOnWrapped(scesimContainer, getJSIScenarioSimulationModelType(scenarioSimulationModel));
        SCESIMMainJs.marshall(scesimContainer, null, getJSInteropMarshallCallback(resolveCallbackFn));
    }

    protected void unmarshallContent(String toUnmarshal) {
        /* Removing the default namespace introduced in the new Test Scenario editor, that is incompatible with this unmarshaller implementation. */
        toUnmarshal = toUnmarshal.replace("xmlns=\"https://kie.org/scesim/1.8\"", "");
        SCESIMMainJs.unmarshall(toUnmarshal, SCESIM, getJSInteropUnmarshallCallback());
    }

    private JSIName makeJSINameForSCESIM() {
        final JSIName jsiName = JSIScenarioSimulationModelType.getJSIName();
        jsiName.setPrefix("");
        jsiName.setLocalPart("ScenarioSimulationModel");
        final String key = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getLocalPart();
        final String keyString = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getPrefix() + ":" + jsiName.getLocalPart();
        jsiName.setKey(key);
        jsiName.setString(keyString);
        return jsiName;
    }

    protected SCESIMMarshallCallback getJSInteropMarshallCallback(Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallbackFn) {
        return resolveCallbackFn::onInvoke;
    }

    protected SCESIMUnmarshallCallback getJSInteropUnmarshallCallback() {
        return scesim -> {
            this.scesimContainer = scesim;
            final JSIScenarioSimulationModelType scenarioSimulationModelType = Js.uncheckedCast(JsUtils.getUnwrappedElement(scesim));
            final ScenarioSimulationModel scenarioSimulationModel = getScenarioSimulationModel(scenarioSimulationModelType);
            onModelSuccessCallbackMethod(scenarioSimulationModel);
        };
    }

    protected void onModelSuccessCallbackMethod(ScenarioSimulationModel model) {
        scenarioSimulationEditorPresenter.setPackageName(DEFAULT_PACKAGE);
        DataManagementStrategy dataManagementStrategy;
        if (ScenarioSimulationModel.Type.RULE.equals(model.getSettings().getType())) {
            scenarioSimulationEditorPresenter.sendNotification(ScenarioSimulationEditorConstants.INSTANCE.ruleScenarioNotSupportedNotification(),
                                                               NotificationEvent.NotificationType.WARNING,
                                                               false);
            dataManagementStrategy = new KogitoDMODataManagementStrategy(kogitoOracle);
        } else {
            dataManagementStrategy = new KogitoDMNDataManagementStrategy(dmnDataManager,
                                                                         scenarioSimulationKogitoDMNMarshallerService,
                                                                         scenarioSimulationEditorPresenter);
        }
        dataManagementStrategy.setModel(model);
        scenarioSimulationEditorPresenter.getModelSuccessCallbackMethod(dataManagementStrategy, model);
        ensureScenarioGridIsSet();
    }

    protected void onBackgroundTabSelected() {
        scenarioSimulationEditorPresenter.onBackgroundTabSelected();
    }

    /**
     * It shows the <code>ScenarioSimulationKogitoCreationPopup</code> given the file location, used
     * @param path
     */
    protected void showScenarioSimulationCreationPopup(Path path) {
        scenarioSimulationKogitoCreationPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.addScenarioSimulation(),
                                                            createNewFileCommand(path));
    }

    /**
     * It creates the command launched when a user wants to create a new Scenario Simulation file.
     * @param scesimPath The part where the scesim file is located
     * @return A <code>Command</code> which will be launched pressing the 'Create' new scesim file button
     */
    protected Command createNewFileCommand(Path scesimPath) {
        return () -> {
            final ScenarioSimulationModel.Type selectedType = scenarioSimulationKogitoCreationPopupPresenter.getSelectedType();
            if (selectedType == null) {
                scenarioSimulationEditorPresenter.sendNotification(ScenarioSimulationEditorConstants.INSTANCE.missingSelectedType(),
                                                                   NotificationEvent.NotificationType.ERROR,
                                                                   false);
                return;
            }
            gotoPath(scesimPath);
            if (ScenarioSimulationModel.Type.DMN.equals(selectedType)) {
                String dmnPath = scenarioSimulationKogitoCreationPopupPresenter.getSelectedPath();
                if (dmnPath == null || dmnPath.isEmpty()) {
                    scenarioSimulationEditorPresenter.sendNotification(ScenarioSimulationEditorConstants.INSTANCE.missingDmnPath(),
                                                                       NotificationEvent.NotificationType.ERROR,
                                                                       false);
                    return;
                }
                scenarioSimulationBuilder.populateScenarioSimulationModelDMN(dmnPath,
                                                                             this::onModelSuccessCallbackMethod,
                                                                             getDMNContentErrorCallback(dmnPath));
            } else {
                scenarioSimulationBuilder.populateScenarioSimulationModelRULE("",
                                                                              this::onModelSuccessCallbackMethod);
            }
        };
    }

    private ErrorCallback<Object> getDMNContentErrorCallback(String dmnFilePath) {
        return (message, throwable) -> {
            scenarioSimulationEditorPresenter.sendNotification(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorDetailedLabel(dmnFilePath,
                                                                                                                                    message.toString()),
                                                               NotificationEvent.NotificationType.ERROR,
                                                               false);
            return false;
        };
    }

}
