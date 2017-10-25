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

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.empty.ProviderTypeEmptyPresenter;
import org.guvnor.ala.ui.client.events.ProviderSelectedEvent;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKeyList;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProvisioningManagementBrowserPresenterTest {

    private static final String TITLE = "TITLE";

    private static final int PROVIDER_TYPES_SIZE = 5;

    @Mock
    private ProvisioningManagementBrowserPresenter.View view;

    @Mock
    private ProviderTypeNavigationPresenter providerTypeNavigationPresenter;

    @Mock
    private ProviderTypeNavigationPresenter.View providerTypeNavigationPresenterView;

    @Mock
    private ProviderTypePresenter providerTypePresenter;

    @Mock
    private ProviderTypeEmptyPresenter providerTypeEmptyPresenter;

    @Mock
    private ProviderTypeEmptyPresenter.View providerTypeEmptyPresenterView;

    @Mock
    private ProviderEmptyPresenter providerEmptyPresenter;

    @Mock
    private ProviderEmptyPresenter.View providerEmptyPresenterView;

    @Mock
    private ProviderPresenter providerPresenter;

    @Mock
    private ProviderPresenter.View providerPresenterView;

    @Mock
    private ProviderTypeService providerTypeService;

    private Caller<ProviderTypeService> providerTypeServiceCaller;

    @Mock
    private ProvisioningScreensService provisioningScreensService;

    private Caller<ProvisioningScreensService> ProvisioningScreensServiceCaller;

    @Mock
    private EventSourceMock<ProviderTypeSelectedEvent> providerTypeSelectedEvent;

    private ProvisioningManagementBrowserPresenter presenter;

    private List<ProviderType> providerTypes;

    @Before
    public void setUp() {
        providerTypeServiceCaller = new CallerMock<>(providerTypeService);
        ProvisioningScreensServiceCaller = new CallerMock<>(provisioningScreensService);

        providerTypes = mockProviderTypeList(PROVIDER_TYPES_SIZE);

        when(providerTypeEmptyPresenter.getView()).thenReturn(providerTypeEmptyPresenterView);
        when(providerTypeNavigationPresenter.getView()).thenReturn(providerTypeNavigationPresenterView);
        when(providerEmptyPresenter.getView()).thenReturn(providerEmptyPresenterView);
        when(providerPresenter.getView()).thenReturn(providerPresenterView);

        presenter = spy(new ProvisioningManagementBrowserPresenter(view,
                                                                   providerTypeNavigationPresenter,
                                                                   providerTypePresenter,
                                                                   providerTypeEmptyPresenter,
                                                                   providerEmptyPresenter,
                                                                   providerPresenter,
                                                                   providerTypeServiceCaller,
                                                                   ProvisioningScreensServiceCaller,
                                                                   providerTypeSelectedEvent));
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(view,
               times(1)).setProviderTypesNavigation(providerTypeNavigationPresenterView);
    }

    @Test
    public void testGetTitle() {
        when(view.getTitle()).thenReturn(TITLE);
        assertEquals(TITLE,
                     presenter.getTitle());
        verify(view,
               times(1)).getTitle();
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testOnRefreshProviderTypesWithEnabledProviderTypes() {

        when(providerTypeService.getEnabledProviderTypes()).thenReturn(providerTypes);
        //pick an arbitrary element as the selected
        ProviderType selectedProviderType = providerTypes.iterator().next();

        presenter.onRefreshProviderTypes(new ProviderTypeListRefreshEvent(selectedProviderType.getKey()));

        //the provider type navigation presenter must have been set properly.
        verify(providerTypeNavigationPresenter,
               times(1)).setup(selectedProviderType,
                               providerTypes);

        verify(providerTypeSelectedEvent,
               times(1)).fire(new ProviderTypeSelectedEvent(selectedProviderType.getKey()));
    }

    @Test
    public void testOnRefreshProviderTypesWithNOEnabledProviderTypes() {

        providerTypes.clear();
        when(providerTypeService.getEnabledProviderTypes()).thenReturn(providerTypes);
        presenter.onRefreshProviderTypes(new ProviderTypeListRefreshEvent());

        verify(view,
               times(1)).setEmptyView(providerTypeEmptyPresenterView);
        verify(providerTypeNavigationPresenter,
               times(1)).clear();
        verify(providerTypeNavigationPresenter,
               times(0)).setup(any(ProviderType.class),
                               anyCollectionOf(ProviderType.class));
    }

    @Test
    public void testOnProviderTypeSelectedWithProviders() {

        //pick an arbitrary element as the selected
        ProviderType selectedProviderType = providerTypes.iterator().next();
        ProviderTypeKey selectedProviderTypeKey = selectedProviderType.getKey();
        List<ProviderKey> providerKeys = mockProviderKeyList(selectedProviderType.getKey(),
                                                             PROVIDER_TYPES_SIZE);
        //pick an arbitrary element as the selected.
        ProviderKey selectedProviderKey = providerKeys.iterator().next();
        ProvidersInfo providersInfo = mock(ProvidersInfo.class);
        when(providersInfo.getProviderType()).thenReturn(selectedProviderType);
        when(providersInfo.getProvidersKey()).thenReturn(providerKeys);

        when(providerTypeService.getProviderType(selectedProviderTypeKey)).thenReturn(selectedProviderType);
        when(provisioningScreensService.getProvidersInfo(selectedProviderTypeKey)).thenReturn(providersInfo);

        presenter.onProviderTypeSelected(new ProviderTypeSelectedEvent(selectedProviderType.getKey(),
                                                                       selectedProviderKey.getId()));

        verify(providerTypePresenter,
               times(1)).setup(selectedProviderType,
                               providerKeys,
                               selectedProviderKey);
    }

    @Test
    public void testOnProviderTypeSelectedWithNOProviders() {

        //pick an arbitrary element as the selected
        ProviderType selectedProviderType = providerTypes.iterator().next();
        ProviderTypeKey selectedProviderTypeKey = selectedProviderType.getKey();
        //the selected provider type don't have providers.
        List<ProviderKey> providerKeys = new ArrayList<>();
        ProvidersInfo providersInfo = mock(ProvidersInfo.class);
        when(providersInfo.getProviderType()).thenReturn(selectedProviderType);
        when(providersInfo.getProvidersKey()).thenReturn(providerKeys);

        when(providerTypeService.getProviderType(selectedProviderTypeKey)).thenReturn(selectedProviderType);
        when(provisioningScreensService.getProvidersInfo(selectedProviderTypeKey)).thenReturn(providersInfo);

        presenter.onProviderTypeSelected(new ProviderTypeSelectedEvent(selectedProviderType.getKey()));

        verify(providerEmptyPresenter,
               times(1)).setProviderType(selectedProviderType);
        verify(view,
               times(1)).setContent(providerEmptyPresenterView);

        verify(providerTypePresenter,
               times(1)).setup(any(ProviderType.class),
                               anyCollectionOf(ProviderKey.class),
                               any(ProviderKey.class));
    }

    @Test
    public void testOnProviderSelected() {
        presenter.onProviderSelected(new ProviderSelectedEvent(mock(ProviderKey.class)));
        verify(view,
               times(1)).setContent(providerPresenterView);
    }
}
