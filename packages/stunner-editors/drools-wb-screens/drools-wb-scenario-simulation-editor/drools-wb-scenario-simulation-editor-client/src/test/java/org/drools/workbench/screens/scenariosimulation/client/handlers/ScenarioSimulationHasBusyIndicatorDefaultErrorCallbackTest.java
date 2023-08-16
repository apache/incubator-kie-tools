/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationHasBusyIndicatorDefaultErrorCallbackTest extends AbstractScenarioSimulationEditorTest {

    private ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback;

    @Before
    public void setup() {
        super.setup();
        scenarioSimulationHasBusyIndicatorDefaultErrorCallback = spy(new ScenarioSimulationHasBusyIndicatorDefaultErrorCallback(scenarioSimulationViewMock) {
            {
                this.view = scenarioSimulationViewMock;
            }

            @Override
            protected boolean errorLocal(Object message, Throwable throwable) {
                return false;
            }
        });
    }

    @Test
    public void error() {
        Object messageMock = mock(Object.class);
        Throwable throwableMock = mock(Throwable.class);
        scenarioSimulationHasBusyIndicatorDefaultErrorCallback.error(messageMock, throwableMock);
        verify(scenarioSimulationHasBusyIndicatorDefaultErrorCallback, times(1)).errorLocal(eq(messageMock), eq(throwableMock));
    }

    @Test
    public void hideBusyIndicator() {
        scenarioSimulationHasBusyIndicatorDefaultErrorCallback.hideBusyIndicator();
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
    }
}