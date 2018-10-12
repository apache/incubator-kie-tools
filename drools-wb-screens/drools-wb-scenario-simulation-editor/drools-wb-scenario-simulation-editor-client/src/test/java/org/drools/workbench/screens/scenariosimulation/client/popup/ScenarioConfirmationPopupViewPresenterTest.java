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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@RunWith(LienzoMockitoTestRunner.class)
public abstract class ScenarioConfirmationPopupViewPresenterTest extends AbstractDeletePopupViewTest {

    @Mock
    private ScenarioConfirmationPopupView scenarioConfirmationPopupViewMock;

    private ScenarioConfirmationPopupPresenter scenarioConfirmationPopupPresenter;

    @Before
    public void setup() {
        scenarioConfirmationPopupPresenter = spy(new ScenarioConfirmationPopupPresenter() {
            {
                this.scenarioConfirmationPopupView = ScenarioConfirmationPopupViewPresenterTest.this.scenarioConfirmationPopupViewMock;
            }
        });
    }

    @Test
    public void show() {
        scenarioConfirmationPopupPresenter.show(MAIN_TITLE_TEXT,
                                                MAIN_QUESTION_TEXT,
                                                TEXT1_TEXT,
                                                TEXT_QUESTION_TEXT,
                                                OKDELETE_BUTTON_TEXT,
                                                okDeleteCommandMock);
        verify(scenarioConfirmationPopupViewMock, times(1)).show(eq(MAIN_TITLE_TEXT), eq(MAIN_QUESTION_TEXT), eq(TEXT1_TEXT), eq(TEXT_QUESTION_TEXT), eq(OKDELETE_BUTTON_TEXT), eq(okDeleteCommandMock));
    }

    @Test
    public void hide() {
        scenarioConfirmationPopupPresenter.hide();
        verify(scenarioConfirmationPopupViewMock, times(1)).hide();
    }
}