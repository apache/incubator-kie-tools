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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.empty.ProviderTypeEmptyPresenter;
import org.guvnor.ala.ui.client.events.ProviderSelectedEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeDeletedEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeListRefreshEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeSelectedEvent;
import org.guvnor.ala.ui.client.navigation.ProviderTypeNavigationPresenter;
import org.guvnor.ala.ui.client.navigation.providertype.ProviderTypePresenter;
import org.guvnor.ala.ui.client.provider.ProviderPresenter;
import org.guvnor.ala.ui.client.provider.empty.ProviderEmptyPresenter;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProvidersInfo;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.ala.ui.service.ProvisioningScreensService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.lifecycle.OnOpen;

import static org.guvnor.ala.ui.client.ProvisioningManagementBrowserPresenter.IDENTIFIER;

@ApplicationScoped
@WorkbenchScreen(identifier = IDENTIFIER)
public class ProvisioningManagementBrowserPresenter {

    public static final String IDENTIFIER = "ProvisioningManagementBrowser";

    public interface View
            extends UberElement<ProvisioningManagementBrowserPresenter> {

        String getTitle();

        void setProviderTypesNavigation(final ProviderTypeNavigationPresenter.View view);

        void setProviderType(final ProviderTypePresenter.View view);

        void setEmptyView(final ProviderTypeEmptyPresenter.View view);

        void setContent(final IsElement view);
    }

    private final View view;

    private final ProviderTypeNavigationPresenter providerTypeNavigationPresenter;

    private final ProviderTypePresenter providerTypePresenter;

    private final ProviderTypeEmptyPresenter providerTypeEmptyPresenter;

    private final ProviderEmptyPresenter providerEmptyPresenter;

    private final ProviderPresenter providerPresenter;

    private final Caller<ProviderTypeService> providerTypeService;

    private final Caller<ProvisioningScreensService> provisioningScreensService;

    private final Event<ProviderTypeSelectedEvent> providerTypeSelectedEvent;

    @Inject
    public ProvisioningManagementBrowserPresenter(final ProvisioningManagementBrowserPresenter.View view,
                                                  final ProviderTypeNavigationPresenter providerTypeNavigationPresenter,
                                                  final ProviderTypePresenter providerTypePresenter,
                                                  final ProviderTypeEmptyPresenter providerTypeEmptyPresenter,
                                                  final ProviderEmptyPresenter providerEmptyPresenter,
                                                  final ProviderPresenter providerPresenter,
                                                  final Caller<ProviderTypeService> providerTypeService,
                                                  final Caller<ProvisioningScreensService> provisioningScreensService,
                                                  final Event<ProviderTypeSelectedEvent> providerTypeSelectedEvent) {
        this.view = view;
        this.providerTypeNavigationPresenter = providerTypeNavigationPresenter;
        this.providerTypePresenter = providerTypePresenter;
        this.providerTypeEmptyPresenter = providerTypeEmptyPresenter;
        this.providerEmptyPresenter = providerEmptyPresenter;
        this.providerPresenter = providerPresenter;
        this.providerTypeService = providerTypeService;
        this.provisioningScreensService = provisioningScreensService;
        this.providerTypeSelectedEvent = providerTypeSelectedEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setProviderTypesNavigation(providerTypeNavigationPresenter.getView());
    }

    @OnOpen
    public void onOpen() {
        refreshProviderTypes();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return view.getTitle();
    }

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    protected void onRefreshProviderTypes(@Observes final ProviderTypeListRefreshEvent event) {
        refreshProviderTypes(event.getProviderTypeKey());
    }

    protected void onProviderTypeSelected(@Observes final ProviderTypeSelectedEvent event) {
        if (event.getProviderTypeKey() != null) {
            selectProviderType(event.getProviderTypeKey(),
                               event.getProviderId());
        }
    }

    protected void onProviderTypeDeleted(@Observes final ProviderTypeDeletedEvent event) {
        refreshProviderTypes();
    }

    protected void onProviderSelected(@Observes final ProviderSelectedEvent event) {
        if (event.getProviderKey() != null) {
            this.view.setContent(providerPresenter.getView());
        }
    }

    private void refreshProviderTypes() {
        refreshProviderTypes(null);
    }

    private void refreshProviderTypes(ProviderTypeKey selectProviderTypeKey) {
        providerTypeService.call((Collection<ProviderType> providerTypes) ->
                                         setupProviderTypes(providerTypes,
                                                            selectProviderTypeKey),
                                 new DefaultErrorCallback()).getEnabledProviderTypes();
    }

    private void selectProviderType(final ProviderTypeKey providerTypeKey,
                                    final String selectedProviderId) {
        provisioningScreensService.call((ProvidersInfo providersInfo) ->
                                                setupProviderType(providersInfo.getProviderType(),
                                                                  providersInfo.getProvidersKey(),
                                                                  selectedProviderId),
                                        new DefaultErrorCallback()
        ).getProvidersInfo(providerTypeKey);
    }

    private void setupProviderTypes(final Collection<ProviderType> providerTypes,
                                    final ProviderTypeKey selectProviderTypeKey) {
        if (providerTypes.isEmpty()) {
            this.view.setEmptyView(providerTypeEmptyPresenter.getView());
            providerTypeNavigationPresenter.clear();
        } else {
            ProviderType providerType2BeSelected = null;
            if (selectProviderTypeKey != null) {
                for (final ProviderType providerType : providerTypes) {
                    if (providerType.getKey().equals(selectProviderTypeKey)) {
                        providerType2BeSelected = providerType;
                        break;
                    }
                }
            }
            if (providerType2BeSelected == null) {
                providerType2BeSelected = providerTypes.iterator().next();
            }
            providerTypeNavigationPresenter.setup(providerType2BeSelected,
                                                  providerTypes);
            providerTypeSelectedEvent.fire(new ProviderTypeSelectedEvent(providerType2BeSelected.getKey()));
        }
    }

    private void setupProviderType(final ProviderType providerType,
                                   final Collection<ProviderKey> providerKeys,
                                   final String selectProviderId) {
        ProviderKey provider2BeSelected = null;
        this.view.setProviderType(providerTypePresenter.getView());
        if (providerKeys.isEmpty()) {
            providerEmptyPresenter.setProviderType(providerType);
            this.view.setContent(providerEmptyPresenter.getView());
        } else {
            if (selectProviderId != null) {
                for (ProviderKey provider : providerKeys) {
                    if (provider.getId().equals(selectProviderId)) {
                        provider2BeSelected = provider;
                        break;
                    }
                }
            }
            if (provider2BeSelected == null) {
                provider2BeSelected = providerKeys.iterator().next();
            }
        }
        providerTypePresenter.setup(providerType,
                                    providerKeys,
                                    provider2BeSelected);
    }
}
