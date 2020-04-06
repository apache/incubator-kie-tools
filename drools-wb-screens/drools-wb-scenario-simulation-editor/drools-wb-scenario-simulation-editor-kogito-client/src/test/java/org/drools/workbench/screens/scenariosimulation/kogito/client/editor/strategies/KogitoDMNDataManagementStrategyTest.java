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

package org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoDMNService;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoDMNDataManagementStrategyTest {

    @Mock
    private EventBus eventBusMock;
    @Mock
    private KogitoDMNService kogitoDMNServiceMock;
    @Mock
    private ScenarioSimulationContext scenarioSimulationContextMock;
    @Mock
    private TestToolsPresenter testToolsPresenterMock;
    @Mock
    private GridWidget gridWidgetMock;
    @Mock
    private RemoteCallback remoteCallbackMock;

    private KogitoDMNDataManagementStrategy kogitoDMNDataManagementStrategySpy;

    @Before
    public void setup() {
        kogitoDMNDataManagementStrategySpy = spy(new KogitoDMNDataManagementStrategy(eventBusMock, kogitoDMNServiceMock));
    }

    @Test
    public void retrieveFactModelTuple() {
        kogitoDMNDataManagementStrategySpy.retrieveFactModelTuple(testToolsPresenterMock, scenarioSimulationContextMock, gridWidgetMock, "path/dmnFile.dmn");
        Path expectedPath = new PathFactory.PathImpl("dmnFile.dmn", "path/dmnFile.dmn");
        verify(kogitoDMNDataManagementStrategySpy, times(1)).getSuccessCallback(eq(testToolsPresenterMock), eq(scenarioSimulationContextMock), eq(gridWidgetMock));
        verify(kogitoDMNServiceMock, times(1)).getDMNContent(eq(expectedPath), isA(RemoteCallback.class), isA(ErrorCallback.class));
    }

    @Test
    public void getDMNContentRemoteCallback() {
        RemoteCallback<String> remoteCallback = kogitoDMNDataManagementStrategySpy.getDMNContentRemoteCallback(remoteCallbackMock);
        Assert.assertNotNull(remoteCallback);
        remoteCallback.callback("");
        verify(kogitoDMNDataManagementStrategySpy, times(1)).getDMN12UnmarshallCallback(eq(remoteCallbackMock));
    }

    @Test
    public void getDMNContentErrorCallback() {
        ErrorCallback<Object> remoteCallback = kogitoDMNDataManagementStrategySpy.getDMNContentErrorCallback("dmnpath/");
        Assert.assertNotNull(remoteCallback);
        remoteCallback.error("message", new Exception());
        verify(eventBusMock, times(1)).fireEvent(isA(ScenarioNotificationEvent.class));
    }
}
