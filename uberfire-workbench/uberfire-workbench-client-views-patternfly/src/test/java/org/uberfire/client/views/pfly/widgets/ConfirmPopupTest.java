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

import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmPopupTest {

    private static final String TITLE = "TITLE";
    private static final String INLINE_NOTIFICATION_MESSAGE = "INLINE_NOTIFICATION_MESSAGE";
    private static final String CONFIRM_MESSAGE = "CONFIRM_MESSAGE";
    private static final String OK_BUTTON_TEXT = "OK_BUTTON_TEXT";

    @Mock
    Modal modal;

    @Mock
    Span modalTitle;

    @Mock
    InlineNotification confirmInlineNotification;

    @Mock
    HTMLElement confirmInlineNotificationElement;

    @Mock
    CSSStyleDeclaration confirmInlineNotificationStyle;

    @Mock
    Span modalConfirmationMessageLabel;

    @Mock
    Button okButton;

    @Mock
    Button cancelButton;

    @InjectMocks
    ConfirmPopup popup;

    @Before
    public void setUp() {
        when(confirmInlineNotification.getElement()).thenReturn(confirmInlineNotificationElement);
        when(confirmInlineNotificationElement.getStyle()).thenReturn(confirmInlineNotificationStyle);
    }

    @Test
    public void testHide() {
        popup.hide();

        verify(modal).hide();
    }

    @Test
    public void testCancel() {
        popup.onCancelClick(null);

        verify(modal).hide();
    }

    @Test
    public void testClose() {
        popup.onCloseClick(null);

        verify(modal).hide();
    }

    @Test
    public void testOk() {
        final Command command = mock(Command.class);
        popup.show(null,
                   null,
                   null,
                   command);

        popup.onOkClick(null);

        verify(command).execute();
        verify(modal).hide();
    }

    @Test
    public void testShow() {
        final Command command = mock(Command.class);
        popup.show(TITLE,
                   OK_BUTTON_TEXT,
                   CONFIRM_MESSAGE,
                   command);
        verify(modalTitle,
               times(1)).setTextContent(TITLE);
        verify(confirmInlineNotificationStyle,
               times(1)).setProperty("display",
                                     "none");
        verify(okButton,
               times(1)).setText(OK_BUTTON_TEXT);
        verify(okButton,
               times(1)).setButtonStyleType(Button.ButtonStyleType.DANGER);
        verify(modalConfirmationMessageLabel,
               times(1)).setTextContent(CONFIRM_MESSAGE);
    }

    @Test
    public void testShowWithInlineNotification() {
        final Command command = mock(Command.class);
        InlineNotification.InlineNotificationType arbitraryNotificationType = InlineNotification.InlineNotificationType.WARNING;
        Button.ButtonStyleType arbitraryButtonType = Button.ButtonStyleType.PRIMARY;
        popup.show(TITLE,
                   INLINE_NOTIFICATION_MESSAGE,
                   arbitraryNotificationType,
                   OK_BUTTON_TEXT,
                   arbitraryButtonType,
                   CONFIRM_MESSAGE,
                   command);
        verify(modalTitle,
               times(1)).setTextContent(TITLE);
        verify(confirmInlineNotificationStyle,
               times(1)).removeProperty("display");
        verify(confirmInlineNotification,
               times(1)).setMessage(INLINE_NOTIFICATION_MESSAGE);
        verify(confirmInlineNotification,
               times(1)).setType(arbitraryNotificationType);
        verify(okButton,
               times(1)).setText(OK_BUTTON_TEXT);
        verify(okButton,
               times(1)).setButtonStyleType(arbitraryButtonType);
        verify(modalConfirmationMessageLabel,
               times(1)).setTextContent(CONFIRM_MESSAGE);
    }
}