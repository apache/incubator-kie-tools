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

package org.guvnor.ala.ui.client.navigation.providertype;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.AddNewProviderEvent;
import org.guvnor.ala.ui.client.events.ProviderSelectedEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeListRefreshEvent;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderType;
import static org.guvnor.ala.ui.client.util.UIUtil.getDisplayableProviderTypeName;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderTypePresenterTest {

    private static final int PROVIDERS_SIZE = 5;

    @Mock
    private ProviderTypePresenter.View view;

    @Mock
    private ProviderTypeService providerTypeService;

    private Caller<ProviderTypeService> providerTypeServiceCaller;

    @Mock
    private EventSourceMock<AddNewProviderEvent> addNewProviderEvent;

    @Mock
    private EventSourceMock<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent;

    @Mock
    private EventSourceMock<ProviderSelectedEvent> providerSelectedEvent;

    private ProviderTypePresenter presenter;

    private ProviderType providerType;

    private List<ProviderKey> providers;

    private ProviderKey provider;

    @Before
    public void setUp() {
        providerType = mockProviderType("0");
        providers = createProviders(providerType.getKey(),
                                    PROVIDERS_SIZE);
        provider = providers.get(0);

        providerTypeServiceCaller = new CallerMock<>(providerTypeService);
        presenter = new ProviderTypePresenter(view,
                                              providerTypeServiceCaller,
                                              addNewProviderEvent,
                                              providerTypeListRefreshEvent,
                                              providerSelectedEvent);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetup() {
        presenter.setup(providerType,
                        providers,
                        provider);
        verify(view,
               times(1)).clear();

        verify(view,
               times(1)).setProviderTypeName(getDisplayableProviderTypeName(providerType));

        providers.forEach(providerKey -> verify(view,
                                                times(1)).addProvider(eq(provider.getId()),
                                                                      eq(provider.getId()),
                                                                      any(Command.class)));
        verify(providerSelectedEvent,
               times(1)).fire(new ProviderSelectedEvent(provider));
    }

    @Test
    public void onProviderSelectTest() {
        presenter.setup(providerType,
                        providers,
                        provider);

        //try the selection of all indexes.
        providers.forEach(providerKey -> {
            presenter.onProviderSelect(new ProviderSelectedEvent(providerKey));
            verify(view,
                   times(1)).select(providerKey.getId());
        });
    }

    @Test
    public void onAddNewProviderTest() {
        presenter.setup(providerType,
                        providers,
                        provider);
        presenter.onAddNewProvider();
        verify(addNewProviderEvent,
               times(1)).fire(new AddNewProviderEvent(providerType));
    }

    @Test
    public void onRemoveProviderTypeTest() {
        presenter.setup(providerType,
                        providers,
                        provider);

        presenter.onRemoveProviderType();

        //emulate user confirmation on screen
        verify(view,
               times(1)).confirmRemove(any(Command.class));
        presenter.removeProviderType();

        verify(providerTypeService,
               times(1)).disableProviderType(providerType);
        verify(providerTypeListRefreshEvent,
               times(1)).fire(new ProviderTypeListRefreshEvent());
    }

    private List<ProviderKey> createProviders(ProviderTypeKey providerTypeKey,
                                              int size) {
        List<ProviderKey> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(mockProviderKey(providerTypeKey,
                                       Integer.toString(i)));
        }
        return result;
    }
}
