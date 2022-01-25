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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CONFIRM_MESSAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.INLINE_NOTIFICATION_MESSAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.NO_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OK_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.YES_BUTTON_TEXT;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class YesNoConfirmYesNoConfirmPopupViewPresenterTest extends AbstractYesNoConfirmYesNoConfirmPopupViewTest {

    @Mock
    private YesNoConfirmPopupView yesNoConfirmPopupViewMock;

    private YesNoConfirmPopupPresenter yesNoConfirmPopupPresenter;

    @Before
    public void setup() {
        yesNoConfirmPopupPresenter = spy(new YesNoConfirmPopupPresenter() {
            {
                this.yesNoConfirmPopupView = YesNoConfirmYesNoConfirmPopupViewPresenterTest.this.yesNoConfirmPopupViewMock;
            }
        });
    }

    @Test
    public void showOkCancel() {
        yesNoConfirmPopupPresenter.show(TITLE, OK_BUTTON_TEXT, CONFIRM_MESSAGE, okCommandMock);
        verify(yesNoConfirmPopupPresenter, times(1)).show(TITLE, null, null, OK_BUTTON_TEXT, Button.ButtonStyleType.DANGER, CONFIRM_MESSAGE, okCommandMock);
    }

    @Test
    public void showOkCancelFull() {
        yesNoConfirmPopupPresenter.show(TITLE, INLINE_NOTIFICATION_MESSAGE, INLINE_NOTIFICATION_TYPE, OK_BUTTON_TEXT, BUTTON_STYLE_TYPE, CONFIRM_MESSAGE, okCommandMock);
        verify(yesNoConfirmPopupViewMock, times(1)).show(eq(TITLE), eq(INLINE_NOTIFICATION_MESSAGE), eq(INLINE_NOTIFICATION_TYPE), eq(OK_BUTTON_TEXT), eq(BUTTON_STYLE_TYPE), eq(CONFIRM_MESSAGE), eq(okCommandMock));
    }

    @Test
    public void showYesNoCancel() {
        yesNoConfirmPopupPresenter.show(TITLE, YES_BUTTON_TEXT, NO_BUTTON_TEXT, CONFIRM_MESSAGE, yesCommandMock, noCommandMock);
        verify(yesNoConfirmPopupPresenter, times(1)).show(TITLE, null, null, YES_BUTTON_TEXT, NO_BUTTON_TEXT, Button.ButtonStyleType.DANGER, Button.ButtonStyleType.DEFAULT, CONFIRM_MESSAGE, yesCommandMock, noCommandMock);
    }

    @Test
    public void showYesNoCancelFull() {
        yesNoConfirmPopupPresenter.show(TITLE, INLINE_NOTIFICATION_MESSAGE, INLINE_NOTIFICATION_TYPE, YES_BUTTON_TEXT, NO_BUTTON_TEXT, BUTTON_STYLE_TYPE, BUTTON_STYLE_TYPE, CONFIRM_MESSAGE, yesCommandMock, noCommandMock);
        verify(yesNoConfirmPopupViewMock, times(1)).show(eq(TITLE), eq(INLINE_NOTIFICATION_MESSAGE), eq(INLINE_NOTIFICATION_TYPE), eq(YES_BUTTON_TEXT), eq(NO_BUTTON_TEXT), eq(BUTTON_STYLE_TYPE), eq(BUTTON_STYLE_TYPE), eq(CONFIRM_MESSAGE), eq(yesCommandMock), eq(noCommandMock));
    }

    @Test
    public void hide() {
        yesNoConfirmPopupPresenter.hide();
        verify(yesNoConfirmPopupViewMock, times(1)).hide();
    }
}