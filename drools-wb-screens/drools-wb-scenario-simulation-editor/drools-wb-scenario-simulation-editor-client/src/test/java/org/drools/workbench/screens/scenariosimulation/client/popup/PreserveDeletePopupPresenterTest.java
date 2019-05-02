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

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_QUESTION_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_TITLE_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OKDELETE_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OKPRESERVE_BUTTON_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OPTION1_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OPTION2_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEXT1_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEXT_QUESTION_TEXT;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class PreserveDeletePopupPresenterTest extends AbstractDeletePopupViewTest {

    @Mock
    private PreserveDeletePopupView preserveDeletePopupViewMock;

    private PreserveDeletePopupPresenter preserveDeletePopupPresenter;


    @Before
    public void setup() {
        preserveDeletePopupPresenter = spy(new PreserveDeletePopupPresenter() {
            {
                this.preserveDeletePopupView = preserveDeletePopupViewMock;
            }
        });
    }

    @Test
    public void show() {
        preserveDeletePopupPresenter.show(MAIN_TITLE_TEXT,
                                          MAIN_QUESTION_TEXT,
                                          TEXT1_TEXT,
                                          TEXT_QUESTION_TEXT,
                                          OPTION1_TEXT,
                                          OPTION2_TEXT,
                                          OKPRESERVE_BUTTON_TEXT,
                                          OKDELETE_BUTTON_TEXT,
                                          okPreserveCommandMock,
                                          okDeleteCommandMock);
        verify(preserveDeletePopupViewMock, times(1)).show(eq(MAIN_TITLE_TEXT), eq(MAIN_QUESTION_TEXT), eq(TEXT1_TEXT), eq(TEXT_QUESTION_TEXT), eq(OPTION1_TEXT), eq(OPTION2_TEXT), eq(OKPRESERVE_BUTTON_TEXT), eq(OKDELETE_BUTTON_TEXT), eq(okPreserveCommandMock), eq(okDeleteCommandMock));
    }

    @Test
    public void hide() {
        preserveDeletePopupPresenter.hide();
        verify(preserveDeletePopupViewMock, times(1)).hide();
    }
}