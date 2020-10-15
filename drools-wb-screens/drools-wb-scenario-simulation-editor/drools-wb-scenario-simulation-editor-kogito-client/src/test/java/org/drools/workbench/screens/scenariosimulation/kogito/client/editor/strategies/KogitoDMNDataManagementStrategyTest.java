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
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.ScenarioSimulationKogitoDMNDataManager;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoDMNDataManagementStrategyTest {

    @Mock
    private EventBus eventBusMock;
    @Mock
    private ScenarioSimulationKogitoDMNDataManager kogitoDMNDataManagerMock;
    @Mock
    private ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerServiceMock;
    @Mock
    private ScenarioSimulationContext scenarioSimulationContextMock;
    @Mock
    private TestToolsPresenter testToolsPresenterMock;
    @Mock
    private GridWidget gridWidgetMock;
    @Mock
    private JSITDefinitions jsitDefinitionsMock;
    @Mock
    private FactModelTuple factModelTupleMock;
    @Mock
    private RemoteCallback<FactModelTuple> factModelTupleRemoteCallbackMock;
    @Captor
    private ArgumentCaptor<Callback<JSITDefinitions>> callbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<ErrorCallback<Object>> errorCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<ScenarioNotificationEvent> scenarioNotificationEventArgumentCaptor;

    private KogitoDMNDataManagementStrategy kogitoDMNDataManagementStrategySpy;

    @Before
    public void setup() {
        kogitoDMNDataManagementStrategySpy = spy(new KogitoDMNDataManagementStrategy(eventBusMock, kogitoDMNDataManagerMock, dmnMarshallerServiceMock));
        when(kogitoDMNDataManagementStrategySpy.getSuccessCallback(eq(testToolsPresenterMock), eq(scenarioSimulationContextMock), eq(gridWidgetMock))).thenReturn(factModelTupleRemoteCallbackMock);
        when(kogitoDMNDataManagerMock.getFactModelTuple(eq(jsitDefinitionsMock))).thenReturn(factModelTupleMock);
    }

    @Test
    public void retrieveFactModelTuple() {
        kogitoDMNDataManagementStrategySpy.retrieveFactModelTuple(testToolsPresenterMock, scenarioSimulationContextMock, gridWidgetMock, "path/dmnFile.dmn");
        Path expectedPath = new PathFactory.PathImpl("dmnFile.dmn", "path/dmnFile.dmn");
        verify(dmnMarshallerServiceMock, times(1)).getDMNContent(eq(expectedPath), callbackArgumentCaptor.capture(), errorCallbackArgumentCaptor.capture());

        callbackArgumentCaptor.getValue().callback(jsitDefinitionsMock);
        verify(kogitoDMNDataManagerMock, times(1)).getFactModelTuple(eq(jsitDefinitionsMock));
        verify(kogitoDMNDataManagementStrategySpy, times(1)).getSuccessCallback(eq(testToolsPresenterMock), eq(scenarioSimulationContextMock), eq(gridWidgetMock));
        verify(factModelTupleRemoteCallbackMock, times(1)).callback(eq(factModelTupleMock));

        errorCallbackArgumentCaptor.getValue().error("Error Message", new Exception());
        verify(eventBusMock, times(1)).fireEvent(scenarioNotificationEventArgumentCaptor.capture());
        assertEquals(NotificationEvent.NotificationType.ERROR, scenarioNotificationEventArgumentCaptor.getValue().getNotificationType());
        assertTrue(scenarioNotificationEventArgumentCaptor.getValue().getMessage().contains("Error Message"));
   }

}
