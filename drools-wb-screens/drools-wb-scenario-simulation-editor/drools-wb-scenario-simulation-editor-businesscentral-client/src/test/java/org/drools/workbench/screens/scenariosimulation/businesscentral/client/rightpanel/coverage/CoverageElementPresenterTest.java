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
package org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.ScenarioSimulationBusinessCentralViewsProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CoverageElementPresenterTest {

    @Mock
    private ScenarioSimulationBusinessCentralViewsProvider scenarioSimulationBusinessCentralViewsProviderMock;
    @Mock
    private CoverageElementView coverageElementViewMock;
    @Mock
    private HTMLElement elementListMock;
    @Mock
    private HTMLElement descriptionNodeMock;
    @Mock
    private HTMLElement numberOfTimeNodeMock;

    private CoverageElementPresenter coverageElementPresenterSpy;

    @Before
    public void setup() {
        coverageElementPresenterSpy = spy(new CoverageElementPresenter() {
            {
                this.viewsProvider = scenarioSimulationBusinessCentralViewsProviderMock;
            }
        });
        when(scenarioSimulationBusinessCentralViewsProviderMock.getCoverageElementView()).thenReturn(coverageElementViewMock);
        when(coverageElementViewMock.getDescription()).thenReturn(descriptionNodeMock);
        when(coverageElementViewMock.getNumberOfTime()).thenReturn(numberOfTimeNodeMock);
    }

    @Test
    public void addElementView() {
        coverageElementPresenterSpy.initElementList(elementListMock);
        coverageElementPresenterSpy.addElementView("description", "value");
        verify(coverageElementViewMock, times(1)).setDescriptionValue(eq("description"));
        verify(coverageElementViewMock, times(1)).setElementValue(eq("value"));
        verify(elementListMock, times(1)).appendChild(eq(descriptionNodeMock));
        verify(elementListMock, times(1)).appendChild(eq(numberOfTimeNodeMock));
    }
}
