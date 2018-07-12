/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.AddNewProviderEvent;
import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.guvnor.ala.ui.client.events.AddNewRuntimeEvent;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.wizard.EnableProviderTypeWizard;
import org.guvnor.ala.ui.client.wizard.NewDeployWizard;
import org.guvnor.ala.ui.client.wizard.NewProviderWizard;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.guvnor.ala.ui.client.ProvisioningManagementPerspective.IDENTIFIER;

@ApplicationScoped
@WorkbenchPerspective(identifier = IDENTIFIER)
public class ProvisioningManagementPerspective {

    public static final String IDENTIFIER = "ProvisioningManagementPerspective";

    private final Caller<ProviderTypeService> providerTypeService;
    private final Caller<RuntimeService> runtimeService;
    private final EnableProviderTypeWizard enableProviderTypeWizard;
    private final NewProviderWizard newProviderWizard;
    private final NewDeployWizard newDeployWizard;
    private final ClientProviderHandlerRegistry handlerRegistry;

    @Inject
    public ProvisioningManagementPerspective(final Caller<ProviderTypeService> providerTypeService,
                                             final Caller<RuntimeService> runtimeService,
                                             final EnableProviderTypeWizard enableProviderTypeWizard,
                                             final NewProviderWizard newProviderWizard,
                                             final NewDeployWizard newDeployWizard,
                                             final ClientProviderHandlerRegistry handlerRegistry) {
        this.providerTypeService = providerTypeService;
        this.runtimeService = runtimeService;
        this.enableProviderTypeWizard = enableProviderTypeWizard;
        this.newProviderWizard = newProviderWizard;
        this.newDeployWizard = newDeployWizard;
        this.handlerRegistry = handlerRegistry;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName());
        perspective.setName(IDENTIFIER);
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(ProvisioningManagementBrowserPresenter.IDENTIFIER)));
        return perspective;
    }

    protected void onAddNewProviderType(@Observes final AddNewProviderTypeEvent event) {
        providerTypeService.call((Map<ProviderType, ProviderTypeStatus> result) -> {
                                     enableProviderTypeWizard.start(buildProviderStatusList(result));
                                 }).getProviderTypesStatus();
    }

    protected void onAddNewProvider(@Observes final AddNewProviderEvent event) {
        if (event.getProviderType() != null && event.getProviderType().getKey() != null) {
            newProviderWizard.start(event.getProviderType());
        }
    }

    protected void onAddNewRuntime(@Observes final AddNewRuntimeEvent event) {
        if (event.getProvider() != null) {
            runtimeService.call((Collection<PipelineKey> result) -> {
                                    newDeployWizard.start(event.getProvider(),
                                                          result);
                                }).getPipelines(event.getProvider().getKey().getProviderTypeKey());
        }
    }

    private List<Pair<ProviderType, ProviderTypeStatus>> buildProviderStatusList(final Map<ProviderType, ProviderTypeStatus> statusMap) {
        return statusMap.entrySet()
                .stream()
                .filter(entry -> handlerRegistry.isProviderInstalled(entry.getKey().getKey()))
                .map(entry -> new Pair<>(entry.getKey(),
                                         entry.getValue()))
                .sorted(Comparator.comparing(o -> o.getK1().getName()))
                .collect(Collectors.toList());
    }
}
