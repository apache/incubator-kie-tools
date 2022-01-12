/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.popup;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CONFIRM_MESSAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.INLINE_NOTIFICATION_MESSAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.NO_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OK_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.YES_BUTTON_TEXT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class YesNoConfirmYesNoConfirmPopupViewTest extends AbstractYesNoConfirmYesNoConfirmPopupViewTest {

    private YesNoConfirmPopupView yesNoConfirmPopupView;

    @Mock
    private Span modalTitleMock;

    @Mock
    private InlineNotification confirmInlineNotificationMock;

    @Mock
    private Span modalConfirmationMessageLabelMock;

    @Mock
    private Modal modalMock;

    @Mock
    private Button okButtonMock;

    @Mock
    private Button yesButtonMock;

    @Mock
    private Button noButtonMock;

    @Mock
    private Command yesCommandMock;

    @Mock
    private Command noCommandMock;

    @Mock
    private MouseEvent mouseEventMock;

    @Mock
    private HTMLElement elementMock;

    @Mock
    private CSSStyleDeclaration styleMock;

    @Before
    public void setup() {
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(confirmInlineNotificationMock.getElement()).thenReturn(elementMock);
        yesNoConfirmPopupView = spy(new YesNoConfirmPopupView() {
            {
                this.modalConfirmationMessageLabel = modalConfirmationMessageLabelMock;
                this.confirmInlineNotification = confirmInlineNotificationMock;
                this.modalTitle = modalTitleMock;
                this.modal = modalMock;
                this.okButton = okButtonMock;
                this.noButton = noButtonMock;
                this.yesButton = yesButtonMock;
            }
        });
    }

    @Test
    public void showOkCancel() {
        yesNoConfirmPopupView.show(TITLE, OK_BUTTON_TEXT, CONFIRM_MESSAGE, okCommandMock);
        verify(yesNoConfirmPopupView, times(1)).show(TITLE, null, null, OK_BUTTON_TEXT, Button.ButtonStyleType.DANGER, CONFIRM_MESSAGE, okCommandMock);
        verify(yesNoConfirmPopupView, times(1)).commonShow(eq(TITLE), any(), any(), eq(CONFIRM_MESSAGE));
    }

    @Test
    public void showOkCancelFull() {
        yesNoConfirmPopupView.show(TITLE, INLINE_NOTIFICATION_MESSAGE, INLINE_NOTIFICATION_TYPE, OK_BUTTON_TEXT, BUTTON_STYLE_TYPE, CONFIRM_MESSAGE, okCommandMock);
        verify(okButtonMock, never()).hide();
        verify(okButtonMock, times(1)).show();
        verify(yesButtonMock, times(1)).hide();
        verify(noButtonMock, times(1)).hide();
        verify(okButtonMock, times(1)).setText(eq(OK_BUTTON_TEXT));
        verify(okButtonMock, times(1)).setButtonStyleType(eq(BUTTON_STYLE_TYPE));
        verify(yesNoConfirmPopupView, times(1)).commonShow(eq(TITLE), eq(INLINE_NOTIFICATION_MESSAGE), eq(INLINE_NOTIFICATION_TYPE), eq(CONFIRM_MESSAGE));
    }

    @Test
    public void showYesNoCancel() {
        yesNoConfirmPopupView.show(TITLE, YES_BUTTON_TEXT, NO_BUTTON_TEXT, CONFIRM_MESSAGE, yesCommandMock, noCommandMock);
        verify(yesNoConfirmPopupView, times(1)).show(TITLE, null, null, YES_BUTTON_TEXT, NO_BUTTON_TEXT, Button.ButtonStyleType.DANGER, Button.ButtonStyleType.DEFAULT, CONFIRM_MESSAGE, yesCommandMock, noCommandMock);
        verify(yesNoConfirmPopupView, times(1)).commonShow(eq(TITLE), any(), any(), eq(CONFIRM_MESSAGE));
    }

    @Test
    public void showYesNoCancelFull() {
        yesNoConfirmPopupView.show(TITLE, INLINE_NOTIFICATION_MESSAGE, INLINE_NOTIFICATION_TYPE, YES_BUTTON_TEXT, NO_BUTTON_TEXT, BUTTON_STYLE_TYPE, BUTTON_STYLE_TYPE, CONFIRM_MESSAGE, yesCommandMock, noCommandMock);
        verify(okButtonMock, times(1)).hide();
        verify(yesButtonMock, times(1)).show();
        verify(noButtonMock, times(1)).show();
        verify(yesButtonMock, never()).hide();
        verify(noButtonMock, never()).hide();
        verify(yesButtonMock, times(1)).setText(eq(YES_BUTTON_TEXT));
        verify(noButtonMock, times(1)).setText(eq(NO_BUTTON_TEXT));
        verify(yesButtonMock, times(1)).setButtonStyleType(eq(BUTTON_STYLE_TYPE));
        verify(noButtonMock, times(1)).setButtonStyleType(eq(BUTTON_STYLE_TYPE));
        verify(yesNoConfirmPopupView, times(1)).commonShow(eq(TITLE), eq(INLINE_NOTIFICATION_MESSAGE), eq(INLINE_NOTIFICATION_TYPE), eq(CONFIRM_MESSAGE));
    }

    @Test
    public void hide() {
        yesNoConfirmPopupView.hide();
        verify(modalMock, times(1)).hide();
    }

    @Test
    public void onOkClick() {
        yesNoConfirmPopupView.okCommand = null;
        yesNoConfirmPopupView.onOkClick(mouseEventMock);
        verify(okCommandMock, never()).execute();
        verify(yesNoConfirmPopupView, times(1)).hide();
        reset(yesNoConfirmPopupView);
        yesNoConfirmPopupView.yesCommand = okCommandMock;
        yesNoConfirmPopupView.onYesClick(mouseEventMock);
        verify(okCommandMock, times(1)).execute();
        verify(yesNoConfirmPopupView, times(1)).hide();
    }

    @Test
    public void onCancelClick() {
        yesNoConfirmPopupView.onCancelClick(mouseEventMock);
        verify(yesNoConfirmPopupView, times(1)).hide();
    }

    @Test
    public void onCloseClick() {
        yesNoConfirmPopupView.onCancelClick(mouseEventMock);
        verify(yesNoConfirmPopupView, times(1)).hide();
    }

    @Test
    public void onYesClick() {
        yesNoConfirmPopupView.yesCommand = null;
        yesNoConfirmPopupView.onYesClick(mouseEventMock);
        verify(yesCommandMock, never()).execute();
        verify(yesNoConfirmPopupView, times(1)).hide();
        reset(yesNoConfirmPopupView);
        yesNoConfirmPopupView.yesCommand = yesCommandMock;
        yesNoConfirmPopupView.onYesClick(mouseEventMock);
        verify(yesCommandMock, times(1)).execute();
        verify(yesNoConfirmPopupView, times(1)).hide();
    }

    @Test
    public void onNoClick() {
        yesNoConfirmPopupView.noCommand = null;
        yesNoConfirmPopupView.onNoClick(mouseEventMock);
        verify(noCommandMock, never()).execute();
        verify(yesNoConfirmPopupView, times(1)).hide();
        reset(yesNoConfirmPopupView);
        yesNoConfirmPopupView.noCommand = noCommandMock;
        yesNoConfirmPopupView.onNoClick(mouseEventMock);
        verify(noCommandMock, times(1)).execute();
        verify(yesNoConfirmPopupView, times(1)).hide();
    }

    @Test
    public void commonShow() {
        yesNoConfirmPopupView.commonShow(TITLE, null, null, CONFIRM_MESSAGE);
        verify(modalTitleMock, times(1)).setTextContent(eq(TITLE));
        verify(confirmInlineNotificationMock, never()).setMessage(anyString());
        verify(confirmInlineNotificationMock, never()).setType(eq(INLINE_NOTIFICATION_TYPE));
        verify(styleMock, never()).removeProperty(eq("display"));
        verify(styleMock, times(1)).setProperty(eq("display"), eq("none"));
        verify(modalConfirmationMessageLabelMock, times(1)).setTextContent(eq(CONFIRM_MESSAGE));
        verify(modalMock, times(1)).show();

        reset(modalTitleMock);
        reset(confirmInlineNotificationMock);
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(confirmInlineNotificationMock.getElement()).thenReturn(elementMock);
        reset(styleMock);
        reset(modalConfirmationMessageLabelMock);
        reset(modalMock);

        yesNoConfirmPopupView.commonShow(TITLE, INLINE_NOTIFICATION_MESSAGE, INLINE_NOTIFICATION_TYPE, CONFIRM_MESSAGE);
        verify(modalTitleMock, times(1)).setTextContent(eq(TITLE));
        verify(confirmInlineNotificationMock, times(1)).setMessage(eq(INLINE_NOTIFICATION_MESSAGE));
        verify(confirmInlineNotificationMock, times(1)).setType(eq(INLINE_NOTIFICATION_TYPE));
        verify(styleMock, times(1)).removeProperty(eq("display"));
        verify(styleMock, never()).setProperty(eq("display"), eq("none"));
        verify(modalConfirmationMessageLabelMock, times(1)).setTextContent(eq(CONFIRM_MESSAGE));
        verify(modalMock, times(1)).show();
    }
}