/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.remote;

import java.util.Collections;

import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.Message;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.remote.card.ContainerCardPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteStatusPresenterTest {

    @Mock
    RemoteStatusPresenter.View view;

    @Mock
    ContainerCardPresenter containerCardPresenter;

    @InjectMocks
    @Spy
    RemoteStatusPresenter presenter;

    @Before
    public void setup() {
        doReturn(containerCardPresenter).when(presenter).newCard();
        when(containerCardPresenter.getView()).thenReturn(mock(org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View.class));
    }

    @Test
    public void testInit() {
        presenter.init();

        assertEquals(view, presenter.getView());
    }

    @Test
    public void testSetup() {
        final Container container = new Container("containerSpecId", "containerName", new ServerInstanceKey(), Collections.<Message>emptyList(), null, null);
        presenter.setup(Collections.singletonList(container));

        verify(containerCardPresenter).setup(container);
        verify(view).addCard(any(Widget.class));
    }
}