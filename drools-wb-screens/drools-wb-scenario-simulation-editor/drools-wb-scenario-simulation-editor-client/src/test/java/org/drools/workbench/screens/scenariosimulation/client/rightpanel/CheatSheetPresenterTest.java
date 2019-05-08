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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class CheatSheetPresenterTest extends AbstractTestToolsTest {

    @Mock
    private CheatSheetView cheatSheetViewMock;

    private CheatSheetPresenter cheatSheetPresenter;

    @Before
    public void setup() {
        super.setup();
        this.cheatSheetPresenter = spy(new CheatSheetPresenter(cheatSheetViewMock) {
            {
            }
        });
    }

    @Test
    public void onSetup() {
        cheatSheetPresenter.setup();
        verify(cheatSheetViewMock, times(1)).init(cheatSheetPresenter);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet(), cheatSheetPresenter.getTitle());
    }

    @Test
    public void initCheatSheetRule() {
        cheatSheetPresenter.initCheatSheet(ScenarioSimulationModel.Type.RULE);
        verify(cheatSheetViewMock, times(1)).setRuleCheatSheetContent();
    }

    @Test
    public void initCheatSheetDMN() {
        cheatSheetPresenter.initCheatSheet(ScenarioSimulationModel.Type.DMN);
        verify(cheatSheetViewMock, times(1)).setDMNCheatSheetContent();
    }

    @Test
    public void reset() {
        cheatSheetPresenter.reset();
        verify(cheatSheetViewMock, times(1)).reset();
    }
}