/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import java.util.Arrays;

import org.jboss.errai.common.client.dom.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InlineNotificationTest {

    @Mock
    Div alert;

    @Mock
    Span message;

    @Mock
    Span icon;

    @Mock
    Document document;

    @InjectMocks
    InlineNotification notification;

    @Test
    public void testType() {
        final DOMTokenList alertDomTokenList = mock(DOMTokenList.class);
        when(alert.getClassList()).thenReturn(alertDomTokenList);
        final DOMTokenList iconDomTokenList = mock(DOMTokenList.class);
        when(icon.getClassList()).thenReturn(iconDomTokenList);

        notification.setType(InlineNotification.InlineNotificationType.SUCCESS);

        verify(alertDomTokenList).add(InlineNotification.InlineNotificationType.SUCCESS.getCssClass());
        verify(iconDomTokenList).add(InlineNotification.InlineNotificationType.SUCCESS.getIcon());
    }

    @Test
    public void testMessage() {
        final String msg = "message";

        notification.setMessage(msg);

        verify(message).setTextContent(msg);
    }

    @Test
    public void testMessages() {
        final HTMLElement htmlElement = mock(HTMLElement.class);
        when(htmlElement.getClassList()).thenReturn(mock(DOMTokenList.class));
        when(document.createElement(anyString())).thenReturn(htmlElement);
        when(message.getChildNodes()).thenReturn(mock(NodeList.class));

        notification.setMessage(Arrays.asList("message"));

        verify(message).appendChild(any(Node.class));
        verify(document).createElement("li");
        verify(document).createElement("ul");
    }
}
