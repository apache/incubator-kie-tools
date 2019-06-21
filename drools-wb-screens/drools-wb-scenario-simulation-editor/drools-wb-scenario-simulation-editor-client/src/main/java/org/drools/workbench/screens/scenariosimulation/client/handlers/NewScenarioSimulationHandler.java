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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.dropdown.ScenarioSimulationDropdown;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.CustomBusyPopup;
import org.drools.workbench.screens.scenariosimulation.client.resources.ScenarioSimulationEditorResources;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Scenario Simulation
 */
@ApplicationScoped
public class NewScenarioSimulationHandler
        extends DefaultNewResourceHandler {

    protected TitledAttachmentFileWidget uploadWidget;

    protected SourceTypeSelector sourceTypeSelector;

    private Caller<ScenarioSimulationService> scenarioSimulationService;

    private ScenarioSimulationResourceType resourceType;

    private final AuthorizationManager authorizationManager;
    private final SessionInfo sessionInfo;
    private final ScenarioSimulationDropdown scenarioSimulationDropdown;

    @Inject
    public NewScenarioSimulationHandler(final ScenarioSimulationResourceType resourceType,
                                        final BusyIndicatorView busyIndicatorView,
                                        final Event<NotificationEvent> notificationEvent,
                                        final Event<NewResourceSuccessEvent> newResourceSuccessEvent,
                                        final PlaceManager placeManager,
                                        final Caller<ScenarioSimulationService> scenarioSimulationService,
                                        final AuthorizationManager authorizationManager,
                                        final SessionInfo sessionInfo,
                                        final ScenarioSimulationDropdown scenarioSimulationDropdown) {
        this.resourceType = resourceType;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.newResourceSuccessEvent = newResourceSuccessEvent;
        this.busyIndicatorView = busyIndicatorView;
        this.scenarioSimulationService = scenarioSimulationService;
        this.placeManager = placeManager;
        this.notificationEvent = notificationEvent;
        this.scenarioSimulationDropdown = scenarioSimulationDropdown;
    }

    @Override
    public String getDescription() {
        return ScenarioSimulationEditorConstants.INSTANCE.newScenarioSimulationDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(ScenarioSimulationEditorResources.INSTANCE.images().typeScenarioSimulation());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public boolean canCreate() {
        return authorizationManager.authorize(new ResourceRef(ScenarioSimulationEditorPresenter.IDENTIFIER,
                                                              ActivityResourceType.EDITOR),
                                              ResourceAction.READ,
                                              sessionInfo.getIdentity());
    }

    @Override
    public Command getCommand(final NewResourcePresenter newResourcePresenter) {
        return () -> getCommandMethod(newResourcePresenter);
    }

    @Override
    public void create(final Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {
        if (!sourceTypeSelector.validate()) {
            return;
        }
        final ScenarioSimulationModel.Type selectedType = sourceTypeSelector.getSelectedType();
        String value;
        switch (selectedType) {
            case DMN:
                value = uploadWidget.getSelectedPath();
                break;
            case RULE:
            default:
                value = null;
        }
        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());
        CustomBusyPopup.showMessage(CommonConstants.INSTANCE.Saving());
        scenarioSimulationService.call(getSuccessCallback(presenter),
                                       new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).create(pkg.getPackageTestResourcesPath(),
                                                                                                                             buildFileName(baseFileName,
                                                                                                                                           resourceType),
                                                                                                                             new ScenarioSimulationModel(),
                                                                                                                             "",
                                                                                                                             selectedType,
                                                                                                                             value);
    }

    @PostConstruct
    public void setupExtensions() {
        uploadWidget = new TitledAttachmentFileWidget(ScenarioSimulationEditorConstants.INSTANCE.chooseDMN(),
                                                      scenarioSimulationService, scenarioSimulationDropdown);
        sourceTypeSelector = new SourceTypeSelector(uploadWidget);
        extensions.add(Pair.newPair(ScenarioSimulationEditorConstants.INSTANCE.sourceType(), sourceTypeSelector));
        extensions.add(Pair.newPair("", uploadWidget));
    }

    protected void getCommandMethod(NewResourcePresenter newResourcePresenter) {
        uploadWidget.clearStatus();
        newResourcePresenter.show(NewScenarioSimulationHandler.this);
    }
}