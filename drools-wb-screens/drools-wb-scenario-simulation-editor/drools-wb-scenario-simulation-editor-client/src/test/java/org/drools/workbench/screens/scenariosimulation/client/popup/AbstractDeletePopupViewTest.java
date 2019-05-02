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

import com.google.gwt.dom.client.HeadingElement;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.when;

public abstract class AbstractDeletePopupViewTest {

    @Mock
    protected Command okDeleteCommandMock;

    @Mock
    protected Command okPreserveCommandMock;

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

    @Mock
    protected HeadingElement mainQuestionMock;



    protected void commonSetup() {
        when(translationServiceMock.getTranslation(Constants.ConfirmPopup_Cancel)).thenReturn(Constants.ConfirmPopup_Cancel);
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(modalMock.getElement()).thenReturn(elementMock);
    }
}