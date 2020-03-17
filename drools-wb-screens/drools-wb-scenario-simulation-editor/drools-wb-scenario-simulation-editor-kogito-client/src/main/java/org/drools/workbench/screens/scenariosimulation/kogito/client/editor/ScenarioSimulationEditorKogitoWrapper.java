/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.kogito.client.editor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.SCESIMMainJs;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.callbacks.SCESIMUnmarshallCallback;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenarioSimulationModelType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.SCESIM;
import org.drools.workbench.scenariosimulation.kogito.marshaller.mapper.JsUtils;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorWrapper;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoDMNService;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMNDataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies.KogitoDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.kogito.client.fakes.KogitoAsyncPackageDataModelOracle;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.client.resources.i18n.KogitoClientConstants;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.kogito.client.converters.scesim.ApiJSInteropConverter.getJSIScenarioSimulationModelType;
import static org.drools.workbench.screens.scenariosimulation.kogito.client.converters.scesim.JSInteropApiConverter.getScenarioSimulationModel;

/**
 * Wrapper to be used inside Kogito
 */
@Dependent
public class ScenarioSimulationEditorKogitoWrapper extends MultiPageEditorContainerPresenter<ScenarioSimulationModel> implements ScenarioSimulationEditorWrapper {

    public static final String DEFAULT_PACKAGE = "com";

    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    protected FileMenuBuilder fileMenuBuilder;
    protected AuthoringEditorDock authoringWorkbenchDocks;
    protected SCESIM scesimContainer;
    protected Promises promises;
    protected Path currentPath;
    protected KogitoDMNService dmnTypeService;
    protected KogitoAsyncPackageDataModelOracle kogitoOracle;
    protected TranslationService translationService;

    public ScenarioSimulationEditorKogitoWrapper() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorKogitoWrapper(
            final ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter,
            final FileMenuBuilder fileMenuBuilder,
            final PlaceManager placeManager,
            final MultiPageEditorContainerView multiPageEditorContainerView,
            final AuthoringEditorDock authoringWorkbenchDocks,
            final Promises promises,
            final KogitoDMNService dmnTypeService,
            final KogitoAsyncPackageDataModelOracle kogitoOracle,
            final TranslationService translationService) {
        super(scenarioSimulationEditorPresenter.getView(), placeManager, multiPageEditorContainerView);
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
        this.fileMenuBuilder = fileMenuBuilder;
        this.authoringWorkbenchDocks = authoringWorkbenchDocks;
        this.promises = promises;
        this.dmnTypeService = dmnTypeService;
        this.kogitoOracle = kogitoOracle;
        this.translationService = translationService;
    }

    @Override
    protected void buildMenuBar() {
        setMenus(fileMenuBuilder.build());
        getMenus().getItems().forEach(menuItem -> menuItem.setEnabled(true));
    }

    @Override
    public Promise<String> getContent() {
        return transform(scenarioSimulationEditorPresenter.getModel());
    }

    @Override
    public void setContent(String path, String value) {
        unmarshallContent(value);
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
    public void onExportToCsv(RemoteCallback<Object> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel) {
        //
    }

    @Override
    public void onDownloadReportToCsv(RemoteCallback<Object> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AuditLog auditLog) {
        //
    }

    @Override
    public void validate(Simulation simulation, Settings settings, RemoteCallback<?> callback) {
        //
    }

    /**
     * This method is called when the main grid tab (Model) is focused
     */
    @Override
    public void onEditTabSelected() {
        super.onEditTabSelected();
        scenarioSimulationEditorPresenter.onEditTabSelected();
    }

    /**
     * This method adds specifically the Background grid and its related onFocus behavior
     * @param scenarioGridWidget
     */
    @Override
    public void addBackgroundPage(final ScenarioGridWidget scenarioGridWidget) {
        final MultiPageEditorViewImpl editorMultiPageView = (MultiPageEditorViewImpl) getWidget().getMultiPage().getView();
        final String mainPageTitle = translationService.getTranslation(KogitoClientConstants.KieEditorWrapperView_EditTabTitle);
        final String backgroundPageTitle = ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle();
        final int mainPageIndex = editorMultiPageView.getPageIndex(mainPageTitle);
        final int backgroundPageIndex = mainPageIndex + 1;
        editorMultiPageView.addPage(backgroundPageIndex, new PageImpl(scenarioGridWidget, backgroundPageTitle) {
            @Override
            public void onFocus() {
                super.onFocus();
                onBackgroundTabSelected();
            }
        });
    }

    @Override
    public void selectSimulationTab() {
        final MultiPageEditorViewImpl editorMultiPageView = (MultiPageEditorViewImpl) getWidget().getMultiPage().getView();
        final int pageIndex = editorMultiPageView.getPageIndex(translationService.getTranslation(KogitoClientConstants.KieEditorWrapperView_EditTabTitle));
        final TabListItem item = (TabListItem) editorMultiPageView.getTabBar().getWidget(pageIndex);
        if (item != null) {
            item.showTab(false);
        }
    }

    @Override
    public void selectBackgroundTab() {
        final MultiPageEditorViewImpl editorMultiPageView = (MultiPageEditorViewImpl) getWidget().getMultiPage().getView();
        final int pageIndex = editorMultiPageView.getPageIndex(ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle());
        final TabListItem item = (TabListItem) editorMultiPageView.getTabBar().getWidget(pageIndex);
        if (item != null) {
            item.showTab(false);
        }
    }

    @Override
    public void resetContentHash() {
        //
    }

    public void onStartup(final PlaceRequest place) {
        super.init(place);
        resetEditorPages();
        authoringWorkbenchDocks.setup("AuthoringPerspective", place);
        SCESIMMainJs.initializeJsInteropConstructors(SCESIMMainJs.getConstructorsMap());
        MainJs.initializeJsInteropConstructors(MainJs.getConstructorsMap());
    }

    public void gotoPath(Path path) {
        resetEditorPages();
        kogitoOracle.init(path);
        currentPath = path;
        scenarioSimulationEditorPresenter.init(this, new ObservablePathImpl().wrap(path));
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    @Override
    public boolean mayClose() {
        return !scenarioSimulationEditorPresenter.isDirty();
    }

    @Override
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    public MultiPageEditorContainerView getWidget() {
        return super.getWidget();
    }

    public FileMenuBuilder getFileMenuBuilder() {
        return fileMenuBuilder;
    }

    public void setMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(getMenus());
    }

    @Override
    public void onRunScenario(RemoteCallback<SimulationRunResult> refreshModelCallback, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, ScesimModelDescriptor simulationDescriptor, Settings settings, List<ScenarioWithIndex> toRun, Background background) {
        scenarioSimulationEditorPresenter.getView().hideBusyIndicator();
        new PopupPanel().show();
    }

    @Override
    public void wrappedSave(String commitMessage) {
        synchronizeColumnsDimension(scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.SIMULATION),
                                    scenarioSimulationEditorPresenter.getContext().getScenarioGridPanelByGridWidget(GridWidget.BACKGROUND));
    }

    @Override
    public Integer getOriginalHash() {
        return super.getOriginalContentHash();
    }

    protected Promise<String> transform(final ScenarioSimulationModel resource) {
        return promises.create((resolveCallbackFn, rejectCallbackFn) -> marshallContent(resource, resolveCallbackFn));
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected void makeMenuBar() {
        scenarioSimulationEditorPresenter.makeMenuBar(fileMenuBuilder);
    }

    @Override
    protected Supplier<ScenarioSimulationModel> getContentSupplier() {
        return () -> scenarioSimulationEditorPresenter.getModel();
    }

    protected void marshallContent(ScenarioSimulationModel scenarioSimulationModel, Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallbackFn) {
        final JSIScenarioSimulationModelType jsiScenarioSimulationModelType = getJSIScenarioSimulationModelType(scenarioSimulationModel);
        JsUtils.setValueOnWrapped(scesimContainer, jsiScenarioSimulationModelType);
        SCESIMMainJs.marshall(scesimContainer, "scesim", getJSInteropMarshallCallback(resolveCallbackFn));
    }

    protected void unmarshallContent(String toUnmarshal) {
        SCESIMMainJs.unmarshall(toUnmarshal, "scesim", getJSInteropUnmarshallCallback());
    }

    protected SCESIMMarshallCallback getJSInteropMarshallCallback(Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallbackFn) {
        return resolveCallbackFn::onInvoke;
    }

    protected SCESIMUnmarshallCallback getJSInteropUnmarshallCallback() {
        return scesim -> {
            this.scesimContainer = scesim;
            final JSIScenarioSimulationModelType scenarioSimulationModelType = Js.uncheckedCast(JsUtils.getUnwrappedElement(scesim));
            final ScenarioSimulationModel scenarioSimulationModel = getScenarioSimulationModel(scenarioSimulationModelType);
            getModelSuccessCallbackMethod(scenarioSimulationModel);
        };
    }

    protected void getModelSuccessCallbackMethod(ScenarioSimulationModel model) {
        scenarioSimulationEditorPresenter.setPackageName(DEFAULT_PACKAGE);
        DataManagementStrategy dataManagementStrategy;
        if (ScenarioSimulationModel.Type.RULE.equals(model.getSettings().getType())) {
            dataManagementStrategy = new KogitoDMODataManagementStrategy(kogitoOracle);
        } else {
            dataManagementStrategy = new KogitoDMNDataManagementStrategy(scenarioSimulationEditorPresenter.getEventBus(), dmnTypeService);
        }
        dataManagementStrategy.setModel(model);
        setOriginalContentHash(scenarioSimulationEditorPresenter.getJsonModel(model).hashCode());
        scenarioSimulationEditorPresenter.getModelSuccessCallbackMethod(dataManagementStrategy, model);
        scenarioSimulationEditorPresenter.showDocks(PlaceStatus.CLOSE);
    }

    protected void onBackgroundTabSelected() {
        scenarioSimulationEditorPresenter.onBackgroundTabSelected();
    }
}
