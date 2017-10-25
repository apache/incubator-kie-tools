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

package org.guvnor.ala.ui.client.empty;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderTypeEmptyPresenterTest {

    @Mock
    private ProviderTypeEmptyPresenter.View view;

    @Mock
    private EventSourceMock<AddNewProviderTypeEvent> addNewProviderTypeEvent;

    private ProviderTypeEmptyPresenter presenter;

    @Before
    public void setUp() {
        presenter = new ProviderTypeEmptyPresenter(view,
                                                   addNewProviderTypeEvent);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void onAddProviderTypeTest() {
        presenter.onAddProviderType();
        verify(addNewProviderTypeEvent,
               times(1)).fire(any(AddNewProviderTypeEvent.class));
    }
}
