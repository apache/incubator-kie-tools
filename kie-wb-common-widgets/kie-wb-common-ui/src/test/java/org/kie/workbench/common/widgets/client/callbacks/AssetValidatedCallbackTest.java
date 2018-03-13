/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.callbacks;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AssetValidatedCallbackTest {

    @Mock
    private Command validationFinishedCommand;
    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;
    @Mock
    private ValidationPopup validationPopup;

    @Captor
    private ArgumentCaptor<NotificationEvent> notificationEventArgumentCaptor;

    private AssetValidatedCallback assetValidatedCallback;

    @Before
    public void setup() throws Exception {
        assetValidatedCallback = new AssetValidatedCallback(validationFinishedCommand,
                                                            notificationEvent,
                                                            validationPopup);
    }

    @Test
    public void success() throws Exception {
        assetValidatedCallback.callback(new ArrayList<>());

        Mockito.verify(notificationEvent).fire(notificationEventArgumentCaptor.capture());
        assertEquals("ItemValidatedSuccessfully", notificationEventArgumentCaptor.getValue().getNotification());
        assertEquals(NotificationEvent.NotificationType.SUCCESS, notificationEventArgumentCaptor.getValue().getType());

        verify(validationPopup, never()).showMessages(anyList());

        verify(validationFinishedCommand).execute();
    }

    @Test
    public void failure() throws Exception {
        final ArrayList<ValidationMessage> validationMessages = new ArrayList<>();
        validationMessages.add(mock(ValidationMessage.class));
        assetValidatedCallback.callback(validationMessages);

        Mockito.verify(notificationEvent, never()).fire(notificationEventArgumentCaptor.capture());

        verify(validationPopup).showMessages(validationMessages);

        verify(validationFinishedCommand).execute();
    }
}