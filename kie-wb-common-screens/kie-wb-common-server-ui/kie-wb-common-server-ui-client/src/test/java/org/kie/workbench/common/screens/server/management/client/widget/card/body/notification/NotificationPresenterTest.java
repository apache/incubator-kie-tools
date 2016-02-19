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

package org.kie.workbench.common.screens.server.management.client.widget.card.body.notification;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.Severity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationPresenterTest {

    @Mock
    NotificationPresenter.View view;

    @InjectMocks
    NotificationPresenter presenter;

    @Test
    public void testInit() {
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testSetup() {
        presenter.setupOk();

        verify( view ).setupOk();
    }

    @Test
    public void testSetupOk() {
        final Message message = new Message();
        message.setSeverity( Severity.INFO );
        presenter.setup( message );

        verify( view ).setupOk();
    }

    @Test
    public void testSetupEmptyMessage() {
        final Message message = new Message();
        message.setSeverity( Severity.ERROR );
        presenter.setup( message );

        verify( view ).setup( NotificationType.ERROR, "0" );
    }

    @Test
    public void testSetupSingleMessage() {
        final Message message = new Message( Severity.ERROR, "single error" );
        presenter.setup( message );

        verify( view ).setup( NotificationType.ERROR, "1", "1: single error" );
    }

    @Test
    public void testSetupSingleEmptyMessage() {
        final Message message = new Message( Severity.ERROR, "" );
        presenter.setup( message );

        verify( view ).setup( NotificationType.ERROR, "0", "" );
    }

    @Test
    public void testSetupMultiMessage() {
        final Message message = new Message( Severity.WARN, Arrays.asList( "first error", "second error" ) );
        presenter.setup( message );

        verify( view ).setup( NotificationType.WARNING, "2", "1: first error\n2: second error" );
    }

}