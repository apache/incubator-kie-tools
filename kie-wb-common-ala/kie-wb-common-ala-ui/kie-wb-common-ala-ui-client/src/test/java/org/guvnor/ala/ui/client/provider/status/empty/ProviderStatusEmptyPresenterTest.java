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

package org.guvnor.ala.ui.client.provider.status.empty;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeKey;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderStatusEmptyPresenterTest {

    @Mock
    private ProviderStatusEmptyPresenter.View view;

    @Mock
    private EventSourceMock<RefreshRuntimeEvent> refreshRuntimeEvent;

    private ProviderStatusEmptyPresenter presenter;

    private ProviderKey providerKey;

    @Before
    public void setUp() {
        ProviderTypeKey providerTypeKey = mockProviderTypeKey("1");
        providerKey = mockProviderKey(providerTypeKey,
                                      "1");

        presenter = new ProviderStatusEmptyPresenter(view,
                                                     refreshRuntimeEvent);
        presenter.init();
        presenter.setup(providerKey);
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testOnRefresh() {
        presenter.onRefresh();
        verify(refreshRuntimeEvent,
               times(1)).fire(new RefreshRuntimeEvent(providerKey));
    }
}
