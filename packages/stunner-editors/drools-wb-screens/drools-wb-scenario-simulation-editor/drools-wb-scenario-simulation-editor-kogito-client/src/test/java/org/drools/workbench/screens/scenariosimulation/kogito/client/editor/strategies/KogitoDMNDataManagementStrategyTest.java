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


package org.drools.workbench.screens.scenariosimulation.kogito.client.editor.strategies;

import java.util.Collections;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.ScenarioSimulationKogitoDMNDataManager;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.model.KogitoDMNModel;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoDMNDataManagementStrategyTest {

    @Mock
    private EventBus eventBusMock;
    @Mock
    private ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;
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
    private ArgumentCaptor<Callback<KogitoDMNModel>> callbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<ErrorCallback<Object>> errorCallbackArgumentCaptor;

    private KogitoDMNModel kogitoDMNModel;
    private KogitoDMNDataManagementStrategy kogitoDMNDataManagementStrategySpy;

    @Before
    public void setup() {
        kogitoDMNModel = new KogitoDMNModel(jsitDefinitionsMock, Collections.emptyMap());
        kogitoDMNDataManagementStrategySpy = spy(new KogitoDMNDataManagementStrategy(kogitoDMNDataManagerMock, dmnMarshallerServiceMock, scenarioSimulationEditorPresenterMock) {
            {
                this.dmnFilePath = "path/dmnFile.dmn";
            }
        });
        when(kogitoDMNDataManagementStrategySpy.getSuccessCallback(testToolsPresenterMock, scenarioSimulationContextMock, gridWidgetMock)).thenReturn(factModelTupleRemoteCallbackMock);
        when(kogitoDMNDataManagerMock.getFactModelTuple(kogitoDMNModel)).thenReturn(factModelTupleMock);
        when(scenarioSimulationEditorPresenterMock.getEventBus()).thenReturn(eventBusMock);
    }

    @Test
    public void retrieveFactModelTuple() {
        kogitoDMNDataManagementStrategySpy.retrieveFactModelTuple(testToolsPresenterMock, scenarioSimulationContextMock, gridWidgetMock);
        Path expectedPath = new PathFactory.PathImpl("dmnFile.dmn", "path/dmnFile.dmn");
        verify(dmnMarshallerServiceMock, times(1)).getDMNContent(eq(expectedPath), callbackArgumentCaptor.capture(), errorCallbackArgumentCaptor.capture());

        callbackArgumentCaptor.getValue().callback(kogitoDMNModel);
        verify(kogitoDMNDataManagerMock, times(1)).getFactModelTuple(kogitoDMNModel);
        verify(kogitoDMNDataManagementStrategySpy, times(1)).getSuccessCallback(testToolsPresenterMock, scenarioSimulationContextMock, gridWidgetMock);
        verify(factModelTupleRemoteCallbackMock, times(1)).callback(factModelTupleMock);

        errorCallbackArgumentCaptor.getValue().error("Error Message", new Exception());
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(anyString(), eq(NotificationEvent.NotificationType.ERROR), eq(false));
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandSettingsDock();
   }

}
