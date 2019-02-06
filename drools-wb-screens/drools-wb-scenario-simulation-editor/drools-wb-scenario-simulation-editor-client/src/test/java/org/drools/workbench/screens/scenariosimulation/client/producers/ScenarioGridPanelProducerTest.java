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

package org.drools.workbench.screens.scenariosimulation.client.producers;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridPanelProducerTest extends AbstractProducerTest {

    @Mock
    private ScenarioGridLayer scenarioGridLayerMock;

    private ScenarioGridPanelProducer scenarioGridPanelProducer;

    @Before
    public void setup() {
        super.setup();
        scenarioGridPanelProducer = spy(new ScenarioGridPanelProducer() {
            {
                this.scenarioGridLayer = scenarioGridLayerMock;
                this.scenarioGridPanel = scenarioGridPanelMock;
            }
        });
    }

    @Test
    public void init() {
        scenarioGridPanelProducer.init();
        verify(scenarioGridLayerMock, times(1)).addScenarioGrid(isA(ScenarioGrid.class));
        verify(scenarioGridPanelMock, times(1)).add(eq(scenarioGridLayerMock));
    }

    @Test
    public void getScenarioGridPanel() {
        final ScenarioGridPanel retrieved = scenarioGridPanelProducer.getScenarioGridPanel();
        assertEquals(scenarioGridPanelMock, retrieved);
    }
}