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

package org.guvnor.ala.ui.client.navigation;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeListRefreshEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeSelectedEvent;
import org.guvnor.ala.ui.model.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderType;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeList;
import static org.guvnor.ala.ui.client.util.UIUtil.getDisplayableProviderTypeName;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderTypeNavigationPresenterTest {

    private static final int PROVIDER_TYPE_SIZE = 5;

    @Mock
    private ProviderTypeNavigationPresenter.View view;

    @Mock
    private EventSourceMock<AddNewProviderTypeEvent> addNewProviderTypeEvent;

    @Mock
    private EventSourceMock<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent;

    @Mock
    private EventSourceMock<ProviderTypeSelectedEvent> providerTypeSelectedEvent;

    private ProviderTypeNavigationPresenter presenter;

    private ProviderType providerType;

    private List<ProviderType> providerTypes;

    @Before
    public void setUp() {
        providerType = mockProviderType("0");
        providerTypes = mockProviderTypeList(PROVIDER_TYPE_SIZE);
        providerTypes.add(providerType);

        presenter = new ProviderTypeNavigationPresenter(view,
                                                        addNewProviderTypeEvent,
                                                        providerTypeListRefreshEvent,
                                                        providerTypeSelectedEvent);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetup() {
        presenter.setup(providerType,
                        providerTypes);
        verify(view,
               times(1)).clear();
        providerTypes.forEach(providerType -> verify(view,
                                                     times(1)).addProviderType(eq(providerType.getKey()),
                                                                               eq(getDisplayableProviderTypeName(providerType)),
                                                                               any(Command.class)));
    }

    @Test
    public void testOnSelect() {
        presenter.setup(providerType,
                        providerTypes);

        //test the selection of whatever of the elements.
        providerTypes.forEach(currentProviderType -> {
            presenter.onSelect(new ProviderTypeSelectedEvent(currentProviderType.getKey()));
            if (!currentProviderType.equals(providerType)) {
                verify(view,
                       times(1)).select(currentProviderType.getKey());
            }
        });
        verify(view,
               times(2)).select(providerType.getKey());
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view,
               times(1)).clear();
    }

    @Test
    public void testOnRefresh() {
        presenter.onRefresh();
        verify(providerTypeListRefreshEvent,
               times(1)).fire(new ProviderTypeListRefreshEvent());
    }

    @Test
    public void testOnAddProviderType() {
        presenter.onAddProviderType();
        verify(addNewProviderTypeEvent,
               times(1)).fire(any(AddNewProviderTypeEvent.class));
    }
}
