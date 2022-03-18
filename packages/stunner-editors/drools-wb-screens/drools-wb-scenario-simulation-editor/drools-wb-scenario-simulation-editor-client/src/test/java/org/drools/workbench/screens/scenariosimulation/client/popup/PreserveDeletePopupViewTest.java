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
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_QUESTION_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_TITLE_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OKDELETE_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OKPRESERVE_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OPTION1_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OPTION2_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEXT1_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEXT_QUESTION_TEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class PreserveDeletePopupViewTest extends AbstractScenarioConfirmationPopupViewTest {

    @Mock
    private LIElement option1Mock;

    @Mock
    private LIElement option2Mock;

    @Mock
    protected Button okPreserveButtonMock;

    @Before
    public void setup() {
        super.commonSetup();
        popupView = spy(new PreserveDeletePopupView() {
            {
                this.mainTitle = (HeadingElement) mainTitleMock;
                this.mainQuestion = mainQuestionMock;
                this.text1 = text1Mock;
                this.textQuestion = textQuestionMock;
                this.option1 = option1Mock;
                this.option2 = option2Mock;
                this.cancelButton = cancelButtonMock;
                this.okPreserveButton = okPreserveButtonMock;
                this.okButton = okDeleteButtonMock;
                this.modal = modalMock;
                this.translationService = translationServiceMock;
            }
        });
    }


    @Test
    public void show() {
        ((PreserveDeletePopupView)popupView).show(MAIN_TITLE_TEXT,
                                                          MAIN_QUESTION_TEXT,
                                                          TEXT1_TEXT,
                                                          TEXT_QUESTION_TEXT,
                                                          OPTION1_TEXT,
                                                          OPTION2_TEXT,
                                                          OKPRESERVE_BUTTON_TEXT,
                                                          OKDELETE_BUTTON_TEXT,
                                                          okPreserveCommandMock,
                                                          okDeleteCommandMock);
        verifyShow(MAIN_TITLE_TEXT,
                   MAIN_QUESTION_TEXT,
                   TEXT1_TEXT,
                   TEXT_QUESTION_TEXT);
        assertEquals(okPreserveCommandMock, ((PreserveDeletePopupView)popupView).okPreserveCommand);
        verify(option1Mock, times(1)).setInnerText(eq(OPTION1_TEXT));
        verify(option2Mock, times(1)).setInnerText(eq(OPTION2_TEXT));
    }

    @Test
    public void onOkPreserveButton() {
        ((PreserveDeletePopupView)popupView).okPreserveCommand = null;
        ((PreserveDeletePopupView)popupView).onOkPreserveButton(mouseEventMock);
        verify(okPreserveCommandMock, never()).execute();
        verify(popupView, times(1)).hide();
        reset(popupView);
        ((PreserveDeletePopupView)popupView).okPreserveCommand = okPreserveCommandMock;
        ((PreserveDeletePopupView)popupView).onOkPreserveButton(mouseEventMock);
        verify(okPreserveCommandMock, times(1)).execute();
        verify(popupView, times(1)).hide();
    }
}