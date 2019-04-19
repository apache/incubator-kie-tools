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
import com.google.gwt.dom.client.ParagraphElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DeletePopupViewTest extends AbstractScenarioConfirmationPopupViewTest {

    @Mock
    private ParagraphElement textDangerMock;

    private final String TEXT_DANGER_TEXT = "TEXT_DANGER_TEXT";

    @Before
    public void setup() {
        super.commonSetup();
        popupView = spy(new DeletePopupView() {
            {
                this.mainTitle = mainTitleMock;
                this.mainQuestion = mainQuestionMock;
                this.text1 = text1Mock;
                this.textQuestion = textQuestionMock;
                this.textDanger = textDangerMock;
                this.cancelButton = cancelButtonMock;
                this.okButton = okDeleteButtonMock;
                this.modal = modalMock;
                this.translationService = translationServiceMock;
            }
        });
    }

    @Test
    public void show() {
        ((DeletePopupView) popupView).show(MAIN_TITLE_TEXT,
                                           MAIN_QUESTION_TEXT,
                                           TEXT1_TEXT,
                                           TEXT_QUESTION_TEXT,
                                           TEXT_DANGER_TEXT,
                                           OKDELETE_BUTTON_TEXT,
                                           okDeleteCommandMock);
        verifyShow(MAIN_TITLE_TEXT,
                   MAIN_QUESTION_TEXT,
                   TEXT1_TEXT,
                   TEXT_QUESTION_TEXT);
        verify(textDangerMock, times(1)).setInnerText(eq(TEXT_DANGER_TEXT));
    }

}