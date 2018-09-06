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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.RightPanelMenuItem;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.soup.project.datamodel.oracle.ModelField;
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
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
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

    private RightPanelMenuItem rightPanelMenuItem;

    private Command populateRightPanelCommand;

    PlaceRequest rightPanelRequest;

    ObservablePath path;

    public ScenarioSimulationEditorPresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public ScenarioSimulationEditorPresenter(final Caller<ScenarioSimulationService> service,
                                             final ScenarioSimulationView view,
                                             final ScenarioSimulationResourceType type,
                                             final ImportsWidgetPresenter importsWidget,
                                             final AsyncPackageDataModelOracleFactory oracleFactory,
                                             final RightPanelMenuItem rightPanelMenuItem,
                                             final PlaceManager placeManager) {
        super(view);
        this.view = view;
        this.baseView = view;
        this.service = service;
        this.type = type;
        this.importsWidget = importsWidget;
        this.oracleFactory = oracleFactory;
        this.rightPanelMenuItem = rightPanelMenuItem;
        this.placeManager = placeManager;

        addMenuItems();

        view.init(this);

        rightPanelRequest = new DefaultPlaceRequest(RightPanelPresenter.IDENTIFIER);
        rightPanelRequest.addParameter("ScenarioSimulationEditorPresenter", this.toString());

        rightPanelMenuItem.init(rightPanelRequest);

        populateRightPanelCommand = getPopulateRightPanelCommand();
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
        this.path = path;
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
        if (PlaceStatus.OPEN.equals(placeManager.getStatus(rightPanelRequest))) {
            placeManager.closePlace(rightPanelRequest);
            this.view.showLoading();
        }
        this.view.clear();
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
        if (!(placeGainFocusEvent.getPlace() instanceof PathPlaceRequest)) {  // Ignoring other requests
            return;
        }
        PathPlaceRequest placeRequest = (PathPlaceRequest) placeGainFocusEvent.getPlace();
        if (placeRequest.getIdentifier().equals(ScenarioSimulationEditorPresenter.IDENTIFIER)
                && placeRequest.getPath().equals(this.path)
                && PlaceStatus.CLOSE.equals(placeManager.getStatus(rightPanelRequest))) {
            registerRightPanelCallback();
            placeManager.goTo(rightPanelRequest);
            populateRightPanel();
        }
    }

    // Observing to hide RightPanel when ScenarioSimulationScreen is put in background
    public void onPlaceHiddenEvent(@Observes PlaceHiddenEvent placeHiddenEvent) {
        if (!(placeHiddenEvent.getPlace() instanceof PathPlaceRequest)) {  // Ignoring other requests
            return;
        }
        PathPlaceRequest placeRequest = (PathPlaceRequest) placeHiddenEvent.getPlace();
        if (placeRequest.getIdentifier().equals(ScenarioSimulationEditorPresenter.IDENTIFIER)
                && placeRequest.getPath().equals(this.path)
                && PlaceStatus.OPEN.equals(placeManager.getStatus(rightPanelRequest))) {
            unRegisterRightPanelCallback();
            clearRightPanelStatus();
            placeManager.closePlace(rightPanelRequest);
        }
    }

    public ScenarioSimulationView getView() {
        return view;
    }

    public ScenarioSimulationModel getModel() {
        return model;
    }

    public void onRunScenario() {
        service.call().runScenario(versionRecordManager.getCurrentPath(),
                                   model);
    }

    protected void registerRightPanelCallback() {
        placeManager.registerOnOpenCallback(rightPanelRequest, populateRightPanelCommand);
        placeManager.registerOnOpenCallback(rightPanelRequest, rightPanelMenuItem.getSetButtonTextTrue());
        placeManager.registerOnCloseCallback(rightPanelRequest, rightPanelMenuItem.getSetButtonTextFalse());
    }

    protected void unRegisterRightPanelCallback() {
        placeManager.getOnOpenCallbacks(rightPanelRequest).remove(populateRightPanelCommand);
        placeManager.getOnOpenCallbacks(rightPanelRequest).remove(rightPanelMenuItem.getSetButtonTextTrue());
        placeManager.getOnCloseCallbacks(rightPanelRequest).remove(rightPanelMenuItem.getSetButtonTextFalse());
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected void makeMenuBar() {
        fileMenuBuilder.addNewTopLevelMenu(view.getRunScenarioMenuItem());
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

    void populateRightPanel() {
        // Execute only when RightPanelPresenter is actually available
        getRightPanelPresenter().ifPresent(this::populateRightPanel);
    }

    void populateRightPanel(RightPanelView.Presenter rightPanelPresenter) {
        GWT.log("ScenarioSimulationPResenter " + this.toString() + " populateRightPanel rightPanelPresenter " + rightPanelPresenter.toString());
        // Instantiate a container map
        SortedMap<String, FactModelTree> factTypeFieldsMap = new TreeMap<>();
        // Execute only when oracle has been set
        if (oracle == null) {
            if (rightPanelPresenter != null) {
                rightPanelPresenter.setFactTypeFieldsMap(factTypeFieldsMap);
            }
            return;
        }
        // Retrieve the relevant facttypes
        String[] factTypes = oracle.getFactTypes();
        if (factTypes.length == 0) {  // We do not have to set nothing
            if (rightPanelPresenter != null) {
                rightPanelPresenter.setFactTypeFieldsMap(factTypeFieldsMap);
            }
            return;
        }
        // Instantiate the aggregator callback
        Callback<FactModelTree> aggregatorCallback = aggregatorCallback(rightPanelPresenter, factTypes.length, factTypeFieldsMap);
        // Iterate over all facttypes to retrieve their modelfields
        for (String factType : factTypes) {
            oracle.getFieldCompletions(factType, fieldCompletionsCallback(factType, aggregatorCallback));
        }
    }

    void clearRightPanelStatus() {
        getRightPanelPresenter().ifPresent(RightPanelView.Presenter::onClearStatus);
    }

    private void addMenuItems() {
        view.addGridMenuItem("one", "ONE", "", () -> GWT.log("ONE COMMAND"));
        view.addGridMenuItem("two", "TWO", "", () -> GWT.log("TWO COMMAND"));
        view.addHeaderMenuItem("one", "HEADER-ONE", "", () -> GWT.log("HEADER-ONE COMMAND"));
        view.addHeaderMenuItem("two", "HEADER-TWO", "", () -> GWT.log("HEADER-TWO COMMAND"));
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
            populateRightPanel();
            importsWidget.setContent(oracle,
                                     model.getImports(),
                                     isReadOnly);
            addImportsTab(importsWidget);
            baseView.hideBusyIndicator();
            view.setContent(model.getSimulation());
            createOriginalHash(model.hashCode());
        };
    }

    private void addRightPanelMenuItem(final FileMenuBuilder fileMenuBuilder) {
        fileMenuBuilder.addNewTopLevelMenu(rightPanelMenuItem);
    }

    /**
     * This <code>Callback</code> will receive <code>ModelField[]</code> from <code>AsyncPackageDataModelOracleFactory.getFieldCompletions(final String,
     * final Callback&lt;ModelField[]&gt;)</code>; build a <code>FactModelTree</code> from them, and send it to the
     * given <code>Callback&lt;FactModelTree&gt;</code> aggregatorCallback
     * @param factName
     * @param aggregatorCallback
     * @return
     */
    private Callback<ModelField[]> fieldCompletionsCallback(String factName, Callback<FactModelTree> aggregatorCallback) {
        return result -> {
            Map<String, String> simpleProperties = new HashMap<>();
            for (ModelField modelField : result) {
                if (!modelField.getName().equals("this")) {
                    simpleProperties.put(modelField.getName(), modelField.getClassName());
                }
            }
            FactModelTree toSend = new FactModelTree(factName, simpleProperties);
            aggregatorCallback.callback(toSend);
        };
    }

    /**
     * This <code>Callback</code> will receive data from other callbacks and when the retrieved results get to the
     * expected ones it will recursively elaborate the map
     * @param rightPanelPresenter
     * @param expectedElements
     * @param factTypeFieldsMap
     * @return
     */
    private Callback<FactModelTree> aggregatorCallback(final RightPanelView.Presenter rightPanelPresenter, final int expectedElements, SortedMap<String, FactModelTree> factTypeFieldsMap) {
        return result -> {
            factTypeFieldsMap.put(result.getFactName(), result);
            if (factTypeFieldsMap.size() == expectedElements) {
                factTypeFieldsMap.values().forEach(factModelTree -> populateFactModel(factModelTree, factTypeFieldsMap));
                rightPanelPresenter.setFactTypeFieldsMap(factTypeFieldsMap);
            }
        };
    }

    private void populateFactModel(FactModelTree toPopulate, SortedMap<String, FactModelTree> factTypeFieldsMap) {
        List<String> toRemove = new ArrayList<>();
        toPopulate.getSimpleProperties().forEach((key, value) -> {
            if (factTypeFieldsMap.containsKey(value)) {
                toRemove.add(key);
                toPopulate.addExpandableProperty(key, factTypeFieldsMap.get(value).getFactName());
            }
        });
        toRemove.forEach(toPopulate::removeSimpleProperty);
    }

    private Optional<RightPanelView> getRightPanelView() {
        if (PlaceStatus.OPEN.equals(placeManager.getStatus(rightPanelRequest))) {
            final AbstractWorkbenchActivity rightPanelActivity = (AbstractWorkbenchActivity) placeManager.getActivity(rightPanelRequest);
            return Optional.of((RightPanelView) rightPanelActivity.getWidget());
        } else {
            return Optional.empty();
        }
    }

    private Optional<RightPanelView.Presenter> getRightPanelPresenter() {
        return getRightPanelView().isPresent() ? Optional.of(getRightPanelView().get().getPresenter()) : Optional.empty();
    }

    private Command getPopulateRightPanelCommand() {
        return this::populateRightPanel;
    }
}
