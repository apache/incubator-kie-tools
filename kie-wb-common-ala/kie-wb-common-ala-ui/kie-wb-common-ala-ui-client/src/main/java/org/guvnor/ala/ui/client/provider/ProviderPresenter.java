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

package org.guvnor.ala.ui.client.provider;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.AddNewRuntimeEvent;
import org.guvnor.ala.ui.client.events.ProviderSelectedEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeSelectedEvent;
import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.handler.FormResolver;
import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.client.provider.status.ProviderStatusPresenter;
import org.guvnor.ala.ui.client.provider.status.empty.ProviderStatusEmptyPresenter;
import org.guvnor.ala.ui.client.wizard.provider.empty.ProviderConfigEmptyPresenter;
import org.guvnor.ala.ui.events.PipelineExecutionChangeEvent;
import org.guvnor.ala.ui.events.RuntimeChangeEvent;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.RuntimesInfo;
import org.guvnor.ala.ui.service.ProviderService;
import org.guvnor.ala.ui.service.ProvisioningScreensService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class ProviderPresenter {

    public interface View
            extends UberElement<ProviderPresenter> {

        void confirmRemove(final Command command);

        void showProviderCantBeDeleted();

        void setProviderName(String name);

        void setStatus(IsElement view);

        void setConfig(IsElement view);

        String getRemoveProviderSuccessMessage();

        String getRemoveProviderErrorMessage();
    }

    private final View view;
    private final Caller<ProviderService> providerService;
    private final Caller<ProvisioningScreensService> provisioningScreensService;
    private final ProviderStatusEmptyPresenter providerStatusEmptyPresenter;
    private final ProviderStatusPresenter providerStatusPresenter;
    private final ProviderConfigEmptyPresenter providerConfigEmptyPresenter;
    private final ClientProviderHandlerRegistry providerHandlerRegistry;

    private final Event<NotificationEvent> notification;
    private final Event<ProviderTypeSelectedEvent> providerTypeSelectedEvent;
    private final Event<AddNewRuntimeEvent> addNewRuntimeEvent;

    private Provider provider;
    private Map<ProviderConfigurationForm, FormResolver> formToResolverMap = new HashMap<>();

    @Inject
    public ProviderPresenter(final View view,
                             final Caller<ProviderService> providerService,
                             final Caller<ProvisioningScreensService> provisioningScreensService,
                             final ProviderStatusEmptyPresenter providerStatusEmptyPresenter,
                             final ProviderStatusPresenter providerStatusPresenter,
                             final ProviderConfigEmptyPresenter providerConfigEmptyPresenter,
                             final ClientProviderHandlerRegistry providerHandlerRegistry,
                             final Event<NotificationEvent> notification,
                             final Event<ProviderTypeSelectedEvent> providerTypeSelectedEvent,
                             final Event<AddNewRuntimeEvent> addNewRuntimeEvent) {
        this.view = view;
        this.providerService = providerService;
        this.provisioningScreensService = provisioningScreensService;
        this.providerStatusEmptyPresenter = providerStatusEmptyPresenter;
        this.providerStatusPresenter = providerStatusPresenter;
        this.providerConfigEmptyPresenter = providerConfigEmptyPresenter;
        this.providerHandlerRegistry = providerHandlerRegistry;
        this.notification = notification;
        this.providerTypeSelectedEvent = providerTypeSelectedEvent;
        this.addNewRuntimeEvent = addNewRuntimeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void onProviderSelected(@Observes final ProviderSelectedEvent event) {
        if (event.getProviderKey() != null) {
            load(event.getProviderKey());
        }
    }

    public void onRefreshRuntime(@Observes final RefreshRuntimeEvent event) {
        if (event.getProviderKey() != null && event.getProviderKey().equals(provider.getKey())) {
            load(event.getProviderKey());
        }
    }

    private void load(final ProviderKey providerKey) {
        providerStatusPresenter.clear();
        provisioningScreensService.call(getLoadRuntimesInfoSuccessCallback()).getRuntimesInfo(providerKey);
    }

    private RemoteCallback<RuntimesInfo> getLoadRuntimesInfoSuccessCallback() {
        return info -> {
            this.provider = info.getProvider();
            clearProviderConfigurationForm();
            view.setProviderName(provider.getKey().getId());
            if (info.getRuntimeItems().isEmpty()) {
                providerStatusEmptyPresenter.setup(provider.getKey());
                view.setStatus(providerStatusEmptyPresenter.getView());
            } else {
                providerStatusPresenter.setupItems(info.getRuntimeItems());
                view.setStatus(providerStatusPresenter.getView());
            }
            final ProviderConfigurationForm providerConfigurationForm = newProviderConfigurationForm(provider.getKey().getProviderTypeKey());
            providerConfigurationForm.load(provider);
            providerConfigurationForm.disable();
            view.setConfig(providerConfigurationForm.getView());
        };
    }

    public void refresh() {
        load(provider.getKey());
    }

    public void onRemoveProvider() {
        provisioningScreensService.call(getRuntimesCheckSuccessCallback()).hasRuntimes(provider.getKey());
    }

    private RemoteCallback<Boolean> getRuntimesCheckSuccessCallback() {
        return hasRuntimes -> {
            if (hasRuntimes) {
                view.showProviderCantBeDeleted();
            } else {
                view.confirmRemove(this::removeProvider);
            }
        };
    }

    public void removeProvider() {
        providerService.call(response -> {
                                 notification.fire(new NotificationEvent(view.getRemoveProviderSuccessMessage(),
                                                                         NotificationEvent.NotificationType.SUCCESS));

                                 providerTypeSelectedEvent.fire(new ProviderTypeSelectedEvent(provider.getKey().getProviderTypeKey()));
                             },
                             (message, throwable) -> {
                                 notification.fire(new NotificationEvent(view.getRemoveProviderErrorMessage(),
                                                                         NotificationEvent.NotificationType.ERROR));
                                 providerTypeSelectedEvent.fire(new ProviderTypeSelectedEvent(provider.getKey().getProviderTypeKey()));
                                 return false;
                             }).deleteProvider(provider.getKey());
    }

    public void deploy() {
        addNewRuntimeEvent.fire(new AddNewRuntimeEvent(provider));
    }

    public IsElement getView() {
        return view;
    }

    protected void onRuntimeChange(@Observes final RuntimeChangeEvent event) {
        if (event.isDelete() && provider != null && event.getRuntimeKey() != null &&
                provider.getKey().equals(event.getRuntimeKey().getProviderKey())) {
            if (providerStatusPresenter.removeItem(event.getRuntimeKey()) && providerStatusPresenter.isEmpty()) {
                refresh();
            }
        }
    }

    protected void onPipelineExecutionChange(@Observes final PipelineExecutionChangeEvent event) {
        if (event.isDelete() && provider != null && event.getPipelineExecutionTraceKey() != null) {
            if (providerStatusPresenter.removeItem(event.getPipelineExecutionTraceKey()) && providerStatusPresenter.isEmpty()) {
                refresh();
            }
        }
    }

    private ProviderConfigurationForm newProviderConfigurationForm(ProviderTypeKey providerTypeKey) {
        if (providerHandlerRegistry.isProviderInstalled(providerTypeKey)) {
            final FormResolver formResolver = providerHandlerRegistry.getProviderHandler(providerTypeKey).getFormResolver();
            final ProviderConfigurationForm providerConfigurationForm = formResolver.newProviderConfigurationForm();
            formToResolverMap.put(providerConfigurationForm,
                                  formResolver);
            return providerConfigurationForm;
        } else {
            return providerConfigEmptyPresenter;
        }
    }

    private void clearProviderConfigurationForm() {
        formToResolverMap.entrySet().forEach(entry -> entry.getValue().destroyForm(entry.getKey()));
        formToResolverMap.clear();
    }
}
