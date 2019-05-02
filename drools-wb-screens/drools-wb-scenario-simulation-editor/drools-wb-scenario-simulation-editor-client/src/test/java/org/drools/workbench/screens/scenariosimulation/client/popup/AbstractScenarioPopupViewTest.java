/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.dom.client.HeadingElement;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_TITLE_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OK_BUTTON_TEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioPopupViewTest {

    protected AbstractScenarioPopupView popupView;

    @Mock
    protected Command okCommandMock;

    @Mock
    protected Button cancelButtonMock;

    @Mock
    protected Button okButtonMock;


    @Mock
    protected MouseEvent mouseEventMock;

    @Mock
    protected HTMLElement elementMock;

    @Mock
    protected TranslationService translationServiceMock;

    @Mock
    protected Modal modalMock;

    @Mock
    protected CSSStyleDeclaration styleMock;

    @Mock
    protected HeadingElement mainTitleMock;



    @Test
    public void init() {
        popupView.init();
        verify(cancelButtonMock, times(1)).setText(anyString());
    }


    @Test
    public void show() {
        popupView.show(MAIN_TITLE_TEXT,
                       OK_BUTTON_TEXT,
                       okCommandMock);
        verifyShow(MAIN_TITLE_TEXT);
        assertEquals(okCommandMock, popupView.okCommand);
    }

    @Test
    public void getElement() {
        final HTMLElement retrieved = popupView.getElement();
        assertNotNull(retrieved);
    }

    @Test
    public void hide() {
        popupView.hide();
        verify(modalMock, times(1)).hide();
    }

    @Test
    public void onOkClick() {
        popupView.okCommand = null;
        popupView.onOkClick(mouseEventMock);
        verify(okCommandMock, never()).execute();
        verify(popupView, times(1)).hide();
        reset(popupView);
        popupView.okCommand = okCommandMock;
        popupView.onOkClick(mouseEventMock);
        verify(okCommandMock, times(1)).execute();
        verify(popupView, times(1)).hide();
    }

    @Test
    public void onCancelClick() {
        popupView.onCancelClick(mouseEventMock);
        verify(popupView, times(1)).hide();
    }

    protected void verifyShow(String mainTitleText) {
        verify(popupView, times(1)).conditionalShow(eq(mainTitleMock), eq(mainTitleText));
        verify(modalMock, times(1)).show();
    }

    protected void commonSetup() {
        when(translationServiceMock.getTranslation(Constants.ConfirmPopup_Cancel)).thenReturn(Constants.ConfirmPopup_Cancel);
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(modalMock.getElement()).thenReturn(elementMock);
    }
}