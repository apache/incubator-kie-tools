/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.popup;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioKogitoCreationPopupPresenterTest {

    @Mock
    private Command okCommandMock;
    @Mock
    private ScenarioKogitoCreationPopupView scenarioKogitoCreationPopupViewMock;

    private ScenarioKogitoCreationPopupPresenter scenarioKogitoCreationPopupPresenterSpy;

    @Before
    public void setup() {
        scenarioKogitoCreationPopupPresenterSpy = spy(new ScenarioKogitoCreationPopupPresenter() {
            {
                scenarioKogitoCreationPopupView = scenarioKogitoCreationPopupViewMock;
            }
        });
    }

    @Test
    public void show() {
        scenarioKogitoCreationPopupPresenterSpy.show("title", okCommandMock);
        verify(scenarioKogitoCreationPopupViewMock, times(1)).show(eq("title"), eq(okCommandMock));
    }

    @Test
    public void hide() {
        scenarioKogitoCreationPopupPresenterSpy.hide();
        verify(scenarioKogitoCreationPopupViewMock, times(1)).hide();
    }

    @Test
    public void getSelectedType() {
        scenarioKogitoCreationPopupPresenterSpy.getSelectedType();
        verify(scenarioKogitoCreationPopupViewMock, times(1)).getSelectedType();
    }

    @Test
    public void getSelectedPath() {
        scenarioKogitoCreationPopupPresenterSpy.getSelectedPath();
        verify(scenarioKogitoCreationPopupViewMock, times(1)).getSelectedPath();
    }
}
