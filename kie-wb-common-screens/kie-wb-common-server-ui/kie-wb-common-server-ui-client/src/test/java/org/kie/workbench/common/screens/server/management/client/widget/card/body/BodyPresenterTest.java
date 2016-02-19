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

package org.kie.workbench.common.screens.server.management.client.widget.card.body;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.Message;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.notification.NotificationPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BodyPresenterTest {

    @Mock
    BodyPresenter.View view;

    @Mock
    NotificationPresenter notificationPresenter;

    private BodyPresenter presenter;

    @Before
    public void setUp() {
        presenter = new BodyPresenter( view ) {
            @Override
            NotificationPresenter newNotification() {
                return notificationPresenter;
            }
        };
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testSetup() {
        final Message message = mock( Message.class );
        presenter.setup( message );
        verify( notificationPresenter ).setup( message );
        verify( view ).addNotification( notificationPresenter.getView() );
    }

}