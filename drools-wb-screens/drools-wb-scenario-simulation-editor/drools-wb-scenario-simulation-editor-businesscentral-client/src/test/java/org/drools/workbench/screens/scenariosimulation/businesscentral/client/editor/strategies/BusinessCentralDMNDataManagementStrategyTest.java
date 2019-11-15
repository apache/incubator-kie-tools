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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.strategies;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BusinessCentralDMNDataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    @Mock
    private DMNTypeService dmnTypeServiceMock;

    private BusinessCentralDMNDataManagementStrategy businessCentralDmnDataManagementStrategySpy;
    @Mock
    private BusinessCentralDMNDataManagementStrategy.ResultHolder factModelTreeHolderMock;

    @Mock
    private FactModelTuple factModelTupleMock;

    @Mock
    private ErrorCallback<Message> errorCallbackMock;

    @Mock
    private RemoteCallback<FactModelTuple> remoteCallbackMock;

    @Mock
    private Path currentPathMock;

    @Before
    public void setup() {
        super.setup();
        try {
            when(dmnTypeServiceMock.retrieveFactModelTuple(any(), anyString())).thenReturn(factModelTupleMock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        businessCentralDmnDataManagementStrategySpy = spy(new BusinessCentralDMNDataManagementStrategy(new CallerMock<>(dmnTypeServiceMock),
                                                                                                       mock(EventBus.class)) {
            {
                this.currentPath = currentPathMock;
                this.model = modelLocal;
                this.factModelTreeHolder = factModelTreeHolderMock;
            }

            @Override
            public RemoteCallback<FactModelTuple> getSuccessCallback(TestToolsView.Presenter testToolsPresenter, ScenarioSimulationContext context, GridWidget gridWidget) {
                return remoteCallbackMock;
            }

            @Override
            protected ErrorCallback<Message> getErrorCallback(TestToolsView.Presenter testToolsPresenter) {
                return errorCallbackMock;
            }
        });
    }

    @Test
    public void retrieveFactModelTuple() {
        businessCentralDmnDataManagementStrategySpy.retrieveFactModelTuple(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION, "DMN_FILE_PATH");
        verify(businessCentralDmnDataManagementStrategySpy, times(1)).getSuccessCallback(eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal), eq(GridWidget.SIMULATION));
        verify(dmnTypeServiceMock, times(1)).retrieveFactModelTuple(eq(currentPathMock), eq("DMN_FILE_PATH"));
    }
}