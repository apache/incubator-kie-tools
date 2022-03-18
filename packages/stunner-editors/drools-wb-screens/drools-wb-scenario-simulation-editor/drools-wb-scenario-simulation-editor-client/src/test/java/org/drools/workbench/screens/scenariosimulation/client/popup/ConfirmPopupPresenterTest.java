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

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MAIN_TITLE_TEXT;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ConfirmPopupPresenterTest extends AbstractDeletePopupViewTest {

    @Mock
    private ConfirmPopupView confirmPopupViewMock;

    private ConfirmPopupPresenter confirmPopupPresenter;



    @Before
    public void setup() {
        confirmPopupPresenter = spy(new ConfirmPopupPresenter() {
            {
                this.confirmPopupView = confirmPopupViewMock;
            }
        });
    }

    @Test
    public void show() {
        confirmPopupPresenter.show(MAIN_TITLE_TEXT,
                                   MAIN_TEXT);
        verify(confirmPopupViewMock, times(1)).show(eq(MAIN_TITLE_TEXT), eq(MAIN_TEXT));
    }

    @Test
    public void hide() {
        confirmPopupPresenter.hide();
        verify(confirmPopupViewMock, times(1)).hide();
    }
}